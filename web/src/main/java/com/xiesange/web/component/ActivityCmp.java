package com.xiesange.web.component;

import java.util.List;

import com.xiesange.gen.dbentity.activity.ActivityJoin;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.define.ConsDefine.ACTIVITY_TYPE;


public class ActivityCmp {
	/**
	 * 判断某个用户是否已经加入某个活动
	 * @param userId
	 * @param activityId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static boolean isJoin(long userId,long activityId,ACTIVITY_TYPE type) throws Exception{
		ActivityJoin joinEntity = DBHelper.getDao().querySingle(ActivityJoin.class, 
				new DBCondition(ActivityJoin.JField.activityId,activityId),
				new DBCondition(ActivityJoin.JField.userId,userId),
				new DBCondition(ActivityJoin.JField.type,type.value()));
		return joinEntity == null ? false : true;
	}
	
	/**
	 * 查询出某个活动下的所有参与人,按先后顺序排列
	 * @param activityId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static List<ActivityJoin> queryJoinList(long activityId,ACTIVITY_TYPE type) throws Exception{
		List<ActivityJoin> joinList = DBHelper.getDao().query(
			new QueryStatement(ActivityJoin.class, 
				new DBCondition(ActivityJoin.JField.activityId,activityId),
				new DBCondition(ActivityJoin.JField.type,type.value()))
					.appendOrderField(ActivityJoin.JField.createTime)
		);
		
		return joinList;
	}
}
