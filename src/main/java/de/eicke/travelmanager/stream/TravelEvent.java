package de.eicke.travelmanager.stream;

import de.eicke.entities.Travel;

public class TravelEvent {
	private EventTypes eventType;
	private Travel payload;
	
	public TravelEvent(EventTypes eventType, Travel payload) {
		super();
		this.eventType = eventType;
		this.payload = payload;
	}

	public EventTypes getEventType() {
		return eventType;
	}

	public Travel getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "TravelEvent [eventType=" + eventType + ", payload=" + payload + "]";
	}
	

}
