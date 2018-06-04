package com.xiesange.orm.test.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.test.DBParam;
import com.xiesange.orm.test.OrmTestUtil;

/**
 * 数据模型文档里按照一定格式贴到这里面,自动生成数据库表
 * @author Think
 *
 */
public class String2DBTool {
	
	
	
	public static void main(String[] args) throws Exception {
		
		DBParam param = new DBParam();
		param.setHost("localhost");
		param.setUser("root");
		param.setPassword("niubiSQL1209%");
		param.setDb("elsetravel");
		
		run(param);
		System.exit(0);
	}
	public static void run(DBParam dbParam) throws Exception{
		String tableName = getTableNameConsoleInput("请输入要生成数据库表名:",null);
		String[] tableBody = getTableBodyConsoleInput("请输入内容:",null);
		
		
		String sql = buildCreateTableSQL(tableName,tableBody);
		System.out.println(sql);
		
		Statement st = OrmTestUtil.getConnection(dbParam).createStatement();
		st.executeUpdate(sql);
		
		return;
	}
	
	
	private static String buildCreateTableSQL(String tableName,String[] tableBody) throws Exception{
    	StringBuffer sb = new StringBuffer(256);
    	sb.append("CREATE TABLE `").append(tableName).append("` (\n");
    	
    	for(int i=0;i<tableBody.length;i++){
    		String[] attrs = tableBody[i].split("\t");
    		String colName = attrs[0];
    		String colType = attrs[2];
    		boolean nullable = !"必填".equals(attrs[3]);
    		Integer length = null;
    		if(colType.startsWith("String")){
    			length = Integer.parseInt(colType.substring("String(".length(),colType.length()-1));
    			colType = "String";
    		}
    		colType = OrmTestUtil.trans2dbType(colType);
    		
    		//名称
    		sb.append("`").append(colName).append("`");
    		//类型
    		sb.append(" ").append(colType);
    		if(length != null){
    			//如果有长度则加上长度
    			sb.append("(").append(length).append(")");
    		}
    		//是否可空
    		sb.append(" ").append(nullable ? "NULL" : "NOT NULL");
    		if(i < tableBody.length-1){
    			sb.append(",\n");
    		}
    	}
    	sb.append("\n)");
    	return sb.toString();
	}
	
	/**
	 * 控制台等待用户的输入表名，用户输入信息后需要按回车键后，返回输入的信息
	 * 
	 * @author wuyujie Oct 15, 2014 9:11:27 PM
	 * @param promp
	 * @param defaultStr
	 * @return
	 * @throws IOException
	 */
	public static String getTableNameConsoleInput(String promp, String defaultStr)
			throws IOException {
		System.out.print(promp);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		if (defaultStr != null && defaultStr.length() > 0)
			System.out.print(defaultStr);
		/*String line = null;
		while (!"exit".equals(line = buffer.readLine())) {
			System.out.println("input context = " + line);
		}*/
		String tableName = buffer.readLine();
		if(tableName.indexOf("\t") > -1){
			tableName = tableName.split("\t")[1];
		}
		return tableName;
	}
	
	/**
	 * 控制台等待用户的输入表名，用户输入信息后需要按回车键后，返回输入的信息
	 * 
	 * @author wuyujie Oct 15, 2014 9:11:27 PM
	 * @param promp
	 * @param defaultStr
	 * @return
	 * @throws IOException
	 */
	public static String[] getTableBodyConsoleInput(String promp, String defaultStr)
			throws IOException {
		System.out.println(promp);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		if (defaultStr != null && defaultStr.length() > 0)
			System.out.print(defaultStr);
		String line = null;
		List<String> colStrList = new ArrayList<String>();
		while (!"".equals(line = buffer.readLine())) {
			System.out.println("input context = " + line);
			colStrList.add(line);
			if(NullUtil.isEmpty(line)){
				break;
			}
		}
		return colStrList.toArray(new String[colStrList.size()]);
	}
	
}
