package de.eicke;

import static org.hamcrest.CoreMatchers.is;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.eicke.controller.TravelManager;
import de.eicke.entities.Destination;
import de.eicke.entities.Travel;
import de.eicke.entities.TravelValidator;
import de.eicke.repository.TravelRepository;
import de.eicke.travelmanager.stream.TravelManagerEventProducer;
import de.eicke.travelmanager.stream.TravelManagerStreamController;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
public class FastUnitTestsTravelManager {
	@TestConfiguration
	static class TravelManagerContextConfiguration {
		
		@Bean
		public TravelManager travelManager() {
			return new TravelManager();
		}
		@Bean
		public TravelManagerStreamController controller() {
			return new TravelManagerStreamController();
		}
		
	}
	
	@Autowired
	private TravelManager manager;
	
	@MockBean
	private TravelManagerStreamController streamController;
	
	@MockBean
	private TravelRepository travelRepository;
	
	@MockBean
	private TravelManagerEventProducer eventProducer;
	
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void provided_valid_travil_entity_successfull_store() throws ParseException {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/15 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/16 08:34:00"));
		
		newTravel.addDestination(destination1);
		
		Travel storedTravel = manager.registerTravel(newTravel); 
		verify(travelRepository, times(1)).save(newTravel);
		verify(streamController, times(1)).createNewTravel(newTravel);
	}
	
	@Test
	public void testValidator() throws Exception {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/16 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/15 08:34:00"));
		newTravel.addDestination(destination1);
		
		Validator validator = new TravelValidator();
		Errors errors = new BeanPropertyBindingResult(newTravel, "newTravel");
		validator.validate(newTravel, errors);
		
		Assert.assertTrue("Hat keine Fehler", errors.hasErrors());
	}
	
}
