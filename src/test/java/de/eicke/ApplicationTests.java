package de.eicke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.isNotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.eicke.controller.TravelManager;
import de.eicke.entities.Destination;
import de.eicke.entities.Travel;
import de.eicke.repository.DestinationRepository;
import de.eicke.repository.TravelRepository;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@Autowired
	TestRestTemplate template = new TestRestTemplate();

	@Autowired
	TravelManager manager;
	@Autowired
	TravelRepository repository;

	@Autowired
	DestinationRepository destinationRepository;

	@Test
	public void contextLoads() {
	}

	@After
	public void prepareDatabase() {
		repository.deleteAll();
	}

	@Test
	public void testTravelCreation() {
		manager.registerTravel(new Travel("Erste Reise", "Die Beschreibung"));
		manager.registerTravel(new Travel("Zweite Reise", "Auch eine Beschreibung"));
		Assert.assertThat("Wrong number of repository entities!", repository.count(), is((long)2));
	}

	@Test
	public void createTravelViaService() {
		long offset = repository.count();
		Travel newTravel = new Travel();
		newTravel.setName("Testreise");
		newTravel.setDescription("Eine wunderschöne Testreise");
		newTravel.setStartDate(new Date());
		template.postForObject("/travels/register", newTravel, Travel.class);
		Assert.assertThat(repository.count(), is(offset + 1));
	}

	@Test
	public void testUpdate() {

		Travel newTravel = new Travel();
		newTravel.setName("Update-Reise");
		newTravel.setDescription("Der alte Text");
		newTravel.setStartDate(new Date());
		manager.registerTravel(newTravel);
		long offset = repository.count();

		String updateText = "Der neue Text";
		newTravel.setDescription(updateText);
		manager.updateTravel(newTravel);

		Travel test = repository.findOne(newTravel.getId());
		Assert.assertNotNull("Couldn't fetch updated object!", test);

		Assert.assertEquals(updateText, test.getDescription());

		Assert.assertThat(repository.count(), is(offset));
	}

	@Test
	public void testUpdateViaRestService() {
		Travel newTravel = new Travel();
		newTravel.setName("Update-Reise");
		newTravel.setDescription("Der alte Text");
		newTravel.setStartDate(new Date());
		manager.registerTravel(newTravel);
		long offset = repository.count();


		String updateText = "Der neue Text";
		newTravel.setDescription(updateText);

		template.put("/travels", newTravel);
		
		Travel test = repository.findOne(newTravel.getId());
		Assert.assertNotNull("Couldn't fetch updated object!", test);

		Assert.assertEquals(updateText, test.getDescription());

		Assert.assertThat(repository.count(), is(offset));
		
	}
	
	@Test
	public void testToAddDestinationsToTravel() {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		newTravel.setStartDate(new Date());
		manager.registerTravel(newTravel);
		long offset = repository.count();
		
		Destination destination1 = new Destination("Bremen", new Date());
		Destination destination2 = new Destination("München", new Date());
		
		destinationRepository.save(destination1);
		destinationRepository.save(destination2);
		newTravel.addDestination(destination1);
		newTravel.addDestination(destination2);
		
		manager.updateTravel(newTravel);
		
		Travel loadedTravel = manager.getTravelWithID(newTravel.getId());
		
		Assert.assertNotNull(loadedTravel.getDestinations());
		
		Assert.assertThat("Anzahl Destinations", loadedTravel.getDestinations().size(), is(2));
	}
}
