package de.eicke;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.eicke.controller.TravelManager;
import de.eicke.entities.Destination;
import de.eicke.entities.Travel;
import de.eicke.entities.TravelListItem;
import de.eicke.exceptions.TravelManagerException;
import de.eicke.repository.TravelRepository;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ITApplicationTests {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	
	
	@Autowired
    private WebApplicationContext context;


	private MockMvc mockMvc;

    @Before
    public void setup() {
    	mockMvc = MockMvcBuilders
                .webAppContextSetup(context)

                // ADD THIS!!
                .apply(springSecurity())
                .build();
    }
	@Autowired
	TravelManager manager;
	@Autowired
	TravelRepository repository;
	
	@Autowired
	ObjectMapper objectMapper;

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
	public void createTravelViaService() throws JsonProcessingException, Exception {
		long offset = repository.count();
		Travel newTravel = new Travel();
		newTravel.setName("Testreise");
		newTravel.setDescription("Eine wunderschöne Testreise");
		newTravel.setStartDate(new Date());
		// @formatter:off
		mockMvc
			.perform(post("/travels/register").with(user("joe"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newTravel))).andExpect(status().isOk());
					
					
	    // @formatter:on
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
	public void testUpdateViaRestService() throws JsonProcessingException, Exception {
		Travel newTravel = new Travel();
		newTravel.setName("Update-Reise");
		newTravel.setDescription("Der alte Text");
		newTravel.setStartDate(new Date());
		manager.registerTravel(newTravel);
		long offset = repository.count();


		String updateText = "Der neue Text";
		newTravel.setDescription(updateText);

		mockMvc.perform(put("/travels").with(user("joe"))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newTravel)))
				.andExpect(status().isOk());
		
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
	public void testFetchingOfTravelList() {
		Travel newTravel = new Travel();
		String name = "Testname";
		newTravel.setName(name);
		newTravel.setDescription("Testen der Liste.");
		newTravel.setStartDate(new Date());
		newTravel.setStartDate(new Date());
		newTravel = manager.registerTravel(newTravel);
		
		TravelListItem item = manager.getAllTravels().get(0);
		Assert.assertThat("Ungleicher Name.", item.getName(), is(name));
		Assert.assertThat("Ungleiche ID", item.getId(), is(newTravel.getId()));
	}
	
	@Test
	public void testFilteringOfTravelList() {
		Travel newTravel = new Travel();
		String name = "Testname";
		newTravel.setName(name);
		newTravel.setDescription("Testen der Liste.");
		newTravel.setStartDate(new Date());
		newTravel.setStartDate(new Date());
		newTravel = manager.registerTravel(newTravel);
		
		newTravel = new Travel();
		name = "Pestname";
		newTravel.setName(name);
		newTravel.setDescription("Testen der Liste.");
		newTravel.setStartDate(new Date());
		newTravel.setStartDate(new Date());
		newTravel = manager.registerTravel(newTravel);
		java.util.List<TravelListItem> results = manager.filterTravels("Pest");
		Assert.assertThat("Anzahl Ergebnisse stimmt nicht.", results.size(), is((int) 1));
		TravelListItem item = results.get(0);
		Assert.assertThat("Ungleicher Name.", item.getName(), is(name));
		Assert.assertThat("Ungleiche ID", item.getId(), is(newTravel.getId()));
	}
	
	@Test
	public void testDestinationValidation() throws JsonProcessingException, Exception {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/15 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/14 08:34:00"));
		
		newTravel.addDestination(destination1);
		
		mockMvc.perform(post("/travels/register").with(user("joe"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTravel))).andExpect(status().is4xxClientError());
		
	}
	
	@Test
	public void testTravelDeletion() throws Exception {
		Travel newTravel = new Travel();
		newTravel.setName("Reise mit Destinations");
		newTravel.setDescription("Hier werden Destinations getestet.");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		newTravel.setStartDate(format.parse("2016/08/15 08:34:00"));
		Destination destination1 = new Destination("Bremen", format.parse("2016/08/16 08:34:00"));
		
		newTravel.addDestination(destination1);
		
		Travel storedTravel = manager.registerTravel(newTravel);
		
		mockMvc.perform(delete("/travels/" + storedTravel.getId()).with(user("joe"))).andExpect(status().isOk());
		
		
		Assert.assertThat("Repository-Size does not match.", repository.count(), is((long) 0));
	}
	@Test
	public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/travels")).andExpect(status().isUnauthorized());
	}
	
}
