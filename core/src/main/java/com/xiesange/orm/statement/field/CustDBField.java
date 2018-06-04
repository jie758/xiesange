package com.xiesange.orm.statement.field;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.summary.IStatQueryField;

/**
 * 自定义的字段对象
 * @author Wilson Wu
 * @date 2015年9月11日
 *
 */
public class CustDBField implements IStatQueryField {
	private String text;//自定义的查询字段表达式
	private String aliasName;
	private String colType;//数据库字段类型
	public CustDBField(String name){
		this(name,null,null);
	}
	public CustDBField(String text,String aliasName){
		this(text,aliasName,null);
	}
	
	public CustDBField(String text,String aliasName,String colType){
		this.text = text;
		this.aliasName = aliasName;
		this.colType = colType;
	}
	
	@Override
	public String getColName() {
		if(NullUtil.isEmpty(aliasName))
			return text;
		else{
			return new StringBuffer(text).append(" ").append(aliasName).toString();
		}
	}
	public String getColName(String tableAlias) {
		return getColName();
	}
	public String getAliasName() {
		return aliasName;
	}
	public String getColType() {
		return colType;
	}
	
	@Override
	public BaseJField getQueryField() {
		return null;
	}
	
}
