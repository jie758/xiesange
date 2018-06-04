package com.elsetravel.mis.pojo;

public class AutoMatchItem {
	private Long id;
	private String label;
	private String value;
	
	public AutoMatchItem(Long id,String label){
		this(id,label,null);
	}
	public AutoMatchItem(Long id,String label,String value){
		this.id = id;
		this.label = label;
		this.value = value;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
