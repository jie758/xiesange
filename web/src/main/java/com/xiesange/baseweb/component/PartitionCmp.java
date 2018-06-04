package com.xiesange.baseweb.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.baseweb.cache.CacheHouse;
import com.xiesange.baseweb.cache.CacheManager;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.sys.SysPartition;
import com.xiesange.gen.dbentity.sys.SysPartitionDetail;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;

public class PartitionCmp {
	private static Map<String,List<SysPartitionDetail>> data;
	
	public static void initData(List<SysPartition> partitionList,List<SysPartitionDetail> detailList){
		if(NullUtil.isEmpty(partitionList) || NullUtil.isEmpty(detailList))
			return;
		data = new HashMap<String,List<SysPartitionDetail>>();
		
		for(SysPartition partition : partitionList){
			List<SysPartitionDetail> valueList = new ArrayList<SysPartitionDetail>();
			for(SysPartitionDetail detail : detailList){
				if(partition.getId().longValue() == detail.getPartitionId()){
					valueList.add(detail);
				}
			}
			if(NullUtil.isNotEmpty(valueList)){
				data.put(partition.getTableName(), valueList);
			}
		}
	}
	
	
	public static SysPartition getPartition(Class<? extends DBEntity> entityClass) throws Exception {
		String tableName = DBHelper.getDBTableName(entityClass);
		CacheHouse<SysPartition> cacheHouse = CacheManager.getCacheHouse(SysPartition.class);
		if(cacheHouse == null)
			return null;
		SysPartition partition = cacheHouse.getSingle(new DBCondition(SysPartition.JField.tableName,tableName));
		return partition;
	}
	public static List<SysPartitionDetail> getPartitionDetailList(long partitionId) throws Exception {
		CacheHouse<SysPartitionDetail> cacheHouse = CacheManager.getCacheHouse(SysPartitionDetail.class);
		
		List<SysPartitionDetail> partitionList = cacheHouse.getList(
				new DBCondition(SysPartitionDetail.JField.partitionId,partitionId));
		return partitionList;
	}
	
	public static String getQueryPartitionName(Class<? extends DBEntity> entityClass,DBCondition...conds) throws Exception {
		SysPartition partition = getPartition(entityClass);
		if (partition == null)
			return null;
		List<SysPartitionDetail> detailList = getPartitionDetailList(partition.getId());
		if (NullUtil.isEmpty(detailList))
			return null;
		StringBuffer sb = new StringBuffer(64);
		sb.append(DBHelper.getDBTableName(entityClass));
		for (SysPartitionDetail detail : detailList) {
			String colName = detail.getFieldName();
			Object fieldValue = null;
			for(DBCondition cond : conds){
				if(cond.getJField().getColName().equals(colName)){
					fieldValue = cond.getValue();
					break;
				}
			}
			if(fieldValue == null)
				continue;
			int mode = detail.getMode();
			String parValue = detail.getValue();

			sb.append("_");
			if (mode == BaseConsDefine.PARTITION_MODE.MOD.value()) {
				// 取模
				Long longValue = (Long) fieldValue;// 取模的话字段值必须是整数类型
				sb.append(longValue % Integer.valueOf(parValue));// 取模的话，value值就是模数
			} else if (mode == BaseConsDefine.PARTITION_MODE.DATE.value()) {
				Date dateValue = (Date) fieldValue;// 按年月的话字段值必须是日期类型
				sb.append(DateUtil.date2Str(dateValue, parValue));// 按年月的话，modValue就是指定分表的年月格式，比如有yyyyMM,yyyyMMdd,注意月份是大写的M
			} else if (mode == BaseConsDefine.PARTITION_MODE.FIX.value()) {
				sb.append(parValue);// 固定值的话，填了什么值这里就显示什么值,这个case下其实指不指定字段都没关系了
			}
		}
		return sb.toString();
	};

	public static String getInsertPartitionName(DBEntity entity) throws Exception {
		SysPartition partition = getPartition(entity.getClass());
		if (partition == null)
			return null;
		List<SysPartitionDetail> detailList = getPartitionDetailList(partition.getId());
		if (NullUtil.isEmpty(detailList))
			return null;
		
		StringBuffer sb = new StringBuffer(64);
		sb.append(DBHelper.getDBTableName(entity.getClass()));
		for (SysPartitionDetail detail : detailList) {
			String colName = detail.getFieldName();
			Object fieldValue = DBHelper.getEntityValue(entity,
					DBHelper.getJFieldByColName(entity.getClass(), colName));
			int mode = detail.getMode();
			String parValue = detail.getValue();

			sb.append("_");
			if (mode == BaseConsDefine.PARTITION_MODE.MOD.value()) {
				// 取模
				Long longValue = (Long) fieldValue;// 取模的话字段值必须是整数类型
				sb.append(longValue % Integer.valueOf(parValue));// 取模的话，value值就是模数
			} else if (mode == BaseConsDefine.PARTITION_MODE.DATE.value()) {
				Date dateValue = (Date) fieldValue;// 按年月的话字段值必须是日期类型
				sb.append(DateUtil.date2Str(dateValue, parValue));// 按年月的话，modValue就是指定分表的年月格式，比如有yyyyMM,yyyyMMdd,注意月份是大写的M
			} else if (mode == BaseConsDefine.PARTITION_MODE.FIX.value()) {
				sb.append(parValue);// 固定值的话，填了什么值这里就显示什么值,这个case下其实指不指定字段都没关系了
			}
		}

		return sb.toString();
	}
}
