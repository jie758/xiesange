package com.xiesange.web.component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pingplusplus.model.Charge;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.baseweb.notify.NotifyCmp;
import com.xiesange.baseweb.notify.NotifyDefine;
import com.xiesange.baseweb.notify.NotifyTargetHolder;
import com.xiesange.baseweb.util.PingppUtil;
import com.xiesange.core.enumdefine.KeyValueHolder;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.activity.ActivityJoin;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersComment;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.orders.OrdersOperation;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.gen.dbentity.user.UserCoupon;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.FieldUpdateExpression;
import com.xiesange.orm.NativeValue;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.orm.statement.update.UpdateStatement;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.define.ConsDefine.ACTIVITY_TYPE;
import com.xiesange.web.define.ErrorDefine;
import com.xiesange.web.define.OrderStatusDefine;
import com.xiesange.web.define.OrderStatusDefine.CUST;
import com.xiesange.web.promotion.AbstractPromotionBean;
import com.xiesange.web.promotion.PromotionManager;

public class OrderCmp {
	private static final String PATTERN_CUST4EMAIL = "客户：<B>$name($mobile)</B> <BR/>编号:$orderCode<BR/>$address";
	public static final String PATTERN_EMAIL4SALE = "$name($spec)<BR/>$amount$unit x $price元(<font color='#FF0000'>$costPrice元</font>)/$unit = $sum元(<font color='#FF0000'>$costSum元</font>)";
	
	public static final String PATTERN_EMAIL_TOTAL4SALE = "$sum元(<font color='#FF0000'>$costSum元</font>)";
	
	
	public static Orders queryById(long orderId,boolean needItems,BaseJField... jfs) throws Exception{
		Orders orderEntity = DBHelper.getDao().queryById(Orders.class, orderId,jfs);
		if(orderEntity != null && needItems){
			appendItems(orderEntity);
		}
		return orderEntity;
	}
	
	public static Orders checkExist(long orderId,boolean needItems) throws Exception{
		Orders orderEntity = queryById(orderId,needItems);
		if(orderEntity == null){
			throw ETUtil.buildException(ErrorDefine.ORDER_NOTEXIST);
		}
		return orderEntity;
	}
	
	public static List<OrdersItem> queryItemsByOrderId(long orderId,BaseJField... jfs) throws Exception{
		List<OrdersItem> items = DBHelper.getDao().query(OrdersItem.class,new DBCondition(OrdersItem.JField.orderId,orderId));
		return items;
	}
	
	public static void transferItem(OrdersItem item,boolean needCostPrice){
		if(item.getPrice() != null){
			item.addAttribute(OrdersItem.JField.price.getName(), ETUtil.parseFen2Yuan(item.getPrice()));
		}
		if(item.getSum() != null){
			item.addAttribute(OrdersItem.JField.sum.getName(), ETUtil.parseFen2Yuan(item.getSum()));
		}
		if(needCostPrice == false){
			item.setCostPrice(null);
		}
	}
	
	public static void transfer(Orders order) throws Exception{
		if(order.getSum() != null){
			order.addAttribute(Orders.JField.sum.getName(), ETUtil.parseFen2Yuan(order.getSum()));
			order.setSum(null);
		}
		if(order.getOrigSum() != null){
			order.addAttribute(Orders.JField.origSum.getName(), ETUtil.parseFen2Yuan(order.getOrigSum()));
			order.setOrigSum(null);
		}
		if(order.getExpressSum() != null){
			order.addAttribute(Orders.JField.expressSum.getName(), ETUtil.parseFen2Yuan(order.getExpressSum()));
			order.setExpressSum(null);
		}
		if(order.getExpressCost() != null){
			order.addAttribute(Orders.JField.expressCost.getName(), ETUtil.parseFen2Yuan(order.getExpressCost()));
			order.setExpressCost(null);
		}
		if(order.getCost() != null){
			order.addAttribute(Orders.JField.cost.getName(), ETUtil.parseFen2Yuan(order.getCost()));
			order.setCost(null);
		}
		/*if(order.getCouponId() != null){
			UserCoupon couponEntity = DBHelper.getDao().queryById(UserCoupon.class, order.getCouponId(),
					UserCoupon.JField.id,
					UserCoupon.JField.type,
					UserCoupon.JField.value);
			if(couponEntity != null){
				CouponCmp.transfer(couponEntity);
				order.addAttribute("coupon", couponEntity);
			}
		}*/
	}
	
	public static void transferComment(OrdersComment comment) throws Exception{
		if(NullUtil.isNotEmpty(comment.getTags())){
			String[] tags = comment.getTags().split("\\|");
			comment.addAttribute("tags", tags);
			comment.setTags(null);
		}
	}
	public static String generateOrderCode(long orderId){
		return DateUtil.now_yyyymmdd() + "_" + orderId;
	}
	
	/**
	 * 根据订单中的产品列表，逐项检查该产品价格是否有变动，如一项有变动则返回true
	 * @param items
	 * 			OrdersItem实体列表，注意实体中的price单位是分
	 * @throws Exception 
	 */
	public static boolean isPriceChanged(List<OrdersItem> items) throws Exception{
		Product prodEntity = null;
		for(OrdersItem orderItem : items){
			prodEntity = (Product)orderItem.getAttr("product");
			if(prodEntity == null){
				prodEntity = ProdCmp.queryProduct(orderItem.getProductId());
			}	
			if(orderItem.getPrice() != prodEntity.getPrice().longValue()){
				return true;
			}
		}
		return false;
	}
	
	public static void checkPriceChanged(List<OrdersItem> items) throws Exception{
		if(isPriceChanged(items)){
			throw ETUtil.buildException(ErrorDefine.ORDER_PRICE_CHANGED);
		}
	}
	
	public static List<OrdersItem> appendItems(List<Orders> orderList,boolean needCostPrice,boolean needPromotion,BaseJField...jfs) throws Exception{
		if(NullUtil.isEmpty(orderList))
			return null;
		Set<Long> orderIds = ETUtil.buildEntityIdList(orderList, Orders.JField.id);
		QueryStatement st = new QueryStatement(OrdersItem.class,new DBCondition(OrdersItem.JField.orderId,orderIds,DBOperator.IN));
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
			st.appendQueryField(OrdersItem.JField.orderId);
		}
		List<OrdersItem> allItems = DBHelper.getDao().query(st); 
		if(NullUtil.isEmpty(allItems)){
			return null;
		}
		
		List<OrdersItem> itemList = null;//某个订单的item列表
		Set<Long> promIds = ClassUtil.newSet();
		//Set<Long> createOrderIds = ClassUtil.newSet();//需要查询的团购中的主订单id
		//List<OrdersItem> groupItems = ClassUtil.newList();
		for(Orders order : orderList){
			//boolean isGroupbuy = OrderCmp.isGroupbuy(order.getBuyType());
			itemList = ClassUtil.newList();
			for(OrdersItem item : allItems){
				if(item.getOrderId() == order.getId().longValue()){
					itemList.add(item);
					/*if(isGroupbuy){
						userIds.add(item.getUserId());
						groupItems.add(item);
					}*/
				}
				if(!needCostPrice){
					item.setCostPrice(null);
				}
			}
			order.addAttribute("items", itemList);
			if(order.getPromotionId() != null){
				promIds.add(order.getPromotionId());
			}
		}
		if(needPromotion && NullUtil.isNotEmpty(promIds)){
			List<Promotion> promList = DBHelper.getDao().queryByIds(Promotion.class, promIds);
			if(NullUtil.isNotEmpty(promList)){
				for(Orders order : orderList){
					if(order.getPromotionId() == null){
						continue;
					}
					for(Promotion prom : promList){
						if(order.getPromotionId().longValue() == prom.getId()){
							order.addAttribute("promotion", prom);
							break;
						}
					}
				}
			}
		}
		
		/*if(NullUtil.isNotEmpty(userIds)){
			List<User> userList = DBHelper.getDao().queryByIds(User.class,userIds,User.JField.id,User.JField.name,User.JField.mobile);
			for(OrdersItem item : groupItems){
				for(User u : userList){
					if(item.getUserId().longValue() == u.getId()){
						item.addAttribute("user", u);
						break;
					}
				}
			}
		}*/
		return allItems;
	}
	
	public static List<Product> appendProducts(List<OrdersItem> itemList,boolean needCostPrice,BaseJField... jfs) throws Exception{
		Set<Long> prodIds = ETUtil.buildEntityIdList(itemList, OrdersItem.JField.productId);
		QueryStatement st = new QueryStatement(
				Product.class,new DBCondition(Product.JField.id,prodIds,DBOperator.IN));
		
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		
		List<Product> allProds = DBHelper.getDao().query(st); 
		if(NullUtil.isEmpty(allProds)){
			return null;
		}
		
		for(Product prod : allProds){
			if(!needCostPrice){
				prod.setCostPrice(null);
			}
			for(OrdersItem item : itemList){
				if(item.getProductId() == prod.getId().longValue()){
					item.addAttribute("product", prod);
				}
			}
		}
		return allProds;
	}
	
	public static List<OrdersItem> appendItems(Orders orderEntity) throws Exception{
		QueryStatement st = new QueryStatement(OrdersItem.class,new DBCondition(OrdersItem.JField.orderId,orderEntity.getId()));
		st.appendOrderField(OrdersItem.JField.id);//按照id从先到后排列
		List<OrdersItem> items = DBHelper.getDao().query(st); 
		orderEntity.addAttribute("items", items);
		return items;
	}
	
	/**
	 * 发送采购联系单
	 * @param orderList
	 * @param prodList
	 * @throws Exception 
	 */
	/*public static void sendPurchase(List<Orders> orderList) throws Exception{
		List<ExcelHeaderCell> headers = ClassUtil.newList();
		headers.add(new ExcelHeaderCell("name","客户",220));
		headers.add(new ExcelHeaderCell("prod","订购产品",220));
		headers.add(new ExcelHeaderCell("memo","备注",150));
		List<ExcelBodyRow> datalist = ClassUtil.newList();
		
		String custName = null;
		Product prod = null;
		List<OrdersItem> items = null;
		
		int custCount = 0;//总客户数
		Map<Long,Object[]> prodStat = ClassUtil.newMap();//统计各个产品数量,Object[]2个元素依次是，数量，产品对象
		long totalCostSum = 0;//总采购金额总计,用进货价计算
		long orderCostSum = 0 ;//每笔订单采购金额小计
		
		long totalSum = 0;//总销售金额总计,用销售价计算
		long orderSum = 0 ;//每笔订单销售金额小计
		
		StringBuffer sb_mail = new StringBuffer();//采购订单邮件明细，要包含采购单价和金额
		StringBuilder sb_purchase = new StringBuilder();//发给采购方的清单，不需要金额，只要数量
		String pattern4purchase = "$name($spec)，$amount$unit";
		
		List<OrdersPurchase> purchaseList = ClassUtil.newList();
		for(Orders order : orderList){
			items = (List<OrdersItem>)order.getAttr("items");
			if(NullUtil.isEmpty(items)){
				continue;//没有明细，不生成采购联系单
			}
			custCount++;
			custName = order.getName()+"("+order.getMobile()+")\n\n"+order.getAddress();
			sb_purchase.setLength(0);//清空
			
			sb_mail.append(buildOrderText(order,PATTERN_EMAIL4PURCHASE,PATTERN_EMAIL_TOTAL4PURCHASE)).append("<BR/><BR/>");
					
			orderCostSum = 0;
			orderSum = 0;
			for(OrdersItem item : items){
				prod = (Product)item.getAttr("product");
				sb_purchase.append(OrderCmp.buildOrderItemText(item, prod,pattern4purchase)).append("\n");
				
				orderCostSum += item.getAmount()*item.getCostPrice();
				orderSum += item.getAmount()*item.getPrice();
						
				Object[] prodInfo = prodStat.get(prod.getId());
				if(prodInfo == null){
					prodInfo = new Object[]{0,prod};
					prodStat.put(prod.getId(), prodInfo);
				}
				prodInfo[0] = (Integer)prodInfo[0]+item.getAmount();
			}
			totalCostSum += orderCostSum;//累加
			totalSum += order.getSum();//累加
			
			
			datalist.add(new ExcelBodyRow()
							.addCell("name", custName)
							.addCell("prod", sb_purchase.toString())
			);
			datalist.add(new ExcelBodyRow(20,null));//空行
			
			OrdersPurchase purchase = new OrdersPurchase();
			purchase.setOrderId(order.getId());
			purchaseList.add(purchase);
		}
		
		datalist.add(0,new ExcelBodyRow().addCell("name", "总计:"));
		datalist.add(1,new ExcelBodyRow().addCell("name", "    客户数："+custCount+"位；"));
		//datalist.add(2,new ExcelBodyRow().addCell("name", "    总金额："+ETUtil.parseFen2YuanStr(totalSum,false)+"元；"));
		datalist.add(2,new ExcelBodyRow(20,null));//空行
		//各个产品分类总计
		Iterator<Object[]> it = prodStat.values().iterator();
		int index = 3;
		StringBuffer sb_email_final = new StringBuffer("<div style='font-size:13px;'>明细统计:<BR/>");
		while(it.hasNext()){
			Object[] prodInfo = it.next();
			prod = (Product)prodInfo[1];
			datalist.add(index++,new ExcelBodyRow().addCell("name", "    "+prod.getName()+"("+prod.getSpec()+")："+prodInfo[0]+prod.getUnit()));
			sb_email_final.append("    "+prod.getName()+"("+prod.getSpec()+")："+prodInfo[0]+prod.getUnit())
					.append("<BR/>");
		}
		sb_email_final.append("<B>总计:")
					  .append(PATTERN_EMAIL_TOTAL4PURCHASE
							  	.replaceAll("\\$costSum",ETUtil.parseFen2YuanStr(totalCostSum, false))
							  	.replaceAll("\\$sum", ETUtil.parseFen2YuanStr(totalSum, false)))
					  .append("</B>");
		sb_email_final.append("<BR/><BR/>").append(sb_mail).append("</div>");
		
		datalist.add(index++,new ExcelBodyRow(20,null));//空行
		datalist.add(index++,new ExcelBodyRow(20,null));//空行
		
		
		String fileName = "采购订单-"+DateUtil.now().getTime()+".xls";
		String path = SysparamCmp.get(SysParamDefine.RESOURCE_SAVE_PATH)+File.separator+fileName;
		ExcelUtil.generate(path, headers, datalist,18);
		
		//存储到采购表里,要把原数据清空
		new DeleteStatement(OrdersPurchase.class).execute();
		DBHelper.getDao().insertBatch(purchaseList);
		
		EMailInfo mailInfo = new EMailInfo(fileName,sb_email_final.toString());
		mailInfo.addTo(new EmailNotifyTarget("wuyj@xiesange.com"));
		mailInfo.addAttach(path,"采购订单-"+DateUtil.nowStr(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMM)+".xls");
		TaskManager.execute(new EmailNotifyTaskBean(mailInfo,false));
		
		
		
		
		LogUtil.getLogger(OrderCmp.class).debug("delete file");
		//EMailUtil.sendHTML(mailInfo);
		//FileUtil.delFile(path);
	}*/
	
	public static String buildOrderText(Orders order,String itemPattern,String totalPattern){
		StringBuffer sb = new StringBuffer();
		//订单主体信息
		sb.append(PATTERN_CUST4EMAIL.replaceAll("\\$name", order.getName())
				.replaceAll("\\$mobile", order.getMobile())
				.replaceAll("\\$orderCode", order.getCode())
				.replaceAll("\\$address", order.getAddress())
		).append("<BR/>");
		
		//订单明细
		List<OrdersItem> items = (List<OrdersItem>)order.getAttr("items");
		
		sb.append(buildOrderItemsText(items,order.getSum(),itemPattern,totalPattern));
		
		return sb.toString();
	}
	
	public static String buildOrderProductText(List<OrdersItem> items){
		Product prod = null;
		StringBuffer sb = new StringBuffer();
		for(OrdersItem item : items){
			prod = (Product)item.getAttr("product");
			String pname = NullUtil.isEmpty(prod.getPname()) ? prod.getName():prod.getPname();
			sb.append( pname+":"+item.getAmount()+"斤; ");
		}
		
		return sb.toString();
	}
	
	public static String buildOrderItemsText(List<OrdersItem> items,Long totalSum,String itemPattern,String totalPattern){
		Product prod = null;
		long sum = 0;
		long costSum = 0;
		StringBuffer sb = new StringBuffer();
		for(OrdersItem item : items){
			prod = (Product)item.getAttr("product");
			sb.append("<div style='margin-bottom:5px;color:#585858;'>").append(OrderCmp.buildOrderItemText(item, prod,itemPattern)).append("</div>");
			costSum += item.getAmount()*item.getCostPrice();
			sum += item.getAmount()*item.getPrice();
		}
		if(totalSum != null){
			//小计
			sb.append("小计:")
				.append(totalPattern
							.replaceAll("\\$costSum", ETUtil.parseFen2YuanStr(costSum, false))
							.replaceAll("\\$sum", ETUtil.parseFen2YuanStr(totalSum, false)))
				;
		}
		return sb.toString();
	}
	
	/**
	 * 把一条订单条目转换成可阅读的文本。
	 * 比如：梭子蟹（3两/只）,1斤 x 20元/斤 = 20元
	 * @param item
	 * @param prodEntity
	 * @return
	 */
	public static String buildOrderItemText(OrdersItem item,Product prod,String pattern){
		pattern = pattern.replaceAll("\\$name", NullUtil.isEmpty(prod.getPname()) ? prod.getName():prod.getPname())
			    .replaceAll("\\$spec", prod.getSpec())
			    .replaceAll("\\$unit", prod.getUnit())
			    .replaceAll("\\$amount", String.valueOf(item.getAmount()))
			    .replaceAll("\\$costPrice", ETUtil.parseFen2YuanStr(item.getCostPrice(), false))
			    .replaceAll("\\$costSum", ETUtil.parseFen2YuanStr(item.getCostPrice()*item.getAmount(), false))
			    .replaceAll("\\$price", ETUtil.parseFen2YuanStr(item.getPrice(), false))
			    .replaceAll("\\$sum", ETUtil.parseFen2YuanStr(item.getPrice()*item.getAmount(), false));
		
		return pattern;
	}
	
	
	public static void checkSwitch() throws Exception{
		boolean isOn = SysparamCmp.getBoolean(SysparamDefine.ORDER_SWITCH);
		if(!isOn){
			throw ETUtil.buildException(ErrorDefine.ORDER_OFF);
		}
	}
	
	
	/**
	 * 订单状态更变操作。不仅会改变当前订单主表的status值，还会往订单状态操作表里插入一条记录
	 * @param order
	 * 			Orders对象，id必须要有值，其他属性可以根据业务需要是否要更新来设置
	 * @param newStatus，新状态值
	 * @param memo,状态更改备注，有些驳回操作是会有备注的
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:13:29
	 */
	public static void changeStatus(Orders order,short newStatus,String memo) throws Exception{
		order.setStatus(newStatus);
		DBHelper.getDao().updateById(order, order.getId());
		
		OrdersOperation oper = new OrdersOperation();
		oper.setOrderId(order.getId());
		oper.setStatus(newStatus);
		oper.setMemo(memo);
		
		DBHelper.getDao().insert(oper);
	}
	
	/**
	 * 往每个order对象里添加"coupon"对象
	 * @param orderList
	 * @throws Exception 
	 */
	public static void appendCoupons(List<Orders> orderList) throws Exception{
		Set<Long> couponIds = ETUtil.buildEntityIdList(orderList, Orders.JField.couponId);
		if(NullUtil.isEmpty(couponIds))
			return;
		List<UserCoupon> couponList = NullUtil.isEmpty(couponIds) ? null : 
			DBHelper.getDao().queryByIds(UserCoupon.class, couponIds,
				UserCoupon.JField.id,
				UserCoupon.JField.type,
				UserCoupon.JField.value);
		
		if(NullUtil.isEmpty(couponList)){
			return;
		}
		
		for(UserCoupon coupon : couponList){
			CouponCmp.transfer(coupon);
			
			for(Orders order : orderList){
				if(order.getCouponId() != null && order.getCouponId().longValue() == coupon.getId()){
					order.addAttribute("coupon", coupon);
					break;
				}
			}
		}
	}
	
	/**
	 * 重新计算整个订单的价格，适用于未支付状态。
	 * 因为未支付状态，订单价格需要实时跟着产品变动;还有运费也要根据整个订单计算
	 * @param order
	 * @param items
	 * @throws Exception
	 */
	public static void refreshSum(Orders order,List<OrdersItem> items) throws Exception{
		short status = order.getStatus();
		if(status != OrderStatusDefine.CUST.EDIT.state()){
			return;
		}
		String[] feeRules = SysparamCmp.getArray(SysparamDefine.ORDER_EXPRESSFEE_RULE, ",");
		long orderSum = 0;
		/*long expressFee = Integer.parseInt(feeRules[0])*100;
		long freeExpressSum = Integer.parseInt(feeRules[1])*100;*/
		
		Promotion prom = (Promotion)order.getAttr("promotion");
		//UserCoupon coupon = (UserCoupon)order.getAttr("coupon");
		Long promPrice = prom==null?null:Long.parseLong(prom.getValue());
		for(OrdersItem item : items){
			Product prod = (Product)item.getAttr("product");
			if(prod == null){
				continue;
			}
			long price = 0;
			if(promPrice!=null){
				price = promPrice;
			}else if(OrderCmp.isTogetherbuy(order.getBuyType()) || order.getStatus() == OrderStatusDefine.CUST.ADJUST_PRICE.state()){
				price = item.getPrice();
			}else{
				price = prod.getPrice();
			}
			prod.setPrice(price);
			//long price = promPrice!=null ?promPrice:prod.getPrice();
			
			//如果未支付，那么需要取产品里的价格
			item.setPrice(price);
			item.setSum(item.getAmount()*price);
			
			orderSum += item.getSum();
		}
		order.setOrigSum(orderSum);
		if(OrderCmp.isPreorderBuy(order.getBuyType()) && order.getStatus() == OrderStatusDefine.CUST.PAYED.state()){
			;//如果是预订且已经支付的，那么运费就按照当时预订的时候来算 
		}else if(OrderCmp.isTogetherbuy(order.getBuyType())){
			order.setExpressSum(0L);//拼团都包邮
		}else{
			order.setExpressSum(calcExpressfee(orderSum));
		}
		
		order.setSum(orderSum);
	}
	
	public static long applyPromotion(List<Promotion> globalPromList,Orders orderEntity) throws Exception{
		//查询促销规则
		long paySum = orderEntity.getOrigSum();
		if(NullUtil.isEmpty(globalPromList)){
			return paySum;
		}
		
		for(Promotion prom : globalPromList){
			AbstractPromotionBean prombean = PromotionCmp.applyPromotion(orderEntity, prom);
			if(prombean.isMatched(orderEntity)){
				prom.addAttribute("text", PromotionManager.getPromotionBean(prom).buildText());
				prom.addAttribute("isMatched", 1);
			}
		}
		return orderEntity.getSum();
	}
	
	public static List<UserCoupon> queryCouponList(Orders orderEntity) throws Exception{
		List<UserCoupon> couponList = CouponCmp.queryListByUserId(orderEntity.getUserId(),true);
		if(NullUtil.isNotEmpty(couponList)){
			for(int i=0;i<couponList.size();i++){
				UserCoupon coupon = couponList.get(i);
				if(!CouponCmp.canUse(coupon,orderEntity)){
					couponList.remove(coupon);
					i--;
				}
				CouponCmp.transfer(coupon);
				ETUtil.clearDBEntityExtraAttr(coupon);
			}
		}
		return couponList;
	}
	public static boolean isSingleBuy(short type){
		return type == ConsDefine.ORDER_BUYTYPE.SINGLE.value();
	}
	public static boolean isPreorderBuy(short type){
		return type == ConsDefine.ORDER_BUYTYPE.PREORDER.value();
	}
	public static boolean isTogetherbuy(short type){
		return type == ConsDefine.ORDER_BUYTYPE.TOGETHERBUY.value();
	}
	/*public static boolean isGroupbuy(short type){
		return type == ConsDefine.ORDER_BUYTYPE.GROUPBUY.value();
	}*/
	
	public static void completeOrder(Map<String,String> metaData,Charge charge) throws Exception{
		LogUtil.dump("-------metaData", metaData);
		long orderId = Long.valueOf(metaData.get("orderId"));
		long origSum = Long.valueOf(metaData.get("origSum"));
		long sum = Long.valueOf(metaData.get("sum"));
		Long expressFee = metaData.get("expressSum")==null?0:Long.valueOf(metaData.get("expressSum"));
		Long couponId = NullUtil.isEmpty(metaData.get("couponId"))?null:Long.valueOf(metaData.get("couponId"));
		//String itemCost = metaData.get("itemCost");//各个明细的成本
		
		Orders orderEntity = OrderCmp.queryById(orderId,true);
		if(orderEntity == null){
			return;
		}
		short buytype = orderEntity.getBuyType();
		boolean isTogeBuy = OrderCmp.isTogetherbuy(buytype);
		boolean isPreorder = OrderCmp.isPreorderBuy(buytype);
		User user = UserCmp.queryById(orderEntity.getUserId());
		long userId = orderEntity.getUserId();
		//如果有优惠券的话需要消耗该优惠券
		String couponText = null;
		if(couponId != null){
			UserCoupon couponEntity = CouponCmp.checkById(couponId);
			if(couponEntity != null){
				CouponCmp.consumeCoupon(couponEntity);
				couponText = CouponCmp.buildCouponText(couponEntity);
			}
		}
		
		orderEntity.setOrigSum(origSum);
		orderEntity.setExpressSum(expressFee);
		orderEntity.setCouponId(couponId);
		orderEntity.setPayChannel(PingppUtil.trans2XSGChannel(charge.getChannel()));
		orderEntity.setTransactionCode(charge.getTransactionNo());
		
		//更新各条明细的成本价，并且统计成所有明细总成本价，作为订单成本总价
		/*List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
		if(NullUtil.isNotEmpty(items)){
			long totalCost = 0;
			OrderCmp.appendProducts(items, true);
			List<UpdateStatement> itemUpdateList = ClassUtil.newList();
			for(OrdersItem item : items){
				Product prod = (Product)item.getAttr("product");
				if(prod == null){
					continue;
				}
				OrdersItem itemUpdate = new OrdersItem();
				itemUpdate.setPrice(prod.getPrice());
				itemUpdate.setCostPrice(prod.getCostPrice());
				itemUpdate.setSum(prod.getPrice() * item.getAmount());
				itemUpdateList.add(new UpdateStatement(itemUpdate,item.getId()));
				totalCost += prod.getCostPrice()*item.getAmount();
			}
			DBHelper.getDao().updateBatch(itemUpdateList.toArray(new UpdateStatement[itemUpdateList.size()]));
			orderEntity.setCost(totalCost);
		}*/
		
		//如果是补余款，那么要把订单类型从预订改为正常
		/*boolean isFillPay = isPreorder && orderEntity.getStatus() == 1;
		if(isFillPay){
			orderEntity.setBuyType(ConsDefine.ORDER_BUYTYPE.SINGLE.value());
			orderEntity.setSum(sum+orderEntity.getSum());//当前支付的+之前预付的
			DBHelper.getDao().updateById(orderEntity, orderEntity.getId());
			
			//更新该用户的总订购次数,总金额，以及最近一次订购时间
			FieldUpdateExpression[] updateValues = new FieldUpdateExpression[]{
				new FieldUpdateExpression(User.JField.orderCount,new NativeValue(User.JField.orderCount.getColName()+(isTogeBuy?"+0":"+1"))),//团购不计入购买次数
				new FieldUpdateExpression(User.JField.orderSum,new NativeValue(User.JField.orderSum.getColName()+"+"+orderEntity.getSum())),
				new FieldUpdateExpression(User.JField.lastOrderTime,DateUtil.now())
			};
			DBHelper.getDao().updateById(updateValues, userId);
			
		}else{*/
		orderEntity.setSum(sum);
		OrderCmp.changeStatus(orderEntity, OrderStatusDefine.CUST.PAYED.state(), null);
		//}
		
		try{
			String orderSum = ETUtil.parseFen2YuanStr(orderEntity.getSum()+orderEntity.getExpressSum(),false)+"元";
			//if(OrderCmp.isSingleBuy(buytype)){
			//游客发送下单成功通知
			NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.order_payed, 
					new KeyValueHolder("name",orderEntity.getName())
							.addParam("coupon",NullUtil.isEmpty(couponText)?"无":couponText)
							.addParam("order_code", String.valueOf(orderEntity.getId()))
							.addParam("order_sum", orderSum),
					new NotifyTargetHolder().addMobile(user.getMobile())
							.addOpenid(user.getWechat())
			);
			//}
			//下单成功通知客服
			//List<OrdersItem> items = OrderCmp.queryItemsByOrderId(orderEntity.getId());
			//OrderCmp.appendProducts(items,false);
			NotifyCmp.sendSysNotify(NotifyDefine.CodeDefine.sys_notify, "<新订单>"+orderEntity.getId()+",￥"+orderSum+","+orderEntity.getName()+","+orderEntity.getMobile()+","+orderEntity.getAddress());
			/*NotifyCmp.sendTemplate(NotifyDefine.CodeDefine.sys_commit_order, 
					new KeyValueHolder("order_code",orderEntity.getCode())
							.addParam("order_id", orderEntity.getId())
							.addParam("order_sum", orderSum)
							.addParam("order_product", OrderCmp.buildOrderProductText(items))
							.addParam("group_type", isTogeBuy ? "【拼团】": ((isPreorder&&orderEntity.getStatus()==0) ? "【预订】" : ""))
							.addParam("linkman", orderEntity.getName())
							.addParam("mobile", orderEntity.getMobile())
							.addParam("address", orderEntity.getAddress()),
					null
			);*/
		}catch(Exception e){
			//提醒不要影响正常逻辑
			LogUtil.getLogger(OrderCmp.class).error(e,e);
		}
	}
	
	
	/*public static void completeGroupbuyOrder(Orders orderEntity,long orderItemId,Charge charge) throws Exception{
		OrdersItem itemEntity = DBHelper.getDao().queryById(OrdersItem.class, orderItemId);
		if(itemEntity == null){
			return;
		}
		//更新订单明细
		//itemEntity.setIsPayed((short)1);
		//DBHelper.getDao().updateById(itemEntity, orderItemId);
		
		//更新用户总金额，总订购数等
		long itemUserId = itemEntity.getUserId();
		//更新该用户的总订购次数,总金额，以及最近一次订购时间
		FieldUpdateExpression[] updateValues = new FieldUpdateExpression[] {
			new FieldUpdateExpression(User.JField.orderCount,new NativeValue(User.JField.orderCount.getColName()+"+1")),
			new FieldUpdateExpression(User.JField.orderSum,new NativeValue(User.JField.orderSum.getColName()+"+"+itemEntity.getSum())),
			new FieldUpdateExpression(User.JField.lastOrderTime,DateUtil.now())
		};
		DBHelper.getDao().updateById(updateValues, itemUserId);
		
		//long userId = orderEntity.getUserId();
		//发起人支付，则标识整个团购结束
		boolean isPayAll = orderEntity.getUserId() == itemUserId;
		if(isPayAll){
			//发起者如果支付则表示整个团购支付完成
			orderEntity.setPayChannel(PingppUtil.trans2XSGChannel(charge.getChannel()));
			orderEntity.setTransactionCode(charge.getTransactionNo());
			OrderCmp.changeStatus(orderEntity, OrderStatusDefine.CUST.PAYED.state(), null);
		}
	}*/
	
	public static long queryBargainSum(long orderId) throws Exception{
		List<ActivityJoin> joinList = ActivityCmp.queryJoinList(orderId, ACTIVITY_TYPE.ORDER_BARGAIN);
		if(NullUtil.isEmpty(joinList))
			return 0;
		long reduceSum = 0L;
		for(ActivityJoin join : joinList){
			reduceSum += Long.valueOf(join.getExt());
		}
		return reduceSum;
	}
	
	/**
	 * 根据订单原价，结合促销规则、优惠券，计算出订单折后的金额，不包括运费。
	 * 因此，最终支付金额还要加上运费
	 * @param orderEntity
	 * @param items
	 * @param globalPromList
	 * @param couponEntity
	 * @return
	 * @throws Exception
	 */
	public static long calcPaySum(Orders orderEntity,List<OrdersItem> items,List<Promotion> globalPromList,UserCoupon couponEntity) throws Exception{
		boolean isEdit = orderEntity.getStatus() == OrderStatusDefine.CUST.EDIT.state()
				|| OrderCmp.isPreorderBuy(orderEntity.getBuyType());
		
		if(!isEdit){
			//已支付的话 已当时支付的价格为准
			return orderEntity.getSum();
		}
		OrderCmp.refreshSum(orderEntity, items);
		long paySum = orderEntity.getOrigSum();
		LogUtil.getLogger(OrderCmp.class).debug("----after refresh sum : "+paySum);
		//应用优惠活动
		if(NullUtil.isNotEmpty(globalPromList)){
			paySum = OrderCmp.applyPromotion(globalPromList,orderEntity);//计算优惠活动
			LogUtil.getLogger(OrderCmp.class).debug("----after promotion : "+paySum);
			
		}
		//应用优惠券
		if(couponEntity != null && paySum > 0){
			paySum = CouponCmp.apply(couponEntity, paySum);
			LogUtil.getLogger(OrderCmp.class).debug("----after coupon : "+paySum);
			
		}
		orderEntity.setSum(paySum);
		//重新计算运费
		boolean isTogebuy = OrderCmp.isTogetherbuy(orderEntity.getBuyType());
		boolean isPreorder = OrderCmp.isPreorderBuy(orderEntity.getBuyType());
		if(isTogebuy){
			orderEntity.setExpressSum(0L);
		}/*else if(isPreorder){
			orderEntity.setExpressSum(orderEntity.getExpressSum());
		}*/else{
			orderEntity.setExpressSum(calcExpressfee(paySum));
		}
		
		LogUtil.getLogger(OrderCmp.class).debug("----finally sum : "+paySum);
		return orderEntity.getSum();
	}
	
	public static long calcExpressfee(long sum) throws Exception{
		LogUtil.getLogger(OrderCmp.class).debug("----calcExpressfee :sum = "+sum);
		String[] feeRules = SysparamCmp.getArray(SysparamDefine.ORDER_EXPRESSFEE_RULE, ",");
		float expressFee = 100*Float.parseFloat(feeRules[0]);//运费,配置的时候单位元，这里要转成分,下同
		float freeExpressSum = 100*Float.parseFloat(feeRules[1]);//达到包邮的订单实付金额阀值
		LogUtil.getLogger(OrderCmp.class).debug("----calcExpressfee :expressFee = "+expressFee+",freeExpressSum="+freeExpressSum);
		
		//freeExpressSum配置了小于0，或者订单金额没达到包邮金额，都需要支付运费
		return (sum < freeExpressSum || freeExpressSum < 0)? Float.valueOf(expressFee).longValue() : 0L;
	}
	
	public static boolean isEdit(short status){
		return status == OrderStatusDefine.CUST.EDIT.state();
	}
	
	public static String buildStatusText(short status){
		CUST[] statusList = OrderStatusDefine.CUST.values();
		for(CUST c : statusList){
			if(c.state() == status){
				return c.text();
			}
		}
		return "";
	}
}
