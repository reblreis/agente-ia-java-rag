package br.com.cotiinformatica.api_rag.tools;

import br.com.cotiinformatica.api_rag.entities.AcaoAgente;
import br.com.cotiinformatica.api_rag.repositories.AcaoAgenteRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AtendimentoTools {

    private final AcaoAgenteRepository acaoAgenteRepository;

    public AtendimentoTools(
            AcaoAgenteRepository acaoAgenteRepository
    ) {
        this.acaoAgenteRepository = acaoAgenteRepository;
    }


    @Tool(
            name = "registrarSolicitacaoMatricula",
            description = """
            Registra uma solicitação de matrícula ou reserva de vaga.
            Use somente quando o usuário disser explicitamente que
            deseja se matricular, fazer matrícula ou reservar uma vaga.
            Não use apenas porque ele perguntou sobre um curso.
            """
    )
    public String registrarSolicitacaoMatricula(

            @ToolParam(
                    description = "Nome do usuário"
            )
            String nomeUsuario,

            @ToolParam(
                    description = "Nome do curso solicitado",
                    required = false
            )
            String curso
    ) {

        var acao = new AcaoAgente();

        acao.setTipo("SOLICITACAO_MATRICULA");
        acao.setNomeUsuario(nomeUsuario);

        acao.setCurso(
                curso == null || curso.isBlank()
                        ? "NAO_INFORMADO"
                        : curso
        );

        acao.setDescricao(
                "Solicitação de matrícula criada pelo agente."
        );

        acao.setDataHora(
                LocalDateTime.now()
        );

        acao = acaoAgenteRepository.save(acao);

        return """
            Solicitação de matrícula registrada com sucesso.
            Protocolo: %s
            """.formatted(acao.getId());
    }


    @Tool(
            name = "encaminharParaAtendente",
            description = """
            Encaminha o usuário para atendimento humano.
            Use quando o usuário pedir atendimento humano,
            solicitar desconto ou quando a informação necessária
            não estiver disponível no contexto.
            """
    )
    public String encaminharParaAtendente(

            @ToolParam(
                    description = "Nome do usuário"
            )
            String nomeUsuario,

            @ToolParam(
                    description = "Motivo do encaminhamento"
            )
            String motivo
    ) {

        var acao = new AcaoAgente();

        acao.setTipo("ATENDIMENTO_HUMANO");
        acao.setNomeUsuario(nomeUsuario);
        acao.setDescricao(motivo);
        acao.setDataHora(LocalDateTime.now());

        acao = acaoAgenteRepository.save(acao);

        return """
            Encaminhamento para atendente realizado.
            Protocolo: %s
            """.formatted(acao.getId());
    }
}