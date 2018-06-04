package com.xiesange.core.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.FileUtil;
/**
 * @Description: XStream的包装类
  * @Author wuyj                                                                                                                                                                                                                                                                           
 */
public class XStreamHolder {
	private XStream xstream;
	
	private String rootName;
	
	
	public static void main(String[] args){
		/*BaseNode root = new BaseNode("services");
		
		root.addAttribute("name", "wuyujie");
		root.addAttribute("sex", "F");
		
		BaseNode child = new BaseNode("service");
		child.addAttribute("name", "wuyujie1");
		child.addAttribute("sex", "FF");
		
		root.addChild(child);
		root.addChild(new BaseNode("service"));
		
		String aa = new XStreamHolder("services_11").parse2Xml(root);
		System.out.println(aa);*/
	}
	
	public XStreamHolder(){
		//重载wrapMapper方式，可以忽略到不存在节点属性
		xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("-_", "_"))){
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@Override
					public boolean shouldSerializeMember(Class definedIn,String fieldName) {
						if (definedIn == Object.class) {
							return false;
						}
						return super.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};
		xstream.autodetectAnnotations(true);  
	}
	
	public XStreamHolder(String rootName,Class clz){
	    this(rootName,clz,new XppDriver(new XmlFriendlyNameCoder("-_", "_")));
	}
	
	public XStreamHolder(String rootName,Class clz,XppDriver driver){
	    xstream = new XStream(driver);
	    xstream.autodetectAnnotations(true);  
	    this.rootName = rootName;
	    this.setAliasAndAttr(clz, rootName);
	}
	
	public void setAliasAndAttr(Class clazz,String alias){
	    setClassAlias(clazz,alias);
	    setAttributes(clazz);
	}
	
	/**
	 * @Description: 设置类别名
	  * @param clazz,需要设置的类
	  * @param alias，需要设置类对应的别名
	 */
	public void setClassAlias(Class clazz,String alias){
		xstream.alias(alias, clazz);
	}
	/**
	 * @Description:省略字段
	  * @param clazz,需要设置省略字段的类
	  * @param fieldName，需要省略的字段名
	 */
	public void setOmit(Class clazz,String fieldName){
		xstream.omitField(clazz, fieldName);
	}
	/**
	 * @Description: 设置类中的字段作为属性
	  * @param clazz,需要设置属性的类
	  * @param field,需要设置作为属性的字段名
	 */
	public void setAttribute(Class clazz,String fieldName){
		xstream.useAttributeFor(clazz, fieldName);
	}
	/**
	 * @Description:设置一个class中的所有字段都作为属性
	  * @param clazz
	 */
	public void setAttributes(Class clazz){
		Field[] fields = clazz.getDeclaredFields();
		if(fields == null || fields.length == 0)
			return;
		for(int i=0;i< fields.length;i++){
			Field field = fields[i];
			setAttribute(clazz, field.getName());
		}
	}
	
	/**
	 * @Description: 把一个Collection类型的字段显式声明
	  * @param clazz
	  * @param fieldName
	 */
	public void setImplicitCollection(Class clazz,String fieldName) throws Exception{
		xstream.addImplicitCollection(clazz, fieldName);
	}
	
	/**
	 * @Description: 注册转换器
	  * @author : wuyj
	  * @date : 2011-10-1  
	  * @param converter
	  * @throws Exception
	 */
	public void registerConverter(Converter converter) throws Exception{
		xstream.registerConverter(converter);
	}
	
	//########################具体操作方法
	public String parse2Xml(Object obj) throws Exception{
		return xstream.toXML(obj);
	}
	public Object parseFromXml(String xml) throws Exception{
		return xstream.fromXML(xml);
	}
	

	public Object parseFromFile(String filePath) throws Exception{
		try{
			InputStream is = FileUtil.getFileInputStream(filePath);
			return parseFromStream(is);
		}catch(Exception e){
			throw e;
		}
	}
	public Object parseFromResource(String filePath) throws Exception{
		try{
			InputStream is = XStreamHolder.class.getResourceAsStream(filePath);
			if(is == null){
				throw CommonUtil.buildException("找不到对应的资源:"+filePath);
			}
			return parseFromStream(is);
		}catch(Exception e){
			throw e;
		}
	}
	public Object parseFromStream(InputStream is) throws Exception{
		InputStreamReader reader = null;
		try{
			if(is.available() == 0){
				return null;
			}
			reader = new InputStreamReader(is, Charset.forName("UTF-8"));
			return xstream.fromXML(reader);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(reader != null)
					reader.close();
				if(is != null)
					is.close();
			}catch(Exception e){
				throw e;
			}
		}
	}
	
	
	public void parse2File(Object obj,String filePath) throws Exception{
		OutputStream os = null;
		OutputStreamWriter writer = null;
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file = FileUtil.createFile(filePath);
			}
			os = new FileOutputStream(file);
			writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			xstream.toXML(obj, writer);
		} catch (Exception e) {
			throw e;
		}finally{
			try{
				if(writer != null){
					writer.flush();
					writer.close();
				}
				if(os != null)
					os.close();
			}catch(Exception e){
				throw e;
			}
		}
	}

	public String getRootName() {
		return rootName;
	}

    public XStream getXstream()
    {
        return xstream;
    }
}
