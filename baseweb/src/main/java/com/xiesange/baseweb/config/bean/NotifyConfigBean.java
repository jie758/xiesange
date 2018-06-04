package com.xiesange.baseweb.config.bean;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.baseweb.config.IConfig;
import com.xiesange.baseweb.pojo.CodeNamePojo;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

public class NotifyConfigBean implements IConfig {
	private static final String PATH = "/notify.xml";
	private static List<BaseNode> NOTIFY_LIST;
	@Override
	public void init(BaseNode node) throws Exception {
		XStreamHolder holder = new XStreamHolder("root", BaseNode.class);
		holder.registerConverter(new UniversalXmlConverter(BaseNode.class));
		BaseNode root = (BaseNode) holder.parseFromStream(CountryConfigBean.class.getResourceAsStream(PATH));
		NOTIFY_LIST = root.getChildren();
	}
	
	public static List<CodeNamePojo> getNotifyTypeList(){
		List<CodeNamePojo> result = new ArrayList<CodeNamePojo>();
		for(BaseNode notifyNode : NOTIFY_LIST){
			result.add(new CodeNamePojo(
				notifyNode.getAttribute("code"),
				notifyNode.getAttribute("name")
			));
		}
		
		return result;
	}
	
}
