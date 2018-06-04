package com.xiesange.core.util;

import java.math.BigDecimal;
import java.util.Random;

public class RandomUtil {
	private static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	         'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


	public static void main(String[] args) {
		//System.out.println(getNum(16));
		System.out.println(getString(32));
		//System.out.println(getRangeLong(10));
	}
	
	public static String getNum(int length){
		Random rm = new Random();
		StringBuffer sb = new StringBuffer(length);
		int randomBound = digits.length;
		for(int i=0;i<length;i++){
			sb.append(digits[rm.nextInt(randomBound)]);
		}
		return sb.toString();
	}
	
	public static String getString(int length){
		Random rm = new Random();
		StringBuffer sb = new StringBuffer(length);
		int randomBound = chars.length;
		for(int i=0;i<length;i++){
			sb.append(chars[rm.nextInt(randomBound)]);
		}
		return sb.toString();
	}
	
	//获取0到某个值之间的一个随机数字
	public static double getRangeNum(long weight,int precision){
		return getRangeNum(0,weight,precision);
	}
	
	//获取某个两个值之间的一个随机数字
	public static double getRangeNum(long start,long end,int precision){
		BigDecimal b = new BigDecimal((start+Math.random()*(end-start)));
		return b.setScale(precision,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	//获取0到某个值之间的一个随机整数
	public static long getRangeLong(long weight){
		return getRangeLong(0,weight);
	}
	
	//获取某个两个值之间的一个随机数字
	public static long getRangeLong(long start,long end){
		return Math.round(getRangeNum(start,end,1));
	}

}
