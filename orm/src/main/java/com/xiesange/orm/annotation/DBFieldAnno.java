package com.xiesange.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DBFieldAnno {
	public String name();
	public String length();
	public String type();
	public boolean nullable();
	public String memo() default "";
}
