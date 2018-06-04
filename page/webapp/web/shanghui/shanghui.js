var _base_host = common.getHost();
var _base_url = common.getHost()+"/xiesange";
var membernames = {
	"wuyj":{
		"name" : "吴宇杰",
		"short" : "会",
		"color" : "#FF0000",
		"nickname":"吴骑龙",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/rxvr4tNUDQB7htE6Rec3oIVDXSVVLiaURhbicxvsnN9Eh1CPlzQJQRaKgzTuRZymc9yhhCt5MIPxumhINgwJF3G3cBmplucLBd/0"
	},
	"huangsp":{
		"name" :"黄世平",
		"short" : "世",
		"nickname":"FREEDOM",
		"color": "#1b6588",
		"data" : [],
		status:0,
		"headimg": "http://wx.qlogo.cn/mmopen/Q3auHgzwzM7IR7ujczml2xMM2EuX2bsosHmmV4nbicSIC9lZiaLziaNqoU2K3P2QnMiaD55WaX8LrzFk5icl8qhia8xHnSZ4l4d8akGSOCEzneicV4/0"
	},
	"shengjun":{
		"name" :"盛军",
		"short" : "盛",
		"nickname":"ClydeSheng",
		"color": "#dc39bb",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/rxvr4tNUDQCAQ1iaq6FX0xB2oGWckEibqWTA13yIbrBpKzuuChQibpZrbKRd1rfVvoh6RGCpzibZeiaZceRSZWkUO9g/0"
	},
	"xuedl":{
		"name" :"薛迪龙",
		"nickname":"薛迪龙",
		"short" : "龙",
		"color": "#13e28f",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/Q3auHgzwzM6BpW4FZiceEvdK6GxJaw8SZ8E4sibCnC6T67U3TSibJlTlzYTGOtO6vkSp8wXzOFV4FOXk5Egsjia2rQ/0"
	},
	"wuxs":{
		"name" : "吴心圣",
		"short" : "圣",
		"nickname":"大圣",
		"color" : "#ffd804",
		"data" : [],
		status:0,
		"headimg": "http://wx.qlogo.cn/mmopen/VFjZrEwLaRwQuETTp99sTpGyJNeaypERrTgAjG1NiajPvt9iaN0aGug25366u2SJibDWolAbIFIiaz80mVDa9Qh48jUbicw6B85sx/0"
	},
	"zy":{
		"name" :"张宇",
		"short" : "张",
		"nickname":"宇风",
		"color" : "#05fa62",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/ajNVdqHZLLDiassbfWgZ7diavLGVJmbteVz8vPDm89E6k2VHzVUsvReff2xnW5zrONTTmIBo1vsguFibUf8ZGzfww/0"
	},
	"zengxr":{
		"name" :"曾宪锐",
		"short" : "曾",
		"color": "#656A34",
		"nickname":"然然爸爸",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/PiajxSqBRaEL1bwBlUkQqrlljRSeKhWrWKulvDTuxeHPKc9cmC9seQVpazsMPibPGLbn9c9SFo144dCIZS07jBhg/0"
	},
	"lixf":{
		"name" :"李幸峰",
		"short" : "峰",
		"color": "#802A2A",
		"nickname":"长大的肚腩",
		"data" : [],
		"headimg": "http://wx.qlogo.cn/mmopen/k0Ue4mIpaVicNfX28icqsxf8michjUn3hmyZgwzIkz4vfEibVYJ7XSGwkWh3Tzc4SgD3a1dZeicHpWloJQIwRubg1GMrJdoRBhHov/0"
	},
	"wangyuan":{
		"name" : "外援",
		"short" : "援",
		"nickname":"外援",
		"color" : "#0000FF",
		"data" : [],
		"status":1,
		"sum_stat":0,
		"realtime_stat":0,
		"headimg": "http://n1.itc.cn/img8/wb/recom/2016/07/01/146736663085553124.JPEG"
	},
	"al":{
		"name" : "安亮",
		"short" : "安",
		"nickname":"安亮",
		"color" : "#0000FF",
		"data" : [],
		"realtime":0,
		"status":0,
		"headimg": "http://wx.qlogo.cn/mmopen/k0Ue4mIpaVicPmObsYpmLvG1k0q87CibOb35MgmJficibySzvN0L1Eo1muqKJGRTtVmX8xkVEjpgqkwaGD1HHlMrvQ/0"
	},
	"shui":{
		"name" :"台费",
		"color": "#888888",
		"data" : []
	},
	"huifei" : {
		"name" :"会费",
		"color": "#aed789",
		"data" : []
	}/*,
	"zhouyl":{
		"name" : "周叶林",
		"short" : "周",
		"color" : "#000000",
		"data" : [],
		"status":0
	},*/
	/*"lixp":{
		"name" : "李晓平",
		"short" : "平",
		"color": "#05fa62",
		"data" : [],
		"status":0
	},*/
	/*"fan":{
		"name" :"小范",
		"short" : "范",
		"color": "#7f1995",
		"data" : [],
		"status":0
	},*/
}

function getMemberByName(name){
	for(var key in membernames){
		if(membernames[key].name == name){
			return key;
		}
	}
	return "unknown";
}
function getUrlParams(paramName){
	var url = window.location.href;
	url = url.split("?");
	if(url.length == 1)
		return paramName ? null : {};
	var strParamArr = url[1].split("&");
	var result = {};
	for(var i = 0; i < strParamArr.length; i++ ) {
		var strParam = strParamArr[i];
		var args = strParam.split("=");

		if(paramName && paramName == args[0]){
			return args[1];
		}
		result[args[0]] = args[1];
	}
	return paramName ? "" : result;
}

function isMatchPeriod(date,periods){
	if(!periods){
		return true;
	}
	var ps = periods.split(",");
	for(var i=0;i<ps.length;i++){
		if(date.indexOf(ps[i]) == 0){
			return true;
		}
	}
}

function showIndexIcon(){
	var $div = $("<div class='cust_service'>").appendTo($("body"));
	$div.css({
		bottom:"40px",
		border:"1px solid #FD9603"
	});
	var $icondiv = $("<div class='icon xsg-fontset'>").appendTo($div);
	$icondiv.html("首");
	$icondiv.css({
		"background-color" : "#FD9603"
	});
	widget.bindTouchClick($div,function(){
		common.gotoPage("index.html");
	});
}
function isSuper(){
	return common.getUrlParams("is_super") == 1;
}

var pwd_chars = "脚踏改革";
var super_chars = "踏实创新";
var str = "天道酬勤人定胜天脚踏实地改革创新";