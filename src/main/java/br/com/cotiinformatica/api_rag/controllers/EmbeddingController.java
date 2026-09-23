package br.com.cotiinformatica.api_rag.controllers;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel){
        this.embeddingModel = embeddingModel;
    }

    @GetMapping
    public float[] gerar(
            @RequestParam String texto
    ){
        return embeddingModel.embed(texto);
    }
}
