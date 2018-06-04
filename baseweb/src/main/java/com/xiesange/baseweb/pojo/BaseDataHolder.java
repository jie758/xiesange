package com.xiesange.baseweb.pojo;

public class BaseDataHolder<T> {
	private long cacheFlag;
	private T cacheData;
	
	
	public BaseDataHolder(long cacheFlag,T cacheData){
		this.cacheFlag = cacheFlag;
		this.cacheData = cacheData;
	}
	
	public long getCacheFlag() {
		return cacheFlag;
	}
	public void setCacheFlag(long cacheFlag) {
		this.cacheFlag = cacheFlag;
	}
	public T getCacheData() {
		return cacheData;
	}
	public void setCacheData(T cacheData) {
		this.cacheData = cacheData;
	}
	
	
}
