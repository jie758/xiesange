package com.xiesange.orm;

import java.util.Date;

import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;

public class FieldUpdateExpression {
	private BaseJField jf;
	private Object value;
	
	public FieldUpdateExpression(BaseJField jf,String value){
		this.jf = jf;
		this.value = value;
	}
	public FieldUpdateExpression(BaseJField jf,Integer value){
		this(jf,String.valueOf(value));
	}
	public FieldUpdateExpression(BaseJField jf,Short value){
		this(jf,String.valueOf(value));
	}
	public FieldUpdateExpression(BaseJField jf,Long value){
		this(jf,String.valueOf(value));
	}
	public FieldUpdateExpression(BaseJField jf,Float value){
		this(jf,String.valueOf(value));
	}
	public FieldUpdateExpression(BaseJField jf,Double value){
		this(jf,String.valueOf(value));
	}
	public FieldUpdateExpression(BaseJField jf,Date value){
		this.jf = jf;
		this.value = value;
	}
	public FieldUpdateExpression(BaseJField jf,NativeValue value){
		this.jf = jf;
		this.value = value;
	}
	public FieldUpdateExpression(BaseJField jf,QueryStatement value){
		this.jf = jf;
		this.value = value;
	}
	
	/*public FieldWrapper(BaseJField jf,Object value,boolean isNativeValue){
		this.jf = jf;
		this.value = value;
		this.isNativeValue = isNativeValue;
	}*/
	
	public BaseJField getJf() {
		return jf;
	}
	public Object getValue() {
		return value;
	}
}
