package com.xiesange.web.promotion;

import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.web.define.ConsDefine;

public abstract class AbstractPromotionBean {
	protected Promotion promotion;
	public AbstractPromotionBean(Promotion prom){
		this.promotion = prom;
	}
	public Promotion getPromotion() {
		return promotion;
	}
	
	public void apply(Object param){
		short actionType = promotion.getActionType();
		if(actionType == ConsDefine.PROMOTION_ACTION.REDUCE.value()){
			reduce(param);
		}else if(actionType == ConsDefine.PROMOTION_ACTION.DISCOUNT.value()){
			discount(param);
		}else if(actionType == ConsDefine.PROMOTION_ACTION.MORE.value()){
			more(param);
		}else if(actionType == ConsDefine.PROMOTION_ACTION.FIXPRICE.value()){
			fixprice(param);
		}
	}
	
	/*public boolean apply(Product product){
		if(!isMatched(orders)){
			return false;
		}
		short actionType = promotion.getActionType();
		if(actionType == ConsDefine.PROMOTION_ACTION.REDUCE.value()){
			reduce(orders);
		}
		return true;
	}*/
	
	public abstract boolean isMatched(Object param);
	public abstract void reduce(Object param);
	public abstract void discount(Object param);
	public abstract void more(Object param);
	public abstract void fixprice(Object param);
	public abstract String buildText();
}
