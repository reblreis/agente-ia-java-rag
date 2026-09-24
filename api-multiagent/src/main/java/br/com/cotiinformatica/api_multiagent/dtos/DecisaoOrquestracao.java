package br.com.cotiinformatica.api_multiagent.dtos;

import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;

public record DecisaoOrquestracao(
        TipoAgente agente,
        String justificativa
) {
}
