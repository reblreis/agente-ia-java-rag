package br.com.cotiinformatica.api_rag.services;

import br.com.cotiinformatica.api_rag.dtos.AtendimentoResponse;
import br.com.cotiinformatica.api_rag.dtos.RagRequest;
import br.com.cotiinformatica.api_rag.dtos.RagResponse;
import br.com.cotiinformatica.api_rag.entities.Conversa;
import br.com.cotiinformatica.api_rag.repositories.ConversaRepository;
import br.com.cotiinformatica.api_rag.tools.AtendimentoTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    @Value("${tokens.base}") private Double tokensBase;
    @Value("${tokens.preco.entrada}") private Double tokensPrecoEntrada;
    @Value("${tokens.preco.saida}") private Double tokensPrecoSaida;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ConversaRepository conversaRepository;
    private final AtendimentoTools atendimentoTools;

    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            ConversaRepository conversaRepository,
            AtendimentoTools atendimentoTools
    ) {

        this.vectorStore = vectorStore;

        this.chatClient =
                chatClientBuilder.build();

        this.conversaRepository = conversaRepository;
        this.atendimentoTools = atendimentoTools;
    }

    public RagResponse perguntar(
            RagRequest request
    ) {

        var nomeUuario = request.nomeUsuario();
        var pergunta = request.pergunta();

        /*
          1 - Buscar no mongodb as 3 ultimas
          conversas do usuário
         */
        var conversas = conversaRepository
                .findTop3ByNomeUsuarioOrderByDataHoraDesc(nomeUuario);

        /*
          2 - Montar o histórico de conversas
         */
        var historico = conversas.stream().map(conversa -> """
                    Usuário: %s
                    Atendente: %s
                """.formatted(
                        conversa.getPergunta(),
                        conversa.getResposta()
                ))
                .collect(Collectors.joining("\n"));

        if(historico.isEmpty()) {
            historico = "Nenhuma conversa anterior.";
        }

        /*
         * 1 - Busca documentos semanticamente
         * relacionados à pergunta
         */

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(pergunta)
                        .topK(3) //Quantidade de trechos obtidos da base de conhecimento
                        .build();

        List<Document> documentos =
                vectorStore.similaritySearch(
                        searchRequest
                );

        /*
         * 2 - Monta o contexto
         */

        String contexto =
                documentos
                        .stream()
                        .map(Document::getText)
                        .collect(
                                Collectors.joining("\n")
                        );

        /*
         *  3 - Criar o conversor da saída estruturada
         */
        BeanOutputConverter<AtendimentoResponse> converter =
                new BeanOutputConverter<>(
                        AtendimentoResponse.class
                );

        //O método getFormat() gera as instruções
        //de formato de saída baseado no DTO
        var formatoResposta = converter.getFormat();

        /*
         * 4 - Envia pergunta + contexto
         * para o LLM
         */

        ChatResponse response = chatClient
                .prompt()
                .system("""
                    Você é o atendente virtual da COTI Informática.

                    Sua função é atender alunos e pessoas interessadas
                    nos cursos da instituição.

                    Responda somente com base nas informações presentes
                    no contexto fornecido.

                    Regras:

                    1. Seja educado, objetivo e acolhedor.
                    2. Não invente cursos, preços, datas ou horários.
                    3. Quando encontrar a resposta no contexto,
                       defina informacaoEncontrada como true.
                    4. Quando não encontrar a informação,
                       defina informacaoEncontrada como false.
                    5. Quando não encontrar a informação, explique na
                       mensagem que um atendente poderá ajudar.
                    6. Defina encaminharParaAtendente como true quando:
                       - a informação não estiver no contexto;
                       - o aluno pedir matrícula;
                       - o aluno solicitar desconto;
                       - o aluno pedir atendimento humano.
                    7. Nunca solicite senha, cartão ou informações
                       bancárias.
                    8. Não escreva nada fora da estrutura solicitada.
                    9. Seja cordial e trate o usuário pelo nome.
                    
                    FERRAMENTAS / AÇÕES AUTOMÁTICAS
                    
                    10. Se o usuário disser explicitamente que quer se matricular,
                        fazer matrícula ou reservar uma vaga, chame a ferramenta
                        registrarSolicitacaoMatricula.
                    11. Se o usuário pedir atendimento humano, pedir desconto
                        ou se a informação necessária não estiver no contexto,
                        chame a ferramenta encaminharParaAtendente.
                    12. Não chame ferramentas apenas para responder dúvidas
                        informativas.
                    13. Nunca invente argumentos para uma ferramenta.
                    14. Depois de executar uma ferramenta, utilize o resultado
                        retornado por ela para elaborar a resposta final.
                    15. Ao final, responda obrigatoriamente no formato solicitado.

                    FORMATO OBRIGATÓRIO:

                    %s
                    """.formatted(formatoResposta))
                .user("""
                    
                    NOME DO USUÁRIO:
                    
                    %s
                    
                    HISTORICO DAS 3 ULTIMAS CONVERSAS:
                    
                    %s
                    
                    CONTEXTO DA BASE DE CONHECIMENTO:

                    %s

                    PERGUNTA DO ALUNO:

                    %s
                    """.formatted(nomeUuario, historico, contexto, pergunta))
                .tools(atendimentoTools)
                .call()
                .chatResponse();

        //Capturar o JSON produzido pelo modelo
        var jsonGerado = response
                .getResult()
                .getOutput()
                .getText();

        //Converter o JSON para um objeto Java (DTO)
        var atendimento = converter.convert(jsonGerado);

        //Capturar as informações dos Tokens usados
        Usage usage = response
                .getMetadata()
                .getUsage();

        //Capturar os dados de gastos
        var tokensEntrada = usage.getPromptTokens();
        var tokensSaida = usage.getCompletionTokens();
        var totalTokens = usage.getTotalTokens();

        //Calcular o custo dos tokes usados
        var custoEstimado = calcularCusto(tokensEntrada, tokensSaida);

        //Capturar o texto da resposta
        var resposta = response.getResult().getOutput().getText();

        //Capturar os dados da conversa
        var conversa = new Conversa();
        conversa.setNomeUsuario(nomeUuario);
        conversa.setPergunta(pergunta);
        conversa.setResposta(atendimento.mensagem());
        conversa.setDataHora(LocalDateTime.now());

        //Salvar a conversa no banco de dados
        conversaRepository.save(conversa);

        //Retornar a saida do método
        return new RagResponse(
                atendimento,
                tokensEntrada,
                tokensSaida,
                totalTokens,
                custoEstimado
        );
    }

    private double calcularCusto(int tokensEntrada, int tokensSaida) {

        var custoEntrada = (tokensEntrada / tokensBase) * tokensPrecoEntrada;
        var custoSaida = (tokensSaida / tokensBase) * tokensPrecoSaida;

        return custoEntrada + custoSaida;
    }
}