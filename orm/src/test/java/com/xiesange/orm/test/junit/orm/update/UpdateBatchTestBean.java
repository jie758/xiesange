package com.xiesange.orm.test.junit.orm.update;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.insert.InsertBatchStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateBatchStatement;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

public class UpdateBatchTestBean extends BaseTestBean
{
    @Test
    public void testUpdateBatchByCondition() throws Exception{
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
        
        SysMenuTest valueEntity1 = new SysMenuTest();
        String memo1 = "updated by batch 1/3: "+System.currentTimeMillis();
        valueEntity1.setIsLeaf((short)11);
        valueEntity1.setMemo(memo1);
        Thread.sleep(100);
        
        SysMenuTest valueEntity2 = new SysMenuTest();
        String memo2 = "updated by batch 2/3 : "+System.currentTimeMillis();
        valueEntity2.setMemo(memo2);
        valueEntity2.setIsLeaf((short)22);
        Thread.sleep(100);
        
        
        SysMenuTest valueEntity3 = new SysMenuTest();
        String memo3 = "updated by batch 3/3: "+System.currentTimeMillis();
        valueEntity3.setMemo(memo3);
        valueEntity3.setIsLeaf((short)33);
        
        UpdateBatchStatement updateSt = new UpdateBatchStatement(
    		new UpdateStatement(
    			valueEntity1,
				new DBCondition(SysMenuTest.JField.name,menu1.getName()),
				new DBCondition(SysMenuTest.JField.url,menu1.getUrl())
    		),
    		new UpdateStatement(
				valueEntity2,
				new DBCondition(SysMenuTest.JField.name,menu2.getName()),
				new DBCondition(SysMenuTest.JField.url,menu2.getUrl())
    		),
    		new UpdateStatement(
				valueEntity3,
				new DBCondition(SysMenuTest.JField.name,menu3.getName()),
				new DBCondition(SysMenuTest.JField.url,menu3.getUrl())
    		)
        );
        
        updateSt.execute();
        
        
        SysMenuTest new_menu1 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu1.getId()).execute();
        SysMenuTest new_menu2 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu2.getId()).execute();
        SysMenuTest new_menu3 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu3.getId()).execute();
        
        Assert.assertEquals(memo1, new_menu1.getMemo());
        Assert.assertEquals(memo2, new_menu2.getMemo());
        Assert.assertEquals(memo3, new_menu3.getMemo());
        
        Assert.assertEquals(menu1.getParentId(), new_menu1.getParentId());
        Assert.assertEquals(menu2.getParentId(), new_menu2.getParentId());
        Assert.assertEquals(menu3.getParentId(), new_menu3.getParentId());
        
        OrmTestUtil.deleteMenus(menu1.getId(),menu2.getId(),menu3.getId());
        
    }
    
    
    @Test
    public void testUpdateBatchById() throws Exception{
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
        
        SysMenuTest valueEntity1 = new SysMenuTest();
        String memo1 = "updated by batch 1/3: "+System.currentTimeMillis();
        valueEntity1.setMemo(memo1);
        valueEntity1.setIsLeaf((short)11);
        Thread.sleep(100);
        
        SysMenuTest valueEntity2 = new SysMenuTest();
        String memo2 = "updated by batch 2/3 : "+System.currentTimeMillis();
        valueEntity2.setMemo(memo2);
        valueEntity2.setIsLeaf((short)22);
        Thread.sleep(100);
        
        
        SysMenuTest valueEntity3 = new SysMenuTest();
        String memo3 = "updated by batch 3/3: "+System.currentTimeMillis();
        valueEntity3.setMemo(memo3);
        valueEntity3.setIsLeaf((short)33);
        
        UpdateBatchStatement updateSt = new UpdateBatchStatement(
    		new UpdateStatement(
    			valueEntity1,
    			menu1.getId()
    		),
    		new UpdateStatement(
				valueEntity2,
				menu2.getId()
    		),
    		new UpdateStatement(
				valueEntity3,
				menu3.getId()
    		)
        );
        
        updateSt.execute();
        
        
        SysMenuTest new_menu1 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu1.getId()).execute();
        SysMenuTest new_menu2 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu2.getId()).execute();
        SysMenuTest new_menu3 = (SysMenuTest)new QueryStatement(SysMenuTest.class,menu3.getId()).execute();
        
        Assert.assertEquals(memo1, new_menu1.getMemo());
        Assert.assertEquals(memo2, new_menu2.getMemo());
        Assert.assertEquals(memo3, new_menu3.getMemo());
        
        Assert.assertEquals(menu1.getParentId(), new_menu1.getParentId());
        Assert.assertEquals(menu2.getParentId(), new_menu2.getParentId());
        Assert.assertEquals(menu3.getParentId(), new_menu3.getParentId());
        
        OrmTestUtil.deleteMenus(menu1.getId(),menu2.getId(),menu3.getId());
    }
    
}
