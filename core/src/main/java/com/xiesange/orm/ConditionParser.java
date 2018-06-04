package com.xiesange.orm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiesange.core.util.CommonUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.pojo.NoTimeDate;
import com.xiesange.orm.sql.BlockDBCondition;
import com.xiesange.orm.sql.BlockDBOrCondition;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;

/**
 * 条件解析器,适用于把字符串类似"x1=v1 and x2=v2"这种语句解析成标准的格式。字符串支持标准的sql语法，而且也支持复杂的嵌套的格式
 * 
 * @author wuyujie Dec 8, 2014 5:52:37 PM
 *
 */
public class ConditionParser {
	public static Pattern pattern = Pattern.compile("\\w+(and or).+");
	private List sqls = new ArrayList();
	
	/**
	 * 往当前SqlParser里添加一个新的StringBuffer子语句。
	 * @param sb
	 */
	public void addSql(StringBuffer sb){
		sqls.add(sb);
	}
	
	
	/**
	 * 往当前SqlParser的最后一个StringBuffer里添加字符。
	 * 如果当前SqlParser最后一个语句对象不是StringBuffer，则会新构建一个添加到List里。
	 * @param str
	 */
	public void appendSql(char c){
		StringBuffer sb;
		if(sqls.size() > 0 && sqls.get(sqls.size()-1) instanceof StringBuffer){
			sb = (StringBuffer)sqls.get(sqls.size()-1);
		}else{
			sb = new StringBuffer();
			sqls.add(sb);
		}
		sb.append(c);
	}
	
	/**
	 * 往当前SqlParser的最后一个StringBuffer里添加字符串。
	 * 功能同appendSql(char c)
	 * @param str
	 */
	public void appendSql(String str){
		StringBuffer sb;
		if(sqls.size() > 0 && sqls.get(sqls.size()-1) instanceof StringBuffer){
			sb = (StringBuffer)sqls.get(sqls.size()-1);
		}else{
			sb = new StringBuffer();
			sqls.add(sb);
		}
		sb.append(str);
	}
	
	/**
	 * 加入嵌套的SqlParser对象，如果sql语句里有括号"(...)"就会构建一个嵌套的SqlParser对象，这个新构建的SqlParser也会作为当前sqlParser的一个子语句.
	 * 这里会有个特殊操作，加入嵌套SqlParser的时候会判断当前sqlParser的最后一个语句是否为StringBuffer且内容是否为空，如果都是，则会把这个最后的StringBuffer语句移除。
	 * @param nestParser
	 */
	public void addNest(ConditionParser nestParser){
		if(sqls.size() > 0 && sqls.get(sqls.size()-1) instanceof StringBuffer){
			StringBuffer sb = (StringBuffer)sqls.get(sqls.size()-1);
			if(sb.toString().trim().length() == 0){
				sqls.remove(sqls.size()-1);//在添加嵌套SqlParser的时候，如果当前父parser最后一个是StringBuffer，且该buffer里没有内容，则把这个buffer移除掉
			}
		}
		sqls.add(nestParser);
	}
	
	
	public List<DBCondition> parse2DBConditions(Class<? extends DBEntity> entityClass,List<? extends DBEntity> binderEntityList,Map<String,List<? extends DBEntity>> relBinderEntityMap,Long tenantId) throws Exception{
		String andOr;
		List<DBCondition> condList = new ArrayList<DBCondition>();
		DBCondition itemCond;
		DBOperator dbOperator;
		Object fieldValue = null;
		BaseJField jfield;
		for(int i=0;i<sqls.size();i+=2){
			itemCond = null;
			
			Object item = sqls.get(i);
			andOr = i == 0 ? null : ((StringBuffer)sqls.get(i-1)).toString();//and还是or
			
			if(item instanceof StringBuffer){
				String[] arr = parseSqlItem(((StringBuffer)item).toString().trim());
				dbOperator = DBOperator.getInstance(arr[1]);
				jfield = DBHelper.getJField(entityClass, arr[0]);
				String fieldValueStr = arr[2];
				if(dbOperator == DBOperator.IN && NullUtil.isNotEmpty(fieldValueStr)){
					fieldValueStr = fieldValueStr.trim();
					if(fieldValueStr.startsWith("query")){
						//如果是query开头的，表示是查询的简易模式编写，比如staff_id in query(sys.SysDeptStaff,staffId,deptId=xxx)，需要把rel里面的内容解析成完整的查询语句
						fieldValueStr = fieldValueStr.substring(fieldValueStr.indexOf("(")+1, fieldValueStr.lastIndexOf("")-1);
						//采用截取法，把第一个逗号之前的截取出来作为实体类名称，再截取第二个逗号之前的作为查询字段，再截取剩下的作为条件
						String[] paramArr = CommonUtil.split(fieldValueStr, ",", 3);
						String entityClassName = paramArr[0];
						String selectField = paramArr[1];
						String queryCond = paramArr[2];
						
						//构建QueryStatement
						Class<? extends DBEntity> queryEntityClass = DBHelper.getEntityClass(entityClassName);
						
						List<DBCondition> subQuerycondList = DBHelper.parse2DBCondition(queryEntityClass, queryCond,binderEntityList,relBinderEntityMap,tenantId);
						boolean hasTenantIdCond = false;
						if(subQuerycondList != null){
							for(int k=0;k<subQuerycondList.size();k++){
								DBCondition condition = subQuerycondList.get(k);
								if(condition.getJField() != null && condition.getJField().getName().equalsIgnoreCase("TENANTID")){
									hasTenantIdCond = true;//如果条件中已经有tenantId，那么就不需要再加tenantId作为条件了
									if(condition.getValue().equals("null")){
										subQuerycondList.remove(k);
										k--;
									}
									break;
								}
							}
						}
						if(tenantId != null && !hasTenantIdCond){
							if(subQuerycondList == null){
								subQuerycondList = new ArrayList<DBCondition>();
							}
							subQuerycondList.add(new DBCondition(DBHelper.getTenantJField(queryEntityClass, true),tenantId));
						}
						
						QueryStatement queryST = new QueryStatement(
								queryEntityClass,
								subQuerycondList.toArray(new DBCondition[subQuerycondList.size()])
								
						).appendQueryField(DBHelper.getJField(queryEntityClass, selectField));
						fieldValue = queryST;
				        
					}else if(fieldValueStr.startsWith("binder")){
						//如果是binder开头的，表示是当前字段等于查询主绑定实体里的某个字段值，比如staff_id in binder(id)，括号里的是主绑定实体列表中的
						fieldValueStr = fieldValueStr.substring(fieldValueStr.indexOf("(")+1, fieldValueStr.lastIndexOf("")-1);
						//还要判断字段是否带了分隔符，比如staff_id in binder(targetId[,]),那么表示targetId这个字段值是由多个id组成的，且以,分隔的
						String splitChar = null;
						int splitIndex = fieldValueStr.indexOf("[");
						if(splitIndex > -1){
							splitChar = fieldValueStr.substring(splitIndex+1, fieldValueStr.indexOf("]"));
							fieldValueStr = fieldValueStr.substring(0, splitIndex);
						}
						Set<Object> fieldValueSet = null;
						if(NullUtil.isNotEmpty(binderEntityList)){
							fieldValueSet = new HashSet<Object>();
							for(DBEntity binderEntity : binderEntityList){
								Object fieldVal = DBHelper.getEntityValue(binderEntity, fieldValueStr);
								if(splitChar != null){
									//有分隔符的话那存储的肯定是字符串，要把值分隔截取出来
									String[] items = ((String)fieldVal).split(splitChar);
									for(String itemStr : items){
										fieldValueSet.add(itemStr);
									}
								}else{
									fieldValueSet.add(fieldVal);
								}
								
							}
						}
						fieldValue = fieldValueSet;
					}else if(fieldValueStr.startsWith("rel")){
						fieldValueStr = fieldValueStr.substring(fieldValueStr.indexOf("(")+1, fieldValueStr.lastIndexOf("")-1);
						String[] paramArr = fieldValueStr.split(",");
						String[] relFieldNames = paramArr[1].split(" ");
						List<? extends DBEntity> relList = relBinderEntityMap.get(paramArr[0]);
						
						Set<Object> fieldValueSet = null;
						if(NullUtil.isNotEmpty(relList)){
							fieldValueSet = new HashSet<Object>();
							for(DBEntity relEntity : relList){
								for(String relFieldName : relFieldNames){
									fieldValueSet.add(DBHelper.getEntityValue(relEntity, relFieldName));
								}
								
							}
						}
						fieldValue = fieldValueSet;
					}else{
						fieldValueStr = fieldValueStr.substring(fieldValueStr.indexOf("(")+1, fieldValueStr.lastIndexOf("")-1);
						fieldValue = fieldValueStr.split(",");
					}
				}else{
					if(NullUtil.isEmpty(arr[2])){
						fieldValue = null;
					}else if(arr[2].startsWith("field(")){
						//如果是field(xxx)则表示是表中的字段，那要构建一个BaseJFeild字段。比如where sum > receivingSum，就是找出sum大于receivingSum的记录
						fieldValue = DBHelper.getJField(entityClass, arr[2].substring("field(".length(), arr[2].length()-1));
					}else{
						fieldValue = arr[2];
					}
				}
				if(fieldValue == null && dbOperator != DBOperator.IS){
					//如果字段值为null，但是运算符并不是is null，那只能说没有查询到数据，当前查询条件不成立，要构建一个阻塞条件对象。但并不能完全的阻断并返回，因为可能还有or条件呢。
					if(andOr == null || andOr.equalsIgnoreCase("and")){
						itemCond = new BlockDBCondition();
					}else if(andOr.equalsIgnoreCase("or")){
						itemCond = new BlockDBOrCondition();
					}
				}else{
					if(dbOperator != DBOperator.IS && Date.class.isAssignableFrom(jfield.getJavaType())){
						fieldValue = parseDateValue(jfield,fieldValue);
					}
					
					if(andOr == null || andOr.equalsIgnoreCase("and")){
						itemCond = new DBCondition(jfield,fieldValue,dbOperator);
					}else if(andOr.equalsIgnoreCase("or")){
						itemCond = new DBOrCondition(jfield,fieldValue,dbOperator);
					}
				}
			}else{
				List<DBCondition> subCondList = ((ConditionParser)item).parse2DBConditions(entityClass,binderEntityList,relBinderEntityMap,tenantId);
				if(andOr == null || andOr.equalsIgnoreCase("and")){
					itemCond = subCondList.size() == 1 ? subCondList.get(0) : new DBCondition(subCondList.toArray(new DBCondition[subCondList.size()]));
				}else if(andOr.equalsIgnoreCase("or")){
					itemCond = subCondList.size() == 1 ? subCondList.get(0) : new DBOrCondition(subCondList.toArray(new DBCondition[subCondList.size()]));
				}
			}
			condList.add(itemCond);
		}
		return condList;
	}
	
	/**
	 * 解析单个sql表达式，即只有一个运算符的表达式。
	 * 比如 name=bill
	 * @param sqlString
	 * @return,返回String[],三个元素，第一个左边值，第二个是运算符,第三个是右边值
	 * 例如:
	 * parseSqlItem("name=bill"),返回["name","=","bill"];
	 *parseSqlItem("name!=bill jacky"),返回["name","!=","bill jacky"];
	 * 
	 */
	private static String[] parseSqlItem(String sqlString){
		Pattern pattern;
		if(sqlString.indexOf(" in ") > -1 || sqlString.indexOf(" in(") > -1){
			//说明是in运算符
			pattern = Pattern.compile("(\\w+)\\s*(in)\\s*(.+)");
		}else{
			pattern = Pattern.compile("(\\w+)\\s*(is|!=|=|>=|<=|>|<|like)\\s*(.*)");
		}
		
		Matcher matcher = pattern.matcher(sqlString);
		
		while(matcher.find()){
			String left = matcher.group(1);
			String express = matcher.group(2);
			String right = matcher.group(3);
			
			return new String[]{left,express,right};
		}
		
		return null;
	}
	
	//解析日期类型字段的值。因为有前台传过来的都是字符串，所以如果是日期类型的，要转成Date。注意转换的format要区分带时间精度和不带时间精度
	private static Date parseDateValue(BaseJField jfield,Object value) throws Exception{
		if(value == null)
			return null;
		if(value instanceof Date)
			return ((Date)value);
		if(value instanceof String){
			return jfield.getJavaType() == NoTimeDate.class ? 
					//无时间精度
					DateUtil.str2Date((String)value, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD):
					//带时间精度
					DateUtil.str2Date((String)value, DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS);
				
		}
		return null;
	}
	

	public List getSqls() {
		return sqls;
	}
}
