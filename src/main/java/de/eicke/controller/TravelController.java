package de.eicke.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.eicke.entities.Travel;

@RestController
public class TravelController {
	@Autowired
	TravelManager manager;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(path="/travels/register", method=RequestMethod.POST)
	public Travel register(@RequestBody Travel travel) {
		logger.info("Travelregistration for Travel {}", travel.getName()); 
		return manager.registerTravel(travel);
	}
	@RequestMapping(path="/travels/{someId}", method=RequestMethod.GET)
	public Travel fetchTravel(@PathVariable(value="someId") final String id) {
		logger.info("Request received for Travel {}.", id);
		return manager.getTravelWithID(id);
	}
	@RequestMapping(path="/travels", method=RequestMethod.GET)
	public List<Travel> fetchAllTravels() {
		logger.info("Fetch all travels."); 
		return manager.getAllTravels();
	}
	@RequestMapping(path="/travels", method=RequestMethod.PUT)
	public Travel update(@RequestBody Travel travel) {
		logger.info("Update for Travel {}", travel.getName()); 
		return manager.updateTravel(travel);
	}
}
