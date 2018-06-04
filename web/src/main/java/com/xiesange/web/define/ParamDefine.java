package com.xiesange.web.define;

import com.xiesange.core.enumdefine.IParamEnum;



/**
 * 定义请求参数枚举，业务侧使用RequestBody.getXXX(ParamDefine.Common.xxx)方式来获取
 * @Description
 * @author wuyj
 * @Date 2013-11-4
 */
public class ParamDefine
{
	public static enum Common implements IParamEnum{
		header,
		body,
		xml,
		entity_list,
		entity,
		mobile,
		name,
		code,
		memo,
		vcode,
		total_count,
		url,
		status
	}
	
	public static enum Regist implements IParamEnum{
		vcode,
		nickname,
		password,
		mobile,
		zone,
		pic
	}
	public static enum Login implements IParamEnum{
		account,
		password,
		skey,
		wechat,
		qq,
		weibo
	}
	
	
	public static enum User implements IParamEnum{
		user_id,
		wechat,
		sex,
		mobile,
		birthday,
		pic,
		email,
		city_code,
		orderby_order_count,
		orderby_order_sum,
		address,
		delivery_no
	}
	
	
	public static enum Product implements IParamEnum{
		product_id,
		product_ids,
		name,
		summary,
		spec,
		price,
		cost_price,
		status
	}
	public static enum Order implements IParamEnum{
		order_id,
		only_self,
		buy_type,
		amount,
		sum,
		pay_channel,
		address,
		items,
		status,
		coupon_id,
		need_promotion_list,
		need_bargain,
		need_notify,
		comment_items,
		is_adust_price,
		express_sum,
		express_cost,
		cost
		
	}
	public static enum Purchase implements IParamEnum{
		order_ids,
		purchase_id,
		express_no,
		express_info,
		express_notify,
		express_balance,
		purchase_price,
		express_fee
	}
	
	public static enum Message implements IParamEnum{
		from_user_id,
		target_user_id,
		title,
		message,
		is_passed,
		zone
	}
	
	public static enum Cache implements IParamEnum{
		baseparam,
		enums
	}
	
	
	public static enum Wechat implements IParamEnum{
		url,
		wx_signature_url,
		access_token,
		oauth_code,
		oauth_token,
		openid,
		need_user,
		need_create_new
	}
	
	public static enum VCode implements IParamEnum{
		type,
		need_profile
	}
	
	public static enum Qiniu implements IParamEnum{
		key,
		keys,
		need_common
	}
	
	public static enum Coupon implements IParamEnum{
		redeem_code,
		coupon_id,
		coupon_type,
		coupon_value,
		validity,
		expire_date
	}
	
	public static enum Groupbuy implements IParamEnum{
		groupbuy_id,
		groupbuy_order_id,
		promotion_id,
		intro,
		product_info,
		need_payed,
		need_unpay
	}
	public static enum Article implements IParamEnum{
		article_id,
		article_code,
		content,
		share_message_title,
		share_message_desc,
		share_timeline_desc,
		share_pic,
		record_view
	}
	public static enum Activity implements IParamEnum{
		activity_code
	}
	public static enum Shanghui implements IParamEnum{
		items,
		shanghui_id,
		member,
		members,
		price,
		grade,
		round_id,
		sum,
		start_date,
		end_date,
		mj_type,
		is_online,
		scores,
		group_by,
		is_order_by_date_desc
	}
	public static enum Qingyuan implements IParamEnum{
		code,
		building,
		room,
		unit,
		signature
	}
}
