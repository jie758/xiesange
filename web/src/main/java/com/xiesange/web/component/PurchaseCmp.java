package com.xiesange.web.component;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.SysparamCmp;
import com.xiesange.baseweb.define.SysparamDefine;
import com.xiesange.baseweb.notify.TaskManager;
import com.xiesange.baseweb.notify.task.EmailNotifyTaskBean;
import com.xiesange.core.notify.mail.EMailInfo;
import com.xiesange.core.notify.target.EmailNotifyTarget;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.ExcelUtil;
import com.xiesange.core.util.ExcelUtil.ExcelBodyRow;
import com.xiesange.core.util.ExcelUtil.ExcelHeaderCell;
import com.xiesange.core.util.HttpUtil;
import com.xiesange.core.util.JsonUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.purchase.Purchase;
import com.xiesange.gen.dbentity.purchase.PurchaseItem;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.sql.DBOrCondition;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.pojo.SpeedaRoute;

public class PurchaseCmp {
	public static final String PATTERN_EMAIL_TOTAL4PURCHASE = "<font color='#FF0000'>$costSum元</font>($sum元)";
	public static final String PATTERN_EMAIL4PURCHASE = "$name($spec)<BR/>$amount$unit x <font color='#FF0000'>$costPrice元</font>($price元)/$unit = <font color='#FF0000'>$costSum元</font>($sum元)";
	
	public static void main(String[] args) throws Exception {
		/*List<SpeedaRoute> routeList = queryDeliveryInfo("100000217987");
		LogUtil.dump("xxxxxx", routeList);
		
		SpeedaRoute route = queryLatestDeliveryInfo("100000217987");
		LogUtil.dump("xxxxxx", route);*/
	}
	
	public static void transfer(Purchase purchase) throws Exception{
		if(purchase.getSum() != null){
			purchase.addAttribute(Purchase.JField.sum.getName(), ETUtil.parseFen2Yuan(purchase.getSum()));
			purchase.setSum(null);
		}
		
		if(purchase.getExpressFee() != null){
			purchase.addAttribute(Purchase.JField.expressFee.getName(), ETUtil.parseFen2Yuan(purchase.getExpressFee()));
			purchase.setExpressFee(null);
		}
		
		if(purchase.getOrderSum() != null){
			purchase.addAttribute(Purchase.JField.orderSum.getName(), ETUtil.parseFen2Yuan(purchase.getOrderSum()));
			purchase.setOrderSum(null);
		}
		if(purchase.getOrderExpressFee() != null){
			purchase.addAttribute(Purchase.JField.orderExpressFee.getName(), ETUtil.parseFen2Yuan(purchase.getOrderExpressFee()));
			purchase.setOrderExpressFee(null);
		}
	}
	
	public static void transferItem(PurchaseItem item) throws Exception{
		if(item.getSum() != null){
			item.addAttribute(PurchaseItem.JField.sum.getName(), ETUtil.parseFen2Yuan(item.getSum()));
			item.setSum(null);
		}
		
		if(item.getExpressFee() != null){
			item.addAttribute(PurchaseItem.JField.expressFee.getName(), ETUtil.parseFen2Yuan(item.getExpressFee()));
			item.setExpressFee(null);
		}
		
		if(item.getOrderSum() != null){
			item.addAttribute(PurchaseItem.JField.orderSum.getName(), ETUtil.parseFen2Yuan(item.getOrderSum()));
			item.setOrderSum(null);
		}
		if(item.getOrderExpressFee() != null){
			item.addAttribute(PurchaseItem.JField.orderExpressFee.getName(), ETUtil.parseFen2Yuan(item.getOrderExpressFee()));
			item.setOrderExpressFee(null);
		}
	}
	
	public static List<PurchaseItem> appendItems(List<Purchase> purchaseList,boolean needTransfer) throws Exception{
		Set<Long> purchaseIdSet = ClassUtil.newSet();
		for(Purchase purchase : purchaseList){
			purchaseIdSet.add(purchase.getId());
		}
		List<PurchaseItem> allItemList = DBHelper.getDao().query(PurchaseItem.class, 
				new DBCondition(PurchaseItem.JField.purchaseId,purchaseIdSet,DBOperator.IN));
		
		if(NullUtil.isEmpty(allItemList)){
			return null;
		}
		List<PurchaseItem> itemList = null;
		for(Purchase purchase : purchaseList){
			itemList = ClassUtil.newList();
			for(PurchaseItem item : allItemList){
				if(needTransfer){
					transferItem(item);
				}
				if(item.getPurchaseId() == purchase.getId().longValue()){
					itemList.add(item);
				}
			}
			purchase.addAttribute("items", itemList);
		}
		return allItemList;
	}
	
	public static List<Orders> appendOrders(List<PurchaseItem> itemList,boolean needTransfer) throws Exception{
		Set<Long> orderIds = ETUtil.buildEntityIdList(itemList, PurchaseItem.JField.orderId);
		List<Orders> orderList = DBHelper.getDao().query(Orders.class,new DBCondition(Orders.JField.id,orderIds,DBOperator.IN)); 
		if(NullUtil.isEmpty(orderList)){
			return null;
		}
		//List<Orders> creatorOrderList = ClassUtil.newList();
		//Set<Long> allOrderIds = ETUtil.buildEntityIdList(orderList, Orders.JField.id);
		//查询出明细,包含创建者的
		/*List<OrdersItem> orderItems = DBHelper.getDao().query(new QueryStatement(OrdersItem.class, 
			new DBCondition(OrdersItem.JField.orderId,orderIds,DBOperator.IN))
				.appendQueryField(OrdersItem.JField.id,OrdersItem.JField.productId,OrdersItem.JField.orderId,OrdersItem.JField.amount)
		);*/
		for(PurchaseItem p : itemList){
			for(Orders order : orderList){
				if(p.getOrderId().longValue() == order.getId()){
					OrderCmp.transfer(order);
					p.addAttribute("order", order);
					break;
				}
			}
		}
		
		//Set<Long> prodIds = ETUtil.buildEntityIdList(allItems, OrdersItem.JField.productId);
		/*List<Product> allProdList = DBHelper.getDao().queryByIds(Product.class, prodIds);
		
		Iterator<Long> it = orderIds.iterator();
		while(it.hasNext()){
			long orderId = it.next();
			Orders creatorOrder = null;
			long custs = 0;//拼单人数
			long amount = 0;//总数量
			Long productId = null;
			for(Orders order : orderList){
				if(order.getId() == orderId || order.getGroupbuyId() == orderId){
					if(order.getId() == orderId){
						creatorOrder = order;
					}
					custs++;
					for(OrdersItem item : allItems){
						if(item.getOrderId().longValue() == order.getId()){
							amount += item.getAmount();
							productId = item.getProductId();
							break;
						}
					}
				}
			}
			
			creatorOrder.addAttribute("productId", productId);
			creatorOrder.addAttribute("totalCust", custs);
			creatorOrder.addAttribute("totalAmount", amount);
			
			for(Product prod : allProdList){
				if(prod.getId().longValue() == productId){
					creatorOrder.addAttribute("productName", prod.getName());
					break;
				}
			}
			
			if(needTransfer){
				OrderCmp.transfer(creatorOrder);
			}
			for(PurchaseItem item : itemList){
				if(item.getOrderId() == orderId){
					item.addAttribute("order", creatorOrder);
					break;
				}
			}
			
			creatorOrderList.add(creatorOrder);
		}*/
		
		return orderList;
	}
	
	public static List<SpeedaRoute> queryDeliveryRoute(String deliveryNo) throws Exception{
		String url = "http://api.speeda.cn/openapi/common/route?appkey=5733f8f0c941a46d2bedb0d7&number_id="+deliveryNo+"&timestamp="+DateUtil.now().getTime()/1000;
		HttpGet httpGet = HttpUtil.createHttpGet(url,null);
		String str = HttpUtil.execute(httpGet);
		Map<String,Object> map = JsonUtil.json2Map(str);
		JSONArray array = (JSONArray)map.get("rows");
		if(array == null)
			return null;
		JSONArray routes = (JSONArray)((JSONObject)array.get(0)).get("routes");
		if(routes == null || routes.size() == 0)
			return null;
		
		List<SpeedaRoute> routeList = JsonUtil.json2List(routes, SpeedaRoute.class);
		return routeList;
	}
	
	/**
	 * 发送采购联系单
	 * @param orderList
	 * @param prodList
	 * @throws Exception 
	 */
	public static void sendPurchaseEmail(List<PurchaseItem> itemList) throws Exception{
		List<ExcelHeaderCell> headers = ClassUtil.newList();
		headers.add(new ExcelHeaderCell("name","客户",220));
		headers.add(new ExcelHeaderCell("prod","订购产品",220));
		headers.add(new ExcelHeaderCell("memo","备注",150));
		List<ExcelBodyRow> datalist = ClassUtil.newList();
		
		String custName = null;
		Product prod = null;
		List<OrdersItem> items = null;
		
		int custCount = 0;//总客户数
		Map<Long,Object[]> prodStat = ClassUtil.newMap();//统计各个产品数量,Object[]3个元素依次是，数量，产品对象,明细数量
		long totalCostSum = 0;//总采购金额总计,用进货价计算
		long orderCostSum = 0 ;//每笔订单采购金额小计
		
		long totalSum = 0;//总销售金额总计,用销售价计算
		long orderSum = 0 ;//每笔订单销售金额小计
		
		StringBuffer sb_mail = new StringBuffer();//采购订单邮件明细，要包含采购单价和金额
		StringBuilder sb_purchase = new StringBuilder();//发给采购方的清单，不需要金额，只要数量
		String pattern4purchase = "$name($spec)，$amount$unit";
		
		for(PurchaseItem item : itemList){
			Orders order = (Orders)item.getAttr("order");
			List<OrdersItem> orderItems = (List<OrdersItem>)order.getAttr("items");
			if(NullUtil.isEmpty(orderItems)){
				continue;//没有明细，不生成采购联系单
			}
			custCount++;
			custName = order.getName()+"("+order.getMobile()+")\n\n"+order.getAddress();
			sb_purchase.setLength(0);//清空
			
			sb_mail.append(OrderCmp.buildOrderText(order,PATTERN_EMAIL4PURCHASE,PATTERN_EMAIL_TOTAL4PURCHASE)).append("<BR/><BR/>");
					
			orderCostSum = 0;
			orderSum = 0;
			for(OrdersItem orderItem : orderItems){
				prod = (Product)orderItem.getAttr("product");
				sb_purchase.append(OrderCmp.buildOrderItemText(orderItem, prod,pattern4purchase)).append("\n");
				
				orderCostSum += orderItem.getAmount()*orderItem.getCostPrice();
				orderSum += orderItem.getAmount()*orderItem.getPrice();
						
				Object[] prodInfo = prodStat.get(prod.getId());
				if(prodInfo == null){
					prodInfo = new Object[]{0,prod,new StringBuffer()};
					prodStat.put(prod.getId(), prodInfo);
				}
				prodInfo[0] = (Integer)prodInfo[0]+orderItem.getAmount();
				((StringBuffer)prodInfo[2]).append("+").append(orderItem.getAmount()).append(prod.getUnit());
			}
			totalCostSum += orderCostSum;//累加
			totalSum += order.getSum();//累加
			
			
			datalist.add(new ExcelBodyRow()
							.addCell("name", custName)
							.addCell("prod", sb_purchase.toString())
			);
			datalist.add(new ExcelBodyRow(20,null));//空行
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
			String prodName = NullUtil.isNotEmpty(prod.getPname())?prod.getPname():prod.getName();
			datalist.add(index++,new ExcelBodyRow()
										.addCell("name", "    "+(prodName)+"("+prod.getSpec()+")："+prodInfo[0]+prod.getUnit())
										.addCell("prod", ((StringBuffer)prodInfo[2]).substring(1))
			);
			
			sb_email_final.append("    "+prodName+"("+prod.getSpec()+")："+prodInfo[0]+prod.getUnit())
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
		String path = SysparamCmp.get(SysparamDefine.RESOURCE_SAVE_PATH)+File.separator+fileName;
		ExcelUtil.generate(path, headers, datalist,18);
		
		//存储到采购表里,要把原数据清空
		/*new DeleteStatement(OrdersPurchase.class).execute();
		DBHelper.getDao().insertBatch(purchaseList);*/
		
		EMailInfo mailInfo = new EMailInfo(fileName,sb_email_final.toString());
		mailInfo.addTo(new EmailNotifyTarget("wuyj@xiesange.com"));
		mailInfo.addAttach(path,"采购订单-"+DateUtil.nowStr(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMM)+".xls");
		
		
		//发货单
		headers = ClassUtil.newList();
		headers.add(new ExcelHeaderCell("orderId","",220));
		headers.add(new ExcelHeaderCell("name","客户",220));
		headers.add(new ExcelHeaderCell("deliveryNo","行必达单号",220));
		headers.add(new ExcelHeaderCell("weight","重量",100));
		headers.add(new ExcelHeaderCell("fee","运费",150));
		
		datalist = ClassUtil.newList();
		
		for(PurchaseItem item : itemList){
			Orders order = (Orders)item.getAttr("order");
			datalist.add(new ExcelBodyRow()
						.addCell("orderId", order.getId())
						.addCell("name", order.getName()+"\r\n"+order.getMobile())
						//.addCell("deliveryNo", sb_purchase.toString())
						//.addCell("weight", sb_purchase.toString())
						//.addCell("fee", sb_purchase.toString())
			);
		}
		
		fileName = "发货单-"+DateUtil.now().getTime()+".xls";
		path = SysparamCmp.get(SysparamDefine.RESOURCE_SAVE_PATH)+File.separator+fileName;
		ExcelUtil.generate(path, headers, datalist,14);
		mailInfo.addAttach(path,"发货单-"+DateUtil.nowStr(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMM)+".xls");
		
		TaskManager.execute(new EmailNotifyTaskBean(mailInfo,false));
		
		
		//LogUtil.getLogger(OrderCmp.class).debug("delete file");
		//EMailUtil.sendHTML(mailInfo);
		//FileUtil.delFile(path);
	}
	
	/*public static void sendDeliveryEmail(List<PurchaseItem> itemList) throws Exception{
		List<ExcelHeaderCell> headers = ClassUtil.newList();
		headers.add(new ExcelHeaderCell("orderId","",220));
		headers.add(new ExcelHeaderCell("name","客户",220));
		headers.add(new ExcelHeaderCell("deliveryNo","行必达单号",220));
		headers.add(new ExcelHeaderCell("weight","重量",100));
		headers.add(new ExcelHeaderCell("fee","运费",150));
		
		List<ExcelBodyRow> datalist = ClassUtil.newList();
		
		for(PurchaseItem item : itemList){
			Orders order = (Orders)item.getAttr("order");
			datalist.add(new ExcelBodyRow()
						.addCell("orderId", order.getId())
						.addCell("name", order.getName())
						//.addCell("deliveryNo", sb_purchase.toString())
						//.addCell("weight", sb_purchase.toString())
						//.addCell("fee", sb_purchase.toString())
			);
		}
		
		String fileName = "发货单-"+DateUtil.now().getTime()+".xls";
		String path = SysparamCmp.get(SysparamDefine.RESOURCE_SAVE_PATH)+File.separator+fileName;
		ExcelUtil.generate(path, headers, datalist,14);
		

		EMailInfo mailInfo = new EMailInfo(fileName,"");
		mailInfo.addTo(new EmailNotifyTarget("wuyj@xiesange.com"));
		mailInfo.addAttach(path,"发货单-"+DateUtil.nowStr(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMM)+".xls");
		TaskManager.execute(new EmailNotifyTaskBean(mailInfo,false));
		
		
	}*/
}
