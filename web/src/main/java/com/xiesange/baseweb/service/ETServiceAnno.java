package com.xiesange.baseweb.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ETServiceAnno {
	public String name();
	public String version();
}
