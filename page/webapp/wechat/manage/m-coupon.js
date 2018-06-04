var userId = null;
var loading = new PageLoading();
$(function(){
	loading.show();
	initPage();
});
function initPage(){
	var $page = $(".index-page")
	userId = common.getUrlParams("user_id");//从用户列表中过来，附带用户id，只查询该用户的订单
	if(!userId){
		message.alert("请以正确的姿势打开");
		return;
	}
	
	initTitlebarBttns($page.find('.titlebar'),[{
		icon: image_url+"/xsg/allocate.png",
		handler:function(){
			showAllocatePage();
		}
	},{
		class_name : 'menu',
		menu_items : getMenus()
	}]);
	
	loadCoupons();
}

function loadCoupons(startIndex){
	startIndex = startIndex || 0;
	ajax.request({
		url : _base_url+"/web/manage/queryUserCouponList.do",
		need_progressbar : false,
		params:{
			user_id : userId,
			need_oauth:true
		},
		success : function(header,body){
			var couponList = body.couponList;
			initCouponList(couponList);
		},
		complete : function(){
			loading.hide();
			$(".index-page").show();
		}
	});
}

function initCouponList(couponList){
	if(couponList == null || couponList.length == 0)
		return;
	var $listDiv = $(".coupon-list");
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
		$newDiv.find(".coupon-event").text(itemEntity.eventName);
		$newDiv.find(".coupon-expiretime").text("有效期至:"+itemEntity.expireTime);
		$newDiv.find(".coupon-createtime").text("领取日期:"+itemEntity.createTime);
		if(itemEntity.consumeTime){
			$newDiv.find(".coupon-consumetime").text("消费日期:"+(itemEntity.consumeTime || "无"));
		}else{
			$newDiv.find(".coupon-consumetime").hide();
		}
		
		
		if(itemEntity.isExpired == 1){
			//过期
			$couponName.addClass("gray-font");
			$("<span class='expired-text'>(已过期)</span>").appendTo($couponName);
		}else if(itemEntity.consumeTime){
			$couponName.addClass("gray-font");
			$("<span class='expired-text'>(已消费)</span>").appendTo($couponName);
		}else{
			$couponName.addClass("xsg-font");
		}
		
		$newDiv.show();
		
	}
}
function showAllocatePage(){
	var $page = $(".allocate-page");
	var $items = $page.find(".coupon-type-item");
	if($page.attr("is_init") != 1){
		widget.bindTouchClick($items,function(e){
			var $this = $(e.target);
			$items.removeClass("common-bg");
			$this.addClass("common-bg");
		});
		
		widget.bindTouchClick($page.find(".footbar-bttn-ok"),function(e){
			commitAllocation();
		});
		widget.bindTouchClick($page.find(".footbar-bttn-cancel"),function(e){
			$page.hide();
		});
		
		
		initTitlebarBttns($page.find('.titlebar'),[{
			iconfont : 'close',
			handler:function(){
				$page.hide();
			}
		}]);
		$page.attr("is_init",1);
	}
	
	widget.slide($page,"left right",400);
}

function commitAllocation(){
	var $page = $(".allocate-page");
	var type = $page.find(".coupon-type-item.common-bg").eq(0).attr("val");
	var value = $page.find(".item-input.amount").val();
	var expireDate = $page.find(".item-input.expiredate").val();
	var $titlebar = $page.find(".titlebar");
	if(!type){
		message.errorHide("请选择优惠券类型",$titlebar);
		return;
	}
	
	if(!value){
		message.errorHide("请输入优惠值",$titlebar);
		return;
	}else{
		if(type == 1){
			value = value * 10;//折扣券，这里要乘以10，后台是百分之多少来存储的
		}
	}
	
	if(!expireDate){
		message.errorHide("请输入有效期",$titlebar);
		return;
	}
	
	ajax.request({
		url : _base_url+"/web/manage/allocateCoupons.do",
		need_progressbar : $titlebar,
		params:{
			user_id:userId,
			coupon_type:type,
			coupon_value:value,
			expire_date:expireDate
		},
		success : function(header,body){
			message.successHide("操作成功，正在跳转...");
			common.gotoPage("../manage/m-coupon.html?user_id="+userId,1500);
		}
	});
	
}
/**/