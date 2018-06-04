package net.sf.cglib.proxy;
/**
 * 纯粹是为了能够对DBEntity在转换成json串的时候，能进入DBEntitySerializer这个序列化器。
 * 貌似fastjson只有实现了这个net.sf.cglib.proxy.Factory接口才能进入自定义序列化器
 * @author Think
 *
 */
public interface Factory {
}
