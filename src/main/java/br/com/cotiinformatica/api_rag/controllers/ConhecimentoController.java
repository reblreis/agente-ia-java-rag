package br.com.cotiinformatica.api_rag.controllers;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conhecimentos")
public class ConhecimentoController {

    private final VectorStore vectorStore;

    public ConhecimentoController(
            VectorStore vectorStore
    ) {

        this.vectorStore = vectorStore;
    }

    @PostMapping
    public String cadastrar(
            @RequestBody Map<String, String> request
    ) {

        String texto = request.get("texto");

        Document document = new Document(
                texto,
                Map.of(
                        "origem", "api",
                        "tipo", "conhecimento"
                )
        );

        vectorStore.add(
                List.of(document)
        );

        return "Conhecimento armazenado com sucesso.";
    }
}