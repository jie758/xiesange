package com.xiesange.web.test.frame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCmp {
	public static void init114(){
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_114_test.xml","classpath:/test/commonContext_test.xml"}
		);
	}
	
	public static void initLocalhost(){
		//初始化spring相关配置，数据源要用test专用的，其它的可以用正式代码里的
		ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[]{"classpath:/test/dbContext_localhost_test.xml","classpath:/test/commonContext_test.xml"}
		);
	}
}
