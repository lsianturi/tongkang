package com.gunungsewu.database.entity;

import java.util.Date;

public class Issue {
	private Integer id;
	private String msisdn;
	private String userName;
	private Integer userId;
	private Long chatId;
	private String poNo;
	private String orderNo;
	private String topic;
	private String summary;
	private String detail;
	private String attachment1;
	private String att1Detail;
	private String attachment2;
	private String att2Detail;
	private String attachment3;
	private String att3Detail;
	private Integer status;
	private Integer jtracNo;
	private Date createTime;
	private String companyName;
	
	public Issue() {
		super();
	}
	public Issue(Integer id) {
		super();
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Long getChatId() {
		return chatId;
	}
	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAttachment1() {
		return attachment1;
	}
	public void setAttachment1(String attachment1) {
		this.attachment1 = attachment1;
	}
	public String getAttachment2() {
		return attachment2;
	}
	public void setAttachment2(String attachment2) {
		this.attachment2 = attachment2;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAttachment3() {
		return attachment3;
	}
	public void setAttachment3(String attachment3) {
		this.attachment3 = attachment3;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getJtracNo() {
		return jtracNo;
	}
	public void setJtracNo(Integer jtracNo) {
		this.jtracNo = jtracNo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPoNo() {
		return poNo;
	}
	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getAtt1Detail() {
		return att1Detail;
	}
	public void setAtt1Detail(String att1Detail) {
		this.att1Detail = att1Detail;
	}
	public String getAtt2Detail() {
		return att2Detail;
	}
	public void setAtt2Detail(String att2Detail) {
		this.att2Detail = att2Detail;
	}
	public String getAtt3Detail() {
		return att3Detail;
	}
	public void setAtt3Detail(String att3Detail) {
		this.att3Detail = att3Detail;
	}
	@Override
	public String toString() {
		return "id: " + id + ", msisdn: " + msisdn + ", userId: " + userId + ", detail: " + detail + ", topic: "+ topic ;
	}
}
