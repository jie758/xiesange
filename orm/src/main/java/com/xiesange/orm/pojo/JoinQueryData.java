package com.xiesange.orm.pojo;

import java.util.HashMap;
import java.util.Map;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;

public class JoinQueryData {
	private Map<Class<? extends DBEntity>,DBEntity> result;
	
	public void addResult(Class<? extends DBEntity> clz,DBEntity entity){
		if(result == null){
			result = new HashMap<Class<? extends DBEntity>,DBEntity>();
		}
		result.put(clz, entity);
	}
	
	public <T extends DBEntity> T getResult(Class<T> clz){
		if(NullUtil.isEmpty(result)){
			return null;
		}
		return (T)result.get(clz);
	}
}
