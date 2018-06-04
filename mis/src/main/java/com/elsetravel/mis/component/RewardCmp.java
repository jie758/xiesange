package com.elsetravel.mis.component;

import java.util.List;

import com.elsetravel.baseweb.cache.CacheManager;
import com.elsetravel.gen.dbentity.base.BaseRewardRule;

public class RewardCmp {
	public static final String ACTION_LOGIN = "login";
	public static final String ACTION_CONSUME_ORDER = "consume_order";
	public static final String ACTION_CONSUME_CATALOG = "consume_catalog";
	public static final String ACTION_CONSUME_TICKET = "consume_ticket";
	public static final String ACTION_EXCHANGE = "exchange";
	public static final String ACTION_PUBLISH_TICKET = "publish_ticket";
	public static final String ACTION_COMPLETE_ORDER = "complete_order";
	
	
	
	public static final short REWARD_TYPE_MEDAL = 1;
	public static final short REWARD_TYPE_POINT = 2;
	
	public static final short PERIOD_TYPE_ONCE = 1;
	public static final short PERIOD_TYPE_CYCLE = 2;
	
	
	/**
	 * 获取所有奖励规则定义列表
	 * @return
	 * @author Wilson Wu
	 * @date 2015年9月15日
	 */
	public static List<BaseRewardRule> getAllRewardRuleList(){
		List<BaseRewardRule> ruleList = CacheManager.getCacheHouse(BaseRewardRule.class).getAll();
		return ruleList;
	}
	
	
	/**
	 * 判断当前赠送类型是否是勋章
	 * @param rule
	 * @return
	 * @author Wilson 
	 * @date 下午3:55:21
	 */
	public static boolean isRewardMedal(short rewardType){
		return rewardType == REWARD_TYPE_MEDAL;
	};
	/**
	 * 判断当前赠送类型是否是勋章
	 * @param rule
	 * @return
	 * @author Wilson 
	 * @date 下午3:55:21
	 */
	public static boolean isRewardPoint(short rewardType){
		return rewardType == REWARD_TYPE_POINT;
	};
	
	/**
	 * 判断当前规则的触发周期是否是一次性的
	 * @param rule
	 * @return
	 * @author Wilson 
	 * @date 下午3:55:21
	 */
	public static boolean isTriggerOnce(short periodType){
		return periodType == PERIOD_TYPE_ONCE;
	};
	/**
	 * 判断当前规则的触发周期是否是周期性的
	 * @param rule
	 * @return
	 * @author Wilson 
	 * @date 下午3:55:21
	 */
	public static boolean isTriggerCycle(short periodType){
		return periodType == PERIOD_TYPE_CYCLE;
	};
}
