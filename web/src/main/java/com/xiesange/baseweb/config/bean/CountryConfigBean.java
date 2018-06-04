package com.xiesange.baseweb.config.bean;

import java.util.List;

import com.xiesange.baseweb.config.IConfig;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

public class CountryConfigBean implements IConfig {
	private static final String PATH = "/country.xml";
	private static List<BaseNode> COUNTRY_LIST;
	
	@Override
	public void init(BaseNode node) throws Exception {
	}
	
	
	public static List<BaseNode> getCountyList() throws Exception{
		if(COUNTRY_LIST == null){
			XStreamHolder holder = new XStreamHolder("root", BaseNode.class);
			holder.registerConverter(new UniversalXmlConverter(BaseNode.class));
			BaseNode root = (BaseNode) holder.parseFromStream(CountryConfigBean.class.getResourceAsStream(PATH));
			COUNTRY_LIST = root.getChildren();
		}
		return COUNTRY_LIST;
	}

}
