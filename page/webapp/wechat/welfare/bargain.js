var loading = new PageLoading();
var orderId;
var orderEntity;
var isCreator;
var isJoin;
var canBargain;
var isSubscribed;
var bargainList;
//var paySum;
//var remainSum;
$(function(){
	loading.show();
	initPage();
	showCustServiceIcon();
});

function initPage(){
	orderId = common.getUrlParams("order_id");
	if(!orderId){
		message.alert("请以正确的姿势打开~")
		return;
	}
	ajax.request({
		url : _base_url+"/web/bargain/queryOrderDetail.do",
		need_progressbar : false,
		params : {
			order_id : orderId,
			need_oauth:true,
			basedata:'baseparam',
			need_wx_signature:true
		},
		success : function(header,body){
			queryArticle("bargain_info",function(content){
				$(".index-page .bargain-intro .content").html(content);
			});
			
			orderEntity = body.order;
			isCreator = body.isCreator == 1;
			isJoin = body.isJoin == 1;
			canBargain = body.canBargain == 1;
			bargainList = body.bargainList;
			isSubscribed = body.isSubscribed == 1;
			//paySum = orderEntity.sum;
			initTitlebarBttns($(".titlebar"),{
				iconfont : 'menu',
				menu_items : getMenus()
			});
			
			initIndex();
			
			var firstProduct = body.order.items[0].product;
			var loginUser = common.getLoginUser();
			initWxConfig(body.wxSignature,{
				url : buildTransitionUrl(_base_host+"/wechat/welfare/bargain.html?order_id="+orderEntity.id+"&fromwx=1"),
				img : firstProduct.pic,//"http://resource.xiesange.com/image/prod/crab_3_2.jpg",
				timeline_desc : "#免费吃海鲜大餐！就是这么任性!# "+loginUser.name+"邀请您来帮Ta砍价，砍到0元免费吃！",
				message_title : "#免费吃海鲜大餐！就是这么任性!",
				message_desc : loginUser.name+ "邀请您来帮Ta砍价，砍到0元免费吃！"
			});
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initIndex(){
	initAdv();
	initOrderItems();
	initExpressFee();
	initFootbar();
	initBargainList();
}
function initAdv(){
	return;
	var $picSlider = $('.index-page .prod-pic-unslider');
	//$picSlider.attr("src",getSysparam("bargain_adv_pic"));
	
	var imgUrls = [];
	imgUrls.push('http://resource.xiesange.com/image/prod/crab/baixie/1.jpg');
	imgUrls.push('http://resource.xiesange.com/image/prod/crab/baixie/9.jpg');
	imgUrls.push('http://resource.xiesange.com/image/prod/crab/baixie/10.jpg');
	$picSlider.css("width",window.innerWidth);
	$picSlider.css("height",window.innerWidth);
	initUnslider($picSlider,imgUrls);
	/*widget.bindTouchClick($adv,function(){
		common.gotoPage("../product/product-list.html");
	});*/
};
function initOrderItems(){
	var orderItems = orderEntity.items;
	var $listDiv = $(".index-page .bargain-prodlist");
	if(orderItems == null || orderItems.lenght == 0){
		$listDiv.text("(无明细)");
		return;
	}
	
	var $tempDiv = $("#_orderitem_temp");
	var $newDiv = null;
	var itemEntity = null;
	var prodEntity = null;
	var priceNotSure = false;
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
		ProductSet.init($newDiv,prodEntity,{size:60});
		$newDiv.find(".payitem-amount").text(itemEntity.amount+prodEntity.unit);
		$newDiv.find(".payitem-sum").text("￥"+itemEntity.sum.toFixed(2));
		
		
		/*continue;
		var name = prodEntity.name;
		if(prodEntity.spec){
			name = name+"("+prodEntity.spec+")";
		}
		$newDiv.find(".prodname").text(name+"：");
		$newDiv.find(".amount").text(itemEntity.amount+prodEntity.unit);
		$newDiv.find(".prodprice").text(isPriceNotSure?"价格待定":"￥"+itemEntity.price+"/"+prodEntity.unit);
		var $sum = $newDiv.find(".sum");
		$sum.text(isPriceNotSure?"价格待定":"￥"+itemEntity.sum.toFixed(2));
		isPriceNotSure && $sum.addClass("money-font");
		
		$newDiv.show();*/
	}
	
	$(".bargain-origsum .sum").text("￥"+orderEntity.origSum.toFixed(2));
	return priceNotSure;
}

function initExpressFee(){
	
	var $bagainBlock = $(".index-page .bargain-expressfee");
	var sum4expressfree = getSum4ExpressFeeFree();
	initPromotionList($bagainBlock,[{type:'express',text:"运费"}]);
	
	var $promItem = $bagainBlock.children().eq(0);
	$promItem.addClass("express-fee");
	var actionValue = orderEntity.expressFee;
	$("<span class='tip gray-font'>").appendTo($promItem).text(buildExpressFeeText(sum4expressfree,true));
	var $feesum = $("<span class='sum money-font'>").appendTo($promItem);
	if(actionValue > 0){
		$feesum.text("￥"+actionValue.toFixed(2));
	}else{
		$feesum.remove("money-font").addClass("gray-font").css("font-size","1rem");
		$feesum.text("免运费");
	}
}

function initFootbar(){
	var $okBttn = $(".index-page .footbar-bttn-ok");
	var $cancelBttn = $(".index-page .footbar-bttn-cancel");
	if(!canBargain){
		$okBttn.text("砍价期限已过");
		$okBttn.addClass("disable-bttn");
		
		$cancelBttn.text("我也要吃海鲜");
		widget.bindTouchClick($cancelBttn,function(){
			common.gotoPage("../product/product-list.html");
		});
	}else if(isCreator){
		$okBttn.text("确定支付:￥"+(orderEntity.sum+orderEntity.expressFee).toFixed(2));
		widget.bindTouchClick($okBttn,function(){
			commitPay();
		});
		
		$cancelBttn.text("返回正常支付");
		widget.bindTouchClick($cancelBttn,function(){
			common.gotoPage("../order/pay.html?order_id="+orderEntity.id);
		});
		
	}else if(isJoin){
		$okBttn.text("您已帮Ta砍过价");
		$okBttn.addClass("disable-bttn");
		
		$cancelBttn.text("我也要吃海鲜");
		widget.bindTouchClick($cancelBttn,function(){
			common.gotoPage("../product/product-list.html");
		});
	}else{
		$okBttn.text("帮Ta砍价");
		widget.bindTouchClick($okBttn,function(){
			if(!isSubscribed){
				showSubscribQR("为了提供更好的服务，随时接受海鲜特价资讯，请您先关注蟹三哥公众号");
				return;
			}
			commitBargain();
		});
		
		$cancelBttn.text("我也要吃海鲜");
		widget.bindTouchClick($cancelBttn,function(){
			common.gotoPage("../product/product-list.html");
		});
	}
}

function initBargainList(){
	var $listDiv = $(".index-page .bargain-member-list"); 
	if(bargainList == null || bargainList.length == 0){
		showEmptyBargain();
		return;
	}
	var totalSum = 0;
	for(var i=0;i<bargainList.length;i++){
		var bargain = bargainList[i];
		var $item = $("<div class='bargin-memeber-item'><span class='index'></span><span class='name'></span><span style='float:right'><span class='sum-tip gray-font'>砍掉:</span><span class='sum xsg-font'></span></span></div>").appendTo($listDiv);
		$item.find(".index").text(i+1);
		$item.find(".name").text(bargain.name || "匿名好友");
		$item.find(".sum").text(bargain.sum+"元");
		if(i > 0){
			$item.css("margin-top","10px");
		}
		totalSum += parseFloat(bargain.sum);
	}
	var $reducesum = $(".index-page .bargain-reducesum");
	$reducesum.find(".amount").text(bargainList.length);
	$reducesum.find(".sum").text(totalSum.toFixed(2)+'元');
	$reducesum.find(".ta").text(isCreator?"您":"Ta");
	$reducesum.find('.icon').css("background-color",promotionIcon[3].color);
	$(".index-page .bargain-remainsum .sum").text(orderEntity.sum+'元');
}

function commitBargain(){
	var $titlebar = $(".index-page .titlebar");
	ajax.request({
		url : _base_url+"/web/bargain/bargain.do",
		need_progressbar : $titlebar,
		params : {
			order_id:orderId
		},
		success : function(header,body){
			if(body.isSubscribed == 1){
				message.alert("您已成功帮Ta砍掉:"+body.bargainSum+"元",null,function(){
					location.reload();
				});
			}/*else{
				var str = "<span class='xsg-font'>您已成功帮Ta砍掉:"+body.bargainSum+"元</span>";
				showSubscribQR(str+"<BR/><BR/>为了提供更好的服务，随时接受海鲜特价资讯，请您关注蟹三哥公众号",function(){
					location.reload();
				});
			}*/
		},
		complete : function(){
		}
	});
}

function showEmptyBargain(){
	var $listDiv = $(".index-page .bargain-member-list"); 
	var $emptyLine = $("<div class='empty-line overflow-hidden gray-font'><div class='label'></div><div class='invite common-bg'></div></div>").appendTo($listDiv);
	if(isCreator){
		$emptyLine.find(".label").text("目前还没有好友帮您砍价~");
		$emptyLine.find(".invite").text("邀请好友来砍价");
		widget.bindTouchClick($emptyLine.find(".invite"),function(){
			app.creatWxTopRightIndicator("点击右上角菜单，分享到朋友圈或者发送给您的朋友来帮忙砍价吧~",true,true);
		});
	}else{
		$emptyLine.find(".label").text("目前还没有好友帮Ta砍价~");
		$emptyLine.find(".invite").remove();
	}
	
	$(".index-page .bargain-sum").hide();
}

function commitPay(){
	var $bttn = $(".index-page .footbar-bttn-ok");
	if($bttn.hasClass('disable-bttn')){
		return;
	}
	var $titlebar = $(".titlebar");
	$bttn.addClass('disable-bttn');
	
	ajax.request({
		url : _base_url+"/web/order/createPayByPingpp.do",
		params : {
			order_id : orderEntity.id,
			sum : orderEntity.sum+orderEntity.expressFee,
			pay_channel : 21,
			need_bargain:1
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
				/*console.log(result);
			    console.log(err.msg);
			    console.log(err.extra);*/
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
}

