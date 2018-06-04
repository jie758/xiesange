package com.elsetravel.mis.test.merchant;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.RequestHeader;
import com.elsetravel.mis.test.frame.TestFrame;

public class CommonServiceTestBean {
	@Test
    public void testQuickMatch() throws Exception{
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
			ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/spring/commonContext.xml","classpath:/spring/servicebean.xml"}
			);
			RequestHeader header = new RequestHeader();
			header.setToken("mis");
			
			RequestBody body = new RequestBody();
			body.put("type", "guider");
			body.put("value", "wu");
			TestFrame.doTest("web/common/quickMatch.do", header, body);
	}
}
