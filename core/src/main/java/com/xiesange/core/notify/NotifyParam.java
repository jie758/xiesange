package com.xiesange.core.notify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.core.enumdefine.IParamEnum;
import com.xiesange.core.notify.target.BaseNotifyTarget;

public class NotifyParam {
	private String templateCode;//告警模板编码
	private Map<String,String> params;//存放参数,和告警模板中匹配
	private List targetList;
	
	public NotifyParam(String templateCode){
		this.templateCode =templateCode;
	};
	
	
	public String getTemplateCode() {
		return templateCode;
	}


	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void addParam(IParamEnum key,String value){
		if(params == null)
			params = new HashMap<String,String>();
		params.put(key.name(), value);
	}


	public List<? extends BaseNotifyTarget> getTargetList() {
		return (List<? extends BaseNotifyTarget>)targetList;
	}


	public void setTargetList(List<? extends BaseNotifyTarget> targetList) {
		this.targetList = targetList;
	}

	public <T extends BaseNotifyTarget>void addTarget(BaseNotifyTarget target){
		if(targetList == null)
			targetList = new ArrayList<T>();
		targetList.add(target);
	}
}
