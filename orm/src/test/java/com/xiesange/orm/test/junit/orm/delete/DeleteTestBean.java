package com.xiesange.orm.test.junit.orm.delete;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.delete.DeleteStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class DeleteTestBean  extends BaseTestBean
{
	@Test
    public void testDeleteById() throws Exception{
		SysMenuTest menu = OrmTestUtil.buildSysMenu();
		OrmTestUtil.insertMenu(menu);
		
		//删除前先查询一下
		QueryStatement queryST = new QueryStatement(SysMenuTest.class, menu.getId());
		SysMenuTest queryEntity = (SysMenuTest)queryST.execute();
		Assert.assertNotNull(queryEntity);//能查出来，不为空
		
		DeleteStatement deleteST = new DeleteStatement(SysMenuTest.class, menu.getId());
		deleteST.execute();
		
		//删除后再查询
		queryST = new QueryStatement(SysMenuTest.class, menu.getId());
		queryEntity = (SysMenuTest)queryST.execute();
		Assert.assertNull(queryEntity);//查出来，为空
		
	}
	
    @Test
    public void testDeleteByCondition() throws Exception{
    	SysMenuTest menu = OrmTestUtil.buildSysMenu();
    	menu.setName("bill:"+menu.getId());
    	menu.setMemo("bill is a boy:"+menu.getId());
		OrmTestUtil.insertMenu(menu);
		
		//删除前先查询一下
		QueryStatement queryST = new QueryStatement(SysMenuTest.class, menu.getId());
		SysMenuTest queryEntity = (SysMenuTest)queryST.execute();
		Assert.assertNotNull(queryEntity);//能查出来，不为空
		
		DeleteStatement deleteST = new DeleteStatement(
				SysMenuTest.class, 
				new DBCondition(SysMenuTest.JField.name, menu.getName()),
				new DBCondition(SysMenuTest.JField.memo, menu.getMemo())
		);
		deleteST.execute();
		
		//删除后再查询
		queryST = new QueryStatement(SysMenuTest.class, menu.getId());
		queryEntity = (SysMenuTest)queryST.execute();
		Assert.assertNull(queryEntity);//查出来，为空
        
    }
   
}
