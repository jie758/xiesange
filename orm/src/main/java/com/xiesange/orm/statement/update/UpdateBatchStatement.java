package com.xiesange.orm.statement.update;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xiesange.core.util.NullUtil;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.BaseDBStatement;
import com.xiesange.orm.statement.IBatchStatement;
import com.xiesange.orm.statement.field.BaseJField;
/**
 * 批量更新操作,注意，更新的字段和where中的字段名称必须要都一致.
 * 例如有一批更新操作：
 * update sys_menu set name = 'menu1' where id = 1001 and is_leaf=1;
 * update sys_menu set name = 'menu2' where id = 1002 and is_leaf=2;
 * update sys_menu set name = 'menu3' where id = 1003 and is_leaf=3;
 * 先设置更新的模式：
 * udpate sys_menu set name = ? where id = ? and is_leaf = ?
 * 然后再根据每个更新操作对这个statement做变量绑定赋值。
 * 更新的模式由传入的第一个对象来保证，如果后续的对象和第一个对象不一致则会发生不可预料的错误，所以调用方需要保证。
 * 例如传入的更新的对象为：
 * update sys_menu set name = 'menu1' where id = 1001 and is_leaf=1;
 * update sys_menu set name = 'menu2',memo='by batch' where id = 1002 and is_leaf=2;
 * 这个时候更新的模式还是按照第一个对象决定，即udpate sys_menu set name = ? where id = ? and is_leaf = ?
 * 而第二个对象多了一个memo字段的更新，便会出错
 * @Description
 * @author wuyj
 * @Date 2013-10-27
 */
public class UpdateBatchStatement extends BaseDBStatement implements IBatchStatement
{
    private UpdateStatement[] batchUpdateItems;
    
    public UpdateBatchStatement(UpdateStatement... items){
    	super(items[0].getValueEntity().getClass());
    	this.batchUpdateItems = items;
    }
    
    @Override
    public void parse() throws Exception{
    	//先把第一个更新对象作为基准来构建SQL语句，同时把第一个对象的变量先绑定好
    	this.appendSQL("update ")
    		.appendSQL(DBHelper.getDBTableName(super.getEntityClass()))
    		.appendSQL(" set ");
    	
    	//set字段以第一个批量元素里的实体设置值为准
    	UpdateStatement firstUpdateItem = batchUpdateItems[0];
    	Map<BaseJField,Object> settedValues = firstUpdateItem.getValueEntity()._getSettedValue();
    	Iterator<Entry<BaseJField,Object>> it = settedValues.entrySet().iterator();
    	
    	Entry<BaseJField,Object> entry;
    	boolean isFirst = true;
    	List<BaseJField> setJFields = new ArrayList<BaseJField>();
    	while(it.hasNext()){
    		entry = it.next();
    		if(!isFirst){
    			this.appendSQL(" , ");
    		}else{
    			isFirst = false;
    		}
    		this.appendSQL(entry.getKey().getColName()).appendBindSQL(" = ?",entry.getKey(),entry.getValue());
    		setJFields.add(entry.getKey());//需要把set的字段存起来,后续其他批量元素都要按照这个第一个批量元素的字段顺序来设置
    	}
    	//构建where条件
        if(firstUpdateItem.getId() == null && NullUtil.isEmpty(firstUpdateItem.getConditions())){
        	return;//没传入条件，表示查询全部
        }else if(firstUpdateItem.getId() != null){
        	DBHelper.appendWhereSQL(this, firstUpdateItem.getId());
        }else{
        	DBHelper.appendWhereSQL(this, firstUpdateItem.getConditions());
        }
    	
        //到此为止SQL都已经构建完毕了,同时把第一个批量更新元素也绑定更好了,接下来就要根据顺序把剩余的批量元素也绑定完成
    	//注意实体的循环是从下标1开始的，因为第一个已经在前面进行绑定了
    	int batchLength = batchUpdateItems.length;
    	BaseJField jf_id = DBHelper.getIdJField(this.getEntityClass());
    	UpdateStatement batchItemInfo;
    	for(int i=1;i<batchLength;i++){
    		batchItemInfo = batchUpdateItems[i];
    		
    		//先绑定set值
    		for(BaseJField jfield : setJFields){
    			this.addBindParameter(jfield,DBHelper.getEntityValue(batchItemInfo.getValueEntity(), jfield));
    		}
    		
    		//再绑定where值
    		if(batchItemInfo.getId() != null){
    			this.addBindParameter(jf_id,batchItemInfo.getId());
    		}else if(batchItemInfo.getConditions() != null){
    			//如果是condition对象作为where，因为可能会有的嵌套条件，处理起来比较复杂，因此提供了一个工具方法
    			DBHelper.appendWhereBindParameters(this, batchItemInfo.getConditions());
    		}
        }
    	
    	
    	
    }
    
    @Override
    public Object access(PreparedStatement ps) throws Exception
    {
        int[] result = ps.executeBatch();
        int total = 0;
        for(int i : result){
            total+=i;
        }
        return total;
    }

	@Override
	public int getBatchCount() {
		return batchUpdateItems == null ? 0 :batchUpdateItems.length;
	}
	@Override
	public Object wrap(Object accessResult) throws Exception {
		return accessResult;
	}

	@Override
	public long count(Object wrapResult) {
		return ((Integer)wrapResult).longValue();
	}
}
