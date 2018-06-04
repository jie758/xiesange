package com.xiesange.baseweb;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;

public class ServiceManager {
	private static final Logger logger = LogUtil.getLogger(ServiceManager.class);
	
	private static Map<String,Class<? extends AbstractService>> SERVICE_MAPPING = new HashMap<String,Class<? extends AbstractService>>();
	private static Map<String,AbstractService> SERVICEBEAN_MAPPING = new HashMap<String,AbstractService>();
	
	private static Map<String,Method> methodMapping = new HashMap<String,Method>();//key格式为serviceName.methodName
	
	/*public static<T extends ServiceProxy>T getFlow(String flowName){
		return (T)SpringUtil.getBeanByName("flow_"+flowName);
	}*/
	
	
	public static void registService(String serviceName,String version,Class<? extends AbstractService> clz){
		if(NullUtil.isEmpty(version)){
			SERVICE_MAPPING.put(serviceName, clz);//基础版的服务类
		}else{
			SERVICE_MAPPING.put(serviceName+"_V"+version, clz);//某个特定版本的服务类
		}
		
	}
	
	/**
	 * 获取对应的service类，可以指定获取哪个版本的service类。优先获取对应版本的service，如果没有配置则获取基础版本service
	 * @param serviceName,服务名称，配置在每个类的annotation上
	 * @param appVersion,指定版本号
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T extends AbstractService>T getService(String serviceName,String appVersion) throws InstantiationException, IllegalAccessException{
		T serviceBean = getService(serviceName+"_V"+appVersion);
		if(serviceBean == null){
			serviceBean = getService(serviceName);
		}
		return serviceBean;
	}
	public static <T extends AbstractService>T getService(String serviceName) throws InstantiationException, IllegalAccessException{
		if(SERVICEBEAN_MAPPING.get(serviceName) == null){
			Class<T> clz = (Class<T>)SERVICE_MAPPING.get(serviceName);
			if(clz == null)
				return null;
			SERVICEBEAN_MAPPING.put(serviceName, clz.newInstance());
		}
		return (T)SERVICEBEAN_MAPPING.get(serviceName);
	}
	
	public static Method getServiceMethod(String serviceName,String methodName,Class<? extends RequestContext> contextClass,String appVersion) throws InstantiationException, IllegalAccessException{
		String key = serviceName+"."+methodName+"_V"+appVersion;//一定更要加上版本号
		Method method = methodMapping.get(key);
		if(method == null){
			AbstractService service = getService(serviceName,appVersion);
			if(service != null){
				try {
					method = service.getClass().getMethod(methodName, contextClass);
				} catch (Exception e) {
					logger.error(e,e);
				}
				methodMapping.put(key, method);
			}
		}
		return method;
	}
	
}
