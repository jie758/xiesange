package com.xiesange.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @Description: 日志工具类。
 * @Author wuyj
 */
public class LogUtil
{
	private static Logger logger = LogUtil.getLogger(LogUtil.class);
	
	public static Logger getLogger(Class clazz)
    {
        return Logger.getLogger(clazz.getName());
    }
    
	public static void dump(String name, Object object){
		dump(name,object,false);
	}
    public static void dump(String name, Object object,boolean ignoreNullField)
    {
        if(!canDump()){
        	logger.debug("dump :" + object != null?object.getClass().getName():"null"+"."+name);
        	return;
        }
    	try
        {
            StringBuffer sb = new StringBuffer("dump [\"" + name + "\"] : ");
            if (object == null)
            {
                sb.append("[null]");
            }
            else
            {
                sb.append(object.getClass().getName()+"@"+object.hashCode()).append("\n");
                Object val = LogUtil.parse(object, 1, null,null,ignoreNullField);
                if(val != null)
                	sb.append(val);

            }
            sb.append("\n").append("dump finished");
            
            logger.debug(sb.toString());

        }
        catch (Exception e)
        {
            logger.debug(e, e);
        }
    }
    
    
    public static String parse(Object object, int lay, String excludeReg,HashSet linkedParent,boolean ingoreNullField) throws Exception
    {
        HashSet linked = null;
        if(linkedParent == null)
            linked = new HashSet();
        else{
            linked = new HashSet(linkedParent);
            
        }
        Class clazz = object.getClass();
        if (!isComplex(clazz))
        {
            return object.toString();// 对象本身是简单类型，则直接打印
        }
        
        if(linked.contains(object))
            return getPrefixBlank(lay)+"[referenced]";
        
        linked.add(object);
        
        if (object instanceof Collection)
        {
            return parseCollection((Collection) object, lay, excludeReg,linked,ingoreNullField);
        }
        else if (object instanceof Map)
        {
            return parseMap((Map) object, lay, excludeReg,linked,ingoreNullField);
        }
        else if (clazz.isArray())
        {
            Object[] arr = null;
            Class cmpType = clazz.getComponentType();
            if (cmpType == long.class)
            {
                arr = parse2ObjectArray((long[]) object);
            }
            else if (cmpType == int.class)
            {
                arr = parse2ObjectArray((int[]) object);
            }
            else if (cmpType == short.class)
            {
                arr = parse2ObjectArray((short[]) object);
            }
            else if (cmpType == double.class)
            {
                arr = parse2ObjectArray((double[]) object);
            }
            else if (cmpType == float.class)
            {
                arr = parse2ObjectArray((float[]) object);
            }
            else
            {
                arr = (Object[]) object;
            }
            return parseArray(arr, lay, excludeReg,linked,ingoreNullField);

        }
        else
        {
            return parseObject(object, lay, excludeReg,linked,ingoreNullField);
        }

    }

    private static String parseCollection(Collection coll, int lay, String excludeReg,HashSet linkedParent,boolean ignoreNullField) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        if (coll.isEmpty())
        {
            if(!ignoreNullField){
            	sb.append(sb.append(getPrefixBlank(lay)).append("[empty]"));
            }
        }
        else
        {
            Object[] arr = coll.toArray(new Object[coll.size()]);
            sb.append(parseArray(arr, lay, excludeReg,linkedParent,ignoreNullField));
        }
        return sb.toString();
    }

    private static String parseArray(Object[] arr, int lay, String excludeReg,HashSet linkedParent,boolean ignoreNullField) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        String blankStr = getPrefixBlank(lay);
        if (NullUtil.isEmpty(arr))
        {
            return ignoreNullField ? null : blankStr + "[empty]";
        }
        for (int i = 0; i < arr.length; i++)
        {
            Object item = arr[i];
            if (sb.length() > 0)
            {
                sb.append("\n");
            }
            sb.append(blankStr);
            sb.append("[" + i + "] : ");
            Class type = (item == null) ? null : item.getClass();
            sb.append(parseItem(item, type, lay, excludeReg,linkedParent,ignoreNullField));
        }
        return sb.toString();
    }

    private static String parseMap(Map map, int lay, String excludeReg,HashSet linkedParent,boolean ignoreNullField) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        String blankStr = getPrefixBlank(lay);
        if (NullUtil.isEmpty(map))
        {
            return ignoreNullField ? null : blankStr + "[empty]";
        }
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object value = map.get(key);
            if (sb.length() > 0)
            {
                sb.append("\n");
            }
            sb.append(blankStr);
            sb.append("[key]" + key + " : ");

            Class type = value == null ? null : value.getClass();
            sb.append(parseItem(value, type, lay, excludeReg,linkedParent,ignoreNullField));
        }

        return sb.toString();
    }

    private static String parseObject(Object object, int lay, String excludeReg,HashSet linkedParent,boolean ignoreNullField)
    {
        
        // 复杂类型需要循环打印字段
        Field[] fs = null;
        /*
         * if(object instanceof DataObject){ return "[sdl object]"; }else
         */if (object.getClass().getName().equals("com.sjm.db.DBEntity"))
        {
            fs = ClassUtil.getFields(object.getClass());// DataObject不需要取父级字段
            if(fs != null){
                List<Field> list = CommonUtil.parseArray2List(fs);
                //list.add(ClassUtil.getField(DBEntity.class, "children"));
                fs = list.toArray(new Field[list.size()]); 
            }
        }
        else
        {
            fs = ClassUtil.getAllFields(object.getClass());
        }
        if (NullUtil.isEmpty(fs))
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        boolean isSdl = isSdlObject(object);
        for (Field f : fs)
        {

            String fName = f.getName();
            //LogUtil.getLogger(LogUtil.class).debug("field name : "+fName);
            if(needFilter(f))
                continue;
            if (!NullUtil.isEmpty(excludeReg) && Pattern.matches(excludeReg, fName))
                continue;
            if (isSdl && (Modifier.isStatic(f.getModifiers()) || !Modifier.isPrivate(f.getModifiers())))
            {
                continue;// 如果是sdl对象，private或者非static字段都不打印
            }
            Class fType = f.getType();
            try
            {
                Object fValue = ClassUtil.getFieldValue(object, fName);//ClassUtil.getPropertyValue(object, fName);
                if(fValue == null && ignoreNullField){
                	continue;
                }
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(getPrefixBlank(lay)).append(fName).append(" : ");

                sb.append(parseItem(fValue, fType, lay, excludeReg,linkedParent,ignoreNullField));
            }
            catch (Exception e)
            {
//                IMSUtil.throwBusiException(e);
            	LogUtil.getLogger(LogUtil.class).error(e);
            }
        }
        return sb.toString();
    }

    private static String parseItem(Object value, Class type, int lay, String excludeReg,HashSet linkedParent,boolean ignoreNullField) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb_type = new StringBuffer();
        if (type != null)
        {
            String strType = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();
            sb_type.append("                   ").append(strType);
        }
        if (value == null)
        {
            sb.append("[null]");
            sb.append(sb_type);
        }
        else if (!isComplex(value.getClass()))
        {
            if(value instanceof Date){
                value = DateUtil.date2Str((Date)value, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
            }
            
            sb.append(value.toString());
            sb.append(sb_type);
        }
        else
        {
            Object val = parse(value, lay + 1, excludeReg,linkedParent,ignoreNullField);
            if(val != null || !ignoreNullField){
            	sb.append(sb_type).append("@"+value.hashCode()).append("\n").append(val);
            }
        	
        }
        return sb.toString();
    }

    private static String getPrefixBlank(int lay)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lay; i++)
        {
            sb.append("    ");
        }
        return sb.toString();
    }

    private static Object[] parse2ObjectArray(long[] items)
    {
        if (NullUtil.isEmpty(items))
            return null;
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++)
        {
            result[i] = Long.valueOf(items[i]);
        }
        return result;
    }

    private static Object[] parse2ObjectArray(int[] items)
    {
        if (NullUtil.isEmpty(items))
            return null;
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++)
        {
            result[i] = Integer.valueOf(items[i]);
        }
        return result;
    }

    private static Object[] parse2ObjectArray(short[] items)
    {
        if (NullUtil.isEmpty(items))
            return null;
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++)
        {
            result[i] = Short.valueOf(items[i]);
        }
        return result;
    }

    private static Object[] parse2ObjectArray(float[] items)
    {
        if (NullUtil.isEmpty(items))
            return null;
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++)
        {
            result[i] = Float.valueOf(items[i]);
        }
        return result;
    }

    private static Object[] parse2ObjectArray(double[] items)
    {
        if (NullUtil.isEmpty(items))
            return null;
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++)
        {
            result[i] = Double.valueOf(items[i]);
        }
        return result;
    }

    /**
     * 判断一个对象是否sdl对象
     * 
     * @param obj
     * @return
     */
    private static boolean isSdlObject(Object obj){
        return obj.getClass().getPackage().getName().startsWith("com.ailk.easyframe.sdl") || 
            obj.getClass().getSuperclass().getPackage().getName().startsWith("com.ailk.easyframe.sdl");
        /*return obj instanceof CsdlStructObject 
            || obj instanceof CsdlArrayList
            || obj instanceof IHolder;*/
    }
    private static boolean isComplex(Class clazz)
    {
        if (clazz.isArray())
            return true;
        if (Date.class.isAssignableFrom(clazz))
            return false;// Date当做简单类型处理
        if(BigDecimal.class.isAssignableFrom(clazz)){
        	return false;
        }
        return clazz.getPackage() != null && !clazz.getName().startsWith("java.lang.");
    }
    private static boolean needFilter(Field f){
        return Logger.class.isAssignableFrom(f.getType());
    }
    
    private static boolean canDump()
    {
        return logger.isDebugEnabled();
    }
}
