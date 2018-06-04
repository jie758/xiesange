package com.xiesange.orm.statement.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.BaseDBStatement;
@Deprecated
public class CountQueryStatement extends BaseDBStatement
{
    private Long id;//主键值，queryById使用
    private DBCondition[] conditions;//查询条件，queryByCondition使用
    
    //以下是复杂查询所用到的参数
    private CountQueryStatement subQuery;//嵌套子查询
    private String subQueryAlias;//只有subQuery有值的情况下这个字段才有值，表示子查询的别名
    
    public <T extends DBEntity>CountQueryStatement(Class<T> entityClass,Long id){
    	//按照id查询
    	super(entityClass);
        this.id = id;
    }
    
    public <T extends DBEntity>CountQueryStatement(Class<T> entityClass,DBCondition...conditions){
    	//按照条件查询
    	super(entityClass);
        this.conditions = conditions;
    }
   
    
    //完整的查询，支持复杂的整表嵌套查询，即select * from (select * from table1 where xxxx) where yyyy
    public <T extends DBEntity>CountQueryStatement(CountQueryStatement queryTable,Long id){
    	super(queryTable.getEntityClass());
    	this.subQuery = queryTable;
    	this.id = id;
    }
    public <T extends DBEntity>CountQueryStatement(CountQueryStatement queryTable,DBCondition...conditions){
    	super(queryTable.getEntityClass());
    	this.subQuery = queryTable;
    	this.conditions = conditions;
    }
    
    @Override
    public void parse() throws Exception
    {
        if(NullUtil.isNotEmpty(this.getSQL())){
        	return;//已经parse过sql里会有值，就不重复解析了
        }
        String tableSQL = null;//this.queryST == ? DBHelper.getDBTableName(entityClass):null;
    	if(this.subQuery==null){
    		tableSQL = DBHelper.getDBTableName(entityClass);
    	}else{
    		this.subQuery.parse();
    		tableSQL = "("+this.subQuery.getSQL()+") A";
    		this.subQueryAlias = "A";
    		this.addBindParameters(this.subQuery.getBindParameters());
    	}
        this.appendSQL("SELECT COUNT(*) FROM ")
        	.appendSQL(tableSQL);
        
        //构建where条件
        if(id != null){
        	DBHelper.appendWhereSQL(this, id);
        }else if(NullUtil.isNotEmpty(conditions)){
        	DBHelper.appendWhereSQL(this, conditions);
        }
        
        
    }

    @Override
    public Object access(PreparedStatement ps) throws Exception
    {
        return ps.executeQuery();
    }


    @Override
    public Object wrap(Object accessResult) throws Exception
    {
    	ResultSet rs = (ResultSet)accessResult;
    	while(rs.next()){
    		return rs.getBigDecimal("COUNT(*)").intValue();
    	}
    	return 0;
    }

	@Override
	public long count(Object wrapResult) {
		return (Integer)wrapResult;
	}

	public String getSubQueryAlias() {
		return subQueryAlias;
	}
	
}
