package com.xiesange.web.test.service;

import org.junit.Before;
import org.junit.Test;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.web.test.frame.TestCmp;
import com.xiesange.web.test.frame.TestFrame;

public class ManageServiceTestBean {
	@Before
	public void init(){
		TestCmp.initLocalhost();
	}
	
	@Test
    public void testCreatePurchase() throws Exception{
		RequestHeader header = new RequestHeader();
		header.setToken("app");
		
		RequestBody body = new RequestBody();
		body.put("delivery_date", "2016-08-26");
		TestFrame.doTest("web/manage/createPurchase.do", header, body);
	}
	
	@Test
    public void testQueryUserList() throws Exception{
		RequestHeader header = new RequestHeader();
		header.setToken("app");
		header.setPage_index(0);
		
		RequestBody body = new RequestBody();
		body.put("orderby_order_count", 1);
		//body.put("p", "2016-08-26");
		TestFrame.doTest("web/manage/queryUserList.do", header, body);
	}
}
