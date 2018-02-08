package com.gunungsewu.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NLUser  {
	@JsonProperty("ChatId")
    private Integer chatId;
	
	private String name;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("Msisdn")
	private String msisdn;

	public Integer getChatId() {
		return chatId;
	}
	public void setChatId(Integer chatId) {
		this.chatId = chatId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public static void main(String[] args) throws Exception{
		NLUser u = new NLUser();
		u.setChatId(23421);
		u.setName("Lambok");
		u.setCompanyName("GUnung sewu");
		u.setMsisdn("01233243");
		
		ObjectMapper mapper = new ObjectMapper();
		String s = mapper.writeValueAsString(u);
		
		System.out.println(s);
		
		String s1 = "{\"ChatId\":242423421,\"CompanyName\":\"BTPN\"}";
		u = mapper.readValue(s1, NLUser.class);
		
		System.out.println(u.toString());
	}
	
	@Override
	public String toString() {
		return "Chat Id: " + chatId + "\n" +
			   "Name: " + name + "\n" +
			   "Company name: " + companyName + "\n" +
			   "Mobile: " + msisdn;
	}
}
