package com.xiesange.orm.test.junit.orm.query;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.query.CountQueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class QueryCountTestBean extends BaseTestBean
{
    @Test
    /**
     * 测试记录合计：select count(*) from base_menu where layNo=100;
     * @throws Exception
     */
    public void testQueryCountByCondititon() throws Exception{
    	SysMenuTest menu1 = OrmTestUtil.buildSysMenu();
    	menu1.setLayNo(100);
    	OrmTestUtil.insertMenu(menu1);
    	
    	SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
    	menu2.setLayNo(100);
    	OrmTestUtil.insertMenu(menu2);
    	
        Long id1 = menu1.getId();
        Long id2 = menu2.getId();
        
        
        CountQueryStatement queryST = new CountQueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.layNo,100));
        int count = (Integer)queryST.execute();
        Assert.assertEquals(2, count);
        //Assert.assertEquals(menu.getMemo(), list.get(0).getMemo());
        
        OrmTestUtil.deleteMenus(id1,id2);
        
    }
    
}
