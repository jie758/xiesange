package com.xiesange.baseweb;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.component.PartitionCmp;
import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.baseweb.define.BaseConsDefine.DB_OPER;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.SpringUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.dao.IDao;
import com.xiesange.orm.pojo.JoinQueryData;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.delete.DeleteStatement;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.IQueryField;
import com.xiesange.orm.statement.field.JoinPart;
import com.xiesange.orm.statement.field.OrderField;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.insert.InsertBatchStatement;
import com.xiesange.orm.statement.insert.InsertStatement;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateBatchStatement;
import com.xiesange.orm.statement.update.UpdateStatement;


public class CommonDao implements IDao{
	protected Logger logger = LogUtil.getLogger(this.getClass());
	//##################### insert系列
	public <T extends DBEntity> int insert(T entity) throws Exception{
		ETUtil.dealInsertEntity(entity);
		//解析分表名称
		String partitionName = PartitionCmp.getInsertPartitionName(entity);
		try{
			return (Integer)new InsertStatement(entity,partitionName).execute();
		}catch(Exception e){
			String message = e.getCause().getMessage();
			if(Pattern.matches(".+Table '.+' doesn't exist$",message)){
				//说明是分表没有创建，则自动创建
				String sql = DBHelper.buildPartitionTableCreateSQL(entity.getClass(), partitionName);
				logger.info("create partition table sql : "+sql);
				SpringUtil.getJdbcTemplateBean().execute(sql);
				return (Integer)new InsertStatement(entity,partitionName).execute();
			}else{
				throw e;
			}
		}
		
	}
	
	/*public <T extends DBEntity> int insert(T entity,String tableName) throws Exception {
		
	    return (Integer)new InsertStatement(entity,tableName).execute();
	}*/
	
	
	public <T extends DBEntity> int insertBatch(List<T> entityList) throws Exception {
		return insertBatch(entityList,null);
	}
	
	public <T extends DBEntity> int insertBatch(List<T> entityList,String tableName) throws Exception {
		if(NullUtil.isEmpty(entityList))
    		return 0;
    	if(entityList.size() == 1){
    		return insert(entityList.get(0));
    	}else{
    		//一次性把列表中没有ID的对象提取出来并从数据库获取sequence
    		int newIdCount = 0;//需要新生成ID的个数
    		for(T entity : entityList){
    			if(entity.getId() == null){
    				newIdCount++;
    			}
    		}
    		long[] newIds = getSequence(entityList.get(0).getClass(), newIdCount);
    		int newIdIndex = 0;
    		for(T entity : entityList){
    			if(entity.getId() == null){
    				entity.setId(newIds[newIdIndex]);
    				newIdIndex++;
    			}
    			ETUtil.dealInsertEntity(entity);
        	}
    		return (Integer)new InsertBatchStatement(entityList,tableName).execute();
    	}
	}
	//##################### insert系列 结束###############################
	
	
	//##################### query系列 ###############################
	/**
	 * 查询出某张表中所有记录，通过指定表名的快捷方式查询
	 */
	public <T extends DBEntity> List<T> queryAll(Class<T> dbClass,BaseJField...jfs) throws Exception {
		QueryStatement st = ETUtil.createQueryStatement(dbClass);
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		return (List<T>)st.execute();
	}
	/**
	 * 查询出某张表中所有记录。
	 */
	/*public <T extends DBEntity> List<T> queryAll(QueryStatement statement) throws Exception {
		return (List<T>)statement.execute();
	}*/
	
	/**
	 * 根据id查询出某条记录
	 */
	public <T extends DBEntity> T queryById(Class<T> dbClass, long id,BaseJField...jfs) throws Exception {
		QueryStatement qt = new QueryStatement(dbClass,id);
		if(NullUtil.isNotEmpty(jfs)){
			qt.appendQueryField(jfs);
		}
		return (T)qt.execute();
	}
	public <T extends DBEntity> List<T> queryByIds(Class<T> dbClass, Collection<Long> ids,BaseJField...jfs) throws Exception {
		QueryStatement st =new QueryStatement(dbClass,new DBCondition(DBHelper.getIdJField(dbClass),ids,DBOperator.IN));
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		return (List<T>)st.execute();
	}
	/**
	 * 根据条件查询出符合的记录
	 */
	public <T extends DBEntity> List<T> query(Class<T> dbClass,DBCondition... conditions) throws Exception {
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("query方法中条件参数不能为空");
		}
		QueryStatement st = ETUtil.createQueryStatement(dbClass,conditions);
		return (List<T>)st.execute();
	}
	/**
	 * 根据条件查询出符合的记录，且可以指定排序字段
	 */
	public <T extends DBEntity> List<T> query(Class<T> dbClass,OrderField[] orderFields,DBCondition... conditions) throws Exception {
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("query方法中条件参数不能为空");
		}
		QueryStatement st = ETUtil.createQueryStatement(dbClass,conditions);
		if(NullUtil.isNotEmpty(orderFields)){
			st.appendOrderField(orderFields);
		}
		return (List<T>)st.execute();
	}
	/**
	 * 最完整的查询方法，支持所有的场景。具体请参见QueryStatement的说明
	 */
	public <T extends DBEntity> List<T> query(QueryStatement statement) throws Exception {
		return (List<T>)statement.execute();
	}
	
	
	/**
	 * 根据条件查询，但做多只返回一条记录。多见于一些用唯一索引字段查询的场景。
	 * 比如用code来查询，整个表中code值都是唯一的，因此只会返回一条。
	 * 和query方法的区别是，querySingle会加上limit 0,1作为记录数的限制，因此如果确定只需要一条数据的时候请使用querySingle，效率更高
	 * @author wuyj 2013-10-25
	 * @param <T>
	 * @param valueEntity,存放需要更新的值
	 * @param id
	 * @throws Exception
	 */
	public <T extends DBEntity>T querySingle(Class<T> dbClass,DBCondition...conditions) throws Exception{
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("querySingle方法中条件参数不能为空");
		}
		QueryStatement st = ETUtil.createQueryStatement(dbClass,conditions);
		List<T> list = query(st.appendRange(0, 1));//指定只查询一条
	    return NullUtil.isEmpty(list) ? null : list.get(0);
	}
	
	/**
	 * 方便某些表中根据非id字段查询，但却又只能查出一条数据的场景。
	 * 比如select * from order where code = 'S0001'
	 * @param dbClass
	 * @param jfield
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public <T extends DBEntity>T querySingle(Class<T> dbClass,BaseJField jfield,Object value) throws Exception{
		if(jfield == null || value == null){
			throw CommonUtil.buildException("querySingle方法中条件参数不能为空");
		}
		QueryStatement st = ETUtil.createQueryStatement(dbClass,new DBCondition(jfield,value));
		List<T> list = query(st.appendRange(0, 1));//指定只查询一条
	    return NullUtil.isEmpty(list) ? null : list.get(0);
	}
	
	/**
	 * 统计记录数
	 * @param dbClass
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public <T extends DBEntity>long queryCount(Class<T> dbClass,DBCondition...conditions) throws Exception{
		List<BigDecimal> result = queryStat(dbClass,new IStatQueryField[]{CountQueryField.getInstance()},conditions);
    	if(NullUtil.isEmpty(result) || result.get(0) == null)
    		return 0;
		return ((BigDecimal)result.get(0)).longValue();
	}
	
	public <T extends DBEntity>long queryJoinCount(Class<T> dbClass,JoinPart[] joinParts,DBCondition...conditions) throws Exception{
		List<BigDecimal> result = queryJoinStat(dbClass,new IStatQueryField[]{CountQueryField.getInstance()},joinParts,conditions);
    	if(NullUtil.isEmpty(result) || result.get(0) == null)
    		return 0;
		return ((BigDecimal)result.get(0)).longValue();
	}
	
	public List<JoinQueryData> queryJoin(QueryStatement st) throws Exception{
		return (List<JoinQueryData>)st.execute();
	}
	
	/**
     * 统计型查询，包括count(*),max(),min(),sum等等。
     * 适用于sql模式:select sum(field),count(),max() from table where condition
     * @author wuyujie Jan 2, 2015 11:40:05 AM
     * @param dbClass
     * @param statFields,比如是实现了IStatQueryField接口的字段
     * @param conditions
     * @return,按照字段顺序，返回统计后的数值数组
     * @throws Exception
     */
    public <T extends DBEntity>List<BigDecimal> queryStat(Class<T> dbClass,IStatQueryField[] statFields,DBCondition...conditions) throws Exception{
    	QueryStatement queryST = ETUtil.createQueryStatement(dbClass,conditions);
    	queryST.appendQueryField(statFields);
    	List<T> entityList = (List<T>)queryST.execute();
    	T entity = entityList.get(0);
    	List<BigDecimal> result = new ArrayList<BigDecimal>();
    	for(IStatQueryField field : statFields){
    		result.add((BigDecimal)entity.getAttr(NullUtil.isEmpty(field.getAliasName()) ? field.getColName() : field.getAliasName()));
    	}
    	return result;
	}
    
    public <T extends DBEntity>List<BigDecimal> queryJoinStat(Class<T> mainClass,IStatQueryField[] statFields,JoinPart[] joinParts,DBCondition...conditions) throws Exception{
    	QueryStatement queryST = ETUtil.createQueryStatement(mainClass,conditions);
    	queryST.appendQueryField(statFields).appendJoin(joinParts);
    	List<JoinQueryData> entityList = (List<JoinQueryData>)queryST.execute();
    	JoinQueryData joinData = entityList.get(0);
    	List<BigDecimal> result = new ArrayList<BigDecimal>();
    	T mainEntity = joinData.getResult(mainClass);
    	for(IStatQueryField field : statFields){
    		result.add((BigDecimal)mainEntity.getAttr(NullUtil.isEmpty(field.getAliasName()) ? field.getColName() : field.getAliasName()));
    	}
    	return result;
	}
	//##################### query系列 结束###############################
	
	//##################### update系列 ###############################
	public <T extends DBEntity> int updateAll(T valueEntity) throws Exception {
		move2his(valueEntity.getClass(),BaseConsDefine.DB_OPER.UPDATE,null);
		
		ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(valueEntity).execute();
	}
	
	public <T extends DBEntity> int updateById(T valueEntity, long id) throws Exception {
		move2his(valueEntity.getClass(),BaseConsDefine.DB_OPER.UPDATE,id);
		
		ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(valueEntity,id).execute();
	}
	
	public <T extends DBEntity> int updateByIds(T valueEntity, Collection<Long> ids) throws Exception {
		DBCondition cond = new DBCondition(DBHelper.getIdJField(valueEntity.getClass()),ids,DBOperator.IN);
		
		move2his(valueEntity.getClass(),BaseConsDefine.DB_OPER.UPDATE,null,cond);
		
		ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(valueEntity,cond).execute();
	}
	
	public <T extends DBEntity> int updateById(FieldUpdateExpression[] values,long id) throws Exception {
		move2his(values[0].getJf().getEntityClass(),BaseConsDefine.DB_OPER.UPDATE,id);
		
		//ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(values,id).execute();
	}
	public <T extends DBEntity> int update(T valueEntity,DBCondition... conditions) throws Exception {
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("update方法中条件参数不能为空");
		}
		
		move2his(valueEntity.getClass(),BaseConsDefine.DB_OPER.UPDATE,null,conditions);
		
		ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(valueEntity,conditions).execute();
	}
	public <T extends DBEntity> int update(FieldUpdateExpression[] values,DBCondition... conditions) throws Exception {
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("update方法中条件参数不能为空");
		}
		
		move2his(values[0].getJf().getEntityClass(),BaseConsDefine.DB_OPER.UPDATE,null,conditions);
		
		//ETUtil.dealUpdateEntity(valueEntity);
		return (Integer)new UpdateStatement(values,conditions).execute();
	}
	
	public <T extends DBEntity> int updateBatch(UpdateStatement... statements) throws Exception {
		for(UpdateStatement updateInfo : statements){
			ETUtil.dealUpdateEntity(updateInfo.getValueEntity());
		}
		return (Integer)new UpdateBatchStatement(statements).execute();
	}
	//##################### update系列 结束 ###############################
	
	
	
	
	//##################### delete系列 ###############################
	public <T extends DBEntity> int deleteAll(Class<T> dbClass) throws Exception {
		move2his(dbClass,BaseConsDefine.DB_OPER.REMOVE,null);
		return (Integer)new DeleteStatement(dbClass).execute();
	}
	
	public <T extends DBEntity> int deleteById(Class<T> dbClass, long id) throws Exception {
		move2his(dbClass,BaseConsDefine.DB_OPER.REMOVE,id);
		return (Integer)new DeleteStatement(dbClass,id).execute();
	}
	public <T extends DBEntity> int deleteByIds(Class<T> dbClass, Collection<Long> ids) throws Exception {
		if(ids.size() == 1){
			return deleteById(dbClass,ids.iterator().next());
		}else{
			DBCondition conds = new DBCondition(DBHelper.getIdJField(dbClass),ids,DBOperator.IN);
			move2his(dbClass,BaseConsDefine.DB_OPER.REMOVE,null,conds);
			return (Integer)new DeleteStatement(dbClass,conds).execute();
		}
	}
	
	public <T extends DBEntity> int delete(Class<T> dbClass,DBCondition... conditions) throws Exception {
		if(NullUtil.isEmpty(conditions)){
			throw CommonUtil.buildException("delete方法中条件参数不能为空");
		}
		move2his(dbClass,BaseConsDefine.DB_OPER.REMOVE,null,conditions);
		return (Integer)new DeleteStatement(dbClass,conditions).execute();
	}
	//##################### delete系列 结束 ###############################
	
	//##################### sequence系列 结束 ###############################
	/**
	 * 获取某张表对应的sequence,这是获取每张表的生成记录时的主键字段值，因此是全局的
	 * @author wuyj 2013-11-1
	 * @param clz，表对应的数据库实体类
	 * @return
	 * @throws Exception
	 */
	public long getSequence(Class<? extends DBEntity> clz) throws Exception{
		return getSequence(clz,1)[0];
	}
	public long[] getSequence(Class<? extends DBEntity> clz,int count) throws Exception{
		String index = DBHelper.getSequenceManager().getDBEntityKey(clz);
		if(NullUtil.isEmpty(index)){
			throw CommonUtil.buildException(clz.getSimpleName()+"没有定义sequence索引!请检查entity_seq.properties文件。");
		}
		
		return getSequence(index,count);
	}
	
	public long getSequence(String key) throws Exception{
	    return getSequence(key,1)[0];
	}
	
	public long[] getSequence(String key,int count) throws Exception{
	    return DBHelper.getSequenceManager().getNext(key,count);
	}
	
	
	/**
	 * 把原数据移到历史表。
	 * 历史表的命名规则是：原表名_his;
	 * 历史表的字段规则是：原表所有字段，his_time(移到历史表的操作时间),oper_sn(移到历史表操作的流水号),oper_type(操作类型，1-更新，2-删除)
	 * 历史表不要建任何约束性主键或者索引
	 * @param entityClass
	 * 			要移到历史表的记录的原表
	 * @param operType
	 * 			1-更新
	 * 			2-删除
	 * @param id
	 *			把某条id确定的记录移到历史表，和conditions互斥
	 * @param conditions
	 * 			把某批符合条件的记录移到历史表，和id互斥
	 * @throws Exception
	 */
	private <T extends IQueryField>void move2his(Class<? extends DBEntity> entityClass,DB_OPER operType,Long id,DBCondition...conditions) throws Exception{
		/*String hisTableName = null;
		List<T> insertFields = null;
		QueryStatement query_st = null;
		
		try{
			//Class<? extends DBEntity> hisClass = DBHelper.buildHisClass(entityClass);
			query_st = id != null ? new QueryStatement(entityClass,id)
					: new QueryStatement(entityClass,conditions);
			List<BaseJField> jFields = DBHelper.getAllJFieldList(entityClass);
			for(BaseJField jf : jFields){
				query_st.appendQueryField(jf);
			}
			
			long sn = ETUtil.getRequestContext()==null?-1:ETUtil.getRequestContext().getSn(true);
			
			CustDBField hisTimeField = new CustDBField("str_to_date('"+DateUtil.now19()+"','%Y-%m-%d %H:%i:%s')");
			CustDBField operSnField = new CustDBField("'"+sn+"'");
			IQueryField operTypeField = new CustDBField(String.valueOf(operType.value()));
			
			query_st.appendQueryField(hisTimeField);
			query_st.appendQueryField(operSnField);
			query_st.appendQueryField(operTypeField);
			
			hisTableName = DBHelper.buidDBHisTableName(entityClass,false);
			insertFields = (List<T>)DBHelper.getAllJFieldList(entityClass);
			//加上历史表里的公用字段
			insertFields.addAll((List<T>)DBHelper.getHisTableFields());
			
			new InsertByQueryStatement(hisTableName,insertFields,query_st).execute();
		}catch(Exception e){
			logger.error(e, e);
			String message = e.getCause().getMessage();
			
			if(Pattern.matches(".+Table '.+' doesn't exist$",message)){
				//说明是历史表没有创建，则自动创建
				String sql = DBHelper.buildHisTableCreateSQL(entityClass, hisTableName);
				logger.info("create his table sql : "+sql);
				SpringUtil.getJdbcTemplateBean().execute(sql);
				
				new InsertByQueryStatement(hisTableName,insertFields,query_st).execute();
			}else{
				throw e;
			}
		}*/
	};
	
	public static void createTable(String sql) throws SQLException{
		System.out.println("create table sql:\n"+sql);
		/*Statement st = DBUtil.getConnection().createStatement();
		st.executeUpdate(sql);*/
	}
	
}