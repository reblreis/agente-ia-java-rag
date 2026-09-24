package br.com.cotiinformatica.api_multiagent.services;

import br.com.cotiinformatica.api_multiagent.dtos.DecisaoOrquestracao;
import br.com.cotiinformatica.api_multiagent.dtos.MultiAgentRequest;
import br.com.cotiinformatica.api_multiagent.dtos.MultiAgentResponse;
import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;
import br.com.cotiinformatica.api_multiagent.interfaces.Agente;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class OrquestradorService {

    private final ChatClient chatClient;
    private final Map<TipoAgente, Agente> agentes;

    public OrquestradorService (
            ChatClient.Builder chatClient,
            List<Agente> listaAgentes
    ) {
      this.chatClient = chatClient.build();
      this.agentes = new EnumMap<>(TipoAgente.class);

      for(Agente agente : listaAgentes) {
          this.agentes.put(
                  agente.getTipo(),
                  agente
          );
      }
    }

    public MultiAgentResponse processar(
            MultiAgentRequest request
    ) {
        /*
        ETAPA 1 -  Pedir para que o LLM decidir
        qual agente deverá executar a solicitação.
         */
        DecisaoOrquestracao decisao = chatClient
                .prompt()

                .system("""
                        Você é ORQUESTRADOR
                        de um sstema multiagente.
                        Sua responsabilidade NÃO
                                                        é responder à pergunta.
                        
                                                        Sua responsabilidade é
                                                        decidir qual agente deve
                                                        processar a solicitação.
                        
                                                        Existem os seguintes agentes:
                        
                                                        CURSOS
                        
                                                        Utilize para perguntas sobre:
                        
                                                        - cursos;
                                                        - conteúdo;
                                                        - carga horária;
                                                        - tecnologias;
                                                        - programação;
                                                        - treinamentos.
                        
                                                        ----------------------------
                        
                                                        MATRICULA
                        
                                                        Utilize quando existir
                                                        intenção de:
                        
                                                        - fazer matrícula;
                                                        - fazer inscrição;
                                                        - reservar vaga;
                                                        - participar de um curso.
                        
                                                        ----------------------------
                        
                                                        SUPORTE
                        
                                                        Utilize para:
                        
                                                        - certificado;
                                                        - cobrança;
                                                        - acesso;
                                                        - problemas administrativos;
                                                        - reclamações;
                                                        - atendimento humano;
                                                        - outros problemas.
                        
                                                        REGRAS IMPORTANTES:
                        
                                                        Se a pessoa perguntar sobre
                                                        um curso e também demonstrar
                                                        intenção clara de inscrição,
                                                        utilize MATRICULA.
                        
                                                        Não responda diretamente
                                                        ao usuário.
                        
                                                        Apenas classifique a
                                                        solicitação.                        
                        """)

                .user(request.mensagem())
                .call()
                .entity(
                        DecisaoOrquestracao.class
                );

        /*
        ETAPA 2 - Buscar o agente escolhido
                  pelo Orquestrador.
         */
        Agente agenteSelecionado =
                agentes.get(
                        decisao.agente()
                );

        /*
        Segurança caso nenhum
        agente seja encontrado.
         */
        if (agenteSelecionado == null) {

            throw new IllegalArgumentException(
                    "Agente não encontrado: " +
                            decisao.agente()
            );
        }

        /*
        ETAPA 3 - Executar o agente especialista.
         */
        String resposta = agenteSelecionado.executar(
                request.nome(),
                request.mensagem()
        );

        /*
        ETAPA 4 - Retornar a resposta final.
         */
        return new MultiAgentResponse(
                decisao.agente() .name(),
                decisao.justificativa(),
                resposta
        );
    }
}
