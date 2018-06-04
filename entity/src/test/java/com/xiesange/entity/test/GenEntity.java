package com.xiesange.entity.test;

import com.xiesange.core.util.EncryptUtil;
import com.xiesange.orm.test.DBParam;
import com.xiesange.orm.test.tool.DB2EntityTool;




public class GenEntity {
	public static void main(String[] args) throws Exception {
		System.out.println(EncryptUtil.MD5.encode("123"));
		/*DB2EntityTool.run(
				GenEntity.class,
				buildDBParam(),
				"com.xiesange.gen.dbentity",
				"com.xiesange.orm");*/
	}
	
	
	private static DBParam buildDBParam(){
		DBParam param = new DBParam();
		param.setHost("localhost");
		param.setUser("root");
		param.setPassword("niubiSQL1209%");
		param.setDb("xiesange");
		return param;
	}
}
