package com.elsetravel.mis.define;

import com.elsetravel.core.IParamEnum;



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
		entity_list,
		entity,
		user_id,
		mobile,
		name,
		code,
		memo,
		total_count,
		status
	}
	
	public static enum BaseData implements IParamEnum{
		country_flag,
		sysparam_flag,
		enum_flag,
	}
	public static enum Area implements IParamEnum{
		country_code,
		province_code,
		city_code
	}
	public static enum Enum implements IParamEnum{
		type_id,
		item_id,
		type_code,
		item_code,
		value
	}
	
	public static enum Login implements IParamEnum{
		account,
		password
	}
	
	public static enum Menu implements IParamEnum{
		menu_id,
		parent_id,
		name,
		url,
		is_leaf,
		memo
	}
	
	public static enum Staff implements IParamEnum{
		staff_id,
		name,
		sex_name,
		profile_pic,
		birthday,
		mobile,
		email
	}
	
	public static enum Config implements IParamEnum{
		config_id,
		config_ids,
		code,
		value,
		type
	}
	public static enum User implements IParamEnum{
		user_id,
		nickname,
		email,
		mobile,
		profile_pic,
		country_code,
		city_code,
		type
	}
	
	public static enum Person implements IParamEnum{
		sex_name,
		sex,
		guider_id,
		is_approved,
		languages,
		birthday,
		occupation,
		is_guider,
		tags
	}
	
	public static enum GuiderWithdrawApply implements IParamEnum{
		withdraw_id,
		is_approved
	}
	
	public static enum Order implements IParamEnum{
		order_id,
		code,
		transaction_code,
		visitor_id
	}
	public static enum Ticket implements IParamEnum{
		ticket_main_id,
		ticket_id,
		summary,
		pic,
		price,
		cost_time,//TODO 3.0后废弃
		trip_cost,
		attr_tags,
		tags,
		intro_list,
		catalog_id,
		available_time_list,
		disable_date_list,
		guider_id,
		country_code,
		city_code,
		latitude,//纬度
		longtitude,//经度
		place_name,
		can_join,
		times,
		dates,
		expense_intro,
		detail_intro,
		other_intro
	}
	public static enum Topic implements IParamEnum{
		topic_id,
		summary,
		intro_list,
		pic,
		detail_pic,//TODO已改成top_pic了
		top_pic,
		bottom_pic,
		comment_id,
		country_code,
		city_code,
		latitude,//纬度
		longtitude,//经度
		place_name,
	}
	public static enum Approve implements IParamEnum{
		is_approved,
		action,
		reason
	}
	public static enum Tag implements IParamEnum{
		tag_id,
		type
	}
	public static enum TicketCatalog implements IParamEnum{
		ticket_catalog_id,
		pic,
		name
	}
	public static enum MainCatalog implements IParamEnum{
		main_catalog_id,
		pic,
		name,
		country_code,
		title,
		summary
	}
	public static enum Medal implements IParamEnum{
		medal_id,
		pic,
		group_code
	}
	public static enum Banner implements IParamEnum{
		banner_id,
		pic,
		target_type,
		target_value
	}
	public static enum RewardRule implements IParamEnum{
		reward_rule_id,
		action,
		trigger_value,
		trigger_period_type,
		trigger_target_id,
		reward_type,
		reward_value
	}
	public static enum HomePage implements IParamEnum{
		homepage_id,
		pic
	}
	public static enum QuickMatch implements IParamEnum{
		type,
		field,
		value
	}
	
	public static enum Feedback implements IParamEnum{
		feedback_id
	}
	
	public static enum Notify implements IParamEnum{
		template_id,
		target_user_ids,
		target_emails,
		title,
		content,
		channel
	}
	public static enum Article implements IParamEnum{
		article_id,
		content,
		is_system
	}
	public static enum Message implements IParamEnum{
		date
	}
	
	public static enum Merchant implements IParamEnum{
		merchant_id,
		identity_type,
		identity_pic,
		phone,
		linkman,
		website,
		website2
	}
	
	public static enum Qiniu implements IParamEnum{
		keys
	}
	public static enum Sharing implements IParamEnum{
		sharing_id,
		sharing_ids,
		start_time,
		detail_intro,
		cover_pic,
		discussion_id,
		host_staff_id
	}
}
