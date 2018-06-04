package com.elsetravel.mis.component;

import java.util.ArrayList;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.gen.dbentity.base.BaseMenu;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBHelper;

public class MenuCmp {
	/**
	 * 从列表中查出当前指定父菜单id的直属子菜单
	 * @param parentId
	 * @param childenlist
	 * @return
	 */
	public static List<BaseMenu> getChildrenList(long parentId,List<BaseMenu> childenlist){
		List<BaseMenu> result = new ArrayList<BaseMenu>();
		
		for(BaseMenu child : childenlist){
			if(child.getParentId() == parentId){
				result.add(child);
			}
		}
		return result.size() == 0 ? null : result;
	}
	
	public static BaseMenu query(long menuId) throws Exception{
		return DBHelper.getDao().queryById(BaseMenu.class, menuId);
	}
	
	/**
	 * 检查某个菜单是否存在
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	public static BaseMenu checkMenuExsit(long menuId) throws Exception{
		BaseMenu menu = query(menuId);
		if(menu == null){
			throw ETUtil.buildException(ErrorDefine.MENU_NOTEXIST);
		}
		return menu;
	}
	
}
