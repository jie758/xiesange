var loading = new PageLoading();
$(function(){
	loading.show();
	initPage();
});

function initPage(){
	var orderId = common.getUrlParams("order_id");
	var $titlebar = $(".titlebar");
	
	var $vcodeSet = VCodeSet.init($(".vcodeset-container"),{
		next_button:false,
		success : function($mobile,$vcodeInput,$vcodeBttn){
			message.successHide("验证码已发送至"+$mobile.val(),$(".titlebar.linkinfo"));
		},
		error : function(errorMsg){
			message.errorHide(errorMsg,$(".titlebar.linkinfo"));
		},
		vcode_changed : function($mobile,$vcodeInput,$vcodeBttn){
		},
		mobile_changed : function($mobile,$vcodeInput,$vcodeBttn){
			var val = $mobile.val();
			var loginUser = common.getLoginUser() || {};
			if(common.checkMobile(val) && val != loginUser.mobile){
				$(".vcode-set").find(".vcode-input").parents().show();
			}
		}
	});
	
	checkAuth(function(loginUser,openid){
		//右上角菜单按钮点击
		initTitlebarBttns($(".titlebar"),{
			iconfont : 'menu',
			menu_items : getMenus()
		});
		
		var mobile = loginUser && loginUser.mobile;
		if(mobile){
			//登录状态，则不需要验证码
			VCodeSet.trustMobile($vcodeSet,mobile);
		}else{
			$vcodeSet.find(".vcode-input").parents("tr").show();
			$vcodeSet.find(".footbar-bttn-ok").addClass("disable-bttn");
		}
		
		//查看我的优惠券按钮
		widget.bindTouchClick($(".coupon-query-tip"),function(){
			ajax.request({
				url : _base_url+"/web/coupon/queryList.do",
				need_progressbar : false,
				success : function(header,body){
					initCouponList(body.couponList);
				}
			});
		});
		
		//领取优惠券按钮
		widget.bindTouchClick($(".coupon-bttn"),function(){
			demandCoupon($vcodeSet,orderId);
		});
		loading.hide();
		$(".index-page").show();
	});
	
	widget.bindTouchClick($(".coupon-goto-index"),function(){
		if(orderId){
			//有值说明是付款界面过来，返回付款界面
			common.gotoPage("../order/pay.html?order_id="+orderId);
		}else{
			common.gotoPage("../product/product-list.html");
		}
	});
}

function demandCoupon($vcodeSet,orderId){
	var $bttn = $(this);
	if($bttn.hasClass("disable-bttn")){
		return;
	}
	var msg = VCodeSet.checkValue($vcodeSet);
	if(msg){
		message.errorHide(msg,$titlebar);
		return;
	}
	var $titlebar = $(".titlebar");
	$bttn.addClass("disable-bttn");
	var loginUser = common.getLoginUser() || {};
	ajax.request({
		url : _base_url+"/web/coupon/demand.do",
		params : {
			mobile : $vcodeSet.find(".vcode-mobile").val(),
			vcode : $vcodeSet.find(".vcode-input").val(),
			redeem_code : $(".coupon-redeem-code").val()
		},
		need_progressbar : $titlebar,
		success : function(header,body){
			initNewCouponList(body.couponList,orderId);
			if(!loginUser.mobile){
				loginUser.mobile = $vcodeSet.find(".vcode-mobile").val();
				
				VCodeSet.trustMobile($vcodeSet,loginUser.mobile);
				
				common.setAuth(false,false,loginUser);
			}
		},
		complete : function(){
			$bttn.removeClass("disable-bttn");
		}
	});
}

/**
 * 初始化新领取的优惠券列表
 * @param couponList
 */
function initNewCouponList(couponList,orderId){
	var $listDiv = $(".coupon-newlist .list");
	if(couponList == null || couponList.length == 0){
		$(".coupon-newlist .coupon-tip").text("哎~还没有最新的优惠活动~");
		if(orderId){
			//有值说明是付款界面过来，返回付款
			$(".coupon-newlist .coupon-goto-index .text").text("返回继续付款~");
		}else{
			$(".coupon-newlist .coupon-goto-index .text").text("先去逛逛呗~");
		}
		$(".coupon-newlist").show();
		$(".coupon-use-tip").hide();
		$listDiv.hide();
		return;
	}
	$(".coupon-newlist .coupon-tip").text("手气不错哟~获得新优惠券：");
	if(orderId){
		//有值说明是付款界面过来，返回付款
		$(".coupon-newlist .coupon-goto-index .text").text("正好派上用场，赶紧完成付款~");
	}else{
		$(".coupon-newlist .coupon-goto-index .text").text("不用浪费了，赶紧去买点啥吧~");
	}
	$(".coupon-use-tip").show();
	var $tempDiv = $("#coupon_item_temp");
	var $newDiv = null;
	var itemEntity = null;
	$listDiv.empty();
	for(var i=0;i<couponList.length;i++){
		itemEntity = couponList[i];
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","coupon_"+itemEntity.id);
		
		$newDiv.find(".coupon-name").text(buildCouponText(itemEntity)).addClass("xsg-font");
		$newDiv.find(".coupon-expire").text("有效期至:"+itemEntity.expireTime);
		
		$newDiv.show();
		//if(i < couponList.length-1){
		$("<div class='separator_h'/>").appendTo($listDiv);
		//}
	}
	$listDiv.show();
	$(".coupon-newlist").show();
}


function initCouponList(couponList){
	var $listDiv = $(".coupon-list .list");
	
	if(couponList == null || couponList.length == 0){
		$(".coupon-list .coupon-tip").text("您当前没有可用的优惠券了~");
		$(".coupon-list").show();
		$listDiv.hide();
		return;
	}
	
	var $tempDiv = $("#coupon_item_temp");
	var $newDiv = null;
	var itemEntity = null;
	$listDiv.empty();
	for(var i=0;i<couponList.length;i++){
		itemEntity = couponList[i];
		$newDiv = $tempDiv.clone().appendTo($listDiv);
		$newDiv.attr("id","coupon_"+itemEntity.id);
		var $couponName = $newDiv.find(".coupon-name");
		$couponName.text(buildCouponText(itemEntity));
		$newDiv.find(".coupon-expire").text("有效期至:"+itemEntity.expireTime);
		$newDiv.show();
		if(itemEntity.isExpired == 1){
			//过期
			$couponName.addClass("gray-font");
			$("<span class='expired-text'>(已过期)</span>").appendTo($couponName);
		}
		
		if(i < couponList.length-1){
			$("<div class='separator_h'/>").appendTo($listDiv);
		}
	}
	$(".coupon-list").show();
}

