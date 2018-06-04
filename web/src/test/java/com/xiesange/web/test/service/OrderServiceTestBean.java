package com.xiesange.web.test.service;

import org.junit.Before;
import org.junit.Test;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.web.test.frame.TestCmp;
import com.xiesange.web.test.frame.TestFrame;

public class OrderServiceTestBean {
	@Before
	public void init(){
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		TestCmp.initLocalhost();	
	}
	
	@Test
    public void testQueryList() throws Exception{
		RequestHeader header = new RequestHeader();
		header.setToken("app");
		header.setPage_index(0);
		header.setPage_count(20);
		
		RequestBody body = new RequestBody();
		body.put("mobile", "13588830404");
		TestFrame.doTest("web/order/queryList.do", header, body);
	}
}
