package com.xiesange.baseweb.pojo;

import java.lang.reflect.Method;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.ServiceManager;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.BaseNode;

public class UrlNode extends BaseNode{
	private static final String KEY_CODE = "code";
	private static final String KEY_NEED_AUTH = "need_auth";
	private static final String KEY_NEED_SN = "need_sn";
	private static final String KEY_NEED_VERSION = "need_version";
	private static final String KEY_IS_ENCRYPT = "is_encrypt";
	private static final String KEY_REDIRECT = "redirect";
	private static final String KEY_REWARD = "reward";
	
	public UrlNode(String tagName) {
		super(tagName);
	}
	
	/*public UrlNode(BaseNode node) {
		this.node = node;
	}*/
	
	public String getUrl() {
		return this.getAttribute(KEY_CODE);
	}

	public boolean isNeedAuth() {
		String needAuth = this.getAttribute(KEY_NEED_AUTH);
		return NullUtil.isEmpty(needAuth) ? true : Boolean.valueOf(needAuth);//默认是true
	}

	public boolean isNeedSn() {
		String needSn = this.getAttribute(KEY_NEED_SN);
		return NullUtil.isEmpty(needSn) ? true : Boolean.valueOf(needSn);//默认是true
	}
	
	public boolean isNeedVersion() {
		String needVersion = this.getAttribute(KEY_NEED_VERSION);
		return NullUtil.isEmpty(needVersion) ? true : Boolean.valueOf(needVersion);//默认是true
	}

	
	
	public boolean isEncrypt(){
		String isEncrypt = this.getAttribute(KEY_IS_ENCRYPT);
		return NullUtil.isEmpty(isEncrypt) ? false : Boolean.valueOf(isEncrypt);//默认是false
	}
	
	public String getRedirect(){
		return this.getAttribute(KEY_REDIRECT);
	}
	public String getReward(){
		return this.getAttribute(KEY_REWARD);
	}
	
	public String[] getUrlElements(){
		String[] urlItems = getUrl().split("/");
		String m = urlItems[urlItems.length-1];
		String methodName = m.substring(0,m.lastIndexOf("."));//把后缀.do去掉
		
		urlItems[2] = methodName;
		return urlItems;
		/*String[] urlItems = url.split("/");
		
		String serviceName = urlItems[urlItems.length-2];
		
		String m = urlItems[urlItems.length-1];
		String methodName = m.substring(0,m.lastIndexOf("."));//把后缀.do去掉
		
		AbstractService service = ServiceManager.getService(serviceName);
		if(service == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_SERVICE_NOT_FOUND);
		}
		
		Method method = ServiceManager.getServiceMethod(serviceName, methodName);
		if(method == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_METHOD_NOT_FOUND);
		}*/
	}
}
