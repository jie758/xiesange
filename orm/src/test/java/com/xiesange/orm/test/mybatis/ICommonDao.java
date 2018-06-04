package com.xiesange.orm.test.mybatis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;

public interface ICommonDao {
    public List<HashMap<String,Object>> query(Map params);
	
	//query系列
	public <T extends DBEntity> List<T> queryAll(QueryStatement statement) throws Exception;
	public <T extends DBEntity> List<T> queryAll(Class<T> dbClass) throws Exception;
	
	public <T extends DBEntity> List<T> query(QueryStatement statement) throws Exception;
	public <T extends DBEntity> List<T> query(Class<T> dbClass,DBCondition... conditions) throws Exception;
	public <T extends DBEntity>T queryById(Class<T> dbClass, long id) throws Exception;
	public <T extends DBEntity>T querySingle(Class<T> dbClass,DBCondition...conditions) throws Exception;
	
	public <T extends DBEntity>List<BigDecimal> queryStat(Class<T> dbClass,IStatQueryField[] statFields,DBCondition...conditions) throws Exception;
	public <T extends DBEntity>long queryCount(Class<T> dbClass,DBCondition...conditions) throws Exception;
	
	//insert系列
	public <T extends DBEntity> int insert(T entity) throws Exception;
	public <T extends DBEntity> int insert(T entity,String tableName) throws Exception;
	public <T extends DBEntity> int insertBatch(List<T> entityList) throws Exception;
	public <T extends DBEntity> int insertBatch(List<T> entityList,String tableName) throws Exception;
	
	//update系列
	public <T extends DBEntity> int updateAll(T valueEntity) throws Exception;
	public <T extends DBEntity> int updateById(T valueEntity, long id) throws Exception;
	public <T extends DBEntity> int update(T valueEntity,DBCondition... conditions) throws Exception;
	public <T extends DBEntity> int updateBatch(UpdateStatement... statements) throws Exception;
	
	//delete系列
	public <T extends DBEntity> int deleteAll(Class<T> dbClass) throws Exception;
	public <T extends DBEntity> int deleteById(Class<T> dbClass, long id) throws Exception;
	public <T extends DBEntity> int delete(Class<T> dbClass,DBCondition... conditions) throws Exception;
		
}
