package com.xiesange.orm.pojo;

import java.util.Date;

import com.xiesange.core.util.DateUtil;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * SQL变量绑定对象。
 * 假如有sql：select name from sys_menu where code = ? and url = ?;
 * 其中code和ur两个字段是需要变量绑定的，因此会创建两个SQLBindParameter：
 * 	 new SQLBindParameter(SysMenu.JField.code,'system');
 *   new SQLBindParameter(SysMenu.JField.url,'/system.jsp');
 * 后续在处理的过程中会使用这两个变量绑定对象进行SQL的解析操作
 * @author wuyujie Sep 4, 2014 1:30:16 PM
 *
 */
public class SQLBindParameter
{
    private Object value;//绑定的变量值
    private BaseJField jfield;//绑定的变量对应的字段
    
    public SQLBindParameter(BaseJField jfield,Object value){
        this.value = value;
        this.jfield = jfield;
    }
    public Object getValue()
    {
        return value;
    }
    public BaseJField getJfield()
    {
        return jfield;
    }
    
    public String toString(int paramIndex,Object paramValue,BaseJField jf) throws Exception{
        StringBuffer sb = new StringBuffer(128);
        Object inputValue = paramValue==null?value:paramValue;
        if(inputValue != null && inputValue instanceof Date){
        	boolean isDateTime = jf.getColTypeName().equalsIgnoreCase("DATETIME");
        	inputValue = DateUtil.date2Str((Date)inputValue, isDateTime ? DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS : DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
        }
        sb.append("[")
            .append(paramIndex)
            .append("]")
            .append(jfield.getColName())
            .append(" = ")
            .append(inputValue)
            .append(" (")
            .append(jfield.getJavaType().getSimpleName())
            .append(")"); 
        return sb.toString();
        
    }
}
