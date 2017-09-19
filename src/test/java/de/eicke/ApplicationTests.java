package de.eicke;

import static org.hamcrest.CoreMatchers.is;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.eicke.controller.TravelManager;
import de.eicke.entities.Destination;
import de.eicke.entities.Travel;
import de.eicke.exceptions.TravelManagerException;
import de.eicke.repository.TravelRepository;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@LocalServerPort
    int randomServerPort;
	
	@Value("${server.context-path}")
	private String context;
	
	@Autowired
	TestRestTemplate template = new TestRestTemplate();

	@Autowired
	TravelManager manager;
	@Autowired
	TravelRepository repository;

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
	public void testToAddDestinationsToTravel() throws TravelManagerException {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		newTravel.setStartDate(new Date());
		manager.registerTravel(newTravel);
		
		Destination destination1 = new Destination("Bremen", new Date());
		Destination destination2 = new Destination("München", new Date());
		
		newTravel.addDestination(destination1);
		newTravel.addDestination(destination2);
		
		manager.updateTravel(newTravel);
		
		Travel loadedTravel = manager.getTravelWithID(newTravel.getId());
		
		Assert.assertNotNull(loadedTravel.getDestinations());
		
		Assert.assertThat("Anzahl Destinations", loadedTravel.getDestinations().size(), is(2));
	}
	
	@Test
	public void testDestinationValidation() throws ParseException, TravelManagerException, URISyntaxException {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/15 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/14 08:34:00"));
		
		newTravel.addDestination(destination1);
		
		RequestEntity<Travel> request = RequestEntity.post(new URI("http://localhost:" + randomServerPort + "/"+ context + "/travels/register")).accept(MediaType.ALL).body(newTravel);
		
		ResponseEntity<Void> response = template.exchange(request, Void.class);
		
		Assert.assertThat(response.getStatusCode().is4xxClientError(), is(true));
		
	}
	
	@Test
	public void testTravelDeletion() throws ParseException, TravelManagerException, URISyntaxException {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/15 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/16 08:34:00"));
		
		newTravel.addDestination(destination1);
		
		Travel storedTravel = manager.registerTravel(newTravel);
		
		String request_string = "http://localhost:" + randomServerPort + "/"+ context + "/travels/" + storedTravel.getId();
		
		logger.info("Deleting with request: " + request_string);
		
		RequestEntity<Void> request = RequestEntity.delete(new URI(request_string)).accept(MediaType.ALL).build();
		
		ResponseEntity<Void> response = template.exchange(request, Void.class);
		
		Assert.assertThat("Deletion not successfull, returing code " + response.getStatusCode() + "!",  response.getStatusCode().is2xxSuccessful(), is(true));
		
		Assert.assertThat("Repository-Size does not match.", repository.count(), is((long) 0));
	}
}
