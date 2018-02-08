package com.gunungsewu.database.entity;

import java.util.HashMap;
import java.util.Map;

import com.gunungsewu.rabbit.ws.Order;
import com.gunungsewu.rabbit.ws.OrderItem;

public class SessionHolder {
	private Issue issue;
	private Integer status;
	private Order order;
	private Order newOrder;
	private NLUser user;
	private Map<String, Chat> chat = new HashMap<String, Chat>();
	
	private OrderItem currentOrderItem;
	private String currentSize;
	
	public SessionHolder() {
		super();
	}
	public SessionHolder(NLUser user) {
		super();
		this.user = user;
	}
	public SessionHolder(NLUser user, Order order, Issue issue) {
		super();
		this.issue = issue;
		this.user = user;
		this.order = order;
	}
	public Issue getIssue() {
		return issue;
	}
	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public NLUser getUser() {
		return user;
	}
	public void setUser(NLUser user) {
		this.user = user;
	}
	public Order getNewOrder() {
		return newOrder;
	}
	public void setNewOrder(Order newOrder) {
		this.newOrder = newOrder;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public OrderItem getCurrentOrderItem() {
		return currentOrderItem;
	}
	public void setCurrentOrderItem(OrderItem currentOrderItem) {
		this.currentOrderItem = currentOrderItem;
	}
	public String getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(String currentSize) {
		this.currentSize = currentSize;
	}
	public Map<String, Chat> getChat() {
		return chat;
	}
	public void setChat(Map<String, Chat> chat) {
		this.chat = chat;
	}
}
