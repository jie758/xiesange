package com.xiesange.core.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.xiesange.core.exception.IXSGException;

public class CommonUtil
{
    public static boolean isIn(Object item, Object[] arr)
    {
        if (NullUtil.isEmpty(arr))
            return false;
        for (int i = 0; i < arr.length; i++)
        {
        	//LogUtil.getLogger(CommonUtil.class).debug("____________:"+arr[i]);
        	if (String.valueOf(item).equals(String.valueOf(arr[i])) || item == arr[i])
                return true;
        }
        return false;
    }

    public static boolean isIn(Object item, Collection list)
    {
        if (list == null)
            return false;
        Iterator it = list.iterator();
        while(it.hasNext()){
            Object value = it.next();
            if (item.equals(value) || item == value)
                return true;
        }
        return false;
    }

    public static boolean isIn(int item, int[] arr)
    {
        if (arr == null)
            return false;
        for (int i = 0; i < arr.length; i++)
        {
            if (item == arr[i])
                return true;
        }
        return false;
    }
    
    public static boolean isIn(short item, short[] arr)
    {
        if (arr == null)
            return false;
        for (int i = 0; i < arr.length; i++)
        {
            if (item == arr[i])
                return true;
        }
        return false;
    }
    
    public static boolean isIn(long item, long[] arr)
    {
        if (arr == null)
            return false;
        for (int i = 0; i < arr.length; i++)
        {
            if (item == arr[i])
                return true;
        }
        return false;
    }
    
    /**
     * 把一个名称转成java的驼峰式名称。比如传入一个menu_id,会被转成MenuId或者menuId
     * @author wuyj 2013-10-24
     * @param name,传入的名称
     * @param isFirstUpper,首字母是否转成大写
     * @return
     */
    public static String parse2JavaName(String name, boolean isFirstUpper)
    {
        String[] arr = name.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++)
        {
            String item = arr[i];
            String firstWord = item.substring(0, 1);
            String leftWords = item.substring(1);
            if (i == 0)
            {
                firstWord = (isFirstUpper) ? firstWord.toUpperCase() : firstWord.toLowerCase();
            }
            else
            {
                firstWord = firstWord.toUpperCase();
            }

            sb.append(firstWord).append(leftWords);

        }
        return sb.toString();
    }
    
    /**
     * 把数组转换成List
     * @author wuyj 2013-11-1
     * @param arr
     * @return
     */
    public static <T>List<T> parseArray2List(T[] arr)
    {
        if (arr == null)
            return null;
        List<T> result = new ArrayList<T>();
        for (T item : arr)
        {
            result.add(item);
        }
        return result;
    }
    /**
     * 把一个Object数组转成int数组
     * @author wuyj 2013-11-27
     * @param arr
     * @return
     * @throws Exception
     */
    public static int[] parse2IntArray(Object[] arr) throws Exception
    {
        if (arr == null || arr.length == 0)
            return null;
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            result[i] = Integer.parseInt(arr[i].toString());
        }
        return result;
    }
    
    
    /**
     * 加载properties文件。
     * 
     * @author wuyujie Sep 25, 2014 2:25:12 PM
     * @param path,该路径必须是在classpath下，且以/开头
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Properties loadProperties(String path) throws FileNotFoundException, IOException{
    	Properties prop = new Properties();
    	prop.load(CommonUtil.class.getResourceAsStream(path));
    	return prop;
    }
    
    /**
     * 根据分隔符，截取固定数量的元素
     * @param str,要截取的字符串
     * @param seprator，分隔符
     * @param count，需要截取的个数，从左到右
     * @returns
     * var str = "A:B:C:D";
     * str.split(":");返回的是数组：["A","B","C","D"]
     * split(str,":",2)；返回的是数组:["A","B:C:D"]，因为参数2代表从左到右只截取两个元素
     * split(str,":",5)；返回的是数组:["A","B","C","D",null]，参数5超出了所有的元素，因此剩余的就是null
     */
    public static String[] split(String str,String seprator,int count){
    	int index;
    	String[] result = new String[count];
    	for(int i=0;i<count;i++){
    		index = str.indexOf(seprator);
    		if(index < 0){
    			result[i] = str;
    			str = null;
    			break;
    		}
    		result[i] = str.substring(0, index);
    		str = str.substring(index+1);
    	}
    	if(NullUtil.isNotEmpty(str)){
    		result[result.length-1] += seprator+str;
    	}
    	return result;
    }
    /**
     * 合并数组中的元素，用指定的连接符连接，并返回最终的字符串
     * @author wuyujie Dec 9, 2014 11:49:13 AM
     * @param arr
     * @return
     */
    public static <T>String joinBySeperator(char seprator,T... items){
    	StringBuffer sb = new StringBuffer();
    	for(Object item : items){
    		if(sb.length() > 0){
    			sb.append(seprator);
    		}
    		sb.append(item == null ? "" : String.valueOf(item));
    	}
    	return sb.toString();
    }
    public static String join(Object... items){
    	if(NullUtil.isEmpty(items))
    		return null;
    	StringBuffer sb = new StringBuffer();
    	for(int i=0;i<items.length;i++){
    		sb.append(items[i] == null ? "" : String.valueOf(items[i]));
    	}
    	return sb.toString();
    }
    public static String joinBySeperator(char seprator,List<?> items){
    	StringBuffer sb = new StringBuffer();
    	for(Object item : items){
    		if(sb.length() > 0){
    			sb.append(seprator);
    		}
    		sb.append(item == null ? "" : String.valueOf(item));
    	}
    	return sb.toString();
    }
    public static String join(List<?> items){
    	StringBuffer sb = new StringBuffer();
    	for(Object item : items){
    		sb.append(item == null ? "" : String.valueOf(item));
    	}
    	return sb.toString();
    }
    /*public static String join(List<?> items){
    	return joinBySeparator(null,items);
    }*/
    /**
     * 控制台等待用户的输入，用户输入信息后按回车键后，返回输入的信息
     * @author wuyujie Oct 15, 2014 9:11:27 PM
     * @param promp
     * @param defaultStr
     * @return
     * @throws IOException
     */
    public static String getConsoleInput(String promp,String defaultStr) throws IOException{
        System.out.print(promp);
        BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
        if(NullUtil.isNotEmpty(defaultStr))
            System.out.print(defaultStr);
        return strin.readLine();
    }
    
    /**
     * 判断两个值是否泛义上的相等。即1和"1"相等。
     * @author wuyujie Dec 9, 2014 11:24:39 AM
     * @param value1
     * @param value2
     * @return
     */
    public static boolean isEqual(Object value1,Object value2){
    	if(value1 == null && value2 == null){
    		return true;
    	}
    	if((value1 == null) ^ (value2 == null)){
    		return false;//异或为true说明两个一个是null，一个是非null，那肯定不相等
    	}
    	if((value1.getClass().isArray()) ^ (value2.getClass().isArray())){
    		return false;//说明一个是数组，一个不是数组
    	}
    	
    	if(value1 instanceof Number || value2 instanceof Number){
    		//如果两个值其中有一个是数字，那就都转成数字来比较，这是为了防止出出现1和"1.0"不相等的状况
    		return new BigDecimal(String.valueOf(value1)).compareTo(new BigDecimal(String.valueOf(value2))) == 0;
    	}
    	
    	value1 = value1.getClass().isArray() ? CommonUtil.join((Object[])value1) : String.valueOf(value1);
    	value2 = value2.getClass().isArray() ? CommonUtil.join((Object[])value2) : String.valueOf(value2);
    	
    	return value1.equals(value2);
    }
    public static boolean isTrue(Object value1,Object value2,String operator) throws Exception{
    	if(operator.equals("==")){
    		return isEqual(value1,value2);
    	}
    	if(operator.equals("!=")){
    		return !isEqual(value1,value2);
    	}
    	if(operator.equals(">") || operator.equals(">=") || operator.equals("<") || operator.equals("<=")){
    		//两个都不为null，且都是数字才能用这些操作符比较
    		if(value1 == null || value2 == null)
    			return false;
    		value1 = String.valueOf(value1);
    		value2 = String.valueOf(value2);
    		if(NullUtil.isEmpty(((String)value1)) || NullUtil.isEmpty(((String)value2)))
    			return false;
    		int result = new BigDecimal((String)value1).compareTo(new BigDecimal((String)value2));
    		if(operator.equals(">"))
    			return result > 0;
    		if(operator.equals(">="))
        		return result >= 0;
        	if(operator.equals("<"))
        		return result < 0;
        	if(operator.equals("<="))
        		return result <= 0;					
    	}
    	if(operator.equals("in")){
    		//value1是单项值，value2是数组或者List才能用in
    		if(value2 == null || value1 == null)
    			return false;
    		if(value2.getClass().isArray()){
    			return CommonUtil.isIn(value1, (Object[])value2);
    		}else if(value2 instanceof Collection){
    			return CommonUtil.isIn(value1, (Collection)value2);
    		}else{
    			throw new Exception("invalid parameter type!");
    		}
    	}
    	
    	return false;
    }
    
    /*public static void main(String[] args) throws NumberFormatException, Exception {
    	System.out.println("null,null : "+isEqual(null,null));
    	System.out.println("1,'1' : "+isEqual(1,"1"));
    	System.out.println("1,2 : "+isEqual(1,2));
    	
    	System.out.println("array1,array2 : "+isEqual(new String[]{"1","2","4"},new String[]{"1","2","3"}));
    	
    	
    	Number number1 = new Integer("1");
    	Number number2 = new Float("1");
    	
    	System.out.println("int,float : "+isTrue("2",new Float("1.00"),">"));
    	System.out.println("int,float : "+isTrue("2",new Float("1.00"),">="));
    	System.out.println("int,float : "+isTrue("2",new Float("1.00"),"<"));
    	System.out.println("int,float : "+isTrue("2",new Float("1.00"),"<="));
    	System.out.println("int,float : "+isTrue("2",new Float("1.00"),"!="));
    	
    	System.out.println("int,float : "+isTrue("2",new Integer[]{1,2,3},"in"));
    	
	}*/
    
    /**
     * 把inputStream转成String.采用UTF-8编码
     * @author wuyujie Jan 14, 2015 10:26:33 PM
     * @param is
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String inputStream2String(InputStream is) throws UnsupportedEncodingException {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));      
        StringBuilder sb = new StringBuilder();      
    
        String line = null;      
       try {      
           while ((line = reader.readLine()) != null) {      
                sb.append(line + "\n");      
            }      
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
           try {      
                is.close();      
            } catch (IOException e) {      
                e.printStackTrace();      
            }      
        }      
    
       return sb.toString();      
    }
    
    /*public static BusiException buildException(IErrorCodeEnum error,Object... params){
		String message = parsePatternText(error.getMessage(),params);
		return new BusiException(error,message);
	}*/
	public static Exception buildException(String message){
		return new Exception(message);
	}
	public static String parsePatternText(String text,Object... params){
		if(NullUtil.isEmpty(params)){
			return text;
		}
		return MessageFormat.format(text, params);
	}
    /**
     * 获取到最底层的Caused，如果是ICaiException类别的，则直接返回
     * @param e
     * @return
     */
    public static Throwable getCaused(Throwable e){
		while(e.getCause() != null){
			e = e.getCause();
			if(e instanceof IXSGException){
				break;
			}
		}
		return e;
	}
    
    public static<T>T convert(Object value,Class<T> type) throws Exception{
    	Object result = value;
    	if(type == Long.class){
    		if(!(value instanceof Long)){
    			result = Long.parseLong(String.valueOf(value));;
    		}
        }else if(type == String.class){
        	if(!(value instanceof String)){
        		result = String.valueOf(value);
    		}
        }else if(type == Integer.class){
        	if(!(value instanceof Integer)){
        		result = Integer.parseInt(String.valueOf(value));
    		}
        }else if(type == Short.class){
        	if(!(value instanceof Short)){
        		result = Short.parseShort(String.valueOf(value));
    		}
        }else if(type == Date.class){
        	if(!(value instanceof Date)){
        		result = DateUtil.str2Date((String)value);
    		}
        }else if(type == Float.class){
        	if(!(value instanceof Float)){
        		result = Float.parseFloat(String.valueOf(value));
    		}
        }else if(type == Double.class){
        	if(!(value instanceof Float)){
        		result = Double.parseDouble(String.valueOf(value));
    		}
        }
    	return (T)result;
    }
    
    public static <T>List<T> buildList(T... objs){
    	List<T> list = new ArrayList<T>();
    	for(T obj : objs){
    		list.add(obj);
    	}
    	return list;
    }
}
