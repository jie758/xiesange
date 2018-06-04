package com.xiesange.orm.statement.field.summary;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.IQueryField;
/**
 * 针对查询字段总和的语句。
 * select avg(amount) from xxx
 * @author wuyujie Dec 28, 2014 5:39:53 PM
 *
 */
public class AvgQueryField implements IStatQueryField {
	private BaseJField field;//对应字段的BaseJField
	private String aliasName;
	
	public AvgQueryField(BaseJField field){
		this.field = field;
	}
	public AvgQueryField(BaseJField field,String aliasName){
		this.field = field;
		this.aliasName = aliasName;
	}
	
	@Override
	public String getColName() {
		return getColName(null);
	}
	public String getColName(String tableAlias) {
		String colName = field instanceof BaseJField ? ((BaseJField)field).getColName() : ((IStatQueryField)field).getColName();
		String result =  field instanceof BaseJField && NullUtil.isNotEmpty(tableAlias) ? 
								"AVG("+tableAlias+"."+colName+")"
								:"AVG("+colName+")";
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
