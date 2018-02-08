package com.gunungsewu.database.entity;

public class MQConfig {
	private String hostname;
	private int port;
	private String queueOrderFromBot;
	private String queueOrderToBot;
	private String queueComplaintFromBot;
	private String queueComplaintToBot;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getQueueOrderFromBot() {
		return queueOrderFromBot;
	}
	public void setQueueOrderFromBot(String queueOrderFromBot) {
		this.queueOrderFromBot = queueOrderFromBot;
	}
	public String getQueueOrderToBot() {
		return queueOrderToBot;
	}
	public void setQueueOrderToBot(String queueOrderToBot) {
		this.queueOrderToBot = queueOrderToBot;
	}
	public String getQueueComplaintFromBot() {
		return queueComplaintFromBot;
	}
	public void setQueueComplaintFromBot(String queueComplaintFromBot) {
		this.queueComplaintFromBot = queueComplaintFromBot;
	}
	public String getQueueComplaintToBot() {
		return queueComplaintToBot;
	}
	public void setQueueComplaintToBot(String queueComplaintToBot) {
		this.queueComplaintToBot = queueComplaintToBot;
	}
}
