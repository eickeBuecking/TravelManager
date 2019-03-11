package de.eicke.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.eicke.entities.StatusMessage;
import de.eicke.entities.Travel;
import de.eicke.entities.TravelListItem;
import de.eicke.entities.TravelValidator;
import de.eicke.exceptions.ErrorMessage;
import de.eicke.exceptions.TravelManagerException;
import de.eicke.travelmanager.stream.TravelManagerEventProducer;

@RestController
public class TravelController {
	@Autowired
	TravelManager manager;
	
	@Autowired
	TravelValidator travelValidator;
	
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(travelValidator);
    }
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(path="/travels/register", method=RequestMethod.POST)
	public Travel register(@Valid @RequestBody Travel travel) {
		logger.info("Travelregistration for Travel {}", travel.getName()); 
		return manager.registerTravel(travel);
	}
	@RequestMapping(path="/travels/{someId}", method=RequestMethod.GET)
	public Travel fetchTravel(@PathVariable(value="someId") final String id) {
		logger.info("Request received for Travel {}.", id);
		return manager.getTravelWithID(id);
	}

	@RequestMapping(path="/travels", method=RequestMethod.GET)
	public List<TravelListItem> filterTravels(@RequestParam("search") Optional<String> searchTerm) {
		if(searchTerm.isPresent()) {
			logger.info("Fetch travels with filter: {}", searchTerm.get());
			return manager.filterTravels(searchTerm.get());
		} else {

			logger.info("No searchterm applied, fetching all.");
			return manager.getAllTravels();
		}
	}
	@RequestMapping(path="/travels", method=RequestMethod.PUT)
	public Travel update(@RequestBody Travel travel) {
		logger.info("Update for Travel {}", travel.getName()); 
		return manager.updateTravel(travel);
	}
	@RequestMapping(path="/travels/{someId}", method=RequestMethod.DELETE)
	public void delete(@PathVariable(value="someId") final String id) {
		logger.info("Deleting travel with id " + id);
		manager.delete(id);
	}
	
	@ExceptionHandler(TravelManagerException.class)
	public ResponseEntity<ErrorMessage> exceptionHandler(Exception ex) {
		ErrorMessage error = new ErrorMessage();
		error.setErrorCode(HttpStatus.PRECONDITION_FAILED.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<ErrorMessage>(error, HttpStatus.OK);
	}
	
	@RequestMapping(path="/healthy", method=RequestMethod.GET)
	public ResponseEntity<StatusMessage> getHealthStatus() {
		logger.info("Health-Check!");
		return new ResponseEntity<StatusMessage>(new StatusMessage("All fine!"), HttpStatus.OK);
	}
	
	@RequestMapping(path="/travels/stream", method=RequestMethod.GET)
	public ResponseEntity<StatusMessage> replayAllTravels() {
		manager.startReplication();
		return new ResponseEntity<StatusMessage>(new StatusMessage("Replication started!"), HttpStatus.OK);
	}
}
