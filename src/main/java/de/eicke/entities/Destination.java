package de.eicke.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Destination {
	
	@Override
	public String toString() {
		return "Destination [id=" + id + ", name=" + name + ", arrival=" + arrival + "]";
	}

	@Id
	private String id;

	private String name;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
	private Date arrival;
	
	public Destination() {
		super();
	}

	public Destination(String name, Date arrival) {
		this.name = name;
		this.arrival = arrival;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		this.arrival = arrival;
	}
}
