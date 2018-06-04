package com.xiesange.baseweb.request;

import java.lang.reflect.Method;

import com.xiesange.baseweb.service.AbstractService;

public class UrlElement {
	private AbstractService service;
	private Method method;
	
	public UrlElement(AbstractService service,Method method){
		this.service = service;
		this.method = method;
	}

	public AbstractService getService() {
		return service;
	}

	public void setService(AbstractService service) {
		this.service = service;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	
}
