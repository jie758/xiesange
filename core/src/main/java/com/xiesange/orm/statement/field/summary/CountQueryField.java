package com.xiesange.orm.statement.field.summary;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;


/**
 * 针对查询记录总数的语句。
 * select count(*) from xxx
 * @author wuyujie Dec 28, 2014 5:39:53 PM
 *
 */
public class CountQueryField implements IStatQueryField {
	private static CountQueryField INSTANCE;
	private static final String DEFAULT_NAME = "COUNT(*)";
	private String aliasName;
	
	/**
	 * 如果count(*)不进行别名设置的话进行用这个方法，返回单例，可以避免每次都去new一个，增加内存开销
	 * @return
	 */
	public static CountQueryField getInstance(){
		if(INSTANCE == null){
			INSTANCE = new CountQueryField();
		}
		return INSTANCE;
	}
	
	public static CountQueryField getInstance(String aliasName){
		CountQueryField countQF = new CountQueryField();
		countQF.aliasName = aliasName;
		return countQF;
	}
	
	public String getColName() {
		StringBuffer result = new StringBuffer(DEFAULT_NAME);
		if(NullUtil.isNotEmpty(aliasName)){
			result.append(" ").append(aliasName);
		}
		return result.toString();
	}
	public String getColName(String tableAlias) {
		return getColName();
	}

	public String getAliasName() {
		return aliasName;
	}
	
	@Override
	public BaseJField getQueryField() {
		return null;
	}
}
