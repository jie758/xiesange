var loading = new PageLoading();
var productList = null;
var productEntity = null;
var deliveryInfo;
function init(){
	loading.show();
	initPage();
}
function initPage(){
	var urlParam = common.getUrlParams();
	var productId = urlParam.product_id;
	
	ajax.request({
		url : _base_url+"/web/product/queryDetail.do",
		need_progressbar : false,
		params : {
			product_id : productId,
			basedata:"baseparam",
			need_oauth:true,
			wx_share : function(header,body){
				var product = body.product;
				var loginUser = common.getLoginUser();
				var title = null;
				var desc = null;
				var url = null;
				if(product.typeId == 5){
					title = "#海鲜大礼包，就等你来拿#";
					desc = "超值大礼包优惠大放送~"+product.summary;
					url = logoUrl;
				}else{
					title = "#快看!有"+product.name+"#";
					desc = "每一天,都是吃海鲜的好日子 !"+body.product.summary;
					url = product.pic;
				}
				return {
					url : xsgwx.buildWxOAuthUrl(_base_host+"/wechat/op/1111/preorder.html?product_id="+productId+"&fromwx=1"),
					img : url,
					timeline_desc : title+" "+desc,
					message_title : title,
					message_desc : desc
				};
				
			}
		},
		success : function(header,body){
			productEntity = body.product;
			productList = body.productItems || [productEntity];
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
	initTitlebarBttns($(".index-page .titlebar"),{
		iconfont : 'menu',
		menu_items : getMenus()
	});
	
	
	var totalSum = initProdList();
	initDeliveryInfo();
	initSumbar(totalSum);
	var expressfee = initExpressInfo(totalSum);
	initFootbar(totalSum+expressfee);
	
	showCustServiceIcon();
}

function initSumbar(totalSum){
	if(productEntity.typeId == 5){
		$(".sumbar .orig").html("单品总价:<span>￥"+totalSum+"</span>");
		$(".sumbar .prom").html("套盒特价:<span>￥"+productEntity.price+"</span>");
		$(".price-bar.package").show();
	}else{
		$(".price-bar.single").show();
		$(".sumbar").remove();
		//$(".sumbar .orig").html("原价:<span>￥"+productEntity.price+"</span>");
		//$(".sumbar .prom").html("套盒特价:<span>￥"+totalSum+"</span>");
	}
	
	
	//$(".sumbar .orig-sum").text("￥"+totalSum);
	//$(".sumbar .prom-sum").text("￥"+productEntity.price);
}

function initProdList(){
	var $orderlist = $(".ordercfm-item-list");
	var $tempDiv = $("#_confirm_item_temp");
	var totalSum=0;
	var sum;
	//var totalAmount = 0;
	for(var i=0;i<productList.length;i++){
		var prodEntity = productList[i];
		var isPriceNotSure = prodEntity.price < 0;//是否存在价格待定
		var amount = prodEntity.amount || 0;
		/*if(amount == "0")
			continue;*/
		//amount = parseInt(amount);
		sum = amount*prodEntity.price;
		if(isPriceNotSure){
			totalSum = -1;
		}else{
			totalSum += sum;
		}
		
		//totalAmount += amount;
		var $newOrderItem = ProductSet.init($tempDiv.clone(),prodEntity,{
			size:80,
			amount_change_callback : function($prodItem,prodEntity,amount){
				var result = ProductSet.build($orderlist);
				refreshTotalSum(result.total_sum);
			}
		}).appendTo($orderlist);
	}
	
	
	//查看产品详情
	widget.bindTouchClick($orderlist.find(".detail-td"),function(e){
		var $item = $(e.currentTarget).parents(".prod-item");
		var entity = $item.data("entity");
		
		common.gotoPage("../../product/product-detail.html?can_order=0&product_id="+entity.id);
	});
	
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
	var sum2free = parseFloat(feeruleitems[1]);//满多少包运费
	var $expressbar = $(".index-page .expressfee-area .sum");//$(".express-fee");
	if(totalSum < 0){
		$expressbar.hide();
		return 0;
	}
	if(totalSum < sum2free){
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
	var $footbar = $(".index-page .footbar");
	var $totalSum = $footbar.find(".footbar-bttn-right .ok");
	$totalSum.text("￥10.00 预订");
	
	$footbar.find(".prod-amount-unit").text(productEntity.unit);
	
	ProductSet.initAmountBttn($footbar.find(".prod-amount-bttn"),function(isAdd,amount,$item,event){
		if(amount < 0){
			amount = 0;
		}
		event.stopPropagation();
		showTotalSum(amount);
		
		refreshExpressFee(amount*productEntity.price);
	});
	
	//下单按钮
	widget.bindTouchClick($totalSum,function(){
		deliveryInfo = deliveryInfo || {};
		if(!deliveryInfo.name || !deliveryInfo.mobile || !deliveryInfo.address){
			message.errorHide("请补充收件人信息");
			return;
		}
		commitOrderCreate(deliveryInfo);
	});
	
	widget.bindTouchClick($footbar.find(".footbar-bttn-right .cancel"),function(){
		common.gotoPage("../../product/product-list.html");
	});
}
function back2prodList(){
	var orderItems = buildOrderItems(true);
	var orderMap = {};
	for(var i=0;i<orderItems.length;i++){
		orderMap[orderItems[i].product_id] = orderItems[i].amount;
	}
	addShoppingCart(orderMap);
	common.gotoPage("../product/product-list.html?order_id="+orderId);
}

var $progressDialog = null;
function commitOrderCreate(deliveryInfo){
	var $bttn = $(".index-page .footbar-bttn-ok");
	if(!enableBttn($bttn)){
		return;
	}
	
	var amount = $(".index-page .footbar .prod-amount-input").val();
	var premise = productEntity.premise || 1;
	if(premise > amount){
		message.errorHide("本产品"+premise+productEntity.unit+"起售");
		return;
	}
	
	var $titlebar = $(".index-page .titlebar");
	$progressDialog = message.progress2($titlebar);
	enableBttn($bttn,false);
	ajax.request({
		url : _base_url+"/web/order/preorder.do",
		need_progressbar : false,
		params : {
			name : deliveryInfo.name,
			mobile: deliveryInfo.mobile,
			address : deliveryInfo.address,
			product_id : productEntity.id,
			amount : parseInt(amount)
		},
		success : function(header,body){
			createPay(body.newid,body.sum);
		},
		error : function(){
			enableBttn($bttn,true);
			$progressDialog && message.hideDialog($progressDialog);
		}
	});
}

function createPay(orderId,sum,callback){
	ajax.request({
		url : _base_url+"/web/order/createPayByPingpp.do",
		need_progressbar : false,
		params : {
			order_id : orderId,
			sum : sum,
			pay_channel : 21
		},
		success : function(header,body){
			var charge = body.charge;
			pingpp.createPayment(charge, function(result, err){
				$progressDialog && message.hideDialog($progressDialog);
				if (result == "success") {
			        // 只有微信公众账号 wx_pub 支付成功的结果会在这里返回，其他的支付结果都会跳转到 extra 中对应的 URL。
			    	message.successHide("支付成功，正在跳转...",$titlebar);
			    	setTimeout(function(){
			    		common.gotoPage("../../order/order-list.html");
			    	},800);
			    } else if (result == "fail") {
			        // charge 不正确或者微信公众账号支付失败时会在此处返回
			    	message.errorHide(err.msg,$titlebar);
			    } else if (result == "cancel") {
			        // 微信公众账号支付取消支付
			    }
				enableBttn($(".index-page .footbar-bttn-ok"),true);
			});
		},
		error : function(){
			enableBttn($(".index-page .footbar-bttn-ok"),true);
			$progressDialog && message.hideDialog($progressDialog);
		}
	});
}

function showTotalSum(amount){
	var $totalSum = $(".index-page .totalsum");
	if($totalSum == null || $totalSum.length == 0){
		$totalSum = $("<div class='totalsum money-font'>").appendTo($(".index-page"));
		
	}
	var sum = amount*productEntity.price;
	$totalSum.text("￥"+sum.toFixed(2));
	$totalSum.show();
	
	widget.bindTouchClick($totalSum,function(e){
		$totalSum.hide();
		e.stopPropagation();
	});
}
