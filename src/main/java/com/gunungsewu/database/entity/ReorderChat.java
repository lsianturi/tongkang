package com.gunungsewu.database.entity;

public class ReorderChat extends Chat {
	private String newPo;
	private Integer status;
	
	public ReorderChat() {
		super();
	}
	
	public ReorderChat(Long chatId, String keyword) {
		super(chatId, keyword);
	}
	
	public String getNewPo() {
		return newPo;
	}
	public void setNewPo(String newPo) {
		this.newPo = newPo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
