package com.gunungsewu.database.entity;

public class Chat {
	private Integer id;
	private Long chatId;
	private Integer userId;
	private String name;
	private String type;
	private String text;
	private String keyword;
	
	public Chat(){}

	public Chat(Long chatId, Integer userId, String name, String type, String text) {
		super();
		this.chatId = chatId;
		this.userId = userId;
		this.name = name;
		this.type = type;
		this.text = text;
	}

	public Chat(Long chatId, String keyword) {
		super();
		this.chatId = chatId;
		this.keyword = keyword;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
