package com.xiesange.web.test.service;

import org.junit.Before;
import org.junit.Test;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.web.test.frame.TestCmp;
import com.xiesange.web.test.frame.TestFrame;

public class GroupbuyServiceTestBean {
	@Before
	public void init(){
		TestCmp.init114();
	}
	
	@Test
    public void testSendRegisterVCode() throws Exception{
		RequestHeader header = new RequestHeader();
		header.setToken("BF8EhcebIaZvUPnj941Q5aCrraGtN8Lf");
		
		RequestBody body = new RequestBody();
		body.put("order_id", "10000172");
		TestFrame.doTest("web/groupbuy/close.do", header, body);
	}
}
