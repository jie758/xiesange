package com.xiesange.web.component;

import java.util.List;
import java.util.Set;

import com.xiesange.baseweb.ETUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.orders.Orders;
import com.xiesange.gen.dbentity.product.Product;
import com.xiesange.gen.dbentity.product.ProductItem;
import com.xiesange.gen.dbentity.product.ProductPic;
import com.xiesange.orm.DBHelper;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.statement.query.QueryStatement;

public class ProdCmp {
	public static void transfer(Product product,boolean needCostPrice) throws Exception{
		//照片
		transfer(product,needCostPrice,null);
	}
	public static void transfer(Product product,boolean needCostPrice,Integer size) throws Exception{
		//照片
		if(NullUtil.isNotEmpty(product.getPic())){
			String url = ETUtil.buildPicUrl(product.getPic());
			if(size != null){
				url += "?imageMogr2/thumbnail/"+size;
			}
			product.setPic(url);
		}
		if(product.getPrice() != null){
			product.addAttribute(Product.JField.price.getName(), ETUtil.parseFen2Yuan(product.getPrice()));
			product.setPrice(null);
		}
		if(product.getOrigPrice() != null){
			product.addAttribute(Product.JField.origPrice.getName(), ETUtil.parseFen2Yuan(product.getOrigPrice()));
			product.setOrigPrice(null);
		}
		if(needCostPrice == false){
			product.setCostPrice(null);
		}else{
			product.addAttribute(Product.JField.costPrice.getName(), ETUtil.parseFen2Yuan(product.getCostPrice()));
			product.setCostPrice(null);
		}
	}
	
	public static void transferPic(ProductPic productPic) throws Exception{
		if(NullUtil.isNotEmpty(productPic.getPic())){
			String url = ETUtil.buildPicUrl(productPic.getPic());
			productPic.setPic(url);
		}
	}
	
	public static Product queryProduct(long prodId,BaseJField... jfs) throws Exception{
		return DBHelper.getDao().queryById(Product.class, prodId,jfs);
		//return CacheManager.getCacheHouse(Product.class).getById(prodId);
	}
	public static List<Product> getProductByIds(Set<Long> prodIds,BaseJField...jfs) throws Exception{
		if(NullUtil.isEmpty(prodIds))
			return null;
		QueryStatement st = new QueryStatement(Product.class,new DBCondition(Product.JField.id,prodIds,DBOperator.IN));
		if(NullUtil.isNotEmpty(jfs)){
			st.appendQueryField(jfs);
		}
		return DBHelper.getDao().query(st);
		
		//return DBHelper.getDao().query.queryByIds(Product.class, prodIds, jfs);
		/*return DBHelper.getDao().query(Product.class,
				new DBCondition(Product.JField.id,prodIds,DBOperator.IN));
		*/
		/*return CacheManager.getCacheHouse(Product.class).getList(jfs,
					new DBCondition(Product.JField.id,prodIds,DBOperator.IN));*/
	}
	
	public static void appendPics(Product prodEntity) throws Exception{
		List<ProductPic> picList = DBHelper.getDao().query(ProductPic.class, new DBCondition(ProductPic.JField.productId,prodEntity.getId()));
		if(NullUtil.isNotEmpty(picList)){
			for(ProductPic picEntity : picList){
				transferPic(picEntity);
				ETUtil.clearDBEntityExtraAttr(picEntity);
			}
			prodEntity.addAttribute("picList", picList); 
		}
		
	}
	
	public static List<Product> queryProductItems(long productId) throws Exception{
		List<ProductItem> prodItems = DBHelper.getDao().query(ProductItem.class, new DBCondition(ProductItem.JField.productId,productId));
		Set<Long> subProdIds = ETUtil.buildEntityIdList(prodItems, ProductItem.JField.itemId);
		if(NullUtil.isEmpty(subProdIds)){
			return null;
		}
		List<Product> subProdList = DBHelper.getDao().queryByIds(Product.class, subProdIds);
		if(NullUtil.isEmpty(subProdList)){
			return null;
		}
		for(Product prod : subProdList){
			for(ProductItem item : prodItems){
				if(item.getItemId().longValue() == prod.getId()){
					prod.addAttribute("amount", item.getAmount());
					subProdList.remove(item);
					break;
				}
			}
			prod.setCommentTags(null);
			ProdCmp.transfer(prod,false);
			ETUtil.clearDBEntityExtraAttr(prod);
		}
		return subProdList;
	}
}
