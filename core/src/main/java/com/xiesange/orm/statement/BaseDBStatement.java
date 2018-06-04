package com.xiesange.orm.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.SpringUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBErrorException;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.SqlTracer;
import com.xiesange.orm.pojo.SQLBindParameter;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 执行DB操作的statement对象基类，用于变量绑定
 * 
 * @Description
 * @author wuyj
 * @Date 2013-10-23
 */
public abstract class BaseDBStatement
{
    protected static Logger logger = LogUtil.getLogger(BaseDBStatement.class);
    
    protected String dbTableName;//如果指定了操作表名就用这表名，否则就用默认的entityClass去构建表名
    protected Class<? extends DBEntity> entityClass;
    protected SqlTracer tracer = new SqlTracer();
    
    private StringBuffer sql = new StringBuffer(64);//完整的sql，其中变量用占位符？代替
    private List<SQLBindParameter> bindParameters = new ArrayList<SQLBindParameter>();//sql中绑定变量的参数值,也就是？对应的值
    
    
    public BaseDBStatement(Class<? extends DBEntity> entityClass){
        this(entityClass,null);
    }
    public BaseDBStatement(Class<? extends DBEntity> entityClass,String dbTableName){
    	this.dbTableName = dbTableName;
        this.entityClass = entityClass;
    }
    
    public BaseDBStatement appendSQL(String sqlStr){
    	sql.append(sqlStr);
        return this;
        
    }
    public BaseDBStatement appendBindSQL(String sqlStr,BaseJField jfield,Object value){
    	appendSQL(sqlStr);
    	addBindParameter(jfield,value);
        return this;
        
    }
    
    public void addBindParameter(BaseJField jfield,Object value){
    	addBindParameter(new SQLBindParameter(jfield,value));
    }
    public void addBindParameter(SQLBindParameter bindParam){
    	bindParameters.add(bindParam);
    }
    public void addBindParameters(List<SQLBindParameter> bindParamList){
    	bindParameters.addAll(bindParamList);
    }
    
    public Class<? extends DBEntity> getEntityClass(){
    	return entityClass;
    }
    
    public String getDbTableName() {
		return dbTableName;
	}
	public void setDbTableName(String dbTableName) {
		this.dbTableName = dbTableName;
	}
	public String getSQL(){
    	return sql.toString();
    }
    public List<SQLBindParameter> getBindParameters(){
    	return bindParameters;
    }
    public Object[] getBindParameterValues(){
    	int size = bindParameters.size();
    	Object[] result = new Object[size];
    	for(int i=0;i<size;i++){
    		result[i] = this.getBindParameters().get(i).getValue();
		}
    	return result;
    }
    /**
     * 对当前dbstatement进行解析，主要创建好预购的SQL语句对象，把对应的绑定变量也都创建好
     * @author wuyujie Sep 3, 2014 9:01:54 PM
     */
    public abstract void parse() throws Exception;
    /**
     * 访问数据库，对sql进行变量绑定设置以及真实访问数据库
     * @author wuyj 2013-10-25
     * @param prepare
     * @return
     * @throws Exception
     */
    public abstract Object access(PreparedStatement ps) throws Exception;
    /**
     * 把访问结果包装成实体进行返回
     * @author wuyj 2013-10-25
     * @param accessResult
     * @return
     * @throws Exception
     */
    public abstract Object wrap(Object accessResult) throws Exception;
    
    /**
     * 进行数据库访问记录数的统计。
     * 如果是查询，则返回查询到的记录数；如果是更新则返回更新到的记录数；如果是删除则返回删除的记录数
     * @author wuyujie Dec 28, 2014 7:14:06 PM
     * @param wrapResult
     * @return
     */
    public abstract long count(Object wrapResult);
    
    public Object execute() throws Exception{
    	final SqlTracer tracer = new SqlTracer();
        final boolean isBatch = this instanceof IBatchStatement;
		try{
        	tracer.start("parse");//解析执行语句对象，构建好sql和绑定的变量
        	this.parse();
            tracer.setSql(DBHelper.buildBindSQLWithIndex(this.getSQL()));
            tracer.setBindParametersString(
            		isBatch ? 
            		DBHelper.buildBatchBindParametersString(this.getBindParameters(), ((IBatchStatement)this).getBatchCount()) :
            		DBHelper.buildBindParametersString(this.getBindParameters()));
            
            tracer.start("access");//访问数据库：1、创建获取connection并创建PreparedStatement，2、执行数据库SQL访问操作
            final BaseDBStatement baseST = this;					
            Object wrapResult = SpringUtil.getJdbcTemplateBean().execute(new PreparedStatementCreator(){
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						//System.out.println("connection@"+connection.hashCode());
						return isBatch ? 
	        					DBHelper.createBatchPreparedStatement(connection,baseST) : 
	        					DBHelper.createPreparedStatement(connection,baseST);
					}
		    		
		    	},new PreparedStatementCallback<Object>(){
		    		@Override
					public Object doInPreparedStatement(PreparedStatement statement) throws SQLException, DataAccessException {
						
						Object accessResult;
						try {
							accessResult = baseST.access(statement);
							
							tracer.start("wrap");//把操作的结构进行包装，如果是查询等操作，需要把原生返回的ResultSet结构包装秤ORM的数据库实体类返回
							Object wrapResult = baseST.wrap(accessResult);
							return wrapResult;
						} catch (Exception e) {
							throw new SQLException(e);
						}
					}	
	    	});					
            
            long count = this.count(wrapResult);//wrapResult == null ? 0 : wrapResult instanceof List ? ((List<?>)wrapResult).size() : wrapResult instanceof DBEntity ? 1 : (Integer)wrapResult ;
            logger.debug("[ORM] - "+tracer.finish(count));
            return wrapResult;
        }catch(Exception e){
        	String sql = tracer.finish(-1);
            try{
                logger.debug("[ORM] - "+sql);
            }catch(Exception e1){
            }
            //logger.error(e,e);
            throw new DBErrorException(e,sql);
        }finally{
            tracer.clear();
        }
    }
    
}
