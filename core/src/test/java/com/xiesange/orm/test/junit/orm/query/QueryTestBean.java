package com.xiesange.orm.test.junit.orm.query;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.insert.InsertStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class QueryTestBean extends BaseTestBean
{
    
    @Test
    /**
     * 测试普通的按条件查询:
     * select * from base_menu where name='系统菜单';
     * @throws Exception
     */
    public void testQueryByCondititon() throws Exception{
    	SysMenuTest menu = OrmTestUtil.buildSysMenu();
        menu.setName("my menu "+System.currentTimeMillis());
        Long id = menu.getId();
        new InsertStatement(menu).execute();
        //dao.insert(menu);
        
        QueryStatement queryST = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.name, menu.getName()));
        List<SysMenuTest> list = (List<SysMenuTest>)queryST.execute();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(menu.getMemo(), list.get(0).getMemo());
        
        OrmTestUtil.deleteMenus(id);
        
    }
    
    @Test
    /**
     * 测试普通的按ID查询:
     * select * from base_menu where id=xxx;
     * @throws Exception
     */
    public void testQueryById() throws Exception{
        SysMenuTest menu = OrmTestUtil.insertMenu(null);
        Long id = menu.getId();
        
        QueryStatement queryST = new QueryStatement(SysMenuTest.class, id);
        SysMenuTest result = (SysMenuTest)queryST.execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(menu.getMemo(), result.getMemo());
        
        OrmTestUtil.deleteMenus(id);
        
    }
    
    @Test
    /**
     * 测试查询指定字段:select name,memo from base_menu where id=xxx
     * @throws Exception
     */
    public void testQueryFields() throws Exception{
    	//测试查询指定字段
    	String name = "my menu "+System.currentTimeMillis();
    	String memo = "my menu's memo "+System.currentTimeMillis();
    	SysMenuTest menu = OrmTestUtil.buildSysMenu();
        menu.setName(name);
        menu.setMemo(memo);
        Long id = menu.getId();
        OrmTestUtil.insertMenu(menu);
        
        QueryStatement queryST = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.name, menu.getName()));
        queryST.appendQueryField(SysMenuTest.JField.name).appendQueryField(SysMenuTest.JField.memo);
        List<SysMenuTest> list = (List<SysMenuTest>)queryST.execute();
        
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(menu.getMemo(), memo);//指定了memo，所以memo肯定有值
        Assert.assertEquals(menu.getName(), name);//指定了name，所以name肯定有值
        Assert.assertNull(list.get(0).getUrl());//url字段没有查询，应该没值
        Assert.assertNull(list.get(0).getIcon());//icon字段没有查询，应该没值
        
        OrmTestUtil.deleteMenus(id);
        
    }
    
    
    @Test
    /**
     * 测试查询排序：select * from base_menu where xx=yyy order by layNo
     * 或者:select * from base_menu where xx=yyy order by layNo desc
     * @throws Exception
     */
    public void testOrderFields() throws Exception{
    	String name = "menu:"+System.currentTimeMillis();
    	SysMenuTest menu1 = OrmTestUtil.buildSysMenu();
    	menu1.setLayNo(10);
    	menu1.setName(name);
    	
    	SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
    	menu2.setId(menu1.getId()+10);
    	menu2.setName(name);
    	menu2.setLayNo(20);//menu2的layNo要设置的比menu1大，这样才可以提现排序
    	
    	OrmTestUtil.insertMenu(menu1);
    	OrmTestUtil.insertMenu(menu2);
    	
    	//先测试layNo升序排列
    	QueryStatement queryST = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.name,name));
        queryST.appendOrderField(SysMenuTest.JField.layNo);
        List<SysMenuTest> list = (List<SysMenuTest>)queryST.execute();
    	
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(menu1.getId(), list.get(0).getId());
        Assert.assertEquals(menu2.getId(), list.get(1).getId());
        
        //再测试layNo降序排列
        queryST = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.name,name));
        queryST.appendOrderFieldDesc(SysMenuTest.JField.layNo);
        list = (List<SysMenuTest>)queryST.execute();
    	
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(menu2.getId(), list.get(0).getId());
        Assert.assertEquals(menu1.getId(), list.get(1).getId());
    	
        OrmTestUtil.deleteMenus(menu1.getId(),menu2.getId());
    }
    
    @Test
    /**
     * 测试子查询：select * from (select * from base_menu where layNo=xxx) where name='xxx'
     */
    public void testSubQuery() throws Exception{
        SysMenuTest menu1 = OrmTestUtil.insertMenu(null);
        SysMenuTest menu2 = OrmTestUtil.insertMenu(null);
        SysMenuTest menu3 = OrmTestUtil.insertMenu(null);
        
        Long id1 = menu1.getId();
        Long id2 = menu2.getId();
        Long id3 = menu3.getId();
        
        
        QueryStatement subQuery = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.id, new Long[]{id1,id2,id3},DBOperator.IN));
        QueryStatement queryST = new QueryStatement(subQuery,new DBCondition(SysMenuTest.JField.name, menu1.getName()));
        
        List<SysMenuTest> list = (List<SysMenuTest>)queryST.execute();
        
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(menu1.getName(), list.get(0).getName());
        
        OrmTestUtil.deleteMenus(id1,id2,id3);
        
    }
    
    
}
