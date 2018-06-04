package com.xiesange.baseweb.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.BaseJField;

/**
 * 用户存放数据库缓存数据的容器
 * @author wuyujie Jan 9, 2015 12:19:02 PM
 *
 */
public class CacheHouse<T extends DBEntity> {
	private static final Logger logger = LogUtil.getLogger(CacheHouse.class);
	private Class<T> entityClass;
	private List<T> cacheList = new ArrayList<T>();
	//private boolean isGroupByTenant;//是否按照tenantId归组
	
	public CacheHouse(Class<T> entityClass,List<T> dataList) throws Exception{
		this.entityClass = entityClass;
		//isGroupByTenant = entityClass != SysTenant.class && DBHelper.getTenantJField(this.entityClass, false) != null;
		this.cacheList = dataList;
		//tenantMap = buildTenantCache(this.entityClass,dataList);
		
	}
	public List<T> getAll(BaseJField...jfs){
		log(NullUtil.isEmpty(cacheList)?0:cacheList.size());
		return cacheList;
	}
	public T getById(long id){
		return getById(id,null);
	}
	public T getById(long id,BaseJField...jfs){
		List<T> list = getAll(jfs);
		if(NullUtil.isEmpty(list)){
			return null;
		}
		T result = null;
		for(T entity : list){
			if(entity.getId() == id){
				result = entity;
				break;
			}
		}
		log(result==null?0:1);
		return result;
	};
	public List<T> getList(DBCondition...conditions) throws Exception{
		return getList(null,conditions);
	};
	
	public List<T> getList(BaseJField[] jfs,DBCondition...conditions) throws Exception{
		List<T> result = getByCondition(false,jfs,conditions);
		log(NullUtil.isEmpty(result)?0:result.size());
		
		return result;
	};
	
	public T getSingle(DBCondition...conditions) throws Exception{
		return getSingle(null,conditions);
	}
	public T getSingle(BaseJField[] jfs,DBCondition...conditions) throws Exception{
		List<T> result =  getByCondition(true,jfs,conditions);
		
		log(NullUtil.isEmpty(result)?0:result.size());
		
		return NullUtil.isEmpty(result) ? null : result.get(0);
	};
	
	
	
	
	private void log(long count){
		//logger.debug("___[cache:"+entityClass.getSimpleName()+"]getList");
		//logger.debug("___[cache:"+entityClass.getSimpleName()+"]getList matched : "+count);
	}
	
	/**
	 * 内置核心方法，根据条件获取到匹配的缓存数据
	 * @param isSingle
	 * @param conditions
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:36:38
	 */
	private List<T> getByCondition(boolean isSingle,BaseJField[] jfs,DBCondition...conditions) throws Exception{
		List<T> list = getAll();;
		if(NullUtil.isEmpty(list)){
			return null;
		}
		List<T> result = new ArrayList<T>();
		for(T entity : list){
			if(isMatched(entity,conditions)){
				result.add((T)entity.clone(jfs));
				if(isSingle){
					break;
				}
			}
		}
		return result.size() == 0 ? null : result;
	};
	
	
	
	/**
	 * 按tenantId对加载出来的数据列表进行分组，返回一个map，key=tenantId，value=该tenantId的数据列表
	 * @author wuyujie Jan 9, 2015 12:43:23 PM
	 * @param list
 	 * @return
	 * @throws Exception
	 */
	/*private static <T extends DBEntity>Map<Long,List<T>> buildTenantCache(Class<T> entityClass,List<T> list) throws Exception{
		Map<Long,List<T>> tenantMap = new HashMap<Long,List<T>>();
		BaseJField tenantJF = null;//DBHelper.getTenantJField(entityClass, false);
		if(tenantJF == null){
			tenantMap.put(-1L, list);//如果当前实体中没有tenantId字段，那么就不分组了，直接用-1当做key
		}else if(NullUtil.isNotEmpty(list)){
			for(T entity : list){
				Long tenantId = (Long)DBHelper.getEntityValue(entity,tenantJF);
				if(tenantMap.get(tenantId) == null){
					tenantMap.put(tenantId, new ArrayList<T>());
				}
				tenantMap.get(tenantId).add(entity);
			}
		}
		return tenantMap;
	}*/
	
	private static <T extends DBEntity>boolean isMatched(T entity,DBCondition... conditions){
		if(NullUtil.isEmpty(conditions))
			return true;
		try{
			for(DBCondition condition : conditions){
				if(condition == null)
					continue;
				BaseJField jf = condition.getJField();
				Object value = condition.getValue();
				DBOperator operator = condition.getOperator();
				
				Object entityValue = DBHelper.getEntityValue(entity, jf);
				if(!canEqual(entityValue,value,operator)){
					return false;//一个条件不成立就不成立了
				}
			}
		}catch(Exception e){
			LogUtil.getLogger(CacheHouse.class).error(e, e);
			return false;
		}
		return true;
	};
	
	private static boolean canEqual(Object value1,Object value2,DBOperator operator) throws Exception{
		if(operator == null || operator == DBOperator.EQUALS){
			//=
			if(value1 == null && value2 == null){
				return true;
			}else if((value1 == null && value2 != null) || (value1 != null && value2 == null)){
				return false;
			}else{
				return value1.toString().equals(value2.toString());
			}
		}else if(operator == DBOperator.NOT_EQUALS){
			//!=
			if(value1 == null && value2 == null){
				return false;
			}else if((value1 == null && value2 != null) || (value1 != null && value2 == null)){
				return true;
			}else{
				return !value1.toString().equals(value2.toString());
			}
		}else if(operator == DBOperator.GREAT){
			//>,只能数字之间比较
			if(value1 == null || value2 == null){
				return false;//一个为null就不成立
			}else{
				return new BigDecimal(value1.toString()).compareTo(new BigDecimal(value2.toString())) > 0;
			}
		}else if(operator == DBOperator.GREAT_EQUALS){
			//>=,只能数字之间比较
			if(value1 == null || value2 == null){
				return false;//一个为null就不成立
			}else{
				return new BigDecimal(value1.toString()).compareTo(new BigDecimal(value2.toString())) >= 0;
			}
		}else if(operator == DBOperator.LESS){
			//>,只能数字之间比较
			if(value1 == null || value2 == null){
				return false;//一个为null就不成立
			}else{
				return new BigDecimal(value1.toString()).compareTo(new BigDecimal(value2.toString())) < 0;
			}
		}else if(operator == DBOperator.LESS_EQUALS){
			//>=,只能数字之间比较
			if(value1 == null || value2 == null){
				return false;//一个为null就不是不成立
			}else{
				return new BigDecimal(value1.toString()).compareTo(new BigDecimal(value2.toString())) <= 0;
			}
		}else if(operator == DBOperator.IN){
			//in,判断value1是否在value2集合中，注意value2必须是集合类型，可以是Array、List或者任意Collection
			Object[] arr = null;
			if(value2.getClass().isArray()){
			    arr = (Object[])value2;
			}else if(value2 instanceof Collection){
			    arr = ((Collection<?>)value2).toArray(new Object[((Collection<?>)value2).size()]);
			}
			return CommonUtil.isIn(value1, arr);
		}else{
			throw new Exception("无效的缓存运算符:"+operator.getExpression());
		}
	} 
	
	/*private static Long[] parseTenantIds(Object tenantIdValue){
		Long[] result = null;
		if(tenantIdValue.getClass().isArray()){
			result = (Long[])tenantIdValue;
		}else if(Collection.class.isAssignableFrom(tenantIdValue.getClass())){
			result = (Long[])((Collection)tenantIdValue).toArray(new Long[((Collection)tenantIdValue).size()]);
		}else{
			result = new Long[]{(Long)tenantIdValue};
		}
		return result;
	}*/
	

	/*public static void main(String[] args) throws Exception {
		SysTenant tenant = new SysTenant();
		tenant.setCode("huangtu");
		tenant.setName("皇图科技");
		tenant.setId(1001L);
		tenant.setType((short)1);
		
		SysTenant tenant2 = new SysTenant();
		tenant2.setCode("taobao");
		tenant2.setName("淘宝科技");
		tenant2.setId(1002L);
		tenant2.setType((short)2);
		
		boolean result1 = isMatched(tenant,new DBCondition(SysTenant.JField.code,"huangtu"),
							new DBCondition(SysTenant.JField.type,2,DBOperator.LESS));
		
		boolean result2 = isMatched(tenant,new DBCondition(SysTenant.JField.type,0,DBOperator.GREAT));
		boolean result3 = isMatched(tenant,new DBCondition(SysTenant.JField.type,2,DBOperator.GREAT));
		boolean result4 = isMatched(tenant,new DBCondition(SysTenant.JField.type,1,DBOperator.GREAT_EQUALS));
		
		Integer[] arr = new Integer[]{4,2,4};
		boolean result5 = isMatched(tenant,new DBCondition(SysTenant.JField.type,arr,DBOperator.IN));
		
		
		System.out.println(result1);
		System.out.println(result2);
		System.out.println(result3);
		System.out.println(result4);
		System.out.println(result5);
	}*/
}
