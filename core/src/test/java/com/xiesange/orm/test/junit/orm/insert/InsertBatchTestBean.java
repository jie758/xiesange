package com.xiesange.orm.test.junit.orm.insert;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.statement.delete.DeleteStatement;
import com.xiesange.orm.statement.insert.InsertBatchStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class InsertBatchTestBean extends BaseTestBean
{
    
    @Test
    public void testInsertBatch() throws Exception{
        SysMenuTest menu1 = OrmTestUtil.buildSysMenu();
        Thread.sleep(100);
        SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
        Thread.sleep(100);
        SysMenuTest menu3 = OrmTestUtil.buildSysMenu();
        List<SysMenuTest> list = new ArrayList<SysMenuTest>();
        list.add(menu1);
        list.add(menu2);
        list.add(menu3);
        
        new InsertBatchStatement(list).execute();
        
        SysMenuTest q_menu1 = (SysMenuTest)new QueryStatement(SysMenuTest.class, menu1.getId()).execute();
        SysMenuTest q_menu2 = (SysMenuTest)new QueryStatement(SysMenuTest.class, menu2.getId()).execute();
        SysMenuTest q_menu3 = (SysMenuTest)new QueryStatement(SysMenuTest.class, menu3.getId()).execute();
    
        Assert.assertNotNull(q_menu1);
        Assert.assertEquals(menu1.getMemo(), q_menu1.getMemo());
        
        Assert.assertNotNull(q_menu2);
        Assert.assertEquals(menu2.getMemo(), q_menu2.getMemo());
        
        Assert.assertNotNull(q_menu3);
        Assert.assertEquals(menu3.getMemo(), q_menu3.getMemo());
        
        
        new DeleteStatement(SysMenuTest.class, menu1.getId()).execute();
        new DeleteStatement(SysMenuTest.class, menu2.getId()).execute();
        new DeleteStatement(SysMenuTest.class, menu3.getId()).execute();
    }
    
}
