package org.example.vivesbankproject.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimientosRepository extends MongoRepository<Movimientos, ObjectId> {
    Optional<Movimientos> findByGuid(String guid);
    Optional<Movimientos> findMovimientosByClienteId(String clienteId);

}
