package com.gunungsewu.rabbit;

import java.util.List;

public class Order {
	private int custNo;
	private int telegramId;
	private String poNumber;
	private String orderNo;
	
	private List<OrderItem> items;
	public int getCustNo() {
		return custNo;
	}
	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}
	public int getTelegramId() {
		return telegramId;
	}
	public void setTelegramId(int telegramId) {
		this.telegramId = telegramId;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
}
