package com.xiesange.orm.statement.field.summary;

import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.IQueryField;

/**
 * 统计型字段，比如select count(*),max(age),min(age),sum(age) 这类字段
 * @author wuyujie Dec 31, 2014 8:23:53 AM
 *
 */
public interface IStatQueryField extends IQueryField{
	public String getAliasName();//获取别名
	
	public String getColName(String tableAlias);
	
	public BaseJField getQueryField();
}
