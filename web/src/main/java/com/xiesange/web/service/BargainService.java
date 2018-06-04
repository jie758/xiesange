package com.xiesange.web.service;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.baseweb.wechat.WechatCmp;
import com.xiesange.baseweb.wechat.pojo.WXUserInfo;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.util.RandomUtil;
import com.xiesange.gen.dbentity.activity.ActivityJoin;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.ActivityCmp;
import com.xiesange.web.component.BargainCmp;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.component.ProdCmp;
import com.xiesange.web.define.ConsDefine.ACTIVITY_TYPE;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OrderStatusDefine;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name = "bargain", version = "")
public class BargainService extends AbstractService{
	/**
	 * 查询订单详情。适用于单购类型
	 * @param context
	 * 			order_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryOrderDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		long loginUserId = context.getAccessUserId();
		Orders orderEntity = OrderCmp.checkExist(orderId,true);
		/*//只能当前用户操作
		CCP.checkOperateOwner(context, orderEntity.getUserId());*/
		boolean isCreator = orderEntity.getUserId() == loginUserId;//是否订单创建者
		boolean isJoin = false;
		List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		List<Product> prodList = OrderCmp.appendProducts(items, false);
		//Product prodEntity = prodList.get(0);
		
		OrderCmp.refreshSum(orderEntity, items);
		
		long paySum = BargainCmp.apply(orderId, orderEntity.getOrigSum());
		orderEntity.setSum(paySum);
		orderEntity.setExpressSum(OrderCmp.calcExpressfee(paySum));
		
		OrderCmp.transfer(orderEntity);
		ETUtil.clearDBEntityExtraAttr(orderEntity);
		
		for(Product prod : prodList){
			prod.setSummary(null);
			prod.setOrderCount(null);
			prod.setPname(null);
			prod.setStatus(null);
			ProdCmp.transfer(prod, false);
			ETUtil.clearDBEntityExtraAttr(prod);
		}
		
		for(OrdersItem item : items){
			OrderCmp.transferItem(item,false);
			ETUtil.clearDBEntityExtraAttr(item);
		}
		
		List<ActivityJoin> joinList = ActivityCmp.queryJoinList(orderId, ACTIVITY_TYPE.ORDER_BARGAIN);
		
		if(NullUtil.isNotEmpty(joinList)){
			for(ActivityJoin join : joinList){
				BargainCmp.transfer(join);
				join.setName(join.getName());
				ETUtil.clearDBEntityExtraAttr(join, ActivityJoin.JField.createTime);
				if(join.getUserId() == loginUserId){
					isJoin = true;
				}
			}
		}
		boolean canBargin = orderEntity.getStatus() == OrderStatusDefine.CUST.EDIT.state();
		
		
		boolean isSubscribed = false;//是否关注公众号
		String openid = context.getAccessToken().getUserInfo().getWechat();
		WXUserInfo wxUser = null;
		if(NullUtil.isNotEmpty(openid)){
			wxUser = WechatCmp.getFans(openid);
			if(wxUser != null && wxUser.getSubscribe() != null && wxUser.getSubscribe() == 1){
				isSubscribed = true;
			}
		}
		
		return new ResponseBody("order",orderEntity)
						.add("bargainList", joinList)
						.add("isCreator", isCreator?1:0)
						.add("canBargain", canBargin?1:0)
						.add("isJoin", isJoin?1:0)
						.add("isSubscribed", isSubscribed?1:0);
	} 
	
	/**
	 * 帮某个用户订单进行砍价
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody bargain(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Order.order_id
		);
		long userId = context.getAccessUserId();
		long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		Orders orderEntity = OrderCmp.checkExist(orderId,true);
		
		if(orderEntity.getStatus() != OrderStatusDefine.CUST.EDIT.state()){
			throw ETUtil.buildInvalidOperException();//非草稿状态不支持砍价
		}
		
		if(ActivityCmp.isJoin(userId, orderId, ACTIVITY_TYPE.ORDER_BARGAIN)){
			throw ETUtil.buildException(ErrorDefine.ORDER_BARGAIN_REPEAT);
		}
		
		List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		OrderCmp.appendProducts(items, false);
		
		OrderCmp.refreshSum(orderEntity, items);
		//可以砍价的金额，需要减去运费，运费不参与砍价 ，除非超过包邮的金额
		long canReduceSum = orderEntity.getSum() - orderEntity.getExpressSum();
		long fsum = Math.round((canReduceSum*5/100)/20);//以总额的25%作为砍价基础，分摊到20人，每人平均多少元
		long reducedSum = RandomUtil.getRangeLong(100, fsum);//取1块~1块间的随机数，小数点保留到分
		if(reducedSum > canReduceSum){
			reducedSum = canReduceSum;
		}
		
		/*orderEntity.setSum(canReduceSum-reduceSumL);
		dao().updateById(orderEntity, orderEntity.getId());*/
		
		boolean isSubscribed = false;//是否关注公众号
		String openid = context.getAccessToken().getUserInfo().getWechat();
		WXUserInfo wxUser = null;
		if(NullUtil.isNotEmpty(openid)){
			wxUser = WechatCmp.getFans(openid);
			if(wxUser != null && wxUser.getSubscribe() != null && wxUser.getSubscribe() == 1){
				isSubscribed = true;
			}
		}
		
		ActivityJoin join = new ActivityJoin();
		join.setActivityId(orderId);
		join.setType(ACTIVITY_TYPE.ORDER_BARGAIN.value());
		join.setUserId(context.getAccessUserId());
		join.setName(wxUser!=null?wxUser.getNickname():null);
		join.setExt(String.valueOf(reducedSum));
		dao().insert(join);
		
		return new ResponseBody("bargainSum",ETUtil.parseFen2YuanStr(reducedSum,false))
						.add("isSubscribed", isSubscribed?1:0);
	}
}
