package de.eicke.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TravelListItem {
	private String id;
	private String name;
	private String description;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
	private Date startDate;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
	private Date endDate;
	
	public TravelListItem(String id, String name, String description, Date startDate, Date endDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public TravelListItem(Travel travel) {
		this(travel.getId(),
				travel.getName(),
				travel.getDescription(),
				travel.getStartDate(),
				travel.getEndDate());
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	
	
}
