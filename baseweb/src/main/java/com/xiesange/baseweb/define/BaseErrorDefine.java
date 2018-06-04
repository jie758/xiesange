package com.xiesange.baseweb.define;


/**
 * 定义错误信息
 * @author Think
 *
 */
public enum BaseErrorDefine implements IErrorCodeEnum{
	SYS_ERROR(10000,"访问量太大，请稍后再试试吧~"),
	SYS_AUTHLIMIT(100000,"您需要先登录哦~"),//特殊错误码，前端会使用，修改了需要告知前端
	SYS_METHOD_NOT_FOUND(100001,"找不到请求对应的方法"),
	SYS_SERVICE_NOT_FOUND(100002,"找不到请求对应的服务"),
	SYS_OPEARTION_INVALID(100003,"不合法的操作"),
	SYS_REQUESET_INVALID(100004,"无效的访问 : {0}"),
	SYS_PARAM_ISNULL(100005,"请求参数不能为空 : {0}"),
	SYS_VCODE_INVALID(100006,"验证码无效"),
	SYS_PARAM_INVALID(100007,"参数不合法 : {0}"),
	SYS_VERSION_NOTSUPPORT(100008,"您的当前版本过低，请更新到最新版本"),//特殊错误码，前端会使用，修改了需要告知前端
	SYS_NOT_ALLOWED(100009,"您没有权限进行该项操作"),
	SYS_URL_INVALID(100010,"链接已失效"),
	INVALID_COUNTRY_CODE(100011,null),
	INVALID_CITY_CODE(100012,null),
	SYS_INVALID_SIGNATURE(100013,null),
	SYS_OPEN_FROM_WX(100014,"请从微信界面中打开"),
	SYS_IM_ERROR(100015,"通信交互出错 : {0}")
	;
	
	
	private String message;
	private long code;
	
	private BaseErrorDefine(long code,String message){
		this.code = code;
		this.message = message;
	}

	/*public String getMessage() {
		return message;
	}*/

	public long getCode() {
		return code;
	}
}
