package com.gunungsewu.rabbit.ws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {
	@JsonProperty("OrderId")
	private String orderId;
	
	@JsonProperty("Description")
	private String description;
	
	@JsonProperty("Uom")
	private String uom;
	
	@JsonProperty("Image")
	private String image;
	
	@JsonProperty("QtyPesan")
	private Integer qtyPesan;
	
	@JsonProperty("Size")
	private List<Size> sizes;
	
	@JsonProperty("TglKirim")
	private String tglKirim;
	
	@JsonProperty("StatusKirim")
	private String statusKirim;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<Size> getSizes() {
		return sizes;
	}
	public void setSizes(List<Size> sizes) {
		this.sizes = sizes;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public Integer getQtyPesan() {
		return qtyPesan;
	}
	public void setQtyPesan(Integer qtyPesan) {
		this.qtyPesan = qtyPesan;
	}

	public String getTglKirim() {
		return tglKirim;
	}
	public void setTglKirim(String tglKirim) {
		this.tglKirim = tglKirim;
	}
	public String getStatusKirim() {
		return statusKirim;
	}
	public void setStatusKirim(String statusKirim) {
		this.statusKirim = statusKirim;
	}

	public static class Size {
		@JsonProperty("MerkSize")
		private String size;
		@JsonProperty("Qty")
		private Integer quantity;
		
		public Size() {
		}
		public Size(String size, Integer quantity) {
			this.size = size;
			this.quantity = quantity;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		
	}
}
