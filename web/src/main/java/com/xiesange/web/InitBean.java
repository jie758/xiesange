package com.xiesange.web;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.parser.ParserConfig;
import com.xiesange.baseweb.ServiceManager;
import com.xiesange.baseweb.cache.CacheManager;
import com.xiesange.baseweb.config.ConfigManager;
import com.xiesange.baseweb.exception.ErrorManager;
import com.xiesange.baseweb.json.BaseNodeSerializer;
import com.xiesange.baseweb.json.DBEntitySerializer;
import com.xiesange.baseweb.json.ETDateDeserializer;
import com.xiesange.baseweb.notify.TaskManager;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.FileUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.PackageUtil;
import com.xiesange.core.util.SpringUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.sequence.ISequenceManager;

public class InitBean {
	private static final long serialVersionUID = 1L;
    private static Logger logger = LogUtil.getLogger(InitBean.class);
    public static final String PATH_CONFIG = "/config.xml";
    public static final String PATH_ERROR_CODE = "/error_code.properties";
    public static final String PATH_CACHE = "/cache.xml";
    public static final String PATH_NOTIFY = "/notify.xml";
    public static final String DB_CONFIG_KEY = "xsgconfig";
    
	public void init() throws ServletException {
    	logger.info("......begin to init");
    	logger.info("......user.dir="+System.getProperty("user.home"));
    	logger.info("......db_config_path="+System.getProperty(DB_CONFIG_KEY));
    	try
        {
    		initJsonsSerializer();
    		RequestUtil.initUrls();
    		initService();
    		
    		loadConfigFile();
    		
    		//数据库相关
    		initSequence();
    		ConfigManager.init(PATH_CONFIG);
        	CacheManager.init(PATH_CACHE);
        	ErrorManager.init(PATH_ERROR_CODE);
        	TaskManager.init();
        	//initRSA();
        	
        	logger.info("......success to init");
        }
        catch (Exception e)
        {
            logger.error(e, e);
            throw new ServletException(e);
        }
    }
    
    private void initJsonsSerializer(){
    	//设置json序列化
    	JsonUtil.DEFAULT_JSON_CONFIG.put(DBEntity.class, new DBEntitySerializer());
    	JsonUtil.DEFAULT_JSON_CONFIG.put(BaseNode.class, new BaseNodeSerializer());
    	
    	ETDateDeserializer dateDeserializer = new ETDateDeserializer();
		ParserConfig.getGlobalInstance().putDeserializer(Date.class, dateDeserializer);
		
    }
    
    private Properties loadConfigFile() throws Exception{
    	String configFile = System.getProperty(DB_CONFIG_KEY);
    	logger.info("......begin to load db config : "+configFile);
    	if(NullUtil.isEmpty(configFile))
    		return null;
		Properties p = FileUtil.loadProperties(configFile);
		
		Iterator<Entry<Object,Object>> it = p.entrySet().iterator();
    	String key = null;
    	while(it.hasNext()){
    		Entry<Object,Object> entry = it.next();
    		key = (String)entry.getKey();
    		logger.info("......[config] "+key+" = "+entry.getValue());
    	}
		
		return p;
    }
    
    private void initSequence() throws Exception{
    	((ISequenceManager)SpringUtil.getBeanByName("sequenceManager")).init(10000000L);
    }
    
    
    private void initService() throws Exception{
    	List<Class<?>> service_list = PackageUtil.getClassListByAnnotation("com.xiesange.web.service", ETServiceAnno.class);
    	if(NullUtil.isEmpty(service_list)){
    		return;
    	}
    	for(Class clz : service_list){
    		ETServiceAnno serviceAnno = (ETServiceAnno)clz.getAnnotation(ETServiceAnno.class);
    		String version = serviceAnno.version();
    		if(NullUtil.isEmpty(version)){
    			ServiceManager.registService(serviceAnno.name(),null, clz);
    			logger.info("......[service] "+serviceAnno.name()+" = "+clz.getName());
    		}else{
    			String[] vers = version.split(";");
    			for(String v : vers){
    				ServiceManager.registService(serviceAnno.name(),v, clz);
    			}
    			logger.info("......[service] "+serviceAnno.name()+"[V"+version+"] = "+clz.getName());
    		}
    		
    	}
    }
    
    /*protected void initRSA() throws Exception{
    	EncryptUtil.RSA.init(
    			SysparamCmp.get(SysParamDefine.RSA_PUBLIC_KEY_PATH), 
    			SysparamCmp.get(SysParamDefine.RSA_PRIVATE_KEY_PATH));
    }*/
}
