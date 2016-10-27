package de.eicke.entities;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TravelValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return Travel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Travel travel = (Travel) target;
		if (travel.getName().isEmpty())
			errors.rejectValue("name", "NAME_MANDATORY");

		if (travel.getStartDate() == null)
			errors.rejectValue("startDate", "STARTDATE_MANDATORY");
		
		for (Destination destination : travel.getDestinations()) {
			if(destination.getArrival().before(travel.getStartDate()))
				errors.rejectValue("arrival", "NOT_BEFORE_TRAVEL_START");
			
			if(travel.getEndDate() != null && destination.getArrival().after(travel.getEndDate()))
				errors.rejectValue("arrival", "NOT_AFTER_TRAVEL_END");
		}
	}
}
