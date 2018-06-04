package com.xiesange.orm.statement.field;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.summary.IStatQueryField;

/**
 * 分组字段信息。包括group by和having两个部分
 * @author wuyujie Jan 1, 2015 8:40:13 PM
 *
 */
public class GroupField {
	private List<BaseJField> groupList;
	private List<HavingField> havingList;
	
	public GroupField(BaseJField groupField){
		addGroup(groupField);
	}
	
	public GroupField addGroup(BaseJField groupField){
		if(groupList == null)
			groupList = new ArrayList<BaseJField>();
		groupList.add(groupField);
		return this;
	}
	public BaseJField getGroup(int i){
		return NullUtil.isNotEmpty(groupList) ? groupList.get(i) : null;
	}
	
	
	public GroupField addHaving(HavingField condition){
		if(havingList == null)
			havingList = new ArrayList<HavingField>();
		havingList.add(condition);
		return this;
	}
	public GroupField addHaving(IStatQueryField field,DBOperator operator,Object value){
		return this.addHaving(new HavingField(field,operator,value));
	}
	
	public HavingField getHaving(int i){
		return NullUtil.isNotEmpty(havingList) ? havingList.get(i) : null;
	}

	public List<BaseJField> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<BaseJField> groupList) {
		this.groupList = groupList;
	}

	public List<HavingField> getHavingList() {
		return havingList;
	}

	public void setHavingList(List<HavingField> havingList) {
		this.havingList = havingList;
	}
	
	
}
