package com.xiesange.baseweb.wechat;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.wechat.define.WechatDefine;
import com.xiesange.baseweb.wechat.pojo.Menu;
import com.xiesange.baseweb.wechat.pojo.button.Button;
import com.xiesange.baseweb.wechat.pojo.button.ClickButton;
import com.xiesange.baseweb.wechat.pojo.button.ViewButton;
import com.xiesange.core.util.NullUtil;
import com.xiesange.core.xml.BaseNode;
import com.xiesange.core.xml.UniversalXmlConverter;
import com.xiesange.core.xml.XStreamHolder;

/**
 * 微信工具类
 * @author Think
 *
 */
public class WechatUtil {
	private static Logger logger = Logger.getLogger(WechatCmp.class);
	public static void main(String[] args) throws Exception {
		Menu menu = WechatUtil.parseMenu("/wechat/wechat_menu.xml");
		WechatCmp.createMenu(menu);
	}
	
	public static Menu parseMenu(String menuPath) throws Exception{
		Menu menu = new Menu();
		XStreamHolder holder = new XStreamHolder("root",BaseNode.class);
        holder.registerConverter(new UniversalXmlConverter(BaseNode.class));
		BaseNode root = (BaseNode)holder.parseFromResource(menuPath);
		List<BaseNode> menuNodes = root.getChildren();
		
		List<Button> buttons = parseMenuNode(menuNodes);
		menu.setButton(buttons);
		return menu;
	}
	
	private static List<Button> parseMenuNode(List<BaseNode> menuNodes) throws Exception{
		List<Button> buttons = new ArrayList<Button>();
		for(BaseNode menuNode : menuNodes){
			String name = menuNode.getAttribute("name");
			String type = menuNode.getAttribute("type");
			Button button = null;
			if(NullUtil.isNotEmpty(menuNode.getChildren())){
				button = new Button(name);
				List<Button> subButtons = parseMenuNode(menuNode.getChildren());
				button.setSub_button(subButtons);
			}else if(type.equals("click")){
				button = new ClickButton(name,menuNode.getAttribute("key"));
			}else if(type.equals("view")){
				String url = menuNode.getAttribute("url");
				String oauthType = menuNode.getAttribute("oauth_type");
				if(NullUtil.isNotEmpty(oauthType)){
					url = WechatUtil.buildOAuthUrl(url, oauthType);
				}
				button = new ViewButton(name,url);
			}
			buttons.add(button);
		}
		return buttons;
	}
	
	//判断是否登录前的操作，如果是登录前，那么租户必须通过参数传进来，如果是登录后可以根据login_id去获取
	public static boolean isBeforeLogin(String actionName,String methodName){
		return methodName.equals("loginAuth");
	}
	
	
	public static String buildOAuthUrl(String url,short scope) throws Exception{
		String scopeStr = scope == 1 ? "snsapi_base" : "snsapi_userinfo";
		return buildOAuthUrl(url,scopeStr);
	}
	public static String buildOAuthUrl(String url,String scope) throws Exception{
		StringBuffer sb = new StringBuffer(128);
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?")
			.append("appid=").append(WechatDefine.WECHAT_APP_ID)
			.append("&redirect_uri=").append(url)
			.append("&response_type=code&scope=").append(scope)
			.append("&state=1#wechat_redirect");
		return sb.toString();
	}
}
