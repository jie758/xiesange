package com.xiesange.orm.statement.field.summary;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.IQueryField;
/**
 * 针对查询字段最小值的语句。
 * select min(amount) from xxx
 * @author wuyujie Dec 28, 2014 5:39:53 PM
 *
 */
public class MinQueryField implements IStatQueryField {
	private BaseJField field;//对应字段的BaseJField
	private String aliasName;
	public MinQueryField(BaseJField field){
		this.field = field;
	}
	public MinQueryField(BaseJField field,String aliasName){
		this.field = field;
		this.aliasName = aliasName;
	}
	public String getColName() {
		return getColName(null);
	}
	@Override
	public String getColName(String tableAlias) {
		String colName = field instanceof BaseJField ? ((BaseJField)field).getColName() : ((IStatQueryField)field).getColName();
		String result =  NullUtil.isEmpty(tableAlias) ? "MIN("+colName+")" : "MIN("+tableAlias+"."+colName+")";
		if(NullUtil.isNotEmpty(aliasName)){
			result += " "+aliasName;
		}
		return result;
	}
	@Override
	public String getAliasName() {
		return aliasName;
	}
	
	@Override
	public BaseJField getQueryField() {
		return field;
	}
}
