package com.xiesange.web.promotion;

import java.util.List;

import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.promotion.Promotion;

public class BargainPromBean extends AbstractPromotionBean{
	private Orders creatorOrder = null;
	public BargainPromBean(Promotion prom) {
		super(prom);
	}
	
	@Override
	public boolean isMatched(Object param) {
		return true;
	}

	@Override
	public void reduce(Object param) {
		List<Orders> orders = (List<Orders>)param;
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
		return "下单后邀请好友来砍价，砍到0元免费吃";
	}

	

}
