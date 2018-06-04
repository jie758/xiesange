package com.xiesange.baseweb.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.xiesange.core.enumdefine.IParamEnum;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.field.BaseJField;

/**
 * 请求对象
 * @Description
 * @author wuyj
 * @Date 2012-6-30
 */
public class RequestBody extends HashMap<String,Object>
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String getString(IParamEnum param){
        Object val = this.get(param.name());
        if(val == null)
            return null;
        try{
            return (String)val;
        }catch(Exception e){
            return String.valueOf(val);
        }
    }
    
    public Integer getInt(IParamEnum param){
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
        return Integer.valueOf(val.toString());
    }
    public Integer getInt(IParamEnum param,int defaultValue){
        Integer value = getInt(param);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    
    public Boolean getBool(IParamEnum param){
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        if(String.valueOf(val).equals("0"))
        	return false;
        else if(String.valueOf(val).equals("1")){
        	return true;
        }else{
        	return Boolean.valueOf(val.toString());
        }
    }
    public Boolean getBool(IParamEnum param,boolean defaultValue){
        Boolean value = getBool(param);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    public Long getLong(IParamEnum param){
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
        return Long.valueOf(val.toString());
    }
    public Long getLong(IParamEnum param,long defaultValue){
        Long value = getLong(param);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    public Short getShort(IParamEnum param){
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
        return Short.valueOf(val.toString());
    }
    public short getShort(IParamEnum param,short defaultValue){
    	Short value = getShort(param);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    public Float getFloat(IParamEnum param){
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
        return Float.valueOf(val.toString());
    }
    public float getFloat(IParamEnum param,float defaultValue){
    	Float value = getFloat(param);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    public Date getDateTime(IParamEnum param) throws Exception{
		return getDateTime(param,DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
    }
    public Date getDateTime(IParamEnum param,String format) throws Exception{
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
		return DateUtil.str2Date((String)val,format);
    }
    public Date getDateTime(IParamEnum param,String format,Date defaultValue) throws Exception{
        Date value = getDateTime(param,format);
        if(value == null)
            value = defaultValue;
        return value;
    }
    public Date getDate(IParamEnum param) throws Exception{
		return getDate(param,DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
    }
    public Date getDate(IParamEnum param,String format) throws Exception{
    	Object val = this.get(param.name());
        if(val == null)
            return null;
        if(val instanceof String && ((String)val).length() == 0)
        	return null;
        
		return DateUtil.str2Date((String)val,format);
    }
    public Date getDate(IParamEnum param,String format,Date defaultValue) throws Exception{
        Date value = getDate(param,format);
        if(value == null)
            value = defaultValue;
        return value;
    }
    
    public <T>Map<String,T> getMap(IParamEnum param,Class<T> valueClass) throws Exception{
        String value = this.getString(param);
        if(value == null)
        	return null;
        return JsonUtil.json2Map(value);
    }
    
    public <T>T getObject(IParamEnum param,Class<T> clz) throws Exception{
        String str = this.getString(param);
        return JsonUtil.json2Obj(str, clz);
    }
    public <T>List<T> getList(IParamEnum param,Class<T> componentClass) throws Exception{
    	Object val = this.get(param.name());
    	if(val == null)
            return null;
    	if(val instanceof JSONArray){
    		return JsonUtil.json2List(((JSONArray)val).toJSONString(), componentClass);
    	}else{
    		String str = (String)val;
    		return NullUtil.isEmpty(str) ? null : JsonUtil.json2List(str, componentClass);
    	}
    }
    
    /**
     * 反序列化成数据库实体。因为前端过来的参数都是有下划线的，而实体都是驼峰式命名，所以这个方法可以根据实体中数据库字段名称和参数命名进行匹配，
     * 从而进行对应字段的设值
     * @param param
     * @param componentClass
     * @return
     * @throws Exception
     * @author Wilson Wu
     * @date 2015年9月21日
     */
    public <T extends DBEntity>List<T> getDBEntityList(IParamEnum param,Class<T> componentClass) throws Exception{
    	Object val = this.get(param.name());
    	if(val == null)
            return null;
    	List<Map> mapList = new ArrayList<Map>();
    	if(val instanceof JSONArray){
    		mapList = JsonUtil.json2List(((JSONArray)val).toJSONString(), Map.class);
    	}else{
    		String str = (String)val;
    		mapList = NullUtil.isEmpty(str) ? null : JsonUtil.json2List(str, Map.class);
    	}
    	BaseJField jf = null;
    	Object jfval = null;
    	List<T> entityList = new ArrayList<T>();
    	for(Map entityMap : mapList){
    		Iterator<Entry<String,String>> it = entityMap.entrySet().iterator();
    		T newEntity = componentClass.newInstance();
    		while(it.hasNext()){
    			Entry<String,String> entry = it.next();
    			jf = DBHelper.getJFieldByColName(componentClass, entry.getKey());
    			if(jf == null){
    				newEntity.addAttribute(entry.getKey(), entry.getValue());
    			}else{
    				jfval = CommonUtil.convert(entry.getValue(),jf.getJavaType());
    				DBHelper.setEntityValue(newEntity, jf, jfval);
    			}
    			
    			
    		}
    		entityList.add(newEntity);
    	}
    	
    	return entityList;
    }
}
