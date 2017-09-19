package de.eicke.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.eicke.entities.Travel;
import de.eicke.repository.TravelRepository;

@Component
public class TravelManager {
	
	@Autowired
	TravelRepository repository;
	
	public Travel registerTravel(Travel newTravel) {
		
		if (newTravel.getName().isEmpty()) {
			throw new RuntimeException("Field Name is mandatory!");
		} else {
			repository.save(newTravel);
			return newTravel;
		}
	}
	public Travel updateTravel(Travel update) {
		
		if (update.getId()== null || update.getId().isEmpty()) {
			throw new RuntimeException("ID not set, update not possible!");
		} else {
			repository.save(update);
			return update;
		}
	}
	public Travel getTravelWithID(String id) {
		if(id == null || id.isEmpty()) {
			throw new RuntimeException("ID not set, fetching of ressource not possible.");
		}
		Travel result = repository.findOne(id);
		if (result == null) {
			throw new RuntimeException("Ressource with ID " + id + " not found!");
		}
		return result;
	}
	public List<Travel> getAllTravels() {
		return repository.findAll();
	}
	public void delete(String id) {
		if (id == null || id.isEmpty()) {
			throw new RuntimeException("ID not set, deleting of resource not possible!");
		}
		Travel travel = getTravelWithID(id);
		repository.delete(travel);
	}
}
