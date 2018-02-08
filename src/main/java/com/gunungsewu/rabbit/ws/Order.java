package com.gunungsewu.rabbit.ws;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Order {
	@JsonProperty("PoNo")
	private String poNumber;
	private String customerName;
	
	@JsonProperty("NewPO")
	private String newPoNumber;
	
	@JsonProperty("ChatId")
	private Integer chatId;
	
	@JsonProperty("Item")
	List<OrderItem> items;
	
	@JsonProperty("StatusRO")
	private boolean statusRo;
	
	@JsonProperty("OrderDate")
	private String orderDate;
	
	public Order() {
		super();
	}
	
	public Order(String poNumber) {
		super();
		this.poNumber = poNumber;
	}
	
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public List<OrderItem> getItems() {
		return items;
	}
	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	public Integer getChatId() {
		return chatId;
	}
	public void setChatId(Integer chatId) {
		this.chatId = chatId;
	}
	public String getNewPoNumber() {
		return newPoNumber;
	}
	public void setNewPoNumber(String newPoNumber) {
		this.newPoNumber = newPoNumber;
	}

	public boolean isStatusRo() {
		return statusRo;
	}

	public void setStatusRo(boolean statusRo) {
		this.statusRo = statusRo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public static void main1(String[] args) throws Exception {
		Order order = new Order();
		order.setNewPoNumber("78688");
		order.setChatId(3424234);
		
		List<OrderItem> items = new ArrayList<OrderItem>();
		OrderItem item1 = new OrderItem();
		item1.setOrderId("NL1234");
		
		List<OrderItem.Size> sizes = new ArrayList<OrderItem.Size>();
		OrderItem.Size size1 = new OrderItem.Size();
		size1.setSize("M");
		size1.setQuantity(1000);
		sizes.add(size1);
		
		OrderItem.Size size2 = new OrderItem.Size();
		size2.setSize("L");
		size2.setQuantity(2000);
		sizes.add(size2);
		
		item1.setSizes(sizes);
		items.add(item1);
		
		order.setItems(items);
		
		OrderItem item2 = new OrderItem();
		item2.setDescription("Item 2");
		item2.setSizes(sizes);
		items.add(item2);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		String str = mapper.writeValueAsString(order);
		System.out.println(str);
		
	}
	public static void main(String[] args) throws Exception {
		String str = "{\"NewPO\":\"123456799\",\"ChatId\":263354083,\"StatusRO\":\"True\"}";
		ObjectMapper mapper = new ObjectMapper();
		Order ord = mapper.readValue(str, Order.class);
		System.out.println(ord.getNewPoNumber() + " " + ord.isStatusRo());
	}
}
