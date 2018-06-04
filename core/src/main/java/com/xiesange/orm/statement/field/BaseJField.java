package com.xiesange.orm.statement.field;

import com.xiesange.orm.DBEntity;

public interface BaseJField extends IQueryField{
	public abstract String getName();
	public abstract String getColTypeName();
	public abstract Class<?> getJavaType();
	public abstract Integer getLength();
	public abstract boolean getNullable();
	public abstract String getTableName();
	public abstract Class<? extends DBEntity> getEntityClass();
}
