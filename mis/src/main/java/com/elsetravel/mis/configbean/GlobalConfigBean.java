package com.elsetravel.mis.configbean;

import java.util.List;

import com.elsetravel.baseweb.config.IGlobalConfig;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.xml.BaseNode;

public class GlobalConfigBean implements IGlobalConfig {
	private static List<BaseNode> ATTR_LIST;
	private static final String LABEL_ATTR_CODE = "system.id";
	
	@Override
	public void init(BaseNode node) throws Exception {
		ATTR_LIST = node.getChildren();
	}
	
	@Override
	public Short getSystem_Id(){
		BaseNode attrNode = getNode(LABEL_ATTR_CODE);
		return attrNode == null ? null : Short.valueOf(attrNode.getAttribute("value"));
	}
	
	private static BaseNode getNode(String code){
		if(NullUtil.isNotEmpty(ATTR_LIST)){
			for(BaseNode node : ATTR_LIST){
				if(node.getAttribute("code").equals(code)){
					return node;
				}
			}
		}
		return null;
	}

}
