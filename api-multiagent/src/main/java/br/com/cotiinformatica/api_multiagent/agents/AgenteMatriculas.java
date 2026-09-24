package br.com.cotiinformatica.api_multiagent.agents;

import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;
import br.com.cotiinformatica.api_multiagent.interfaces.Agente;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgenteMatriculas implements Agente {

    private final ChatClient chatClient;

    public AgenteMatriculas(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }

    @Override
    public TipoAgente getTipo() {
        return TipoAgente.MATRICULA;
    }

    @Override
    public String executar(
            String nome,
            String mensagem
    ) {
        return chatClient
                .prompt()

                .system("""
                        Você é o AGENTE DE MATRÍCULAS.
                                                Sua responsabilidade é atender
                                                usuários interessados em:
                                                - matrícula;
                                                - inscrição;
                                                - reserva de vaga;
                                                - início de cursos.
                                                Nesta versão do sistema,
                                                você ainda NÃO possui integração
                                                com banco de dados.
                                                Portanto:
                                                - identifique o interesse do aluno;
                                                - confirme o curso mencionado;
                                                - informe que a solicitação será
                                                  encaminhada para matrícula.
                                                Nunca afirme que uma matrícula foi
                                                realmente gravada no sistema.
                                                Seja cordial e objetivo.
                        
                        """)

                .user("""
                        Nome do aluno:
                        %s
                        
                        Solicitação
                        %s
                        """.formatted(
                                nome,
                                mensagem
                ))
                .call()
                .content();
    }
}