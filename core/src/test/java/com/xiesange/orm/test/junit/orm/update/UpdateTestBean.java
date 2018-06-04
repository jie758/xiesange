package com.xiesange.orm.test.junit.orm.update;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class UpdateTestBean extends BaseTestBean
{
    @Test
    public void testUpdateByCondition() throws Exception{
        SysMenuTest menu = OrmTestUtil.insertMenu(null);
        
        //设置更新值
        SysMenuTest value = new SysMenuTest();
        String memo = "this is modified by UpdateTestBean"+System.currentTimeMillis();
        value.setMemo(memo);
        
        //更新前查询
        SysMenuTest result = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu.getId()).execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(menu.getMemo(),result.getMemo());//和老的memo相等
        
        
        new UpdateStatement(value,new DBCondition(SysMenuTest.JField.name,menu.getName())).execute();
        
        //更新后查询
        result = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu.getId()).execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(memo,result.getMemo());//和新的memo相等
        
        OrmTestUtil.deleteMenus(menu.getId());
    }
    
    @Test
    public void testUpdateById() throws Exception{
    	SysMenuTest menu = OrmTestUtil.insertMenu(null);
        
        //设置更新值
        SysMenuTest value = new SysMenuTest();
        String memo = "this is modified by UpdateTestBean"+System.currentTimeMillis();
        value.setMemo(memo);
        
        //更新前查询
        SysMenuTest result = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu.getId()).execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(menu.getMemo(),result.getMemo());//和老的memo相等
        
        
        new UpdateStatement(value,menu.getId()).execute();
        
        //更新后查询
        result = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu.getId()).execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(memo,result.getMemo());//和新的memo相等
        
        OrmTestUtil.deleteMenus(menu.getId());
    }
}
