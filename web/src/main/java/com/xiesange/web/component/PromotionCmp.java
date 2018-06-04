package com.xiesange.web.component;

import java.util.List;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.promotion.Promotion;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.web.define.ConsDefine;
import com.xiesange.web.promotion.AbstractPromotionBean;
import com.xiesange.web.promotion.PromotionManager;

/**
 * 促销组件
 * @author Think
 *
 */
public class PromotionCmp {
	/*public static List<Promotion> applyPromotion(List<Orders> orders,Short[] types) throws Exception{
		if(NullUtil.isEmpty(orders)){
			return null;
		}
		List<Promotion> promList = DBHelper.getDao().query(Promotion.class, new DBCondition(Promotion.JField.type,types,DBOperator.IN));
		if(NullUtil.isEmpty(promList)){
			return null;
		}
		List<Promotion> matchedPromList = ClassUtil.newList();
		for(Promotion prom : promList){
			AbstractPromotionBean promBean = PromotionManager.getPromotionBean(prom);
			if(promBean.apply(orders)){
				matchedPromList.add(prom);
			}
			prom.addAttribute("text", promBean.buildText());
		}
		return matchedPromList;
	}*/
	public static AbstractPromotionBean applyPromotion(List<Orders> orders,long promotionId) throws Exception{
		Promotion prom = DBHelper.getDao().queryById(Promotion.class, promotionId);
		return applyPromotion(orders,prom);
	}
	
	public static AbstractPromotionBean applyPromotion(Object value,Promotion prom) throws Exception{
		AbstractPromotionBean promBean = PromotionManager.getPromotionBean(prom);
		if(promBean.isMatched(value)){
			promBean.apply(value);
		}
		return promBean;
	}
	
	
	/**
	 * 查询团购促销规则
	 * @return
	 * @throws Exception
	 */
	/*public static List<Promotion> queryGroupbuyPromotions(ConsDefine.PROMOTION_TYPE promotionType,Long productId,boolean needTransfer) throws Exception{
		List<Promotion> promList = DBHelper.getDao().query(Promotion.class,
				new DBCondition(Promotion.JField.type,promotionType.value()),
				productId==null?null:new DBCondition(Promotion.JField.productId,productId),
				//new DBCondition(Promotion.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS),
				new DBCondition(Promotion.JField.status,1));
		if(NullUtil.isEmpty(promList))
			return null;
		if(needTransfer){
			for(Promotion prom : promList){
				prom.addAttribute("text", PromotionManager.getPromotionBean(prom).buildText());
				transfer(prom);
			}
		}
		return promList;
	}*/
	
	public static Promotion queryGroupbuyPromotion(ConsDefine.PROMOTION_TYPE promotionType,Long productId,boolean needTransfer) throws Exception{
		Promotion prom = DBHelper.getDao().querySingle(Promotion.class,
				new DBCondition(Promotion.JField.type,promotionType.value()),
				productId==null?null:new DBCondition(Promotion.JField.productId,productId),
				//new DBCondition(Promotion.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS),
				new DBCondition(Promotion.JField.status,1));
		if(prom == null)
			return null;
		if(needTransfer){
			prom.addAttribute("text", PromotionManager.getPromotionBean(prom).buildText());
			transfer(prom);
		}
		return prom;
	}
	
	public static List<Promotion> queryGlobalPromotions() throws Exception{
		return queryPromotions(-1L);
	}
	
	public static List<Promotion> queryPromotions() throws Exception{
		return queryPromotions(null);
	}
	
	public static List<Promotion> queryPromotions(Long productId) throws Exception{
		List<Promotion> promList = DBHelper.getDao().query(Promotion.class,
				productId==null?null:new DBCondition(Promotion.JField.productId,productId),
				//new DBCondition(Promotion.JField.expireTime,DateUtil.now(),DBOperator.GREAT_EQUALS),
				new DBCondition(Promotion.JField.status,1));
		if(NullUtil.isEmpty(promList))
			return null;
		return promList;
	}
	
	
	public static void transfer(Promotion prom){
		if(prom.getConditionSum() != null){
			prom.addAttribute(Promotion.JField.conditionSum.name(),ETUtil.parseFen2Yuan(prom.getConditionSum()));
			prom.setConditionSum(null);
		}
		String value = prom.getValue();
		if(NullUtil.isEmpty(value)){
			return;
		}
		
		if(prom.getAttr("text") == null){
			String text = PromotionManager.getPromotionBean(prom).buildText();
			prom.addAttribute("text", text);
		}
		short actionType = prom.getActionType();
		if(actionType == ConsDefine.PROMOTION_ACTION.FIXPRICE.value()
				|| actionType == ConsDefine.PROMOTION_ACTION.REDUCE.value()){
			prom.setValue(ETUtil.parseFen2YuanStr(Long.valueOf(prom.getValue()),false).toString());
		}
	}
	
	public static String buildActionText(Promotion prom){
		short action = prom.getActionType();
		String value = prom.getValue();
		if(action == ConsDefine.PROMOTION_ACTION.MORE.value()){
			String val = ETUtil.parseFen2YuanStr(Long.valueOf(value), false);
			return "立送"+val+"元优惠券 ";
		}
		
		if(action == ConsDefine.PROMOTION_ACTION.REDUCE.value()){
			String val = ETUtil.parseFen2YuanStr(Long.valueOf(value), false);
			return "立减"+val+"元 ";
		}
		
		if(action == ConsDefine.PROMOTION_ACTION.DISCOUNT.value()){
			int discount = Integer.valueOf(value);
			return "立享"+discount+"折 ";
		}
		
		if(action == ConsDefine.PROMOTION_ACTION.FIXPRICE.value()){
			String val = ETUtil.parseFen2YuanStr(Long.valueOf(value), false);
			return "享受特价"+val+"元/斤";
		}
		
		return "";
	}
	
	public static boolean isGroupProm(short promType){
		return promType == ConsDefine.PROMOTION_TYPE.GROUP.value()
				|| promType == ConsDefine.PROMOTION_TYPE.TOGETHER.value();
	}
}
