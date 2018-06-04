package com.xiesange.baseweb.util;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.core.util.FileUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;

public class QiniuUtil {
	public static final String SPACE_NAME = "xiesange";
	public static UploadManager UPLOAD_MANAGER = new UploadManager();
	public static Auth AUTH = null;
	private static Logger logger = LogUtil.getLogger(QiniuUtil.class);
	
	public static String getUpToken() throws Exception{
		return getUpToken(null);
	}
	
	public static String getUpToken(String key) throws Exception{
		if(AUTH == null){
			AUTH = Auth.create(getAccessToken(), getSecretToken());
		}
		String token = NullUtil.isEmpty(key) ? 
				AUTH.uploadToken(SPACE_NAME)
				: AUTH.uploadToken(SPACE_NAME,key);//覆盖上传
		return token;
	}
	
	public static void uploadPic(String localPath) throws Exception{
		uploadPic(localPath,null);
	}
	
	public static void uploadPic(String localPath,String fileName) throws Exception{
		if(AUTH == null){
			AUTH = Auth.create(getAccessToken(), getSecretToken());
		}
		
		String token = getUpToken(fileName);
		
		UPLOAD_MANAGER.put(localPath, fileName, token);
		
		logger.debug("success to upload 7niu : "+localPath+" -> "+fileName);
	}
	
	public static void uploadPic(InputStream is) throws Exception{
		uploadPic(is,null);
	}
	public static void uploadPic(InputStream is,String fileName) throws Exception{
		if(AUTH == null){
			AUTH = Auth.create(getAccessToken(), getSecretToken());
		}
		String token = getUpToken(fileName);
		
		UPLOAD_MANAGER.put(
        		FileUtil.getFileContentBytes(is),
        		fileName, 
        		token);
		logger.debug("success to upload 7niu : "+fileName);
	}
	
	public static String getAccessToken() throws Exception{
		return SysparamCmp.get(SysparamDefine.QINIU_ACCESS_TOKEN);
	}
	
	public static String getSecretToken() throws Exception{
		return SysparamCmp.get(SysparamDefine.QINIU_ACCESS_SECRET);
	}
}
