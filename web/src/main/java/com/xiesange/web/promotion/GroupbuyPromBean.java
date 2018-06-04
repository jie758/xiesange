package com.xiesange.web.promotion;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.web.component.GroupbuyCmp;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.define.ConsDefine;

public class GroupbuyPromBean extends AbstractPromotionBean{
	public GroupbuyPromBean(Promotion prom) {
		super(prom);
	}
	
	
	public void apply(Object param){
		List<Orders> orders = (List<Orders>)param;
		super.apply(orders);
		//发起者优惠
		String createReduce = promotion.getCreatorValue();
		if(NullUtil.isEmpty(createReduce)){
			return;
		}
		long reduceSum = Long.parseLong(promotion.getCreatorValue());
		Orders creatorOrder = null;
		for(Orders order : orders){
			if(order.getGroupbuyId() == -1){
				creatorOrder = order;
				break;
			}
		}
		if(creatorOrder == null || !OrderCmp.isEdit(creatorOrder.getStatus())){
			//非未支付状态，无需重新计算，支付了多少就算多少
			return;
		}
		long creatorPaySum = creatorOrder.getSum()-reduceSum;
		creatorOrder.setSum(creatorPaySum<0?0:creatorPaySum);
	}
	
	@Override
	public boolean isMatched(Object param) {
		List<Orders> orders = (List<Orders>)param;
		OrdersItem orderItem = GroupbuyCmp.getOrdersItem(orders.get(0));
		Product product = (Product)orderItem.getAttr("product");
		int custAmount = orders.size();
		int totalAmount = 0;
		long totalSum = 0L;
		for(Orders order : orders){
			OrdersItem item = GroupbuyCmp.getOrdersItem(order);
			totalAmount += item.getAmount();
			totalSum += (item.getAmount()*product.getPrice());
		}
		if(promotion.getConditionSum() != null && totalSum < promotion.getConditionSum()){
			return false;//总额没达标
		}
		if(promotion.getConditionAmount() != null && totalAmount < promotion.getConditionAmount()){
			return false;//总斤数没达标
		}
		if(promotion.getConditionCusts() != null && custAmount < promotion.getConditionCusts()){
			return false;//总客户数没达标
		}
		return true;
	}

	@Override
	public void reduce(Object param) {
	}

	@Override
	public void discount(Object param) {
	}

	@Override
	public void more(Object param) {
	}

	@Override
	public void fixprice(Object param) {
		List<Orders> orders = (List<Orders>)param;
		long fixPrice = Long.parseLong(promotion.getValue());
		//Product prodEntity = (Product)GroupbuyCmp.getOrdersItem(orders.get(0)).getAttr("product");
		for(Orders order : orders){
			if(!OrderCmp.isEdit(order.getStatus())){
				continue;
			}
			OrdersItem item = GroupbuyCmp.getOrdersItem(order);
			
			item.setPrice(fixPrice);
			item.setSum(fixPrice*item.getAmount());
			
			order.setOrigSum(item.getSum());
			order.setSum(item.getSum());
		}
		/*prodEntity.setOrigPrice(prodEntity.getPrice());
		prodEntity.setPrice(fixPrice);*/
	}

	@Override
	public String buildText() {
		int custs = promotion.getConditionCusts();
		int amount = promotion.getConditionAmount();
		String value = promotion.getValue();
		if(promotion.getActionType() == ConsDefine.PROMOTION_ACTION.FIXPRICE.value()){
			value = ETUtil.parseFen2YuanStr(Long.valueOf(promotion.getValue()),false).toString();
		}
		String str = custs+"人团,"+amount+"斤起售,"+value+"元/斤;";
		String creatorValue = promotion.getCreatorValue();
		if(NullUtil.isNotEmpty(creatorValue)){
			creatorValue = ETUtil.parseFen2YuanStr(Long.valueOf(creatorValue),false).toString();
			str += "发起者立减"+creatorValue+"元";
		}
		return str;
	}

}
