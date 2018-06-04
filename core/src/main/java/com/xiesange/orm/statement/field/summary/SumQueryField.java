package com.xiesange.orm.statement.field.summary;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 针对查询字段总和的语句。
 * select sum(amount) from xxx
 * @author wuyujie Dec 28, 2014 5:39:53 PM
 *
 */
public class SumQueryField implements IStatQueryField {
	private BaseJField field;//对应字段的BaseJField
	private String aliasName;
	public SumQueryField(BaseJField field){
		this.field = field;
	}
	
	public SumQueryField(BaseJField field,String aliasName){
		this.field = field;
		this.aliasName = aliasName;
	}
	
	public String getColName() {
		return getColName(null);
	}
	
	@Override
	public String getColName(String tableAlias) {
		String colName = field instanceof BaseJField ? ((BaseJField)field).getColName() : ((IStatQueryField)field).getColName();
		String result =  NullUtil.isEmpty(tableAlias) ? "SUM("+colName+")": "SUM("+tableAlias+"."+colName+")";
		if(NullUtil.isNotEmpty(aliasName)){
			result += " "+aliasName;
		}
		return result;
	}
	@Override
	public String getAliasName() {
		return aliasName;
	}
	public BaseJField getJField(){
		return field instanceof BaseJField ? (BaseJField)field : ((SumQueryField)field).getJField();
	}
	@Override
	public BaseJField getQueryField() {
		return field;
	}

}
