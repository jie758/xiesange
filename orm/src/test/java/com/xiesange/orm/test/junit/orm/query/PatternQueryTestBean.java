package com.xiesange.orm.test.junit.orm.query;

/**
 * 验证预构模式的查询操作
 * @Description
 * @author wuyj
 * @Date 2013-11-2
 */
public class PatternQueryTestBean
{/*
    private DBClient dbClient;
    
    @Before
    public void initJDBC() throws ClassNotFoundException{
        dbClient = new MySQLClient("localhost","root",null,"wuyj_db");
    }
    
    @Test
    public void testQueryByCondititon() throws Exception{
        SysMenu menu1 = createSysMenu();
        InsertInvoker.insert(menu1);
        
        SysMenu menu2 = createSysMenu();
        InsertInvoker.insert(menu2);
        
        SysMenu menu3 = createSysMenu();
        InsertInvoker.insert(menu3);
        
        PatternStatement pst = QueryInvoker.compile(SysMenu.class,SysMenu.JField.id);
        List<SysMenu> list1 = pst.query(menu1.getId());
        List<SysMenu> list2 = pst.query(menu2.getId());
        List<SysMenu> list3 = pst.query(menu3.getId());
        
        
        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(menu1.getMemo(), list1.get(0).getMemo());
        
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals(menu2.getMemo(), list2.get(0).getMemo());
        
        Assert.assertEquals(1, list3.size());
        Assert.assertEquals(menu3.getMemo(), list3.get(0).getMemo());
        
        
        
        DeleteInvoker.deleteById(SysMenu.class, menu1.getId());
        DeleteInvoker.deleteById(SysMenu.class, menu2.getId());
        DeleteInvoker.deleteById(SysMenu.class, menu3.getId());
    }
    
    
    @Test
    public void testQueryById() throws Exception{
        SysMenu menu1 = createSysMenu();
        InsertInvoker.insert(menu1);
        
        SysMenu menu2 = createSysMenu();
        InsertInvoker.insert(menu2);
        
        SysMenu menu3 = createSysMenu();
        InsertInvoker.insert(menu3);
        
        PatternStatement pst = QueryInvoker.compileById(SysMenu.class);
        List<SysMenu> list1 = pst.query(menu1.getId());
        List<SysMenu> list2 = pst.query(menu2.getId());
        List<SysMenu> list3 = pst.query(menu3.getId());
        
        
        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(menu1.getMemo(), list1.get(0).getMemo());
        
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals(menu2.getMemo(), list2.get(0).getMemo());
        
        Assert.assertEquals(1, list3.size());
        Assert.assertEquals(menu3.getMemo(), list3.get(0).getMemo());
        
        
        
        DeleteInvoker.deleteById(SysMenu.class, menu1.getId());
        DeleteInvoker.deleteById(SysMenu.class, menu2.getId());
        DeleteInvoker.deleteById(SysMenu.class, menu3.getId());
    }
    
    
    
    private SysMenu createSysMenu() throws Exception{
        SysMenu menu = new SysMenu();
        Long id = System.currentTimeMillis();
        menu.setId(id);
        menu.setLayStr("0-99999-"+id);
        menu.setIsLeaf((short)1);
        String memo = "This is insert by QueryTestBean,insert_time="+id;
        menu.setMemo(memo);
        menu.setName("Test Menu");
        menu.setParentId(9999999L);
        menu.setUrl("test/test.jsp");
        return menu;
    }
*/}
