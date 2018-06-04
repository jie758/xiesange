package com.xiesange.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class ClassUtil
{
    private static Logger logger = LogUtil.getLogger(ClassUtil.class);

    public static Object instance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (className == null || className.length() == 0)
        {
            return null;
        }
        Class clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        return obj;
    }

    public static Object instance(Class clazz) throws InstantiationException, IllegalAccessException
    {
        Object obj = clazz.newInstance();
        return obj;
    }
    
    public static Field getField(Class clazz, String fieldName)
    {
        Class current = clazz;
        while (current != null)
        {
            try
            {
                Field field = current.getDeclaredField(fieldName);
                return field;
            }
            catch (Exception e)
            {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    /**
     * @Description 避免使用反射，用getPropertyValue方法代替
     * @author zenglu
     * @throws Exception 
     * @Date 2012-10-08
     */
    public static Object getFieldValue(Object obj, String fieldName) throws Exception
    {
        Class clazz = obj.getClass();
        Field field = getField(clazz, fieldName);
        if (field == null)
        {
            throw new Exception("Can not find field : \"" + fieldName + "\" during " + obj.getClass().getName());
        }
        return getFieldValue(obj,field);
    }

    /**
     * @Description 避免使用反射，用getPropertyValue方法代替
     * @author zenglu
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @Date 2012-10-08
     */
    public static Object getFieldValue(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException
    {
    	field.setAccessible(true);
        return field.get(obj);
    }
    
    public static Field[] getAllFields(Class clazz)
    {
        Class temp = clazz;
        Map fields = new LinkedHashMap();
        while (temp != null)
        {
            Field[] fs = getFields(temp);
            for (int i = 0; i < fs.length; i++)
            {
                Field f = fs[i];
                if (f.getName().startsWith("this$"))
                    continue;
                if (fields.get(f.getName()) == null)
                    fields.put(f.getName(), f);
            }
            temp = temp.getSuperclass();
        }
        return (Field[]) fields.values().toArray(new Field[fields.size()]);
    }

    public static Field[] getFields(Class clazz)
    {
        clazz.getFields();
        return clazz.getDeclaredFields();
    }
    
    
    public static Object invokeMethod(Object obj, String methodName, Class[] paramTypes, Object[] params) throws Exception
    {
        Class clazz = obj.getClass();
        Method m = clazz.getMethod(methodName, paramTypes);
        if (m == null)
        {
            throw new Exception("can not find method \"" + methodName + "\" in Class \"" + clazz.getName() + "\"");
        }
        return m.invoke(obj, params);
    }
    
    public static <T>ArrayList<T> newList(){
    	return new ArrayList<T>();
    }
    
    public static <K,V>HashMap<K,V> newMap(){
    	return new HashMap<K,V>();
    }
    
    public static <T>HashSet<T> newSet(){
    	return new HashSet<T>();
    }

}
