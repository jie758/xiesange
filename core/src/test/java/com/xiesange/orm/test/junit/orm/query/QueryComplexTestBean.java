package com.xiesange.orm.test.junit.orm.query;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;
/**
 * 测试复杂嵌套条件
 * @Description
 * @author wuyj
 * @Date 2013-12-13
 */
public class QueryComplexTestBean extends BaseTestBean
{
    @Test
    public void testOr() throws Exception{
        SysMenuTest menu = OrmTestUtil.buildSysMenu();
        menu.setName("menu1"+System.currentTimeMillis());
        Long id = menu.getId();
        OrmTestUtil.insertMenu(menu);
        Thread.sleep(100);
        
        SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
        menu2.setName("menu2"+System.currentTimeMillis());
        Long id2 = menu2.getId();
        OrmTestUtil.insertMenu(menu2);
        Thread.sleep(100);
        
        
        SysMenuTest menu3 = OrmTestUtil.buildSysMenu();
        menu3.setName("menu3"+System.currentTimeMillis());
        Long id3 = menu3.getId();
        OrmTestUtil.insertMenu(menu3);
        
        
        QueryStatement queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBOrCondition(SysMenuTest.JField.name, menu.getName()),
            new DBOrCondition(SysMenuTest.JField.name, menu2.getName()),
            new DBOrCondition(SysMenuTest.JField.name, menu3.getName())
        );
        List<SysMenuTest> result = (List<SysMenuTest>)queryST.execute();
       
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        
        OrmTestUtil.deleteMenus(id,id2,id3);
    }
    
    @Test
    public void testComplex() throws Exception{
        SysMenuTest menu = OrmTestUtil.buildSysMenu();
        menu.setName("menu1"+System.currentTimeMillis());
        menu.setLayNo(2);
        menu.setMemo("group1");
        Long id = menu.getId();
        OrmTestUtil.insertMenu(menu);
        Thread.sleep(100);
        
        SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
        menu2.setName("menu2"+System.currentTimeMillis());
        menu2.setLayNo(2);
        menu2.setMemo("group2");
        Long id2 = menu2.getId();
        OrmTestUtil.insertMenu(menu2);
        Thread.sleep(100);
        
        
        SysMenuTest menu3 = OrmTestUtil.buildSysMenu();
        menu3.setName("menu3"+System.currentTimeMillis());
        menu3.setLayNo(3);
        menu3.setUrl("url3");
        menu3.setMemo("group2");
        Long id3 = menu3.getId();
        OrmTestUtil.insertMenu(menu3);
        
        
        QueryStatement queryST = new QueryStatement(
            SysMenuTest.class, 
            new DBCondition(SysMenuTest.JField.layNo, 2),
            new DBCondition(
                new DBOrCondition(SysMenuTest.JField.name, menu2.getName()),
                new DBOrCondition(SysMenuTest.JField.name, menu.getName())  
            ),
            new DBCondition(
                new DBOrCondition(SysMenuTest.JField.memo, "group2"),
                new DBOrCondition(SysMenuTest.JField.url, "url3")
            )
       );
        List<SysMenuTest> result = (List<SysMenuTest>)queryST.execute();
        
       
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        
        OrmTestUtil.deleteMenus(id,id2,id3);
    }
    
   
}
