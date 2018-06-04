package com.xiesange.orm.test;

import java.sql.Connection;
import java.sql.DriverManager;

import com.xiesange.core.util.DateUtil;
import com.xiesange.orm.pojo.NoTimeDate;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.delete.DeleteStatement;
import com.xiesange.orm.statement.insert.InsertStatement;
import com.xiesange.orm.test.entity.SysMenuTest;

public class OrmTestUtil {
	public static final String DBTYPE_VARCHAR = "VARCHAR";
    public static final String DBTYPE_TEXT = "TEXT";
    
    public static final String DBTYPE_BIT = "BIT";
    public static final String DBTYPE_TINYINT = "TINYINT";
    public static final String DBTYPE_SMALLINT = "SMALLINT";
    public static final String DBTYPE_MEDIUMINT = "MEDIUMINT";
    public static final String DBTYPE_INT = "INT";
    public static final String DBTYPE_BIGINT = "BIGINT";
    
    public static final String DBTYPE_FLOAT = "FLOAT";
    
    public static final String DBTYPE_DATE = "DATE";
    public static final String DBTYPE_DATETIME = "DATETIME";
    
	public static Connection getConnection(DBParam param) throws Exception {
		Class.forName("com.mysql.jdbc.Driver"); // 加载mysq驱动
		String url = "jdbc:mysql://"+param.getHost()+"/"+param.getDb()+"?useUnicode=true&&characterEncoding=gb2312&autoReconnect = true";// 简单写法：url
		return DriverManager.getConnection(url, param.getUser(), param.getPassword());
	}
	public static String trans2dbType(String type) throws Exception
    {
        if("Long".equalsIgnoreCase(type)){
            return "BIGINT";
        }else if("String".equalsIgnoreCase(type)){
            return "VARCHAR";
        }else if("Int".equalsIgnoreCase(type) || "Integer".equalsIgnoreCase(type)){
            return "INT";
        }else if("Short".equalsIgnoreCase(type)){
            return "TINYINT";
        }else if("DATETIME".equalsIgnoreCase(type)){
            return "DATETIME";
        }else if("DATE".equalsIgnoreCase(type)){
            return "DATE";
        }/*else if(DBTYPE_DATE.equalsIgnoreCase(dbType)){
            return DateNoTime.class.getName();//无论有没有时间精度的日期在java侧都是Date类型，只是从数据库查询带有时间精度的字段的时候要用getTimestamp()，而无精度的日期用getDate()
        }*/
        throw new Exception("Can not match type["+type+"] to database type!");
    }
	
	public static String dbType2JavaType(String dbType,Integer length) throws Exception
    {
        if(DBTYPE_VARCHAR.equalsIgnoreCase(dbType)){
            return "String";
        }else if(DBTYPE_TINYINT.equalsIgnoreCase(dbType)){
            return "Short";
        }else if(DBTYPE_SMALLINT.equalsIgnoreCase(dbType)){
            return "Short";
        }else if(DBTYPE_MEDIUMINT.equalsIgnoreCase(dbType)){
            return "Integer";
        }else if(DBTYPE_INT.equalsIgnoreCase(dbType)){
            return "Integer";
        }else if(DBTYPE_BIGINT.equalsIgnoreCase(dbType)){
            return "Long";
        }else if(DBTYPE_BIT.equalsIgnoreCase(dbType)){
            return "Short";
        }else if(DBTYPE_TEXT.equalsIgnoreCase(dbType)){
            return "String";
        }else if(DBTYPE_FLOAT.equalsIgnoreCase(dbType)){
            return "Float";
        }else if(DBTYPE_DATETIME.equalsIgnoreCase(dbType) || DBTYPE_DATE.equalsIgnoreCase(dbType)){
            return "java.util.Date";//无论有没有时间精度的日期在java侧都是Date类型，只是从数据库查询带有时间精度的字段的时候要用getTimestamp()，而无精度的日期用getDate()
        }/*else if(DBTYPE_DATE.equalsIgnoreCase(dbType)){
            return NoTimeDate.class.getName();//无论有没有时间精度的日期在java侧都是Date类型，只是从数据库查询带有时间精度的字段的时候要用getTimestamp()，而无精度的日期用getDate()
        }*/
        throw new Exception("Can not match database type["+dbType+"] to java type!");
    }
	
	/*public static BaseDBClient createDBClient() throws Exception{
		DBClientParam param = new DBClientParam();
		param.setHost("localhost");
		param.setPort(3306);
		param.setSid("kingwant_erp");
		param.setUser("root");
		param.setPassword("niubiSQL1209%");
		param.setInitialSize(10);
		param.setMinIdle(10);
		param.setMaxIdle(10);
		param.setMaxActive(10);
		
		SequenceManagerParam sequenceParam = new SequenceManagerParam();
		sequenceParam.setSequenceClass(TestTableSequenceManager.class);
		sequenceParam.setInitValue(10000L);
		
		BaseDBClient dbClient = new MysqlDBClient(param,sequenceParam);
		
		return dbClient;
	}*/
	
	public static SysMenuTest buildSysMenu() {
		Long id = System.currentTimeMillis();
		SysMenuTest menu = new SysMenuTest();
		menu.setId(id);
		menu.setLayStr("0-99999");
		menu.setLayNo(1);
		menu.setIsLeaf((short) 1);
		String memo = "This is insert by InsertTestBean.testInsert,random="
				+ System.currentTimeMillis();
		menu.setMemo(memo);
		menu.setName("Test Menu-"+id);
		menu.setParentId(9999999L);
		menu.setUrl("test/test.jsp");
		menu.setTenantId(99999L);
		menu.setCreateDate(DateUtil.now());
		return menu;
		
	}
	
	 public static SysMenuTest insertMenu(SysMenuTest menu) throws Exception{
    	if(menu == null)
    		menu = OrmTestUtil.buildSysMenu();
    	new InsertStatement(menu).execute();
    	return menu;
	 }
    
	 public static void deleteMenus(Long... ids) throws Exception{
    	if(ids.length == 1)
    		new DeleteStatement(SysMenuTest.class,ids[0]).execute();
    	else
    		new DeleteStatement(SysMenuTest.class, new DBCondition(SysMenuTest.JField.id,ids,DBOperator.IN)).execute();
    }
}
