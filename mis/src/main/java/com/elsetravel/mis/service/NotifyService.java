package com.elsetravel.mis.service;

import java.util.ArrayList;
import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.ETEditorCmp;
import com.elsetravel.baseweb.config.bean.NotifyConfigBean;
import com.elsetravel.baseweb.notify.NotifyCmp;
import com.elsetravel.baseweb.notify.NotifyDefine;
import com.elsetravel.baseweb.notify.NotifyDefine.CodeDefine;
import com.elsetravel.baseweb.notify.NotifyTargetHolder;
import com.elsetravel.baseweb.pojo.CodeNamePojo;
import com.elsetravel.baseweb.pojo.EmailHtmlSkin;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.notify.mail.MailUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.notify.NotifyTemplate;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.request.MisAccessToken;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="notify",version="")
public class NotifyService extends AbstractService {
	/**
	 * 查询某个模板的内容
	 * @param context
	 * 			code,模板编码
	 * 			channel
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午9:57:56
	 */
	public ResponseBody queryTemplateContent(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Common.code,
				ParamDefine.Notify.channel);
		String tempCode = context.getRequestBody().getString(ParamDefine.Common.code);
		short channel = context.getRequestBody().getShort(ParamDefine.Notify.channel);
		
		NotifyTemplate tempEntity = dao().querySingle(NotifyTemplate.class,
				new DBCondition(NotifyTemplate.JField.code,tempCode),
				new DBCondition(NotifyTemplate.JField.channel,channel)
		);
		if(tempEntity == null){
			return null;
			//tempEntity = new NotifyTemplate();
			//tempEntity.setId(dao().getSequence(NotifyTemplate.class));//因为有涉及到图文编辑，所以预先生成一个id，用于图片存储路径方便
		}
		return new ResponseBody("template",tempEntity);
	}
	
	/**
	 * 保存通知模板数据，只能修改名称和内容
	 * @param context
	 * 			code,
	 * 			channel,
	 * 			name,
	 * 			content
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午9:51:06
	 */
	/*public ResponseBody saveTemplate(MisRequestContext context) throws Exception{
		Long tempId = context.getRequestBody().getLong(ParamDefine.Notify.template_id);
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		Short channel = context.getRequestBody().getShort(ParamDefine.Notify.channel);
		
		boolean isNew = tempId == null;
		NotifyTemplate entity = null;
		if(isNew){
			RequestUtil.checkEmptyParams(context.getRequestBody(), 
					ParamDefine.Common.code,
					ParamDefine.Common.name,
					ParamDefine.Notify.channel
			);
			//判断相同编码和渠道是否已经存在
			NotifyTemplate temp = dao().querySingle(NotifyTemplate.class, 
					new DBCondition(NotifyTemplate.JField.code,code),
					new DBCondition(NotifyTemplate.JField.channel,channel)
			);
			if(temp != null){
				throw ETUtil.buildInvalidOperException("相同渠道下通知模板编码重复!channel="+channel+",code="+code);
			}
			entity = new NotifyTemplate();
			
		}else{
			RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Notify.template_id
			);
			entity = dao().queryById(NotifyTemplate.class, tempId);
			if(entity == null){
				throw ETUtil.buildInvalidOperException("模板不存在");
			}
		}
		
		if(code != null){
			//判断编码是否合法，模板编码都是固定值，不能随意编辑
			if(!NotifyCmp.isValidTemplateCode(code)){
				throw ETUtil.buildInvalidOperException("不合法的编码值");
			}
			entity.setCode(code);
		}
		if(name != null)
			entity.setName(name);
		if(channel != null)
			entity.setChannel(channel);
		
		if(isNew){
			dao().insert(entity);
		}else if(DBHelper.isModified(entity)){
			dao().updateById(entity, tempId);
		}
		return new ResponseBody("template_id",entity.getId());
	}*/
	/**
	 * 修改某条通知模板的具体内容
	 * @param context
	 * 			code,模板编码，用来定位要修改的记录
	 * 			channel,模板渠道，用来定位要修改的记录
	 * 			name,通知标题，如果传了表示修改
	 * 			content，通知内容，如果传了表示修改
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:00:37
	 */
	public ResponseBody saveTemplateContent(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Common.code,
				ParamDefine.Notify.channel);
		String tempCode = context.getRequestBody().getString(ParamDefine.Common.code);
		short channel = context.getRequestBody().getShort(ParamDefine.Notify.channel);
		String tempContent = context.getRequestBody().getString(ParamDefine.Notify.content);
		String tempName = context.getRequestBody().getString(ParamDefine.Common.name);
		
		NotifyTemplate tempEntity = dao().querySingle(NotifyTemplate.class,
				new DBCondition(NotifyTemplate.JField.code,tempCode),
				new DBCondition(NotifyTemplate.JField.channel,channel)
		);
		
		if(tempEntity == null){
			RequestUtil.checkEmptyParams(context.getRequestBody(), 
					ParamDefine.Notify.content,
					ParamDefine.Common.name);
			
			
			tempEntity = new NotifyTemplate();
			tempEntity.setChannel(channel);
			tempEntity.setCode(tempCode);
			
		}
		
		
		if(tempName != null){
			tempEntity.setName(tempName);
		}
		if(tempContent != null){
			tempEntity.setContent(tempContent);
		}
		
		if(tempEntity.getId() == null){
			dao().insert(tempEntity);
		}else if(DBHelper.isModified(tempEntity)){
			dao().updateById(tempEntity, tempEntity.getId());
		}
		/*
		Long tempId = context.getRequestBody().getLong(ParamDefine.Notify.template_id);
		
		
		NotifyTemplate entity = new NotifyTemplate();
		entity.setContent(tempContent);
		dao().updateById(entity, tempId);*/
		
		return null;
	}
	
	/**
	 * 上传模板内容中的图片。一次性只能上传一张
	 * @param context
	 * 			code,必传，模板编码，用于定位图片目录
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:35:56
	 */
	public ResponseBody uploadTemplatePic(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Common.code,
				ParamDefine.Notify.channel
		);
		String tempCode = context.getRequestBody().getString(ParamDefine.Common.code);
		Short channel = context.getRequestBody().getShort(ParamDefine.Notify.channel);
		String savePath = CommonUtil.join("/image/notify/",tempCode,"_",channel);
		
		UploadRequestBody requestBody = (UploadRequestBody)context.getRequestBody();
		
		String[] savePaths = ETEditorCmp.uploadPics(requestBody.getUploadFiles(), savePath);
		
		if(NullUtil.isEmpty(savePaths))
			return null;
		String picPath = savePaths[0];
		
		return new ResponseBody("picUrl", ETUtil.buildPicUrl(picPath));
	}
	
	
	/**
	 * 删除某个通知模板。
	 * @param context
	 * 			template_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:50:51
	 *//*
	public ResponseBody removeTemplate(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Notify.template_id
		);
		long tempid = context.getRequestBody().getLong(ParamDefine.Notify.template_id);
		
		NotifyTemplate temp = dao().queryById(NotifyTemplate.class, tempid);
		if(temp == null)
			return null;
		
		//删除数据库记录
		dao().deleteById(NotifyTemplate.class, tempid);
		
		//删除图片
		FileUtil.delFolder(ETUtil.buildPicPath(CommonUtil.join("/image/notify/",tempid)));
		
		return null;
	}*/
	
	/**
	 * 群发邮件消息，以Elsetravel的官方邮箱
	 * @param context
	 * 			target_user_ids,指定要发送的用户id串，以逗号分隔,如果要发送全部有效用户，请传"all"
	 * 			target_emails,指定用户之外的其它email串，以逗号分隔
	 * 			title,
	 * 			content，发送的内容,支持占位符
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午7:13:12
	 */
	public ResponseBody sendEmailMessage(final MisRequestContext context) throws Exception{
		
		/*title = "你好！";
		content = "你好:${nickname}";*/
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Notify.title,
				ParamDefine.Notify.content);
		
		final EmailHtmlSkin skin = EmailHtmlSkin.getInstance();
		new Thread(new Runnable(){
			@Override
			public void run() {
				int index = 0;
				List<User> userList = null;
				String emails = context.getRequestBody().getString(ParamDefine.Notify.target_emails);
				
				String title = context.getRequestBody().getString(ParamDefine.Notify.title);
				String content = context.getRequestBody().getString(ParamDefine.Notify.content);
				String targetUserIds = context.getRequestBody().getString(ParamDefine.Notify.target_user_ids);
				try{
					if(!targetUserIds.equals("all")){
						String[] userIds = targetUserIds.split(",");
						userList = dao().query(User.class, new DBCondition(User.JField.email,null,DBOperator.IS_NOT_NULL),
										new DBCondition(User.JField.email,"",DBOperator.NOT_EQUALS),
										new DBCondition(User.JField.id,userIds,DBOperator.IN));
						
						ParamHolder params = new ParamHolder();
						for(User user : userList){
							params.clear();
							params.addParam("nickname", user.getNickname());
							
							
							logger.debug("send mail : "+user.getEmail()+",content:"+skin.buildHtml(ETUtil.parseTextExpression(content, params)));
							
							MailUtil.sendHTML(user.getEmail(), 
									ETUtil.parseTextExpression(title, params), 
									skin.buildHtml(ETUtil.parseTextExpression(content, params)));
							//NotifyCmp.
						}
					
					}else{
						StringBuilder sb = new StringBuilder();
						while(true){
							userList = dao().query(new QueryStatement(User.class,
									new DBCondition(User.JField.email,null,DBOperator.IS_NOT_NULL),
									new DBCondition(User.JField.email,"",DBOperator.NOT_EQUALS)/*,
									new DBCondition(User.JField.id,10020108L10004)*/
								).appendRange(index, 20)
							);
						
							if(NullUtil.isNotEmpty(userList)){
								boolean begin = true;
								ParamHolder params = new ParamHolder();
								
								for(User user : userList){
									if(user.getEmail().equals("asouthi@163.com")){
										begin = true;
									}
									if(!begin){
										continue;
									}
									//Thread.sleep(2000L);
									params.clear();
									params.addParam("nickname", user.getNickname());
									
									//logger.debug("send mail : "+user.getEmail()+",content:"+skin.buildHtml(ETUtil.parseTextExpression(content, params)));
									sb.append(",").append(user.getEmail());
									try{
										/*MailUtil.sendHTML(user.getEmail(), 
												ETUtil.parseTextExpression(title, params), 
												skin.buildHtml(ETUtil.parseTextExpression(content, params)));*/
									}catch(Exception e){
										logger.error(e,e);
									}
									/*NotifyCmp.sendEmail(user.getEmail(), 
											ETUtil.parseTextExpression(title, params),
											skin.buildHtml(ETUtil.parseTextExpression(content, params)));*/
								}
							}
							
							if(NullUtil.isEmpty(userList) || userList.size() < 20){
								logger.debug(sb.substring(1));
								break;//说明没有数据了
							}
							index = index+userList.size();
						}
					}
				}catch(Exception e){
					logger.error(e,e);
				}
				
				
			}
			
		}).start();
		
		
		
		
		return null;
		
	}
}
