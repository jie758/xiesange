package com.xiesange.web.test.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.RequestHeader;
import com.xiesange.web.test.frame.TestFrame;

public class MessageServiceTestBean {
	@Before
	public void init(){
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/spring/commonContext.xml","classpath:/spring/servicebean.xml"}
		);
	}
	
	@Test
    public void testSendRegisterVCode() throws Exception{
		RequestHeader header = new RequestHeader();
		header.setToken("app");
		
		RequestBody body = new RequestBody();
		body.put("mobile", "13588830404");
		body.put("zone", 86);
		TestFrame.doTest("web/msg/sendRegisterVCode.do", header, body);
	}
}
