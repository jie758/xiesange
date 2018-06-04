package com.xiesange.orm.test.mybatis;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.xiesange.core.util.LogUtil;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.test.entity.User;

public class MybatisTest {
	private static SqlSessionFactory sqlSessionFactory;  
    private static Reader reader;  
  
    static {  
        try {  
            // 最终实现了从配置文件中配置工厂的初始化  
            reader = Resources.getResourceAsReader("mybatis_config.xml");  
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static SqlSessionFactory getSession() {  
        return sqlSessionFactory;  
    }  
  
      
    public static void main(String[] args) {      
    	 SqlSession session = sqlSessionFactory.openSession();
         try {
        	 ICommonDao dao = session.getMapper(ICommonDao.class);
        	 
        	 HashMap map = new HashMap();
        	 map.put("table_name", "user");
        	 map.put("query_fields", "id,wechat");
        	 map.put("order_fields", "id desc");
        	 map.put("conditions", "id = 2");
        	 
        	 List<DBCondition> conds = new ArrayList<DBCondition>();
        	 DBCondition cond = new DBCondition(User.JField.id,10001L);
        	 conds.add(cond);
        	 
        	 cond = new DBCondition(User.JField.bankCard,"2222");
        	 conds.add(cond);
        	 
        	 map.put("conditions", conds);
        	 
        	 List<HashMap<String,Object>> user = (List<HashMap<String,Object>>)dao.query(map);
        	 
        	 //HashMap user = session.selectOne("com.elsetravel.IUserOperation.select", 10044L);
	         //System.out.println(user.get(key));
        	 LogUtil.dump("user", user);
         } finally {
        	 session.close();
         }            
    } 
}
