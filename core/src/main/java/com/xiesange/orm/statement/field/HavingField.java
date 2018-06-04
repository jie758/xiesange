package com.xiesange.orm.statement.field;

import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.summary.IStatQueryField;


public class HavingField {
	private IStatQueryField field;
	private DBOperator operator;
	private Object value;
	
	public HavingField(IStatQueryField field,DBOperator operator,Object value){
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public IStatQueryField getField() {
		return field;
	}



	public void setField(IStatQueryField field) {
		this.field = field;
	}



	public DBOperator getOperator() {
		return operator;
	}

	public void setOperator(DBOperator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
