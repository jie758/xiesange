package com.xiesange.core.enumdefine;
/**
 * 通用的键值参数对象
 * @author Wilson 
 * @date 上午10:42:04
 */
public class KeyValueEntry {
	private String code;
	private String value;
	
	public KeyValueEntry(String code,String value){
		this.code = code;
		this.value = value;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
