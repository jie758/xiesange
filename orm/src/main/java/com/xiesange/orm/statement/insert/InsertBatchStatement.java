package com.xiesange.orm.statement.insert;

import java.sql.PreparedStatement;
import java.util.List;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.statement.IBatchStatement;
import com.xiesange.orm.statement.field.BaseJField;

public class InsertBatchStatement extends InsertStatement implements IBatchStatement
{
	private List<? extends DBEntity> entityList;
    
    public <T extends DBEntity>InsertBatchStatement(List<T> entityList){
        super(entityList.get(0));
        this.entityList = entityList;
    }
    
    public <T extends DBEntity>InsertBatchStatement(List<T> entityList,String tableName){
        super(entityList.get(0),tableName);
        this.entityList = entityList;
    }
    
    /**
     * 这里的parse要对父对象里里的进行重载。因为父对象InsertStatement只插入一个对象，因此在绑定变量上只要把这一个对象的所有字段解析出来绑定即可。
     * 但是InsertStatement是插入多个对象，因此需要把每个对象的字段都绑定好。
     */
    @Override
    public void parse() throws Exception{
    	super.parse();//单个插入和批量插入的SQL语句是一样，调用父对象的parse方法，会构建好SQL和第一个对象的变量绑定
    	//第一个之后的对象需要这里再做绑定
    	List<BaseJField> jfs = DBHelper.getAllJFieldList(entityClass);
    	int length = jfs.size();
    	int entityLength = entityList.size();
    	//注意实体的循环是从下标1开始的，因为第一个已经通过super.parse进行绑定了
    	for(int i=1;i<entityLength;i++){
            for(int k=0;k<length;k++){
                this.addBindParameter(jfs.get(k), DBHelper.getEntityValue(entityList.get(i),jfs.get(k)));
            }
        }
    }
    
    @Override
    public Object access(PreparedStatement ps) throws Exception
    {
        return ps.executeBatch().length;
    }

	@Override
	public int getBatchCount() {
		return entityList == null ? 0 : entityList.size();
	}
}
