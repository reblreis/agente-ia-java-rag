package br.com.cotiinformatica.api_multiagent.interfaces;

import br.com.cotiinformatica.api_multiagent.enums.TipoAgente;

public interface Agente {

    TipoAgente getTipo();

    String executar(
            String nome,
            String mensagem
    );
}