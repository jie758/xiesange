package com.xiesange.web.component;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.activity.ActivityJoin;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.web.define.ConsDefine.ACTIVITY_TYPE;

public class BargainCmp {
	public static void transfer(ActivityJoin join){
		String ext = join.getExt();
		if(NullUtil.isEmpty(ext))
			return;
		long bargainSum = Long.valueOf(ext);
		join.addAttribute("sum",ETUtil.parseFen2YuanStr(bargainSum, false));
		join.setExt(null);
	}
	
	public static long apply(long orderId,long sum) throws Exception{
		//判断是否有砍价
		//LogUtil.getLogger(BargainCmp.class).debug("--------orig_sum : "+sum);
		List<ActivityJoin> joinlist = ActivityCmp.queryJoinList(orderId, ACTIVITY_TYPE.ORDER_BARGAIN);
		if(NullUtil.isNotEmpty(joinlist)){
			for(ActivityJoin join : joinlist){
				//LogUtil.getLogger(BargainCmp.class).debug("--------deduct sum : "+join.getExt());
				sum -= Long.parseLong(join.getExt());
			}
		}
		//LogUtil.getLogger(BargainCmp.class).debug("--------after_sum : "+sum);
		return sum < 0 ? 0 : sum;
	}
}
