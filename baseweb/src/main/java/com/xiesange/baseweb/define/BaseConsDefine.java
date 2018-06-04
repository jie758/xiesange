package com.xiesange.baseweb.define;

import com.xiesange.core.enumdefine.IShortConsEnum;




/**
 * 定义常量值
 * @Description
 * @author wuyj
 * @Date 2013-11-4
 */
public class BaseConsDefine
{
	public static final String APP_NAME = "蟹三哥";
	
	//系统类型
	public static enum SYS_TYPE implements IShortConsEnum{
		XIESANGE{public short value(){return 1;}}
		;
	}
	
	//第三方基础数据类型，一般都是授权token，可以保存在服务端，避免每次去第三方获取
	public static enum VendorConfigType {
		wx_oauth_access_token,//oauth需要的token
		wx_access_token,//普通微信请求的token
		wx_jsapi_ticket,
		rong_token
	}

	
	//设备类型
	public static enum DEVICE implements IShortConsEnum{
		PC{public short value(){return 1;}},
		IPHONE{public short value(){return 2;}},
		IPAD{public short value(){return 3;}},
		ANDROID{public short value(){return 4;}}
		;
	}
	//操作渠道
	public static enum CHANNEL implements IShortConsEnum{
		FAKE{public short value(){return -1;}},//
		APP{public short value(){return 1;}},//APP
		MIS{public short value(){return 2;}},//MIS后台
		WEBSITE{public short value(){return 3;}},//官网
		WECHAT{public short value(){return 4;}}//微信渠道
		;
	}
	
	//数据状态
	public static enum STS implements IShortConsEnum{
		INVALID{public short value(){return 0;}},
		EFFECT{public short value(){return 1;}}
		;
	}
	
	//通用的状态
	public static enum STATUS implements IShortConsEnum{
		DELETED{public short value(){return -1;}},//删除
		INIT{public short value(){return 0;}},//初始状态/无状态
		APPROVING{public short value(){return 1;}},//审核中
		REJECTED{public short value(){return 2;}},//驳回
		EFFECTIVE{public short value(){return 99;}}//已生效
		;
	}
	
	
	//数据操作类型
	public static enum DB_OPER implements IShortConsEnum{
		UPDATE{public short value(){return 1;}},//修改
		REMOVE{public short value(){return 2;}}//删除
		;
	}
	
	//用户性别
	public static enum SEX implements IShortConsEnum{
		MALE{public short value(){return 1;}},//男
		FEMALE{public short value(){return 2;}},//女
		OTHER{public short value(){return 3;}}//其它
		;
	}
	
		
	//分表规则类型
	public static enum PARTITION_MODE implements IShortConsEnum{
		MOD{public short value(){return 1;}},//取模
		DATE{public short value(){return 2;}},//按日期年月
		FIX{public short value(){return 3;}}//固定值
		;
	}
		
	
	//通知渠道类型 
	public static enum NOTIFY_CHANNEL implements IShortConsEnum{
		SMS{public short value(){return 1;}},//短信
		EMAIL{public short value(){return 2;}},//email
		APP{public short value(){return 3;}},//app
		WECHAT{public short value(){return 4;}}//微信
		;
	}
	
	//订单支付渠道
	public static enum PAYCHANNEL implements IShortConsEnum{
		ALIPAY{public short value(){return 10;}},//支付宝
		ALIPAY_PC{public short value(){return 11;}},//支付宝PC支付
		WECHAT{public short value(){return 20;}},//微信
		WX_PUB{public short value(){return 21;}},//微信公众号扫码
		WX_PUB_QR{public short value(){return 22;}}//微信扫码
		;
	}
	
	
	//用户类型
	public static enum USER_ROLE implements IShortConsEnum{
		NORMAL{public short value(){return 1;}},//普通用户
		ADMIN{public short value(){return 99;}}//管理员
		;
	}
}
