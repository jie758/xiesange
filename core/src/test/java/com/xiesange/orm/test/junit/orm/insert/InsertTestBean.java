package com.xiesange.orm.test.junit.orm.insert;

import junit.framework.Assert;

import org.junit.Test;

import com.xiesange.orm.statement.delete.DeleteStatement;
import com.xiesange.orm.statement.insert.InsertStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.test.OrmTestUtil;
import com.xiesange.orm.test.entity.SysMenuTest;
import com.xiesange.orm.test.junit.orm.BaseTestBean;

/**
 * 测试insert相关操作
 * @Description
 * @author wuyj
 * @Date 2013-10-26
 */
public class InsertTestBean extends BaseTestBean
{
    @Test
    public void testInsert() throws Exception{
    	SysMenuTest menu = OrmTestUtil.buildSysMenu();
        Long id = System.currentTimeMillis();
        menu.setId(id);
        menu.setLayStr("0-99999");
        menu.setIsLeaf((short)1);
        String memo = "This is insert by InsertTestBean.testInsert,random="+System.currentTimeMillis();
        menu.setMemo(memo);
        menu.setName("Test Menu");
        menu.setParentId(9999999L);
        menu.setUrl("test/test.jsp");
        
        new InsertStatement(menu).execute();
       
        
        SysMenuTest queryEntity = (SysMenuTest)new QueryStatement(menu.getClass(),id).execute();
        
        Assert.assertNotNull(queryEntity);
        Assert.assertEquals(memo, queryEntity.getMemo());
        
        
        new DeleteStatement(SysMenuTest.class,id).execute();
        
    }
}
