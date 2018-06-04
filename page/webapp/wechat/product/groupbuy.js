//var orderId = null;
//var prodEntity = null;//当前团购对象
//var switchValue = null;
var orderEntity = null;
//var orderItem = null;
//var type = null;
//var choosedPromotion;
var deliveryInfo;
var groupbuyId;
var loading = new PageLoading($(".index-page"));
$(function(){
	loading.show();
	initPage();
});
function initPage(){
	var paramObj = common.getUrlParams();
	groupbuyId = paramObj.groupbuy_id;
	//orderId = paramObj.order_id;
	//type = paramObj.type;
	if(!groupbuyId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	
	queryGroupbuy(groupbuyId);
}

function queryGroupbuy(groupbuyId){
	ajax.request({
		url : _base_url+"/web/groupbuy/queryDetail.do",
		need_progressbar : false,
		params : {
			groupbuy_id:groupbuyId,
			need_oauth:true
		},
		success : function(header,body){
			orderEntity = body.order;
			initIndex(
					body.groupbuy,
					body.productList,
					body.hasPayed == 1);
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initIndex(groupbuy,prodList,hasPayed){
	initTitlebarBttns($(".index-page .titlebar"),{
		iconfont : 'menu',
		menu_items : getMenus()
	});
	$(".groupbuy-intro-content").text(groupbuy.intro);
	
	initDelivery();
	initFootbar(hasPayed);
	
	initProdArea(prodList,"_prod_item_temp");
	
	/*if(order == null){
		//未参与
		initNewOrder(groupbuy,prodList);
	}else if(order.status == 0){
		//已参与，但未支付
		initNeedPay(groupbuy,prodList,order,orderItemList);
	}else if(order.status == 1){
		//已支付
		common.gotoPage("../order/pay.html?order_id="+order.id);
		//initPayed(groupbuy,prodList,order,orderItemList);
	}*/
}

function initNewOrder(groupbuy,prodList){
	
}
/*function initNeedPay(groupbuy,prodList,order,orderItemList){
	for(var i=0;i<prodList.length;i++){
		var prod = prodList[i];
		for(var k=0;k<orderItemList.length;k++){
			if(prod.id == orderItemList[k].productId){
				prod.amount = orderItemList[k].amount;
				prod.price = orderItemList[k].price;
				break;
			}
		}
	}
	initProdArea(prodList,"_prod_item_temp");
}
function initPayed(groupbuy,prodList,order,orderItemList){
	for(var i=0;i<prodList.length;i++){
		var prod = prodList[i];
		for(var k=0;k<orderItemList.length;k++){
			if(prod.id == orderItemList[k].productId){
				prod.amount = orderItemList[k].amount;
				prod.price = orderItemList[k].price;
				break;
			}
		}
	}
	initProdArea(prodList,"_prod_item_payed_temp");
}*/
function initDelivery(){
	//地址栏点击事件
	var loginUser = common.getLoginUser() || {};
	deliveryInfo = $.extend(loginUser,{});
	/*deliveryInfo = {
		name : ismodify ? order.name : loginUser.name,
		mobile : ismodify ? order.mobile : loginUser.mobile,
		address : ismodify ? order.address : loginUser.address
	}*/
	var $deliveryBar = $('.deliverybar');
	initDeliveryBar($deliveryBar,$(".order-delivery"),deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		deliveryInfo = newDeliveryInfo;
		/*if(ismodify){
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
			
		}*/
		
		refreshDeliveryBar($('.deliverybar'),newDeliveryInfo.name,newDeliveryInfo.mobile,newDeliveryInfo.address);
		$(".order-delivery").hide();
		successCallback && successCallback.apply();
	});
}

function initFootbar(hasPayed){
	widget.bindTouchClick($(".index-page .footbar-bttn-ok"),function(){
		deliveryInfo = deliveryInfo || {};
		if(!deliveryInfo.name || !deliveryInfo.mobile || !deliveryInfo.address){
			message.errorHide("请补充收件人信息");
			return;
		}
		//var ismodify = !!orderId;
		var $titlebar = $(".index-page .titlebar");
		var orderItems = buildOrderItems(true,false);
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
		
		if(hasPayed){
			message.confirmSlide("您已参与本次团购，是否再次购买？",function(){
				//弹出订购须知声明
				commitOrderCreate(deliveryInfo,orderItems,$(".index-page .titlebar"));
			});
		}else{
			//弹出订购须知声明
			showOrderInfoPage(function(){
				commitOrderCreate(deliveryInfo,orderItems,$(".order-info-page .titlebar"));
			});
		}
		
		
		
		
		/*if(ismodify){
			if(orderItems == null){
				//说明明细没做修改
				common.gotoPage("../order/pay.html?order_id="+order.id);
			}else{
				commitOrderModify(orderId,orderItems);
			}
		}else{
			
		}*/
	});
}

function commitOrderCreate(deliveryInfo,items,$titlebar){
	ajax.request({
		url : _base_url+"/web/groupbuy/follow.do",
		need_progressbar : $titlebar,
		params : {
			groupbuy_id : groupbuyId,
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
			//addShoppingCart(null);
			message.successHide("订单创建成功，正在跳转...",$titlebar);
			common.gotoPage("../order/pay.html?order_id="+body.orderId,1200);
		},
		complete : function(){
			enableBttn($(".order-info-page .footbar-bttn-ok"),true);
		}
	});
}


function initProdArea(productList,tempId){
	var $prodlist = $(".prod-list");
	var $tempDiv = $("#"+tempId);
	for(var i=0;i<productList.length;i++){
		var prodEntity = productList[i];
		var $newDiv = $tempDiv.clone().appendTo($prodlist);
		$newDiv.attr("id","product_"+prodEntity.id);
		$newDiv.data("entity",prodEntity);
		ProductSet.init($newDiv,prodEntity,{
			size:80,
			amount_change_callback : function($prodItem,prodEntity,amount){
				refreshTotalSum();
			}
		});
		CheckboxSet.init($newDiv.find(".prod-choose"));
		$newDiv.find(".groupbuy-price").val(prodEntity.price);
		
		$newDiv.show();
	}
}

function refreshTotalSum(){
	var result = ProductSet.build($(".prod-list"));
	var totalSum = result.total_sum;
	var $totalSum = $(".index-page .footbar-bttn-ok .totalsum");
	$totalSum.text("￥"+totalSum.toFixed(2));
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
	
	widget.slide($orderInfoPage,"left right",400,function(){
		queryArticle("order_information",function(content){
			$orderInfoContent.html(content);
		});
	});
	$orderInfoPage.find(".footbar-bttn-ok").addClass("disable-bttn");
	CheckboxSet.select($orderInfoPage.find(".footbar-bttn-cancel .checkbox-set"),false);
}
/*function commitModify(){
	var $titlebar = $(".index-page .titlebar");
	//修改
	var amount = $(".prod-item .prod-amount-input").val();
	ajax.request({
		url : _base_url+"/web/groupbuy/modify.do",
		need_progressbar : $titlebar,
		params : {
			order_id : orderEntity.id,//传入当前的用户的orderid
			amount : parseInt(amount)
		},
		success : function(header,body){
			if(body.priceChanged == 1){
				//价格变动,重新更新界面上的价格
				message.errorHide("产品价格有变动，请重新确认",$titlebar);
				//refreshProductList(body.productList);
				return;
			}
			//common.setAuth(false,false,loginUser);
			message.successHide("修改成功，正在跳转...",$titlebar);
			common.gotoPage("../order/pay-groupbuy.html?order_id="+orderId,1200);
		}
	});
}*/
/*function commitFollow(){
	var loginUser = common.getLoginUser();
	var commitFunc = function(){
		var amount = $(".prod-item .prod-amount-input").val();
		var $titlebar = $(".index-page .titlebar");
		//跟单
		ajax.request({
			url : _base_url+"/web/groupbuy/follow.do",
			need_progressbar : $titlebar,
			params : {
				order_id : orderId,//表示团购
				amount : parseInt(amount),
				name : deliveryInfo ? deliveryInfo.name : loginUser.name,
				mobile: deliveryInfo ? deliveryInfo.mobile : loginUser.mobile
			},
			success : function(header,body){
				if(body.priceChanged == 1){
					//价格变动,重新更新界面上的价格
					message.errorHide("产品价格有变动，请重新确认",$titlebar);
					//refreshProductList(body.productList);
					return;
				}
				if(!loginUser.mobile){
					loginUser.mobile = deliveryInfo.mobile;
				}
				if(!loginUser.name){
					loginUser.name = deliveryInfo.name;
				}
				common.setAuth(false,false,loginUser);
				message.successHide("拼单成功，正在跳转...",$titlebar);
				common.gotoPage("../order/pay-groupbuy.html?order_id="+orderId,1200);
			}
		});
	}
	var isCrab = prodEntity.typeId == 2;//蟹的话需要显示订购说明
	if(isCrab){
		showOrderInfoPage(commitFunc);
	}else{
		commitFunc();
	}
	
}*/
/*function commitCreate(){
	var $titlebar = $(".index-page .titlebar");
	var commitFunc = function(){
		//新创建
		var amount = $(".prod-item .prod-amount-input").val();
		ajax.request({
			url : _base_url+"/web/groupbuy/create.do",
			need_progressbar : $titlebar,
			params : {
				name : deliveryInfo.name,
				mobile: deliveryInfo.mobile,
				address : deliveryInfo.address,
				product_id : prodEntity.id,
				amount : parseInt(amount),
				promotion_id : 1//choosedPromotion.id
			},
			success : function(header,body){
				if(body.priceChanged == 1){
					//价格变动,重新更新界面上的价格
					message.errorHide("产品价格有变动，请重新确认",$titlebar);
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
				message.successHide("订单创建成功，正在跳转...",$titlebar);
				common.gotoPage("../order/pay-groupbuy.html?order_id="+body.newid,1200);
			}
		});
	}
	var isCrab = prodEntity.typeId == 2;
	if(isCrab){
		showOrderInfoPage(commitFunc);
	}else{
		commitFunc();
	}
}*/

//获取当前已选择订购的产品列表，只需要构建product_id和amount即可，其它后台计算
function buildOrderItem(){
	var item = $(".index-page .prod-item").eq(0);
	var prodEntity = item.data("entity");
	return {
		product_id : prodEntity.id,
		amount : parseInt(prodEntity.amount) || 0,
		_price : prodEntity.price//因为前端传过来的price是元，所以可能会有小数点，这会导致数据结构里的long型的price解析出错，所以暂时用了一个_price作为额外属性，这里再转成分
	}
}

function showFllowDeliveryPage(callback){
	var $page = $(".order-delivery");
	$page.find(".titlebar").text("填写客户资料");
	$page.find(".delivery-container .deliverypage-label").text("联系人信息");
	$page.find(".deliverypage-linkman").attr("placeholder","联系人姓名");
	$page.find(".deliverypage-address").hide();
	$page.find(".deliverypage-address").val("");
	showDeliveryPage($page,deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		deliveryInfo = newDeliveryInfo;
		$page.hide();
		callback && callback.apply();
		/*ajax.request({
			url : _base_url+"/web/user/modify.do",
			params : {
				mobile : newDeliveryInfo.mobile,
				vcode : newDeliveryInfo.vcode,
				name : newDeliveryInfo.name
			},
			need_progressbar : $page.find(".titlebar"),
			success : function(header,body){
				common.setAuth(false,false,{
					name : newDeliveryInfo.name,
					mobile : newDeliveryInfo.mobile
				})
				$page.hide();
				callback && callback.apply();
			},
			complete :function(data,textStatus){
				completeCallback && completeCallback.apply(null,[data,textStatus]);
			}
		});*/
	});
}


//获取当前已选择订购的产品列表，只需要构建product_id和amount即可，其它后台计算
function buildOrderItems(force,checkPremise){
	var items = [];
	var hasChanged = false;
	var orderInfo = ProductSet.build($(".prod-list"));
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
			amount : prodEntity.amount || 0
		});
	}
	
	return (hasChanged || force)? items : null;
}

