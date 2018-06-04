package com.xiesange.orm.sql;

import com.xiesange.orm.statement.field.BaseJField;


/**
 * @Description:Or条件连接对象                  
  * @Company: Asiainfo-Linkage Technologies(China),Inc.  Hangzhou                                                                                                                                                                                                                             
  * @Author wuyj                                                                                                                                                                                                                                                                           
  * @Date 2011-9-27
 */
public class DBOrCondition extends DBCondition {
    
    protected DBOrCondition(){
    }
    
    public DBOrCondition(DBCondition... subConds) throws Exception{
        super(subConds);
    }
    
    public DBOrCondition(BaseJField field, Object value)
    {
        super(field,value,DBOperator.EQUALS);
    }

    public DBOrCondition(BaseJField jfield, Object value, DBOperator operator)
    {
        super(jfield,value,operator);
    }
}
