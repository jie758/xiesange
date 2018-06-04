package com.xiesange.orm;

import java.util.ArrayList;
import java.util.List;

import com.xiesange.core.util.NullUtil;

/**
 * sql追踪器,用于统计sql执行的各项耗时,以及打印语句以变量绑定等
 * @Description
 * @author wuyj
 * @Date 2013-11-1
 */
public class SqlTracer
{
    private String sql;
    private String bindParametersString;
    private List<Object> costStat = new ArrayList<Object>();
    
    public void start(String name){
        costStat.add(name);
        costStat.add(System.currentTimeMillis());
    }
    
    /**
     * 整个访问操作结束时调用，会以字符串形式返回整个SQL跟踪器的所有执行情况,包括sql语句,绑定变量以及各个步骤耗时
     * @author wuyj 2013-11-2
     * @param tracer
     * @param count
     */
    public String finish(long count){
        long endtime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer(sql==null?"":sql).append("\n");
        
        if(NullUtil.isNotEmpty(bindParametersString))
            sb.append(bindParametersString).append("\n");
        //prepare,100ms,access,110ms,wrap,140ms
        if(NullUtil.isNotEmpty(costStat)){
            for(int i=0;i<costStat.size()-1;i+=2){
                String name = costStat.get(i).toString();
                long finishtime = (i == (costStat.size()-2)) ?  endtime : (Long)costStat.get(i+3);
                long cost = finishtime - (Long)costStat.get(i+1);
                
                sb.append("[").append(name).append("]:").append(cost).append("ms").append(",");
            }
        }
        
        sb.append("[count]:")
          .append(count)
          .append("\n");
        return sb.toString();
    }

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }
    
    public String getBindParametersString() {
		return bindParametersString;
	}

	public void setBindParametersString(String bindParametersString) {
		this.bindParametersString = bindParametersString;
	}

	public void clear(){
        this.sql = null;
        this.bindParametersString = null;
        this.costStat.clear();
    }
    
}
