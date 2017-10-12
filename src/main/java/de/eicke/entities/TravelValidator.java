package de.eicke.entities;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
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

		if (travel.getStartDate() == null) {
			errors.rejectValue("startDate", "STARTDATE_MANDATORY");
		} else if (travel.getDestinations() != null) {
			int idx = 0;
			for (Destination destination : travel.getDestinations()) {
				if(destination.getArrival().before(travel.getStartDate()))
					errors.rejectValue("destinations[" + idx + "].arrival", "NOT_BEFORE_TRAVEL_START");
				
				if(travel.getEndDate() != null && destination.getArrival().after(travel.getEndDate()))
					errors.rejectValue("destinations[" + idx + "].arrival", "NOT_AFTER_TRAVEL_END");
				
				idx++;
			}
		}
	}
}
