package com.xiesange.orm.sequence;

import com.xiesange.orm.DBEntity;

public interface ISequenceManager {
	public void init(long baseNum) throws Exception;
	public long getNext(String key) throws Exception;
	public long[] getNext(String key,int count) throws Exception;
	public <T extends DBEntity>String getDBEntityKey(Class<T> clz) throws Exception;
}
