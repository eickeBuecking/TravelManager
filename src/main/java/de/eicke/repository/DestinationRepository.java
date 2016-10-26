package de.eicke.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.eicke.entities.Destination;

@Lazy
public interface DestinationRepository  extends MongoRepository<Destination, String> {

}
