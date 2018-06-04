package com.xiesange.baseweb.json;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.DateFormatDeserializer;
import com.xiesange.core.util.DateUtil;
import com.xiesange.orm.pojo.NoTimeDate;
/**
 * 日期反序列化解析器。
 * 入果是DateNoTime类型字段，则按照yyyy-MM-dd格式创建DateNoTime；
 * 如果是数据库里是DateTime类型字段，则按照yyyy-MM-dd hh:mm:ss格式创建Date；
 * 
 * @author wuyujie Jan 20, 2015 5:33:28 PM
 *
 */
public class ETDateDeserializer extends DateFormatDeserializer{
    @Override  
    protected <Date> Date cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {  
        if (val instanceof String) {  
            String strVal = (String) val;  
            if (strVal.length() == 0) {  
                return null;  
            }  
            try{
            	java.util.Date date = null;
            	if(clazz == NoTimeDate.class){
            		date = DateUtil.str2Date(strVal, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
            		date = new NoTimeDate(date.getTime());
            	}else{
            		date = DateUtil.str2Date(strVal, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
            	}
            	return (Date)date;
            }catch (Exception e) {  
                throw new JSONException("parse error");  
            }  
        }  
        throw new JSONException("parse error");  
    }  
	

}
