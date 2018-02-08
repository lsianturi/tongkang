package com.gunungsewu.rabbit;

import java.util.ArrayList;
import java.util.List;

import com.gunungsewu.database.entity.MQConfig;
import com.gunungsewu.database.service.NLBotService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

  public static void main(String[] argv) throws Exception {
	  
	  MQConfig mqCfg = NLBotService.getInstance().getMQConfig();
	  
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(mqCfg.getHostname());
    factory.setPort(mqCfg.getPort());
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

//    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
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
	
//	ObjectMapper mapper = new ObjectMapper();
//	String message = mapper.writeValueAsString(order);
	String message = "{\"NewPO\":\"123456\",\"ChatId\":263354083,\"Item\":[{\"OrderId\":\"NL137613\",\"QtyPesan\":310,\"Size\":[{\"MerkSize\":\"S\",\"Qty\":100},{\"MerkSize\":\"M\",\"Qty\":200},{\"MerkSize\":\"L\",\"Qty\":10}]}]}";
	
    channel.basicPublish("", mqCfg.getQueueOrderToBot(), null, message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + message + "'");

    channel.close();
    connection.close();
  }
}
