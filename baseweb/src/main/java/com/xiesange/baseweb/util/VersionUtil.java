package com.xiesange.baseweb.util;

import com.xiesange.baseweb.request.RequestHeader;

public class VersionUtil {
	public static boolean is2_1(RequestHeader header){
		return "2.1".equals(header.getApp_version());
	}
	
	public static boolean is2_0(RequestHeader header){
		return "2.0".equals(header.getApp_version());
	}
	
	public static boolean is2_2(RequestHeader header){
		return "2.2".equals(header.getApp_version());
	}
	
	public static boolean is3_0(RequestHeader header){
		return "3.0".equals(header.getApp_version());
	}
}
