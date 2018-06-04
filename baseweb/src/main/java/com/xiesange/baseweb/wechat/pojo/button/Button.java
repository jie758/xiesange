package com.xiesange.baseweb.wechat.pojo.button;

import java.util.List;

public class Button {
	private String name;
	private String type;//click,view
	private List<Button> sub_button;
	
	public Button(String name){
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public List<Button> getSub_button() {
		return sub_button;
	}
	public void setSub_button(List<Button> sub_button) {
		this.sub_button = sub_button;
	}
}
