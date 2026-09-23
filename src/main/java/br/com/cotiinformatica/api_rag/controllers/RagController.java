package br.com.cotiinformatica.api_rag.controllers;

import br.com.cotiinformatica.api_rag.dtos.RagRequest;
import br.com.cotiinformatica.api_rag.dtos.RagResponse;
import br.com.cotiinformatica.api_rag.services.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(
            RagService ragService
    ) {

        this.ragService = ragService;
    }

    @PostMapping
    public RagResponse perguntar(
            @RequestBody RagRequest request
    ) {

        return ragService.perguntar(request);
    }
}