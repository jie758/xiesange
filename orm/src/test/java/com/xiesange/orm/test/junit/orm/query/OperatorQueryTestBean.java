package com.xiesange.orm.test.junit.orm.query;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;
/**
 * 主要是运算符的单元测试，比如=,>,<,like等,还有复杂条件的查询
 * @author Think
 *
 */
public class OperatorQueryTestBean extends BaseTestBean
{
    @Test
    public void testQueryOfLike() throws Exception{
    	Long time = System.currentTimeMillis();
    	
    	SysMenuTest menu1 = OrmTestUtil.buildSysMenu();
    	menu1.setLayStr(time+"-01-01");
    	OrmTestUtil.insertMenu(menu1);
    	
    	SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
    	menu2.setLayStr(time+"-02-01");
    	OrmTestUtil.insertMenu(menu2);
        
    	QueryStatement queryST = new QueryStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.layStr, time+"-%",DBOperator.LIKE));
        List<SysMenuTest> result = (List<SysMenuTest>)queryST.execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        
        OrmTestUtil.deleteMenus(menu1.getId(),menu2.getId());
        
    }
    
    @Test
    public void testQueryOfMultiCond() throws Exception{
        SysMenuTest menu = OrmTestUtil.buildSysMenu();
        Long id = menu.getId();
        String name = "Test name : "+id;
        menu.setName(name);//name值设置成一样
        menu.setMemo("group1");
        OrmTestUtil.insertMenu(menu);
        
        menu = OrmTestUtil.buildSysMenu();
        menu.setName(name);//name值设置成一样
        menu.setMemo("group1");
        Long id2 = menu.getId();
        OrmTestUtil.insertMenu(menu);
        
        menu = OrmTestUtil.buildSysMenu();
        menu.setName(name);//name值设置成一样
        Long id3 = menu.getId();
        menu.setMemo("group2");
        OrmTestUtil.insertMenu(menu);
        
        
        QueryStatement queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBCondition(SysMenuTest.JField.name, name),
    		new DBCondition(SysMenuTest.JField.memo, "group1")
        );
        List<SysMenuTest> result = (List<SysMenuTest>)queryST.execute();
        
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        
        OrmTestUtil.deleteMenus(id,id2,id3);
        
    }
    
    @Test
    public void testQueryOfGreateEq() throws Exception{
    	SysMenuTest menu = OrmTestUtil.buildSysMenu();
        Long id = menu.getId();
        String name = "Test name : "+id;
        menu.setName(name);//name值设置成一样
        menu.setMemo("group1");
        menu.setLayNo(10);
        OrmTestUtil.insertMenu(menu);
        
        menu = OrmTestUtil.buildSysMenu();
        menu.setName(name);//name值设置成一样
        menu.setMemo("group1");
        menu.setLayNo(20);
        Long id2 = menu.getId();
        
        OrmTestUtil.insertMenu(menu);
        
        menu = OrmTestUtil.buildSysMenu();
        menu.setName(name);//name值设置成一样
        menu.setLayNo(30);
        Long id3 = menu.getId();
        menu.setMemo("group2");
        OrmTestUtil.insertMenu(menu);
        
        QueryStatement queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBCondition(SysMenuTest.JField.name, name),
    		new DBCondition(SysMenuTest.JField.layNo, 10,DBOperator.GREAT_EQUALS)//查询layNo>=10
        );
        List<SysMenuTest> result = (List<SysMenuTest>)queryST.execute();
        
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        
        
        queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBCondition(SysMenuTest.JField.name, name),
    		new DBCondition(SysMenuTest.JField.layNo, 20,DBOperator.GREAT_EQUALS)//查询layNo>=20
        );
        result = (List<SysMenuTest>)queryST.execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        
        
        queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBCondition(SysMenuTest.JField.name, name),
    		new DBCondition(SysMenuTest.JField.layNo, 30,DBOperator.GREAT_EQUALS)//查询layNo>=30
        );
        result = (List<SysMenuTest>)queryST.execute();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(id3, result.get(0).getId());
        
        OrmTestUtil.deleteMenus(id,id2,id3);
    }
    
    
    @Test
    /**
     * 测试条件中的子查询：select * from base_menu where id in (select id from base_menu where layNo=1);
     * @throws Exception
     */
    public void testInSubQuery() throws Exception{
    	SysMenuTest menu1 = OrmTestUtil.buildSysMenu();
    	menu1.setLayNo(100);
    	OrmTestUtil.insertMenu(menu1);
    	
    	SysMenuTest menu2 = OrmTestUtil.buildSysMenu();
    	menu2.setLayNo(100);
    	OrmTestUtil.insertMenu(menu2);
    	
        Long id1 = menu1.getId();
        Long id2 = menu2.getId();
        
        
        QueryStatement queryST = new QueryStatement(
    		SysMenuTest.class, 
    		new DBCondition(
				SysMenuTest.JField.id, 
				new QueryStatement(SysMenuTest.class,new DBCondition(SysMenuTest.JField.layNo,100)).appendQueryField(SysMenuTest.JField.id),
				DBOperator.IN
    		),
    		new DBCondition(SysMenuTest.JField.id,new Long[]{id1,id2},DBOperator.IN)
        );
        List<SysMenuTest> list = (List<SysMenuTest>)queryST.execute();
        
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        
        OrmTestUtil.deleteMenus(id1,id2);
        
    }
    
    
    
}
