package com.xiesange.baseweb.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xiesange.core.util.LogUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class CacheManager {
	private static final Logger logger = LogUtil.getLogger(CacheManager.class);
	private static Map<Class<?>,CacheHouse> cacheData = new HashMap<Class<?>,CacheHouse>();
	private static Map<Class,List<DBCondition>> cacheConditionMap = new HashMap<Class,List<DBCondition>>();
	
	public static void init(String filePath) throws Exception {
		XStreamHolder holder = new XStreamHolder("root", BaseNode.class);
		holder.registerConverter(new UniversalXmlConverter(BaseNode.class));

		BaseNode root = (BaseNode) holder.parseFromStream(CacheManager.class.getResourceAsStream(filePath));
		List<BaseNode> configNodeList = root.getChildren();
		String tableName;
		Class<DBEntity> entityClass;
		for (int i = 0; i < configNodeList.size(); i++) {
			BaseNode configNode = configNodeList.get(i);
			tableName = configNode.getAttribute("table");
			String condition = configNode.getText();
			entityClass = (Class<DBEntity>)Class.forName(tableName);
			
			loadCache(entityClass);
		}
	}
	
	public static boolean isCached(Class<? extends DBEntity> entityClass){
		return cacheData.containsKey(entityClass);
	}
	private static <T extends DBEntity>void loadCache(Class<T> entityClass) throws Exception{
		List<T> list = DBHelper.getDao().queryAll(entityClass);
		CacheHouse<T> house = new CacheHouse<T>(entityClass,list);
		cacheData.put(entityClass, house);
		//LogUtil.dump(entityClass.getSimpleName(), house.getAll());
	}
	
	public static <T extends DBEntity>void refreshCache(Class<T> entityClass) throws Exception{
		if(cacheData.get(entityClass) == null)
			return;
		logger.debug("....begin to refreshCache : "+entityClass.getSimpleName());
		List<T> alllist = cacheData.get(entityClass).getAll();
		if(alllist != null)
			alllist.clear();
		loadCache(entityClass);
		
		logger.debug("....finish to refreshCache");
	}
	
	
	public static <T extends DBEntity>CacheHouse<T> getCacheHouse(Class<T> entityClass){
		return cacheData.get(entityClass);
	}
}
