package de.eicke.travelmanager.stream;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TravelEventSerializer implements Serializer<TravelEvent> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	@Override
	public byte[] serialize(String topic, TravelEvent data) {
		byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return retVal;
	}

	@Override
	public void close() {
		
	}
	
}
