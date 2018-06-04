package com.xiesange.orm.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.pojo.JoinQueryData;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.JoinPart;
import com.xiesange.orm.statement.field.OrderField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;

public interface IDao{
	//query系列
	/*public <T extends DBEntity> List<T> queryAll(QueryStatement statement) throws Exception;*/
	public <T extends DBEntity> List<T> queryAll(Class<T> dbClass,BaseJField...jfs) throws Exception;
	
	public <T extends DBEntity> List<T> query(QueryStatement statement) throws Exception;
	public <T extends DBEntity> List<T> query(Class<T> dbClass,DBCondition... conditions) throws Exception;
	public <T extends DBEntity> List<T> query(Class<T> dbClass,OrderField[] orderFields,DBCondition... conditions) throws Exception;
	public <T extends DBEntity> List<T> queryByIds(Class<T> dbClass, Collection<Long> ids,BaseJField...jfs) throws Exception;
	
	public <T extends DBEntity>T queryById(Class<T> dbClass, long id,BaseJField...jfs) throws Exception;
	public <T extends DBEntity>T querySingle(Class<T> dbClass,DBCondition...conditions) throws Exception;
	public <T extends DBEntity>T querySingle(Class<T> dbClass,BaseJField jfield,Object value) throws Exception;
		
	public <T extends DBEntity>List<BigDecimal> queryStat(Class<T> dbClass,IStatQueryField[] statFields,DBCondition...conditions) throws Exception;
	public <T extends DBEntity>List<BigDecimal> queryJoinStat(Class<T> dbClass,IStatQueryField[] statFields,JoinPart[] joinParts,DBCondition...conditions) throws Exception;
	
	public <T extends DBEntity>long queryCount(Class<T> dbClass,DBCondition...conditions) throws Exception;
	public List<JoinQueryData> queryJoin(QueryStatement statement) throws Exception;
	
	//insert系列
	public <T extends DBEntity> int insert(T entity) throws Exception;
	//public <T extends DBEntity> int insert(T entity,String tableName) throws Exception;
	public <T extends DBEntity> int insertBatch(List<T> entityList) throws Exception;
	public <T extends DBEntity> int insertBatch(List<T> entityList,String tableName) throws Exception;
	
	//update系列
	public <T extends DBEntity> int updateAll(T valueEntity) throws Exception;
	public <T extends DBEntity> int updateById(T valueEntity, long id) throws Exception;
	public <T extends DBEntity> int updateById(FieldUpdateExpression[] values, long id) throws Exception;
	public <T extends DBEntity> int updateByIds(T valueEntity, Collection<Long> ids) throws Exception;
	
	public <T extends DBEntity> int update(T valueEntity,DBCondition... conditions) throws Exception;
	public <T extends DBEntity> int update(FieldUpdateExpression[] values,DBCondition... conditions) throws Exception;
	
	public <T extends DBEntity> int updateBatch(UpdateStatement... statements) throws Exception;
	
	//delete系列
	public <T extends DBEntity> int deleteAll(Class<T> dbClass) throws Exception;
	public <T extends DBEntity> int deleteById(Class<T> dbClass, long id) throws Exception;
	public <T extends DBEntity> int deleteByIds(Class<T> dbClass, Collection<Long> ids) throws Exception;
	public <T extends DBEntity> int delete(Class<T> dbClass,DBCondition... conditions) throws Exception;
	
	//sequence系列
	public long getSequence(Class<? extends DBEntity> clz) throws Exception;
	public long[] getSequence(Class<? extends DBEntity> clz,int count) throws Exception;
	public long getSequence(String key) throws Exception;
	public long[] getSequence(String key,int count) throws Exception;
	
	
	
}
