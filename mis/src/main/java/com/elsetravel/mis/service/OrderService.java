package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.notify.NotifyCmp;
import com.elsetravel.baseweb.notify.NotifyDefine;
import com.elsetravel.baseweb.notify.NotifyTargetHolder;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.AlipayUtil;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.EncryptUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.RandomUtil;
import com.elsetravel.gen.dbentity.orders.Orders;
import com.elsetravel.gen.dbentity.orders.OrdersComment;
import com.elsetravel.gen.dbentity.person.Person;
import com.elsetravel.gen.dbentity.ticket.TicketMain;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.component.OrderCmp;
import com.elsetravel.mis.component.PersonCmp;
import com.elsetravel.mis.component.TicketCmp;
import com.elsetravel.mis.component.UserCmp;
import com.elsetravel.mis.define.ConstantDefine;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.OrderStatusDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="order",version="")
public class OrderService extends AbstractService{
	/**
	 * 查询订单列表，需要分页查询,从近到远排列。
	 * @param context
	 * 			code,根据订单编号模糊查询
	 * 			ticket_id,根据订单对应的旅票id过滤
	 * 			visitor_id,根据订单游客id过滤
	 * 			guider_id,根据订单导游id过滤
	 * 			status,根据订单状态过滤
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:34:49
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		String code = reqbody.getString(ParamDefine.Order.code);
		Long ticketId = reqbody.getLong(ParamDefine.Ticket.ticket_id);
		Long visitorId = reqbody.getLong(ParamDefine.Order.visitor_id);
		Long guiderId = reqbody.getLong(ParamDefine.Person.guider_id);
		Short status = reqbody.getShort(ParamDefine.Common.status);
		
		List<DBCondition> conds = new ArrayList<DBCondition>();
		if(NullUtil.isNotEmpty(code)){
			conds.add(new DBCondition(Orders.JField.code,"%"+code+"%",DBOperator.LIKE));
		}
		if(ticketId != null){
			conds.add(new DBCondition(Orders.JField.ticketId,ticketId));
		}
		if(visitorId != null){
			conds.add(new DBCondition(Orders.JField.visitorUserId,visitorId));
		}
		if(guiderId != null){
			conds.add(new DBCondition(Orders.JField.guiderUserId,guiderId));
		}
		if(status != null){
			conds.add(new DBCondition(Orders.JField.status,status));
		}
		
		DBCondition[] condArr = conds.toArray(new DBCondition[conds.size()]);
		
		List<Orders> orderList = dao().query(
			new QueryStatement(Orders.class,condArr)
				.appendRange(ETUtil.buildPageInfo(context.getRequestHeader()))
				.appendOrderFieldDesc(Orders.JField.createTime)
		);
		
		if(NullUtil.isEmpty(orderList)){
			return null;
		}
		
		
		long count = dao().queryCount(Orders.class, condArr);
		Set<Long> userIds = new HashSet<Long>();
		Set<Long> orderIds = new HashSet<Long>();
		for(Orders order : orderList){
			userIds.add(order.getVisitorUserId());
			userIds.add(order.getGuiderUserId());
			orderIds.add(order.getId());
			ETUtil.clearDBEntityExtraAttr(order);
			OrderCmp.transfer(order);
			
			OrderStatusDefine statueEnum = OrderCmp.getOrderState(order.getStatus());
			order.addAttribute("needApprove", statueEnum.name().endsWith("_APPLY")?1:0);//以_APPLY结尾的就是待审批状态
		}
		
		List<User> userList = dao().query(
			new QueryStatement(User.class,new DBCondition(User.JField.id,userIds,DBOperator.IN))
					.appendQueryField(User.JField.id,User.JField.nickname)
		);
		ETUtil.clearDBEntityExtraAttr(userList);
		
		List<OrdersComment> commentList = dao().query(OrdersComment.class,new DBCondition(OrdersComment.JField.orderId,orderIds,DBOperator.IN));
		if(NullUtil.isNotEmpty(commentList)){
			for(OrdersComment comment : commentList){
				for(Orders order : orderList){
					if(comment.getOrderId().longValue() == order.getId()){
						order.addAttribute("commentGrade", comment.getGrade());
						break;
					}
				}
			}
		}
		
		return new ResponseBody("order_list",orderList)
					.add("user_list", userList)
					.addTotalCount(count);
	}
	
	
	/**
	 * 某个某张订单的具体评价
	 * @param context
	 * 			order_id
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:34:49
	 */
	public ResponseBody queryComment(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Order.order_id);
		long orderId = context.getRequestBody().getLong(ParamDefine.Order.order_id);
		
		OrdersComment comment = dao().querySingle(OrdersComment.class, new DBCondition(OrdersComment.JField.orderId,orderId));
		if(comment == null)
			return null;
		return new ResponseBody("comment",comment.getComment())
					.add("grade", comment.getGrade())
					.add("createTime", comment.getCreateTime())
		;
	}
	
	/**
	 * 某个订单的所有状态定义数据
	 * @param context
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午8:34:49
	 */
	public ResponseBody queryStatusDefineList(MisRequestContext context) throws Exception{
		OrderStatusDefine[] states = OrderStatusDefine.class.getEnumConstants();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(OrderStatusDefine define : states){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("code", define.getState());
			map.put("text", define.getText());
			result.add(map);
		}
		return new ResponseBody("result",result);
	}
	
	/**
	 * 订单审批，包括导游推单审批，游客退订审批
	 * @param context
	 * 			order_id,
	 * 			action,0-reject,1-pass
	 * 			memo,驳回操作会有备注
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午3:23:42
	 */
	public ResponseBody approve(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Order.order_id,
				ParamDefine.Approve.action
		);
		long orderId = context.getRequestBody().getLong(ParamDefine.Order.order_id);
		Short action = context.getRequestBody().getShort(ParamDefine.Approve.action);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		Orders orders = OrderCmp.checkOrderExist(orderId);
		OrderStatusDefine newStatus = null;
		boolean isPassed = action==ConstantDefine.APPROVE_PASS;
		if(orders.getStatus() == OrderStatusDefine.CANCEL_APPLY.getState()){
			//游客退订申请
			newStatus = isPassed?
							OrderStatusDefine.CANCELED://退订审核通过变为：已退订
							OrderStatusDefine.PAYED;//退订审核驳回仍旧变为:已支付
			//发送通知
			TicketMain main = TicketCmp.queryMainByTicketId(orders.getTicketId());
			if(isPassed){
				//申请通过，目标：游客
				Person visitor = PersonCmp.queryById(orders.getVisitorUserId());
				User user = UserCmp.queryUserById(orders.getVisitorUserId());
				NotifyCmp.sendTemplate(
					NotifyDefine.CodeDefine.approve_refund_4_visitor, 
					new ParamHolder(NotifyDefine.ApproveRefund4Visitor.ticket_name.name(),main.getName()),
					new NotifyTargetHolder().addUser(user)
				);
				
				//申请通过，目标：导游
				User guider = UserCmp.queryUserById(orders.getGuiderUserId(), User.JField.mobile,User.JField.id,User.JField.email);
				NotifyCmp.sendTemplate(
					NotifyDefine.CodeDefine.approve_refund_4_guider, 
					new ParamHolder(NotifyDefine.ApproveRefund4Guider.ticket_name.name(),main.getName())
								.addParam(NotifyDefine.ApproveRefund4Guider.visitor_nickname.name(), visitor.getName()),
					new NotifyTargetHolder().addUser(guider)
				);
			}else{
				//申请驳回，目标：游客
				User visitor = UserCmp.queryUserById(orders.getVisitorUserId(), User.JField.mobile,User.JField.id,User.JField.email);
				NotifyCmp.sendTemplate(
					NotifyDefine.CodeDefine.reject_refund,
					new ParamHolder(NotifyDefine.RejectRefund.ticket_name.name(),main.getName())
							.addParam(NotifyDefine.RejectRefund.reason.name(), memo),
					new NotifyTargetHolder().addUser(visitor)
				);
			}
		}/*else if(orders.getStatus() == OrderStatusDefine.REJECT_APPLY.getState()){
			//导游拒单申请
			newStatus = isPassed?
					OrderStatusDefine.REJECTED://退订审核通过变为：已推单
					OrderStatusDefine.PAYED;//退订审核驳回仍旧变为:已支付
		}*/else{
			throw ETUtil.buildException(ErrorDefine.ORDER_STATUS_NOTALLOWED);
		}
		
		OrderCmp.changeStatus(orders, newStatus.getState(), memo);
		return new ResponseBody("new_status",newStatus.getState()).add("new_status_text", newStatus.getText());
	}
	
	public ResponseBody buildTransferForm(MisRequestContext context) throws Exception{
		//String respHtml = AlipayUtil.transfer();
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Order.transaction_code);
		String tradeNo = context.getRequestBody().getString(ParamDefine.Order.transaction_code);
		StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + AlipayUtil.URL_ALIPAY_GATEWAY
                      + "?_input_charset=" + AlipayUtil.CHARSET + "\" method=\"get\">");
		
        Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("service", "refund_fastpay_by_platform_pwd");
		paramMap.put("partner", AlipayUtil.PARTNER_ID);
		paramMap.put("_input_charset", AlipayUtil.CHARSET);
		paramMap.put("seller_user_id", AlipayUtil.PARTNER_ID);
		paramMap.put("refund_date", DateUtil.now19());
		paramMap.put("batch_no", DateUtil.now14()+RandomUtil.getNum(6));
		paramMap.put("batch_num", "1");
		paramMap.put("notify_url", "http://114.215.199.197/elsetravel/wxpay/order/completePayByWechat.do");
		paramMap.put("detail_data", tradeNo+"^0.01^订单取消退款");

		String paramStr = ETUtil.createSortedUrlStr(paramMap) + AlipayUtil.PRIVATE_KEY;
		String sign = EncryptUtil.MD5.encode(paramStr);
		paramMap.put("sign", sign);
		paramMap.put("sign_type", AlipayUtil.SIGN_TYPE);
		
		Iterator<Entry<String,String>> it = paramMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,String> entry = it.next();
			sbHtml.append("<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\"/>");
		}

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"确定\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
        
		return new ResponseBody("result",sbHtml.toString());
	}
}
