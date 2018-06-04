package com.xiesange.orm.statement.field;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.statement.field.summary.IStatQueryField;

public class OrderField {
	private IQueryField jfield;
	private boolean isAsc;//默认升序
	
	public OrderField(IQueryField jfield){
		this(jfield,true);
	}
	public OrderField(IQueryField jfield,boolean isAsc){
		this.jfield = jfield;
		this.isAsc = isAsc; 
	}
	public IQueryField getJfield() {
		return jfield;
	}
	public void setJfield(IQueryField jfield) {
		this.jfield = jfield;
	}
	public boolean isAsc() {
		return isAsc;
	}
	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(jfield instanceof BaseJField){
			sb.append(jfield.getColName());
		}else{
			String aliasName = ((IStatQueryField)jfield).getAliasName();
			sb.append(NullUtil.isNotEmpty(aliasName) ? aliasName : jfield.getColName());
		}
		if(!isAsc){
			sb.append(" ").append("DESC");//降序
		}
		return sb.toString();
	}
}
