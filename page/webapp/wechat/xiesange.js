var _base_host = common.getHost();
var _base_url = common.getHost()+"/xiesange";
var _sysparam = null;//系统参数，map类型
var speeda_url = "http://api.speeda.cn/openapi/common/route?appkey=5733f8f0c941a46d2bedb0d7";
var speeda_phone = '4009095543';
var image_url = "http://resource.xiesange.com/image/";
var logoUrl = "http://resource.xiesange.com/image/sys/logo.jpg";
var page_count = 10;
var menus_cust = [{
		text :'首页',
		iconfont : '&#xe60a;',
		handler : function(){
			//清空shoppingcart
			addShoppingCart(null);
			common.gotoPage((menuPath() || "..")+"/product/product-list.html")
		}
	},{  
		text :'我的订单',
		iconfont : '&#xe600;',
		handler : function(){
			common.gotoPage((menuPath() || "..")+"/order/order-list.html")
		}
	},{
		text :'优惠券',
		iconfont : '&#xe60b;',
		handler : function(){
			common.gotoPage((menuPath() || "..")+"/welfare/coupon.html")
		}
	},{
		text :'联系老板',
		iconfont : '&#xe6d8;',
		handler : function(){
			common.gotoPage((menuPath() || "..")+"/service/contact.html")
		}
	}
];

var menus_manage = [{
	text :'产品管理',
	iconfont : '&#xe60f;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/manage/m-prod.html")
	}
},{
	text :'订单管理',
	iconfont : '&#xe606;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/manage/m-order-list.html")
	}
},{
	text :'采购管理',
	iconfont : '&#xe60e;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/manage/m-purchase-list.html")
	}
},{
	text :'客户管理',
	iconfont : '&#xe60c;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/manage/m-user.html")
	}
},{
	text :'团购管理',
	iconfont : '&#xe61c;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/manage/m-groupbuy-list.html")
	}
},{
	text :'文章管理',
	iconfont : '&#xe89d;',
	handler : function(){
		common.gotoPage((menuPath() || "..")+"/article/article-list.html")
	}
}];

function div($parent,clazz){
	var $div = $("<div>");
	if($parent != null){
		$div.appendTo($parent);
	}
	if(clazz && typeof clazz == 'string'){
		$div.addClass(clazz);
		return $div;
	}
	
	if(clazz && typeof clazz == 'object'){
		$div.css(clazz);
		return $div;
	}
	
	return $div;
}

function menuPath(){
	var url = window.location.href;
	if(url.indexOf("file:") == 0){
		return null;
	}else{
		return common.getHost()+"/wechat";
	}
}

var iconfontset = {
	menu : '&#xe601;',
	close : '&#xe604;',
	check : '&#xe613;',
	filter : '&#xe610;',
	star_empty : '&#xe61d;',
	star : '&#xe61e;',
	profile_pic:"&#xe60c;",
	arrow : '&#xe605;',
	arrow_down : '&#xe715;',
	delivery:'&#xe609;',
	tag:'&#xe614;',
	cust_service:'&#xe714;',
	wechat:'&#xe617;',
	alipay:'&#xe618;',
	adminOp : '&#xe684;',
	groupbuyList : '&#xe76a;',
	add:"&#xe858;",
	customer : '&#xe619;',
	contact_boss:"&#xe6d8;",
	article_manage : "&#xe89d;"
}

var promotionIcon = {
	coupon : {
		text:"券",
		color:"#f5ca07"
	},
	express : {
		iconfont:iconfontset.delivery,
		color:"#949494"
	},
	preorder : {
		text:"定",
		color:"#fd71ff"
	},
	wechat : {
		iconfont:iconfontset.wechat,
		color:"#5dca32"
	},
	alipay : {
		iconfont:iconfontset.alipay,
		color:"#059ae3"
	},
	1 : {
		text:"首",
		color:"#04d804"
	},
	2:  {
		text:"满",
		color:"#08d0b2"
	},
	3:  {
		text:"砍",
		color:"#f55934"
	},
	4:  {
		text:"团",
		color:"#25a1ab",
		handler : function(){
			common.gotoPage("wechat/product/groupbuy.html");
		}
	},
	5:  {
		text:"拼",
		color:"#fd9603",
		handler : function(promEntity,$promItem,prodEntity){
			common.gotoPage((menuPath() || "..")+"/product/groupbuy.html?product_id="+prodEntity.id);
			
			//common.gotoPage("wechat/product/groupbuy.html?product_id="+prodEntity.id);
		}
	}
}

function getMenus(){
	var loginUser = common.getLoginUser();
	if(loginUser && loginUser.isAdmin == 1){
		return menus_cust.concat("-",menus_manage);//-表示分隔线
	}else{
		return menus_cust;
	}
}
//当前登录用户是否管理员
function isAdmin(){
	var loginUser = common.getLoginUser();
	return loginUser && loginUser.isAdmin == 1;
}
function setSysparam(val,flag){
	common.session("__sysparam",JSON.stringify(val));
	common.session("__sysparam_flag",flag || "")
}
function getSysparam(key){
	var json = common.session("__sysparam");//
	return !json ? null : JSON.parse(json)[key];
}
function getSysparamFlag(){
	return common.session("__sysparam_flag") || null;//
}

function setEnum(val,flag){
	common.session("__enum",JSON.stringify(val));
	common.session("__enum_flag",flag || "")
}
function getEnum(key){
	var json = common.session("__enum");//
	return !json ? null : JSON.parse(json)[key];
}
function getEnumFlag(){
	return common.session("__enum_flag") || null;//
}

//显示右上角的菜单
function initPopupMenu($menuBttn,direction,menus,title){
	widget.bindTouchClick($menuBttn,function(){
		var $popupMenu = $menuBttn.data("popupmenu");
		$popupMenu = widget.popupMenu($popupMenu,direction,menus,{need_titlebar:title});
		$menuBttn.data("popupmenu",$popupMenu);
	});
}

//同步更新，如果数据标识没变化，则表示基础数据没变化，则不覆盖
function updateBaseData(responseBody){
	if(responseBody.baseparamFlag != getSysparamFlag()){
		setSysparam(responseBody.baseparam,responseBody.baseparamFlag);
	}
	if(responseBody.enumFlag != getEnumFlag()){
		setEnum(responseBody.enum,responseBody.enumFlag);
	}
}
function updateAuth(authInfo){
	if(authInfo.user){
		common.setAuth(authInfo.token,authInfo.skey,authInfo.user);
	}
}
function checkAuth(callback,needCreateNew){
	if(common.getToken()){
		ajax.request({
			url : _base_url+"/web/auth/loginByToken.do",
			need_progressbar : false,
			params : {
			},
			success : function(header,body){
				if(body.user){
					common.setAuth(body.token,body.skey,body.user);
				}
				callback && callback.apply(null,[body.user,body.openid,body.token,body.skey]);
			}
		});
	}else{
		//如果微信链入，则进行oauth验证
		var oauthCode = common.getUrlParams("code");// 如果是通过oauth进来的，这个code是微信自动附加上的；不是oauth进来就不会有code
		if (!oauthCode) {
			message.alert("请从微信界面里打开");
			return null;
		}
		ajax.request({
			url : _base_url+"/web/auth/loginByOAuthCode.do",
			need_progressbar : false,
			params : {
				oauth_code : oauthCode,
				need_create_new : needCreateNew === false ? 0 : 1
			},
			success : function(header,body){
				if(body.user){
					common.setAuth(body.token,body.skey,body.user);
				}
				callback && callback.apply(null,[body.user,body.openid,body.token,body.skey]);
			}
		});
	}
	
}
function gotoLogin(openid){
	var str = "../login/login.html?redirect="+encodeURIComponent(window.location.href);
	if(openid){
		str += "&openid="+openid;
	}
	common.gotoPage(str);
}

function enableBttn($bttn){
	if(arguments.length == 1){
		//判断按钮是否可行
		return !$bttn.hasClass("disable-bttn");
	}else if(arguments[1]){
		//把按钮置为可用状态
		$bttn.removeClass("disable-bttn")
	}else if(!arguments[1]){
		//把按钮置为不可用状态
		$bttn.addClass("disable-bttn")
	}
}
function buildDeliveryDate(curDate){
	var curTime = curDate.split(" ")[1];
	curDate = curDate.toDate();//转成Date对象
	//截止时间之前，明天发货;超过截止时间，后天发货
	curDate = curDate.offsetDate(curTime > getSysparam("order_deadline") ? 2 : 1);
	return curDate;
}
function buildDeliveryDesc(curDate){
	return "";//现在下单，预计"+buildDeliveryDate(curDate).toStr('yyyy-MM-dd')+"上午发货,当天下午到货。";
}

function initTouchClass($obj,touchedClass,clickFunc){
	widget.initTouchable($obj,function(e){
		e.stopPropagation();
		$obj.addClass(touchedClass);
	},null,function(e,offsetX,offsetY){
		e.stopPropagation();
		$obj.removeClass(touchedClass);
		if(!clickFunc)
			return;
		if(Math.abs(offsetX) < 3 && Math.abs(offsetY) < 3){
			//偏移量超过3px就表示不是点击，而是触摸移动了，所以只有小于3才认为点击
			clickFunc && clickFunc.apply();
		}
	});
}

function checkSwitch(needContent){
	if(getSysparam("order_switch") != 1){
		if(needContent !== false){
			message.alert("系统升级中，暂时不接受预订！如给您带来不便,敬请谅解。",function(){
				common.gotoPage((menuPath() || "..")+"/service/contact.html");
			});
		}
		return false;
	}
	return true;
}

function initTitlebarBttns($titlebar,bttnEntityList){
	if(!(bttnEntityList instanceof Array)){
		bttnEntityList = [bttnEntityList];
	}
	var $bttnArea = $('<div class="titlebar-bttn-area">').appendTo($titlebar);
	for(var i=0;i<bttnEntityList.length;i++){
		var bttnEntity = bttnEntityList[i];
		var $bttn = $('<div class="titlebar-bttn"></div>').appendTo($bttnArea);
		$bttn.data("entity",bttnEntity);
		if(bttnEntity.class_name){
			$bttn.addClass(bttnEntity.class_name);
		}
		if(bttnEntity.iconfont){
			$bttn.html(iconfontset[bttnEntity.iconfont]);
			$bttn.addClass("xsg-fontset");
		}
		
		if(bttnEntity.icon){
			$bttn.css({
				"background":"url("+bttnEntity.icon+") no-repeat center center"
			});
		}
		if(bttnEntity.text){
			$bttn.html(bttnEntity.text);
		}
		if(bttnEntity.handler){
			//说明是点击直接有事件
			widget.bindTouchClick($bttn,function(e){
				e.stopPropagation();
				var entity = $(this).data("entity");
				entity.handler.apply(entity,[e]);
			});
		}else{
			//说明点击是需要弹出菜单
			initPopupMenu($bttn,"right left",bttnEntity.menu_items);
		}
	}
}

/**
 * 把某个实体对象的值都赋值到对应的元素框里去
 * @param entity,对象实体
 * @param $items，需要赋值的元素列表，注意这些元素必须有entityKey属性，这个属性表明该元素要取用实体里的什么值
 */
function showEntityValue(entity,$items){
	var $item = null;
	var key = null;
	for(var i=0;i<$items.length;i++){
		$item = $items.eq(i);
		key = $item.attr("entityKey");
		key = key.split(":")[0];//:前第一个时候取值键名，第二个是赋值键名
		$item.data("origValue",entity[key]);
		if($item.get(0).tagName == 'INPUT' || $item.get(0).tagName == 'TEXTAREA'){
			$item.val(entity[key]);
		}else{
			$item.text(entity[key]);
		}
	}
}
/**
 * 和showEntityValue配套使用；
 * 把元素框里的值如果有修改过的构建成一个对应实体。每个元素框赋值后所对应的键名来自该元素框所配置的entityKey属性.
 * entityKey属性可以配置两部分，用:分隔，比如："costPrice:cost_price",前半部分表示显示的时候从实体里取用，
 * 第二部分表示构建的时候设置到entity实体里的键名
 * @param entity,对象实体
 * @param $items，需要赋值的元素列表，注意这些元素必须有entityKey属性，这个属性表明该元素要取用实体里的什么值
 */
function buildEntityValue($items){
	var $item = null;
	var key = null;
	var newValue = null;
	var result = {};
	for(var i=0;i<$items.length;i++){
		$item = $items.eq(i);
		key = $item.attr("entityKey");
		key = key.indexOf(":")==-1 ? key : key.split(":")[1];//:前第一个时候取值键名，第二个是赋值键名
		if($item.get(0).tagName == 'INPUT' || $item.get(0).tagName == 'TEXTAREA'){
			newValue = $item.val();
		}else{
			newValue = $item.data("newValue");
		}
		var origValue = $item.data("origValue")===undefined ? '':$item.data("origValue");
		if(newValue!==undefined && origValue != newValue){
			result[key] = newValue;
		}
	}
	return result;
}

function initPagination($nextPage,pageCount,callback){
	//分页点击
	$nextPage.data("page_count",pageCount);
	widget.bindTouchClick($nextPage,function(e){
		if(!enableBttn($(this))){
			return;
		}
		enableBttn($nextPage,false);
		callback.apply(null,[$nextPage.data("page_index")]);
	});
}

function updatePagination($nextPage,dataCount){
	enableBttn($nextPage,true);
	dataCount = dataCount || 0;
	var pageCount = $nextPage.data("page_count");
	var startIndex = $nextPage.data("page_index") || 0;
	$nextPage.data("page_index",startIndex+pageCount);//下一次开始的下标
	
	if(dataCount < pageCount){
		if(startIndex > 0){
			var $titlebar = $($nextPage.attr("titlebar"));
			message.successHide("没有更多数据了",$titlebar);
		}
		$nextPage.text('没有更多数据了');
	}else{
		$nextPage.text('点击加载更多数据...');
	}
}

function buildCouponText(coupon){
	var str = null;
	if(coupon.type == 1){
		//折扣券
		str = (coupon.value/10)+"折券";
	}else if(coupon.type == 2){
		//现金券
		str = coupon.value+"元券";
	}
	//str += "("+coupon.expireTime+"过期)";
	return str;
}

function buildPromotionText(promotion){
	if(!promotion){
		return "[团购方案已下线]" 
	}
	var custs = promotion.conditionCusts;
	var amount = promotion.conditionAmount;
	var value = promotion.value;
	return custs+"人团，"+amount+"斤起售，团购价:￥"+value+"/斤";
}

function getProduct(prodId,prodList){
	for(var k=0;k<prodList.length;k++){
		if(prodList[k].id == prodId){
			return prodList[k];
		}
	}
}
//显示收件人界面
function showDeliveryPage($page,deliveryInfo,commitHandler){
	if($page.attr("is_init") != "1"){
		initDeliveryPage($page,deliveryInfo,commitHandler);
		$page.attr("is_init",1);
	}
	
	var $titlebar = $page.find(".titlebar");
	var loginUser = common.getLoginUser();
	if(loginUser.mobile){
		//如果当前登录者验证过手机号码，那么直接填写收件人信息即可
		deliveryInfo = deliveryInfo || {
			mobile : loginUser.mobile,
			name : loginUser.name,
			address : loginUser.address
		}
		showDeliveryContainer($page,deliveryInfo);
	}else if(!loginUser.mobile){
		$page.find(".footbar").hide();
		$page.find(".delivery-self-container").show();
		//下单者手机号为验证，需要先验证
		var $vcodeSet = VCodeSet.init($(".vcodeset-container"),{
			success : function($mobile,$vcodeInput,$vcodeBttn){
				message.successHide("验证码已发送至"+$mobile.val(),$titlebar);
			},
			error : function(errorMsg){
				message.errorHide(errorMsg,$titlebar);
			},
			handler : function($mobile,$vcodeInput,$vcodeBttn){
				ajax.request({
					url : _base_url+"/web/user/modify.do",
					params : {
						mobile : $mobile.val(),
						vcode : $vcodeInput.val()
					},
					need_progressbar : $titlebar,
					success : function(header,body){
						$page.find(".delivery-self-container").hide();
						loginUser.mobile = $mobile.val();
						common.setAuth(false,false,loginUser);
						
						showDeliveryContainer($page,{
							mobile : loginUser.mobile,
							name : loginUser.name,
							address : loginUser.address
						});
					},
					complete :function(data,textStatus){
					}
				});
			}
		});
	}
	enableBttn($page.find(".footbar-bttn-ok"),true);
	widget.slide($page, "left right",400);
}
function initDeliveryPage($page,deliveryInfo,commitHandler){
	$page.css("z-index",9999);
	//右上角菜单关闭按钮
	var $titlebar = $page.find(".titlebar.linkinfo");
	initTitlebarBttns($titlebar,{
		iconfont : 'close',
		handler:function(){
			$page.hide();
		}
	});
	
	//取消按钮
	widget.bindTouchClick($page.find(".footbar-bttn-cancel"),function(e){
		$page.hide();
	});
	//确认按钮
	widget.bindTouchClick($page.find(".footbar-bttn-ok"),function(e){
		commitDeliveryPage($page,commitHandler);
	});
	widget.initTouchable($page,null,function(offsetX,offsetY, h_direction, v_direction){
		return false;
	});
	$page.find(".item-input").focus(function(){
		$(this).removeClass('error-font');
	});
}
function showDeliveryContainer($page,deliveryInfo){
	var $deliveryContainer = $page.find(".delivery-container");
	$deliveryContainer.find(".deliverypage-mobile").val(deliveryInfo.mobile);
	$deliveryContainer.find(".deliverypage-linkman").val(deliveryInfo.name);
	$deliveryContainer.find(".deliverypage-address").val(deliveryInfo.address);
	$deliveryContainer.show();
	$page.find(".footbar").show();
}
function commitDeliveryPage($page,commitHandler){
	var $bttn = $page.find(".footbar-bttn-ok");
	if($bttn.hasClass('disable-bttn')){
		return;
	}
	
	var $titlebar = $page.find(".titlebar");
	
	var $mobile = $page.find(".deliverypage-mobile");
	var $name = $page.find(".deliverypage-linkman");
	var $addr = $page.find(".deliverypage-address");
	
	var hasError = 0;
	if(!common.checkMobile($mobile.val())){
		message.errorHide("请输入正确的手机号",$titlebar);
		return;
	}
	
	if(!$name.is(":hidden") && !$name.val()){
		message.errorHide("请输入收件人姓名",$titlebar);
		return;
	}
	
	if(!$addr.is(":hidden") && !$addr.val()){
		message.errorHide("请输入收件人地址",$titlebar);
		return;
	}
	$bttn.addClass('disable-bttn');
	
	commitHandler.apply(null,[{
		name : $name.val(),
		mobile : $mobile.val(),
		address : $addr.val()
	},function(){
		var deliveryInfo = $page.data("deliveryInfo");
		deliveryInfo.name = $name.val();
		deliveryInfo.mobile = $mobile.val();
		deliveryInfo.address = $addr.val();
	},function(data,textStatus){
		/*if(data.status == 200 && data.responseJSON.header.error_code == 0)
			return;//处理成功不需要移除disable样式，因为不能再重复提交*/
		$bttn.removeClass("disable-bttn");
	}]);
}

function initDeliveryBar($deliveryBar,$deliveryPage,deliveryInfo,commitHandler){
	if($deliveryPage != null){
		$deliveryPage.data("deliveryInfo",deliveryInfo);
		//点击事件
		widget.bindTouchClick($deliveryBar,function(e){
			showDeliveryPage($deliveryPage,deliveryInfo,commitHandler);
		});
	}else{
		$deliveryBar.find(".deliverybar-arrow").hide();
	}
	refreshDeliveryBar($deliveryBar,deliveryInfo.name,deliveryInfo.mobile,deliveryInfo.address);
}
function refreshDeliveryBar($deliveryBar,name,mobile,address){
	if(address){
		$deliveryBar.find(".deliverybar-address").text(address);
	}
	
	if(mobile){
		$deliveryBar.find(".deliverybar-person .mobile").text(mobile);
	}
	
	if(name){
		$deliveryBar.find(".deliverybar-person .name").text(name);
	}
	var needFillup = !address || !mobile || !name;
	if(needFillup){
		$deliveryBar.find(".deliverybar-person .fillup").show();
		
	}else{
		$deliveryBar.find(".deliverybar-person .fillup").hide();
	}
	
	//根据地址栏动态调整列表高度
	//var $orderlist = $(".ordercfm-item-list");
	//$orderlist.css("top",$('.titlebar.confirm').height()+$deliveryTable.height());
	
}


var VCodeSet = new function(){
	/**
	 * 初始化短信验证码组件
	 * $container,vcode组件需要放入的父容器对象
	 * options:
	 * 	  vcode_length,验证码长度，默认4位
	 * 	  next_button,boolean,是否需要下一步按钮
	 * 	  success，function，验证码发送成功回调函数
	 * 	  error,funciton，验证码发送失败回调函数
	 * 	  vcode_changed,function,验证码输入框值变化事件
	 * 	  mobile_changed,function,手机输入框值变化事件
	 * 	  handler,function，下一步事件
	 * 	  
	 */
	this.init = function($container,options){
		var $set = $container.children(".vcode-set");
		if($set.length == 0){
			options = options || {};
			//需要先创建
			$set = $('<div class="vcode-set">');
			var $bttnline = $('<div class="vcode-bttn-line">').appendTo($set);
			var $mobileWrap = $('<div class="vcode-mobile-wrap"><input placeholder="请输入您的手机号" type="tel" class="vcode-mobile"/></div>').appendTo($bttnline);
			var $vcodeBttn = $('<div class="xsg-bttn vcode-bttn disable-bttn common-bg">获取验证码</div>').appendTo($bttnline);
			var $vcodeWrap = $('<div class="vcode-input-wrap" style=""><input placeholder="请输入短信验证码" type="tel" class="vcode-input"/></div>').appendTo($set);
			var $nextbttn = options.next_button==false?null:$('<div class="xsg-bttn vcode-next disable-bttn common-bg">下一步</div>').appendTo($set);
			var $mobileInput = $mobileWrap.children('input');
			var $vcodeInput = $vcodeWrap.children('input');
			
			$set.appendTo($container);
			
			
			var vcodeLength = (options && options.vcodeLength) || 4;//验证码长度，默认4位
			//获取验证码点击事件
			widget.bindTouchClick($vcodeBttn,function(e){
				if($vcodeBttn.hasClass('disable-bttn')){
					return;
				}
				var mobile = $mobileInput.val();
				if(!common.checkMobile(mobile)){
					options.error ? options.error.apply(this,["请输入正确的手机号",$mobileInput,$vcodeInput,$vcodeBttn])
							: message.alert("请输入正确的手机号");
					return;
				}
				$vcodeBttn.addClass('disable-bttn');
				ajax.request({
					url : _base_url+"/web/msg/sendVCode.do",
					params : {
						mobile : mobile,
						length : vcodeLength
					},
					need_progressbar : false,
					success : function(header,body){
						$vcodeBttn.addClass("vcode-countdown");//表示进入倒计时
						$vcodeBttn.data("timeleft",59);
						$vcodeBttn.text("59s后重新获取");
						$vcodeBttn.data("mobile",mobile);//当前验证码对应的手机号
						var interval = setInterval(function(){
							var next = $vcodeBttn.data("timeleft")-1;
							if(next == 0){
								$vcodeBttn.removeClass('disable-bttn vcode-countdown');
								$vcodeBttn.text("获取验证码");
								clearInterval(interval)
								return;
							}
							$vcodeBttn.text(next+"s后重新获取");
							$vcodeBttn.data("timeleft",next);
						},1000);
						$vcodeBttn.data("interval",interval);
						$set.find("tr").show();
						$vcodeInput.val("");
						options.success && options.success.apply(null,[$mobileInput,$vcodeInput,$vcodeBttn]);
						$vcodeInput.select();
					}
				});
			});
			
			//手机号实时输入检测事件
			$mobileInput.bind('input propertychange', function() {
				if($vcodeBttn.hasClass("vcode-countdown"))
					return;//如果在倒计时，则不需要做样式变更
				var value = $(this).val();
				var result = options.mobile_changed && options.mobile_changed.apply(null,[$mobileInput,$vcodeInput,$vcodeBttn]);
				if(result === false)
					return;//如果自定义事件返回null则不执行后续默认逻辑
				
				//如果不是验证码场景，那么改变手机号了之后按钮需要重新开发
				if(!common.checkMobile(value)){
					$vcodeBttn.addClass('disable-bttn');
				}else{
					$vcodeBttn.removeClass('disable-bttn');
					$vcodeBttn.text("获取验证码");
				}
			});
			
			$vcodeInput.bind('input propertychange', function() {
				$nextbttn && $nextbttn.removeClass('disable-bttn');
				var handler = options && options.vcode_changed;
				handler && handler.apply(null,[$mobileInput,$vcodeInput,$vcodeBttn]);
			});
			
			//下一步按钮
			if($nextbttn){
				widget.bindTouchClick($nextbttn,function(e){
					if($nextbttn.hasClass('disable-bttn')){
						return;
					}
					var mobile = $mobileInput.val();
					var vcode = $vcodeInput.val();
					if(!common.checkMobile(mobile)){
						options.error ? options.error.apply(this,["请输入正确的手机号",$mobileInput,$vcodeInput,$vcodeBttn])
								: message.alert("请输入正确的手机号");
						return;
					}
					if(!vcode){
						options.error ? options.error.apply(this,["请输入收到的验证码",$mobileInput,$vcodeInput,$vcodeBttn])
								: message.alert("请输入收到的验证码");
						return;
					}
					$nextbttn.addClass('disable-bttn');
					options.handler && options.handler.apply(null,[$mobileInput,$vcodeInput,$vcodeBttn]);
				});
			}
		}
		
		return $set;
	}
	
	/**
	 * 验证输入的值是否正确
	 */
	this.checkValue = function($vcodeSet){
		var $mobile = $vcodeSet.find(".vcode-mobile");
		var $vcode = $vcodeSet.find(".vcode-input");
		
		if(!common.checkMobile($mobile.val())){
			return "请输入正确的手机号";
		}
		
		if(!$vcode.is(":hidden") && !$vcode.val()){
			return "请输入验证码";
		}
		
		if($vcodeSet.find(".vcode-bttn").data("mobile") != $mobile.val()){
			$vcodeSet.find(".vcode-input").parents("tr").show();
			return "该号码尚未获取验证码，请重新输入";
		}
	}
	
	/**
	 * 信任某个手机号，那就要把vcode输入框隐藏掉，以及一系列样式的处理
	 */
	this.trustMobile = function($vcodeSet,mobile){
		$vcodeSet.find(".vcode-mobile").val(mobile);
		$vcodeSet.find(".vcode-input").val("");
		$vcodeSet.find(".vcode-input").parent().hide();
		$vcodeSet.find(".footbar-bttn-ok").removeClass("disable-bttn");
		
		
		var $vcodeBttn = $vcodeSet.find(".vcode-bttn");
		clearInterval($vcodeBttn.data("interval"));//可能之前正在获取验证码
		$vcodeBttn.removeClass("vcode-countdown");
		$vcodeBttn.text("该号码已验证");
		$vcodeBttn.data("mobile",mobile);
	}
}


/**
 * 初始化checkbox组件
 * $container,checkbox组件需要放入的父容器对象
 * options:
 * 	  text,label里的文字
 * 	  handler，function,点击事件
 * 	  checked,boolean,默认是否选中
 *    group_code,有值表示单选,表示组编码
 *    click_area,jquery对象，点击该区域也会触发checkbox的选择事件
 * 	  
 */
var CheckboxSet = new function(){
	this.init = function($container,options){
		var $set = $container.children(".checkbox-set");
		options = options || {};
		if($set.length == 0){
			//需要先创建
			$set = $('<table class="checkbox-set"><tr><td class="checkbox-td"></td><td class="label"></td></tr></table>');
			var $checkbox = $("<div class='checkbox xsg-fontset xsg-font'></div>").appendTo($set.find(".checkbox-td"));
			if(options.text){
				var $label = $set.find(".label");
				$label.text(options.text);
			}
			$set.appendTo($container);
			var groupCode = options.group_code;
			if(groupCode){
				$checkbox.addClass("single");
				$checkbox.addClass("Checkbox-"+options.group_code);
			}
			
			widget.bindTouchClick($set,function(e){
				e.stopPropagation();
				CheckboxSet.select($set);
				options.handler && options.handler.apply($set,[CheckboxSet.isSelected($set)]);
			});
			
			if(options.click_area){
				widget.bindTouchClick(options.click_area,function(e){
					e.stopPropagation();
					CheckboxSet.select($set);
					options.handler && options.handler.apply($set,[$checkbox.hasClass("checked")]);
				});
			}
		}
		$set.data("options",options);
		if(options.checked){
			CheckboxSet.select($set,true);
		}
		return $set;
	}
	
	//选中或者取消
	this.select = function($set,checked){
		var options = $set.data("options");
		if(options.group_code){
			setRadiobox($set,options.group_code,checked);
		}else{
			setCheckbox($set,checked);
		}
	}
	
	this.isSelected = function($set){
		return $set.find('.checkbox').hasClass("checked");
	}
	
	var setCheckbox = function($set,checked){
		var $cbx = $set.find(".checkbox");
		if(checked === true){
			$cbx.html(iconfontset.check);
			$cbx.addClass("checked");
		}else if(checked === false){
			$cbx.removeClass("checked");
			$cbx.empty();
		}else{
			//自动判断，如果选中，则取消；如果未选中则选中
			setCheckbox($set,$cbx.html()=="");
		}
	}
	var setRadiobox = function($set,groupCode){
		var $cbx = $set.find(".checkbox");
		//单选，先把其它取消，再把当前选中
		$(".Checkbox-"+groupCode).html("");
		$cbx.html(iconfontset.check);
		$cbx.addClass("checked");
	}
}

function initNestPageScroll($scrollObj){
	//var $lastItem = $scrollObj.children().last();
	//var bottom = $lastItem.length == 0 ? 0 : $lastItem.position().top + $lastItem.height();
	//var diff = bottom - $scrollObj.height();
	widget.initTouchable($scrollObj,null,function(event,offsetX,offsetY, h_direction, v_direction){
		var $children = $scrollObj.children();
		if($children.length == 0){
			return false;
		}
		var firstItemTop = $children.first().position().top;
		if(v_direction == 'down' && firstItemTop == 0){
			//鼠标往下拖，如果已经在最顶部了，则阻止拖动事件
			return false;
		}
		
		var $lastItem = $children.last();
		var lastItemBottom = $lastItem.position().top+$lastItem.height();
		if(v_direction == 'up' && lastItemBottom <= $scrollObj.height()){
			//往上拖动，如果已经在最底部则阻止事件
			return false;
		}
	});
}

function getSum4ExpressFeeFree(){
	var feerule = getSysparam("order_expressfee_rule");
	var feeruleitems = feerule && feerule.split(",");
	return feeruleitems && feeruleitems[1];
}
function getExpressFee(){
	var feerule = getSysparam("order_expressfee_rule");
	var feeruleitems = feerule && feerule.split(",");
	return parseFloat(feeruleitems && feeruleitems[0]);
}

function queryArticle(code,callback,needRecordView){
	ajax.request({
		url : _base_url+"/web/article/queryDetailByCode.do",
		params : {
			article_code : code,
			record_view : needRecordView === false ? 0 : 1 //不传,默认是要记录的
		},
		need_progressbar : false,
		success : function(header,body){
			callback && callback.apply(null,[body.article && body.article.content]);
		}
	});
}


function PageLoading($container){
	var $loadingDialog = null;
	this.show = function(){
		$loadingDialog = message.showLoading();
	};
	this.hide = function(){
		message.hideDialog($loadingDialog);
		$container && $container.show();//进度框隐藏后，把主界面显露
	};
}
function createSplitline(){
	return $("<div class='separator_h'/>");
}

function showSubscribQR(text,callback){
	var $mask = null;
	var $container = $("<div class='qrcode-container'>").appendTo($("body"));
	var $closeBttn = $("<div class='close gray-font'>关闭</div>").appendTo($container);
	if(text){
		$("<div class='text'>"+text+"</div>").appendTo($container);
	}
	var $sep = $("<div class='separator_h'/>").appendTo($container);
	var $qr = $("<div class='qrcode'><div>").appendTo($container);
	$qr.children("img").attr("src","");
	var imgNode = new Image();
	$container.show();
	imgNode.onload =function(){  
		imgNode.onload =null;
        $(imgNode).appendTo($qr);
        $container.css("top",(window.innerHeight-$container.height())/2);
        $mask = message.showMask();
	} 
	imgNode.src = image_url+"/qrcode/qr_service.png";
	widget.bindTouchClick($closeBttn,function(){
		$container.remove();
		$mask.remove();
		callback && callback.apply();
	});
	
}

function showGZHQRTip(bottom,force){
	return;
	var loginUser = common.getLoginUser();
	if(loginUser && loginUser.isSubscribe == 1){
		return;
	}
	if(force != true && common.session("qrcode_closed") == 1){
		return;
	}
	var $div = $("<div class='gzh_qrcode'>").appendTo($("body"));
	$div.css('bottom',bottom);
	var $title = $("<div class='qrtitle'>").appendTo($div);
	var $img = $("<img class='qrimg'>").appendTo($div);
	var $text = $("<div class='qrtext xsg-font'>").appendTo($div);
	$text.text("关注公众号,海鲜吃到老");
	$title.text("关闭");
	$img.attr("src",image_url+"/qrcode/qr_service.png");
	widget.bindTouchClick($title,function(){
		common.session("qrcode_closed",1);
		$div.remove();
	});
	//上下浮动
	var finalBottom = bottom;
	var floatDiv = function(diff){
		finalBottom += diff; 
		$div.animate({bottom:finalBottom},500,null,function(){
			floatDiv(diff*-1);
		});
	}
	floatDiv(-10);
	
}


var resizePic = function(url,size){
	return url+"?imageMogr2/thumbnail/"+size;
}

var ProductSet = new function(){
	/**
	 * options
	 * 		size,图片尺寸
	 * 		need_group_promotion,boolean
	 * 		pic_slider:boolean,是否轮播样式
	 * 		pic_preivew:jQuery,点击可以预览图片
	 * 		amount_change_callback,编辑状态下，数量变动后的回调函数
	 */
	this.init = function($prodItem,prodEntity,options){
		options = options || {};
		
		initDom($prodItem,prodEntity,options);
		
		var canGroupbuy = prodEntity.canGroupbuy == 1;
		$prodItem.data("entity",prodEntity);
		$prodItem.attr("id","product_"+prodEntity.id);
		
		if(!canGroupbuy){
			$prodItem.find(".prod-groupbuy").remove();
		}
		
		var $pic = $prodItem.find(".prod-pic");
		
		if(options.pic_slider === true){
			var imgUrls = ProductSet.buildPicUrls(prodEntity,prodEntity.picList);
			initUnslider($pic,imgUrls);
			prodEntity.picList = imgUrls;
		}else{
			var picSize = options.size;
			var picUrl = prodEntity.pic;
			
			picUrl = !picSize ? picUrl : resizePic(picUrl,picSize+100);
			var $img = $("<img>").appendTo($pic);
			$img.attr("src",picUrl).css("width","100%");
			
		}
		
		$prodItem.find(".prod-summary").text(prodEntity.summary);
		$prodItem.find(".prod-name").text(prodEntity.name);
		if(prodEntity.spec){
			$prodItem.find(".prod-spec").text("("+prodEntity.spec+")");
		}
		if(prodEntity.premise){
			$prodItem.find(".prod-premise").text(prodEntity.premise+prodEntity.unit+"起售");
		}
		$prodItem.find(".prod-amount").text(prodEntity.amount);
		var $price = $prodItem.find(".prod-price");
		if(prodEntity.price < 0){
			$price.css("color","#FF0000");
			$price.text("价格待定");
			$price.addClass("price-not-sure");
		}else{
			$prodItem.find(".prod-price").text("￥"+prodEntity.price+"/");
			$prodItem.find(".prod-unit").text(prodEntity.unit);
			
			if(prodEntity.origPrice){
				$prodItem.find(".prod-origprice")
						.addClass("gray-font")
						.text("￥"+prodEntity.origPrice+"/"+prodEntity.unit)
						.css("text-decoration","line-through");
			}
		}
		
		if(prodEntity.status == 1){
			$prodItem.find(".prod-sum").text("缺货");
			$prodItem.find(".prod-amount-set").remove();
		}else{
			$prodItem.find(".prod-amount-unit").text(prodEntity.unit);
			if(prodEntity.amount){
				$prodItem.find('.prod-amount-input').val(prodEntity.amount);
				$prodItem.find('.prod-sum').text("￥"+(prodEntity.amount*prodEntity.price).toFixed(2));
			}
		}
		
		if(prodEntity.commentCount){
			$prodItem.find(".comment-amount").text(prodEntity.commentCount+"条评论");
		}else{
			$prodItem.find(".comment").remove();
		}
		$prodItem.find(".sale-amount").text("月销量:"+(prodEntity.totalAmount || 0));
		$prodItem.show();
		
		//产品图片点击事件
		if(options.pic_preview !== false){
			widget.bindTouchClick($pic,function(e){
				var $this = $(e.target);
				if($this.hasClass("xsg-unslider-arrow")){
					return;
				}
				ProductSet.previewPic(prodEntity);
			});
		}
		
		
		return $prodItem;
	}
	this.buildPicUrls = function(prodEntity,picEntityList,needSlogan){
		var picUrlList = [prodEntity.pic];
		if(picEntityList != null){
			for(var k=0;k<picEntityList.length;k++){
				picUrlList.push(picEntityList[k].pic);
			}
		}
		needSlogan && picUrlList.push("http://resource.xiesange.com/image/new/adv/slogan.jpg?t=1");
		return picUrlList;
	}
	this.previewPic = function(entity){
		var picList = entity.picList;
		if(picList === undefined){
			ajax.request({
				url : _base_url+"/web/product/queryPicList.do",
				need_progressbar : false,
				params : {
					product_id : entity.id
				},
				success : function(header,body){
					var picUrlList = ProductSet.buildPicUrls(entity,body.picList,true);
					entity.picList = picUrlList;//设置之后，后续不再从后台查
					xsgwx.viewImages(picUrlList);
				}
			});
		}else{
			xsgwx.viewImages(picList);
		}
	}
	
	this.build = function($container){
		var $prodItems = $container.find(".prod-item");
		if($prodItems.length == 0){
			return;
		}
		var result = {};
		
		var totalAmount = 0;
		var totalSum = 0;
		var prodList = [];//订购的产品列表
		for(var i=0;i<$prodItems.length;i++){
			var $proditem = $prodItems.eq(i);
			var prodEntity = $proditem.data("entity");
			var amount = $proditem.find(".prod-amount-input").val();
			if(amount == "" || amount == 0 || amount == undefined){
				continue;
			}
			prodEntity.amount = amount;
			prodList.push(prodEntity);
			totalAmount += parseInt(amount);
			totalSum += amount * prodEntity.price;
		}
		
		
		return {
			total_amount : totalAmount,
			total_sum : totalSum,
			product_list : prodList || null
		};
	}
	
	var initDom = function($prodItem,prodEntity,options){
		if(options.size){
			$prodItem.find(".pic-td").css({
				width : options.size
			});
			$prodItem.find(".prod-pic").css({
				width : options.size,
				height: options.size
			})
		}
		var $promList = $prodItem.find(".promotion-block");
		var promList = prodEntity.promotionList;//促销规则
		
		initPromotionList($promList,promList,prodEntity);
		
		
		var amountChangeCallback = options.amount_change_callback;
		//数量增减点击事件
		ProductSet.initAmountBttn($prodItem.find(".prod-amount-bttn"),function(isAdd,amount,$item,e){
			if(amount < 0){
				amount = 0;
			}
			$prodItem.find(".prod-sum").text("￥"+(prodEntity.price * amount).toFixed(2));
			amountChangeCallback && amountChangeCallback.apply(null,[$prodItem,prodEntity,amount,e]);
		});
		
		$prodItem.find(".prod-amount-input").focus(function(){
			$(this).select();
		});
		$prodItem.find(".prod-amount-input").blur(function(){
			var $input = $(this);
			var $prodItem = $input.parents('.prod-item');
			var prodEntity = $prodItem.data("entity");
			var amount = parseInt($input.val()) || 0;
			if(amount < 0){
				amount = 0;
				$input.val(0);
			}
			$prodItem.find(".prod-sum").text("￥"+(prodEntity.price * amount).toFixed(2));
			amountChangeCallback && amountChangeCallback.apply(null,[$prodItem,prodEntity,amount]);
			
		});
	}
	
	this.initAmountBttn = function($bttns,callback){
		widget.bindTouchClick($bttns,function(e){
			var $bttn = $(e.target);
			if(!$bttn.hasClass("prod-amount-bttn"))
				return;
			var isAdd = $bttn.hasClass("add");
			var $prodAmountSet = $bttn.parents(".prod-amount-set");
			//var prodEntity = $prodItem.data("entity");
			var $input = $prodAmountSet.find(".prod-amount-input");
			var prevAmount = $input.val() ? parseInt($input.val()) : 0 ;
			if(prevAmount == 0 && !isAdd){
				$input.val(0);
				return;//不能低于0
			}
			$input.val(isAdd ? prevAmount+1 : prevAmount - 1);
			
			//updateOrderProdList(prodEntity,$input.val());
			
			callback && callback.apply($bttn,[isAdd,$input.val(),$prodAmountSet,e])
		});
	}
}

function initPromotionList($promList,promList,prodEntity){
	if(promList == null || promList.length == 0){
		$promList.remove();
		return;
	}
	var result = [];
	for(var i=0;i<promList.length;i++){
		var promotion = promList[i];
		var $promItem = $("<div class='item'>").appendTo($promList);
		result.push($promItem);
		$promItem.attr("id","promotion_"+promotion.type);
		var iconInfo = promotionIcon[promotion.type];
		//promotion = $.extend(iconInfo,promotion);
		$promItem.data("entity",promotion);
		$promItem.data("iconInfo",iconInfo);
		promotion.handler = promotion.handler || iconInfo.handler;
		var $iconArea = $("<div class='icon'></div>").appendTo($promItem);
		if(iconInfo.text){
			$iconArea.text(iconInfo.text);
			$iconArea.css("background-color",iconInfo.color);
			//$("<div>"+iconInfo.text+"</div>").appendTo($iconArea);
		}else if(iconInfo.iconfont){
			$iconArea.addClass("xsg-fontset iconfont").html(iconInfo.iconfont);
			iconInfo.color && $iconArea.css("color",iconInfo.color);
		}
		var $text = $("<span class='text'>"+promotion.text+"</span>").appendTo($promItem);
		
		var $rightpart = $("<span class='right-part'>").appendTo($promItem);
		
		
		var $detailPart = $("<span class='detail'>");
		if(promotion.detail_align == "right"){
			$detailPart.appendTo($rightpart);//靠右的话插在right-part里，作为第一个元素
		}else{
			$rightpart.before($detailPart);//靠左的话，插在rightpart前面
		}
		if(promotion.detail_class){
			$detailPart.addClass(promotion.detail_class);
		}
		if(promotion.detail){
			$detailPart.html(promotion.detail);
		}
		
		
		if(promotion.actionValue){
			var $actionValue = $("<span class='value'>").appendTo($rightpart);
			if(typeof promotion.actionValue == 'number'){
				$actionValue.html("￥"+promotion.actionValue);
				if(promotion.actionValue < 0 ){
					$actionValue.addClass("xsg-font");
					$actionValue.html("-￥"+(-1*promotion.actionValue).toFixed(2));
				}else{
					$actionValue.addClass("money-font");
					$actionValue.html("￥"+promotion.actionValue.toFixed(2));
				}
				$promItem.addClass("action-value");
			}else{
				$actionValue.html(promotion.actionValue);
			}
		}
		
		var $arrow = $("<span class='arrow xsg-fontset gray-font'>").appendTo($rightpart);
		if(promotion.handler){
			$arrow.html(iconfontset.arrow);
			widget.bindTouchClick($promItem,function(e){
				var $promItem = $(e.target);
				var promEntity = $promItem.data("entity");
				promotion.handler.apply(null,[promEntity,$promItem,prodEntity]);
			});
		}else{
			$arrow.html("&nbsp;");
		}
		
	}
	return result;
}


function refreshPromotion(promEntity,$promItem){
	var $promItem = $promItem || $("#promotion_"+promEntity.type);
	if($promItem == null || $promItem.length == 0){
		return;
	}
	
	var $detail = $promItem.find(".detail");
	$detail.html(promEntity.detail);
	
	var $actionValue = $promItem.find(".value");
	
	if(promEntity.type){
		var iconInfo = promotionIcon[promEntity.type];
		var $iconArea = $promItem.find(".icon");
		if(iconInfo.text){
			$iconArea.text(iconInfo.text);
			$iconArea.css("background-color",iconInfo.color);
		}else if(iconInfo.iconfont){
			$iconArea.addClass("xsg-fontset iconfont").html(iconInfo.iconfont);
			iconInfo.color && $iconArea.css("color",iconInfo.color);
		}
	}
	if(promEntity.text){
		$promItem.find(".text").html(promEntity.text);
		//var $text = $("<span class='text'>"+promotion.text+"</span>").appendTo($promItem);
	}
	if(promEntity.actionValue){
		if(typeof promEntity.actionValue == 'number'){
			if(promEntity.actionValue < 0 ){
				$actionValue.removeClass("money-font").addClass("xsg-font");
				$actionValue.html("-￥"+(-1*promEntity.actionValue).toFixed(2));
			}else{
				$actionValue.removeClass("xsg-font").addClass("money-font");
				$actionValue.html("￥"+promEntity.actionValue.toFixed(2));
			}
			$promItem.addClass("action-value");
		}else{
			$actionValue.html(promEntity.actionValue);
		}
	}
}
function buildExpressFeeText(reachSum){
	return reachSum < 0 ? "" : "实付满"+reachSum+"元包邮,不参与优惠活动";
}

function initUnslider($container,imgUrls){
	var $slideDiv = $("<div><ul></ul></div>").appendTo($container);
	var $ul = $slideDiv.children("ul").eq(0);
	for(var i=0;i<imgUrls.length;i++){
		var $li = $("<li class='xsg-pic-li'>").appendTo($ul);
		//var $img = $("<img width='100%'>").appendTo($li).attr("src",imgUrls[i]);
		var imgNode = new Image();
		
		$li.css({
			height:$container.height(),
			width:$container.width(),
			"background-image":"url("+imgUrls[i]+")"
		});
	}
	var slider = $slideDiv.unslider({
		autoplay: true,
		speed:500,
		infinite:true,
		arrows: {
			prev: '<a class="xsg-fontset xsg-unslider-arrow prev">&#xe61f;</a>',
			next: '<a class="xsg-fontset xsg-unslider-arrow next">&#xe605;</a>'
		}
	});
	
	
	return slider;
}

function showCustServiceIcon(){
	var $div = $("<div class='cust_service'>").appendTo($("body"));
	var $icondiv = $("<div class='icon xsg-fontset'>").appendTo($div);
	
	$icondiv.html(iconfontset.cust_service);
	
	widget.bindTouchClick($div,function(){
		var loginUser = common.getLoginUser();
		var userId = loginUser && loginUser.id;
		var nickname = (loginUser && loginUser.name) || "访客";
		var pic = (loginUser && loginUser.pic) || "";
		var tel = (loginUser && loginUser.tel) || "";
		var str = "http://www.xiesange.com/wechat/service/contact.html";
		//str += "&partnerId="+userId+"&uname="+nickname+"&tel="+tel;
		common.gotoPage(str);
	});
}




function addShoppingCart(orderMap){
	common.session("__shoppingcart",orderMap==null?"":JSON.stringify(orderMap));
}

function getShoppingCart(){
	var json = common.session("__shoppingcart");
	return !json ? null : JSON.parse(json);
}

function initWxConfig(signature,shareOptions){
	xsgwx.config(signature,function(){
		xsgwx.showOptionMenu();
		xsgwx.shareTimeline(
				shareOptions.timeline_desc, 
				shareOptions.url, 
				shareOptions.img);
		
		xsgwx.sendAppMessage(
				shareOptions.message_title, 
				shareOptions.message_desc,
				shareOptions.url, 
				shareOptions.img);
	});
}

function buildTransitionUrl(url){
	url = encodeURIComponent(url);
	return _base_host+"/wechat/oauth-transition.html?url="+url;
}

/**
 * 把订购产品字符串形式转成对象
 * 字符串形式为:prod_id1:amount1,prod_id2:amount2,prod_id2:amount2,...
 * @param orderProduct
 * @returns {___anonymous41465_41466}
 */
function buildOrderProductMap(orderProduct){
	if(!orderProduct){
		return null;
	}
	var orderMap = {};
	var prodArr = orderProduct.split(",");
	for(var i=0;i<prodArr.length;i++){
		var itemArr = prodArr[i].split(":");
		orderMap[itemArr[0]] = itemArr[1];
		//productIdStr += ","+itemArr[0];
	}
	return orderMap;
}

function getOrderStatus(orderEntity){
	var status = orderEntity.status;
	if(status == -1){
		return {
			text : '已取消',
			color: '#b3b3b3'
		}
	}else if(status == 0 || status == 98){
		return {
			text : '待支付'+(status == 98 ? '(已调价)':''),
			color: '#FF0000'
		}
	}else if(status == 1){
		var isGroup = orderEntity.buyType==2;
		return {
			text : isGroup ? '团购中':'已支付'
		}
	}else if(status == 11){
		return {
			text : '已截单'
		}
	}else if(status == 2){
		return {
			text : '打包中'
		}
	}else if(status == 3){
		return {
			text : '配送中'
		}
	}else if(status == 99){
		return {
			text : '已完成',
			color: '#b3b3b3'
		}
	}
}
