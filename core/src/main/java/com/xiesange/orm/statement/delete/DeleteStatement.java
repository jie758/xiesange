package com.xiesange.orm.statement.delete;

import java.sql.PreparedStatement;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.BaseDBStatement;

public class DeleteStatement extends BaseDBStatement
{
    private Class<? extends DBEntity> entityClass;
    private DBCondition[] conditions;//查询条件，queryByCondition使用
    private Long id;//主键值
    
    
    /*public static DeleteStatement getPattern(Class<? extends DBEntity> entityClass,DBCondition...conditions) throws Exception{
        DeleteStatement st = new DeleteStatement(entityClass);
        st.conditions = conditions;
        return st;
    }
    public static DeleteStatement getPatternById(Class<? extends DBEntity> entityClass) throws Exception{
        DeleteStatement st = new DeleteStatement(entityClass);
        st.id = -1L;//这个设置没有其他意义，只是为了构建的时候用ID去构建
        return st;
    }*/
    
    //加了一个isDeleteAll参数，主要是为了让用户显式的告知是删除全部
    public DeleteStatement(Class<? extends DBEntity> entityClass,boolean isDeleteAll){
        super(entityClass);
    } 

    
    public <T extends DBEntity>DeleteStatement(Class<T> entityClass,Long id){
        super(entityClass);
    	this.entityClass = entityClass;
        this.id = id;
    }
    
    public <T extends DBEntity>DeleteStatement(Class<T> entityClass,DBCondition...conditions){
    	super(entityClass);
        this.entityClass = entityClass;
        this.conditions = conditions;
    }

    @Override
	public void parse() throws Exception {
        this.appendSQL("delete from ")
        	.appendSQL(DBHelper.getDBTableName(entityClass));
        
        //构建where条件
        if(id == null && NullUtil.isEmpty(conditions)){
        	return;//没传入条件，表示删除全部
        }else if(id != null){
        	DBHelper.appendWhereSQL(this, id);
        }else{
        	DBHelper.appendWhereSQL(this, conditions);
        }
		
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
