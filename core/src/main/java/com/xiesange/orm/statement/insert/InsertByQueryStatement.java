package com.xiesange.orm.statement.insert;

import java.sql.PreparedStatement;
import java.util.List;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.IQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
/**
 * 适用于查询插入的场景。比如insert into guider_his select * from guider，即插入的数据来自另一查询sql的结果集
 * @Description
 * @author wuyj
 * @Date 2013-10-24
 */
public class InsertByQueryStatement extends BaseDBStatement
{
	private List<? extends IQueryField> insertFields;//插入的字段,如果没有指定这个属性，那么默认就是目标表的所有字段值都插入
    private QueryStatement queryStatement;//查询语句
    
    public <T extends DBEntity>InsertByQueryStatement(Class<T> entityClass,QueryStatement queryStatement){
    	this(entityClass,null,queryStatement);
    }
    public <T extends DBEntity>InsertByQueryStatement(Class<T> entityClass,List<? extends IQueryField> insertFields,QueryStatement queryStatement){
    	super(entityClass);
    	this.insertFields = insertFields;
    	this.queryStatement = queryStatement;
    }
    
    public <T extends DBEntity>InsertByQueryStatement(String tableName,List<? extends IQueryField> insertFields,QueryStatement queryStatement){
    	super(null,tableName);
    	this.insertFields = insertFields;
    	this.queryStatement = queryStatement;
    }
    
    @Override
    public void parse() throws Exception
    {
    	String tableName = NullUtil.isNotEmpty(this.dbTableName) ? this.dbTableName : DBHelper.getDBTableName(entityClass);
    	this.appendSQL("insert into ").appendSQL(tableName);
    	List<? extends IQueryField> jfs = insertFields != null ? insertFields : DBHelper.getAllJFieldList(entityClass);
    	int length = jfs.size();
    	StringBuffer sb_col_name = new StringBuffer(64);
    	IQueryField jf;
        for(int i=0;i<length;i++){
        	jf = jfs.get(i);
            if(i > 0){
                sb_col_name.append(",");
            }
            sb_col_name.append(jf.getColName());
        }
        
        this.appendSQL("(");
        this.appendSQL(sb_col_name.toString());
        this.appendSQL(") ");
        
        //后面构建select的sql
        queryStatement.parse();
        this.appendSQL(queryStatement.getSQL());
        this.addBindParameters(queryStatement.getBindParameters());
    }
    
    @Override
    public Object access(PreparedStatement ps) throws Exception
    {
        return ps.executeUpdate();
    }

    @Override
    public Object wrap(Object accessResult) throws Exception
    {
        return accessResult;
    }

	@Override
	public long count(Object wrapResult) {
		return ((Integer)wrapResult).longValue();
	}
    
}
