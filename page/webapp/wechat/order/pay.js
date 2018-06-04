var loading = new PageLoading($(".index-page"));
var orderEntity ;
var promotionList;
var chooseCoupon;
var paySum;
var editable = false;
$(function(){
	loading.show();
	initPage();
	//showCustServiceIcon();
});
function initPage(){
	var orderId = common.getUrlParams("order_id");
	if(!orderId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	
	ajax.request({
		url : _base_url+"/web/order/queryDetail.do",
		need_progressbar : false,
		params : {
			need_oauth:true,
			basedata:'baseparam',
			order_id:orderId,
			need_promotion_list:1,
			need_oauth:true,
			need_wx_signature:true
		},
		success : function(header,body){
			orderEntity = body.order;
			//右上角菜单按钮点击
			var titleBttns = [];
			if(isAdmin()){
				//管理员有管理按钮
				titleBttns.push({
					iconfont : 'adminOp',
					handler : function(){
						common.gotoPage("../manage/m-order-modify.html?order_id="+orderEntity.id);
					}
				});
			}
			titleBttns.push({
				iconfont : 'menu',
				menu_items : getMenus()
			});
			initTitlebarBttns($(".index-page .titlebar"),titleBttns);
			
			promotionList = body.promotionList;
			paySum = orderEntity.sum + orderEntity.expressSum;
			editable = body.isCreator == 1 && (orderEntity.status==0 || orderEntity.status==98 || orderEntity.buyType==4);
			initIndex(
				body.couponList,
				body.bargainSum);
			
			var totalSum = orderEntity.sum+orderEntity.expressSum;
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/order/pay.html?order_id="+orderId+"&fromwx=1"),
				img : logoUrl,
				timeline_desc : "#蟹三哥# 订单详情￥"+totalSum+" "+orderEntity.code,
				message_title : "#蟹三哥# 订单详情￥"+totalSum+" "+orderEntity.code,
				message_desc : "经营纯正的东海野生海鲜"
			});
		},
		complete : function(){
			loading.hide();
		}
	});
	
}

function initIndex(couponList,bargainSum){
	var $indexPage = $(".index-page");
	//编码栏
	var $codeBar = $('.order-titleline');
	$codeBar.find(".order-code").text("订单编号:"+orderEntity.id);
	$codeBar.find(".create-time").text(orderEntity.createTime);
	
	//收件栏
	var $deliveryBar = $('.deliverybar');
	var $deliveryPage= $(".delivery-page");
	var deliveryInfo = {
		name : orderEntity.name,
		mobile : orderEntity.mobile,
		address : orderEntity.address
	}
	initDeliveryBar($('.deliverybar'),editable ? $deliveryPage : null,deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		ajax.request({
			url : _base_url+"/web/order/modify.do",
			params : {
				order_id : orderEntity.id,
				mobile : newDeliveryInfo.mobile == orderEntity.mobile ? null : newDeliveryInfo.mobile,
				vcode : newDeliveryInfo.vcode,
				name : newDeliveryInfo.name == orderEntity.name ? null : newDeliveryInfo.name,
				address : newDeliveryInfo.address == orderEntity.address ? null : newDeliveryInfo.address
			},
			need_progressbar : $(".titlebar"),
			success : function(header,body){
				refreshDeliveryBar($deliveryBar,newDeliveryInfo.name,newDeliveryInfo.mobile,newDeliveryInfo.address);
				$deliveryPage.hide();
				successCallback && successCallback.apply();
			},
			complete :function(data,textStatus){
				completeCallback && completeCallback.apply(null,[data,textStatus]);
			}
		});
	});
	
	//订单内容
	var itemTotalSum = initOrderItems(orderEntity,orderEntity.items);
	
	//订单总金额
	var $orderSum = $(".pay-totalsum .sum");
	var $origSum = $(".pay-totalsum .origsum");
	var $status =  $(".pay-totalsum .order-status");
	
	var statusObj = getOrderStatus(orderEntity);
	$status.text(statusObj.text).css("color",statusObj.color || "#01C8D8")
	
	var isAjdust = orderEntity.status == 98;
	var isEdit = orderEntity.status == 0;
	if(isAjdust){
		$orderSum.text("￥"+orderEntity.sum.toFixed(2));
		if(orderEntity.origSum > orderEntity.sum){
			$origSum.text("￥"+orderEntity.origSum.toFixed(2));
		}
		$(".index-page .promotion-block").hide();
		$(".index-page .promotion-block.paychannel").show();
		$(".index-page .promotion-block.expresssum").show();
		initPromotionArea(couponList,true,isEdit);
	}else{
		//促销活动
		initPromotionArea(couponList,false,isEdit);
		couponList && useCoupon(couponList[0]);
		$orderSum.text("￥"+orderEntity.origSum.toFixed(2));
		
		if(!isEdit){
			//已经是已支付状态，则把支付方式、优惠券（如果没使用）隐藏
			$(".index-page .promotion-block.paychannel").hide();
			if(couponList == null || couponList.length == 0){
				$(".index-page .promotion-block.reduce").hide();
			}
		}
		
	}
	
	initFootbar();

	if(editable && !isAjdust){
		refreshPaySum();
	}
	
}

function initFootbar(){
	var isPayed = orderEntity.status != 0 && orderEntity.status != 98;
	var isAjdust = orderEntity.status == 98;
	var isPreorder = orderEntity.buyType == 4;
	
	//支付金额
	var $payBttn = $(".index-page .footbar-bttn-ok");
	var $sumText = $payBttn.find(".sum-text");
	var $sumValue = $payBttn.find(".sum-value");
	var $cancelBttn = $(".index-page .footbar-bttn-cancel");
	/*if(isAjdust){
		$sumText.text("已调价:");
	}else */if(isPreorder){
		$sumText.text("支付余款:");
	}else if(isPayed){
		$payBttn.addClass("disable-bttn");
		$sumText.text("已完成支付:");
	}else{
		$sumText.text("实付:");
	}
	$sumValue.text("￥"+paySum.toFixed(2));
	
	if(!$payBttn.hasClass('disable-bttn')){
		widget.bindTouchClick($payBttn,function(e){
			var deliveryInfo = $(".delivery-page").data("deliveryInfo");
			commitPay(orderEntity,chooseCoupon);
		});
	}
	
	//修改订单，只有在未支付场景下才能修改
	if(orderEntity.status == 0){
		$cancelBttn.show();
		widget.bindTouchClick($cancelBttn,function(e){
			if(!enableBttn($cancelBttn)){
				return;
			}
			enableBttn($cancelBttn,false);
			ajax.request({
				url : _base_url+"/web/order/remove.do",
				params : {
					order_id : orderEntity.id
				},
				need_progressbar : $(".index-page .titlebar"),
				success : function(header,body){
					common.pageBack();
				},
				complete : function(){
					enableBttn($cancelBttn,true);
				}
			});
			
			
		});
	}else{
		$cancelBttn.remove();
		$payBttn.addClass("fill");
	}
}

function initOrderItems(order,orderItems){
	var $listDiv = $(".pay-prodlist");
	if(orderItems == null || orderItems.lenght == 0){
		$listDiv.text("(无明细)");
		return;
	}
	
	var $tempDiv = $("#_payitem_temp");
	var $newDiv = null;
	var itemEntity = null;
	var prodEntity = null;
	var orderSum = 0;
	for(var i=0;i<orderItems.length;i++){
		if(i > 0){
			var $sepDiv = $("<div>").appendTo($listDiv);
			createSplitline().appendTo($sepDiv);
		}
		itemEntity = orderItems[i];
		prodEntity = itemEntity.product;
		var isPriceNotSure = prodEntity.price < 0;
		if(isPriceNotSure){
			priceNotSure = true;
		}
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","orderitem_"+itemEntity.id);
		prodEntity.price = itemEntity.price || prodEntity.price;
		ProductSet.init($newDiv,prodEntity,{size:50});
		
		$newDiv.find(".payitem-amount").text(itemEntity.amount+prodEntity.unit);
		$newDiv.find(".payitem-sum").text("￥"+itemEntity.sum.toFixed(2));
		orderSum += itemEntity.price * itemEntity.amount;
	}
	return orderSum;
}

function useCoupon(coupon){
	orderEntity.couponId = coupon && coupon.id;
	chooseCoupon = coupon;
	//return;
	
	//$(".footbar-bttn-ok").data("coupon",coupon);//存下来，提交订单时需要用到
	var isAdjust = orderEntity.status == 98;
	if(!enableBttn($(".footbar-bttn-ok")) || isAdjust){
		return;
	}
	var $couponArea = $("#promotion_coupon");
	var couponProm = $couponArea.data("entity");
	//计算优惠额度
	var reduceSum = 0;
	
	if(coupon.type == 1){
		//折扣券
		var origSum = orderEntity.origSum;
		var $promitems = $(".index-page .promotion-block.reduce .action-value");
		for(var i=0;i<$promitems.length;i++){
			var $promItem = $promitems.eq(i);
			var prom = $promItem.data("entity");
			if(prom != null && prom.type != 'coupon' && prom.actionValue){
				origSum += prom.actionValue;
			}
		}
		reduceSum = (origSum * (100-coupon.value))/100;
	}else if(coupon.type == 2){
		//现金券
		reduceSum = parseFloat(coupon.value);
	}
	couponProm.detail = buildCouponText(coupon);
	couponProm.actionValue = -1*reduceSum;
	refreshPromotion(couponProm);
	
	
	
}

function commitPay(order,coupon){
	var $bttn = $(".index-page .footbar-bttn-ok");
	if($bttn.hasClass('disable-bttn')){
		return;
	}
	var $titlebar = $(".titlebar");
	$bttn.addClass('disable-bttn');
	var paychannel = $(".promotion-block.paychannel").children().eq(0).data("entity").type;
	if(paychannel == 'alipay'){
		common.gotoPage("pay_alipay_qrcode.html?order_id="+orderEntity.id+"&pay_sum="+paySum+"&linkman="+orderEntity.name);
		return;
	}
	ajax.request({
		url : _base_url+"/web/order/createPayByPingpp.do",
		params : {
			order_id : order.id,
			sum : paySum,
			pay_channel : 21,
			coupon_id : coupon ? coupon.id : null
		},
		need_progressbar : $titlebar,
		error : function(header,body){
			if(header.error_code == 210203){
				message.alert(header.error_message,function(){
					location.reload();
				});
				return false;
			}
		},
		success : function(header,body){
			var charge = body.charge;
			
			pingpp.createPayment(charge, function(result, err){
			    if (result == "success") {
			        // 只有微信公众账号 wx_pub 支付成功的结果会在这里返回，其他的支付结果都会跳转到 extra 中对应的 URL。
			    	message.successHide("支付成功，正在跳转...",$titlebar);
			    	setTimeout(function(){
			    		common.gotoPage("order-list.html");
			    	},800);
			    } else if (result == "fail") {
			        // charge 不正确或者微信公众账号支付失败时会在此处返回
			    	message.errorHide(err.msg,$titlebar);
			    } else if (result == "cancel") {
			        // 微信公众账号支付取消支付
			    }
			});
		},
		complete : function(){
			enableBttn($bttn,true);
		}
	});
	
	//common.gotoPage("pay-temp.html?mobile="+deliveryInfo.mobile+"&pay_sum="+order.sum+"&order_id="+order.id+"&coupon_id="+(coupon?coupon.id:""));
}

function initPromotionArea(couponList,isAdjust,isEdit){
	if(!isAdjust){
		var bargainProm = null;
		var newPromList = [];
		
		if(promotionList){
			for(var i=0;i<promotionList.length;i++){
				var promotion = promotionList[i];
				if(promotion.type == 3){
					bargainProm = promotion;
				}else{
					newPromList.push(promotion);
					promotion.actionValue = -1*promotion.value;
				}
			}
		}
		//再加上优惠券，放到最后
		var couponProm = {type:'coupon',text:"优惠券",detail_align:"right",detail_class:"gray-font coupon-detail"};
		if(couponList){
			if(isEdit){
				//只有待支付状态才需要能够操作优惠券
				for(var k=0;k<couponList.length;k++){
					var cp = couponList[k];
					cp.text = buildCouponText(cp)+"<span class='pay-coupon-expiretime'>("+cp.expireTime+"过期)</span>";
					cp.handler = function(entity){
						useCoupon(entity);
						refreshPaySum();
					}
				}
				couponProm.handler = function(promEntity,$promItem){
					widget.popupMenu(null,"bottom top",couponList,{need_titlebar:"选择优惠券"});
				}
			}
			couponProm.actionValue = "<span class='gray-font'>选择优惠券</span>";
		}else if(isEdit){
			couponProm.actionValue = "无可用优惠券,不妨前去试试手气";
			couponProm.handler = function(e){
				common.gotoPage("../welfare/coupon.html?order_id="+orderEntity.id);
			}
		}
		newPromList.push(couponProm);
		var $promBlock = $(".index-page .promotion-block.reduce");
		initPromotionList($promBlock,newPromList);
		
		
		//处理bargain区
		var $bagainBlock = $(".index-page .promotion-block.bargain");
		if(orderEntity.status == 0 && bargainProm){
			bargainProm.handler = function(){
				common.gotoPage("../welfare/bargain.html?order_id="+orderEntity.id);
			};
			initPromotionList($bagainBlock,[bargainProm]);
		}else{
			$bagainBlock.remove();
		}
		
		//定金
		var $preorderBlock = $(".index-page .promotion-block.preorder");
		if(orderEntity.buyType == 4){
			initPromotionList($preorderBlock,[{type:'preorder',text:"已付定金",actionValue:-1*orderEntity.sum}]);
		}else{
			$preorderBlock.remove();
		}
	}
	
	//运费区
	var $bagainBlock = $(".index-page .promotion-block.expresssum");
	var sum4expressfree = getSum4ExpressFeeFree();
	var expressFee = orderEntity.expressSum;
	var	detail = sum4expressfree < 0 || orderEntity.buyType==2 ? "" : buildExpressFeeText(sum4expressfree);
	
	actionValue = expressFee > 0 ? expressFee : "免运费";
	
	initPromotionList($bagainBlock,[{type:'express',text:"运费",actionValue:actionValue,detail:"<span class='gray-font'>"+detail+"</span>"}]);
	
	
	//支付方式
	var $paychannelBlock = $(".index-page .promotion-block.paychannel");
	var paychannelProm = {type:'wechat',text:"微信支付",handler:function(){
		var popupMenus = [{
			iconfont : iconfontset.wechat,
			icon_color : promotionIcon.wechat.color,
			text : "微信支付",
			handler : function(){
				paychannelProm.type = 'wechat';
				paychannelProm.text = '微信支付';
				refreshPromotion(paychannelProm,$paychannelBlock.children().eq(0));
			}
		},{
			iconfont : iconfontset.alipay,
			icon_color : promotionIcon.alipay.color,
			text : "支付宝",
			handler : function(){
				paychannelProm.type = 'alipay';
				paychannelProm.text = '支付宝';
				refreshPromotion(paychannelProm,$paychannelBlock.children().eq(0));
			}
		}]
		widget.popupMenu(null,"bottom top",popupMenus,{need_titlebar:false,remove_when_close:true});
	}};
	initPromotionList($paychannelBlock,[paychannelProm]);
}

function refreshPaySum(){
	paySum = orderEntity.origSum;
	
	var $promitems = $(".index-page .promotion-block.reduce .action-value");
	for(var i=0;i<$promitems.length;i++){
		var $promItem = $promitems.eq(i);
		var prom = $promItem.data("entity");
		if(prom != null && prom.actionValue){
			paySum += prom.actionValue;
		}
	}
	
	var $preorderItem = $(".index-page .promotion-block.preorder").children().eq(0);
	var preorderEntity = $preorderItem.data("entity");
	if(preorderEntity != null && preorderEntity.actionValue){
		paySum += preorderEntity.actionValue;
	}
	
	
	//重新计算运费,如果是已预订或者调价状态下，不需要再次计算运费
	var expressFee = 0;
	var sum4expressfree = getSum4ExpressFeeFree();
	var $expressFee = $(".promotion-block.expresssum .sum");
	if(orderEntity.buyType == 2 || orderEntity.status == 1 || orderEntity.status == 98){
		expressFee = orderEntity.expressSum;
	}else if(sum4expressfree < 0 || paySum < sum4expressfree){
		expressFee = getExpressFee();
	}
	var $expressfeeProm = $("#promotion_express");
	var expressProm = $expressfeeProm.data("entity");
	expressProm.actionValue = expressFee==0?"免运费":expressFee;
	refreshPromotion(expressProm);
	
	
	paySum = paySum+expressFee;
	$(".index-page .footbar-bttn-ok .sum-value").text("￥"+paySum.toFixed(2));
		
	return paySum;
}

