package com.xiesange.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class JsonUtil
{
	public static final SerializeConfig DEFAULT_JSON_CONFIG = new SerializeConfig();
	public static final SimpleDateFormatSerializer DATE_SERIALIZER = new SimpleDateFormatSerializer(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
		
	static{
    	DEFAULT_JSON_CONFIG.put(Date.class, DATE_SERIALIZER);
    	DEFAULT_JSON_CONFIG.put(java.sql.Date.class, DATE_SERIALIZER);
    }
	
	
	public static void main(String[] args)
    {
		
        // String bb = JsonUtil.obj2Json(detail1);
        /*
         * String aa = "[{\"XSize\":6,\"YSize\":2,\"XStart\":5,\"YStart\":1}]";//JsonUtil.obj2Json(list); System.out.println(aa);
         * System.out.println(bb); List result = JsonUtil.json2List(aa, SysPageConfigDetail.class); LogUtil.dump("xxx", result);
         */
    }
    private static Logger logger = LogUtil.getLogger(JsonUtil.class);
    //private static JsonConfig DEFAULT_JSON_CONFIG = new JsonConfig();
    
    //private static DateJsonValueProcessor DATE_PROCESSOR = new DateJsonValueProcessor();
    
    public static <T> T json2Obj(String str, Class<T> clz) throws Exception
    {
        if(NullUtil.isEmpty(str))
        	return null;
        //JSON.parseObject(input, clazz, config, featureValues, features)
        /*ParserConfig config = new ParserConfig();
        config.putDeserializer(type, deserializer);*/
    	return JSON.parseObject(str, clz);
    }
    public static <T>T json2Obj(JSONObject jsonObj,Class<T> entityClass){
		if(jsonObj == null)
			return null;
		if(Map.class.isAssignableFrom(entityClass)){
			Map map = null;
			try {
				map = (Map)ClassUtil.instance(entityClass);
				map.putAll(jsonObj);
			} catch (Exception e) {
				logger.error(e,e);
			}
			return (T)map;
		}else{
			return JSON.parseObject(jsonObj.toJSONString(), entityClass);
		}
		
	}
    
    
    
    public static <T> List<T> json2List(String str, Class<T> componentClass) throws Exception
    {
        return JSON.parseArray(str, componentClass);
    }
    public static <T>List<T> json2List(JSONArray jsonList,Class<T> componentClass){
    	if(jsonList == null)
			return null;
		List<T> result = new ArrayList<T>();
		for(int i=0;i<jsonList.size();i++){
			Object item = jsonList.get(i);
			if(item instanceof JSONObject){
				result.add(json2Obj((JSONObject)item,componentClass));
			}else{
				result.add((T)JSON.toJSON(item));
			}
			//result.add(JSON.toJavaObject(jsonList.getJSONObject(i), componentClass));
		}
		return result;
	}

    
    public static <K,V>Map<K,V> json2Map(String str) throws Exception
    {
        if(NullUtil.isEmpty(str))
        	return null;
    	return JSON.parseObject(str, Map.class);
    }
    
    
    public static String obj2Json(Object obj){
		if(obj == null)
			return null;
		return JSON.toJSONString(obj,DEFAULT_JSON_CONFIG);
	}
    
    public static String obj2Json(Object obj,SerializeConfig CONFIG){
		if(obj == null)
			return null;
		CONFIG.put(Date.class, DATE_SERIALIZER);
		CONFIG.put(java.sql.Date.class, DATE_SERIALIZER);
		return JSON.toJSONString(obj,CONFIG);
	}
    
}
