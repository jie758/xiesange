package com.xiesange.orm.statement.query;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.FieldPair;
import com.xiesange.orm.pojo.JoinQueryData;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.field.HavingField;
import com.xiesange.orm.statement.field.IQueryField;
import com.xiesange.orm.statement.field.JoinPart;
import com.xiesange.orm.statement.field.OrderField;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.field.summary.SumQueryField;

public class QueryStatement extends BaseDBStatement
{
    private Long id;//主键值，queryById使用
    private DBCondition[] conditions;//查询条件，queryByCondition使用
    private String tableName;//可以指定表名，适用于查询历史表、分表
    //以下是复杂查询所用到的参数
    private QueryStatement subQuery;//嵌套子查询,select * from (select ....)，括号里的子查询
    private String subQueryAlias;//只有subQuery有值的情况下这个字段才有值，表示子查询的别名
	
    private List<IQueryField> queryFields;//需要查询返回的字段，如果为null，表示查询全部。
    private List<JoinPart> joinParts;//join查询用到
    
    private int[] range;//分页,sql中解析为limit x,y
    private List<OrderField> orderFields;//排序条件,sql中解析为order by
    private List<IQueryField> groupFields;//归组字段 ，sql中解析为group by
    private List<HavingField> havingFields;//归组条件，sql中解析为having
   
    
    private Map<String,String> tableAlias;//所操作的表的别名,key=表名,value=别名
    
    public <T extends DBEntity>QueryStatement(Class<T> entityClass,Long id){
    	//按照id查询
    	super(entityClass);
        this.id = id;
    }
    
    public <T extends DBEntity>QueryStatement(Class<T> entityClass,DBCondition...conditions){
    	//按照条件查询
    	super(entityClass);
        this.conditions = conditions;
    }
   
    //完整的查询，支持复杂的整表嵌套查询，即select * from (select * from table1 where xxxx) where yyyy
    public <T extends DBEntity>QueryStatement(QueryStatement queryTable,Long id){
    	super(queryTable.getEntityClass());
    	this.subQuery = queryTable;
    	this.id = id;
    }
    public <T extends DBEntity>QueryStatement(QueryStatement queryTable,DBCondition...conditions){
    	super(queryTable.getEntityClass());
    	this.subQuery = queryTable;
    	this.conditions = conditions;
    }
    
    @Override
    public void parse() throws Exception
    {
        if(NullUtil.isNotEmpty(this.getSQL())){
        	return;//已经parse过sql里会有值，就不重复解析了
        }
        String tableSQL = null;//this.queryST == ? DBHelper.getDBTableName(entityClass):null;
    	String mainTableAlias = null;
        if(this.subQuery==null){
        	tableSQL = NullUtil.isNotEmpty(tableName) ? tableName : DBHelper.getDBTableName(entityClass);
    		
        	mainTableAlias = NullUtil.isEmpty(joinParts) ? null : generateAlias(entityClass.getSimpleName());
    		if(NullUtil.isNotEmpty(mainTableAlias))
    			tableSQL += " "+mainTableAlias;
    	}else{
    		this.subQuery.parse();
    		mainTableAlias = generateAlias("subQueryTable");
    		tableSQL = "("+this.subQuery.getSQL()+") "+mainTableAlias;
    		this.subQueryAlias = mainTableAlias;
    		this.addBindParameters(this.subQuery.getBindParameters());
    	}
        
        StringBuffer joinSql = new StringBuffer();
        if(NullUtil.isNotEmpty(joinParts)){
        	String joinTableName = null;
			for(JoinPart joinField : joinParts){
				joinTableName = DBHelper.getDBTableName(joinField.getJoinClass());
				joinSql.append("JOIN ")
						.append(joinTableName)
						.append(" ")
						.append(generateAlias(joinField.getJoinClass().getSimpleName()))
						.append(" ON ");
				for(int i=0;i<joinField.getFieldPairs().size();i++){
					FieldPair pair = joinField.getFieldPairs().get(i);
					if(i > 0){
						joinSql.append(" and ");
					}
					joinSql.append(getAlias(pair.getField1().getEntityClass().getSimpleName()))
							.append(".").append(pair.getField1().getColName())
							.append(" = ")
							.append(getAlias(pair.getField2().getEntityClass().getSimpleName()))
							.append(".").append(pair.getField2().getColName());
				}
			}
		}
        
        List<Class<? extends DBEntity>> joinQueryClass = null;
        if(NullUtil.isNotEmpty(joinParts)){
        	joinQueryClass = new ArrayList<Class<? extends DBEntity>>();
        	for(JoinPart join : joinParts){
        		joinQueryClass.add(join.getJoinClass());
        	}
        }
        
        this.appendSQL("SELECT ")
		    	.appendSQL(DBHelper.buildColumnsString(entityClass,joinQueryClass,queryFields,NullUtil.isEmpty(joinQueryClass) ? null : tableAlias))
		    	.appendSQL(" FROM ")
		    	.appendSQL(tableSQL);
        
        if(NullUtil.isNotEmpty(joinSql)){
        	this.appendSQL(" ").appendSQL(joinSql.toString());
        }
        
        //构建where条件
        if(id != null){
        	DBHelper.appendWhereSQL(this, id);
        }else if(NullUtil.isNotEmpty(conditions)){
        	DBHelper.appendWhereSQL(this, conditions);
        }
        
        
        
        if(groupFields != null){
        	if(NullUtil.isNotEmpty(groupFields)){
        		this.appendSQL(" GROUP BY ");
            	for(int i=0;i<groupFields.size();i++){
            		if(i > 0){
            			this.appendSQL(",");
            		}
            		this.appendSQL(DBHelper.parseFieldColumnName(groupFields.get(i), tableAlias));
            	}
        	}
        }	
        
        if(NullUtil.isNotEmpty(orderFields)){
        	this.appendSQL(" ORDER BY ");
        	for(int i=0;i<orderFields.size();i++){
        		if(i > 0){
        			this.appendSQL(",");
        		}
        		this.appendSQL(DBHelper.parseFieldColumnName(orderFields.get(i).getJfield(), tableAlias));
        		if(!orderFields.get(i).isAsc()){
        			this.appendSQL(" DESC");
        		}
        	}
        }
        
        if(NullUtil.isNotEmpty(havingFields)){
    		this.appendSQL(" HAVING ");
    		for(int i=0;i<havingFields.size();i++){
    			HavingField havingCondition = havingFields.get(i);
    			if(i > 0){
        			this.appendSQL(" AND ");
        		}
    			 DBOperator operator = havingCondition.getOperator();
                 IStatQueryField field = havingCondition.getField();
                 Object value = havingCondition.getValue();
                
                 this.appendSQL(DBHelper.parseFieldColumnName(field, tableAlias))
        		 	 .appendSQL(" ")
        		 	 .appendSQL(operator.getExpression())
        		 	 .appendSQL(" ");
                 
                 if(value instanceof IStatQueryField){
             		//表示是两个字段之间的比较，比如where sum > receivingSum
             		this.appendSQL(DBHelper.parseFieldColumnName((IStatQueryField)value, tableAlias));
             	}else{
             		if(operator == DBOperator.LIKE){
                 		value = ((String)value).replaceAll("_", "\\\\_");//mysql对于"_"当做特殊字符处理的，因此要做转移，否则%A_B%会把"A B" "AB"都查询出来
                 	}
             		this.appendBindSQL("?",((SumQueryField)field).getJField(),value);
             	}
    		}
    	}
        	
        if(range != null){
        	this.appendSQL(" LIMIT ")
        		.appendSQL(String.valueOf(range[0]))
        		.appendSQL(",")
        		.appendSQL(String.valueOf(range[1]));
        }
        
    }
    
    
    
    @Override
    public Object access(PreparedStatement ps) throws Exception
    {
        return ps.executeQuery();
    }


    @Override
    public Object wrap(Object accessResult) throws Exception
    {
        List<?> list = null;
    	if(NullUtil.isEmpty(joinParts)){
        	list = DBHelper.wrap2Entity(entityClass,(ResultSet)accessResult,queryFields);
        }else{
        	List<Class<? extends DBEntity>> joinClassList = new ArrayList<Class<? extends DBEntity>>();
        	for(JoinPart join : joinParts){
        		joinClassList.add(join.getJoinClass());
        	}
        	list = DBHelper.wrap2JoinEntity(joinClassList, tableAlias,entityClass, (ResultSet)accessResult, queryFields);
        }
    	if(NullUtil.isEmpty(list))
    		return null;
        return id != null ? list.get(0) : list;
    }

	@Override
	public long count(Object wrapResult) {
		if(wrapResult == null)
			return 0;
		else if(wrapResult instanceof List){
			//这里还要做个特殊判断，如果是仅仅查询count(*)来统计的，那么这里的记录数就要从返回的记录中得出真正的统计总数，否则按照默认逻辑总是返回一条记录的
			if(NullUtil.isNotEmpty(queryFields) && queryFields.size()==1 && queryFields.get(0) instanceof CountQueryField){
				Object el = ((List)wrapResult).get(0);
				if(el instanceof DBEntity){
					CountQueryField countQueryF = (CountQueryField)queryFields.get(0);
					String queryName = NullUtil.isNotEmpty(countQueryF.getAliasName())?countQueryF.getAliasName():countQueryF.getColName();
					DBEntity countEntity = (DBEntity)el;
					return ((BigDecimal)countEntity.getAttr(queryName)).longValue();
				}else{
					JoinQueryData joinData = (JoinQueryData)el;
					return ((BigDecimal)joinData.getResult(entityClass).getAttr(queryFields.get(0).getColName())).longValue();
				}
			}else{
				return ((List<?>)wrapResult).size();
			}
			
		}else if(wrapResult instanceof DBEntity){
			return 1;
		}else{
			return -1;
		}
	}
	
	public QueryStatement appendTable(String tableName){
		this.tableName = tableName;
		return this;
	}
	
	//增加主表的所有字段作为查询字段
	public QueryStatement appendAllQueryField(){
		return appendAllQueryField(this.getEntityClass());
	}
	//增加注定表的所有字段作为查询字段，适用于join查询的场景
	public QueryStatement appendAllQueryField(Class<? extends DBEntity> clz){
		if(queryFields == null){
    		queryFields = new ArrayList<IQueryField>();
    	}
		queryFields.addAll(DBHelper.getAllJFieldList(clz));
		return this;
	}
	
	
	public QueryStatement appendQueryField(IQueryField... fields){
    	if(queryFields == null){
    		queryFields = new ArrayList<IQueryField>();
    	}
    	for(IQueryField field : fields){
    		queryFields.add(field);
    	}
    	return this;
    }
    
    public QueryStatement appendRange(int startIndex,int count){
    	range = new int[]{startIndex,count};
    	return this;
    }
    
    public QueryStatement appendRange(int[] pageInfo){
    	range = new int[]{pageInfo[0],pageInfo[1]};
    	return this;
    }
    
    //添加升序排序字段
    public QueryStatement appendOrderField(IQueryField... jfields){
    	if(orderFields == null){
    		orderFields = new ArrayList<OrderField>();
    	}
    	for(IQueryField f : jfields){
    		orderFields.add(new OrderField(f,true));
    	}
    	
    	return this;
    }
    //添加降序序排序字段
    public QueryStatement appendOrderFieldDesc(IQueryField... jfields){
    	if(orderFields == null){
    		orderFields = new ArrayList<OrderField>();
    	}
    	for(IQueryField f : jfields){
    		orderFields.add(new OrderField(f,false));
    	}
    	return this;
    }
    //添加排序字段
    public QueryStatement appendOrderField(OrderField... orderFieldArr){
    	if(orderFields == null){
    		orderFields = new ArrayList<OrderField>();
    	}
    	if(NullUtil.isNotEmpty(orderFieldArr)){
    		for(OrderField orderField : orderFieldArr){
    			orderFields.add(orderField);
    		}
    	}
    	
    	return this;
    }
    
    public QueryStatement appendGroupField(IQueryField... fields){
    	if(groupFields == null){
    		groupFields = new ArrayList<IQueryField>();
    	}
    	for(IQueryField field : fields){
    		groupFields.add(field);
    	}
    	return this;
    }
    public QueryStatement appendHavingField(IStatQueryField field,DBOperator operator,Object value){
    	if(havingFields == null){
    		havingFields = new ArrayList<HavingField>();
    	}
    	havingFields.add(new HavingField(field,operator,value));
    	return this;
    }
    
    public QueryStatement appendJoin(Class<? extends DBEntity> joinTable,FieldPair... fields){
    	if(joinParts == null){
    		joinParts = new ArrayList<JoinPart>();
    	}
    	joinParts.add(new JoinPart(joinTable,fields));
    	
    	return this;
    }
    
    public QueryStatement appendJoin(JoinPart... joinPart){
    	if(joinParts == null){
    		joinParts = new ArrayList<JoinPart>();
    	}
    	for(JoinPart part : joinPart){
    		joinParts.add(part);
    	}
    	
    	return this;
    }
    
	public List<IQueryField> getQueryFields() {
		return queryFields;
	}

	public void setQueryFields(List<IQueryField> queryFields) {
		this.queryFields = queryFields;
	}

	public int[] getRange() {
		return range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}

	public List<OrderField> getOrderFields() {
		return orderFields;
	}

	public void setOrderFields(List<OrderField> orderFields) {
		this.orderFields = orderFields;
	}


	public List<IQueryField> getGroupFields() {
		return groupFields;
	}

	public void setGroupFields(List<IQueryField> groupFields) {
		this.groupFields = groupFields;
	}

	public List<HavingField> getHavingFields() {
		return havingFields;
	}

	public void setHavingFields(List<HavingField> havingFields) {
		this.havingFields = havingFields;
	}

	public String getSubQueryAlias() {
		return subQueryAlias;
	}
	
	private String generateAlias(String name){
		if(tableAlias == null){
			tableAlias = new HashMap<String,String>();
		}
		String alias = "t"+(tableAlias.size()+1);
		tableAlias.put(name,alias);
		return alias;
	}
	public String getAlias(String name){
		if(NullUtil.isEmpty(tableAlias)){
			return null;
		}
		return tableAlias.get(name);
	}
	
}
