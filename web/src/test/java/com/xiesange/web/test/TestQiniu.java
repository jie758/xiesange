package com.xiesange.web.test;

import java.io.File;

import com.xiesange.baseweb.util.QiniuUtil;
import com.xiesange.core.util.FileUtil;
import com.xiesange.web.test.frame.TestCmp;

public class TestQiniu {
	public static final String PATH_CONFIG = "/config.xml";
	public static final String PATH_CACHE = "/cache.xml";

	public static void main(String[] args) throws Exception {
		TestCmp.init114();
		
		String dir = "C:\\Users\\Think\\Desktop\\pic\\zhujiexia";
		File[] files = FileUtil.getFileList(dir);
		for(File f : files){
			String path = f.getAbsolutePath();
			QiniuUtil.uploadPic(path,"image/prod/xia/zhujiexia/"+path.substring(path.lastIndexOf("\\")+1));
		}
	}	
}
