package de.eicke.travelmanager.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.eicke.entities.Travel;

@Component
public class TravelManagerStreamController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TravelManagerEventProducer producer;
	
	public void createNewTravel(Travel newTravel) {
		TravelEvent event = new TravelEvent(EventTypes.CREATE, newTravel);
		producer.sendMessage(event);
		logger.debug("Send creation-message via Kafka: {}", event);
	}

	public void updateTravel(Travel update) {
		TravelEvent event = new TravelEvent(EventTypes.UPDATE, update);
		producer.sendMessage(event);
		logger.debug("Send update-message via Kafka: {}", event);
	}
	
	public void deleteTravel(Travel toDelete) {
		TravelEvent event = new TravelEvent(EventTypes.DELETE, toDelete);
		producer.sendMessage(event);
		logger.debug("Send deletion-message via Kafka: {}", event);
	}
}


