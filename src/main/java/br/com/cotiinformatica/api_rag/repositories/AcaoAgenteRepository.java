package br.com.cotiinformatica.api_rag.repositories;

import br.com.cotiinformatica.api_rag.entities.AcaoAgente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcaoAgenteRepository extends MongoRepository<AcaoAgente, String> {
}