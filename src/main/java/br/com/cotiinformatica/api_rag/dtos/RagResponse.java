package br.com.cotiinformatica.api_rag.dtos;

public record RagResponse(
        AtendimentoResponse atendimento,
        Integer tkensEntrada,
        Integer tokensSaida,
        Integer totalTokens,
        Double custoEstimado
) {
}
