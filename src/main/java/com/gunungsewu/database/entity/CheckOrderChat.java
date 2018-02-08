package com.gunungsewu.database.entity;

public class CheckOrderChat extends Chat{
	private String type;
	
	public CheckOrderChat() {
		super();
	}
	
	public CheckOrderChat(Long chatId, String keyword) {
		super(chatId, keyword);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
