package com.xiesange.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 定义日期相关的公用方法
 * @Author wuyj
 * @Date 2011-7-2
 */
public class DateUtil
{
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String DATE_FORMAT_YYYYMMDDHH = "yyyyMMddHH";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYYMM = "yyyyMM";
    public static final String DATE_FORMAT_YYYY = "yyyy";
    public static final String DATE_FORMAT_EN_B_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_EN_B_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_EN_B_YYYYMMDDHH = "yyyy-MM-dd HH";
    public static final String DATE_FORMAT_EN_B_YYYYMMDD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_EN_B_YYYYMM = "yyyy-MM";
    public static final String DATE_FORMAT_EN_B_YYYY = "yyyy";
    
    /**
     * 2014-09-13 wuyj 增加获取当前时间的方法，统一使用该接口方便以后转换实现方式
     * 
     * @return
     */
    public static Date now()
    {
        return new Date();
    }
    /**
     * 得到当前日期字符串,根据传入的格式返回
     * 
     * @return
     */
    public static String nowStr(String format)
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    
    /**
     * 得到当前日期字符串,yyyymm格式
     * 
     * @return
     */
    public static String now_yyyymm()
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_YYYYMM);
        return formatter.format(date);
    }
    /**
     * 得到当前日期字符串,yyyymmdd格式
     * 
     * @return
     */
    public static String now_yyyymmdd()
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
        return formatter.format(date);
    }
    /**
     * 得到当前日期字符串,yyyy-mm-dd格式
     * 
     * @return
     */
    public static String now_yyyy_mm_dd()
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_EN_B_YYYYMMDD);
        return formatter.format(date);
    }
    /**
     * 得到当前日期字符串,yyyymmddhhmmss格式
     * 
     * @return
     */
    public static String now_yyyymmddhhmmss()
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
        return formatter.format(date);
    }
    /**
     * 得到当前日期字符串,yyyy-mm-dd hh:mm:ss
     * 
     * @return
     */
    public static String now_yyyy_mm_dd_hh_mm_ss()
    {
        Date date = now();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
        return formatter.format(date);
    }

    /**
     * 根据一个日期字符串，及其对应的格式，解析成日期对象
     * 
     * @param dateStr 日期
     * @param format 模式
     * @return java型的日期
     */
    public static Date str2Date(String dateStr, String format) throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try{
            return sdf.parse(dateStr);
        }
        catch (ParseException pe){
            throw new Exception(pe.getMessage());
        }
    }
    
    /**
     * 根据一个日期字符串，自动判别其格式，解析成日期对象。
     * 如果能知道日期格式，最好用parseDate(String dateStr, String format)，自动判别格式会有性能损耗
     * 
     * @param strDate 需要进行格式化的日期
     * @return 经过格式化后的字符串
     * @throws Exception
     */
    public static Date str2Date(String strDate) throws Exception
    {
        String formatStr = "yyyyMMdd";
        if (strDate == null || strDate.trim().equals(""))
        {
            return null;
        }
        switch (strDate.trim().length())
        {
        case 6:
            if (strDate.substring(0, 1).equals("0"))
            {
                formatStr = "yyMMdd";
            }
            else
            {
                formatStr = "yyyyMM";
            }
            break;
        case 8:
            formatStr = "yyyyMMdd";
            break;
        case 10:
            if (strDate.indexOf("-") == -1)
            {
                formatStr = "yyyy/MM/dd";
            }
            else
            {
                formatStr = "yyyy-MM-dd";
            }
            break;
        case 11:
            if (strDate.getBytes().length == 14)
            {
                formatStr = "yyyy年MM月dd日";
            }
            else
            {
                return null;
            }
        case 14:
            formatStr = "yyyyMMddHHmmss";
            break;
        case 19:
            if (strDate.indexOf("-") == -1)
            {
                formatStr = "yyyy/MM/dd HH:mm:ss";
            }
            else
            {
                formatStr = "yyyy-MM-dd HH:mm:ss";
            }
            break;
        default:
            throw new Exception("invalid date format : " + strDate);
        }
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            return formatter.parse(strDate);
        }
        catch (Exception e)
        {
            LogUtil.getLogger(DateUtil.class).error(e, e);
            LogUtil.getLogger(DateUtil.class).debug("转换日期字符串格式时出错;" + e.getMessage());
            return null;
        }
    }

    /**
     * 把日期对象格式化成指定格式的字符串
     * @return
     * @throws Exception
     */
    public static String date2Str(Date date, String formatStr) throws Exception
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            return sdf.format(date);
        }
        catch (Exception ex)
        {
            LogUtil.getLogger(DateUtil.class).error(ex, ex);
            throw new Exception("fail to format date : [date=" + date + ";format=" + formatStr + "]");
        }
    }
    

    
    /**
     * 获取年份数，比如2014-12-13，则返回2014
     * @author wuyujie Dec 13, 2014 12:55:53 PM
     * @param date
     * @return
     */
    public static int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * 获取小时数，比如2014-12-13 20:23:23，则返回20
     * @author wuyujie Dec 13, 2014 12:55:53 PM
     * @param date
     * @return
     */
    public static int getHour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取月份数，比如2014-12-13，则返回12
     * @author wuyujie Dec 13, 2014 12:55:53 PM
     * @param date
     * @return
     */
    public static int getMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;//月份数是从0开始的，因此要加1
    }
    /**
     * 获取月份中的日期数，比如2014-12-13，则返回13
     * @author wuyujie Dec 13, 2014 12:55:53 PM
     * @param date
     * @return
     */
    public static int getDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 获取一周中的日期数，比如2014-12-13（周六），则返回6
     * @author wuyujie Dec 13, 2014 12:55:53 PM
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int val = cal.get(Calendar.DAY_OF_WEEK);//一周中的日期是从周日开始的，周日是1，周一是2
        return val == 1 ? 7 : val-1;
    }
    /**
     * 返回指定日期所在月的第一天的日期，即当前月1号
     * @param date
     * @return
     */
    public static Date getFirstDateOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }
    /**
     * 返回指定日期所在周的第一天的日期，即周一的日期
     * @param date
     * @return
     */
    public static Date getFirstDateOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }
    
    /**
     * 获取一年中的日期数，比如2014-12-13（2014年中的第347天），则返回347
     * @author wuyujie Dec 13, 2014 12:57:25 PM
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }
    
    public static void main(String[] args) {
    	Date date = new Date();
    	System.out.println("getYear : "+getYear(date));
    	System.out.println("getMonth : "+getMonth(date));
    	System.out.println("getDay : "+getDay(date));
    	System.out.println("getDayOfWeek : "+getDayOfWeek(date));
    	System.out.println("getDayOfYear : "+getDayOfYear(date));
    	
	}
    
    /**
     * 获取传入的日期中最后一秒钟的时刻。比如date=2015-10-23 19:20:30,返回2015-10-23 23:59:59
     * @param date
     * @return
     * @author Wilson 
     * @date 上午9:50:19
     */
    public static Date getDayEnd(Date date){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return  cal.getTime();
    }
    
    /**
     * 获取传入的日期中最开始一秒钟的时刻。比如date=2015-10-23 19:20:30,返回2015-10-23 00:00:01
     * @param date
     * @return
     * @author Wilson 
     * @date 上午9:50:19
     */
    public static Date getDayBegin(Date date){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTime();
    }
    
    /**
     * 针对某个日期偏移时间，偏移的单位可以各个单位，比如秒，天，年等
     * @param date,指定的基准日期
     * @param offset，偏移单位：
     * 					xs，偏移x秒，x可以是负数，比如10s,表示向前偏移10秒；-10s表示后退10秒
     * 					xm，偏移x分种，x可以是负数，比如10m,表示向前偏移10分钟；-10m表示后退10分钟
     * 					xh，偏移x分种，x可以是负数，比如10m,表示向前偏移10分钟；-10m表示后退10分钟 
     *                  xd，偏移x天，x可以是负数，比如10d,表示向前偏移10天；-10m表示后退10天 
     *                  xM，偏移x月，x可以是负数，比如10M,表示向前偏移10月；-10m表示后退10月
     *                  xy，偏移x年，x可以是负数，比如10y,表示向前偏移10年；-10m表示后退10年
     * @return
     * @throws Exception
     * @author Wilson 
     * @date 上午9:51:45
     */
    public static Date offset(Date date,String offset) throws Exception{
    	Integer offsetValue = Integer.parseInt(offset.substring(0,offset.length()-1));
    	if(offset.endsWith("s")){
    		//按秒偏移
    		return offsetSecond(date,offsetValue);
    	}else if(offset.endsWith("m")){
    		//按分钟偏移
    		return offsetMinute(date,offsetValue);
    	}else if(offset.endsWith("h")){
    		//按小时偏移
    		return offsetHour(date,offsetValue);
    	}else if(offset.endsWith("d")){
    		//按天偏移
    		return offsetDate(date,offsetValue);
    	}else if(offset.endsWith("M")){
    		//按月偏移
    		return offsetMonth(date,offsetValue);
    	}else if(offset.endsWith("y")){
    		//按年偏移
    		return offsetYear(date,offsetValue);
    	}else{
    		throw new Exception("invalid operation");
    	}
    	
    }
    /**
     * 按照秒数来偏移日期。
     * 比如获取某个日期往后7秒钟，可以采用offsetSecond(date,7);
     * 比如获取某个日期往前7秒钟，可以采用offsetSecond(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetSecond(Date date,int seconds){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return  cal.getTime();
    }
    /**
     * 按照分钟来偏移日期。
     * 比如获取某个日期往后7分钟，可以采用offsetMinute(date,7);
     * 比如获取某个日期往前7分钟，可以采用offsetMinute(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetMinute(Date date,int minutes){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return  cal.getTime();
    }
    /**
     * 按照小时来偏移日期。
     * 比如获取某个日期往后7小时，可以采用offsetHour(date,7);
     * 比如获取某个日期往前7小时，可以采用offsetHour(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetHour(Date date,int hours){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return  cal.getTime();
    }
    /**
     * 按照天数来偏移日期。
     * 比如获取某个日期往后7天，可以采用offsetDate(date,7);
     * 比如获取某个日期往前7天，可以采用offsetDate(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetDate(Date date,int days){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return  cal.getTime();
    }
    
    /**
     * 按照月数来偏移日期
     * 比如获取某个日期往后7个月，可以采用offsetMonth(date,7);
     * 比如获取某个日期往前7个月，可以采用offsetMonth(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetMonth(Date date,int months){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return  cal.getTime();
    }
    
    /**
     * 按照年数偏移日期。
     * 比如获取某个日期往后7年，可以采用offsetYear(date,7);
     * 比如获取某个日期往前7年，可以采用offsetYear(date,-7);
     * @author wuyujie Feb 22, 2015 1:46:12 PM
     * @param date
     * @param days
     * @return
     */
    public static Date offsetYear(Date date,int years){
    	Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return  cal.getTime();
    }
    
}
