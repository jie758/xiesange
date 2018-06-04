package com.xiesange.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

public class SpringUtil implements ApplicationContextAware{
	private static final String KEY_JDBCTEMPLATE = "jdbcTemplate";
	private static ApplicationContext context;
	
	public static Object getBeanByName(String name){
		return context.getBean(name);
	}
	
	public static <T>T getBeanByClass(Class<T> className){
		return context.getBean(className);
	}

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		if (context == null) {
			return;
		}
		SpringUtil.context = context;
	}
	
	public static JdbcTemplate getJdbcTemplateBean(){
		return (JdbcTemplate)context.getBean(KEY_JDBCTEMPLATE);
	}
}
