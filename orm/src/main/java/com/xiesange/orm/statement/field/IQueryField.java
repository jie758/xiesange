package com.xiesange.orm.statement.field;
/**
 * select field1,field2 from table where xx=v1中的field1字段
 * @author wuyujie Dec 31, 2014 8:23:53 AM
 *
 */
public interface IQueryField {
	public String getColName();
}
