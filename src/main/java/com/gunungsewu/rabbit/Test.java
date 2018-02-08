package com.gunungsewu.rabbit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
	public static void main(String[] args) throws Exception {
		Order order = new Order();
		order.setCustNo(123456);
		order.setTelegramId(3422342);
		order.setOrderNo("131235");
		order.setPoNumber("A234239");
		
		List<OrderItem> items = new ArrayList<>();
		OrderItem item = new OrderItem();
		item.setId(1);
		item.setDescription("Print Label Snobby Size: M");
		item.setQty(5600.00);
		items.add(item);
		
		item = new OrderItem();
		item.setId(2);
		item.setDescription("Print Label Snobby Size: XL");
		item.setQty(2500.00);
		items.add(item);
		
		order.setItems(items);
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(order));
		
		String jsonString = "{\"custNo\":123456,\"telegramId\":3422342,\"poNumber\":\"A234239\",\"orderNo\":\"131235\",\"items\":[{\"id\":1,\"description\":\"Print Label Snobby Size: M\",\"qty\":5600.0},{\"id\":2,\"description\":\"Print Label Snobby Size: XL\",\"qty\":2500.0}]}";
		Order ord = mapper.readValue(jsonString, Order.class);
		System.out.println( ord.getPoNumber());
		
		
	}
}
