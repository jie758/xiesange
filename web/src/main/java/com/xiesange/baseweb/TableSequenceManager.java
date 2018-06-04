package com.xiesange.baseweb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.sequence.ISequenceManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TableSequenceManager implements ISequenceManager {
	private Long SEQUENCE_INIT = 0L;
	private long SEQUENCE_BASE_NUM; 
	//private Map<String,SysSequence> SEQ_CACHE = new HashMap<String,SysSequence>();
	private Properties CLASS_SEQ_MAPPING = null;//key=entityClassName,value=table key
	private static Logger logger = LogUtil.getLogger(TableSequenceManager.class);
	//private static Jedis jedis;
	private static JedisPool pool;
	@Override
	public void init(long baseNum) throws Exception{
		SEQUENCE_BASE_NUM = baseNum;
		
		CLASS_SEQ_MAPPING = initSequence("/entity_seq.properties");
		
        /*List<SysSequence> list = (List<SysSequence>)new QueryStatement(SysSequence.class).execute();//dao.queryAll(SysSequence.class);
        
        if(NullUtil.isNotEmpty(list)){
	        for(SysSequence seq : list){
	            String key = String.valueOf(seq.getTableKey());
	            SEQ_CACHE.put(key, seq);
	        }
        }*/
		//jedis = new Jedis("114.55.88.174",6379);
		createJedisPool();
	}

	
	private static void createJedisPool() {

        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();

        // 设置最大连接数
        config.setMaxTotal(1000);

        // 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxWaitMillis(1000);
        // 设置空间连接
        config.setMaxIdle(100);

        // 创建连接池
        pool = new JedisPool(config, "114.55.88.174", 6379);

    }
	
	@Override
	public long getNext(String key) throws Exception {
        return getNext(key,1)[0];
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public long[] getNext(String key, int count) throws Exception {
		long startime = System.currentTimeMillis();
		Jedis jedis = pool.getResource();
		long startSeq = jedis.incr("sequence_"+key)+SEQUENCE_BASE_NUM;
		logger.debug("--------access jedis.incr:"+(System.currentTimeMillis()-startime)+"ms");
		jedis.close();
		//SysSequence seqEntity = SEQ_CACHE.get(key);
		//
        //String tableKey = CLASS_SEQ_MAPPING.getProperty(key);
		//synchronized(key) {
			/*DBCondition cond = new DBCondition(SysSequence.JField.tableKey,key);
			SysSequence seqEntity = DBHelper.getDao().querySingle(SysSequence.class,cond );
            if(seqEntity == null){
            	startSeq = SEQUENCE_INIT;
            	logger.debug("start sequence : "+startSeq);
            	seqEntity = new SysSequence();
                seqEntity.setTableKey(key);
                seqEntity.setValue(startSeq+count);
                
                Date now = DateUtil.now();
                seqEntity.setUpdateTime(now);
                seqEntity.setCreateTime(now);
                new InsertStatement(seqEntity).execute();
            }else{
            	startSeq = seqEntity.getValue()+RandomUtil.getRangeLong(0, 1000);
            	logger.debug("start sequence : "+startSeq);
            	CCP.updateFieldNum(SysSequence.JField.value, count,cond);
            }*/
            //startSeq = seq.getAndIncrement();
        //}
        long[] result = new long[count];
        for(int i=0;i<count;i++){
        	result[i] = (++startSeq)+SEQUENCE_BASE_NUM;
        }
        return result;
	}
	
	
	
	/*public SysSequence getSequenceEntity(String key,int count)
			throws Exception {
		if(NullUtil.isEmpty(SEQ_CACHE))
			return null;
		return SEQ_CACHE.get(key);
	}*/

	

	/*public <T extends DBEntity> T getSequenceEntity(String key) throws Exception {
		if(NullUtil.isEmpty(SEQ_CACHE))
			return null;
		return (T)SEQ_CACHE.get(key);
	}*/
	
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
	/*private Map<String,Long> initTableSeqBaseNum(Properties classKeyMapping,Map<String,Long> seqBaseMap){
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
	}*/
	
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
