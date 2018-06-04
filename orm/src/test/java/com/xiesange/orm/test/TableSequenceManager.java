package com.xiesange.orm.test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.NativeValue;
import com.xiesange.orm.sequence.ISequenceManager;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.insert.InsertStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.orm.test.entity.SysSequenceTest;

public class TableSequenceManager implements ISequenceManager {
	private Long SEQUENCE_INIT = 0L;
	private long SEQUENCE_BASE_NUM; 
	private Map<String,SysSequenceTest> SEQ_CACHE = new HashMap<String,SysSequenceTest>();
	private Properties CLASS_SEQ_MAPPING = null;//key=entityClassName,value=key
	
	@Override
	public void init(long baseNum) throws Exception{
		SEQUENCE_BASE_NUM = baseNum;
		
		CLASS_SEQ_MAPPING = initSequence("/entity_seq.properties");
		
        List<SysSequenceTest> list = (List<SysSequenceTest>)new QueryStatement(SysSequenceTest.class).execute();//dao.queryAll(SysSequence.class);
        
        if(NullUtil.isNotEmpty(list)){
	        for(SysSequenceTest seq : list){
	            String key = String.valueOf(seq.getTableKey());
	            SEQ_CACHE.put(key, seq);
	        }
        }
	}

	@Override
	public long getNext(String key) throws Exception {
        return getNext(key,1)[0];
	}
	
	@Override
	public long[] getNext(String key, int count) throws Exception {
		SysSequenceTest seqEntity = SEQ_CACHE.get(key);
		long startSeq = 0;//之前最后一个被使用的序列之,新的值要在这个值基础上加
        synchronized(key) {
            if(seqEntity == null){
            	List<SysSequenceTest> result = (List<SysSequenceTest>)new QueryStatement(SysSequenceTest.class,new DBCondition(SysSequenceTest.JField.tableKey,key))
            									.appendRange(0,1).execute();
            	if(NullUtil.isEmpty(result)){
	            	//如果在缓存中找不到对应的sequence对象，那就需要新建，并插入到数据库里
	            	startSeq = SEQUENCE_INIT;
	            	seqEntity = new SysSequenceTest();
	                seqEntity.setTableKey(key);
	                seqEntity.setValue(startSeq+count);
	                
	                Date now = DateUtil.now();
	                seqEntity.setUpdateTime(now);
	                seqEntity.setCreateTime(now);
	                new InsertStatement(seqEntity).execute();
	                //dbClient.execute(,connection);
	                
	                seqEntity.setTableKey(null);//放入到缓存的时候就不需要code，减少存储空间
	                seqEntity._getSettedValue().clear();
            	}else{
            		seqEntity = result.get(0);
            		startSeq = seqEntity.getValue();
            		seqEntity.setValue(startSeq+count);//要把当前缓存里的设置成最后用到的那个值
            		
            		FieldUpdateExpression[] updateValues = new FieldUpdateExpression[]{
            			new FieldUpdateExpression(SysSequenceTest.JField.value,new NativeValue("value+"+count))	
            		};
            		new UpdateStatement(updateValues, new DBCondition(SysSequenceTest.JField.tableKey,key)).execute();
            		
            	}
            	
            	SEQ_CACHE.put(key, seqEntity);
                
            }else{
            	if(seqEntity.getValue() == null){
            		seqEntity.setValue(SEQUENCE_INIT);
            	}
            	startSeq = seqEntity.getValue();
            	seqEntity.setValue(startSeq+count);//要把当前缓存里的设置成最后用到的那个值
                
                FieldUpdateExpression[] updateValues = new FieldUpdateExpression[]{
        			new FieldUpdateExpression(SysSequenceTest.JField.value,new NativeValue("value+"+count))	
        		};
        		new UpdateStatement(updateValues, new DBCondition(SysSequenceTest.JField.tableKey,key)).execute();
            }
            
        }
        long[] result = new long[count];
        for(int i=0;i<count;i++){
        	result[i] = (++startSeq)+SEQUENCE_BASE_NUM;
        }
        return result;
	}
	
	
	
	public SysSequenceTest getSequenceEntity(String key,int count)
			throws Exception {
		if(NullUtil.isEmpty(SEQ_CACHE))
			return null;
		return SEQ_CACHE.get(key);
	}
	
	
	public <T extends DBEntity>String getDBEntityKey(Class<T> clz){
		return CLASS_SEQ_MAPPING.getProperty(clz.getSimpleName());
	}
	
	/**
	 * 初始化每个key在当前系统中sequence的基数。
	 * 用于分布式部署的时候，如果sequence都一样的话会起冲突，因此可以给各个系统分配一个区间
	 * @param classKeyMapping
	 * 			数据库实体所对应的key，在entity_seq.properties里配置
	 * @param seqBaseMap
	 * 			部署的时候指定的各个sequence的区间基数，配置的时候是表名=基数
	 * @return
	 * 		返回一个map，因为配置的时候表名=基数，这里map要转换成:表名对应的key=基数
	 */
	private Map<String,Long> initTableSeqBaseNum(Properties classKeyMapping,Map<String,Long> seqBaseMap){
		if(NullUtil.isEmpty(seqBaseMap)){
			return null;
		}
		Iterator<Entry<String,Long>> it = seqBaseMap.entrySet().iterator();
		Map<String,Long> resultMap = new HashMap<String,Long>();
		String tablePrefix = "table.";//table.开头表示是数据库表的序列，还有其它类型的序列
		String key = null;
		while(it.hasNext()){
			Entry<String,Long> entry = it.next();
			key = entry.getKey();
			if(key.startsWith(tablePrefix)){
				key = key.substring(tablePrefix.length());
				key = CommonUtil.parse2JavaName(key, true);
			}
			resultMap.put(classKeyMapping.getProperty(key), entry.getValue());
		}
		return resultMap;
	}
	
	private Properties initSequence(String path) throws Exception{
		Properties prop = CommonUtil.loadProperties(path);
		//检查是否有序列重复定义
		Iterator<Entry<Object,Object>> it = prop.entrySet().iterator();
    	String value = null;
    	Set<String> valueSet = new HashSet<String>();
    	while(it.hasNext()){
    		Entry<Object,Object> entry = it.next();
    		value = (String)entry.getValue();
    		if(valueSet.contains(value)){
    			throw new Exception("序列值定义重复:"+value);
    		}
    		valueSet.add(value);
    	}
		
		return prop;
	}
}
