function initPage(){
	var $titlebar = $(".titlebar");
	//右上角菜单按钮点击
	initPopupMenu($(".titlebar-bttn"),"right left");
	//初始化验证码组件
	var $vcodeNext = $(".vcode-next");
	var $vcodeSet = initVCodeSet($("#vcode-container"),{
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
		if($(this).hasClass("disable-bttn"))
			return;
		var $mobileInput = $vcodeSet.find(".vcode-mobile");
		var $vcodeInput = $vcodeSet.find(".vcode-input");
		if(!common.checkMobile($mobileInput.val())){
			message.errorHide("请输入正确的手机号",$titlebar);
			return;
		}
		
		if(!$vcodeInput.val()){
			message.errorHide("请输入验证码",$titlebar);
			return;
		}
		$bttn.addClass('disable-bttn');
		ajax.request({
			url : _base_url+"/web/user/loginByMobile.do",
			need_progressbar : $titlebar,
			params : {
				mobile : $mobileInput.val(),
				vcode : $vcodeInput.val()
			},
			success : function(header,body){
				$bttn.removeClass('disable-bttn');
				var loginUser = body.user;
				if(loginUser){
					message.successHide("成功！",$titlebar);
					common.setAuth(body.token,body.skey);
					//$vcodeSet.hide();
					//showIndex();
				}else{
					message.errorHide("用户不存在",$titlebar);
				}
			}
		});
	});
}



