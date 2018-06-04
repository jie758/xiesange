package com.xiesange.orm.statement.update;

import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.NativeValue;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;

public class UpdateStatement extends BaseDBStatement
{
    private DBEntity valueEntity;//需要更新的值
    private DBCondition[] conditions;//查询条件，updateByCondition使用
    private Long id;//主键值，updateById使用
    
    public <T extends DBEntity>UpdateStatement(T valueEntity,Long id){
        super(valueEntity.getClass());
    	this.valueEntity = valueEntity;
        this.id = id;
    }
    public <T extends DBEntity>UpdateStatement(FieldUpdateExpression[] values,Long id) throws InstantiationException, IllegalAccessException{
    	super(values[0].getJf().getEntityClass());
    	this.valueEntity = DBHelper.parse2DBEntity(values);
        this.id = id;
    }
    
    public <T extends DBEntity>UpdateStatement(T valueEntity,DBCondition...conditions){
    	super(valueEntity.getClass());
    	this.valueEntity = valueEntity;
        this.conditions = conditions;
    }
    
    public <T extends DBEntity>UpdateStatement(FieldUpdateExpression[] values,DBCondition...conditions) throws InstantiationException, IllegalAccessException{
    	super(values[0].getJf().getEntityClass());
    	this.valueEntity = DBHelper.parse2DBEntity(values);
        this.conditions = conditions;
    }
    
    @Override
    public void parse() throws Exception
    {
        this.appendSQL("update ")
        	.appendSQL(DBHelper.getDBTableName(valueEntity.getClass()))
        	.appendSQL(" set ");
    	
        Map<BaseJField,Object> settedValue = valueEntity._getSettedValue();
        Iterator<Entry<BaseJField,Object>> it = settedValue.entrySet().iterator();
        
        int i=0;
        while(it.hasNext()){
            Entry<BaseJField,Object> entry = it.next();
            BaseJField jfield = entry.getKey();
            String colName = jfield.getColName();
            Object value = entry.getValue();
            if(i > 0){
            	this.appendSQL(" , ");
            }
            this.appendSQL(colName);
            if(value instanceof NativeValue){
            	//原生值，不用变量绑定
            	this.appendSQL("="+((NativeValue)value).getValue());
            }else if(value instanceof QueryStatement){
            	//子查询的结果值，比如:set value = (select max(sum) from xx where xxx)
            	QueryStatement qs = (QueryStatement)value;
            	qs.parse();
            	this.appendSQL("=("+qs.getSQL()+")");
            	this.addBindParameters(qs.getBindParameters());
            }else{
            	this.appendBindSQL(" = ?",jfield,value);
            }
            i++;
        }
        
        //构建where条件
        if(id == null && NullUtil.isEmpty(conditions)){
        	return;//没传入条件，表示查询全部
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

	public DBEntity getValueEntity() {
		return valueEntity;
	}

	public DBCondition[] getConditions() {
		return conditions;
	}

	public Long getId() {
		return id;
	}

	@Override
	public long count(Object wrapResult) {
		return ((Integer)wrapResult).longValue();
	}
}
