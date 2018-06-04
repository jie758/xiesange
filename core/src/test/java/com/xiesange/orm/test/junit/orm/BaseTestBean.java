package com.xiesange.orm.test.junit.orm;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseTestBean {
	//protected CommonDao dao;
    @Before
    public void init() throws Exception{
        //dbClient = OrmTestUtil.createDBClient();
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"/dbContext.xml","/commonContext.xml"});
        
       //dao = (CommonDao)context.getBean("commonDao");
    }
}
