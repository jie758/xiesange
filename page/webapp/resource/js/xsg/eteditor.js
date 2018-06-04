/**
 * options:
 *   canvas,jQuery对象，画布区对象
 *   height,高度
 *   lang,语言,默认中文
 *   html,初始的html串
 * 	 qiniuOpt:
 * 		qiniuInstance,
 * 		domain,
 * 		path
 * 
 * method :
 * 		option(key,val),设置参数
 * 		html(string),获取或者设置内容
 * 		empty(),清空内容
 * 		destroy(),销毁整个编辑器
 * 		setPicPath(),设置图片上传路径格式，应该属于qiniuOpt，因为比较特殊，所以单独提取出来
 */
var xsg_summernote = function(options){
	//common.importCss("http://netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.css");
	//common.importCss("../../resource/js/3rd/summernote/summernote.css");
	//common.importCss("../../resource/style/eteditor.css");
	
	//定义字体样式列表
	var fontNameList = ['宋体','黑体','微软雅黑','Serif', 'Sans', 'Arial', 'Arial Black', 'Courier', 
	                   'Courier New', 'Comic Sans MS', 'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans', 'Tahoma', 'Times',
	                   'Times New Roman', 'Verdana'];
	//定义字体大小，单位px
	//var fontSizeList = ["9:小号","14:普通","22:大号","30:超大"];
	var initQiniu = function($button,tokenSet){
		var bttnId = $button.attr("id");
		var qiniuOpt = options.qiniuOpt;

		var initOptions = {
			browse_button : bttnId,
			container : $button.parent().attr('id'),
			path : qiniuOpt.path || "*",
			tokenSet : tokenSet,
			domain : qiniuOpt.domain,
			events : {
				'FilesAdded': function(files) {
					xsg_progress.showProgressDialog(files,function(urls){
					});
				},
				'BeforeUpload': function(file) {
					xsg_progress.beginUpload(file.id);
				},
				'UploadProgress': function(file,percent,uploadedSize,speed) {
					xsg_progress.updateProgress(file.id,percent,uploadedSize,speed);
				},
				'FileUploaded': function(file,url) {
					xsg_progress.completeProgress(file.id,url);
					options.canvas.summernote('insertImage', url, function ($image) {
						$image.css('width', "100%");
						$image.attr('data-filename', file.id);
					});
					//document.execCommand("insertimage",false,url);
				},
				UploadComplete : function(){
					$button.parents('.modal-dialog').find(".modal-header").children("button").click();
				}
			}
		}
		xsg_qiniu.init(qiniuOpt.qiniuInstance,initOptions);

		return initOptions;
	};
	
	var qiniuInitOption = null;
	options.canvas.summernote({
		lang: options.lang || 'zh-CN',
		height: options.height || "100%",
		max_height : options.max_height,
		toolbar: [
          // [groupName, [list of button]]
          ['style', ['bold', 'italic', 'underline', 'strikethrough','height','style']],
          ['fontsize', ['fontsize','fontname','color']],
          ['para', ['paragraph','ul', 'ol']],
          ['insert', ['picture','link','table']],
          ['oper', ['undo','redo']],
          ['other', ['fullscreen','codeview','clear','help']]
        ],
        fontNames: fontNameList,
		fontSizes: ['10','12','14','16','18','20','22','24'],
        callbacks: {
            onImageUpload: function(files) {
            },
            onInit: function() {
            	var bttnId = common.unique('summernote_choosepic_');
            	var $input = $(".note-group-select-from-files input");//summernote内置的按钮
            	var $button = $("<div id='"+common.unique('summernote_choosepic_div_')+"'><button id='"+bttnId+"'>选择图片</button><div>");
            	$input.after($button);
            	$input.remove();//把自定义的按钮插入后，就要把内置的移除掉
            	
            	var qiniuOpt = options.qiniuOpt;
				if(qiniuOpt){
					var uptoken = qiniuOpt.uptoken;
					if(!uptoken){
						xsg_qiniu.getUpTokens(null,function(tokenSet){
							qiniuInitOption = initQiniu($button,tokenSet);
						}); 
					}else{
						qiniuInitOption = initQiniu($button,{commonToken:uptoken});
					}
				}
            }
        }
	});
	if(options.padding_h){
		$(".note-editor .note-editable").parent().css("background-color","#f5f5f5");
		$(".note-editor .note-editable").css("margin","0px "+options.padding_h+"px");
	}
	

	
	this.html = function(html){
		if(arguments.length == 0){
			return options.canvas.summernote('code');
		}else{
			options.canvas.summernote('code',html);
		}
		
	};
	this.empty = function(){
		options.canvas.summernote('code',null);
	}
	this.destroy = function(){
		options.canvas.summernote("destroy");
		options.canvas.empty();
	}
	this.option = function(key,val){
		if(key == "options.qiniuOpt.path"){
			qiniuInitOption.path = val;
		}else{
			options[key] = val;
		}
		
	}
};
