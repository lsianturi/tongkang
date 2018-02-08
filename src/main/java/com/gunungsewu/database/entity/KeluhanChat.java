package com.gunungsewu.database.entity;

public class KeluhanChat extends Chat {
	private String orderId;
	private String typeKeluhan;
	private String detailKeluhan;
	private Integer ticketId;
	
	public KeluhanChat() {
		super();
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public KeluhanChat(Long chatId, String keyword) {
		super(chatId, keyword);
	}
	
	public String getTypeKeluhan() {
		return typeKeluhan;
	}
	public void setTypeKeluhan(String typeKeluhan) {
		this.typeKeluhan = typeKeluhan;
	}
	public String getDetailKeluhan() {
		return detailKeluhan;
	}
	public void setDetailKeluhan(String detailKeluhan) {
		this.detailKeluhan = detailKeluhan;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
}
