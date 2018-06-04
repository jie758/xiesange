package com.elsetravel.mis;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.parser.ParserConfig;
import com.elsetravel.baseweb.LangResourceHolder;
import com.elsetravel.baseweb.ServiceManager;
import com.elsetravel.baseweb.cache.CacheManager;
import com.elsetravel.baseweb.config.ConfigManager;
import com.elsetravel.baseweb.json.BaseNodeSerializer;
import com.elsetravel.baseweb.json.DBEntitySerializer;
import com.elsetravel.baseweb.json.ETDateDeserializer;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.FileUtil;
import com.elsetravel.core.util.JsonUtil;
import com.elsetravel.core.util.LogUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.PackageUtil;
import com.elsetravel.core.util.SpringUtil;
import com.elsetravel.core.xml.BaseNode;
import com.elsetravel.orm.DBEntity;
import com.elsetravel.orm.sequence.ISequenceManager;
public class InitBean{
    private static final long serialVersionUID = 1L;
    private static Logger logger = LogUtil.getLogger(InitBean.class);
    public static final String PATH_CONFIG = "/config.xml";
    public static final String PATH_CACHE = "/cache.xml";
    public static final String PATH_BOOT = "/boot.properties";
    
    public static void main(String[] args) {
    	List<Class<?>> clzList = PackageUtil.getClassListByAnnotation("com.elsetravel.mis", ETServiceAnno.class);
    	int a = 0;
	}
    
    public void init() throws ServletException {
    	logger.info("......begin to init");
    	logger.info("......user.dir="+System.getProperty("user.home"));
    	try
        {
    		initJsonsSerializer();
    		RequestUtil.initUrls();
    		initService();
    		initLang();
    		
    		
    		Properties configProp = loadConfigFile();
    		initSequence(configProp);
    		ConfigManager.init(PATH_CONFIG);
        	CacheManager.init(PATH_CACHE);
        	
        	logger.info("......success to init");
        }
        catch (Exception e)
        {
            logger.error(e, e);
            throw new ServletException(e);
        }
    }
    
    
    private void initLang() throws Exception{
    	URL langUrl = this.getClass().getResource("/lang");
    	File[] langFiles = new File(langUrl.getPath()).listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".properties");
			}
    	});
    	if(NullUtil.isEmpty(langFiles))
    		return;
    	String name = null;
    	for(File langFile : langFiles){
    		name = langFile.getName().split("\\.")[0];
    		Properties langProp = FileUtil.loadProperties(new FileInputStream(langFile));
        	LangResourceHolder.addResource(name, langProp);
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
    	String configFile = System.getProperty("etconfig_mis");
    	logger.info("......begin to load et_conf : "+configFile);
		Properties p = FileUtil.loadProperties(configFile);
		return p;
    }
    
    private void initSequence(Properties configFile) throws Exception{
    	/*Iterator<Entry<Object,Object>> it = configFile.entrySet().iterator();
    	String key = null;
    	while(it.hasNext()){
    		Entry<Object,Object> entry = it.next();
    		key = (String)entry.getKey();
    		logger.info("......[config] "+key+" = "+entry.getValue());
    	}*/
    	((ISequenceManager)SpringUtil.getBeanByName("sequenceManager")).init(20000000L);
    }
    
    
    private void initService() throws Exception{
    	List<Class<?>> service_list = PackageUtil.getClassListByAnnotation("com.elsetravel.mis.service", ETServiceAnno.class);
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
    			String[] vers = version.split(",");
    			for(String v : vers){
    				ServiceManager.registService(serviceAnno.name(),v, clz);
    			}
    			logger.info("......[service] "+serviceAnno.name()+"[V"+version+"] = "+clz.getName());
    		}
    		
    	}
    }
}
