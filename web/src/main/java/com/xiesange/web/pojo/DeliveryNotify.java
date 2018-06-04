package com.xiesange.web.pojo;

public class DeliveryNotify {
	private Long user_id;
	private Long purchase_item_id;
	private Long order_id;
	private String delivery_node;
	private String delivery_time;
	
	public String getDelivery_node() {
		return delivery_node;
	}
	public void setDelivery_node(String delivery_node) {
		this.delivery_node = delivery_node;
	}
	public String getDelivery_time() {
		return delivery_time;
	}
	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Long getPurchase_item_id() {
		return purchase_item_id;
	}
	public void setPurchase_item_id(Long purchase_item_id) {
		this.purchase_item_id = purchase_item_id;
	}
	public Long getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}
	
	
}
