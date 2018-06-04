var demo = angular.module("demo", ["RongWebIMWidget"]);
demo.controller("main", ["$scope","$http","RongCustomerService", function($scope,$http,RongCustomerService) {
	//$scope.title="asdf";
	getToken(function(token){
		RongCustomerService.init({
	        appkey:"qf3d5gbj35lrh",
	        token:token,
	        customerServiceId:"KEFU147797014698318",
	        reminder:"在线咨询",
	        position:RongCustomerService.Position.right,
	        style:{
	        	width:320
	        },
	        onSuccess:function(e){
	        	console.log(e);
	        }
		});

		//RongCustomerService.setProductInfo({title:"这是啦啦啦",imageUrl:"https://www.baidu.com/img/bd_logo1.png"})
		
		$("#bttn").click(function(){
			RongCustomerService.show();
		});
		
		/*$scope.show = function() {
			RongCustomerService.show();
		}
		$scope.hidden = function() {
			RongCustomerService.hidden();
		}*/
	});
	
	
}]);

function showDialog(RongCustomerService){
	RongCustomerService.show();
}
function getToken(callback){
	ajax.request({
		url : _base_url+"/web/ry/getToken.do",
		need_progressbar : false,
		params : {
			user_id : 1,
			need_oauth:true
		},
		success : function(header,body){
			callback.apply(null,[body.token]);
			//initChat(body.token)
		}
	});
}

//initChat("123");

/*function initChat(token){
	demo.controller("main", ["$scope","$http","RongCustomerService", function($scope,$http,RongCustomerService) {
		$scope.title="asdf";
		RongCustomerService.init({
	        appkey:"3argexb6r934e",//selfe
	        token:"I8zRoTYOdtHug+ox4s7HapUnU/cREmEFuMhOJuGv5bP+dl6CkOlF+WuQPPbm30kCrX6ygPNSBvlJzwuiv72NPw==",//selfe kefu
	        customerServiceId:"KEFU145914839332836",//selfe
	        reminder:"在线咨询",
	        position:RongCustomerService.Position.right,
	        style:{
	          width:320
	        },
	        onSuccess:function(e){
	          console.log(e);
	        }
		})
	
		RongCustomerService.setProductInfo({title:"这是啦啦啦",imageUrl:"https://www.baidu.com/img/bd_logo1.png"})
		$scope.show = function() {
			RongCustomerService.show();
		}
		$scope.hidden = function() {
			RongCustomerService.hidden();
		}
	}]);
}*/
