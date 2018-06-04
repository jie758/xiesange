package com.xiesange.orm;

import com.xiesange.orm.statement.field.BaseJField;

public class FieldPair {
	private BaseJField field1;
	private BaseJField field2;
	
	public FieldPair(BaseJField field1,BaseJField field2){
		this.field1 = field1;
		this.field2 = field2;
	}

	public BaseJField getField1() {
		return field1;
	}

	public void setField1(BaseJField field1) {
		this.field1 = field1;
	}

	public BaseJField getField2() {
		return field2;
	}

	public void setField2(BaseJField field2) {
		this.field2 = field2;
	}
	
}
