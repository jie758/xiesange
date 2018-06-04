package com.xiesange.web.promotion;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.web.CustAccessToken;
import com.xiesange.web.component.PromotionCmp;

public class FirstPromBean extends AbstractPromotionBean{
	public FirstPromBean(Promotion prom) {
		super(prom);
	}
	
	@Override
	public boolean isMatched(Object param) {
		Orders order = (Orders)param;
		User orderUser = (User)order.getAttr("user");
		if(orderUser == null){
			orderUser = ((CustAccessToken)ETUtil.getRequestContext().getAccessToken()).getUserInfo();
		}
		Long orderCount = orderUser.getOrderCount();
		//LogUtil.getLogger(this.getClass()).debug("-----------loginUser:"+orderUser.getId()+",orderCount="+orderCount);
		
		return orderCount == null || orderCount == 0;
	}

	@Override
	public void reduce(Object param) {
		Long reduceSum = Long.valueOf(promotion.getValue());
		Orders orders = (Orders)param;
		long paySum = orders.getSum()-reduceSum;
		orders.setSum(paySum<0?0:paySum);
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
		//String val = ETUtil.parseFen2YuanStr(Long.valueOf(promotion.getValue()), false);
		return "首次下单,"+PromotionCmp.buildActionText(promotion);
	}

	

}
