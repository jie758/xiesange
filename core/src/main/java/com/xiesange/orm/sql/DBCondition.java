package com.xiesange.orm.sql;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;



public class DBCondition {
    private BaseJField jfield;// 需要查询的Field
    private Object value;// 需要查询的缓存DataObject字段对应的值
    private DBOperator operator;// 枚举值 ,如： Operator.EQUALS(默认)
    
    
    private DBCondition[] subConditions;// 嵌套子条件，和上面jfield,value,operator三个是互斥，只能用一种
    
    protected DBCondition(){
        //禁止使用构造函数
    }
    
    /**
     * 构造嵌套条件
     * @author wuyj 2013-12-13
     * @param subConds
     * @return
     * @throws Exception
     */
    public DBCondition(DBCondition... subConds) throws Exception
    {
        if (NullUtil.isEmpty(subConds) || subConds.length < 2)
        {
            throw new Exception("At least 2 conditions should be passed in.");
        }
        this.subConditions = subConds;
    }
    
    
    public DBCondition(BaseJField field, Object value)
    {
        this(field,value,DBOperator.EQUALS);
    }

    public DBCondition(BaseJField jfield, Object value, DBOperator operator)
    {
    	this.jfield = jfield;
        this.value = value;
        this.operator = operator;
    }

    public BaseJField getJField()
    {
        return jfield;
    }

    public Object getValue()
    {
        return value;
    }
    public DBOperator getOperator()
    {
        return operator;
    }

    public DBCondition[] getSubConditions()
    {
        return subConditions;
    }

	public void setValue(Object value) {
		this.value = value;
	}
    
}
