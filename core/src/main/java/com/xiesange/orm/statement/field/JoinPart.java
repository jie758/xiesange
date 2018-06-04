package com.xiesange.orm.statement.field;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.FieldPair;

public class JoinPart {
	private Class<? extends DBEntity> joinClass;
	private List<FieldPair> fieldPairs;
	
	public JoinPart(Class<? extends DBEntity> joinClass){
		this.joinClass = joinClass;
	}
	public JoinPart(Class<? extends DBEntity> joinClass,List<FieldPair> fieldPairs){
		this.joinClass = joinClass;
		this.fieldPairs = fieldPairs;
	}
	public JoinPart(Class<? extends DBEntity> joinClass,FieldPair... fieldPairs){
		this.joinClass = joinClass;
		for(FieldPair pair : fieldPairs){
			addFieldPair(pair);
		}
	}
	
	public void addFieldPair(FieldPair fieldPair){
		if(fieldPairs == null){
			fieldPairs = new ArrayList<FieldPair>();
		}
		fieldPairs.add(fieldPair);
	}
	public Class<? extends DBEntity> getJoinClass() {
		return joinClass;
	}
	public List<FieldPair> getFieldPairs() {
		return fieldPairs;
	}
}
