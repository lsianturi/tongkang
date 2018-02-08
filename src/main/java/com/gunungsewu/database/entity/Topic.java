package com.gunungsewu.database.entity;

public class Topic {
	private Integer id;
	private String topic;
	private String subTopic;
	private Integer picUser;
	private String ccUser;
	private Integer topicJtracId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getSubTopic() {
		return subTopic;
	}
	public void setSubTopic(String subTopic) {
		this.subTopic = subTopic;
	}
	public Integer getPicUser() {
		return picUser;
	}
	public void setPicUser(Integer picUser) {
		this.picUser = picUser;
	}
	public String getCcUser() {
		return ccUser;
	}
	public void setCcUser(String ccUser) {
		this.ccUser = ccUser;
	}
	public Integer getTopicJtracId() {
		return topicJtracId;
	}
	public void setTopicJtracId(Integer topicJtracId) {
		this.topicJtracId = topicJtracId;
	}
	@Override
	public String toString() {
		return "Topic: " + topic + ", sub topic: " + subTopic;
	}
}
