package de.eicke.travelmanager.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TravelManagerEventProducer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Value("${kafka.topic}")
	private String topic;
	
	@Value("${kafka.streaming}")
	private boolean streaming;
	
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(TravelEvent message) {
        logger.info(String.format("#### -> Producing message -> %s", message));
        if (streaming) {
        	this.kafkaTemplate.send(topic, message.getPayload().getId(), message);
        } else {
        	logger.info("Streaming not activated.");
        }
    }
}
