package br.com.cotiinformatica.api_multiagent.controllers;

import br.com.cotiinformatica.api_multiagent.dtos.MultiAgentRequest;
import br.com.cotiinformatica.api_multiagent.dtos.MultiAgentResponse;

import br.com.cotiinformatica.api_multiagent.services.OrquestradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/multiagents")
public class MultiAgentController {

    private final OrquestradorService orquestradorService;

    public MultiAgentController
            (OrquestradorService orquestradorService) {
        this.orquestradorService = orquestradorService;
    }

    @PostMapping
    public ResponseEntity<MultiAgentResponse> post
            (@RequestBody MultiAgentRequest request) {
        var response = orquestradorService.processar(request);
        return ResponseEntity.ok(response);
    }
}
