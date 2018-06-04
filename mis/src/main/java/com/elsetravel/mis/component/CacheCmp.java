package com.elsetravel.mis.component;

import org.apache.http.client.methods.HttpPost;

import com.elsetravel.baseweb.cache.CacheManager;
import com.elsetravel.core.ParamHolder;
import com.elsetravel.core.util.HttpUtil;
import com.elsetravel.gen.dbentity.base.BaseBanner;
import com.elsetravel.gen.dbentity.base.BaseConfig;
import com.elsetravel.gen.dbentity.base.BaseEnum;
import com.elsetravel.gen.dbentity.base.BaseMedal;
import com.elsetravel.gen.dbentity.base.BaseRewardRule;
import com.elsetravel.gen.dbentity.base.BaseTag;

/**
 * 缓存组件
 * @author Wilson 
 * @date 下午8:24:26
 */
public class CacheCmp {
	private static final String APPILICATION_URL = "http://www.elsetravel.com/elsetravel/web/common/refreshCache.do";
	private static HttpPost bannerPost = null;
	private static HttpPost configPost = null;
	private static HttpPost enumPost = null;
	private static HttpPost medalPost = null;
	private static HttpPost rewardRulePost = null;
	private static HttpPost ticketCatalogPost = null;
	private static HttpPost tagPost = null;
	
	public static void refreshBanner() throws Exception{
		CacheManager.refreshCache(BaseBanner.class);
		if(bannerPost == null){
			bannerPost = createHttPost("banner");
		}
		HttpUtil.execute(bannerPost);
	}
	public static void refreshMedal() throws Exception{
		CacheManager.refreshCache(BaseMedal.class);
		if(medalPost == null){
			medalPost = createHttPost("medal");
		}
		HttpUtil.execute(medalPost);
	}
	public static void refreshConfig() throws Exception{
		CacheManager.refreshCache(BaseConfig.class);
		if(configPost == null){
			configPost = createHttPost("config");
		}
		HttpUtil.execute(configPost);
	}
	public static void refreshEnum(boolean needRefreshRemote) throws Exception{
		CacheManager.refreshCache(BaseEnum.class);
		
		if(!needRefreshRemote)
			return;
		if(enumPost == null){
			enumPost = createHttPost("enum");
		}
		HttpUtil.execute(enumPost);
	}
	public static void refreshTag() throws Exception{
		CacheManager.refreshCache(BaseTag.class);
		if(tagPost == null){
			tagPost = createHttPost("tag");
		}
		HttpUtil.execute(tagPost);
	}
	public static void refreshRewardRule() throws Exception{
		CacheManager.refreshCache(BaseRewardRule.class);
		if(rewardRulePost == null){
			rewardRulePost = createHttPost("rewardrule");
		}
		HttpUtil.execute(rewardRulePost);
	}
	/*public static void refreshTicketCatalog() throws Exception{
		CacheManager.refreshCache(BaseTicketCatalog.class);
		if(ticketCatalogPost == null){
			ticketCatalogPost = createHttPost("ticketcatalog");
		}
		HttpUtil.execute(ticketCatalogPost);
	}*/
	
	private static HttpPost createHttPost(String param) throws Exception{
		return HttpUtil.createHttpPost(APPILICATION_URL,new ParamHolder(param,"1"));
	}
}
