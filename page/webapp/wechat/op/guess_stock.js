$(function(){
	initPage();
});

function initPage(){
	var $commit = $(".commit");
	widget.bindTouchClick($commit,function(){
		commit();
	});
	
	
	ajax.request({
		url : _base_url+"/web/op_guess_stock/querySignupList.do",
		need_progressbar : false,
		error : function(header,body){
			message.errorHide(header.error_message);
			return false;
		},
		success : function(header,body){
			var list = body.signupList;
			if(!list)return;
			var $list = $('.signup-list');
			for(var i=0;i<list.length;i++){
				var signup = list[i];
				var $item = div($list,"signup-item overflow-hidden");
				$("<span>").appendTo($item).addClass("mobile").text(signup.mobile);
				$("<span>").appendTo($item).addClass("name").text(signup.nickname);
				$("<span>").appendTo($item).addClass("num xsg-font").text(signup.ext1);
				$("<span>").appendTo($item).addClass("time gray-font").text(signup.createTime);
			}
		}
	});
}


function commit(){
	var $commit = $(".commit");
	if(!enableBttn($commit)){
		return;
	}
	enableBttn($commit,false);
	var num = $(".num").val();
	var mobile = $(".mobile").val();
	var name = $(".name").val();
	
	ajax.request({
		url : _base_url+"/web/op_guess_stock/signup.do",
		need_progressbar : false,
		params : {
			name:name,
			ext1:num,
			mobile:mobile,
			need_oauth:true
		},
		error : function(header,body){
			message.errorHide(header.error_message);
			return false;
		},
		success : function(header,body){
			message.successHide("提交成功!");
		},
		complete : function(){
			enableBttn($commit,true);
		}
	});
}