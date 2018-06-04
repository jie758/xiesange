var loading = new PageLoading();
var orderMap = null;
var productList = null;
var orderId;
var deliveryInfo;
var orderProdStr;
function init(){
	loading.show();
	initPage();
}
function initPage(){
	var urlParam = common.getUrlParams();
	orderId = urlParam.order_id;
	orderProdStr = urlParam.order_product;//productId1:amount1,productId2:amount2,...
	
	if(orderProdStr != null){
		var productIdStr = "";
		orderMap = {};
		var prodArr = orderProdStr.split(",");
		for(var i=0;i<prodArr.length;i++){
			var itemArr = prodArr[i].split(":");
			orderMap[itemArr[0]] = itemArr[1];
			productIdStr += ","+itemArr[0];
		}
		queryProductList(productIdStr.substring(1));
	}else if(!orderId){
		
	}
	
}

function queryProductList(productIdStr){
	ajax.request({
		url : _base_url+"/web/product/queryList.do",
		need_progressbar : false,
		params : {
			product_ids : productIdStr,
			basedata:"baseparam",
			need_wx_signature:true,
			need_oauth : true
		},
		success : function(header,body){
			productList = body.productList;
			if(productList != null && productList.length > 0){
				for(var i=0;i<productList.length;i++){
					productList[i].amount = orderMap[productList[i].id];
				}
			}
			
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/order/order-confirm.html?order_product="+orderProdStr),
				img : logoUrl,
				timeline_desc : "#蟹三哥# 从海洋到厨房，只需18小时！我们的海鲜，鲜到崩溃",
				message_title : "#蟹三哥,东海野生海鲜工坊# ",
				message_desc : "从海洋到厨房，只需18小时！我们的海鲜，鲜到崩溃"
			});
			
			initIndex();
		},
		complete : function(){
			$(".index-page").show();
			loading.hide();
		}
	});
}
function initIndex(){
	//右上角菜单按钮点击
	var menus = [];
	/*if(isAdmin()){
		menus.push({
			iconfont : 'menu',
			handler : function(){
				alert(1)
			}
		});
	};*/
	menus.push({
		iconfont : 'menu',
		menu_items : getMenus()
	});
	initTitlebarBttns($(".index-page .titlebar"),menus);
	
	
	var totalSum = initProdList();
	initDeliveryInfo();
	var expressfee = initExpressInfo(totalSum);
	initFootbar(totalSum+expressfee);
	
	if(orderId != null){
		var $addProdBttn = $("<div class='ordercfm-addprod xsg-font'>").appendTo($(".index-page"));
		$addProdBttn.text("添加产品");
		widget.bindTouchClick($addProdBttn,function(){
			back2prodList();
		});
	}
}


function initProdList(){
	var $orderlist = $(".ordercfm-item-list");
	var $tempDiv = $("#_confirm_item_temp");
	var totalSum=0;
	var sum;
	var totalAmount = 0;
	for(var i=0;i<productList.length;i++){
		var prodEntity = productList[i];
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
				refreshTotalSum(result.total_sum);
			}
		}).appendTo($orderlist);
	}
	return totalSum;
}

function initDeliveryInfo(){
	var type = null;
	var ismodify = (type == 'modify');
	//地址栏点击事件
	var loginUser = common.getLoginUser() || {};
	deliveryInfo = {
		name : ismodify ? order.name : loginUser.name,
		mobile : ismodify ? order.mobile : loginUser.mobile,
		address : ismodify ? order.address : loginUser.address
	}
	//deliveryInfo = {};
	var $deliveryBar = $('.deliverybar');
	var loginUser = common.getLoginUser();
	initDeliveryBar($deliveryBar,$(".order-delivery"),deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		deliveryInfo = newDeliveryInfo;
		if(ismodify){
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
}

function refreshExpressFee(totalSum,totalAmount){
	//运费栏
	var feerule = getSysparam("order_expressfee_rule");
	var feeruleitems = feerule && feerule.split(",");
	var feeSum = parseFloat(feeruleitems[0]);//
	var sum2free = feeruleitems[1] && parseFloat(feeruleitems[1]);//满多少包运费
	var $expressbar = $(".index-page .expressfee-area .sum");//$(".express-fee");
	if(totalSum < 0){
		$expressbar.hide();
		return 0;
	}
	if(feeSum > 0 && (sum2free < 0 || totalSum < sum2free)){
		$expressbar.text("￥"+feeSum);
		$expressbar.removeClass("gray-font").addClass("money-font");
	}else{
		$expressbar.text("免运费");
		$expressbar.removeClass("money-font").addClass("gray-font");
		feeSum = 0;
	}
	return feeSum;
}

function initExpressInfo(totalSum){
	//运费栏
	var sum4expressfree = getSum4ExpressFeeFree();
	
	var $expressBlock = $(".index-page .expressfee-area");
	initPromotionList($expressBlock,[{type:'express',text:"运费","detail":buildExpressFeeText(sum4expressfree,true)}]);
	
	$("<span class='sum right-part'>").appendTo($expressBlock.find(".item"));
	
	return refreshExpressFee(totalSum);
}

/**
 * 刷新总金额
 * @param totalSum，不包含运费，运费是通过这个总金额计算出来
 */
function refreshTotalSum(totalSum){
	var expressfee = refreshExpressFee(totalSum);
	var totalSum = totalSum+expressfee;
	var $totalSum = $(".index-page .footbar-bttn-ok .totalsum");
	$totalSum.text("￥"+totalSum.toFixed(2));
}


function closeDeliveryPage(){
	$(".order-delivery").hide();
}
/**
 * 初始化底部栏金额
 * @param totalSum,包含了运费
 */
function initFootbar(totalSum){
	var $totalSum = $(".index-page .footbar-bttn-ok .totalsum");
	$totalSum.text("￥"+totalSum.toFixed(2));
	
	var $action = $(".index-page .footbar-bttn-ok .action");
	$action.text("确定下单");
	
	var $leftButtn = $(".index-page .footbar-bttn-cancel");
	
	//返回修改按钮
	widget.bindTouchClick($leftButtn,function(){
		if(orderId == null){
			back2prodList();
		}else{
			//表示从支付页修改场景过来
			//清空shoppingcart
			addShoppingCart(null);
			common.gotoPage("../order/pay.html"+(orderId ? "?order_id="+orderId : ""));
		}
	});
	
	//下单按钮
	widget.bindTouchClick($(".index-page .footbar-bttn-ok"),function(){
		deliveryInfo = deliveryInfo || {};
		if(!deliveryInfo.name || !deliveryInfo.mobile || !deliveryInfo.address){
			message.errorHide("请补充收件人信息");
			return;
		}
		var ismodify = !!orderId;
		var $titlebar = $(".index-page .titlebar");
		var orderItems = buildOrderItems(true,true);
		if(orderItems === false){
			return;
		}
		var totalAmount = 0;
		for(var i=0;i<orderItems.length;i++){
			totalAmount += orderItems[i].amount;
		}
		if(totalAmount == 0){
			message.errorHide("请至少选择一个产品",$titlebar);
			return;
		}
		if(ismodify){
			if(orderItems == null){
				//说明明细没做修改
				common.gotoPage("../order/pay.html?order_id="+order.id);
			}else{
				commitOrderModify(orderId,orderItems);
			}
		}else{
			//弹出订购须知声明
			showOrderInfoPage(function(){
				commitOrderCreate(deliveryInfo,orderItems,$(".index-page .titlebar"));
			});
		}
	});
}
function back2prodList(){
	var orderItems = buildOrderItems(true);
	var orderMap = {};
	var orderProdInfo = "";
	for(var i=0;i<orderItems.length;i++){
		orderMap[orderItems[i].product_id] = orderItems[i].amount;
		orderProdInfo += ","+orderItems[i].product_id+":"+orderItems[i].amount;
	}
	//addShoppingCart(orderMap);
	var url = "../product/product-list.html";
	url += "?order_product="+orderProdInfo.substr(1);
	if(orderId != null){
		url += "&order_id="+orderId;
	}
	common.gotoPage(url);
}

function commitOrderModify(orderId,orderItems){
	var $titlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/order/modify.do",
		need_progressbar : $titlebar,
		params : {
			order_id : orderId,
			items : JSON.stringify(orderItems)
		},
		success : function(header,body){
			//清空shoppingcart
			addShoppingCart(null);
			message.successHide("订单修改成功，正在跳转...",$titlebar);
			common.gotoPage("../order/pay.html?order_id="+orderId,1200);
		}
	});
}
function commitOrderCreate(deliveryInfo,items,$titlebar){
	ajax.request({
		url : _base_url+"/web/order/create.do",
		need_progressbar : $titlebar,
		params : {
			name : deliveryInfo.name,
			mobile: deliveryInfo.mobile,
			address : deliveryInfo.address,
			items : JSON.stringify(items)
		},
		success : function(header,body){
			if(body.priceChanged == 1){
				//价格变动,重新更新界面上的价格
				$(".order-info-page").hide();
				message.alert("产品价格有变动，请重新确认",function(){
					window.location.reload();
				});
				
				//refreshProductList(body.productList);
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
			//清空shoppingcart
			addShoppingCart(null);
			message.successHide("订单创建成功，正在跳转...",$titlebar);
			
			common.gotoPage("../order/pay.html?order_id="+body.newid,1200);
		}
	});
}


//如果后端产品价格做了变动，这里需要重新刷前台的产品价格
function refreshProductList(prodList){
	prod_list = prodList;//先更新全局数组
	
	order_prod_list = [];
	//第一部分，确认界面
	var $proditems = $(".ordercfm-item-list .prod-item");
	var oldProdEntity = null;
	var newProdEntity = null;
	var $prodItem = null;
	var sum = null;
	var totalSum = 0;
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
				$prodItem.find(".prod-price").text("￥"+newProdEntity.price);
				$prodItem.find(".prod-sum").text("￥"+sum.toFixed(2));
			}
			totalSum += sum;
		}
	}
	
	/*//第二部分,首页数据
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
	}*/
	
	//刷新总金额
	refreshTotalSum(totalSum);

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
			text : "我知道了",
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
		},false);
	});
	$orderInfoPage.find(".footbar-bttn-ok").addClass("disable-bttn");
	
}

//获取当前已选择订购的产品列表，只需要构建product_id和amount即可，其它后台计算
function buildOrderItems(force,checkPremise){
	var items = [];
	var hasChanged = false;
	var orderInfo = ProductSet.build($(".ordercfm-item-list"));
	var productList = orderInfo.product_list;
	for(var i=0;i<productList.length;i++){
		var prodEntity = productList[i];
		if(checkPremise && prodEntity.premise && prodEntity.premise > prodEntity.amount){
			message.errorHide(prodEntity.name+prodEntity.premise+"斤起卖",$(".index-page .titlebar"));
			return false;
		}
		if(prodEntity.amount != prodEntity.origAmount){
			hasChanged = true;
		}
		items.push({
			product_id : prodEntity.id,
			amount : prodEntity.amount || 0,
			_price : prodEntity.price//因为前端传过来的price是元，所以可能会有小数点，这会导致数据结构里的long型的price解析出错，所以暂时用了一个_price作为额外属性，这里再转成分
			
		});
	}
	
	return (hasChanged || force)? items : null;
}
