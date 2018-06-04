window.onerror = function(sMessage, sUrl, sLine) {
	var err = sUrl + "(第" + sLine + "行):<br/>" + sMessage;
	if (sMessage.indexOf("WeixinJSBridge") > -1) {
		return;
	}
	if (sMessage.indexOf("_WXJS") > -1) {
		return;
	}
	//console.log(err);
	err = "出错了~~~这个就很尴尬了~~"+err;
	typeof message == "undefined" ? alert(err) : message.alert(err);
};
var common = new function() {
	var mobile_reg = /^0?1[3|4|5|8|7][0-9]\d{8}$/;
	var __access_token;
	var __skey;
	this.checkMobile = function(mobile) {
		return mobile_reg.test(mobile);
	}
	this.session = function(key,val){
		if(sessionStorage == undefined){
			return;
		}
		if(arguments.length == 1){
			return sessionStorage[key];//表示获取
		}else{
			try{
				sessionStorage[key] = val;//表示存储 
			}catch(e){
				
			}
		}
	}
	
	this.setAuth = function(token,skey,loginUser) {
		if(token !== false){
			this.session("__access_token",token);
		}
		if(skey !== false){
			this.session("__skey",skey);
		}
		if(loginUser !== false){
			this.session("__user",JSON.stringify(loginUser));
		}
		//sessionStorage.__access_token = token;
		//sessionStorage.__skey = skey
	}
	this.getToken = function() {
		return this.session("__access_token");//__access_token;
	}
	this.getSkey = function() {
		return this.session("__skey");//__skey;
	}
	this.getLoginUser = function() {
		var json = this.session("__user");//
		return !json ? null : JSON.parse(json);
	}
	this.pageBack = function(index){
		history.go(index || -1);
	};
	this.importJs = function(jsPath, callback) {
		var head = document.getElementsByTagName('head')[0];
		var script = document.createElement('script');
		script.type = 'text/javascript';
		if(jsPath.indexOf("http") != 0 && jsPath.indexOf("https") != 0){
			jsPath = common.getHost()+"/"+jsPath;
		}
		script.src = jsPath;
		script.onerror = function() {
			callback && callback();
		};
		script.onload = function() {
			callback && callback();
		};
		head.appendChild(script);
	};
	
	this.importCss = function(cssPath) {
		var head = document.getElementsByTagName('head')[0];
        var link = document.createElement('link');
        link.href = cssPath;
        link.rel = 'stylesheet';
        link.type = 'text/css';
        head.appendChild(link);

	}
	// 跳转到页面
	this.gotoPage = function(url,delay) {
		if(delay > 0){
			setTimeout(function(){
				top.window.location.href = url;
			},delay);
		}else{
			top.window.location.href = url;
		}
		
	}

	// 本地存储
	this.local = function(key, value) {
		if (arguments.length == 1) {
			// 取值
			return window.localStorage.getItem(key);
		} else if (arguments.length == 2) {
			// 赋值
			window.localStorage.setItem(key, value);
		}
	}

	this.trim = function(text) {
		if(!text)
			return text;
		return text.replace(/(^\s*)|(\s*$)/g, "");
	}

	/**
	 * 判断一个对象是否为空，即没有任何属性（内置属性除外）
	 * 
	 * @param obj
	 */
	this.isEmpty = function(obj) {
		for ( var key in obj) {
			return false;
		}
		return true;
	}

	/**
	 * 返回一个基于当前时间的毫秒数构建而成的唯一值
	 * 
	 * @param prefix,需要加的前缀，会加到时间毫秒数后面
	 * @param suffix,需要加的后缀，会加到时间毫秒的后面
	 * @returns 假设当前时间的毫秒数是12345678 unique();//则返回:12345678
	 *          unique("aa_");//则返回:aa_12345678
	 *          unique(null,"_aa");//则返回:12345678_aa
	 *          unique("aa_","_bb");//则返回：aa_12345678_bb
	 */
	this.unique = function(prefix, suffix) {
		var result = [];
		var time = (new Date()).getTime();
		if (prefix)
			result.push(prefix);
		result.push(time);
		if (suffix)
			result.push(suffix);
		return result.join("");
	}

	this.buildUrlParam = function(bodyParam, ignoreToken) {
		var url = "";
		if (bodyParam) {
			for ( var key in bodyParam) {
				if (url.length > 0) {
					url += '&';
				}
				var val = bodyParam[key];
				url += key + "=" + val;
			}
		}
		/*
		 * if(ignoreToken !== false){ if(url.length > 0){ url += '&'; } url +=
		 * "token="+__accessToken; }
		 */
		return url;
	}

	this.calcHeight = function(width, origWidth, origHeight) {
		return origHeight * width / origWidth;
	}

	this.createImage = function(imageData, callback) {
		var image = new Image();
		image.onload = function() {
			var origWidth = image.width;
			var origHeight = image.height;

			callback && callback.apply(null, [ image, origWidth, origHeight ]);
		};
		image.src = imageData;
	}

	this.getDeviceType = function() {
		var userAgent = navigator.userAgent.toLowerCase();
		if (userAgent.indexOf("ipad") > -1) {
			return "ipad";
		} else if (userAgent.indexOf("iphone") > -1) {
			return "iphone";
		} else if (userAgent.indexOf("android") > -1) {
			return "android";
		} else {
			return "pc";
		}
		;
	};

	// 判断是否PC
	this.isPC = function() {
		var userAgentInfo = navigator.userAgent;
		var Agents = [ "Android", "iPhone", "SymbianOS", "Windows Phone",
				"iPad", "iPod" ];
		var flag = true;
		for (var v = 0; v < Agents.length; v++) {
			if (userAgentInfo.indexOf(Agents[v]) > 0) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	// 是否微信端
	this.isWechat = function() {
		var ua = window.navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) == 'micromessenger') {
			return true;
		} else {
			return false;
		}
	}
	// 是否从其乐app里载入的界面
	this.isFromApp = function() {
		var params = common.getUrlParams();
		if (params && params.agent_type
				&& params.agent_type.indexOf("app") == 0) {
			return true;
		} else {
			return false;
		}
	}
	this.getHost = function() {
		var url = window.location.href;
		if(url.indexOf("file:") == 0){
			return "http://localhost:8080/";
		}else{
			var port = window.location.port;
			return "http://" + window.location.hostname + (port ? ":" + port : "");
		}
	}
	
	this.getUrlContext = function(){
		var pathname = window.location.pathname;
		pathname = pathname.substring(pathname.indexOf('/')+1);
		return pathname.substring(0,pathname.indexOf('/'));
	};
	
	// 获取url中的参数,以对象方式返回。如果指定了paramName，那么只返回该参数值
	this.getUrlParams = function(paramName) {
		var url = window.location.href;
		url = url.split("?");
		if (url.length == 1)
			return paramName ? null : {};
		var strParamArr = url[1].split("&");
		var result = {};
		for (var i = 0; i < strParamArr.length; i++) {
			var strParam = strParamArr[i];
			var args = strParam.split("=");

			if (paramName && paramName == args[0]) {
				return args[1];
			}
			result[args[0]] = args[1];
		}
		return paramName ? "" : result;
	}
	// 获取url中的参数,以字符串方式返回，即?之后的所有字符串
	this.getUrlParamStr = function() {
		var url = window.location.href;
		url = url.split("?");
		return url.length == 1 ? null : url[1];
	}
	// rgb格式转成16进制返回，包含前缀#
	this.rgbTo16 = function(rgbStr) {
		// rgbStr格式为:rbg(xx,xx,xx)
		if (rgbStr.indexOf("rgb") == -1)
			return rgbStr;
		var colors = rgbStr.replace(/(?:||rgb|RGB)*/g, "")
				.replace(/\(|\)/g, "").split(",");
		var r = Number(colors[0]).toString(16);
		r = r.length == 1 ? 0 + r : r;// 不足两位前面补0；
		var g = Number(colors[1]).toString(16);
		g = g.length == 1 ? 0 + g : g;// 不足两位前面补0；
		var b = Number(colors[2]).toString(16);
		b = b.length == 1 ? 0 + b : b;// 不足两位前面补0；
		return "#" + r + g + b;
	}

	/**
	 * randomWord 产生任意长度随机字母数字组合
	 * randomFlag-是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
	 */
	this.randomWord = function(randomFlag,min,max) {
		var str = "",
			range = min,
			arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

		// 随机产生
		if(randomFlag){
			range = Math.round(Math.random() * (max-min)) + min;
		}
		for(var i=0; i<range; i++){
			var pos = Math.round(Math.random() * (arr.length-1));
			str += arr[pos];
		}
		return str;
	}
	
	/**
	 * 从一个列表中匹配到对应的元素
	 * @param value,匹配值
	 * @param matchList,需要匹配的源数据列表
	 * @param matchIdOfListEntity,string,需要匹配的数据列表中的字段名称
	 */
	this.matchItem = function(value,matchList,matchFieldOfListEntity){
		if(matchList==null || matchList.length == 0){
			return null;
		}
		var length = matchList.length;
		for(var i=0;i<length;i++){
			if(matchList[i][matchFieldOfListEntity] == value){
				return matchList[i];
			}
		}
		return null;
	}
}

var ajax = new function() {
	/**
	 * 向服务端发送数据
	 * 
	 * @param options,是个对象，有以下属性
	 *            url:请求的url params,object，存放具体的业务请求参数，比如登录的时候需要传账号和密码：
	 *            async : 是否异步
	 *            params:{account:bill,password:123}
	 *            success:function(response_header,response_body),请求成功后的回调函数
	 *            error:function,处理失败(error_code!=0)时的回调函数
	 *            need_progressbar,boolean,是否需要显示"正在处理"提示框，true/false表示是否需要显示，如果是字符串则显示进度框，且显示的文字就是该本文
	 * 
	 * 
	 */
	this.request = function(options) {
		options = options || {};
		var needProgressbar = options && options.need_progressbar;
		var $progressDialog = null;
		if(needProgressbar != null && typeof needProgressbar == 'object'){
			$progressDialog = message.progress2(needProgressbar);
		}else if(typeof needProgressbar == 'function'){
			$progressDialog = needProgressbar.apply();
		}else{
			$progressDialog = needProgressbar === false ? null : message.progress();
		}
		
		var params = options.params || {};
		params = $.extend(params, buildHeader(params.token));
		
		var needOAuth = params.need_oauth;
		var needWxSignature = params.need_wx_signature;
		var basedata = params.basedata;
		
		if(needOAuth){
			delete params.need_oauth;
			params.wx_oauth_code = common.getUrlParams("code");
			if (!params.wx_oauth_code && common.getToken()==null) {
				message.alert("请从微信界面里打开");
				return null;
			}
		}
		if(needWxSignature && common.isWechat()){
			delete params.need_wx_signature;
			params.wx_signature_url = window.location.href;
		}
		if(basedata){
			var basedata = params.basedata;
			delete params.basedata;//涉及到签名，所以无关字段都要删除
			params.enum_flag = (basedata===true || basedata==='enum') ? (getEnumFlag() || 0):-1,
			params.baseparam_flag = (basedata===true || basedata=='baseparam') ? (getSysparamFlag()||0):-1;
		}
		
		params.sign = md5Params(params);
		$.ajax({
			dataType : "json",
			async : (options.async == false) ? false : true,
			type : 'POST',
			url : options.url,
			data : params,
			error : function(xmlHttpRequest, textStatus, errorThrown) {
				if (options.error) {
					options.error.apply(null, [xmlHttpRequest, textStatus,errorThrown ]);
				}else if(needProgressbar != null && typeof needProgressbar == 'object'){
					message.errorHide("服务操作失败:" + options.url,needProgressbar);
				}else{
					message.alert ? message.alert("服务操作失败:" + options.url)
							: alert("服务操作失败" + options.url);
				}
			},
			success : function(responseData, textStatus, jqXHR) {
				try {
					var resp_body = responseData.body || {};
					var resp_header = responseData.header || {};

					var success = options.success;
					var error = options.error;

					// 需要对应答体内容做一下包装，以便增加一些便捷的API，具体参见wrapData方法
					if (resp_header.error_code == 0) {
						if (success != null){
							dealCommonResponse(resp_header,resp_body,needOAuth,basedata);
							success.apply(this, [ resp_header, resp_body,textStatus, jqXHR ]);
						}
							
					} else {
						var result = true;
						if (error != null) {
							result = error.apply(this, [resp_header,resp_body, textStatus, jqXHR ]);
						}
						if (result === false)
							return;
						
						if(needProgressbar != null && typeof needProgressbar == 'object'){
							message.errorHide(resp_header.error_message,needProgressbar);
						}else{
							message.alert ? message.alert(resp_header.error_message) 
									: alert("操作失败:"+ resp_header.error_message);
						}
								
					}
				} catch (exception) {
					$progressDialog && message.hideDialog($progressDialog);
					throw exception;
				}
			},
			complete : function(data, textStatus) {
				var complete = options && options.complete;
				if (complete != null){
					var result = complete.apply(this, [data,textStatus]);
					if(result === false){
						return;
					}
				}
				$progressDialog && message.hideDialog($progressDialog);
			}
		});
	}

	/**
	 * 向服务端上传数据，一般用于文件、图片等二进制流的上传
	 * 
	 * @param
	 * options,参数里的属性同request方法，只是属性里可以支持文件流的blob值，这里会采用formData的方式来上传各个属性值
	 */
	this.upload = function(options) {
		options = options || {};
		var needProgressbar = options && options.need_progressbar;
		var $progressDialog = needProgressbar == false ? null : message
				.progress(needProgressbar != true ? needProgressbar : null);

		var formData = new FormData();
		var params = options.params || {};
		params = $.extend(params, buildHeader(params.token));
		params.sign = md5Params(params);
		if (params) {
			for ( var key in params) {
				formData.append(key, params[key]);
			}
		}
		$.ajax({
			async : options.async || true,
			dataType : "json",
			type : 'POST',
			url : options.url,
			data : formData,
			processData : false, // 必须false才会避开jQuery对 formdata 的默认处理
									// ,XMLHttpRequest会对 formdata 进行正确的处理
			contentType : false,// 必须false才会自动加上正确的Content-Type
			error : function(xmlHttpRequest, textStatus, errorThrown) {
				if (options.error) {
					options.error.apply(null, [ xmlHttpRequest, textStatus,
							errorThrown ]);
				} else {
					alert("服务端出错:<BR/>" + textStatus + "." + errorThrown);
				}
			},
			success : function(responseData, textStatus, jqXHR) {
				try {
					var resp_body = responseData.body || {};
					var resp_header = responseData.header || {};

					var success = options.success;
					var error = options.error;

					// 需要对应答体内容做一下包装，以便增加一些便捷的API，具体参见wrapData方法
					if (resp_header.error_code == 0) {
						if (success != null){
							success.apply(this, [ resp_header, resp_body,textStatus, jqXHR ]);
						}
					} else {
						var result = true;
						if (error != null) {
							result = error.apply(this, [ resp_header,resp_body, textStatus, jqXHR ]);
						}
						if (result == false)
							return;
						message.alert(resp_header.error_message,resp_header.error_code);
					}
				} catch (exception) {
					$progressDialog && message.hideDialog($progressDialog);
					throw exception;
				}
			},
			complete : function(data, textStatus) {
				$progressDialog && message.hideDialog($progressDialog);
			}
		});
	};
	
	/**
	 * 向服务端下载某个资源
	 * @param options,是个对象，有以下属性
	 *            url:请求的url 
	 *            params,object，存放具体的业务请求参数，比如登录的时候需要传账号和密码：
	 *            			params:{account:bill,password:123}
	 * 
	 * 
	 */
	this.download = function(options){
		var $iframe = $("#export_commitIframe");
		if($iframe == null || $iframe.length == 0){
			$("<iframe id='export_commitIframe' name='export_commitIframe' target='_self' style='display:none'/>").appendTo($("body"));
			
		}
		
		var params = options.params || {};
		params = $.extend(params, buildHeader(params.token));
		params.sign = md5Params(params);
		
		var $form = $("#export_form");
		if($form == null || $form.length == 0){
			$form = $("<form id='export_form' action='"+options.url+"' target='export_commitIframe' style='display:none' method='post'/>").appendTo($("body"));
		}else{
			$form.empty();
		}
		
		for(key in params){
			$("<input name='"+key+"' value='"+params[key]+"'>").appendTo($form);
		}
		$form.submit();
	};
	
	
	var buildHeader = function(token) {
		token = token || common.getToken();
		return {
			agent_type : common.isWechat() ? "wechat" : "browser",
			//agent_type : "wechat",
			device_type : common.getDeviceType(),
			token : token,
			app_version : common.getUrlParams("app_version") || ''
		};
	}
	var md5Params = function(params) {
		//var md5IgnoreParams = ["device_type","agent_type","app_lang"];
		var md5NeedParams = [];
		var md5str = "";
		for(var k in params) {
			if(params[k] === null || params[k] === undefined){
				delete params[k];
				continue;
			}
			md5NeedParams.push(k);
		}
		if(md5NeedParams.length>0){
			md5NeedParams.sort();
			md5str = md5NeedParams[0] + "=" + params[md5NeedParams[0]];
			for(var i=1;i<md5NeedParams.length;i++) {
				md5str = md5str + "&" + md5NeedParams[i] + "=" + params[md5NeedParams[i]];
			}
		}
		md5str = md5str + common.getSkey();
		//console.log("before md5:" + md5str);
		md5str = encrypt.md5.encode(md5str);
		//console.log("after md5:" + md5str);
		return md5str;
	}
	
	var dealCommonResponse = function(responseHeader,responseBody,needOAuth,basedata){
		//身份信息
		if(responseBody.auth){
			common.setAuth(
					responseBody.auth.token,
					responseBody.auth.skey,
					responseBody.auth.user);
		}
		//微信签名,wxShare可以是object，也可以是function
		/*if(wxShare && responseBody.wxSignature){
			xsgwx.config(responseBody.wxSignature,function(){
				var shareOpitons = typeof wxShare == 'function' ? wxShare.apply(null,[responseHeader,responseBody])
												: wxShare;
				
				xsgwx.showOptionMenu();
				xsgwx.shareTimeline(
						shareOpitons.timeline_desc, 
						shareOpitons.url, 
						shareOpitons.img);
				
				xsgwx.sendAppMessage(
						shareOpitons.message_title, 
						shareOpitons.message_desc,
						shareOpitons.url, 
						shareOpitons.img);
			});
		}*/
		
		//基础数据
		if(responseBody.baseparamFlag != null && responseBody.baseparamFlag != getSysparamFlag()){
			setSysparam(responseBody.baseparam,responseBody.baseparamFlag);
		}
		if(responseBody.enumFlag != null && responseBody.enumFlag != getEnumFlag()){
			setEnum(responseBody.enum,responseBody.enumFlag);
		}
	}
}

var widget = new function() {
	/**
	 * 初始化拖动行为，pc版和移动版不一样. 判断是左右上下四个方向的滑动，然后回调传入的callback方法
	 * 
	 * @param $touchObj
	 * @param callback,接收两个参数:$touchObj,direction
	 */
	this.initDragable = function($touchObj, startCallback, moveCallback,
			endCallback) {
		if (common.isPC()) {
			widget.initPCDragable($touchObj, startCallback, moveCallback,
					endCallback);
		} else {
			widget.initMobileDragable($touchObj, startCallback, moveCallback,
					endCallback);
		}
	}
	this.initPCDragable = function($touchObj, startCallback, moveCallback,
			endCallback) {
		var isMousedown = false, isMoved = false;
		var pressX, pressY;
		var $body = $("body");
		$body.bind("mousedown", function(e) {
			e = e.originalEvent;
			pressX = e.pageX;
			pressY = e.pageY;
			isMousedown = true;
			startCallback && startCallback.apply($(this), [ pressX, pressY ]);
		});
		$body.bind("mousemove", function(e) {
			// console.log("mousemove
			// ,isMousedown="+isMousedown+",isMoved="+isMoved);
			if (!isMousedown) {
				isMoved = false;
				pressX = pressY = null;
				return;
			}
			e = e.originalEvent;
			var offsetX = e.pageX - pressX;
			var offsetY = e.pageY - pressY;
			var direction = "none";
			// console.log("x:"+offsetX+",y:"+offsetY);
			if (Math.abs(offsetX) > Math.abs(offsetY)) {
				// 水平方向
				direction = offsetX > 0 ? "right" : "left";
			} else {
				// 垂直方向
				direction = offsetY > 0 ? "down" : "up";
			}
			isMoved = true;
			moveCallback.apply($(this), [ offsetX, offsetY, direction ]);

		});
		$body.bind("mouseup", function(e) {
			if (!isMoved) {
				isMousedown = false;
				isMoved = false;
				pressX = pressY = null;
				return;
			}
			e = e.originalEvent;
			var offsetX = e.pageX - pressX;
			var offsetY = e.pageY - pressY;
			var direction = "none";
			if (Math.abs(offsetX) > Math.abs(offsetY)) {
				// 水平方向
				direction = offsetX > 0 ? "right" : "left";
			} else {
				// 垂直方向
				direction = offsetY > 0 ? "down" : "up";
			}
			endCallback.apply($(this), [ offsetX, offsetY, direction ]);
			isMousedown = false;
			isMoved = false;
			pressX = pressY = null;
		});
	}
	this.initTouchable = function($touchObj, startCallback, moveCallback,endCallback) {
		var pressX = null;
		var pressY = null;
		$touchObj.bind("touchstart", function(event) {
			// 如果这个元素的位置内只有一个手指的话
			event = event.originalEvent;
			if (event.targetTouches.length == 1) {
				var touch = event.targetTouches[0];
				// 手指所在的位置
				pressX = touch.pageX;
				pressY = touch.pageY;

				var result = startCallback
						&& startCallback.apply($(this), [ event,pressX, pressY ]);
				if (result === false) {
					event.preventDefault();// 阻止浏览器的默认事件
				}
			}
		});

		if (moveCallback) {
			$touchObj.bind("touchmove", function(event) {
				event = event.originalEvent;
				// 如果这个元素的位置内只有一个手指的话
				if (event.targetTouches.length == 1) {
					var touch = event.targetTouches[0];
					var offsetX = touch.pageX - pressX;
					var offsetY = touch.pageY - pressY;
					var h_direction = offsetX == 0 ? null
							: (offsetX < 0 ? "left" : "right");// 水平移动的方向
					var v_direction = offsetY == 0 ? null : (offsetY < 0 ? "up"
							: "down");// 垂直移动的方向
					// console.log("x:"+offsetX+",y:"+offsetY);
					var result = moveCallback.apply($(this), [ event,offsetX,
							offsetY, h_direction, v_direction ]);
					if (result === false) {
						event.preventDefault();// 阻止浏览器的默认事件
					}
				}
				// event.preventDefault();//阻止浏览器的默认事件
			});

		}

		if (endCallback) {
			$touchObj.bind("touchend", function(event) {
				event = event.originalEvent;
				// 如果这个元素的位置内只有一个手指的话
				if (event.changedTouches.length == 1) {
					var touch = event.changedTouches[0];
					var offsetX = touch.pageX - pressX;
					var offsetY = touch.pageY - pressY;
					var h_direction = offsetX == 0 ? null
							: (offsetX < 0 ? "left" : "right");// 水平移动的方向
					var v_direction = offsetY == 0 ? null : (offsetY < 0 ? "up"
							: "down");// 垂直移动的方向
					var result = endCallback.apply($(this), [event, offsetX, offsetY,
							h_direction, v_direction]);
					if (result === false) {
						event.preventDefault();// 阻止浏览器的默认事件
					}
				}
				// event.preventDefault();//阻止浏览器的默认事件
			});
		}
	}
	// 绑定元素的触摸事件，会自动判断是点击还是滚屏触摸，如果是后者则不会触发触摸事件
	this.bindTouchClick = function($selector, func) {
		if (common.isPC()) {
			$selector.click(func);
		} else {
			// 使用touch事件，因为移动端对于click的事件性能较差
			$selector.bind("touchstart", function(e) {
				$selector.data("_touchmoved", 0);// 表示有touchmove事件，这样可以区分区分出来是点击还是滑动，滑动的话在touchend里不需要触发
			});
			$selector.bind("touchmove", function(e) {
				$selector.data("_touchmoved", 1);// 表示有touchmove事件，这样可以区分区分出来是点击还是滑动，滑动的话在touchend里不需要触发
			});
			$selector.bind("touchend", function(e) {
				if ($selector.data("_touchmoved") == 1)
					return;
				func && func.apply($selector.get(0), [ e ]);
			});
		}

	}

	var __animate_conf = {
		"left right" : {
			css : {
				position : "fixed",
				"background-color" : "#FFF",
				top : "0px",
				bottom : "0px",
				left : "-100%",
				right : "auto",
				width : "100%"
			},
			style : {
				left : 0
			}
		},
		"right left" : {
			css : function($slideObj){
				return {
					position : "fixed",
					"background-color" : "#FFF",
					top : "0px",
					bottom : "0px",
					right : -1*$slideObj.width()
				}
			},
			style : {
				right : 0
			}
		},
		"top bottom" : {
			css : {
				position : "fixed",
				"background-color" : "#FFF",
				top : "-100%",
				bottom : "auto",
				// height: "100%",
				left : "0px",
				right : "0px"
			},
			style : {
				bottom : 0
			}
		},
		"bottom top" : {
			css : function($slideObj){
				return {
					position : "fixed",
					"background-color" : "#FFF",
					bottom : -1*$slideObj.height(),
					left:0,
					right:0
				}
			},
			style : {
				bottom : 0
			}
		}
	};

	/**
	 * 界面或者组件的切换效果。比如一个界面要从右到左滑入或者下往上滑入。如果不要任何效果，direction参数不要传值
	 * 
	 * @param $slideObj,要滑动的对象
	 * @param direction，滑动的方向效果,取值:null/"left
	 *            right"/"right left"/"bottom top"/"top bottom"
	 * @param options,其它参数，如果options是一个数字，则表示是duration;如果是function，则表示回调函数
	 *            style:最终的滑动后的效果，默认情况下都是滑动都是填满父容器的，如果想滑到距离左侧30px，可以传入{left:30},
	 *            callback:结束后回调函数 duration:效果持续时间
	 */
	this.slide = function($slideObj, direction, options) {
		if(options !== null){
			if(arguments.length == 4){
				options = {
					duration:arguments[2],
					callback:arguments[3]
				};
			}else if(typeof options == 'number'){
				options = {duration:options};
			}else if(typeof options == 'function'){
				options = {callback:options};
			};
		}
		if (!direction) {
			$slideObj.show();
			options && options.callback && options.callback.apply();
			return;
		}
		// if($slideObj.data("init_animate") != 1){
		var defaultCss = __animate_conf[direction].css;
		$slideObj.css(typeof defaultCss=='object'?defaultCss:defaultCss.apply(null,[$slideObj]));
		$slideObj.data("init_animate", 1);// 表示已经初始化过了
		// }
		duration = options && options.duration;
		if (duration == null) {
			duration = direction.indexOf("left") > -1 ? 200 : 400;
		}

		// 如果还有自定义最终的样式，则需要覆盖默认
		var style = __animate_conf[direction].style;
		if (options && options.style){
			style = $.extend({}, style,options.style) ;
		}
		$slideObj.show();
		$slideObj.animate(style, duration, null, options && options.callback);
	}
	
	/**
	 * @param options,
	 * 			z_index,
	 * 			need_titlebar,true/false/string,string的话显示标题栏文字,true的话显示是/否两个按钮
	 * 			remove_when_close,1或0，关闭是是否移除当前popmenu
	 */
	this.popupMenu = function($popupMenu,direction,menus,options){
		if(arguments.length == 2){
			//只有2个参数，表示关闭popupmenu,true表示保留popmenu，false表示移除popmenu
			message.hideMask($popupMenu.data("mask"));
			options = $popupMenu.data("options");
			options && (options.remove_when_close === true) ? 
					$popupMenu.remove() : $popupMenu.hide();
			
			return;
		}
		
		var v_or_h = direction.indexOf("left") > -1 ? 'h':'v';//水平方向还是垂直方向
		var zIndex = (options && options.z_index) || 99990;
		if($popupMenu == null || $popupMenu.length == 0){
			$popupMenu = v_or_h == 'h' ? createPopupmenu4H(menus,options) : createPopupmenu4V(menus,options);
			$popupMenu.css("z-index",zIndex+2);
			$popupMenu.data("options",options);
		}
		
		var $mask = message.showMask(null,function(){
			widget.popupMenu($popupMenu,false);
		},{"z-index":zIndex+1});
		$popupMenu.data("mask",$mask);
		
		widget.slide($popupMenu,direction,200);
		
		return $popupMenu;
	}
	//创建水平方向的popupmenu，即left right或者right left
	var createPopupmenu4H = function(menus,options){
		//说明不存在，则创建并设置各种事件
		var $popupMenu = $('<div class="popupmenu-h"></div>');
		$popupMenu.appendTo($("body"));
		createPopupmenuItems($popupMenu,menus,$popupMenu);
		
		//菜单点击事件
		/*widget.bindTouchClick($popupMenu,function(e){
			widget.popupMenu($(this),false);
			e.stopPropagation();
		});*/
		
		
		return $popupMenu;
	};
	
	/**
	 * 创建垂直方向的popupmenu，即top bottom或者bottom top
	 * @param menus,menu对象，属性
	 * 			icon,图标路径
	 * 			style,string/object,string表示className,object表示css对象
	 * @param options
	 * 			 need_titlebar
	 */
	var createPopupmenu4V = function(menus,options){
		var $popupMenu = $("<div class='popupmenu-v'></div>").appendTo($("body"));
		if(options && options.need_titlebar){
			var $title = $("<div class='popupmenu-v-title'></div>").appendTo($popupMenu);
			if(options.need_titlebar === true){
				var $title_bttn_cancel = $("<div class='popupmenu-v-cancel gray-font'>取消</div>").appendTo($title);
				var $title_bttn_ok = $("<div class='popupmenu-v-ok xsg-font'>确定</div>").appendTo($title);
			}else{
				$title.text(options.need_titlebar).addClass("xsg-font");
			}
			$("<div class='separator_h'></div>").appendTo($popupMenu);
		}
		var $content = $("<div class='popupmenu-v-content'></div>").appendTo($popupMenu);
		
		createPopupmenuItems($popupMenu,menus,$content);
		return $popupMenu;
	}
	
	var createPopupmenuItems = function($popupMenu,menus,$container){
		var $icon;
		var touchedClass='bttn-touched';
		var hasIcon = null;
		for(var i=0;i<menus.length;i++){
			if(menus[i].icon || menus[i].iconfont){
				hasIcon = true;
				break;//有一个有图片，那么所有item都必须留出icon的位置出来
			}
		}
		for(var i=0;i<menus.length;i++){
			var menu = menus[i];
			if(!menu){
				continue;
			}
			if(menu == "-"){
				$("<div class='separator_h'/>").appendTo($container);
				continue;
			}
			var $item = $('<div class="popupmenu-item"></div>').appendTo($container);
			$item.data("entity",menu);
			
			//设置icon
			if(hasIcon){
				$icon = $('<div class="popupmenu-item-icon"></div>').appendTo($item);
				if(menu.icon){
					$icon.css({
						"background":"url("+menu.icon+") no-repeat center center",
						"background-size" : "14px 14px"
					});
				}else if(menu.iconfont){
					$icon.html(menu.iconfont);
					$icon.addClass("xsg-fontset");
					menu.icon_color && $icon.css("color",menu.icon_color);
				}
			}
			
			var $text = $('<div class="popupmenu-item-text">').appendTo($item);
			$text.html(menu.text);
			
			if(menu.style){
				if(typeof menu.style == 'string'){
					$text.addClass(menu.style);
				}else if(typeof menu.style == 'object'){
					$text.css(menu.style);//是一个css的object样式
				}
			}
			if(!hasIcon){
				$text.width("100%");
			}
			
			
			//菜单项touch事件，改变底色，touchend要触发handler函数
			widget.initTouchable($item,function(e){
				$(this).addClass(touchedClass);
			},null,function(e,offsetX,offsetY){
				$(this).removeClass(touchedClass);
			});
			
			$item.click(function(e){
				var entity = $(this).data("entity");//关闭popuoMenu的时候可能整体会移除，所以先把entity保存下来
				
				widget.popupMenu($popupMenu,false);
				entity.handler && entity.handler.apply(null,[entity]);
				e.stopPropagation();
				
			});
			
		}
	}
	
}

var message = new function() {
	var createMask = function(){
		return $("<div class='message_mask'/>");
	}
	var createDialog = function($messageMask,title,content,callback){
		var $messageDialog = $("<div class='common_font message_dialog' tabindex='1'/>");
		var $title = $("<div>").appendTo($messageDialog).addClass("common_font message_dialog_title");
		$("<div>").appendTo($messageDialog).addClass("separator_h");
		var $content = $("<div>").appendTo($messageDialog).addClass("common_font message_dialog_content");
		
		$title.text(title || "");
		$content.html(content);
		
		$messageDialog.data("mask",$messageMask);
		
		return $messageDialog;
	}
	/**
	 * 显示普通消息
	 * 
	 * @param content
	 * @param title
	 * @param callback
	 * @returns {___anonymous_$_messageDialog}
	 */
	this.alert = function(content, callback) {
		if (common.isFromApp()) {
			// 如果是从其乐app里的web界面，则都用原生的弹框
			alert(content);
			return;
		}
		var topbody = top.document.body;
		var $messageMask = createMask().appendTo(topbody);
		var $messageDialog = createDialog($messageMask,"提示",content,callback).appendTo(topbody);
		
		var $button = $("<div id='message_dialog_button' ontouchstart='return true;'>")
						.appendTo($messageDialog).addClass("white_button common_font message_dialog_button")
						.text("确定");
		// pc里按下回车键要关闭对话框
		if (common.isPC()) {
			$messageDialog.keypress(function(e) {
				if (e.keyCode == 13) {
					$button.trigger("click");
				}
			});
		}
		
		widget.bindTouchClick($button, function(e) {
			e.stopPropagation();
			callback && callback.apply();
			message.hideDialog($messageDialog);
			e.preventDefault();
		});
		
		$messageMask.show();
		$messageDialog.show();
		$messageDialog.focus();
		
		//居中显示
		$messageDialog.css({
			top : (window.innerHeight - $messageDialog.outerHeight()) / 2 + "px"
		});

		return $messageDialog;
	}

	/**
	 * 确认对话框，有两个按钮，取消/确定，两个按钮的文字可以通过参数指定
	 * @param content
	 * @param callback,点击第二个按钮时的回调函数
	 * @param buttonTexts,array<string>,指定按钮文字，从左到右，比如['不需要','去看看']
	 */
	this.confirm = function(content, callback,buttonTexts) {
		var topbody = top.document.body;
		var $messageMask = createMask().appendTo(topbody);
		var $messageDialog = createDialog($messageMask,"确认",content,callback).appendTo(topbody);
		
		
		var $no_button = $("<div id='message_dialog_button_yes' style='float:left;width:50%;color:#888888' ontouchstart='return true;'>")
						.appendTo($messageDialog).addClass("white_button common_font message_dialog_button")
						.text((buttonTexts && buttonTexts[0]) || "取消");
		var $yes_button = $("<div id='message_dialog_button_no' style='float:left;width:50%' ontouchstart='return true;'>")
						.appendTo($messageDialog).addClass("white_button common_font message_dialog_button xsg-font")
						.text((buttonTexts && buttonTexts[1]) || "确定");
		
		widget.bindTouchClick($yes_button, function(e) {
			callback && callback.apply();
			e.stopPropagation();
			message.hideDialog($messageDialog);
		});

		widget.bindTouchClick($no_button, function(e) {
			message.hideDialog($messageDialog);
			e.stopPropagation();
		});
		
		$messageDialog.show();
		$messageDialog.show();

		$messageDialog.css({
			top : (window.innerHeight - $messageDialog.outerHeight()) / 2 +"px"
		});

		return $messageDialog;
	}
	
	this.confirmSlide = function(content,callback,buttonTexts,id) {
		var topbody = top.document.body;
		id = id || "_confirm_slide"
		var $slidePage = $(topbody).find("#"+id);//createDialog($messageMask,"确认",content,callback).appendTo(topbody);
		var $popupMenu = widget.popupMenu($slidePage,"bottom top",[{
			text : (buttonTexts && buttonTexts[0]) || "是",
			handler : callback,
			style :{"font-size":"1.1rem"}
		},{
			text : (buttonTexts && buttonTexts[1]) || "否",
			style :{
				"font-size":"1.1rem",
				color : "#b3b3b3"
			}/*,
			handler : function(){
				widget.popupMenu($slidePage,false);
			}*/
		}],{need_titlebar:content,remove_when_close:true});//需要每次移除掉，因为涉及到闭包问题，按钮绑定的是第一次创建传入的callback事件
		$popupMenu.attr("id",id);
	}
	
	/**
	 * 显示处理进度框
	 * 
	 * @param content
	 * @param callback
	 * @returns {___anonymous_$_progressDialog}
	 */
	this.progress = function(content, callback) {
		var topbody = top.document.body;
		var $messageMask = createMask().appendTo(topbody);
		var $messageDialog = createDialog($messageMask,null,content || "正在处理，请稍候...",callback).appendTo(topbody);
		
		$messageDialog.children(".message_dialog_title")
							.removeClass("message_dialog_title")
							.addClass("progress_dialog_title")
		$messageMask.show();
		$messageDialog.show();
		
		//居中显示
		$messageDialog.css({
			top : (window.innerHeight - $messageDialog.outerHeight()) / 2 + "px"
		});

		return $messageDialog;
		
	}
	
	this.progress2 = function($container,callback) {
		var $progress = $container.children(".progress_gif");
		if($progress.length == 0){
			$progress = $("<div>").appendTo($container);
			$progress.addClass("progress_gif");
		}
		$progress.show();
		
		return $progress;
	}
	
	/**
	 * 高亮显示错误信息然后渐渐隐藏.底部为红色背景
	 */
	this.errorHide = function(content, $bgObj, duration,cssObj) {
		highlightMessage(content, "error_highlight", $bgObj, duration,cssObj);
	};
	this.successHide = function(content, $bgObj, duration,cssObj) {
		highlightMessage(content, "success_highlight", $bgObj, duration,cssObj);
	};
	function highlightMessage(content,bgClass, $bgObj, duration,cssObj) {
		duration = duration || 2000;
		var $_hightlightDialog = $("<div class='common_font highlight_dialog'/>")
									.appendTo($bgObj || $("body"));
		$_hightlightDialog.removeClass("error_highlight success_highlight");
		$_hightlightDialog.addClass(bgClass);
		if(cssObj){
			if(typeof cssObj == 'string'){
				$_hightlightDialog.addClass(cssObj);
			}else{
				$_hightlightDialog.css(cssObj);
			}
		}
		
		$_hightlightDialog.show();
		$_hightlightDialog.text(content);

		//$bgObj && $bgObj.addClass("opacity_0");
		setTimeout(function() {
			//$bgObj && $bgObj.removeClass("opacity_0");
			$_hightlightDialog.animate({
				opacity : "hide"
			}, 1000, null,function(){
				$_hightlightDialog.remove();
			});
		}, duration);
	}
	;
	this.hideDialog = function($dialog) {
		var $messageMask = $dialog.data("mask");
		$messageMask && this.hideMask($messageMask);
		$dialog.remove();
	}
	this.showMask = function($area,clickFunc,cssObj) {
		var $mask = createMask().appendTo($area || top.document.body);
		cssObj && $mask.css(cssObj);
		widget.bindTouchClick($mask,function(e){
			clickFunc && clickFunc.apply($mask,[$mask]);
			e.stopPropagation();
		});
		return $mask;
	}
	this.hideMask = function($messageMask) {
		$messageMask.remove();
	}
	
	this.showLoading = function(){
		var $dialog = $(".loading-container");
		if($dialog.length == 0){
			$dialog = $("<div class='loading-container'>").appendTo($("body"));
			var $table = $("<table class='loading-table'><tr><td></td></tr></table>").appendTo($dialog);
			var $logo = $("<div class='loading-logo'>").appendTo($table.find("td"));
			var $loading = $("<div class='loading-gif'>").appendTo($table.find("td"));
		}
		$dialog.show();
		return $dialog;
	}
}
var app = new function() {
	this.setupIosJsBridge = function(callback) {
        if (window.WebViewJavascriptBridge) { 
        	return callback(WebViewJavascriptBridge); 
        }
        if (window.WVJBCallbacks) { 
        	return window.WVJBCallbacks.push(callback); 
        }
        window.WVJBCallbacks = [callback];
        var WVJBIframe = document.createElement('iframe');
        WVJBIframe.style.display = 'none';
        WVJBIframe.src = 'wvjbscheme://__BRIDGE_LOADED__';
        document.documentElement.appendChild(WVJBIframe);
        setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)
    };
	
	/**
	 * 初始化某个控件和原生app交互.为了防止连续的触摸，所以触摸一次后先把控件的disable置为true，完毕后再修改回来
	 * 
	 * @param $item,点击
	 * @param action,类型，根据业务侧来处理
	 */
	this.initAccessApp = function($item, action, params) {
		widget.bindTouchClick($item, function(e) {
			if ($item.attr("disabled") == true)
				return;
			$item.attr("disabled", true);
			app.accessApp(action, params);
			$item.attr("disabled", false);
		});
	}
	this.accessApp = function(action, params) {
		if (!common.isFromApp())
			return;
		var split = ":";
		var url = "accessapp" + split + action;
		if (params != null) {
			url += ":" + JSON.stringify(params);// params.join(":");
		}
		document.location = url;

	};
	this.creatDownloadBar = function(title, desc) {
		var html_arr = [ "<table class='mobile_downloadbar'><tr>" ];
		html_arr.push("	<td width='60px'><div class='logo'></div></td>");

		html_arr.push("	<td>");
		html_arr.push("	<div class='slogon_title'>");
		html_arr.push(title);
		html_arr.push("	</div>");
		html_arr.push("	<div class='slogon_content'>");
		html_arr.push(desc);
		html_arr.push("	</div>");
		html_arr.push("	</td>");

		html_arr.push("<td width='100px'>");
		html_arr.push("<div class='download'>立即下载</div>");
		html_arr.push("</td>");

		html_arr.push("</tr></table>");
		var $div = $(html_arr.join("")).appendTo($("body"));

		widget.bindTouchClick($div.find(".download"), function() {
			common.gotoPage(common.getHost()
					+ "/elsetravel/app/download_app.html");
			// gotoPage("https://itunes.apple.com/cn/app/else-travel-qi-le-lu-xing/id1053429006?mt=8")
		});
	};

	/**
	 * 创建在微信侧，右上角打开某个功能的提示
	 * 
	 * @param content，提示内容
	 * @param needmask,boolean,是否需要黑色底
	 * @param hideByClick,boolean,点击是否隐藏该提示框
	 */
	this.creatWxTopRightIndicator = function(content, needmask, hideByClick) {
		var id = "__topright_indicator";
		var $indicator = $("#" + id);
		if ($indicator.length == 0) {
			var html_arr = [ '<div id="' + id + '" class="topright_indicator">' ];
			html_arr.push('<div class="arrow"></div>');
			html_arr.push('<div class="text">');
			html_arr.push(content);
			html_arr.push('</div>');
			html_arr.push('</div>');
			$indicator = $(html_arr.join("")).appendTo($("body"));
			var $arrow = $(".topright_indicator .arrow");
			var $text = $(".topright_indicator .text");
			if (needmask) {
				$indicator.addClass("mask");
				$arrow.addClass("white");// 黑色底的话要配白色线条
				$text.addClass("white");
			} else {
				$arrow.addClass("black");
				$text.addClass("black");
			}

			$arrow.css({
				"margin-left" : window.innerWidth - 130,
				height : common.calcHeight(130, 130, 254)
			});

			if (hideByClick) {
				widget.bindTouchClick($indicator, function() {
					if(typeof hideByClick == 'function'){
						hideByClick.apply(null,[$indicator]);
					}else if(hideByClick){
						$indicator.hide();
					}
				});
			}
		}
		$indicator.show();
	}
};

var xsgwx = new function() {
	var appid = "wx32b6780d93247483";
	var _api_url = common.getHost() + "/xiesange";
	
	/*this.init = function(callback) {
		ajax.request({
			url : _api_url + "/web/wx/getSignature.do",
			need_progressbar : false,
			params : {
				url : window.location.href
			},
			success : function(header, body) {
				xsgwx.config(body.signature,callback)
			}
		});

	};*/
	
	this.config = function(signatureObj,callback){
		if(signatureObj == null){
			return;
		}
		wx.config({
			//debug: true, //
			// 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			appId : signatureObj.appid, // 必填，公众号的唯一标识
			timestamp : signatureObj.timestamp, // 必填，生成签名的时间戳
			nonceStr : signatureObj.random, // 必填，生成签名的随机串
			signature : signatureObj.signature,// 必填，签名，见附录1
			jsApiList : [ 'checkJsApi', 'onMenuShareTimeline',
				'onMenuShareAppMessage', 'hideOptionMenu',
				'showOptionMenu', 'chooseImage', 'hideAllNonBaseMenuItem',
				'showMenuItems'
			]
			// 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		if(!common.isWechat()){
			callback.apply();
			return;
		}
		if (callback) {
			wx.ready(function() {
				callback.apply();
				//config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
			});
		}
	}
	
	this.shareTimeline = function(title, url, imgurl, func) {
		if(!common.isWechat()){
			return;
		}
		wx.onMenuShareTimeline({
			title : title, // 分享标题
			link : url, // 分享链接
			imgUrl : imgurl,// 分享图标
			success: func
		});
	};
	this.sendAppMessage = function(title, desc, url, imgurl, func) {
		if(!common.isWechat()){
			return;
		}
		wx.onMenuShareAppMessage({
			title : title, // 分享标题
			desc : desc, // 分享描述
			link : url, // 分享链接
			imgUrl : imgurl, // 分享图标
			success: func
		});
	};

	this.hideOptionMenu = function() {
		if(!common.isWechat()){
			return;
		}
		wx.hideOptionMenu();
	};
	this.showOptionMenu = function() {
		if(!common.isWechat()){
			return;
		}
		wx.showOptionMenu();
	};
	this.hideMenuItems = function(list) {
		if(!common.isWechat()){
			return;
		}
		wx.hideMenuItems({
			menuList: list // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮
		});
	};
	this.showMenuItems = function(list) {
		if(!common.isWechat()){
			return;
		}
		wx.showMenuItems({
			menuList: list // 要显示的菜单项
		});
	};
	this.hideAllNonBaseMenuItem = function(){
		if(!common.isWechat()){
			return;
		}
		wx.hideAllNonBaseMenuItem();
	}
	
	this.viewImages = function(allUrls,currentUrl){
		currentUrl = currentUrl || allUrls[0]; 
		wx.previewImage({
		    current: currentUrl, // 当前显示图片的http链接
		    urls: allUrls // 需要预览的图片http链接列表
		});
	}
	
	this.buildWxOAuthUrl = function(redirectUrl, needFullInfo) {
		redirectUrl = redirectUrl.replace("&","%26");
		var scope = needFullInfo ? "snsapi_userinfo" : "snsapi_base";
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="
				+ redirectUrl
				+ "&response_type=code&scope="
				+ scope
				+ "&state=1#wechat_redirect"

	}
}

var encrypt = new function() {
	this.md5 = new function() {
		this.encode = function(string){
			var x = Array();
			var k, AA, BB, CC, DD, a, b, c, d;
			var S11=7, S12=12, S13=17, S14=22;
			var S21=5, S22=9 , S23=14, S24=20;
			var S31=4, S32=11, S33=16, S34=23;
			var S41=6, S42=10, S43=15, S44=21;
			string = uTF8Encode(string);
			x = convertToWordArray(string);
			a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476;
			for (k = 0; k < x.length; k += 16) {
				AA = a; BB = b; CC = c; DD = d;
				a = FF(a, b, c, d, x[k+0],  S11, 0xD76AA478);
				d = FF(d, a, b, c, x[k+1],  S12, 0xE8C7B756);
				c = FF(c, d, a, b, x[k+2],  S13, 0x242070DB);
				b = FF(b, c, d, a, x[k+3],  S14, 0xC1BDCEEE);
				a = FF(a, b, c, d, x[k+4],  S11, 0xF57C0FAF);
				d = FF(d, a, b, c, x[k+5],  S12, 0x4787C62A);
				c = FF(c, d, a, b, x[k+6],  S13, 0xA8304613);
				b = FF(b, c, d, a, x[k+7],  S14, 0xFD469501);
				a = FF(a, b, c, d, x[k+8],  S11, 0x698098D8);
				d = FF(d, a, b, c, x[k+9],  S12, 0x8B44F7AF);
				c = FF(c, d, a, b, x[k+10], S13, 0xFFFF5BB1);
				b = FF(b, c, d, a, x[k+11], S14, 0x895CD7BE);
				a = FF(a, b, c, d, x[k+12], S11, 0x6B901122);
				d = FF(d, a, b, c, x[k+13], S12, 0xFD987193);
				c = FF(c, d, a, b, x[k+14], S13, 0xA679438E);
				b = FF(b, c, d, a, x[k+15], S14, 0x49B40821);
				a = GG(a, b, c, d, x[k+1],  S21, 0xF61E2562);
				d = GG(d, a, b, c, x[k+6],  S22, 0xC040B340);
				c = GG(c, d, a, b, x[k+11], S23, 0x265E5A51);
				b = GG(b, c, d, a, x[k+0],  S24, 0xE9B6C7AA);
				a = GG(a, b, c, d, x[k+5],  S21, 0xD62F105D);
				d = GG(d, a, b, c, x[k+10], S22, 0x2441453);
				c = GG(c, d, a, b, x[k+15], S23, 0xD8A1E681);
				b = GG(b, c, d, a, x[k+4],  S24, 0xE7D3FBC8);
				a = GG(a, b, c, d, x[k+9],  S21, 0x21E1CDE6);
				d = GG(d, a, b, c, x[k+14], S22, 0xC33707D6);
				c = GG(c, d, a, b, x[k+3],  S23, 0xF4D50D87);
				b = GG(b, c, d, a, x[k+8],  S24, 0x455A14ED);
				a = GG(a, b, c, d, x[k+13], S21, 0xA9E3E905);
				d = GG(d, a, b, c, x[k+2],  S22, 0xFCEFA3F8);
				c = GG(c, d, a, b, x[k+7],  S23, 0x676F02D9);
				b = GG(b, c, d, a, x[k+12], S24, 0x8D2A4C8A);
				a = HH(a, b, c, d, x[k+5],  S31, 0xFFFA3942);
				d = HH(d, a, b, c, x[k+8],  S32, 0x8771F681);
				c = HH(c, d, a, b, x[k+11], S33, 0x6D9D6122);
				b = HH(b, c, d, a, x[k+14], S34, 0xFDE5380C);
				a = HH(a, b, c, d, x[k+1],  S31, 0xA4BEEA44);
				d = HH(d, a, b, c, x[k+4],  S32, 0x4BDECFA9);
				c = HH(c, d, a, b, x[k+7],  S33, 0xF6BB4B60);
				b = HH(b, c, d, a, x[k+10], S34, 0xBEBFBC70);
				a = HH(a, b, c, d, x[k+13], S31, 0x289B7EC6);
				d = HH(d, a, b, c, x[k+0],  S32, 0xEAA127FA);
				c = HH(c, d, a, b, x[k+3],  S33, 0xD4EF3085);
				b = HH(b, c, d, a, x[k+6],  S34, 0x4881D05);
				a = HH(a, b, c, d, x[k+9],  S31, 0xD9D4D039);
				d = HH(d, a, b, c, x[k+12], S32, 0xE6DB99E5);
				c = HH(c, d, a, b, x[k+15], S33, 0x1FA27CF8);
				b = HH(b, c, d, a, x[k+2],  S34, 0xC4AC5665);
				a = II(a, b, c, d, x[k+0],  S41, 0xF4292244);
				d = II(d, a, b, c, x[k+7],  S42, 0x432AFF97);
				c = II(c, d, a, b, x[k+14], S43, 0xAB9423A7);
				b = II(b, c, d, a, x[k+5],  S44, 0xFC93A039);
				a = II(a, b, c, d, x[k+12], S41, 0x655B59C3);
				d = II(d, a, b, c, x[k+3],  S42, 0x8F0CCC92);
				c = II(c, d, a, b, x[k+10], S43, 0xFFEFF47D);
				b = II(b, c, d, a, x[k+1],  S44, 0x85845DD1);
				a = II(a, b, c, d, x[k+8],  S41, 0x6FA87E4F);
				d = II(d, a, b, c, x[k+15], S42, 0xFE2CE6E0);
				c = II(c, d, a, b, x[k+6],  S43, 0xA3014314);
				b = II(b, c, d, a, x[k+13], S44, 0x4E0811A1);
				a = II(a, b, c, d, x[k+4],  S41, 0xF7537E82);
				d = II(d, a, b, c, x[k+11], S42, 0xBD3AF235);
				c = II(c, d, a, b, x[k+2],  S43, 0x2AD7D2BB);
				b = II(b, c, d, a, x[k+9],  S44, 0xEB86D391);
				a = addUnsigned(a, AA);
				b = addUnsigned(b, BB);
				c = addUnsigned(c, CC);
				d = addUnsigned(d, DD);
			}
			var tempValue = wordToHex(a) + wordToHex(b) + wordToHex(c) + wordToHex(d);
			return tempValue.toLowerCase();
		}
		
		
		var rotateLeft = function(lValue, iShiftBits) {
			return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
		}
		
		var addUnsigned = function(lX, lY) {
			var lX4, lY4, lX8, lY8, lResult;
			lX8 = (lX & 0x80000000);
			lY8 = (lY & 0x80000000);
			lX4 = (lX & 0x40000000);
			lY4 = (lY & 0x40000000);
			lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
			if (lX4 & lY4) return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
			if (lX4 | lY4) {
				if (lResult & 0x40000000) return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
				else return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
			} else {
				return (lResult ^ lX8 ^ lY8);
			}
		}
		
		var F = function(x, y, z) {
			return (x & y) | ((~ x) & z);
		}
		
		var G = function(x, y, z) {
			return (x & z) | (y & (~ z));
		}
		
		var H = function(x, y, z) {
			return (x ^ y ^ z);
		}
		
		var I = function(x, y, z) {
			return (y ^ (x | (~ z)));
		}
		
		var FF = function(a, b, c, d, x, s, ac) {
			a = addUnsigned(a, addUnsigned(addUnsigned(F(b, c, d), x), ac));
			return addUnsigned(rotateLeft(a, s), b);
		};
		
		var GG = function(a, b, c, d, x, s, ac) {
			a = addUnsigned(a, addUnsigned(addUnsigned(G(b, c, d), x), ac));
			return addUnsigned(rotateLeft(a, s), b);
		};
		
		var HH = function(a, b, c, d, x, s, ac) {
			a = addUnsigned(a, addUnsigned(addUnsigned(H(b, c, d), x), ac));
			return addUnsigned(rotateLeft(a, s), b);
		};
		
		var II = function(a, b, c, d, x, s, ac) {
			a = addUnsigned(a, addUnsigned(addUnsigned(I(b, c, d), x), ac));
			return addUnsigned(rotateLeft(a, s), b);
		};
		
		var convertToWordArray = function(string) {
			var lWordCount;
			var lMessageLength = string.length;
			var lNumberOfWordsTempOne = lMessageLength + 8;
			var lNumberOfWordsTempTwo = (lNumberOfWordsTempOne - (lNumberOfWordsTempOne % 64)) / 64;
			var lNumberOfWords = (lNumberOfWordsTempTwo + 1) * 16;
			var lWordArray = Array(lNumberOfWords - 1);
			var lBytePosition = 0;
			var lByteCount = 0;
			while (lByteCount < lMessageLength) {
				lWordCount = (lByteCount - (lByteCount % 4)) / 4;
				lBytePosition = (lByteCount % 4) * 8;
				lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount) << lBytePosition));
				lByteCount++;
			}
			lWordCount = (lByteCount - (lByteCount % 4)) / 4;
			lBytePosition = (lByteCount % 4) * 8;
			lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80 << lBytePosition);
			lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
			lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
			return lWordArray;
		};
		
		var wordToHex = function(lValue) {
			var WordToHexValue = "", WordToHexValueTemp = "", lByte, lCount;
			for (lCount = 0; lCount <= 3; lCount++) {
				lByte = (lValue >>> (lCount * 8)) & 255;
				WordToHexValueTemp = "0" + lByte.toString(16);
				WordToHexValue = WordToHexValue + WordToHexValueTemp.substr(WordToHexValueTemp.length - 2, 2);
			}
			return WordToHexValue;
		};
		
		var uTF8Encode = function(string) {
			string = string.replace(/\x0d\x0a/g, "\x0a");
			var output = "";
			for (var n = 0; n < string.length; n++) {
				var c = string.charCodeAt(n);
				if (c < 128) {
					output += String.fromCharCode(c);
				} else if ((c > 127) && (c < 2048)) {
					output += String.fromCharCode((c >> 6) | 192);
					output += String.fromCharCode((c & 63) | 128);
				} else {
					output += String.fromCharCode((c >> 12) | 224);
					output += String.fromCharCode(((c >> 6) & 63) | 128);
					output += String.fromCharCode((c & 63) | 128);
				}
			}
			return output;
		};
	};

	this.base64 = new function() {
		var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60,
				61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
				-1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35,
				36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
				-1, -1, -1, -1, -1);

		this.encode = function(str) {
			var out, i, len;
			var c1, c2, c3;

			len = str.length;
			i = 0;
			out = "";
			while (i < len) {
				c1 = str.charCodeAt(i++) & 0xff;
				if (i == len) {
					out += base64EncodeChars.charAt(c1 >> 2);
					out += base64EncodeChars.charAt((c1 & 0x3) << 4);
					out += "==";
					break;
				}
				c2 = str.charCodeAt(i++);
				if (i == len) {
					out += base64EncodeChars.charAt(c1 >> 2);
					out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
							| ((c2 & 0xF0) >> 4));
					out += base64EncodeChars.charAt((c2 & 0xF) << 2);
					out += "=";
					break;
				}
				c3 = str.charCodeAt(i++);
				out += base64EncodeChars.charAt(c1 >> 2);
				out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
						| ((c2 & 0xF0) >> 4));
				out += base64EncodeChars.charAt(((c2 & 0xF) << 2)
						| ((c3 & 0xC0) >> 6));
				out += base64EncodeChars.charAt(c3 & 0x3F);
			}
			return out;
		}

		this.decode = function(str) {
			var c1, c2, c3, c4;
			var i, len, out;

			len = str.length;
			i = 0;
			out = "";
			while (i < len) {
				/* c1 */
				do {
					c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
				} while (i < len && c1 == -1);
				if (c1 == -1)
					break;

				/* c2 */
				do {
					c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
				} while (i < len && c2 == -1);
				if (c2 == -1)
					break;

				out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));

				/* c3 */
				do {
					c3 = str.charCodeAt(i++) & 0xff;
					if (c3 == 61)
						return out;
					c3 = base64DecodeChars[c3];
				} while (i < len && c3 == -1);
				if (c3 == -1)
					break;

				out += String.fromCharCode(((c2 & 0XF) << 4)
						| ((c3 & 0x3C) >> 2));

				/* c4 */
				do {
					c4 = str.charCodeAt(i++) & 0xff;
					if (c4 == 61)
						return out;
					c4 = base64DecodeChars[c4];
				} while (i < len && c4 == -1);
				if (c4 == -1)
					break;
				out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
			}
			return out;
		}

		function utf16to8(str) {
			var out, i, len, c;

			out = "";
			len = str.length;
			for (i = 0; i < len; i++) {
				c = str.charCodeAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) {
					out += str.charAt(i);
				} else if (c > 0x07FF) {
					out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
					out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
					out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
				} else {
					out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
					out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
				}
			}
			return out;
		}

		function utf8to16(str) {
			var out, i, len, c;
			var char2, char3;

			out = "";
			len = str.length;
			i = 0;
			while (i < len) {
				c = str.charCodeAt(i++);
				switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					// 0xxxxxxx
					out += str.charAt(i - 1);
					break;
				case 12:
				case 13:
					// 110x xxxx 10xx xxxx
					char2 = str.charCodeAt(i++);
					out += String.fromCharCode(((c & 0x1F) << 6)
							| (char2 & 0x3F));
					break;
				case 14:
					// 1110 xxxx 10xx xxxx 10xx xxxx
					char2 = str.charCodeAt(i++);
					char3 = str.charCodeAt(i++);
					out += String.fromCharCode(((c & 0x0F) << 12)
							| ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				}
			}

			return out;
		}
	}
};

var xsg_qiniu = new function(){
	/**
	 * 如果某个图片上传的地址固定且可以覆盖，需要采用不同的uptoken，例如用户头像，固定的地址都是image/user/${id}/main;
	 * 如果某个图片上传的地址是不固定的，那么可以采用通用uptoken，例如图文编辑器里的图片，每上传一个的地址都会不一样
	 * 在同一个界面里可能存在多个类型的图片上传，为了避免多次从后台获取uptoken，可以先一次性把要的uptoken都查询出来
	 * @param keys,array<String>，需要查询的固定图片key
	 * @param callback,查询成功后的回调函数，传入参数：
	 * 				tokenSet，{
	 * 					commonToken,通用uptoken，
	 * 					tokenMap,是个map，键值是请求参数中的key值
	 * 				}
	 */
	this.getUpTokens = function(keys,callback){
		ajax.request({
	       	 url : _base_url+'/web/common/getQiniuUptokens.do',
	       	need_progressbar:false,
	       	 params : {
	       		 keys : keys == null ? null : keys.join(","),
	       		 need_common : 1
	       	 },
	       	 success : function(header,body){
	       		 var tokenMap = {};
	       		 if(keys != null && keys.length > 0){
	       			 for(var i=0;i<keys.length;i++){
	       				tokenMap[keys[i]] = body.tokens[i];
	       			 }
	       		 }
	       		 var tokens = body.tokens;
	       		 callback && callback.apply(null,[{
	       			 commonToken : body.commonToken,
	       			 tokenMap : tokenMap
	       		 }]);
	       	 }
       });
	};
	/**
	 * 初始化七牛图片上传插件.
	 * @param qiniuInstance,必填,七牛实例，可以采用new QiniuJsSDK()来创建，七牛有一个默认的Qiniu，多个实例需要创建 
	 * @param browse_button,必填，参见七牛参数
	 * @param container,参见七牛参数，默认是document.body
	 * @param path,必填，图片上传路径，相当于七牛概念中的key，支持占位符*,
	 * 				比如'image/user/10001/*',那么表示是非固定路径,将会采用通用uptoken，且每次上传路径中的*会被一个具有唯一性的随机数替代
	 * @param domain，必填，参见七牛参数
	 * @param multi_selection,true/false,是否可以多选，默认可多选
	 * @param tokenSet,参见getUpTokens方法中的应答参数，如果没有当前path对应的uptoken值，则会从后台去查询
	 */
	this.init = function(qiniuInstance,options){
		if(!options.browse_button){
			message.alert('缺少参数 : browse_button');
			return;
		}
		if(!options.path){
			message.alert('缺少参数 : path');
			return;
		}
		if(!options.domain){
			message.alert('缺少参数 : domain');
			return;
		}
		var events = options.events && options.events;
		var path = options.path;
		var isCommonToken = path.indexOf("*")>-1;//有*表示是通用token
		var tokenSet = options.tokenSet;
		var uptoken = tokenSet && (isCommonToken ? tokenSet.commonToken:tokenSet.tokenMap[path]);
		var $container = $("#__qiniuContainer");
		if($container == null || $container.length == 0){
			$container = $("<div>").appendTo($("body")).attr("id","__qiniuContainer");
			$container.hide();
		}
		var uploaderOpt = {
				runtimes: 'html5,flash,html4',      // 上传模式,依次退化
		        browse_button: options.browse_button,         // 上传选择的点选按钮，**必需**
		        get_new_uptoken: false,             // 设置上传文件的时候是否每次都重新获取新的 uptoken
		        domain: options.domain,     // bucket 域名，下载资源时用到，**必需**
		        container: 	$container.get(0),             // 上传区域 DOM ID，默认是 browser_button 的父元素，
		        max_file_size: '5mb',             // 最大文件体积限制
		        flash_swf_url: '../resource/Moxie.swf',  //引入 flash,相对路径
		        max_retries: 1,                     // 上传失败最大重试次数
		        dragdrop: false,                     // 开启可拖曳上传
		        chunk_size: '4mb',                  // 分块上传时，每块的体积
		        auto_start: true,
		        multi_selection : options.multi_selection !== null ? options.multi_selection : true,//默认可以多选
		        init: {
		            'FilesAdded': function(up, files) {
		            	var event = events && events.FilesAdded;
		            	event && event.apply(null,[files]);
		            	//et_qiniu.showUploadProgress(files);
		            },
		            'BeforeUpload': function(up, file) {
		            	//et_qiniu.beginUpload(file);
		            	
		            	var event = events && events.BeforeUpload;
		            	event && event.apply(null,[file]);
		            },
		            'UploadProgress': function(up, file) {
		            	var chunk_size = plupload.formatSize(file.loaded).toUpperCase();
		            	var speed = plupload.formatSize(file.speed).toUpperCase()+"/s";
		            	var event = events && events.UploadProgress;
		            	if(event == null)
		            		return;
		            	event && event.apply(null,[file,file.percent,chunk_size,speed]);
		            },
		            'UploadComplete': function() {
		            	var event = events && events.UploadComplete;
		            	event && event.apply();
		            },
		            'FileUploaded': function(up, file, info) {
		            	var event = events && events.FileUploaded;
		            	if(event == null)
		            		return;
		            	var domain = up.getOption('domain');
		                 var res = JSON.parse(info);
		                 var sourceLink = "http://"+domain +"/" +res.key+'?t='+common.unique();
		                 console.log(sourceLink);
		                 event && event.apply(null,[file,sourceLink,res.key]);
		            },
		            'Error': function(up, err, errTip) {
		            	//return;
		                $('table').show();
		            },
		            'Key': function(up, file) {
		            	var path = options.path;
		                if(path.indexOf("*") > -1){
		                	return path.replace('*',common.unique());
		                }else{
		                	return path;
		                }
		            }
		        }
		};
		if(uptoken){
			uploaderOpt.uptoken = uptoken;//如果有token值则直接使用
			var uploader = qiniuInstance.uploader(uploaderOpt);
		}else{
			this.getUpTokens(isCommonToken?null:[path],function(tokenSet){
				uploaderOpt.uptoken = isCommonToken ? tokenSet.commonToken:tokenSet.tokenMap[path];
				console.log(uploaderOpt.uptoken);
				qiniuInstance.uploader(uploaderOpt);
			});
		}
	};
	
	
};
var xsg_progress = new function(){
	this.showProgressDialog = function(files,completeCallback){
		var $dialog = $("#xsg-uploadpic-dialog");
		var $content = null;
		if($dialog == null || $dialog.length == 0){
			$dialog = $("<div id='xsg-uploadpic-dialog' class='xsg-uploadpic-dialog'></div>").appendTo($('body'));
			$content = $("<div class='xsg-uploadpic-filelist'></div>").appendTo($dialog);
			var $bottombar = $("<div class='xsg-uploadpic-bottombar'><div class='xsg-uploadpic-totaltext'></div></div>").appendTo($dialog);
			var $closeButtn = $("<div class='xsg-uploadpic-close'>完成</div>").appendTo($bottombar);
			$closeButtn.click(function(e){
				$dialog.hide();
				var progressList = $content.children(".xsg-uploadpic-item");
				var urls = [];
				for(var i=0;i<progressList.length;i++){
					urls.push(progressList.eq(i).data('url'));
				}
				completeCallback && completeCallback.apply(null,[urls]);
			});
		}else{
			$dialog = $dialog.eq(0);
			$content = $dialog.children(".xsg-uploadpic-filelist");
			$content.empty();
			$dialog.show();
		}
		$dialog.find(".xsg-uploadpic-totaltext").data('total',files.length);
		$dialog.find(".xsg-uploadpic-totaltext").text("正在上传第1/"+files.length+"张图片...");
		
		var html_arr = [];
		var file ;
		for(var i=0;i<files.length;i++){
			file = files[i];
			html_arr.length = 0;
			html_arr.push('<table id="'+file.id+'" class="xsg-uploadpic-item"><tr>');
			html_arr.push('	<td class="xsg-uploadpic-td-view"><div class="xsg-uploadpic-viewWrap"><img class="xsg-uploadpic-view"/></div></td>');
			html_arr.push('	<td class="xsg-uploadpic-td-name"><div class="xsg-uploadpic-name">'+file.name+'</div></td>');
			html_arr.push('	<td class="xsg-uploadpic-td-size"><div class="xsg-uploadpic-size">'+(plupload.formatSize(file.size).toUpperCase())+'</div></td>');
			html_arr.push('	<td>');
			html_arr.push('		<div id="loadingbar" class="xsg-uploadpic-loadingbar">');
			html_arr.push('			<table style="width:100%;height:100%;position:absolute"><tr>');
			html_arr.push('				<td class="xsg-uploadpic-uploaded">等待上传...</td>');
			html_arr.push('				<td class="xsg-uploadpic-percent"></td>');
			html_arr.push('			</tr></table>');
			html_arr.push('			<div class="xsg-uploadpic-overlay"></div>');
			html_arr.push('		</div>');
			html_arr.push('	</td>');
			html_arr.push('</tr></table>');
			
			var $table = $(html_arr.join("")).appendTo($content);
			
			!function(file){
				var fr = new mOxie.FileReader();
		        fr.onload = function(){
		        	var $progressInfo = $("#"+file.id);
		        	$progressInfo.find(".xsg-uploadpic-view").attr("src",fr.result);
		        }
		        fr.readAsDataURL(file.getSource());
			}(file);
			
		}
	};
	
	this.beginUpload = function(progressId,isScrollTo){
		var $dialog = $("#xsg-uploadpic-dialog");
		var $content = $dialog.children(".xsg-uploadpic-filelist");
		var $progressInfo = $("#"+progressId);
		if($progressInfo == null || $progressInfo.length == 0)
			return;
		$progressInfo.find(".xsg-uploadpic-uploaded").text("开始上传");
		//滚动到对应进度条位置
		if(isScrollTo != false){
			var offsetTop = $progressInfo.position().top+$progressInfo.height();
			if(offsetTop > $content.height()){
				$content.scrollTop($content.scrollTop()+offsetTop - $content.height());
			}
		}
		
	};
	this.updateProgress = function(progressId,percent,uploadedSize,speed){
		var $progressInfo = $("#"+progressId);
		if(percent == "100%"){
			$progressInfo.find(".xsg-uploadpic-overlay").width(0);
			$progressInfo.find(".xsg-uploadpic-loadingbar").css({
				"background-color":"#86F761"
			});
		}else{
			$progressInfo.find(".xsg-uploadpic-overlay").width(percent + "%");
	      	$progressInfo.find(".xsg-uploadpic-percent").text(percent + "%");
	      	$progressInfo.find(".xsg-uploadpic-uploaded").text("已上传:"+uploadedSize);
		}
		
		var index = $progressInfo.parent().children().index($progressInfo)+1;
		var $totaltext = $("#xsg-uploadpic-dialog").find('.xsg-uploadpic-totaltext');
		$totaltext.text("正在上传第"+index+"/"+$totaltext.data('total')+"张图片...");
	}
	this.completeProgress = function(progressId,url){
		this.updateProgress(progressId,'100%');
		$("#"+progressId).data('url',url);
		
		var $totaltext = $("#xsg-uploadpic-dialog").find('.xsg-uploadpic-totaltext');
		$totaltext.text("共成功上传"+$totaltext.data('total')+"张图片");
	}
}


//############### Array
Array.prototype.has = function(str){
	for(var i=0;i< this.length;i++){
		if(this[i] == str)
			return true;
	}
	return false;
}

//############### String
String.prototype.endWith = function(str){
	return this.lastIndexOf(str) + str.length == this.length;
}
String.prototype.startWith = function(str){
	return this.indexOf(str) == 0;
}
String.prototype.ltrim = function(){
	return this.replace(/(^\s*)/g, "");
}
String.prototype.rtrim = function(){
	return this.replace(/(\s*$)/g, "");
}
String.prototype.trim = function(){
	return this.rtrim().ltrim();
}
//只支持yyyy-mm-dd hh:MM:ss格式
String.prototype.toDate = function(splitChar){
	splitChar = splitChar || "-";
	var arr = this.split(" ");
	var dateStr = arr[0];
	var arrDate = dateStr.split(splitChar);
	
	var year = parseFloat(arrDate[0]);
	var month = parseFloat(arrDate[1])-1;
	var date = parseFloat(arrDate[2]);
	//alert(year+"/"+month+"/"+date)
	var hour = 0;
	var minute = 0;
	var second = 0;
	if(arr.length > 1){
		//还有时间精度
		var arrTime = arr[1].split(":");
		hour = parseFloat(arrTime[0]);
		minute = parseFloat(arrTime[1]);
		second = parseFloat(arrTime[2]);
	}
	return new Date(year,month,date,hour,minute,second);
}
String.prototype.toObject = function(){
	return JSON.parse(this);
}
//###############Date
Date.prototype.toStr = function(pattern){
	pattern = pattern || "yyyy-MM-dd hh:mm:ss"
	var result = pattern.replace("yyyy",this.getFullYear())
		   				  .replace("MM",(this.getMonth() < 9 ? "0":"")+(this.getMonth()+1))
		   				  .replace("dd",(this.getDate() < 10 ? "0":"")+(this.getDate()))
		   				  .replace("hh",(this.getHours() < 10 ? "0":"")+(this.getHours()))
		   				  .replace("mm",(this.getMinutes() < 10 ? "0":"")+(this.getMinutes()))
		   				  .replace("ss",(this.getSeconds() < 10 ? "0":"")+(this.getSeconds()));
	return result;
	
}
Date.prototype.getWeekDay = function(){
	var days = ["日","一","二","三","四","五","六"];
	return "星期"+days[this.getDay()];
}
Date.prototype.offsetDate = function(offset){
	if(offset == 0)
		return this;
	var year = this.getFullYear();
	var month = this.getMonth();
	var date = this.getDate()+offset;
	
	var hour = this.getHours();
	var minute = this.getMinutes();
	var second = this.getSeconds();
	
	return new Date(year,month,date,hour,minute,second);
}
Date.prototype.offsetMonth = function(offset){
	if(offset == 0)
		return this;
	var year = this.getFullYear();
	var month = this.getMonth()+offset;
	var date = this.getDate();
	
	var hour = this.getHours();
	var minute = this.getMinutes();
	var second = this.getSeconds();
	
	return new Date(year,month,date,hour,minute,second);
}
//当前日期所在星期/月份的第一天
Date.prototype.getFirstDay = function(during){
	if(during == "week"){
		var day = this.getDay();
		day = day == 0 ? 7 : day;//周日0转成7
		return this.offsetDate((day-1)*-1);
	}else if(during == "month"){
		var date = this.getDate();
		return this.offsetDate((date-1)*-1);
	}
}
//当前日期所在星期/月份的最后一天
Date.prototype.getLastDay = function(during){
	if(during == "week"){
		var day = this.getDay();
		day = day == 0 ? 7 : day;//周日0转成7
		return this.offsetDate(7-day);
	}else if(during == "month"){
		var nextmonthFirstDay = this.offsetMonth(1).getFirstDay("month");
		return nextmonthFirstDay.offsetDate(-1);
	}
}
