package com.xiesange.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.baseweb.component.EnumCmp;
import com.xiesange.baseweb.define.EnumDefine;
import com.xiesange.baseweb.request.RequestBody;
import com.xiesange.baseweb.request.ResponseBody;
import com.xiesange.baseweb.service.AbstractService;
import com.xiesange.baseweb.service.ETServiceAnno;
import com.xiesange.baseweb.util.RequestUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.base.BaseEnum;
import com.xiesange.gen.dbentity.orders.OrdersComment;
import com.xiesange.gen.dbentity.orders.OrdersItem;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.product.ProductPic;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.summary.CountQueryField;
import com.xiesange.orm.statement.field.summary.IStatQueryField;
import com.xiesange.orm.statement.field.summary.SumQueryField;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.WebRequestContext;
import com.xiesange.web.component.OrderCmp;
import com.xiesange.web.component.ProdCmp;
import com.xiesange.web.component.PromotionCmp;
import com.xiesange.web.component.UserCmp;
import com.xiesange.web.define.ParamDefine;
@ETServiceAnno(name="product",version="")
/**
 * 产品服务类
 * @author Wilson 
 * @date 上午9:48:42
 */
public class ProductService extends AbstractService {
	/**
	 * 查询首页产品列表,涉及到权限控制、微信分享签名、再来一单的订单查询
	 * 因为首页是第一门户，必须要快，所以只能把上述功能都统一放在一起，一次性查询返回
	 * @param context
	 * 			oauth_code,
	 * 			order_id,
	 * 			
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryIndexList(WebRequestContext context) throws Exception{
		//RequestBody reqbody = context.getRequestBody();
		//Long orderId = reqbody.getLong(ParamDefine.Order.order_id);
		
		List<Product> indexProdList = dao().query(
				new QueryStatement(Product.class,new DBCondition(Product.JField.status,0,DBOperator.GREAT))
						.appendOrderField(Product.JField.typeId)
		);
		if(NullUtil.isEmpty(indexProdList)){
			return null;
		}
		
		Set<Long> prodIds = ETUtil.buildEntityIdList(indexProdList, Product.JField.id);
		
		//查询评论
		List<OrdersComment> commentStatList = dao().query(new QueryStatement(OrdersComment.class,
				new DBCondition(OrdersComment.JField.productId,prodIds,DBOperator.IN),
				new DBCondition(OrdersComment.JField.status,1)
			).appendGroupField(OrdersComment.JField.productId)
			 .appendQueryField(OrdersComment.JField.productId,CountQueryField.getInstance("count"))
		);
		
		//查询月销量
		Date now = DateUtil.now();
		Date firstDay = DateUtil.getDayBegin(DateUtil.getFirstDateOfMonth(now));
		Date nextMonthFirstDay = DateUtil.offsetMonth(firstDay, 1);
		List<OrdersItem> saleStatList = dao().query(new QueryStatement(OrdersItem.class,
				new DBCondition(OrdersItem.JField.productId,prodIds,DBOperator.IN),
				new DBCondition(OrdersItem.JField.createTime,firstDay,DBOperator.GREAT_EQUALS),
				new DBCondition(OrdersItem.JField.createTime,nextMonthFirstDay,DBOperator.LESS)
			).appendGroupField(OrdersComment.JField.productId)
			 .appendQueryField(OrdersComment.JField.productId,new SumQueryField(OrdersItem.JField.amount,"totalAmount"))
		);
		
		//查询团购促销规则
		List<Promotion> promList = PromotionCmp.queryPromotions();
		List<Promotion> globalPromList = ClassUtil.newList();
		if(NullUtil.isNotEmpty(promList)){
			for(Promotion prom : promList){
				if(prom.getProductId() == -1){
					ETUtil.clearDBEntityExtraAttr(prom);
					globalPromList.add(prom);
				}
				PromotionCmp.transfer(prom);
			}
		}
		
		for(Product prod : indexProdList){
			ProdCmp.transfer(prod,false);
			if(NullUtil.isNotEmpty(saleStatList)){
				for(OrdersItem saleStat : saleStatList){
					if(prod.getId().longValue() == saleStat.getProductId()){
						//月销量
						prod.addAttribute("totalAmount", ((BigDecimal)saleStat.getAttr("totalAmount")).longValue());
						break;
					}
				}
			}
			if(NullUtil.isNotEmpty(promList)){
				List<Promotion> prodPromList = ClassUtil.newList();
				for(Promotion prom : promList){
					if(prod.getId().longValue() == prom.getProductId()){
						prodPromList.add(prom);
					}
					ETUtil.clearDBEntityExtraAttr(prom);
				}
				if(NullUtil.isNotEmpty(prodPromList)){
					prod.addAttribute("promotionList", prodPromList);
				}
			}
			
			if(NullUtil.isNotEmpty(commentStatList)){
				for(OrdersComment comment : commentStatList){
					if(comment.getProductId().longValue() == prod.getId()){
						prod.addAttribute("commentCount", ((BigDecimal)comment.getAttr("count")).longValue());
						break;
					}
				}
				
			}
		}
		
		List<BaseEnum> prodTypeList = EnumCmp.getEnumTypeItems(EnumDefine.PROD_TYPE);
		ETUtil.clearDBEntityExtraAttr(prodTypeList);
		
		/*Orders orderEntity = null;
		if(orderId != null){
			orderEntity = OrderCmp.queryById(orderId, true);
			List<OrdersItem> items = (List<OrdersItem>)orderEntity.getAttr("items");
			for(OrdersItem item : items){
				OrderCmp.transferItem(item,false);
				ETUtil.clearDBEntityExtraAttr(item);
			}
			
			ETUtil.clearDBEntityExtraAttr(orderEntity);
		}*/
		
		return new ResponseBody("productList",indexProdList)
				.add("productTypeList", prodTypeList)
				//.add("order", orderEntity)
				.add("promotionList", NullUtil.isEmpty(globalPromList)?null:globalPromList)
				;
		
	}
	
	
	public ResponseBody queryDetail(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id
		);
		
		long prodId = reqbody.getLong(ParamDefine.Product.product_id);
		Product prodEntity = dao().queryById(Product.class, prodId);
		ProdCmp.transfer(prodEntity,false);
		
		List<ProductPic> picList = dao().query(ProductPic.class, new DBCondition(ProductPic.JField.productId,prodId));
		if(NullUtil.isNotEmpty(picList)){
			for(ProductPic pic : picList){
				ProdCmp.transferPic(pic);
				ETUtil.clearDBEntityExtraAttr(pic);
			}
		}
		
		//如果是套餐的话，查询出明细
		List<Product> subProdList = ProdCmp.queryProductItems(prodId);
		
		//查询评论
		long commentCount = dao().queryCount(OrdersComment.class,new DBCondition(OrdersComment.JField.productId,prodId));
		
		//查询月销量
		Date now = DateUtil.now();
		Date firstDay = DateUtil.getDayBegin(DateUtil.getFirstDateOfMonth(now));
		Date nextMonthFirstDay = DateUtil.offsetMonth(firstDay, 1);
		List<BigDecimal> totalAmounts = dao().queryStat(OrdersItem.class, new IStatQueryField[]{new SumQueryField(OrdersItem.JField.amount,"totalAmount")}, 
				new DBCondition(OrdersItem.JField.productId,prodId),
				new DBCondition(OrdersItem.JField.createTime,firstDay,DBOperator.GREAT_EQUALS),
				new DBCondition(OrdersItem.JField.createTime,nextMonthFirstDay,DBOperator.LESS)
		);
		
		prodEntity.addAttribute("commentCount", commentCount);
		if(NullUtil.isNotEmpty(totalAmounts) && totalAmounts.get(0) != null){
			prodEntity.addAttribute("totalAmount", totalAmounts.get(0).longValue());
		}
		
		List<Promotion> allPpromList = PromotionCmp.queryPromotions();
		List<Promotion> promList = ClassUtil.newList();
		if(NullUtil.isNotEmpty(allPpromList)){
			for(Promotion prom : allPpromList){
				if(prom.getProductId() == -1L || prom.getProductId() == prodId){
					promList.add(prom);
				}
				PromotionCmp.transfer(prom);
				ETUtil.clearDBEntityExtraAttr(prom);
			}
		}
		
		
		return new ResponseBody("product",prodEntity)
					.add("promotionList", promList)
					.add("productItems", subProdList)
					.add("picList", picList);
	}
	
	/**
	 * 查询某个产品的用户评价，分页
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryCommentList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id
		);
		
		long prodId = reqbody.getLong(ParamDefine.Product.product_id);
		//查询评论
		List<OrdersComment> commentList = dao().query(new QueryStatement(OrdersComment.class,
				new DBCondition(OrdersComment.JField.productId,prodId),
				new DBCondition(OrdersComment.JField.status,1)
			).appendOrderFieldDesc(OrdersComment.JField.stickIndex,OrdersComment.JField.createTime)
		);
		
		if(NullUtil.isNotEmpty(commentList)){
			Set<Long> userIds = ETUtil.buildEntityIdList(commentList, OrdersComment.JField.userId);
			List<User> userList = dao().queryByIds(User.class, userIds,User.JField.id,User.JField.mobile,User.JField.name,User.JField.pic);
			for(OrdersComment comment : commentList){
				OrderCmp.transferComment(comment);
				for(User u : userList){
					if(comment.getUserId() == u.getId().longValue()){
						UserCmp.transfer(u);
						u.setName(ETUtil.maskName(u.getName()));
						u.setMobile(ETUtil.maskMobile(u.getMobile()));
						comment.addAttribute("user", u);
						break;
					}
				}
			}
		}
		
		
		return new ResponseBody("commentList",commentList);
	}
	
	/**
	 * 查询某个产品的所有图片
	 * @param context
	 * 			product_id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryPicList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(context.getRequestBody(),
			ParamDefine.Product.product_id
		);
		
		long prodId = reqbody.getLong(ParamDefine.Product.product_id);
		List<ProductPic> picList = dao().query(ProductPic.class, new DBCondition(ProductPic.JField.productId,prodId));
		if(NullUtil.isNotEmpty(picList)){
			for(ProductPic pic : picList){
				ProdCmp.transferPic(pic);
				ETUtil.clearDBEntityExtraAttr(pic);
			}
		}
		return new ResponseBody("picList",picList);
		
	} 
	
	/**
	 *  查询产品列表
	 * @param context
	 * 			product_ids,逗号分隔，查询指定id的产品
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryList(WebRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		String prodIdsStr = reqbody.getString(ParamDefine.Product.product_ids);
		List<Product> prodList = null;
		if(NullUtil.isEmpty(prodIdsStr)){
			prodList = dao().query(new QueryStatement(Product.class, 
					new DBCondition(Product.JField.status,0,DBOperator.GREAT))
				.appendQueryField(Product.JField.id,
					Product.JField.name,
					Product.JField.origPrice,
					Product.JField.price,
					Product.JField.spec,
					Product.JField.unit,
					Product.JField.premise,
					Product.JField.pic)
			);
		}else{
			List<Long> prodIds = ETUtil.trans2LongArray(prodIdsStr);
			prodList = dao().queryByIds(Product.class, prodIds, 
					Product.JField.id,
					Product.JField.name,
					Product.JField.origPrice,
					Product.JField.price,
					Product.JField.spec,
					Product.JField.unit,
					Product.JField.premise,
					Product.JField.pic);
		}
		
		if(NullUtil.isEmpty(prodList)){
			return null;
		}
		for(Product prod : prodList){
			ProdCmp.transfer(prod, false);
			ETUtil.clearDBEntityExtraAttr(prod);
		}
		return new ResponseBody("productList",prodList);
	}
	
}
