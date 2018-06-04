package com.xiesange.orm.statement.insert;

import java.sql.PreparedStatement;
import java.util.List;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 单条insert操作的语句执行对象。
 * @Description
 * @author wuyj
 * @Date 2013-10-24
 */
public class InsertStatement extends BaseDBStatement
{
    private DBEntity entity;
    
    public <T extends DBEntity>InsertStatement(T entity){
    	this(entity,null);
    }
    
    public <T extends DBEntity>InsertStatement(T entity,String tableName){
    	super(entity.getClass(),tableName);
    	this.entity = entity;
    }
    
    @Override
    public void parse() throws Exception
    {
    	String tableName = NullUtil.isNotEmpty(this.dbTableName) ? this.dbTableName : DBHelper.getDBTableName(entityClass);
    	this.appendSQL("insert into ").appendSQL(tableName);
    	List<BaseJField> jfs = DBHelper.getAllJFieldList(entityClass);
    	int length = jfs.size();
    	StringBuffer sb_col_name = new StringBuffer(64);
        StringBuffer sb_col_value = new StringBuffer(64);
        BaseJField jf;
        for(int i=0;i<length;i++){
        	jf = jfs.get(i);
            if(i > 0){
                sb_col_name.append(",");
                sb_col_value.append(",");
            }
            sb_col_name.append(jf.getColName());
            sb_col_value.append("?");
            
            this.addBindParameter(jf,DBHelper.getEntityValue(entity,jf));
        }
        
        this.appendSQL("(");
        this.appendSQL(sb_col_name.toString());
        this.appendSQL(")");
        this.appendSQL(" values(");
        this.appendSQL(sb_col_value.toString());
        this.appendSQL(")");
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
