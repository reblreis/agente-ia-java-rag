package br.com.cotiinformatica.api_rag.dtos;

import br.com.cotiinformatica.api_rag.enums.AssuntoAtendimento;

public record AtendimentoResponse(
        AssuntoAtendimento assunto,
        String mensagem,
        String curso,
        Boolean interesseIdentificado,
        Boolean encaminharParaAtendente,
        Boolean informacaoEncontrada
) {
}
