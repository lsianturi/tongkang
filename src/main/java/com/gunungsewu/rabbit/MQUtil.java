package com.gunungsewu.rabbit;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.gunungsewu.database.entity.MQConfig;
import com.gunungsewu.database.entity.MQName;
import com.gunungsewu.database.service.Constant;
import com.gunungsewu.database.service.NLBotService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MQUtil {
	private MQConfig mqCfg = NLBotService.getInstance().getMQConfig();
	private ConnectionFactory factory=null;
	Channel channel = null;
	Connection connection = null;
	Map<String, String> map = null;
	private MQUtil() {
		factory = new ConnectionFactory();
		factory.setHost(mqCfg.getHostname());
		factory.setPort(mqCfg.getPort());
		List<MQName> ques = NLBotService.getInstance().getQueueNames();
		map = ques.stream().collect(Collectors.toMap(MQName::getId, MQName::getName));
	}
	private static volatile MQUtil instance;
	public static MQUtil getInstance() {
		final MQUtil currentInstance;
		if (instance == null) {
			synchronized (MQUtil.class) {
				if (instance == null) {
					instance = new MQUtil();
				}
				currentInstance = instance;
			}
		} else {
			currentInstance = instance;
		}
		return currentInstance;
	}

	public void postOrderToMq(String order) throws IOException, TimeoutException {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.basicPublish("", map.get(Constant.TEL_ORDER_TO_NLIS), null, order.getBytes("UTF-8"));
	}
	
	public void postIssueToMq(String issue) throws IOException, TimeoutException {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.basicPublish("", map.get(Constant.TEL_ISSUE_TO_JTRAC), null, issue.getBytes("UTF-8"));
	}
	
	public void postJtracTicketToTELMq(String ticket) throws IOException, TimeoutException {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.basicPublish("", map.get(Constant.TEL_ISSUE_FROM_JTRAC), null, ticket.getBytes("UTF-8"));
	}

}
