function initPage(){
	var redirectUrl = common.getUrlParams("redirect");
	if(!redirectUrl){
		message.alert("不合法的访问");
		return;
	}
	var openid = common.getUrlParams("openid");
	var $titlebar = $(".titlebar");
	
	var $vcodeNext = $(".vcode-next");
	var $vcodeSet = VCodeSet.init($("#vcode-container"),{
		success : function($mobileInput,$vcodeInput,$vcodeBttn){
			message.successHide("验证码已发送至"+$mobileInput.val(),$titlebar);
		},
		error : function(errorMsg,$mobileInput,$vcodeInput,$vcodeBttn){
			message.errorHide(errorMsg,$titlebar);
		},
		vcode_changed : function($mobileInput,$vcodeInput,$vcodeBttn){
			if($vcodeInput.val().length != 0){
				$vcodeNext.removeClass("disable-bttn");
			}else{
				$vcodeNext.addClass("disable-bttn");
			}
		}
	});
	
	//下一步事件
	widget.bindTouchClick($vcodeNext,function(e){
		if($vcodeNext.hasClass("disable-bttn"))
			return;
		var $mobileInput = $vcodeSet.find(".vcode-mobile");
		var $vcodeInput = $vcodeSet.find(".vcode-input");
		if(!common.checkMobile($mobileInput.val())){
			message.errorHide("请输入正确的手机号",$titlebar,10000);
			return;
		}
		
		if(!$vcodeInput.val()){
			message.errorHide("请输入验证码",$titlebar);
			return;
		}
		$vcodeNext.addClass('disable-bttn');
		ajax.request({
			url : _base_url+"/web/auth/loginByMobile.do",
			need_progressbar : $titlebar,
			params : {
				mobile : $mobileInput.val(),
				vcode : $vcodeInput.val(),
				openid : openid
			},
			success : function(header,body){
				var loginUser = body.user;
				if(!loginUser){
					message.errorHide("用户不存在",$titlebar);
					return;
				}
				common.setAuth(body.token,body.skey,loginUser);
				common.gotoPage(decodeURIComponent(redirectUrl));
			},
			complete:function(){
				$vcodeNext.removeClass('disable-bttn');
			}
		});
	});
	
	//逛逛事件
	
	widget.bindTouchClick($('.goto-index'),function(e){
		common.gotoPage('../product/product-list.html');
	});
}



