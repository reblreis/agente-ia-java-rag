package br.com.cotiinformatica.api_multiagent.agents;

import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;
import br.com.cotiinformatica.api_multiagent.interfaces.Agente;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgenteSuporte implements Agente {

    private final ChatClient chatClient;

    public AgenteSuporte(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }

    @Override
    public TipoAgente getTipo() {
        return TipoAgente.SUPORTE;
    }

    public String executar(
            String nome,
            String mensagem
    ){
        return chatClient
                .prompt()

                .system("""
                        Você é o AGENTE DE SUPORTE
                                                de uma escola de tecnologia.
                                                Sua responsabilidade é tratar
                                                situações como:
                                                - problemas de acesso;
                                                - problemas administrativos;
                                                - certificados;
                                                - cobranças;
                                                - atendimento humano;
                                                - reclamações;
                                                - solicitações diversas.
                                                Caso seja necessário atendimento
                                                humano, informe ao usuário que
                                                a solicitação deverá ser encaminhada
                                                para a equipe responsável.
                                                Não invente protocolos.
                                                Não invente informações.
                                                Seja cordial e profissional.
                        
                        """)
                .user("""
                        Usuário:
                        %s
                        
                        Solicitação:
                        %s
                        """.formatted(
                                nome,
                                mensagem
                ))

                .call()
                .content();
    }
}
