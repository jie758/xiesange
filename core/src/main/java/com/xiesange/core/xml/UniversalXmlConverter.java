package com.xiesange.core.xml;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
/**
 * @Description: 通用xml转换器，不需要固定定义java类，会都转成BaseNode              
  * @Author wuyj                                                                                                                                                                                                                                                                           
 */
public class UniversalXmlConverter implements Converter {
	private static Logger logger = LogUtil.getLogger(UniversalXmlConverter.class);
    private Class<? extends BaseNode> userversalClass;
    
    public UniversalXmlConverter(){
        this.userversalClass = BaseNode.class;
    }
    
    public UniversalXmlConverter(Class<? extends BaseNode> clz){
        this.userversalClass = clz;
    }
    
    
	/**
	 * 允许转换的节点
	 */
	public boolean canConvert(Class type) {
		return (userversalClass.isAssignableFrom(type));
	}
	
	/**
	 * 把对象转换成流
	 */
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		BaseNode root = (BaseNode)source;
		
		writeAttributes(root,writer);
		
		marshalChildren(root.getChildren(),writer);
		
	}
	
	public void marshalChildren(List<BaseNode> children,HierarchicalStreamWriter writer){
		if(NullUtil.isEmpty(children))
			return;
		for(BaseNode child : children){
			writer.startNode(child.getTagName());
			
			writeAttributes(child,writer);
			
			//2012-03-03 wuyujie : 修复没有marshal的时候，没有输出节点对应的text内容
			if(NullUtil.isNotEmpty(child.getChildren())){
				//有子节点就直接处理子节点，忽略当前节点的text
				marshalChildren(child.getChildren(),writer);
			}else if(NullUtil.isNotEmpty(child.getText())){
				//无子节点且有text值，则输出内容
				writer.setValue(child.getText());
			}
			writer.endNode();
		}
		
	}
	public void writeAttributes(BaseNode node,HierarchicalStreamWriter writer){
		Map<String,String> map = node.getAttributes();
		if(NullUtil.isEmpty(map))
			return;
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = map.get(key);
			/*if(key.equals("header")){
				String aa = map.get(key);
				System.out.println(aa);
			}
			if(NullUtil.isNotEmpty(value)){
				value = value.replaceAll("\"", "\\\\\"");
			}*/
			writer.addAttribute(key, value);
		}
	}
	
	/**
	 * 流转换成对象
	 */
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String tagName = reader.getNodeName();
		BaseNode node = getUnmarshalObject(tagName,userversalClass);
		
		Map<String,String> attrs = readAttributes(reader);
		node.setAttributes(attrs);
		
		List<BaseNode> list = unmarshalChild(reader);
		node.addChildren(list);
		
		node.setText(reader.getValue());
		
		return node;
	}
	
	private List<BaseNode> unmarshalChild(HierarchicalStreamReader reader){
		List<BaseNode> result = new ArrayList<BaseNode>();
		while(reader.hasMoreChildren()){
			reader.moveDown();
			BaseNode child = getUnmarshalObject(reader.getNodeName(),userversalClass);
			Map<String,String> attrs = readAttributes(reader);
			child.setAttributes(attrs);
			
			List children = unmarshalChild(reader);
    		if(children != null && children.size() > 0){
    			child.addChildren(children);
    		}
    		
    		child.setText(reader.getValue());
    		
    		result.add(child);
			
    		reader.moveUp();
		}
		return result;
	}
	
	private static BaseNode getUnmarshalObject(String tagName,Class<? extends BaseNode> clz){
	    BaseNode node = null;
        if(clz == BaseNode.class){
            node = new BaseNode(tagName);
        }else{
            try{
                Constructor<? extends BaseNode> con = clz.getConstructor(String.class);
                node = (BaseNode)con.newInstance(tagName);
            }catch(Exception e){
                logger.error(e, e);
            }
        }
        return node;
	}
	
	private static Map<String,String> readAttributes(HierarchicalStreamReader reader){
		Map<String,String> map = new HashMap<String,String>();
		Iterator it = reader.getAttributeNames();
		while(it.hasNext()){
			String attrName = (String)it.next();
			String value = reader.getAttribute(attrName);
			map.put(attrName, value);
		}
		return map;
	}
	
}
