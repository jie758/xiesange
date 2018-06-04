var orderEntity;//创建者订单，包含了寄送地址
var selfOrderEntity;//当前用户的订单
var orderList;
var productEntity;
var promotion;
var loading = new PageLoading();
$(function(){
	loading.show();
	initPage();
	showCustServiceIcon();
});
function initPage(){
	//alert(window.location.href);
	var orderId = common.getUrlParams("order_id");
	if(!orderId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	
	ajax.request({
		url : _base_url+"/web/groupbuy/queryDetail.do",
		need_progressbar : false,
		params : {
			order_id:orderId,
			need_oauth:true,
			need_wx_signature:true
			
		},
		success : function(header,body){
			//右上角菜单按钮点击
			initTitlebarBttns($(".index-page .titlebar"),{
				iconfont : 'menu',
				menu_items : getMenus()
			});
			
			orderList = body.orderList;
			productEntity = body.product;
			promotion = body.promotion;
			//productEntity.origPrice = productEntity.price;
			productEntity.price = promotion.value;
			for(var i=0;i<orderList.length;i++){
				if(orderList[i].groupbuyId == -1){
					orderEntity = orderList[i];
				}
				if(orderList[i].isSelf == 1){
					selfOrderEntity = orderList[i];
				}
			}
			
			initIndex(
				body.isJoined == 1,
				body.isCreator == 1,
				body.isClosed == 1,
				body.expireTime);
			
			var loginUser = common.getLoginUser();
			var title = "#我要吃海鲜！"+loginUser.name+"邀请你来拼单"+body.product.name+"#";
			var desc = "蟹三哥,地道的东海野生海鲜,拼单更优惠,快来和我一起拼海鲜吧~";
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/order/pay-groupbuy.html?type=follow&order_id="+orderId+"&fromwx=1"),
				img : body.product.pic,
				timeline_desc : title+" "+desc,
				message_title : title,
				message_desc : desc
			});
			
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
	
}

function initIndex(isJoined,isCreator,isClosed,expireTime){
	var $indexPage = $(".index-page");
	
	/*var $expireText = $indexPage.find(".groupbuy-expire-text");
	$expireText.text("本次拼单截止时间："+expireTime);*/
	//收件栏
	var $deliveryBar = $('.deliverybar');
	var $deliveryPage= $(".delivery-page");
	var deliveryInfo = {
		name : orderEntity.name,
		mobile : orderEntity.mobile,
		address : orderEntity.address
	}
	initDeliveryBar($('.deliverybar'),(!isCreator || isClosed)?null:$(".delivery-page"),deliveryInfo,function(newDeliveryInfo,successCallback,completeCallback){
		ajax.request({
			url : _base_url+"/web/groupbuy/modify.do",
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
	
	//优惠规则
	initPromotion();
	
	//拼单产品
	var totalAmount = 0;
	var totalCust = 0;
	var totalSum = 0;
	var unpayAmount = 0;//未支付总斤数
	var currentItem = null;
	for(var i=0;i<orderList.length;i++){
		var order = orderList[i];
		var orderItem = order.items[0];
		if(order.status != 0){
			totalAmount += orderItem.amount;
			totalCust++;
		}else{
			unpayAmount += orderItem.amount;
		}
		totalSum += order.sum;
		if(orderList[i].isSelf == 1){
			currentItem = orderItem;//当前用户的明细
		}
	}
	initGroupbuyProd();
	
	//订单内容
	initOrderItems();
	
	//订单总金额
	$(".pay-totalamount").html("已付:共"+totalCust+"人,"+totalAmount+"斤"+(unpayAmount==0?"":"; <span class='gray-font'>未付:共"+(orderList.length-totalCust)+"人,"+unpayAmount+"斤</span>"));
	$(".pay-origsum").text("总额:￥"+totalSum.toFixed(2));
	
	
	//邀请好友按钮
	initFootbarButtons(isJoined,isCreator,isClosed);
	
}


function initFootbarButtons(isJoined,isCreator,isClosed){
	var $buttonbar = $(".index-page .footbar");
	var $bttnOk = $buttonbar.find(".footbar-bttn-ok");
	var $bttnCancel = $buttonbar.find(".footbar-bttn-cancel");
	
	var isPayed = selfOrderEntity ? selfOrderEntity.status > 0 : false;
	//isClosed = true;
	if(isClosed){
		var isComplete = orderEntity.status == 99;
		if(isComplete){
			//整个订单已经完成
			$bttnCancel.text("看看其他海鲜");
			widget.bindTouchClick($bttnCancel,function(e){
				common.gotoPage("../product/product-list.html");
			});
			
			$bttnOk.text("已完成"+(isPayed?":￥"+selfOrderEntity.sum.toFixed(2):""));
			$bttnOk.addClass("disable-bttn");
		}else{
			if(isCreator){
				//未完成，创建者可以发送通知
				$bttnCancel.text("发送到货通知");
				widget.bindTouchClick($bttnCancel,function(e){
					message.confirmSlide("确定对所有团员发送到货通知?",function(){
						sendArrivalNotify();
					});
					
				});
			}else{
				$bttnCancel.text("看看其他海鲜");
				widget.bindTouchClick($bttnCancel,function(e){
					common.gotoPage("../product/product-list.html");
				});
			}
			
			$bttnOk.text("已截单"+(isPayed?":￥"+selfOrderEntity.sum.toFixed(2):""));
			$bttnOk.addClass("disable-bttn");
		}
		
		
	}else if(!isJoined){
		$bttnOk.text("立即加入拼单");
		widget.bindTouchClick($bttnOk,function(e){
			common.gotoPage("../product/groupbuy.html?type=follow&order_id="+orderEntity.id);
		});
		
		$bttnCancel.text("看看其他海鲜");
		widget.bindTouchClick($bttnCancel,function(e){
			common.gotoPage("../product/product-list.html");
		});
		
	}else{
		if(isPayed){
			//如果已支付
			$bttnOk.text("已支付:￥"+selfOrderEntity.sum.toFixed(2));
			$bttnOk.addClass("disable-bttn");
			$bttnOk.css("left","150px");
			
			//左侧按钮
			if(isCreator){
				//创始人则显示截单按钮
				$bttnCancel.text("立即截单");
				$bttnCancel.width(150);
				widget.bindTouchClick($bttnCancel,function(e){
					message.confirm("截单后本次拼单将会结束并安排发货，拼单中未支付的小伙伴们将会被视为放弃。<BR/>确定截单？",function(){
						commitClose();
					});
				});
			}else{
				$bttnCancel.text("邀请更多好友加入");
				$bttnCancel.width(150);
				widget.bindTouchClick($bttnCancel,function(e){
					showInviteArrow(true);
				});
			}
		}else{
			$bttnOk.text("确定支付:￥"+selfOrderEntity.sum.toFixed(2));
			widget.bindTouchClick($bttnOk,function(e){
				var $bttn = $(".index-page .footbar-bttn-ok");
				if($bttn.hasClass('disable-bttn')){
					return;
				}
				commitPay(selfOrderEntity);
			});
			
			$bttnCancel.text("修改");
			widget.bindTouchClick($bttnCancel,function(e){
				common.gotoPage("../product/groupbuy.html?type=modify&order_id="+orderEntity.id);
			});
			
		}
	}
	$buttonbar.show();
	
}

function initGroupbuyProd(){
	var $prodarea = $(".prod-item");
	var $pic = $prodarea.find(".prod-pic");
	var picWidth = $pic.width();
	//productEntity.pic = null;//不让ProductSet.init方法不初始化默认的pic样式
	ProductSet.init($prodarea,productEntity,{size:picWidth,pic_slider:true});
	
	return;
	
}


function initOrderItems(){
	var $listDiv = $(".groupbuy-itemlist");
	if(orderList == null || orderList.lenght == 0){
		$listDiv.text("(无明细)");
		return;
	}
	
	var $tempDiv = $("#_payitem_temp");
	var $newDiv = null;
	var orderItemEntity = null;
	//var priceNotSure = false;
	for(var i=0;i<orderList.length;i++){
		if(i > 0){
			createSplitline().appendTo($listDiv);
		}
		var order = orderList[i];
		orderItemEntity = order.items[0];
		//var userEntity = common.matchItem(orderLists[i].userId,userList,'id');
		
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","orderitem_"+orderEntity.id);
		$newDiv.data("entity",order);
		if(order.groupbuyId == -1){
			//如果是发起者订单条目，则用icon标识
			$newDiv.find(".payitem-custinfo .creator")
				.addClass("xsg-fontset xsg-font").show();
		}
		$newDiv.find(".payitem-custinfo .index").text(i+1);
		$newDiv.find(".payitem-custinfo .name").text(order.name);
		if(order.mobile){
			$newDiv.find(".payitem-custinfo .mobile").text(order.mobile);
			if(order.mobile.indexOf("*") == -1){
				$newDiv.find(".dial").attr("href","tel:"+order.mobile);
			}else{
				$newDiv.find(".dial").remove();
			}
		}
		
		$newDiv.find(".payitem-amount").text(orderItemEntity.amount+productEntity.unit);
		$newDiv.find(".payitem-prodprice").text("￥"+orderItemEntity.price+"/"+productEntity.unit);
		if(order.origSum > order.sum){
			$newDiv.find(".payitem-origsum").text("￥"+order.origSum.toFixed(2));
		}else{
			$newDiv.find(".payitem-origsum").remove();
		}
		$newDiv.find(".payitem-sum").text("￥"+order.sum.toFixed(2));
		
		var $paystatus = $newDiv.find(".payitem-orderinfo .paystatus");
		if(order.status == 0){
			$paystatus.text("未付 ");
			$paystatus.addClass("gray-font");
			$newDiv.addClass("unpay");
		}else{
			$paystatus.text("已付 ");
			$paystatus.addClass("highlight-font");
		}	
		
		$newDiv.show();
	}
	
	if(common.getLoginUser().isAdmin == 1){
		//未付款需要可以点击通知支付
		widget.bindTouchClick($listDiv.find(".groupbuy-orderitem.unpay"),function(e){
			if($(e.target).hasClass("dial")){
				return;//点击的是电话
			}
			var $item = $(e.currentTarget);
			var entity = $item.data("entity");
			message.confirmSlide("确定发送支付提醒?",function(){
				sendRemindPayNotify(entity);
			});
		});
	}
}
function commitClose(){
	var $bttn = $(".index-page .footbar-bttn-cancel");
	if($bttn.hasClass('disable-bttn')){
		return;
	}
	$bttn.addClass('disable-bttn');
	var $titlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/groupbuy/close.do",
		params : {
			order_id : orderEntity.id
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			message.successHide("截单成功，正在跳转...",$titlebar);
			common.gotoPage("../order/order-list.html",1200);
		},
		complete : function(){
			enableBttn($bttn,true);
		}
	});
}
function commitPay(selfOrderEntity){
	var $bttn = $(".index-page .footbar-bttn-ok");
	var $titlebar = $(".titlebar");
	$bttn.addClass('disable-bttn');
	
	ajax.request({
		url : _base_url+"/web/order/createPayByPingpp.do",
		params : {
			order_id : selfOrderEntity.id,
			sum : selfOrderEntity && selfOrderEntity.sum,
			pay_channel : 21
		},
		need_progressbar : $titlebar,
		error : function(header,body){
			if(header.error_code == 210203){
				message.alert(header.error_message,null,function(){
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
			    	showInviteArrow(function($indicator){
						$indicator.hide();
						message.successHide("支付成功，正在跳转...",$titlebar);
						setTimeout(function(){
							location.reload();
				    	},1200);
					});
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
}

function initPromotion(){
	var $promBlock = $(".index-page .promotion-block");
	var $promList = initPromotionList($promBlock,[promotion],productEntity);
	var $promItem = $promBlock.children().eq(0);
	
	var $matched = $("<span class='is-matched'>").appendTo($promItem);
	if(promotion.isMatched){
		$matched.text("已符合").addClass("true");
	}else{
		$matched.text("未符合").addClass("false");
	}
	return;
	
	
	var $promItem = $(".index-page .promotion-item");
	
	var iconInfo = promotionIcon[promotion.type];
	var $iconText = $("<div class='icon-label'><div>"+iconInfo.text+"</div></div>").appendTo($promItem);
	$iconText.find("div").css("background-color",iconInfo.color);
	$("<span class='promotion-label'>"+promotion.text+"</span>").appendTo($promItem);
	
	var $matched = $("<span class='is-matched'>").appendTo($promItem);
	if(promotion.isMatched){
		$matched.text("已符合").addClass("true");
	}else{
		$matched.text("未符合").addClass("false");
	}
	
	$promItem.data("entity",promotion);
	
	/*$promItem.text(promotion.text);
	var $matched = $("<span class='is-matched'>").appendTo($promItem);
	if(promotion.isMatched){
		$matched.text("已符合");
	}else{
		$matched.text("未符合").addClass("gray-font");
	}*/
}

function showInviteArrow(clickHandler){
	app.creatWxTopRightIndicator("点击右上角菜单，分享到朋友圈或者发送给您的朋友来参与本次拼单活动吧~",true,clickHandler);
}

function sendArrivalNotify(){
	var $titlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/groupbuy/sendArrivalNotify.do",
		params : {
			order_id : orderEntity.id
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			message.successHide("操作成功",$titlebar);
		}
	});
}

function sendRemindPayNotify(orderEntity){
	var $titlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/manage/remindOrderPay.do",
		params : {
			order_id : orderEntity.id
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			message.successHide("操作成功",$titlebar);
		}
	});
}
