package com.gunungsewu.jtrac;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunungsewu.database.entity.Issue;
import com.gunungsewu.database.entity.MQConfig;
import com.gunungsewu.database.entity.MQName;
import com.gunungsewu.database.entity.Topic;
import com.gunungsewu.database.service.Constant;
import com.gunungsewu.database.service.NLBotService;
import com.gunungsewu.rabbit.MQUtil;
import com.gunungsewu.rabbit.ws.Ticket;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class JtracMain {
	Logger log = LoggerFactory.getLogger(JtracMain.class);
	private NLBotService svc = NLBotService.getInstance();
	private Channel channel = null;

	public void monitorTelegramQueue() {
		MQConfig mqCfg = svc.getMQConfig();
		List<MQName> ques = svc.getQueueNames();
		Map<String, String> map = ques.stream().collect(Collectors.toMap(MQName::getId, MQName::getName));

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(mqCfg.getHostname());
		try {
			Connection connection = factory.newConnection();
			channel = connection.createChannel();
			ObjectMapper mapper = new ObjectMapper();
			Consumer consumerComplaint = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					Issue issue = null;
					Topic topic = null;
					StringBuilder detail = null;
					try {
						issue = mapper.readValue(message, Issue.class);

						String[] sums = issue.getDetail().split(" ");
						if (sums.length > 4) {
							issue.setSummary(sums[0] + " " + sums[1] + " " + sums[2] + " " + sums[3] + "...");
						} else {
							issue.setSummary(issue.getDetail());
						}

						detail = new StringBuilder();
						detail.append("From: " + issue.getMsisdn() + " - " + issue.getUserName());
						detail.append("\nPerusahaan: " + issue.getCompanyName());
						detail.append("\nNo PO: " + issue.getPoNo());
						detail.append("\nNo Order: " + issue.getOrderNo());
						detail.append("\nIssue: " + issue.getDetail());
						if (issue.getAtt1Detail() != null && issue.getAtt1Detail().trim().length() > 1) {
							detail.append("\n" + issue.getAtt1Detail());
						}
						issue.setDetail(detail.toString());

						topic = NLBotService.getInstance().getTopic(issue.getTopic(), null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (issue != null && topic != null) {
						try {
							Integer ticketNo = JtracUtil.getInstance().postToJtrac(issue, topic);
							Ticket ticket = new Ticket();
							ticket.setChatId(issue.getChatId());
							ticket.setTicketNo(ticketNo);
							ticket.setSummary(issue.getSummary());
							MQUtil.getInstance().postJtracTicketToTELMq(mapper.writeValueAsString(ticket));
							channel.basicAck(envelope.getDeliveryTag(), true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					log.debug("Jtrac confirmation:\n" + message);
				}
			};
			channel.basicConsume(map.get(Constant.TEL_ISSUE_TO_JTRAC), false, consumerComplaint);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		new JtracMain().monitorTelegramQueue();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Ticket ticket = new Ticket();
			ticket.setChatId(263354083L);
			ticket.setTicketNo(1234);
			ticket.setSummary("Summary");
			MQUtil.getInstance().postJtracTicketToTELMq(mapper.writeValueAsString(ticket));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
