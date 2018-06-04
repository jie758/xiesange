package com.elsetravel.mis.test.merchant;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.RequestHeader;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.test.frame.TestFrame;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.statement.query.QueryStatement;

public class AgenctServiceTestBean {
	@Test
    public void testQueryList() throws Exception{
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/spring/commonContext.xml","classpath:/spring/servicebean.xml"}
		);
		RequestHeader header = new RequestHeader();
		header.setToken("mis");
		
		RequestBody body = new RequestBody();
		body.put("isApproved", "1");
		body.put("linkman", "骑");
		TestFrame.doTest("web/agency/queryList.do", header, body);
		
	}
	
	@Test
    public void testQueryDetail() throws Exception{
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/spring/commonContext.xml","classpath:/spring/servicebean.xml"}
		);
		RequestHeader header = new RequestHeader();
		header.setToken("mis");
		
		RequestBody body = new RequestBody();
		body.put("merchant_id", "10009");
		TestFrame.doTest("web/agency/queryDetail.do", header, body);
	}
	
	@Test
    public void testQueryTicketList() throws Exception{
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/spring/commonContext.xml","classpath:/spring/servicebean.xml"}
		);
		RequestHeader header = new RequestHeader();
		header.setToken("mis");
		
		RequestBody body = new RequestBody();
		body.put("merchant_id", "10009");
		TestFrame.doTest("web/agency/queryTicketList.do", header, body);
	}
}
