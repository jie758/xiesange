
var prod_list = null;//所有产品列表
var prod_types = null;//产品分类
var promotion_list = null;
var order_prod_list = [];//已订购的产品列表
var loading = new PageLoading();
var productId;
var orderMap;
function init(){
	loading.show();
	initPage();
}
function initPage(){
	var urlParam = common.getUrlParams();
	var orderId = urlParam.order_id;//如果有值，表示是订单列表"再来一单"过来的
	var oauthCode = urlParam.code;// 如果是通过oauth进来的，这个code是微信自动附加上的；不是oauth进来就不会有code
	orderMap = buildOrderProductMap(urlParam.order_product) || {};
	ajax.request({
		url : _base_url+"/web/product/queryIndexList.do",
		need_progressbar : false,
		params : {
			order_id : orderId,
			need_oauth : true,
			basedata:true,
			need_wx_signature:true
		},
		success : function(header,body){
			prod_list = body.productList;
			prod_types = body.productTypeList;
			promotion_list = body.promotionList;
			initIndex();
			showGZHQRTip(120);
			
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/product/product-list.html"),
				img : logoUrl,
				timeline_desc : "#蟹三哥# 从海洋到厨房，只需18小时！我们的海鲜，鲜到崩溃",
				message_title : "#蟹三哥,东海野生海鲜工坊# ",
				message_desc : "从海洋到厨房，只需18小时！我们的海鲜，鲜到崩溃"
			});
			
		},
		complete : function(){
			$(".index-page").show();
			loading.hide();
		}
	});
	//showCustServiceIcon();
}
function initIndex(){
	var isOn = checkSwitch();
	
	initTitlebarBttns($(".index-page .titlebar"),[{
		iconfont : 'menu',
		menu_items : getMenus()
	}]);
	
	initPromotionList($(".index-page .promotion-block.global"),promotion_list);
	initProdTypeFilterbar();
	
	var totalSum = initIndexProdList();
	
	//下单点击事件
	var $orderbttn = $(".prod-order-bttn");
	if(!isOn){
		$orderbttn.addClass("disable-bttn");
	}else{
		widget.bindTouchClick($orderbttn,function(e){
			if(!checkSwitch(false)){
				return;
			}
			var orderInfo = ProductSet.build($(".index-page .prod-list"));
			if(!orderInfo.product_list || orderInfo.product_list.length==0){
				message.errorHide("请先选择产品",null);
				return;
			}
			var url = "../order/order-confirm.html";
			
			//var orderMap = {};
			var orderProdInfo = "";
			for(var i=0;i<orderInfo.product_list.length;i++){
				var prod = orderInfo.product_list[i];
				orderProdInfo += ","+prod.id+":"+prod.amount;
			}
			url += "?order_product="+orderProdInfo.substr(1);
			var orderId = common.getUrlParams('order_id');
			if(orderId){
				url += "&order_id="+orderId;
			}
			common.gotoPage(url);
		});
	}
	
	//拼单点击
	widget.bindTouchClick($(".prod-list .prod-groupbuy"),function(e){
		var $item = $(e.target).parents(".prod-item");
		common.gotoPage("groupbuy.html?product_id="+$item.data("entity").id);
	});
	
	$(".prod-footbar .prod-totalsum").text("￥"+totalSum.toFixed(2));//首页的总金额也要更新
}

//更新已选产品，记录下俩
function sessionProd(orderInfo){
	var orderInfo = orderInfo || ProductSet.build($(".index-page .prod-list"));
	var orderMap = {};
	for(var i=0;i<orderInfo.product_list.length;i++){
		var prod = orderInfo.product_list[i];
		orderMap[prod.id] = prod.amount;
	}
	//addShoppingCart(orderMap);
}

function initIndexProdList(){
	var $tempDiv = $("#_prod_item_temp");
	var $listDiv = $(".prod-list");
	var totalSum = 0;
	for(var i=0;i<prod_list.length;i++){
		var prodEntity = prod_list[i];
		prodEntity.amount = orderMap && orderMap[prodEntity.id];
		var $newDiv = $tempDiv.clone().appendTo($listDiv);
		ProductSet.init($newDiv,prodEntity,{
			size:100,
			amount_change_callback : function($prodItem,prodEntity,amount){
				var result = ProductSet.build($listDiv);
				var $sumDiv = $(".prod-footbar").find(".prod-totalsum");
				$sumDiv.text("￥"+result.total_sum.toFixed(2));
				
				//sessionProd();//数量变更后需要存储到session
			}
		});
		if(prodEntity.amount > 0){
			totalSum += prodEntity.amount * prodEntity.price;
		}
		$newDiv.show();
	}
	
	//评论查看事件
	widget.bindTouchClick($listDiv.find(".prod-stat"),function(e){
		var $item = $(e.currentTarget).parents(".prod-item");
		var entity = $item.data("entity");
		if(!entity.commentCount){
			return;
		}
		common.gotoPage("comment-list.html?product_id="+entity.id);
		e.stopPropagation();
	});
	
	//查看产品详情
	widget.bindTouchClick($listDiv.find(".detail-td"),function(e){
		if($(e.currentTarget).hasClass("comment")){
			return;
		}
		var $item = $(e.currentTarget).parents(".prod-item");
		var entity = $item.data("entity");
		
		common.gotoPage("product-detail.html?product_id="+entity.id);
	});
	
	$listDiv.attr("is_init",1);
	return totalSum;
}

function initProdTypeFilterbar(){
	var $typebar = $(".index-page .prod-catalog");
	for(var i=0;i<prod_types.length;i++){
		var $type = $("<div class='type'>"+prod_types[i].name+"</div>").appendTo($typebar);
		$type.data("entity",prod_types[i]);
	}
	
	var $alltypes = $typebar.find(".type");
	
	widget.bindTouchClick($typebar.find(".type"),function(e){
		var $this = $(e.target);
		var entity = $this.data("entity");
		
		if($this.hasClass("common-bg")){
			//点击表示取消过滤
			$this.removeClass("common-bg");
			filterProd()
		}else{
			$alltypes.removeClass("common-bg");
			$this.addClass("common-bg");
			filterProd(entity.id)
		}
		
	});
}

function filterProd(typeId){
	var $proditems = $(".prod-list .prod-item");
	if(typeId == null){
		$proditems.show();
		return;
	}
	for(var i=0;i<$proditems.length;i++){
		var $prod = $proditems.eq(i);
		var entity = $prod.data('entity');
		if(entity.typeId == typeId){
			$prod.show();
		}else{
			$prod.hide();
		}
	}
	
}



/*function closeConfirmPage(){
	$(".ordercfm").hide();
}*/

//如果后端产品价格做了变动，这里需要重新刷前台的产品价格
function refreshProductList(prodList){
	prod_list = prodList;//先更新全局数组
	
	order_prod_list = [];
	//第一部分，确认界面
	var $proditems = $(".ordercfm-item-list .ordercfm-item");
	var oldProdEntity = null;
	var newProdEntity = null;
	var $prodItem = null;
	var sum = null;
	if($proditems != null && $proditems.length > 0){
		for(var i=0;i<$proditems.length;i++){
			$prodItem = $proditems.eq(i);
			oldProdEntity = $prodItem.data("entity");
			newProdEntity = common.matchItem(oldProdEntity.id,prod_list,'id');
			newProdEntity.amount = oldProdEntity.amount;
			order_prod_list.push(newProdEntity);
			
			$prodItem.data("entity",newProdEntity);
			
			if(oldProdEntity.price != newProdEntity.price){
				sum = oldProdEntity.amount * newProdEntity.price;
				$prodItem.find(".ordercfm-item-price").text("￥"+newProdEntity.price);
				$prodItem.find(".ordercfm-item-sum").text("￥"+sum.toFixed(2));
			}
		}
	}
	
	//第二部分,首页数据
	$proditems = $(".prod-list .prod-item");
	if($proditems != null && $proditems.length > 0){
		for(var i=0;i<$proditems.length;i++){
			$prodItem = $proditems.eq(i);
			oldProdEntity = $prodItem.data("entity");
			newProdEntity = common.matchItem(oldProdEntity.id,prodList,'id');
			if(oldProdEntity.price != newProdEntity.price){
				$prodItem.find(".prod-price").text("￥"+newProdEntity.price);
			}
			newProdEntity.amount = oldProdEntity.amount;
			$prodItem.data("entity",newProdEntity);
		}
	}
	
	//刷新总金额
	var orderInfo = ProductSet.build($(".index-page .prod-list"));
	$(".prod-footbar .prod-totalsum").text("￥"+orderInfo.total_sum.toFixed(2));//首页的总金额也要更新

}

/*function showConfirmPage(order,orderProdList){
	var $cfmpage = $(".ordercfm");
	//如果是第一次初始化，则初始化事件
	if($cfmpage.attr("is_init") != "1"){
		initConfirmPage(order);
		$cfmpage.attr("is_init",1);
		
	}
	var $orderlist = $cfmpage.find(".ordercfm-item-list");
	$orderlist.empty();
	var $tempDiv = $("#_confirm_item_temp");
	var totalSum=0;
	var sum;
	var totalAmount = 0;
	for(var i=0;i<orderProdList.length;i++){
		var prodEntity = orderProdList[i];
		var isPriceNotSure = prodEntity.price < 0;//是否存在价格待定
		var amount = prodEntity.amount || 0;
		if(amount == "0")
			continue;
		amount = parseInt(amount);
		sum = amount*prodEntity.price;
		if(isPriceNotSure){
			totalSum = -1;
		}else{
			totalSum += sum;
		}
		
		totalAmount += amount;
		var $newOrderItem = ProductSet.init($tempDiv.clone(),prodEntity,{
			size:50,
			amount_change_callback : function($prodItem,prodEntity,amount){
				var result = ProductSet.build($orderlist);
				var $sumDiv = $(".prod-footbar").find(".prod-totalsum");
				$sumDiv.text("￥"+result.total_sum.toFixed(2));
				refreshConfirmTotalSum(amount,$prodItem,prodEntity);
			}
		}).appendTo($orderlist);
	}
	
	var expressfee = refreshExpressFee(totalSum,totalAmount);
	refreshFootbarBttn(totalSum+expressfee);
	
	widget.slide($cfmpage, "left right",{duration:400,callback:function(){
		initNestPageScroll($cfmpage.find(".page-body"));
	}});
	
	$cfmpage.show();
	
	
	//根据地址栏动态调整列表高度
	var $orderlist = $(".ordercfm-item-list");
	var $deliveryTable = $(".ordercfm .deliverybar");
	var $expressFee = $(".ordercfm .ordercfm-express");
	var position = $expressFee.position();
	$orderlist.css("top",position.top+$expressFee.height()+parseInt($expressFee.css("margin-top")));
	
}*/

/*function refreshExpressFee(totalSum,totalAmount){
	//运费栏
	var feerule = getSysparam("order_expressfee_rule");
	var feeruleitems = feerule && feerule.split(",");
	var feeSum = parseFloat(feeruleitems[0]);//
	var sum2free = parseFloat(feeruleitems[1]);//满多少包运费
	var $expressbar = $(".ordercfm .express-fee");
	if(totalSum < 0){
		$expressbar.hide();
		return 0;
	}
	$expressbar.show();
	if(totalSum < sum2free){
		$expressbar.text("运费:￥"+feeSum);
	}else{
		$expressbar.text("免运费");
		feeSum = 0;
	}
	return feeSum;
}*/

/*function initConfirmPage(order){
	var type = common.getUrlParams("type");//reorder或者modify
	var ismodify = type == 'modify';
	var $cfmpage = $(".ordercfm");
	//地址栏点击事件
	var loginUser = common.getLoginUser() || {};
	var deliveryInfo = {
		name : ismodify ? order.name : loginUser.name,
		mobile : ismodify ? order.mobile : loginUser.mobile,
		address : ismodify ? order.address : loginUser.address
	}
	var $deliveryBar = $('.deliverybar');
	var loginUser = common.getLoginUser();
	initDeliveryBar($deliveryBar,$(".order-delivery"),deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		if(type == 'modify'){
			//修改的话，改了地址要重新修改订单
			ajax.request({
				url : _base_url+"/web/order/modify.do",
				params : {
					order_id : order.id,
					mobile : newDeliveryInfo.mobile == deliveryInfo.mobile ? null : newDeliveryInfo.mobile,
					name : newDeliveryInfo.name == deliveryInfo.name ? null : newDeliveryInfo.name,
					address : newDeliveryInfo.address == deliveryInfo.address ? null : newDeliveryInfo.address
				},
				need_progressbar : $(".titlebar"),
				success : function(header,body){
					refreshDeliveryBar($deliveryBar,newDeliveryInfo.name,newDeliveryInfo.mobile,newDeliveryInfo.address);
					closeDeliveryPage();
					successCallback && successCallback.apply();
				},
				complete :function(data,textStatus){
					completeCallback && completeCallback.apply(null,[data,textStatus]);
				}
			});
		}else{
			refreshDeliveryBar($('.deliverybar'),newDeliveryInfo.name,newDeliveryInfo.mobile,newDeliveryInfo.address);
			closeDeliveryPage();
			successCallback && successCallback.apply();
		}
	});
	
	//右上角菜单关闭按钮
	initTitlebarBttns($cfmpage.find(".titlebar"),{
		iconfont : 'close',
		handler:function(){
			if(common.getUrlParams("type") == 'modify'){
				//修改的话，取消要回到支付界面
				common.gotoPage("../order/pay.html?order_id="+common.getUrlParams("order_id"));
			}else{
				closeConfirmPage();
				//如果首页没初始化，则需要初始化
				if($(".prod-list").attr("is_init") != 1){
					initIndex();
				}
			}
		}
	});
	
	//运费栏
	var sum4expressfree = getSum4ExpressFeeFree();
	if(sum4expressfree != null){
		$(".ordercfm .express-fee-tip").text(buildExpressFeeText(sum4expressfree,true));
	}
	
	//下单取消按钮
	widget.bindTouchClick($(".ordercfm .footbar-bttn-cancel"),function(e){
		if(common.getUrlParams("type") == 'modify'){
			//修改的话，取消要回到支付界面
			common.gotoPage("../order/pay.html?order_id="+common.getUrlParams("order_id"));
		}else{
			closeConfirmPage();
			//如果首页没初始化，则需要初始化
			if($(".prod-list").attr("is_init") != 1){
				initIndex();
			}
		}
	});
	//下单确认按钮
	widget.bindTouchClick($(".ordercfm .footbar-bttn-ok"),function(e){
		commitOrder($('.order-delivery').data("deliveryInfo"),order);
	});
	
	if(common.getUrlParams("type") == 'modify'){
		var $addProdBttn = $("<div class='ordercfm-addprod xsg-font'>").appendTo($cfmpage.find(".page-body"));
		$addProdBttn.text("添加产品");
		widget.bindTouchClick($addProdBttn,function(){
			closeConfirmPage();
			//如果首页没初始化，则需要初始化
			if($(".prod-list").attr("is_init") != 1){
				var orderInfo = ProductSet.build($cfmpage.find(".ordercfm-item-list"));
				initIndex(orderInfo.product_list);
			}
		});
	}
	
}*/

/*function commitOrder(deliveryInfo,order){
	deliveryInfo = deliveryInfo || {};
	if(!deliveryInfo.name || !deliveryInfo.mobile || !deliveryInfo.address){
		message.errorHide("请补充收件人信息");
		return;
	}
	var type = common.getUrlParams("type");//reorder或者modify
	var ismodify = type == 'modify';
	var $titlebar = $(".titlebar.confirm");
	var orderItems = buildOrderItems();
	if(ismodify){
		if(orderItems == null){
			//说明明细没做修改
			common.gotoPage("../order/pay.html?order_id="+order.id);
		}else{
			if(orderItems[0].amount < 2){
				message.errorHide("2斤起卖，请修改您的数量",$titlebar);
				return;
			}
			commitOrderModify(order,orderItems);
		}
	}else{
		//弹出订购须知声明
		showOrderInfoPage(function(){
			commitOrderCreate(deliveryInfo,orderItems,$(".order-info-page .titlebar"));
		});
		
	}
}
function showOrderInfoPage(callback){
	var $orderInfoPage = $(".order-info-page");
	$orderInfoPage.data("callback",callback);
	var $orderInfoContent = $orderInfoPage.find(".page-body");
	
	if($orderInfoPage.attr("is_init") != 1){
		//右上角菜单关闭按钮
		initTitlebarBttns($orderInfoPage.find(".titlebar"),{
			iconfont : 'close',
			handler:function(){
				$orderInfoPage.hide();
				$orderInfoContent.empty();
			}
		});
		//文字内容滚动
		initNestPageScroll($orderInfoContent);
		
		//checkbox框
		var $nextBttn = $orderInfoPage.find(".footbar-bttn-ok");
		CheckboxSet.init($orderInfoPage.find(".footbar-bttn-cancel"),{
			text : "没关系啦,我能理解接受",
			handler : function(checked){
				if(checked){
					$nextBttn.removeClass('disable-bttn');
				}else{
					$nextBttn.addClass('disable-bttn');
				}
			}
		});
		//继续下单按钮
		widget.bindTouchClick($nextBttn,function(){
			if($nextBttn.hasClass("disable-bttn"))
				return;
			$nextBttn.addClass("disable-bttn");
			$orderInfoPage.data("callback").apply();
		});
		
		$orderInfoPage.attr("is_init",1);
	}
	CheckboxSet.select($orderInfoPage.find(".footbar-bttn-cancel .checkbox-set"),false);
	widget.slide($orderInfoPage,"left right",400,function(){
		queryArticle("order_information",function(content){
			$orderInfoContent.html(content);
		});
	});
	$orderInfoPage.find(".footbar-bttn-ok").addClass("disable-bttn");
	
}

function commitOrderModify(order,orderItems){
	var $titlebar = $(".titlebar.confirm");
	ajax.request({
		url : _base_url+"/web/order/modify.do",
		need_progressbar : $titlebar,
		params : {
			order_id : order.id,
			items : JSON.stringify(orderItems)
		},
		success : function(header,body){
			message.successHide("订单修改成功，正在跳转...",$titlebar);
			common.gotoPage("../order/pay.html?order_id="+order.id,1200);
		}
	});
}
function commitOrderCreate(deliveryInfo,orderItems,$titlebar,$operBttn){
	ajax.request({
		url : _base_url+"/web/order/create.do",
		need_progressbar : $titlebar,
		params : {
			name : deliveryInfo.name,
			mobile: deliveryInfo.mobile,
			address : deliveryInfo.address,
			items : JSON.stringify(buildOrderItems())
		},
		success : function(header,body){
			if(body.priceChanged == 1){
				//价格变动,重新更新界面上的价格
				message.errorHide("产品价格有变动，请重新确认",$titlebar);
				refreshProductList(body.productList);
				return;
			}
			var loginUser = common.getLoginUser() || {};
			if(!loginUser.name){
				loginUser.name = deliveryInfo.name;
			}
			if(!loginUser.mobile){
				loginUser.mobile = deliveryInfo.mobile;
			}
			if(!loginUser.address){
				loginUser.address = deliveryInfo.address;
			}
			common.setAuth(false,false,loginUser);
			message.successHide("订单创建成功，正在跳转...",$titlebar);
			common.gotoPage("../order/pay.html?order_id="+body.newid,1200);
		}
	});
}*/

/*function refreshConfirmTotalSum(amount,$prodItem,prodEntity){
	//刷新当前行的金额
	var prodEntity = $triggerItem.data("entity");
	var isPriceNotSure = prodEntity.price < 0;
	
	var sum = isPriceNotSure?"价格待定":"￥"+(prodEntity.price * amount).toFixed(2);
	var $sum = $triggerItem.find(".ordercfm-item-sum");
	$sum.text(sum);
	if(isPriceNotSure){
		$sum.addClass("price-not-sure");
	}else{
		$sum.removeClass("price-not-sure");
	}
	
	prodEntity.amount = amount;
	
	//在同步更新首页的数量
	$("#product_"+prodEntity.id).find(".prod-amount-input").val(amount);
	$("#product_"+prodEntity.id).find(".prod-sum").text("￥"+prodEntity.price * amount);
	
	var orderInfo = ProductSet.build($(".ordercfm .ordercfm-item-list"));
	var expressfee = refreshExpressFee(orderInfo.total_sum,orderInfo.total_amount);
	refreshFootbarBttn(orderInfo.total_sum+expressfee);
	
}*/

/*function closeCommitPage(){
	$(".order").hide();
}

function closeDeliveryPage(){
	$(".order-delivery").hide();
}*/




//获取当前已选择订购的产品列表，只需要构建product_id和amount即可，其它后台计算
/*function buildOrderItems(){
	var items = [];
	var hasChanged = false;
	var orderInfo = ProductSet.build($(".ordercfm .ordercfm-item-list"));
	var productList = orderInfo.product_list;
	for(var i=0;i<productList.length;i++){
		var prodEntity = productList[i];
		if(prodEntity.amount != prodEntity.origAmount){
			hasChanged = true;
		}
		items.push({
			product_id : prodEntity.id,
			amount : prodEntity.amount || 0,
			_price : prodEntity.price//因为前端传过来的price是元，所以可能会有小数点，这会导致数据结构里的long型的price解析出错，所以暂时用了一个_price作为额外属性，这里再转成分
			
		});
	}
	
	return hasChanged ? items : null;
}*/

/*function initCommentBar(){
	if(common.getLoginUser().isAdmin != 1){
		$(".index-page .prod-stats").remove();
		return;
	}
	widget.bindTouchClick($(".index-page .prod-stats .comment"),function(e){
		var $item = $(e.target).parents('.prod-item');
		var entity = $item.data("entity");
		showCommentPage(entity.id);
	});
}

function showCommentPage(prodId){
	var $page = $(".comment-page");
	if($page.attr("is_init") != 1){
		initCommentPage($page);
		$page.attr("is_init",1);
	}
	
	var $commentList = $page.find(".prod-comment-list");
	$commentList.empty();
	
	widget.slide($page,"left right",400,function(){
		ajax.request({
			url : _base_url+"/web/product/queryCommentList.do",
			need_progressbar : $page.find(".titlebar"),
			params : {
				product_id : prodId
			},
			success : function(header,body){
				var list = [{pic:'http://wx.qlogo.cn/mmopen/Juz9r4qGpD3QXs0KSpGJT7kibiaQ9xCI1hOUkTouGNTqt6nuxQxqqekGIjXaswPUfvZgPiabcRsmjkMG7Aq4v82C1cfc71HHqCd/0',name:'吴宇杰',content:'真实太好吃了，下次一定还来',mobile:'13588830404',orderTime:'2016-09-12',grade:'2',amount:'2'},
				            {name:'吴宇杰',content:'真实太好吃了，下次一定还来真实太好吃了，下次一定还来真实太好吃了，下次一定还来真实太好吃了，下次一定还来真实太好吃了，下次一定还来',mobile:'13588830404',orderTime:'2016-09-12',grade:4,amount:'2'},
				            {name:'吴宇杰',content:'真实太好吃了，下次一定还来',mobile:'13588830404',orderTime:'2016-09-12',grade:'5',amount:'2'}]
				
				list = body.commentList;
				var $tempItem = $page.find("#_comment_item_temp");
				for(var i=0;i<list.length;i++){
					if(i > 0){
						createSplitline().appendTo($commentList);
					}
					var entity = list[i];
					var userEntity = entity.user;
					var $item = $tempItem.clone().appendTo($commentList);
					$item.attr("id","comment_"+entity.id);
					$item.find(".name").text(userEntity?userEntity.name:"匿名用户");
					$item.find(".mobile").text(userEntity?userEntity.mobile:"");
					$item.find(".order-time").text(entity.createTime.split(" ")[0]);//只显示到日期
					$item.find(".content").text(entity.comment);
					
					$item.find(".order-amount").text("购买:2斤");
					
					var $pic = $item.find(".header-pic");
					var pic = (userEntity && userEntity.pic) || logoUrl;
					if(pic){
						$pic.css({
							"background":"url("+pic+") no-repeat center center",
							"background-size":"100% 100%"
						});
					}
					
					entity.grade = entity.grade || 5;
					var $gradeContainer = $item.find(".grade-container");
					for(var k=0;k<5;k++){
						var $star = $("<span class='xsg-fontset star'>").appendTo($gradeContainer).html(k<entity.grade?fontset.star:fontset.star_empty)
					}
					
					$item.show();
				}
			}
		});
	});
}
function initCommentPage($page){
	//右上角菜单关闭按钮
	initTitlebarBttns($page.find(".titlebar"),{
		iconfont : 'close',
		handler:function(){
			$page.hide();
		}
	});
	
	initNestPageScroll($page.find(".page-body"));
}*/


