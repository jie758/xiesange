package com.xiesange.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;

import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.SpringUtil;
import com.xiesange.orm.annotation.DBFieldAnno;
import com.xiesange.orm.annotation.DBTableAnno;
import com.xiesange.orm.dao.IDao;
import com.xiesange.orm.pojo.JoinQueryData;
import com.xiesange.orm.pojo.NoTimeDate;
import com.xiesange.orm.pojo.SQLBindParameter;
import com.xiesange.orm.sequence.ISequenceManager;
import com.xiesange.orm.sql.BlockDBCondition;
import com.xiesange.orm.sql.BlockDBOrCondition;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.IBatchStatement;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.field.CustDBField;
import com.xiesange.orm.statement.field.IQueryField;
import com.xiesange.orm.statement.field.OrderField;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.query.QueryStatement;

public class DBHelper
{
	public static final String DB_ENTITY_PACKAGE = "com.kingwant.gen.dbentity";
	
	//历史表里的几个通用字段
	/*public static final CustQueryField HIS_FIELD_HISTIME = new CustQueryField("HIS_TIME");
	public static final CustQueryField HIS_FIELD_OPER_SN = new CustQueryField("OPER_SN");
	public static final CustQueryField HIS_FIELD_OPER_TYPE = new CustQueryField("OPER_TYPE");*/
	
	private static List<CustDBField> HISTABLE_FIELDS = null;//new ArrayList<CustQueryField>();
	
	/**
     * 获取某个数据库实体类的实际表名
     * @author wuyj 2013-10-23
     * @param <T>
     * @param entityClass
     * @return
     */
    public static <T extends DBEntity>String getDBTableName(Class<T> entityClass){
        DBTableAnno tableAnno = entityClass.getAnnotation(DBTableAnno.class);
        return tableAnno.name();
    }
    
    /**
     * 获取某个数据库实体类的实际历史表名。历史表的命名规则就是在业务表后面加"_his"
     * @author wuyj 2013-10-23
     * @param <T>
     * @param entityClass
     * @return
     */
    public static <T extends DBEntity>String buidDBHisTableName(Class<T> entityClass,boolean needDate){
        String str = getDBTableName(entityClass)+"_HIS";
        if(needDate){
        	str += "_"+DateUtil.now_yyyymm();
        }
        return str;
    }
    
    /**
     * 根据一个业务实体类，构建出该类的历史实体类。
     * 比如传入SysLogin，则返回一个new SysLoginHis()
     * 历史实体类的类名比业务实体类多了"His"后缀,且必须在同一个包名下。
     * @author wuyujie Feb 22, 2015 1:34:31 PM
     * @param entityClass
     * @return
     * @throws Exception
     */
    public static <T extends DBEntity>T buildHisDBEntity(Class<? extends DBEntity> entityClass) throws Exception{
    	return (T)buildHisClass(entityClass).newInstance();
    }
    
    public static <T extends DBEntity>Class<T> buildHisClass(Class<? extends DBEntity> entityClass) throws Exception{
    	return (Class<T>)Class.forName(entityClass.getName()+"His");
    }
    
    /**
     * 获取某个数据库实体类对应的所有字段拼装串,并以英文逗号分隔。
     * 最终调用buildColumnsString(Class<T> entityClass,BaseJField queryFields)方法
     * @author wuyj 2013-10-24
     * @param <T>
     * @param entityClass
     * @return
     */
    /*public static <T extends DBEntity>String buildColumnsString(Class<T> entityClass){
        return buildColumnsString(entityClass,null,null);
    }*/
    /**
     * 获取某个数据库实体类对应的字段拼装串,并以英文逗号分隔。
     * 
     * 
     * @author wuyj 2013-10-24
     * @param <T>
     * @param entityClassList,是个数组，因为可能涉及到夺标查询
     * @param BaseJField queryFields
     * 			如果传了null，则表示查询当前实体中的所有字段，例如某实体有nanme,age,sex,address几个字段，那么拼装后会字符串"name,age,sex,address",顺序以数据库实体中JField的各个字段顺序一致。
     *          如果传了值，那么只查询传值的那几个字段
     * @return
     */
    public static String buildColumnsString(Class<? extends DBEntity> mainClass,List<Class<? extends DBEntity>> joinClassList,List<? extends IQueryField> queryFields,Map<String,String> alias){
        if(NullUtil.isEmpty(queryFields)){
        	queryFields = parseAllQueryFields(mainClass, joinClassList);
        }
    	
        StringBuffer sb_col_name = new StringBuffer(64);
        for(IQueryField queryField : queryFields){
            if(sb_col_name.length() > 0){
                sb_col_name.append(",");
            }
            if(NullUtil.isNotEmpty(alias)){
            	if(queryField instanceof BaseJField){
            		String tableAlias = alias.get(((BaseJField)queryField).getEntityClass().getSimpleName());
            		sb_col_name.append(tableAlias).append(".");
            		sb_col_name.append(queryField.getColName());
            	}else if(queryField instanceof CountQueryField){
            		sb_col_name.append(((IStatQueryField)queryField).getColName(null));
            	}else if(queryField instanceof IStatQueryField){
            		BaseJField queryJField = ((IStatQueryField)queryField).getQueryField();
            		if(queryJField != null){
            			String tableAlias = alias.get(queryJField.getEntityClass().getSimpleName());
            			sb_col_name.append(((IStatQueryField)queryField).getColName(tableAlias));
            		}
            	}
            }else{
            	sb_col_name.append(queryField.getColName());
            }
            
        }
        return sb_col_name.toString();
    }
    
    /**
     * 获取某个字段对应的实际数据库字段名
     * @author wuyj 2013-10-24
     * @param field
     * @return
     */
    public static String getDBColumnName(Field field){
        DBFieldAnno fieldAnno = field.getAnnotation(DBFieldAnno.class);
        return fieldAnno.name();
    }
    /**
     * 获取某个JField字段对应的实际数据库字段名
     * 性能高于getDBColumnName(Field field)
     * @author wuyj 2013-10-25
     * @param dbfield
     * @return
     */
    public static String getDBColumnName(BaseJField dbfield){
        return dbfield.getColName();
    }
    
    /**
     * 获取一个数据库实体类对应的所有JField枚举对象
     * @author wuyj 2013-10-25
     * @param <T>
     * @param entityClass
     * @return
     */
    public static List<BaseJField> getAllJFieldList(Class<? extends DBEntity> entityClass){
        Class<?>[] clzs = entityClass.getDeclaredClasses();//获取实体类中定义的JField内部类,只有一个
        for(Class<?> clz : clzs){
            if(BaseJField.class.isAssignableFrom(clz)){
            	BaseJField[] enumFields = (BaseJField[])clz.getEnumConstants();
            	List<BaseJField> result = new ArrayList<BaseJField>();
            	for(BaseJField jfield : enumFields){
            		result.add(jfield);
            	}
            	return result;
            }
        }
        return null;
    }
    
    /**
     * 获取某些字段除外的其它字段
     * @param entityClass
     * @param jfs
     * @return
     * @author Wilson 
     * @date 2016年6月29日
     */
    public static List<BaseJField> getAllJFieldListExclude(Class<? extends DBEntity> entityClass,BaseJField...excludeJfs){
        Class<?>[] clzs = entityClass.getDeclaredClasses();//获取实体类中定义的JField内部类,只有一个
        BaseJField[] enumFields = null;
        for(Class<?> clz : clzs){
            if(BaseJField.class.isAssignableFrom(clz)){
            	enumFields = (BaseJField[])clz.getEnumConstants();
            	break;
            }
        }
        
        List<BaseJField> result = new ArrayList<BaseJField>();
    	boolean isExclude = false;
        for(BaseJField jfield : enumFields){
        	isExclude = false;
    		for(BaseJField exclude : excludeJfs){
    			if(jfield == exclude){
    				isExclude = true;
    				break;
    			}
    		}
    		if(!isExclude){
    			result.add(jfield);
    		}
    	}
    	return result;
    }
    
    public static BaseJField[] getAllJFieldsExclude(Class<? extends DBEntity> entityClass,BaseJField...excludeJfs){
    	List<BaseJField> jflist = getAllJFieldListExclude(entityClass,excludeJfs);
    	return jflist.toArray(new BaseJField[jflist.size()]);
    }
    /**
     * 获取某个数据库实体类中的对应名称的JField对象
     * 如果在能够获取实体对象的情况下，推荐使用。如果找不到字段则会抛出异常
     * @author wuyj 2013-10-25
     * @param <T>
     * @param entityClass
     * @param javaFieldName
     * @return
     * @throws Exception 
     */
    public static <T extends DBEntity>BaseJField getJField(Class<T> entityClass,String javaFieldName) throws Exception{
    	return getJField(entityClass,javaFieldName,true);
    }
    
    /**
     * 获取某个数据库实体类中的对应名称的JField对象
     * 如果在能够获取实体对象的情况下，推荐使用。如果找不到字段则会根据参数是否抛出异常
     * @author wuyj 2013-10-25
     * @param <T>
     * @param entityClass,需要查找字段的实体字段
     * @param javaFieldName,需要查找的字段名称，是java字段名称，不区分大小写
     * @param throwExcepNotFound,找不到字段是否抛出异常
     * @return
     * @throws Exception 
     */
    public static <T extends DBEntity>BaseJField getJField(Class<T> entityClass,String javaFieldName,boolean throwExcepNotFound) throws Exception{
        List<BaseJField> jfs = getAllJFieldList(entityClass);
        for(BaseJField jf : jfs){
            if(jf.getName().equalsIgnoreCase(javaFieldName)){
                return jf;
            }
        }
        
        if(throwExcepNotFound){
        	throw new Exception("Can not find field:【"+javaFieldName+"】 , Class:"+entityClass.getSimpleName());
            
        }else{
        	return null;
        }
    }
    
    /**
     * 获取历史表中的通用字段。所有历史表的通用字段顺序要保持一致，否则处理的时候会出错的。因此提供一个方法按照固定顺序组织好列表返回
     * @return
     * @throws Exception
     * @author Wilson Wu
     * @date 2015年9月11日
     */
    public static List<CustDBField> getHisTableFields(){
    	if(HISTABLE_FIELDS == null){
    		HISTABLE_FIELDS = new ArrayList<CustDBField>();
    		HISTABLE_FIELDS.add(new CustDBField("HIS_TIME",null,"DATETIME"));
    		HISTABLE_FIELDS.add(new CustDBField("OPER_SN",null,"BIGINT"));
    		HISTABLE_FIELDS.add(new CustDBField("OPER_TYPE",null,"TINYINT"));
    	}
        return HISTABLE_FIELDS;
    }
    
    /**
     * 根据某个数据库中的字段名称，获取到对应的BaseJField对象。名称匹配不区分大小写。
     * @author wuyujie Nov 21, 2014 11:42:09 AM
     * @param dbColumnName
     * @return
     */
    public static BaseJField getJFieldByColName(Class<? extends DBEntity> entityClass,String dbColumnName){
    	List<BaseJField> allfds = getAllJFieldList(entityClass);
    	for(BaseJField fd : allfds){
    		if(fd.getColName().equalsIgnoreCase(dbColumnName) || fd.getName().equalsIgnoreCase(dbColumnName)){
    			return fd;
    		}
    	}
    	return null;
    }
    /**
     * 获取某个数据库实体类中的对应名称的多个JField对象
     * @author wuyj 2014-1-1
     * @param <T>
     * @param entityClass，数据库实体类
     * @param javaFieldNames,需要获取的多个field名称
     * @return
     * Map对象,其中key=javaFieldName,value=BaseJField
     */
    public static <T extends DBEntity>Map<String,BaseJField> getJFields(Class<T> entityClass,String... javaFieldNames){
    	List<BaseJField> jfs = getAllJFieldList(entityClass);
        Map<String,BaseJField> result = new HashMap<String,BaseJField>();
        for(BaseJField jf : jfs){
            String jname = jf.getName();
            if(CommonUtil.isIn(jname, javaFieldNames)){
                result.put(jname, jf);
            }
        }
        return result;
    }
    /**
     * 获取ID字段的jfield对象。数据库实体对象中都有ID字段,因此获取ID字段比较特殊,单独抽取出方法。
     * @author wuyj 2013-10-25
     * @param <T>
     * @param entity
     * @return
     * @throws Exception 
     */
    public static <T extends DBEntity>BaseJField getIdJField(Class<T> entityClass) throws Exception{
        return getJField(entityClass,"ID");
    }
    
    
    /**
     * 获取Tenant字段（租户ID）的jfield对象,具体参见getIdJField(Class<T> entityClass)
     * @author wuyj 2013-10-25
     * @param <T>
     * @param entity
     * @return
     * @throws Exception 
     */
    public static <T extends DBEntity>BaseJField getIdJField(T entity) throws Exception{
        return getIdJField(entity.getClass());
    }
    
    public static <T extends DBEntity>BaseJField getTenantJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
        return getJField(entityClass,"TENANTID",throwExcepNotFound);
    }
    public static <T extends DBEntity>BaseJField getCreateTimeJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
        return getJField(entityClass,"CREATETIME",throwExcepNotFound);
    }
    public static <T extends DBEntity>BaseJField getUpdateTimeJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
        return getJField(entityClass,"UPDATETIME",throwExcepNotFound);
    }
    public static <T extends DBEntity>BaseJField getSnJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
        return getJField(entityClass,"SN",throwExcepNotFound);
    }
    public static <T extends DBEntity>BaseJField getStsJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
        return getJField(entityClass,"STS",throwExcepNotFound);
    }
    
    public static <T extends DBEntity>BaseJField getPYJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
    	return getJField(entityClass,"PY",throwExcepNotFound);
    }
    public static <T extends DBEntity>BaseJField getPYHeaderJField(Class<T> entityClass,boolean throwExcepNotFound) throws Exception{
    	return getJField(entityClass,"PYHEADER",throwExcepNotFound);
    }
    
    private static void appendWhereCondition(BaseDBStatement statement,boolean isOr,DBCondition...conditions) throws Exception{
    	int cond_length = conditions.length;
    	boolean isFirst = true;
        for(int i=0;i<cond_length;i++){
            DBCondition condition = conditions[i];
            if(condition == null)
            	continue;
            if(!isFirst){
            	statement.appendSQL(isOr ? " OR " : " AND ");
            }
            if(condition instanceof BlockDBCondition || condition instanceof BlockDBOrCondition){
            	statement.appendSQL("1=2");
            }else if(NullUtil.isEmpty(condition.getSubConditions())){
                DBOperator operator = condition.getOperator();
                BaseJField jfield = condition.getJField();
                Object value = condition.getValue();
                String fieldAlias = null;
                
                if(statement instanceof QueryStatement && ((QueryStatement)statement).getSubQueryAlias() != null){
        			statement.appendSQL(((QueryStatement)statement).getSubQueryAlias()).appendSQL(".");
        		}
                if(jfield instanceof BaseJField && statement instanceof QueryStatement){
                	fieldAlias = ((QueryStatement)statement).getAlias(jfield.getEntityClass().getSimpleName());
                }
                if(NullUtil.isNotEmpty(fieldAlias)){
                	statement.appendSQL(fieldAlias).appendSQL(".");
                }
                statement.appendSQL(jfield.getColName())
                		 .appendSQL(" ")
                		 .appendSQL(operator.getExpression())
                		 .appendSQL(" ");
                if(operator == DBOperator.IS_NULL || operator == DBOperator.IS_NOT_NULL){
                	;
                }else if(operator == DBOperator.IS && value != null && value.toString().equalsIgnoreCase("null")){
                	statement.appendSQL("NULL");
                }else if(operator.isSingleValue()){
                	if(value instanceof BaseJField){
                		//表示是两个字段之间的比较，比如where sum > receivingSum
                		statement.appendSQL(((BaseJField)value).getColName());
                	}else{
                		if(operator == DBOperator.LIKE){
                    		value = ((String)value).replaceAll("_", "\\\\_");//mysql对于"_"当做特殊字符处理的，因此要做转移，否则%A_B%会把"A B" "AB"都查询出来
                    	}
                    	statement.appendBindSQL("?",jfield,value);
                	}
                }else{
                    if(value == null){
                    	statement.appendSQL("()");
                    }else{
                        Object[] arr = null;
                        statement.appendSQL("(");
                        if(value instanceof QueryStatement){
                        	//嵌套查询
                        	QueryStatement queryST = (QueryStatement)value;
                        	queryST.parse();
                        	statement.appendSQL(queryST.getSQL());
                        	statement.addBindParameters(queryST.getBindParameters());
                        }else{
                        	if(value.getClass().isArray()){
							    arr = (Object[])value;
							}else if(value instanceof Collection){
							    arr = ((Collection<?>)value).toArray(new Object[((Collection<?>)value).size()]);
							}
							 
							int length = arr.length;
							for(int k=0;k<length;k++){
							    Object valueItem = arr[k];
							    if(k > 0)
							    	statement.appendSQL(",");
							    statement.appendBindSQL("?",jfield,valueItem);
							}
                        }
                        statement.appendSQL(")");
                    }
                }
            }else{
            	statement.appendSQL("(");
            	appendWhereCondition(statement,condition instanceof DBOrCondition,condition.getSubConditions());
                statement.appendSQL(")");
            }
            isFirst = false;
        }
    
    }
    
    /**
     * 像statement里添加where语句的sql以及对应的绑定变量
     * 本方法无论怎么样都会添加”where"串，
     * 后面会根据id或者conditions一边构建sql，一边添加绑定对象
     * @author wuyujie Sep 21, 2014 10:38:21 AM
     * @param statement
     * @param id
     * @param conditions
     * @throws Exception
     */
    public static void appendWhereSQL(BaseDBStatement statement,Long id) throws Exception{
    	statement.appendSQL(" WHERE ");
    	if(statement instanceof QueryStatement && ((QueryStatement)statement).getSubQueryAlias() != null){
			statement.appendSQL(((QueryStatement)statement).getSubQueryAlias()).appendSQL(".");
		}
    	
    	String exp = null;
    	if(statement instanceof QueryStatement){
    		String alias = ((QueryStatement)statement).getAlias(statement.getEntityClass().getSimpleName());
    		exp = NullUtil.isEmpty(alias) ? "ID = ?" : alias+".ID = ?";
    	}else{
    		exp = "ID = ?";
    	}
		statement.appendBindSQL(exp,DBHelper.getIdJField(statement.getEntityClass()),id);
    };
    
    public static void appendWhereSQL(BaseDBStatement statement,DBCondition...conditions) throws Exception{
    	statement.appendSQL(" WHERE ");
    	appendWhereCondition(statement,false,conditions);
    };
    
    /**
     * 只添加绑定变量对象，不添加sql。
     * 这种情况适用于那种sql已经构建好，但绑定变量尚未添加的场景，比如批量插入，批量更新的，sql只需要构建一次，但批量中的每个语句都需要进行变量绑定，因此可以通过调用本方法进行单纯的变量绑定
     * @author wuyujie Sep 21, 2014 10:41:04 AM
     * @param statement
     * @param conditions
     * @throws Exception
     */
    public static void appendWhereBindParameters(BaseDBStatement statement,DBCondition...conditions) throws Exception{
    	int cond_length = conditions.length;
        for(int i=0;i<cond_length;i++){
            DBCondition condition = conditions[i];
            
            if (NullUtil.isEmpty(condition.getSubConditions())){
                DBOperator operator = condition.getOperator();
                BaseJField jfield = condition.getJField();
                Object value = condition.getValue();
                
                if(operator.isSingleValue()){
                	statement.addBindParameter(jfield,value);
                }else if(value != null){
                    Object[] arr = null;
                    if(value.getClass().isArray()){
                        arr = (Object[])value;
                    }else{
                        arr = ((Collection<?>)value).toArray(new Object[((Collection<?>)value).size()]);
                    }
                    int length = arr.length;
                    for(int k=0;k<length;k++){
                        Object valueItem = arr[k];
                        statement.appendBindSQL("?",jfield,valueItem);
                    }
                }
            }else{
            	appendWhereBindParameters(statement,condition.getSubConditions());
            }
        }
    
    }
    
   public static String buildBindSQLWithIndex(String bindSQL){
       Pattern pattern = Pattern.compile("\\?");
       Matcher matcher = pattern.matcher(bindSQL);
       StringBuffer sb = new StringBuffer(128);
       int i=1;
       while(matcher.find()){
           matcher.appendReplacement(sb, "?"+i);
           i++;
       }
       matcher.appendTail(sb);
       return sb.toString();
   }
   
   /**
    * * 把单条操作SQL的绑定变量值组织成如下字符串形式返回：
    * 	[1]ID = 111 (Long) 
     	[2]NAME = null (String)
     	[3]PARENT_MENU_ID = 111 (Long)
    * @author wuyujie Sep 3, 2014 10:03:39 PM
    * @param paramValues
    * @return
 * @throws Exception 
    */
   public static String buildBindParametersString(List<SQLBindParameter> bindParameters) throws Exception{
       StringBuffer sb = new StringBuffer(128);
       int length = bindParameters.size();
       SQLBindParameter param;
       for(int i=0;i<length;i++){
           param = bindParameters.get(i);
           if(i > 0)
               sb.append("\n");
           Object paramValue = param.getValue();
           sb.append(param.toString(i+1,paramValue,param.getJfield()));
       }
       
       return sb.toString();
   }
   
   /**
    * 把批量操作SQL的绑定变量值组织成如下字符串形式返回：
    * Batch,1/2:
    *   [1]ID = 111 (Long) 
        [2]NAME = null (String)
        [3]PARENT_MENU_ID = 111 (Long)
      Batch,2/2:
        [1]ID = 222 (Long) 
        [2]NAME = null (String)
        [3]PARENT_MENU_ID = 222 (Long)
    * @author wuyj 2013-10-26
    * @return
 * @throws Exception 
    */
   public static String buildBatchBindParametersString(List<SQLBindParameter> bindParameters,int batchCount) throws Exception{
	   StringBuffer sb = new StringBuffer(128);
       int length = bindParameters.size();
       Integer itemCount = length/batchCount;
       
       for(int i=0;i<length;i++){
           SQLBindParameter param = bindParameters.get(i);
           int batchIndex = (i / itemCount)+1;
           int paramIndex = (i % itemCount)+1;
           
           if(paramIndex == 1){
               if(batchIndex > 1)
                   sb.append("\n");
               sb.append("Batch - ").append(batchIndex).append("/").append(batchCount).append(":\n");
           }
           
           if(paramIndex > 1)
               sb.append("\n");
           
           sb.append("  ").append(param.toString(paramIndex,null,param.getJfield()));
       }
       
       return sb.toString();
   }
    
    /**
     * 把查询出来的ResultSet包装转换成数据库实体返回
     * @author wuyj 2013-10-24
     * @param <T>
     * @param entityClass
     * @param rs
     * @return
     * @throws Exception
     */
    public static <T extends DBEntity>List<T> wrap2Entity(Class<T> entityClass,ResultSet rs,List<? extends IQueryField> queryFields) throws Exception{
        List<T> result = new ArrayList<T>();
        while(rs.next()){
            T entity = (T)ClassUtil.instance(entityClass);
            if(queryFields == null)
            	queryFields = getAllJFieldList(entityClass);
            for(IQueryField qf : queryFields){
            	BaseJField jf = null;
            	if(qf instanceof IStatQueryField){
            		IStatQueryField statField = (IStatQueryField)qf;
            		String aliasName = statField.getAliasName();
            		String colName = NullUtil.isNotEmpty(aliasName) ? aliasName : qf.getColName();
            		BigDecimal bigdec = rs.getBigDecimal(colName);
            		if(NullUtil.isNotEmpty(aliasName)){
            			entity.addAttribute(aliasName,bigdec);
            		}else if(statField.getQueryField() == null){
            			entity.addAttribute(colName,bigdec);
            		}else{
            			jf = statField.getQueryField();
            			Object val = null;
            			if(jf.getJavaType() == Long.class){
            				val = bigdec.longValue();
                        }else if(jf.getJavaType() == Float.class){
                        	val = bigdec.floatValue();
                        }else if(jf.getJavaType() == Integer.class){
                        	val = bigdec.intValue();
                        }else if(jf.getJavaType() == Double.class){
                        	val = bigdec.doubleValue();
                        }else if(jf.getJavaType() == Short.class){
                        	val = bigdec.shortValue();
                        }
            			setEntityValue(entity,jf, val);
            		}
            		//
            		continue;
            	}else{
            		jf = (BaseJField)qf;
            	}
            	
                String colName = jf.getColName();
                if(jf.getJavaType() == Long.class){
                	Object val = rs.getLong(colName);
                	if(rs.wasNull())
                		continue;//wasNull是判断getXXX取过后的字段值，因为getLong，getInt等数字类型从ResultSet取出来后都是简单类型，如果数据库中是null值，这里取出来会变成0，
                				//如果直接赋值的话，对应字段值就都会被赋值成0，这样就和数据库里的数据不一致了，所以做个判断，如果是null，那么这个字段就不赋值了。
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == String.class){
                	Object val = rs.getString(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Integer.class){
                	Object val = rs.getInt(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Short.class){
                	Object val = rs.getShort(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Date.class){
                	Timestamp ts = rs.getTimestamp(colName);
            		if(rs.wasNull())
                		continue;
            		Date val = new Date(ts.getTime());
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == NoTimeDate.class){
                	Date date = rs.getDate(colName);
                	if(rs.wasNull())
                		continue;
                	NoTimeDate val = new NoTimeDate(date.getTime());
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Float.class){
                	Object val = rs.getFloat(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }
            }
            /*if(NullUtil.isNotEmpty(entity._getSettedValue()) || NullUtil.isNotEmpty(entity.getAttributes())){
            	
            }
*/            
            if(entity._getSettedValue() != null)
        		entity._getSettedValue().clear();
            result.add(entity);
            
        }
        return result;
    }
    
    
    public static List<JoinQueryData> wrap2JoinEntity(List<Class<? extends DBEntity>> joinClassList,Map<String,String> aliasMap,Class<? extends DBEntity> entityClass,ResultSet rs,List<? extends IQueryField> queryFields) throws Exception{
        List<JoinQueryData> result = new ArrayList<JoinQueryData>();
        if(NullUtil.isEmpty(queryFields)){
        	queryFields = parseAllQueryFields(entityClass, joinClassList);
        }
        
        while(rs.next()){
        	JoinQueryData joinRs = new JoinQueryData();
        	DBEntity mainEntity = (DBEntity)ClassUtil.instance(entityClass);//主表对象
            joinRs.addResult(entityClass, mainEntity);//把主表对象加入到join查询结果对象里
            
            /*if(queryFields == null)
            	queryFields = ldgetAllJFields(entityClass);*/
            Class<? extends DBEntity> fieldEntityClz = null;
            String colName = null;
            DBEntity entity = null;
            for(IQueryField qf : queryFields){
            	if(!(qf instanceof BaseJField)){
            		IStatQueryField statField = (IStatQueryField)qf;
            		BaseJField queryJField = statField.getQueryField();
            		String tableAlias = queryJField==null?null:aliasMap.get(queryJField.getEntityClass().getSimpleName());
            		String aliasName = statField.getAliasName();
            		colName = NullUtil.isNotEmpty(aliasName) ? aliasName : ((IStatQueryField)qf).getColName(tableAlias);
            		mainEntity.addAttribute(colName, rs.getBigDecimal(colName));
            		continue;
            	}
            	BaseJField jf = (BaseJField)qf;
            	colName = aliasMap.get(jf.getEntityClass().getSimpleName())+"."+jf.getColName();
            	
                fieldEntityClz = jf.getEntityClass();
                
                if(fieldEntityClz == mainEntity.getClass()){
                	entity = mainEntity;
                }else{
                	entity = joinRs.getResult(fieldEntityClz);
                	if(entity == null){
                		entity = (DBEntity)ClassUtil.instance(fieldEntityClz);
                		joinRs.addResult(fieldEntityClz, entity);
                	}
                }
                if(jf.getJavaType() == Long.class){
                	Object val = rs.getLong(colName);
                	if(rs.wasNull())
                		continue;//wasNull是判断getXXX取过后的字段值，因为getLong，getInt等数字类型从ResultSet取出来后都是简单类型，如果数据库中是null值，这里取出来会变成0，
                				//如果直接赋值的话，对应字段值就都会被赋值成0，这样就和数据库里的数据不一致了，所以做个判断，如果是null，那么这个字段就不赋值了。
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == String.class){
                	Object val = rs.getString(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Integer.class){
                	Object val = rs.getInt(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Short.class){
                	Object val = rs.getShort(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Date.class){
                	Timestamp ts = rs.getTimestamp(colName);
            		if(rs.wasNull())
                		continue;
            		Date val = new Date(ts.getTime());
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == NoTimeDate.class){
                	Date date = rs.getDate(colName);
                	if(rs.wasNull())
                		continue;
                	NoTimeDate val = new NoTimeDate(date.getTime());
                	setEntityValue(entity,jf, val);
                }else if(jf.getJavaType() == Float.class){
                	Object val = rs.getFloat(colName);
                	if(rs.wasNull())
                		continue;
                	setEntityValue(entity,jf, val);
                }
            }
            if(entity != null && entity._getSettedValue() != null)
            	entity._getSettedValue().clear();
            if(mainEntity != null && mainEntity._getSettedValue() != null)
            	mainEntity._getSettedValue().clear();
            result.add(joinRs);
        }
        return result;
    }
    
    public static <T extends DBEntity>void setEntityValue(T entity,BaseJField jf,Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	if(jf == null)
    		return;
    	PropertyUtils.setSimpleProperty(entity, jf.getName(), value);
    }
    
    /**
     * 获取数据库实体中某个字段值
     * @param entity,数据库实体对象
     * @param jf，需要获取的字段的JField对象
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T extends DBEntity>Object getEntityValue(T entity,BaseJField jf) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	return PropertyUtils.getProperty(entity, jf.getName());
    }
    public static <T extends DBEntity>Object getEntityValue(T entity,String fieldName) throws Exception{
    	return PropertyUtils.getProperty(entity, fieldName);
    	//return getEntityValue(entity,DBHelper.getJField(entity.getClass(), fieldName));
    }
    public static <T>List<T> getEntityValues(List<? extends DBEntity> entitys,BaseJField jf) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	if(NullUtil.isEmpty(entitys))
    		return null;
    	List<T> values = new ArrayList<T>();
    	for(DBEntity entity : entitys){
    		values.add((T)getEntityValue(entity,jf));
    	}
    	return values;
    }
    
    
    /*public static String buildQueryTableSQL(Class<? extends DBEntity> entityClass){
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append(DBHelper.buildColumnsString(entityClass));
        sb.append(" from ");
        sb.append(DBHelper.getDBTableName(entityClass));
        return sb.toString();
    }*/
    
    /**
     * 获取数据库实体的所有字段值,以数组形式返回
     * @author wuyj 2013-11-2
     * @param entity
     * @return
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static Object[] buildEntityValues(DBEntity entity) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
    	List<BaseJField> jfs = getAllJFieldList(entity.getClass());
        int length = jfs.size();
        Object[] values = new Object[length];
        for(int i=0;i<length;i++){
            BaseJField jf = jfs.get(i);
            Field field = entity.getClass().getField(jf.getName());
            Object value = field.get(entity);
            values[i] = value;
        }
        return values;
    }
    
    /**
     * 把jfield数组转换成DBCondition数组返回
     * 比如传入一个SysMenu.JField.name,返回一个DBConditon.create(SysMenu.JField.name,null)
     * @author wuyj 2013-11-4
     * @param jfields
     * @return
     */
    public static DBCondition[] parse2DBConditions(BaseJField[] jfields){
        DBCondition[] conds = null;
        if(NullUtil.isNotEmpty(jfields)){
            conds = new DBCondition[jfields.length];
            for(int i=0;i<jfields.length;i++){
                conds[i] = new DBCondition(jfields[i],null);
            }
        }
        return conds;
    }
    
    public static ConditionParser buildConditionParser(String conditionStr){
    	Stack<ConditionParser> stack = new Stack<ConditionParser>();
    	ConditionParser topSqlParser = new ConditionParser();
    	char c;
    	ConditionParser sqlParser;
    	Stack<Boolean> isInstartStack = new Stack<Boolean>();//in语句是可以嵌套的，因此要用堆栈
    	isInstartStack.push(false);
    	System.out.println(conditionStr);
    	for(int i=0;i<conditionStr.length();i++){
    		sqlParser = null;
    		boolean currentIsInStart = isInstartStack.peek();//当前是不是处于in语句中
    		c = conditionStr.charAt(i);
    		if(c == '(' && !currentIsInStart){
    			sqlParser = new ConditionParser();
    			ConditionParser parentParser = stack.size() == 0 ? topSqlParser : stack.peek();
    			parentParser.addNest(sqlParser);
    			stack.push(sqlParser);
    			
    		}else if(c == ')' && !currentIsInStart){
    			stack.pop();
    		}else{
    			sqlParser = stack.size() == 0 ? topSqlParser : stack.peek();
    			if(!currentIsInStart && c == ' ' && i<conditionStr.length()-5 && conditionStr.charAt(i+1) =='a' && conditionStr.charAt(i+2) =='n' && conditionStr.charAt(i+3) =='d'  && conditionStr.charAt(i+4) ==' '){
    				sqlParser.addSql(new StringBuffer("and"));
    				sqlParser.addSql(new StringBuffer());
    				i+=3;
    			}else if(!currentIsInStart && c == ' ' && i<conditionStr.length()-4 && conditionStr.charAt(i+1) =='o' && conditionStr.charAt(i+2) =='r'  && conditionStr.charAt(i+3) ==' '){
    				sqlParser.addSql(new StringBuffer("or"));
    				sqlParser.addSql(new StringBuffer());
    				i+=2;
    			}else if(c == 'f' && conditionStr.charAt(i+1) =='i' && conditionStr.charAt(i+2) =='e' && conditionStr.charAt(i+3) =='l' && conditionStr.charAt(i+4) =='d'){
    				sqlParser.appendSql("field");
    				isInstartStack.push(true);
    				i+=4;
    			}else{
    				sqlParser.appendSql(c);
    				if(c == ')'){
    					isInstartStack.pop();
    					//isInStart = false;
    				}else if(c == ' ' && i<conditionStr.length()-4 && conditionStr.charAt(i+1) =='i' && conditionStr.charAt(i+2) =='n' && conditionStr.charAt(i+3) ==' '){
    					isInstartStack.push(true);//in后面跟的也是(.....),为了能和嵌套的子语句区分开来，特意作此标志。
    					sqlParser.appendSql("in");
    					i+=2;
    				}
    			}
    		}
    	}
    	
    	return topSqlParser;
    }
    
    /**
     * 把字符串类型的sql组装成规范的DBCondition.
     * 例如传入"name = 'bill' and age=33",会构建成List：new DBCondition(name_jfield,"bill"),new DBCondition(age_jfield,333)
     * 支持复杂的嵌套，但运算关系符只支持 and和or
     * 
     * @author wuyujie Sep 20, 2014 9:08:07 PM
     * @param entityClass
     * @param conditionStr
     * @param binderEntityList,主绑定实体查询来的实体列表，里面的每个元素是一个主绑定对象
     * @param relBinderEntityList，关联绑定实体查询出来的实体列表，里面的每个元素也是一个List，顺序和前台传入的关联实体数据保持一致，该list里存放的才是真正的关联实体对象
     * @param tenantId,如果有嵌套查询，比如id in (select * from xx ),那么这个嵌套查询也要把tenantId的条件加上。在orm工程里取不到tenanId只能传过来
     * @return
     * @throws Exception
     */
    public static List<DBCondition> parse2DBCondition(Class<? extends DBEntity> entityClass,String conditionStr,List<? extends DBEntity> binderEntityList,Map<String,List<? extends DBEntity>> relBinderEntityMap,Long tenantId) throws Exception{
    	//conditionStr = "id in query(sys.SysDeptStaffRel,staffId,deptId=10000) or id=222";
    	ConditionParser condParser = buildConditionParser(conditionStr);
    	
    	return condParser.parse2DBConditions(entityClass,binderEntityList,relBinderEntityMap,tenantId);
    }
    public static List<DBCondition> parse2DBCondition(Class<? extends DBEntity> entityClass,String conditionStr,Long tenantId) throws Exception{
    	return parse2DBCondition(entityClass,conditionStr,null,null,tenantId);
    }
    
    public static <T extends DBEntity>List<DBCondition> buildDBConditions(T entity){
    	Map<BaseJField,Object> settedValue = entity._getSettedValue();
    	if(NullUtil.isEmpty(settedValue)){
    		return null;
    	}
    	Iterator<Entry<BaseJField,Object>> it = settedValue.entrySet().iterator();
    	List<DBCondition> conds = new ArrayList<DBCondition>();
    	while(it.hasNext()){
    		Entry<BaseJField,Object> entry = it.next();
    		conds.add(new DBCondition(entry.getKey(),entry.getValue()));
    	}
    	
    	return conds;
    }
   /* public static DBCondition[] buildDBConditionArray(Class<? extends DBEntity> entityClass,String conditionStr) throws Exception{
    	return buildDBConditionArray(entityClass,null,conditionStr);
    }
    public static DBCondition[] buildDBConditionArray(Class<? extends DBEntity> entityClass,List<? extends DBEntity> binderEntityList,List<? extends DBEntity> relBinderEntityList,String conditionStr) throws Exception{
    	List<DBCondition> conds = buildDBCondition(entityClass,binderEntityList,conditionStr);
    	return NullUtil.isEmpty(conds) ? null : conds.toArray(new DBCondition[conds.size()]);
    }*/
    
    /**
     * 根据传入的一个数据库实体的字符串名称，实例化对应的数据库实体类
     * 注意，传入的字符串名称并不需要是完整的包名，而是相对于"com.sjm.gen.dbentity."后面的路径,比如"sys.SysMenu"
     * @author wuyujie Oct 22, 2014 8:15:03 PM
     * @param entityClassName
     * @return
     * @throws ClassNotFoundException
     */
    public static <T extends DBEntity>Class<T> getEntityClass(String entityClassName) throws ClassNotFoundException{
    	return (Class<T>)Class.forName(DB_ENTITY_PACKAGE+"."+entityClassName);
    }
    
    /**
     * 构建创建某张分表的create的sql语句。出了表名，分表和主表的结构要保持完全一致,包括主键和索引
     * @author wuyujie Nov 21, 2014 12:56:11 PM
     * @param entityClass,主表的实体类
     * @param partitionName,分表名称
     * @return
     */
    public static String buildPartitionTableCreateSQL(Class<? extends DBEntity> entityClass,String partitionName){
    	List<BaseJField> jfs = getAllJFieldList(entityClass);
    	StringBuffer sb = new StringBuffer(256);
    	sb.append("CREATE TABLE ").append(partitionName).append(" (");
    	
    	for(int i=0;i<jfs.size();i++){
    		BaseJField jf = jfs.get(i);
    		if(i > 0){
    			sb.append(",\n");
    		}
    		//名称
    		sb.append("`").append(jf.getColName()).append("`");
    		//类型
    		sb.append(" ").append(jf.getColTypeName());
    		if(jf.getLength() != null && jf.getJavaType() != Date.class && jf.getJavaType() != NoTimeDate.class){
    			//如果有长度则加上长度
    			sb.append("(").append(jf.getLength()).append(")");
    		}
    		//是否可空
    		sb.append(" ").append(jf.getNullable() ? "NULL" : "NOT NULL");
    	}
    	
    	DBTableAnno tableAnno = entityClass.getAnnotation(DBTableAnno.class);
    	//主键
    	if(NullUtil.isNotEmpty(tableAnno.primaryKey())){
    		sb.append("\n,").append("PRIMARY KEY (").append(tableAnno.primaryKey()).append(")");
    	}
    	//索引
    	if(NullUtil.isNotEmpty(tableAnno.indexes())){
    		Pattern pattern = Pattern.compile("(.+?)\\(([\\w,]+)\\);?");
    		Matcher matcher = pattern.matcher(tableAnno.indexes());
    		while(matcher.find()){
    			String indexName = matcher.group(1);
    			String cols = matcher.group(2);
    			sb.append("\n,").append("INDEX `").append(indexName).append("` (").append(cols).append(")");
    		}
    		//String[] indexArr = tableAnno.indexes().split(";");
    	}
    	sb.append(")");
    	return sb.toString();
    }
    
    /**
     * 构建创建某张主表的历史表的sql语句。
     * 历史表的规则是：
     * 表名：主表名_his_yyyymm
     * 字段：主表完全顺序一致的所有字段，his_time,oper_sn,oper_type，
     * 全部字段都可以为空
     * 不需要任何主键和约束
     * @author wuyujie Nov 21, 2014 12:56:11 PM
     * @param entityClass,主表的实体类
     * @param partitionName,分表名称
     * @return
     */
    public static String buildHisTableCreateSQL(Class<? extends DBEntity> entityClass,String hisName){
    	List<BaseJField> jfs = getAllJFieldList(entityClass);
    	/*jfs.add((T)HIS_FIELD_HISTIME);
    	jfs.add((T)HIS_FIELD_HISTIME);
    	jfs.add((T)HIS_FIELD_HISTIME);*/
    	
    	StringBuffer sb = new StringBuffer(256);
    	sb.append("CREATE TABLE ").append(hisName).append(" (");
    	
    	for(int i=0;i<jfs.size();i++){
    		BaseJField jf = jfs.get(i);
    		if(i > 0){
    			sb.append(",\n");
    		}
    		//名称
    		sb.append("`").append(jf.getColName()).append("`");
    		//类型
    		sb.append(" ").append(jf.getColTypeName());
    		if(jf.getLength() != null && jf.getJavaType() != Date.class && jf.getJavaType() != NoTimeDate.class){
    			//如果有长度则加上长度
    			sb.append("(").append(jf.getLength()).append(")");
    		}
    	}
    	
    	//再加上his_time,oper_sn,oper_type几个通用字段
    	List<CustDBField> commonFields = getHisTableFields();
    	for(CustDBField field : commonFields){
    		sb.append(",\n");
    		//名称
    		sb.append("`").append(field.getColName()).append("`");
    		//类型
    		sb.append(" ").append(field.getColType());
    	}
    	
    	/*DBTableAnno tableAnno = entityClass.getAnnotation(DBTableAnno.class);
    	//主键
    	if(NullUtil.isNotEmpty(tableAnno.primaryKey())){
    		sb.append("\n,").append("PRIMARY KEY (").append(tableAnno.primaryKey()).append(")");
    	}
    	//索引
    	if(NullUtil.isNotEmpty(tableAnno.indexes())){
    		Pattern pattern = Pattern.compile("(.+?)\\(([\\w,]+)\\);?");
    		Matcher matcher = pattern.matcher(tableAnno.indexes());
    		while(matcher.find()){
    			String indexName = matcher.group(1);
    			String cols = matcher.group(2);
    			sb.append("\n,").append("INDEX `").append(indexName).append("` (").append(cols).append(")");
    		}
    		//String[] indexArr = tableAnno.indexes().split(";");
    	}*/
    	sb.append(")");
    	return sb.toString();
    }
    
    public static OrderField[] parseOrderString(String orderStr,Class<? extends DBEntity> entityClass) throws Exception{
		OrderField[] orderFields = null;
		if(NullUtil.isNotEmpty(orderStr)){
			String[] orderArr = orderStr.split(",");
			orderFields = new OrderField[orderArr.length];
			for(int i=0;i<orderArr.length;i++){
				String[] itemArr = orderArr[i].split(" ");
				orderFields[i] = new OrderField(
					DBHelper.getJField(entityClass, itemArr[0]),
					itemArr.length == 2 ? "asc".equals(itemArr[1]) : true
				);
			}
		}
		return orderFields;
    }
    
    /**
	 * 构建分页信息 
	 * @author wuyujie Dec 31, 2014 7:16:26 PM
	 * @param requestBody
	 * @return
	 */
	public static int[] buildRange(Integer start_index,Integer query_count){
		int[] range = null;
		boolean needByPage = start_index != null ;//是否需要分页
		if(needByPage){
			range = new int[]{start_index,query_count == null ? -1 : query_count};//query_count=-1表示查询从start_index到最后一条
		}
		return range;
	}
	
	/**
	 * 构建嵌套的查询条件。
	 * 比如 id in (select cust_id from base_cust_brand_rel where brand_id=xxx)
	 * @param field,条件字段，即示例的id
	 * @param entityClass,嵌套查询表，即示例的base_cust_brand_rel
	 * @param queryField,嵌套查询出来的字段，即示例的cust_id
	 * @param conds,嵌套查询条件，即示例的brand_id=xxx
	 * @return
	 * 返回的是一个DBCondition，因此和前面的条件是用and连接
	 */
	public static <T extends DBEntity>DBCondition buildNestQueryCondition(BaseJField field,Class<T> entityClass,IQueryField queryField,DBCondition...conds){
		return new DBCondition(field,buildNestQuery(entityClass,queryField,conds),DBOperator.IN);
	}
	/**
	 * 功能同buildNestQueryCondition，只是本方法返回的是DBOrCondition，因此和前面的条件是用or连接
	 * @param field
	 * @param entityClass
	 * @param queryField
	 * @param conds
	 * @return
	 */
	/*public static <T extends DBEntity>DBOrCondition buildNestOrQueryCondition(BaseJField field,Class<T> entityClass,IQueryField queryField,DBCondition...conds){
		return new DBOrCondition(field,buildNestQuery(entityClass,queryField,conds),DBOperator.IN);
	}*/
	public static <T extends DBEntity>QueryStatement buildNestQuery(Class<T> entityClass,IQueryField queryField,DBCondition...conds){
		return new QueryStatement(entityClass,conds).appendQueryField(queryField);
	}
	
	
	public static ISequenceManager getSequenceManager(){
		return (ISequenceManager)SpringUtil.getBeanByName("sequenceManager");
	}
	public static IDao getDao(){
		return (IDao)SpringUtil.getBeanByName("commonDao");
	}
	
	/**
	 * 对PreparedStatement执行变量绑定，适用于批量操作的场景。
	 * @author wuyujie Sep 5, 2014 4:48:25 PM
	 * @param dbClient
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static PreparedStatement createBatchPreparedStatement(Connection connection,BaseDBStatement statement) throws SQLException{
		PreparedStatement ps = connection.prepareStatement(statement.getSQL());
		int batchCount = ((IBatchStatement)statement).getBatchCount();
		int paramCount = statement.getBindParameters().size();
		int perCount = paramCount/batchCount;//假如批次里有两条记录，所有绑定变量数是10，那么就说明每条记录需要绑定10/2=5个变量
		for(int i=0;i<paramCount;i++){
			int paramIndex = (i % perCount)+1;//得出绑定的下标，jdbc的绑定下标是从1开始的，所有要加1
			bindParameterValue(ps,paramIndex,statement.getBindParameters().get(i));
			if(paramIndex % perCount == 0)
				ps.addBatch();
		}
		return ps;
	}
	
	
	/**
	 * 对PreparedStatement执行变量绑定，适用于非批量操作的场景。
	 * @author wuyujie Sep 5, 2014 4:48:25 PM
	 * @param dbClient
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static PreparedStatement createPreparedStatement(Connection connection,BaseDBStatement statement) throws SQLException{
		PreparedStatement ps = connection.prepareStatement(statement.getSQL());
		List<SQLBindParameter> params = statement.getBindParameters();
        
		//创建PreparedStatement对象后，要对其进行变量绑定
        if(NullUtil.isEmpty(params))
            return ps;
        int size = params.size();
        for(int i=0;i<size;i++){
            bindParameterValue(ps,i+1,params.get(i));
        }
        return ps;
    }
	
	/**
	 * 对PreparedStatement对象执行变量绑定的核心方法
	 * @author wuyujie Sep 5, 2014 4:48:59 PM
	 * @param ps
	 * @param paramIndex
	 * @param param
	 * @throws Exception
	 */
	public static void bindParameterValue(PreparedStatement ps,int paramIndex,SQLBindParameter param) throws SQLException{
		Object paramValue = param.getValue();
        Class<?> paramType = param.getJfield().getJavaType();//java字段类型
        //String colType = param.getJfield().getColTypeName();//数据库表字段类型
        if(paramType == String.class){
            ps.setString(paramIndex, paramValue == null ? null : String.valueOf(paramValue));
        }else if(paramType == Integer.class){
            if(paramValue == null){
                ps.setNull(paramIndex,java.sql.Types.INTEGER);
            }else{
                ps.setInt(paramIndex, Integer.valueOf(String.valueOf(paramValue)));
            }
        }else if(paramType == Short.class){
            if(paramValue == null){
                ps.setNull(paramIndex,java.sql.Types.INTEGER);
            }else{
                ps.setShort(paramIndex, Short.valueOf(String.valueOf(paramValue)));
            }
        }else if(paramType == java.util.Date.class){
        	//有时间精度的日期
        	if(param.getJfield().getColTypeName().equals("DATETIME")){
        		paramValue = paramValue == null ? 
    					null : 
    					paramValue instanceof java.sql.Timestamp ? 
    							paramValue : 
    							new java.sql.Timestamp(((java.util.Date)paramValue).getTime());
            	ps.setTimestamp(paramIndex, paramValue == null ? null : (Timestamp)paramValue);
        	}else{
        		paramValue = paramValue == null ? 
    					null : 
    					paramValue instanceof java.sql.Date ? 
    							paramValue : 
    							new java.sql.Date(((java.util.Date)paramValue).getTime());
            	ps.setDate(paramIndex, paramValue == null ? null : (java.sql.Date)paramValue);
        	}
        	
        	
        }/*else if(paramType == NoTimeDate.class){
        	//无时间精度的日期
        	paramValue = paramValue == null ? 
					null : new java.sql.Date(((java.util.Date)paramValue).getTime());
					paramValue instanceof java.sql.Date ? 
							paramValue : 
							new java.sql.Date(((java.util.Date)paramValue).getTime());
        	ps.setDate(paramIndex, paramValue == null ? null : (java.sql.Date)paramValue);
        	
        }*/else if(paramType == Long.class){
            if(paramValue == null){
                ps.setNull(paramIndex,java.sql.Types.INTEGER);
            }else{
                ps.setLong(paramIndex, Long.valueOf(String.valueOf(paramValue)));
            }
        }else if(paramType == Float.class){
            if(paramValue == null){
                ps.setNull(paramIndex,java.sql.Types.FLOAT);
            }else{
                ps.setFloat(paramIndex, Float.valueOf(String.valueOf(paramValue)));
            }
        }else{
            throw new SQLException("Illegal date type of parameters! index : "+(paramIndex)+", field : "+param.getJfield().getName()+" !");
        }
	};
	
	public static List<DBCondition> parse2DBConditionList(DBCondition...conds){
		if(NullUtil.isEmpty(conds))
			return null;
		
		List<DBCondition> condList = new ArrayList<DBCondition>();
		for(DBCondition cond : conds){
			condList.add(cond);
		}
		return condList;
				
	}
	
	/**
	 * 把FieldValue[]还原成某个实体对象
	 * @param entity
	 * @return
	 * @author Wilson 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @date 下午9:16:23
	 */
	public static <T extends DBEntity>T parse2DBEntity(FieldUpdateExpression[] values) throws InstantiationException, IllegalAccessException{
		Class<T> entityClass = (Class<T>)values[0].getJf().getEntityClass();
		
		T entity = entityClass.newInstance();
		
		for(FieldUpdateExpression value : values){
			entity._setFieldValue(value.getJf(), value.getValue());
		}
		
        return entity;
	}
	
	/**
	 * 判断是个实体是否修改过。主要是看entity中的_settedValue属性中有没有值
	 * @param entity
	 * @return
	 * @author Wilson 
	 * @date 下午2:53:36
	 */
	public static boolean isModified(DBEntity entity){
		return entity != null && NullUtil.isNotEmpty(entity._getSettedValue());
	}
	public static boolean isModified(DBEntity entity,BaseJField f){
		return entity != null 
				&& NullUtil.isNotEmpty(entity._getSettedValue()) 
				&& entity._getSettedValue().containsKey(f);
	}
	
	/**
	 * 解析出所有需要查询的字段。
	 * 如果没有指定字段，那么表示需要查询该表的所有字段。
	 * @param mainClass
	 * @param joinClassList
	 * @param queryFields
	 * @return
	 * @author Wilson 
	 * @date 上午10:52:31
	 */
	public static List<? extends IQueryField> parseAllQueryFields(Class<? extends DBEntity> mainClass,List<Class<? extends DBEntity>> joinClassList){
        List<IQueryField> finalQueryFieldList = new ArrayList<IQueryField>();
        finalQueryFieldList.addAll(getAllJFieldList(mainClass));
        
        if(NullUtil.isNotEmpty(joinClassList)){
        	for(Class<? extends DBEntity> clz : joinClassList){
        		finalQueryFieldList.addAll(getAllJFieldList(clz));
        	}
        	
        }
        return finalQueryFieldList;
	}
	
	/**
	 * 解析一个查询字段的完整列名，如果该字段的表有对应别名，需要把命名加上，比如t1.name
	 * @param queryField
	 * @return
	 * @author Wilson 
	 * @date 上午11:22:44
	 */
	public static String parseFieldColumnName(IQueryField queryField,Map<String,String> tableAliasMap){
		if(NullUtil.isEmpty(tableAliasMap))
			return queryField.getColName();
		
    	if(queryField instanceof BaseJField){
    		//如果是普通查询BaseJField，
    		String tableAlias = tableAliasMap.get(((BaseJField)queryField).getEntityClass().getSimpleName());
    		if(NullUtil.isEmpty(tableAlias)){
    			return queryField.getColName();
    		}else{
    			return new StringBuilder(tableAlias).append(".").append(queryField.getColName()).toString();
    		}
    	}
    	
    	if(queryField instanceof CountQueryField){
    		return ((CountQueryField)queryField).getColName(null);
    	}
    	
    	if(queryField instanceof IStatQueryField){
    		BaseJField innerJField = ((IStatQueryField)queryField).getQueryField();
    		if(innerJField != null){
    			String tableAlias = tableAliasMap.get(innerJField.getEntityClass().getSimpleName());
    			return ((IStatQueryField)queryField).getColName(tableAlias);
    		}
    	}
		
		return null;
	}
	
}
