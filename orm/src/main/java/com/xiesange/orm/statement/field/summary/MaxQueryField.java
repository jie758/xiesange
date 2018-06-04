package com.xiesange.orm.statement.field.summary;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 针对查询字段最大值的语句。
 * select min(amount) from xxx
 * @author wuyujie Dec 28, 2014 5:39:53 PM
 *
 */
public class MaxQueryField implements IStatQueryField {
	private BaseJField field;//对应字段的BaseJField
	private String aliasName;
	public MaxQueryField(BaseJField field){
		this.field = field;
	}
	public MaxQueryField(BaseJField field,String aliasName){
		this.field = field;
		this.aliasName = aliasName;
	}
	
	@Override
	public String getColName() {
		return getColName(null);
	}
	
	public String getColName(String tableAlias) {
		String colName = field instanceof BaseJField ? ((BaseJField)field).getColName() : ((IStatQueryField)field).getColName();
		String result = NullUtil.isEmpty(tableAlias) ? "MAX("+colName+")" : "MAX("+tableAlias+"."+colName+")";
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
