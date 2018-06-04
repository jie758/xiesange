package com.xiesange.core.enumdefine;
/**
 * 通用的参数持有对象，该对象可以持有多个参数
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;

public class KeyValueHolder {
	private List<KeyValueEntry> params;
	public KeyValueHolder(){
		params = new ArrayList<KeyValueEntry>();
	}
	
	public KeyValueHolder(Map<String,String> paramMap){
		params = new ArrayList<KeyValueEntry>();
		Iterator<Entry<String,String>> it = paramMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,String> entry = it.next();
			addParam(entry.getKey(),entry.getValue());
		}
	}
	
	public KeyValueHolder(String key,String value){
		params = new ArrayList<KeyValueEntry>();
		addParam(key,value);
	}
	
	public KeyValueHolder addParam(String key,String value){
		params.add(new KeyValueEntry(key,value));
		return this;
	}
	public KeyValueHolder addParam(String key,Date val) throws Exception{
		params.add(new KeyValueEntry(key,DateUtil.date2Str(val, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS)));
		return this;
	}
	public KeyValueHolder addParam(String key,Long val) throws Exception{
		params.add(new KeyValueEntry(key,String.valueOf(val)));
		return this;
	}
	public KeyValueHolder addParam(String key,Integer val) throws Exception{
		params.add(new KeyValueEntry(key,String.valueOf(val)));
		return this;
	}
	public List<KeyValueEntry> getParams(){
		return params;
	}
	
	public String getParamValue(String key){
		if(NullUtil.isEmpty(params))
			return null;
		for(KeyValueEntry entry : params){
			if(entry.getCode().equals(key))
				return entry.getValue();
		}
		return null;
	}
	
	public void clear(){
		if(params != null){
			params.clear();
		}
	}
}
