package com.xiesange.web.promotion;

import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.web.define.ConsDefine;

public class PromotionManager {
	public static AbstractPromotionBean getPromotionBean(Promotion promption){
		short type = promption.getType();
		if(type == ConsDefine.PROMOTION_TYPE.FIRST.value()){
			return new FirstPromBean(promption);
		}
		if(type == ConsDefine.PROMOTION_TYPE.REACH.value()){
			return new ReachPromBean(promption);
		}
		if(type == ConsDefine.PROMOTION_TYPE.BARGAIN.value()){
			return new BargainPromBean(promption);
		}
		if(type == ConsDefine.PROMOTION_TYPE.GROUP.value()){
			return new GroupbuyPromBean(promption);
		}
		if(type == ConsDefine.PROMOTION_TYPE.TOGETHER.value()){
			return new TogetherPromBean(promption);
		}
		return null;
	}
}
