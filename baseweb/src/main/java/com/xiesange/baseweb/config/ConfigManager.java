package com.xiesange.baseweb.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

public class ConfigManager {
	private static Map<String,IConfig> valueMap = new HashMap<String,IConfig>();
	private static IGlobalConfig globalConfigBean;//global的配置bean
	
	public static void init(String configFilePath) throws Exception {
		XStreamHolder holder = new XStreamHolder("root", BaseNode.class);
		holder.registerConverter(new UniversalXmlConverter(BaseNode.class));

		BaseNode root = (BaseNode) holder.parseFromStream(ConfigManager.class.getResourceAsStream(configFilePath));
		List<BaseNode> configNodeList = root.getChildren();
		if(NullUtil.isEmpty(configNodeList)){
			return;
		}
		String beanClassName;
		IConfig bean;
		for (int i = 0; i < configNodeList.size(); i++) {
			BaseNode configNode = configNodeList.get(i);
			beanClassName = configNode.getAttribute("bean");
			bean = (IConfig) ClassUtil.instance(beanClassName);
			bean.init(configNode);
			valueMap.put(beanClassName, bean);
			
			if(bean instanceof IGlobalConfig){
				globalConfigBean = (IGlobalConfig)bean;
			}
		}
	}
	
	
	public static <T extends IConfig>T getBean(Class<T> beanclass){
		return (T)valueMap.get(beanclass.getName());
	}
	
	public static IGlobalConfig getGlobalBean(){
		return globalConfigBean;
	}
	
	/**
	 * 获取当前节点下某个属性的value值
	 * @author wuyujie Sep 11, 2014 3:08:33 PM
	 * @param node
	 * @param attrName
	 * @return
	 */
	public static String getAttrValueByName(BaseNode node,String attrName){
		BaseNode childNode = node.getChildByAttribute("name", attrName);
		return childNode == null ? null : childNode.getAttribute("value"); 
	}
}
