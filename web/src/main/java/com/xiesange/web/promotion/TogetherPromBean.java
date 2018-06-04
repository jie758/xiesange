package com.xiesange.web.promotion;

import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.promotion.Promotion;

public class TogetherPromBean extends GroupbuyPromBean{
	private Orders creatorOrder = null;
	public TogetherPromBean(Promotion prom) {
		super(prom);
	}
	
	
	public void apply(Object param){
		super.apply(param);
	}
	
	@Override
	public boolean isMatched(Object param) {
		return super.isMatched(param);
	}

	@Override
	public void reduce(Object param) {
		Long reduceSum = Long.valueOf(promotion.getValue());
		creatorOrder.setSum(creatorOrder.getOrigSum()-reduceSum);
	}

	@Override
	public void discount(Object param) {
		super.discount(param);
	}

	@Override
	public void more(Object param) {
		super.more(param);
	}

	@Override
	public void fixprice(Object param) {
		super.fixprice(param);
	}
	

}
