package com.xiesange.baseweb.request;

public class RequestParam {
	private RequestHeader header;
	private RequestBody body;
	private String input;
	public RequestParam(RequestHeader header,RequestBody body,String input){
		this.header = header;
		this.body = body;
		this.input = input;
	}
	public RequestHeader getHeader() {
		return header;
	}
	public RequestBody getBody() {
		return body;
	}
	public String getInput() {
		return input;
	}
	
}
