$(document).ready(init);
var TrackEngine = function(pathObj){
	var trackIndex = 0;
	var tracks = [];
	this.draw = function(options){
		for(var i=0;i<tracks.length;i++){
			var track = tracks[i];
			if(track._type == 'moveTo'){
				pathObj.moveTo(track.x,track.y,track.options);
			}else if(track._type == 'lineTo'){
				pathObj.lineTo(track.x,track.y,track.options);
			}else if(track._type == 'arc'){
				pathObj.arc(track.x,track.y,track.radius,track.startAngle,track.endAngle,track.counterclockwise,track.options);
			}else if(track._type == 'rotate'){
				pathObj.rotate(track.radians,track.options);
			}
			var name = track.options && track.options.name;
			if(!name)
				continue;
			var $node = $("#"+name);
			$node && $node.length > 0 && $node.css({
				left:track.x,
				top:track.y
			});
		};
		$(".wrapper").scrollPath({drawPath: true, wrapAround: true,scrollBar: false});
		$("canvas").show();
		
		for(var i=0;i<tracks.length;i++){
			var track = tracks[i];
			var $target = track.options && $("#"+track.options.name);
			track._initFunc && track._initFunc.apply(null,[$target]);
		}
		
		return this;
	};
	this.moveTo = function(x, y,options,initFunc){
		tracks.push({
			_type : 'moveTo',
			_initFunc : initFunc,
			x : x,
			y:y,
			options : options
		});
		return this;
	};
	this.lineTo = function(x, y,options,initFunc){
		tracks.push({
			_type : 'lineTo',
			_initFunc : initFunc,
			x : x,
			y:y,
			options : options
		});
		return this;
	};
	this.arc = function(x, y, radius, startAngle, endAngle, counterclockwise,options){
		tracks.push({
			_type : 'arc',
			x : x,
			y : y,
			radius : radius,
			startAngle : startAngle,
			endAngle : endAngle,
			counterclockwise : counterclockwise,
			options : options
		});
		return this;
	};
	this.rotate = function(radians,options){
		tracks.push({
			_type : 'rotate',
			radians : radians,
			options : options
		});
		return this;
	};
	
	this.scrollNext = function(duration,easing,complete){
		var nextName;
		do{
			if(trackIndex == tracks.length -1){
				trackIndex = 0;
			}else{
				++trackIndex;
			}
			
			nextName = tracks[trackIndex] 
							&& tracks[trackIndex].options 
							&& tracks[trackIndex].options.name
		}while(!nextName && tracks[trackIndex] );
		console.log(nextName);
		var engine = this;
		$.fn.scrollPath("scrollTo", nextName, duration, easing,function(){
			complete && complete.apply(null);
			/*if(nextName == "rotations-rotated"){
				engine.scrollNext(duration,easing,complete);
			}*/
		});
		
		
	};
	this.scrollPrev = function(duration,easing,complete){
		var prevName;
		do{
			if(trackIndex == 0){
				trackIndex = tracks.length - 1;
			}else{
				--trackIndex;
			}
			prevName = tracks[trackIndex] 
							&& tracks[trackIndex].options 
							&& tracks[trackIndex].options.name
		}while(!prevName && tracks[trackIndex] );
		
		$.fn.scrollPath("scrollTo", prevName, duration, easing,function(){
			complete && complete.apply(null);
		});
	};
}

function init() {
	var pathObj = $.fn.scrollPath("getPath");
	var trackEngine = new TrackEngine($.fn.scrollPath("getPath"))
		.moveTo(0, 0, {name: "d0"},function($target){
			$target.css("left",-1*$target.width()/2)
		})
		.lineTo(0, 800, {name: "d1"},function($target){
			$target.css("left",-1*$target.width()/2)
		})
		.arc(-600, 1200, 400, -Math.PI/2, Math.PI/2, true)
		.lineTo(200, 1600, {name: "d2"},function($target){
			$target.css("margin-left",-1*$target.width()/2)
		})
		.lineTo(1600, 1600, {name: "d3"})
		.arc(1800, 1000, 600, Math.PI/2, 0, true, {rotate: Math.PI/2 })
		.lineTo(2400, 750, {
				name: "d4",
				callback:function(){}
			},function($target){
				var $node = $("#d4");
				/*$node.css({
					left : 2400 - $node.width()/2
				});*/
			}
		)
		.rotate(3*Math.PI/2, {
			name: "rotations-rotated"
		})
		.lineTo(2400, -1100, {
			name: "d5"
		})
		.arc(2250, -1100, 150, 0, -Math.PI/2, true)
		.lineTo(1350, -1250, {
			name: "d6"
		})
		.arc(1300, 50, 1300, -Math.PI/2, -Math.PI, true, {rotate: Math.PI*2})
		//.arc(400, 0, 400, 0, -Math.PI, true, {name: "d4"})
		.draw({drawPath: true, wrapAround: true,scrollBar: true});
	
	//$("canvas").show();
	
	var isScrolling = false
	$("#container").mousewheel(function(event, delta, deltaX, deltaY) {
		console.log(delta);
		event.preventDefault();
		if(isScrolling)
			return;
		isScrolling = true;
		if(deltaY < 0){
			//往下滚
			trackEngine.scrollNext(1000, "easeInOutSine",function(){
				isScrolling = false;
			});
		}else{
			trackEngine.scrollPrev(1000, "easeInOutSine",function(){
				isScrolling = false;
			});
		};
	});

}