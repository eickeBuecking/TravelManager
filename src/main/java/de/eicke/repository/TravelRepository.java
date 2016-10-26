package de.eicke.repository;

import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;

import de.eicke.entities.Travel;

@Lazy
public interface TravelRepository extends MongoRepository<Travel, String> {
	public Optional<Travel> findByName(String name);

}
