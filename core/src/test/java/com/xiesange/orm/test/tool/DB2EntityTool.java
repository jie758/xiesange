package com.xiesange.orm.test.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.test.DBParam;
import com.xiesange.orm.test.OrmTestUtil;


/**
 * 从数据库表生成java实体的工具类。使用步骤： 1、采用main函数的运行方式
 * 2、控制台(console)会有提示，在控制台里输入需要生成实体的表名，可以同时生成多张表.
 * 3、输入格式：表名1:需要存放的包目录,表名2:需要存放的包目录
 * ,表名3:需要存放的包目录,即表名和目录之间用;分隔，多个表名之间用,分隔，存放的包目录也可以不输入，默认截取表名的_之前的前缀 4、输入完毕后按回车。
 * 例如： 输入：sys_menu:sysmenu,sys_user,busi_order，然后回车，那么会生成三个数据库实体：
 * com.sjm.gen.dbentity.sysmenu.SysMenu,因为输入了包目录sysmenu
 * com.sjm.gen.dbentity.sys.SysUser，因为没有输入包目录，所以包目录默认为sys_user的前缀sys
 * com.sjm.gen.dbentity.busi.BusiOrder，因为没有输入包目录，所以包目录默认为busi_order的前缀busi
 * 注意，所有的数据库实体都是在com.sjm.gen.dbentity包下。
 * 4、如果要生成全部表，那么控制台输入的时候请输:*
 * 
 * @Description
 * @author wuyj
 * @Date 2013-10-24
 */
public class DB2EntityTool {
	
	
	public static void main(String[] args) throws Exception {
		run(DB2EntityTool.class,null,NullUtil.isNotEmpty(args) ? args[0]:null,null);
	}

	public static void run(Class<?> mainClz,DBParam dbParam,String entityPackage,String ormPackage) throws Exception {
		if(NullUtil.isEmpty(entityPackage)){
			throw new Exception("生成的实体包名没有指定！");
		}
		if(NullUtil.isEmpty(ormPackage)){
			throw new Exception("orm包名没有指定！");
		}
		String frameworkPath = getProjectPath(mainClz) + "src/main/java";// +
																	// javaPackageRoot.replace(".",
		Connection connection = OrmTestUtil.getConnection(dbParam);														// "\\\\");
		System.out.println("存放的framework目录:" + frameworkPath);
		String tableName = getConsoleInput("请输入表名和要生成的目录，两者以:分隔，同时生成多个实体以,分隔:",null);
		String[] tables = null;
		
		
		if(tableName.equals("*")){
			tables = queryAllTables(connection,dbParam.getDb());
		}else{
			tables = tableName.split(",");
		}
		

		// 初始化velocity模板引擎
		VelocityEngine ve = initVelocity();

		// 取得velocity的上下文context
		VelocityContext context = new VelocityContext();
		context.put("framework_path", frameworkPath);

		String genPath = "";
		
		for (String table : tables) {
			table = table.toLowerCase();
			String filePath = genEntity(entityPackage,ormPackage,table, ve, context, dbParam,connection);
			genPath += filePath.substring(filePath.lastIndexOf("\\")+1)+ " -- "+filePath + "\n";
		}

		connection.close();
		System.out.println("**** execute successfully!!!");
		System.out.println("**** "+genPath);
	}

	private static String[] queryAllTables(Connection connection,String dbName) throws SQLException{
		ResultSet rs = connection.createStatement().executeQuery("select * from information_schema.tables where table_schema='"+dbName+"'");
		List<String> tables = new ArrayList<String>();
		while(rs.next()){
			String table = rs.getString("TABLE_NAME");
			tables.add(table);
		}
		return tables.toArray(new String[tables.size()]);
	}
	
	private static String genEntity(String entityPackage,String ormPackage,String tableName, VelocityEngine ve,
			VelocityContext context, DBParam param,Connection connection) throws Exception {
		String[] items = tableName.split(":");
		tableName = items[0];
		String entityDir = null;
		if (items.length == 2) {
			entityDir = items[1];
		} else {
			entityDir = tableName.split("_")[0];
		}
		// 把数据填入上下文
		String className = parse2JavaName(tableName.toLowerCase(), true);
		context.put("class_name", className);
		context.put("table_name", tableName.toUpperCase());
		context.put("package_name", entityPackage
				+ (entityDir == null ? "" : "." + entityDir));
		context.put("orm_package_name", ormPackage);

		String primaryKey = getPrimaryKey(param, connection, tableName);
		String indexes = getIndex(param, connection, tableName);

		context.put("primaryKey", primaryKey == null ? "" : primaryKey);
		context.put("indexes", indexes == null ? "" : indexes);
		// context.put("render_path",
		// renderPackageRoot+(NullUtil.isEmpty(entityDir)?"":"/dbentity/"+entityDir+"/"+className)+"/"+className+"_Main_Render.xml");
		List<Map> fields = buildEntityFieldParam(tableName, param,connection);
		context.put("field_list", fields);
		if (fields == null || fields.size() == 0) {
			throw new Exception("数据库表[" + tableName + "]不存在任何字段，请检查数据库中是否存在该表！");
		} else {
			return generateDBEntity(ve, context, "db2entity.vm");
		}

	}

	private static String getPrimaryKey(DBParam param,Connection connection, String tableName) throws SQLException {
		ResultSet pkRSet = connection.getMetaData().getPrimaryKeys(param.getDb(), null, tableName);
		while (pkRSet.next()) {
			return pkRSet.getString("COLUMN_NAME");
		}
		return null;
	};

	private static String getIndex(DBParam param,Connection conn,String tableName) throws SQLException{
    	ResultSet rs = conn.getMetaData().getIndexInfo(param.getDb(), null, tableName, false, true);
        
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();//里面每个元素Map代表一个索引,map中有属性name、col_names、type
        while(rs.next()){
        	String indexName = rs.getString("INDEX_NAME");
        	if(indexName.equalsIgnoreCase("Primary"))
        		continue;//主键过滤掉
        	String colName = rs.getString("COLUMN_NAME");
        	String type = rs.getString("TYPE");
        	
        	Map indexMap = null;
        	if(list != null && list.size() > 0){
        		for(Map<String,Object> index : list){
        			if(indexName.equals(index.get("name"))){
        				indexMap = index;//之前已经创建了这个索引对象
        				break;
        			}
        		}
        	}
        	
        	List<String> col_names = null;
        	if(indexMap == null){
        		indexMap = new HashMap<String,Object>();
        		indexMap.put("name", indexName);
        		list.add(indexMap);
        	}else{
        		col_names = (List<String>)indexMap.get("col_names");
        	}
        	
        	if(col_names == null){
        		col_names = new ArrayList<String>();
        		indexMap.put("col_names", col_names);
        	}
        	col_names.add(colName);
        }
        
        
        //要把索引转成特殊形式：{{},{}}
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<list.size();i++){
        	if(i > 0){
				sb.append(";");
			}
        	Map<String,Object> indexMap = list.get(i);
        	sb.append(indexMap.get("name")).append("(");
        	List<String> col_names = (List<String>)indexMap.get("col_names");
        	if(col_names != null && col_names.size() > 0){
        		for(int k=0;k<col_names.size();k++){
        			if(k > 0){
        				sb.append(",");
        			}
        			sb.append(col_names.get(k));
        		}
        	}
        	sb.append(")");
        }
        
        return sb.toString();
    };

	private static VelocityEngine initVelocity() {
		// 初始化并取得Velocity引擎
		VelocityEngine ve = new VelocityEngine();
		// 设置vm模板从classpath下加载
		Properties p = new Properties();
		p.put("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");   
		ve.init(p);
		return ve;
	}

	private static List<Map> buildEntityFieldParam(String tableName,DBParam param, Connection conn) throws Exception {
		List<Map> field_list = new ArrayList<Map>();

		ResultSet resultSet = conn.getMetaData().getColumns(null, null,tableName, "%");

		while (resultSet.next()) {
			String colName = resultSet.getString("COLUMN_NAME");
			int colLength = resultSet.getInt("COLUMN_SIZE");
			String colType = resultSet.getString("TYPE_NAME");
			Boolean nullable = resultSet.getBoolean("NULLABLE");
			String remark = resultSet.getString("REMARKS");

			String javaName = parse2JavaName(colName.toLowerCase(), false);
			String methodName = parse2JavaName(colName.toLowerCase(), true);
			// System.out.println(javaName + "," + colType + "," + colLength);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("col_name", colName.toUpperCase());
			map.put("java_name", javaName);
			map.put("method_name", methodName);
			map.put("java_type", OrmTestUtil.dbType2JavaType(colType, colLength));
			map.put("col_type", colType);
			map.put("col_length", colLength);
			map.put("nullable", nullable);
			map.put("comment", NullUtil.isEmpty(remark) ? "" : "//" + remark);
			field_list.add(map);
		}

		// conn.close();
		return field_list;
	}

	private static String generateDBEntity(VelocityEngine ve,
			VelocityContext context, String vmPath) throws Exception {
		String packageName = (String) context.get("package_name");
		String className = (String) context.get("class_name");

		// 取得velocity的模版
		Template t = ve.getTemplate(vmPath);
		StringWriter writer = new StringWriter();
		// 转换输出
		t.merge(context, writer);

		String frameworkPath = context.get("framework_path") + "\\"
				+ packageName.replace(".", "\\\\");

		File folder = new File(frameworkPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		String filePath = frameworkPath + "\\" + className + ".java";
		BufferedWriter output = new BufferedWriter(new FileWriter(filePath));

		output.write(writer.toString());
		output.flush();
		output.close();
		System.out.println(writer.toString());

		return filePath;
	}

	private static String getProjectPath(Class<?> clz)
			throws FileNotFoundException, IOException, URISyntaxException {
		String path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		return path.substring(0, path.lastIndexOf("target"));
	}

	

	/**
	 * 控制台等待用户的输入，用户输入信息后按回车键后，返回输入的信息
	 * 
	 * @author wuyujie Oct 15, 2014 9:11:27 PM
	 * @param promp
	 * @param defaultStr
	 * @return
	 * @throws IOException
	 */
	public static String getConsoleInput(String promp, String defaultStr)
			throws IOException {
		System.out.print(promp);
		BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
		if (defaultStr != null && defaultStr.length() > 0)
			System.out.print(defaultStr);
		return strin.readLine();
	}

	/**
	 * 把一个名称转成java的驼峰式名称。比如传入一个menu_id,会被转成MenuId或者menuId
	 * 
	 * @author wuyj 2013-10-24
	 * @param name
	 *            ,传入的名称
	 * @param isFirstUpper
	 *            ,首字母是否转成大写
	 * @return
	 */
	public static String parse2JavaName(String name, boolean isFirstUpper) {
		String[] arr = name.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			String item = arr[i];
			String firstWord = item.substring(0, 1);
			String leftWords = item.substring(1);
			if (i == 0) {
				firstWord = (isFirstUpper) ? firstWord.toUpperCase()
						: firstWord.toLowerCase();
			} else {
				firstWord = firstWord.toUpperCase();
			}

			sb.append(firstWord).append(leftWords);

		}
		return sb.toString();
	}
	
	
	
}
