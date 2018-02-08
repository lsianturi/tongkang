package com.gunungsewu.rabbit.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

public class POType {
	@JsonProperty("Id")
	private int id;
	@JsonProperty("Name")
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
