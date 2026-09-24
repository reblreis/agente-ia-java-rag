package br.com.cotiinformatica.api_multiagent.dtos;

public record MultiAgentResponse(
        String agenteSelecionado,
        String justificativa,
        String resposta
) {
}
