package com.xiesange.orm;
/**
 * 原生的sql字段操作对象。比如update sequence set value=value+1 where key = '10002';
 * 其中value+1就可以用new NativeValue("value+1");
 * NativeValue中的字符串会原样的构建成到sql中，
 * 如果不是采用原生的值对象，在sql构建的时候会进行变量绑定，那么上述value字段由于是int类型时候，值传入"value+1"在变量类型解析的时候就会出错
 * @author Wilson 
 * @date 下午2:19:45
 */
public class NativeValue {
	private Object value;
	
	public NativeValue(String value){
		this.value = value;
	}

	public Object getValue() {
		return value;
	} 
}
