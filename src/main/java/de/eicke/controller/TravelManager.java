package de.eicke.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import de.eicke.entities.Travel;
import de.eicke.entities.TravelListItem;
import de.eicke.repository.TravelRepository;
import de.eicke.travelmanager.stream.TravelManagerEventProducer;
import de.eicke.travelmanager.stream.TravelManagerStreamController;

@Component
public class TravelManager {
	
	@Autowired
	TravelRepository repository;
	
	@Autowired
	TravelManagerStreamController streamController;
	
	public Travel registerTravel(Travel newTravel) {
		
		if (newTravel.getName().isEmpty()) {
			throw new RuntimeException("Field Name is mandatory!");
		} else {
			repository.save(newTravel);
			//send message to kafka. better asynchronous..
			streamController.createNewTravel(newTravel);
			return newTravel;
		}
	}
	public Travel updateTravel(Travel update) {
		
		if (update.getId()== null || update.getId().isEmpty()) {
			throw new RuntimeException("ID not set, update not possible!");
		} else {
			repository.save(update);
			streamController.updateTravel(update);
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
	public List<TravelListItem> getAllTravels() {
		List<Travel> travels = repository.findAll();
		return travels.stream().map(travel -> new TravelListItem(travel)).collect(Collectors.toList());
	}
	public void delete(String id) {
		if (id == null || id.isEmpty()) {
			throw new RuntimeException("ID not set, deleting of resource not possible!");
		}
		Travel travel = getTravelWithID(id);
		repository.delete(travel);
		streamController.deleteTravel(travel);
	}
	public List<TravelListItem> filterTravels(String search) {
		Optional<String> string = Optional.of(search);
		if(!string.isPresent()) {
			throw new RuntimeException("No searchterm applied!");
		}
		List<Travel> travels = repository.findByNameStartingWith(string.get());
		return travels.stream().map(travel -> new TravelListItem(travel)).collect(Collectors.toList());
	}
	
	@Async
	public void startReplication() {
		List<Travel> list = repository.findAll();
		list.forEach(item -> streamController.createNewTravel(item));
	}
	
	
}
