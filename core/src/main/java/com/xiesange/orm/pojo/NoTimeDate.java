package com.xiesange.orm.pojo;

import java.util.Date;
/**
 * 不带时间精度的日期类型。
 * 如果数据库中是带时间精度的日期，那就是生成Date类型，不带时间精度的就生成DateNoTime类型。
 * 这个类的作用纯粹是为了在json串序列化的时候和Date类型区分出来，因为json序列化时注册的一些processor无法获取到任何实体相关的信息，只有当前序列化的字段名称和值，
 * 所以如果值类型都是Date的话，默认都是按照yyyy-mm-dd hh:MM:ss来解析的，这样如果把类似2014-10-10 00:00:00这样无时间精度的值返回到前台的话就显得很不友好了。
 * json序列化的时候专门定义了一个DateNoTimeJsonValueProcessor类来解析这个DateNoTime字段
 * @author wuyujie Dec 12, 2014 5:43:53 PM
 *
 */
public class NoTimeDate extends Date {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoTimeDate(){
		super();
	}
	
	public NoTimeDate(long date){
		super(date);
	}
}
