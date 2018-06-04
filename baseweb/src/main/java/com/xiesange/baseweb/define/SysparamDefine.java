package com.xiesange.baseweb.define;

import com.xiesange.baseweb.component.SysparamCmp.ISysParam;

public enum SysparamDefine implements ISysParam {
	RESOURCE_SAVE_PATH,//资源文件包括图片、文件等的存放路径
	RESOURCE_URL_PATH,//资源文件的url访问路径
	SUPPORT_VERSION,//版本号，配置了版本号说明当前系统支持这些版本，多个版本号以逗号分隔
	RONG_APPID,//融云appid
	RONG_SECRET,//融云的私钥
	CUST_SERVICE_USER_IDS,//客服ID串
	SYS_MESSAGE_SENDER,
	TOKEN_VALIDITY,//token有效期(天)
	QINIU_ACCESS_TOKEN,//七牛AccessToken
	QINIU_ACCESS_SECRET,//七牛SecretToken
	ORDER_DEADLINE,//hh:mm格式，超过这个时间，那么订单要到后台才能发货
	ORDER_PURCHASE_EMAIL,//采购订单接收方邮件
	ORDER_SWITCH,//是否可下单总开关
	ORDER_EXPRESSFEE_RULE,//运费规则
	PINGPP_RSA_PRIVATE_KEY_PATH,//Ping+的RSA私钥证书路径
	PINGPP_RSA_PUBLIC_KEY_PATH,//Ping+的RSA公钥证书路径
	PINGPP_WEBHOOK_RSA_PUBLIC_KEY,//Ping+的webhook回调RSA公钥证书内容
	SHANGHUI_MEMBER
}
