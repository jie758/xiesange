package com.xiesange.baseweb.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.RequestContext;
import com.xiesange.baseweb.ServiceManager;
import com.xiesange.baseweb.define.BaseErrorDefine;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.core.util.LogUtil;


public class ServiceProxy {
	protected Logger logger = LogUtil.getLogger(this.getClass());
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Object callService(AbstractController controller,String serviceName,String methodName,RequestContext context) throws Throwable{
		//获取到servicebean
		String appVersion = context.getRequestHeader().getApp_version();
		AbstractService service = ServiceManager.getService(serviceName,appVersion);
		if(service == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_SERVICE_NOT_FOUND);
		}
		//获取到method对象
		Method method = ServiceManager.getServiceMethod(serviceName,methodName,context.getClass(),appVersion);
		if(method == null){
			throw ETUtil.buildException(BaseErrorDefine.SYS_METHOD_NOT_FOUND);
		}
		//反射执行method
		try{
			//logger.debug("......call service : "+service.getClass().getName());
			return method.invoke(service, context);
		}catch(InvocationTargetException e){
			logger.error("Error while calling : "+serviceName+"."+methodName);
			throw ((InvocationTargetException)e).getTargetException();//把原异常抛出，否则不会回滚事务，不知道为何
		}
	}
	
}
