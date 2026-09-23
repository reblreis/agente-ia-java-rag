package br.com.cotiinformatica.api_rag.repositories;

import br.com.cotiinformatica.api_rag.entities.Conversa;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversaRepository extends MongoRepository<Conversa, String> {


    /*
        Query Methods -> findTop3ByNomeUsuarioOrderByDataHoraDesc
        find - Iniciar um query method
        Top3 - 3 primeiros registros
        ByNomeUsuario - Filtrar pelo campo nomeUsuario
        OrderByDataHoraDesc - Ordenar por dataHora decrescente
     */

    List<Conversa> findTop3ByNomeUsuarioOrderByDataHoraDesc(
        String nomeUsuario
    );
}
