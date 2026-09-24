package br.com.cotiinformatica.api_multiagent.agents;

import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;
import br.com.cotiinformatica.api_multiagent.interfaces.Agente;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgenteCursos implements Agente {

    private final ChatClient chatClient;

    public AgenteCursos(ChatClient.Builder chatBuilder){
        this.chatClient = chatBuilder.build();
    }

    @Override
    public TipoAgente getTipo() {
        return TipoAgente.CURSOS;
    }

    @Override
    public String executar(
            String nome,
            String mensagem
    ) {
        String baseConhecimento = """
                CURSOS DISPONÍVEIS:
                                1. Java para Desenvolvimento de APIs
                                Carga horária:
                                40 horas.
                                Conteúdo:
                                Java, Orientação a Objetos,
                                Spring Boot, REST APIs,
                                JPA e bancos de dados.
                                --------------------------------
                                2. Desenvolvimento de Agentes de IA com Java
                                Carga horária:
                                20 horas.
                                Conteúdo:
                                Spring AI,
                                Prompt Engineering,
                                RAG,
                                Tool Calling e Agentes de IA.
                                --------------------------------
                                3. Cloud Computing com AWS
                                Carga horária:
                                32 horas.
                                Conteúdo:
                                Cloud Computing,
                                EC2,
                                S3,
                                RDS,
                                redes,
                                segurança e deploy.
                
                """;

        return chatClient
                .prompt()

                .system("""
                        Você é o AGENTE DE CURSOS.
                                                Você trabalha em uma escola
                                                de tecnologia.
                                                Sua responsabilidade é responder
                                                somente dúvidas relacionadas
                                                aos cursos oferecidos.
                                                Utilize somente a base de
                                                conhecimento fornecida.
                                                Caso uma informação não esteja
                                                disponível, informe que não possui
                                                essa informação.
                                                Não invente informações.
                                                Seja objetivo e didático.                        
                        """)

                .user("""
                        Nome de usuário:
                        %s
                        
                        Base de conhecimento:
                        %s
                        
                        Pergunta:
                        %s
                        """.formatted(
                                nome,
                        baseConhecimento,
                        mensagem
                ))
                .call()
                .content();
    }
}