package com.xiesange.core.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.NullUtil;
/**
 * @Description: Xml节点的基类，包装了从xml串解析后的节点 的所有api方法          
  * @Author wuyj                                                                                                                                                                                                                                                                           
 */
public class BaseNode implements net.sf.cglib.proxy.Factory{
	private String text;//节点的文本内容
	
	private List<BaseNode> children;//存放子节点
	
	private Map<String,String> attributes;//存放当前属性
	
	private String tagName;//节点标签名称
	
	private BaseNode parent;//父节点
	
	public static void main(String[] args) throws Exception
    {
        //XStreamHolder holder = new XStreamHolder("root",PageBaseNode.class);
        //holder.registerConverter(new UniversalXmlConverter(PageBaseNode.class));
        
        //BaseNode node =  (BaseNode)holder.parseFromStream(SjmUtil.class.getResourceAsStream(PageDefine.login.getPath()));
    }
	
	public BaseNode(String tagName){
		this.tagName = tagName;
	}

	public <T extends BaseNode>List<T> getChildren() {
		return (List<T>)children;
	}

	public void setChildren(List<? extends BaseNode> children) {
		if(NullUtil.isEmpty(children)){
			this.children = null;
		}else{
			if(this.children == null){
				this.children = new ArrayList<BaseNode>();
			}else{
				this.children.clear();
			}
			this.children.addAll(children);
		}
	}
	
	public void addChildren(List<BaseNode> children) {
		addChildren(children,-1);
	}
	public void addChildren(List<BaseNode> children,int index) {
		for(int i=0;i< children.size();i++){
			BaseNode child = children.get(i);
			addChild(child,index);
		}
		
	}
	/**
	 * 提出出该方法主要是用于让Step重载，因为Step的children数量固定
	 * @param child
	 * @param index
	 */
	public void addChild(BaseNode child,int index){
		if(children == null)
			children = new ArrayList<BaseNode>();
		if(index == -1){
			children.add(child);
		}else{
			children.add(index, child);
		}
		/*if(child.getParent() != null){
			child.getParent().getChildren().remove(child);
		}*/
		//child.setParent(this);
	}
	public void addChild(BaseNode child){
		addChild(child,-1);
	}
	
	
	public String getTagName() {
		return tagName;
	}
	
	public void addAttribute(String key,String value){
		if(attributes == null)
			attributes = new LinkedHashMap<String,String>();
		attributes.put(key, value);
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getAttribute(String key){
		if(attributes == null)
			return null;
		return attributes.get(key);
	}
	public Integer getIntAttribute(String key){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return null;
		return Integer.valueOf(value);
	}
	public Long getLongAttribute(String key){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return null;
		return Long.valueOf(value);
	}
	public Short getShortAttribute(String key){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return null;
		return Short.valueOf(value);
	}
	public Float getFloatAttribute(String key){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return null;
		return Float.valueOf(value);
	}
	public Double getDoubleAttribute(String key){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return null;
		return Double.valueOf(value);
	}
	public Boolean getBooleanAttribute(String key,boolean defaulValue){
		String value = getAttribute(key);
		if(NullUtil.isEmpty(value))
			return defaulValue;
		return Boolean.valueOf(value);
	}
	public Boolean getBooleanAttribute(String key){
        String value = getAttribute(key);
        if(NullUtil.isEmpty(value))
            return null;
        return Boolean.valueOf(value);
    }
	public String[] getStringArrayAttribute(String key){
		return getStringArrayAttribute(key,null);
	}
	public String[] getStringArrayAttribute(String key,String splitChar){
		String value = getAttribute(key);
		if(value == null)
			return null;
		if(splitChar == null)
			splitChar = ",";
		return value.split(splitChar);
	}
	public int[] getIntArrayAttribute(String key) throws Exception{
		return getIntArrayAttribute(key,null);
	}
	public int[] getIntArrayAttribute(String key,String splitChar) throws Exception{
		String[] arr = getStringArrayAttribute(key,splitChar);
		if(arr == null)
			return null;
		return CommonUtil.parse2IntArray(arr);
	}
	
	
	
	
	public BaseNode getChildByAttribute(String attrName,String value){
		return getChildByAttribute(attrName,value,false);
	}
	/**
	 * @Description: 根据某个属性值获取符合的单个直属子节点
	  * @author : wuyj
	  * @date : 2011-10-1  
	  * @param attrName，属性名
	  * @param value,属性值
	  * @param ignoreCase,匹配时，属性值是否忽略大小写
	  * @return
	 */
	public BaseNode getChildByAttribute(String attrName,String value,boolean ignoreCase){
		if(value == null)
            return null;
        List<BaseNode> list = this.getChildren();
        if(!NullUtil.isEmpty(list)){
        	String attrValue = null;
            for(BaseNode item : list){
            	attrValue = item.getAttribute(attrName);
            	if(ignoreCase && value.equalsIgnoreCase(attrValue)){
            		return item;
            	}else if(value.equals(attrValue)){
                    return item;
                }
            }
        }
		return null;
	}
	/**
	 * @Description: 根据某个属性值获取所有符合的直属子节点
	  * @author : wuyj
	  * @date : 2011-10-1  
	  * @param attrName
	  * @param value
	  * @return
	 */
	public List<BaseNode> getChildrenByAttribute(String attrName,String value){
		if(value == null)
			return null;
		List<BaseNode> result = null;
		List<BaseNode> list = this.getChildren();
		if(!NullUtil.isEmpty(list)){
			result = new ArrayList<BaseNode>();
			for(BaseNode item : list){
				if(value.equals(item.getAttribute(attrName))){
					result.add(item);
				}
			}
		}
		return result;
	}
	
	/**
	 * @Description:根据节点标签名称获取符合的单个直属子节点
	  * @author : wuyj
	  * @date : 2011-10-1  
	  * @param tagName
	  * @return
	 */
	public BaseNode getChildByTagName(String tagName){
		List<BaseNode> result = getChildrenByTagName(tagName);
		if(NullUtil.isEmpty(result))
			return null;
		return result.get(0);
	}
	/**
	 * @Description:根据节点标签名称获取符合的所有直属子节点
	  * @author : wuyj
	  * @date : 2011-10-1  
	  * @param tagName
	  * @return
	 */
	public List<BaseNode> getChildrenByTagName(String tagName){
		if(NullUtil.isEmpty(tagName))
			return null;
		List<BaseNode> result = null;
		List<BaseNode> list = this.getChildren();
		if(!NullUtil.isEmpty(list)){
			result = new ArrayList<BaseNode>();
			for(BaseNode item : list){
				if(tagName.equalsIgnoreCase(item.getTagName())){
					result.add(item);
				}
			}
		}
		return result;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
	public boolean containsAttribute(String key){
		return attributes != null && attributes.containsKey(key);
	}

	public BaseNode getParent() {
		return parent;
	}

	public void setParent(BaseNode parent) {
		this.parent = parent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
