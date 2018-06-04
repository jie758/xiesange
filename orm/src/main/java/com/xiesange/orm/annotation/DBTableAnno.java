package com.xiesange.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DBTableAnno {
	public String name();
	public String primaryKey();//主键
	public String indexes();//索引
}
