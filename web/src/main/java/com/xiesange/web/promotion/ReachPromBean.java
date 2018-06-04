package com.xiesange.web.promotion;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.promotion.Promotion;

public class ReachPromBean extends AbstractPromotionBean{
	private Orders creatorOrder = null;
	public ReachPromBean(Promotion prom) {
		super(prom);
	}
	
	@Override
	public boolean isMatched(Object param) {
		return true;
	}

	@Override
	public void reduce(Object param) {
		Long reduceSum = Long.valueOf(promotion.getValue());
		creatorOrder.setSum(creatorOrder.getOrigSum()-reduceSum);
	}

	@Override
	public void discount(Object param) {
	}

	@Override
	public void more(Object param) {
	}

	@Override
	public void fixprice(Object param) {
	}

	@Override
	public String buildText() {
		Integer amount = promotion.getConditionAmount();
		Long sum = promotion.getConditionSum();
		String value = promotion.getValue();
		if(amount != null){
			return "满"+amount+"斤,送"+value+"斤";
		}
		if(sum != null){
			String sumStr = ETUtil.parseFen2YuanStr(sum, false);
			String val = ETUtil.parseFen2YuanStr(Long.valueOf(value), false);
			return "满"+sumStr+"元,送"+val+"元";
		}
		return "满就送";
	}

	

}
