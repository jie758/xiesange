package com.elsetravel.mis.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.component.CCP;
import com.elsetravel.baseweb.component.CountryCmp;
import com.elsetravel.baseweb.component.EnumCmp;
import com.elsetravel.baseweb.define.BaseConstantDefine;
import com.elsetravel.baseweb.define.BaseErrorDefine;
import com.elsetravel.baseweb.define.EnumDefine;
import com.elsetravel.baseweb.pojo.BaseDataHolder;
import com.elsetravel.baseweb.request.RequestBody;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.request.UploadRequestBody;
import com.elsetravel.baseweb.request.UploadRequestBody.UploadFile;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.baseweb.util.RequestUtil;
import com.elsetravel.core.util.CommonUtil;
import com.elsetravel.core.util.FileUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.core.util.PinyinUtil;
import com.elsetravel.core.xml.BaseNode;
import com.elsetravel.gen.dbentity.base.BaseBanner;
import com.elsetravel.gen.dbentity.base.BaseConfig;
import com.elsetravel.gen.dbentity.base.BaseEnum;
import com.elsetravel.gen.dbentity.base.BaseMainCatalog;
import com.elsetravel.gen.dbentity.base.BaseMedal;
import com.elsetravel.gen.dbentity.base.BaseMenu;
import com.elsetravel.gen.dbentity.base.BaseRewardRule;
import com.elsetravel.gen.dbentity.base.BaseTag;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.mis.component.BaseDataCmp;
import com.elsetravel.mis.component.CacheCmp;
import com.elsetravel.mis.component.MenuCmp;
import com.elsetravel.mis.component.RewardCmp;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.mis.define.ParamDefine;
import com.elsetravel.mis.pojo.RewardRuleAction;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.DBHelper;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.sql.DBOrCondition;
import com.elsetravel.orm.statement.query.QueryStatement;

/**
 * 基础数据服务
 * @author Wilson
 *
 */
@ETServiceAnno(name="base",version="")
public class BaseDataService extends AbstractService{
	/**
	 * 查询基础数据
	 * @param context
	 * 			sysparam_flag,标签，包括个人标签和旅票标签
	 * 			enum_flag,枚举值
	 * 			country_flag,国家
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @date 下午1:41:22
	 */
	public ResponseBody queryBaseData(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		Long country_flag = reqbody.getLong(ParamDefine.BaseData.country_flag);
		Long sysparam_flag = reqbody.getLong(ParamDefine.BaseData.sysparam_flag);
		Long enum_flag = reqbody.getLong(ParamDefine.BaseData.enum_flag);
		
		ResponseBody respBody = new ResponseBody();
		
		//sysparam
		BaseDataHolder<Map<String,String>> sysparam_cacheData = sysparam_flag != null && sysparam_flag == -1 ? 
							null:BaseDataCmp.getSysParamMap(sysparam_flag);
		BaseDataCmp.appendBaseData(respBody,sysparam_cacheData,"sysparamMap","sysparamFlag");
		
		//enum
		BaseDataHolder<List<Map<String,Object>>> enum_cacheData = enum_flag != null && enum_flag == -1 ? 
				null : BaseDataCmp.getEnumList(enum_flag);
		BaseDataCmp.appendBaseData(respBody,enum_cacheData,"enumList","enumFlag");
		
		//country
		BaseDataHolder<List<BaseNode>> country_cacheData = country_flag != null && country_flag == -1 ? 
				null : BaseDataCmp.getCountryList(country_flag);
		BaseDataCmp.appendBaseData(respBody,country_cacheData,"countryList","countryFlag");
		
		return respBody;
	}
	
	
	
	/*public static void main(String[] args) throws Exception {
		//设置json序列化
    	JsonUtil.DEFAULT_JSON_CONFIG.put(DBEntity.class, new DBEntitySerializer());
    	JsonUtil.DEFAULT_JSON_CONFIG.put(BaseNode.class, new BaseNodeSerializer());
    	
    	ETDateDeserializer dateDeserializer = new ETDateDeserializer();
		ParserConfig.getGlobalInstance().putDeserializer(Date.class, dateDeserializer);
		
		
		
		List<BaseNode> countryList = CountryCmp.getCountryNodeList();
		String firstname = null;
		BaseNode chinaNode = null;
		for(BaseNode country : countryList){
			firstname = PinyinUtil.getFirstSpell(country.getAttribute("name"));
			country.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
			if(country.getAttribute("code").equals("1")){
				chinaNode = country;
				break;
			}
		}
		
		List<BaseNode> provList = chinaNode.getChildren();
		
		for(BaseNode prov : provList){
			firstname = PinyinUtil.getFirstSpell(prov.getAttribute("name"));
			prov.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
		}
		
		System.out.println(JsonUtil.obj2Json(countryList));
	}*/
	public ResponseBody queryCityList(MisRequestContext context) throws Exception{
		List<BaseNode> countryList = CountryCmp.getCountryNodeList();
		String firstname = null;
		BaseNode chinaNode = null;
		//List<BaseNode> resultList = new ArrayList<BaseNode>();
		for(BaseNode country : countryList){
			firstname = PinyinUtil.getFirstSpell(country.getAttribute("name"));
			country.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
			
			if(country.getAttribute("code").equals("CN")){
				chinaNode = country;
			}else{
				List<BaseNode> abroadCityList = CountryCmp.getAbroadCityList(country.getAttribute("code"));
				if(NullUtil.isNotEmpty(abroadCityList)){
					for(BaseNode city : abroadCityList){
						firstname = PinyinUtil.getFirstSpell(city.getAttribute("name"));
						city.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
					}
				}
				country.setChildren(abroadCityList);
			}
		}
		
		List<BaseNode> provList = chinaNode.getChildren();
		
		for(BaseNode prov : provList){
			firstname = PinyinUtil.getFirstSpell(prov.getAttribute("name"));
			prov.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
			if(NullUtil.isEmpty(prov.getChildren()))
				continue;
			for(BaseNode cityNode : prov.getChildren()){
				firstname = PinyinUtil.getFirstSpell(cityNode.getAttribute("name"));
				cityNode.addAttribute("first_letter", String.valueOf(firstname.charAt(0)));
			}
		}
		
		return new ResponseBody("result",countryList);
	}
	
	/**
	 * 查询全球国家列表,不包括中国
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryAbroadCountryList(MisRequestContext context) throws Exception{
		List<BaseNode> countryList = CountryCmp.getCountryNodeList();
		//需要把中国给过滤掉
		List<BaseNode> abroadList = new ArrayList<BaseNode>();
		
		for(int i=1;i<countryList.size();i++){
			BaseNode countryNode = countryList.get(i);
			abroadList.add(countryNode);
			
			//需要告知前端当前城市下是不是具有城市，像小国家比如梵蒂冈这种下面没有城市,这样就前台不用再点进去了
			countryNode.addAttribute("has_city", NullUtil.isEmpty(countryNode.getChildren()) ? "0" : "1");
		}
		return new ResponseBody("result",abroadList);
	}
	
	/**
	 * 查询中国所有省份
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryChinaProvinceList(MisRequestContext context) throws Exception{
		BaseNode chinaNode = CountryCmp.getChinaNode();
		
		List<BaseNode> provList = chinaNode.getChildren();
		
		//需要告知前端当前省份下是不是具有城市，像直辖市下面就没有城市了,这样就不用再点进去了
		for(BaseNode node : provList){
			node.addAttribute("has_city", NullUtil.isEmpty(node.getChildren()) ? "0" : "1");
		}
		
		return new ResponseBody("result",provList);
	}
	
	/**
	 * 查询中国某个省份下的城市
	 * @param context
	 * 			province_code
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryChinaCityList(MisRequestContext context) throws Exception{
		RequestBody reqBody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqBody, ParamDefine.Area.province_code);
		
		String provCode = reqBody.getString(ParamDefine.Area.province_code);
		
		BaseNode provNode = CountryCmp.getProvinceNode(provCode);
		
		return new ResponseBody("result",provNode.getChildren());
		
	}
	
	/**
	 * 查询国外某个国家下的所有城市
	 * @param context
	 * 			country_code
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryAbroadCityList(MisRequestContext context) throws Exception{
		RequestBody reqBody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqBody, ParamDefine.Area.country_code);
		
		String countryCode = reqBody.getString(ParamDefine.Area.country_code);
		
		//BaseNode provNode = CountryCmp.getProvinceNode(provCode);
		
		return new ResponseBody("result",CountryCmp.getAbroadCityList(countryCode));
		
	}
	
	
	/**
	 * 登录后查询该用户所有具有的所有权限菜单列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月21日
	 */
	public ResponseBody queryMenuList(MisRequestContext context) throws Exception {
		List<BaseMenu> menuList =  dao().query(new QueryStatement(BaseMenu.class).appendOrderFieldDesc(BaseMenu.JField.createTime));
		
		List<BaseMenu> topList = new ArrayList<BaseMenu>();
		int layNo = -1;
		for(BaseMenu menu : menuList){
			layNo = menu.getLayNo();
			ETUtil.clearDBEntityExtraAttr(menu);
			menu.setLayNo(null);
			menu.setLayStr(null);
			//menu.setIsLeaf(null);
			if(layNo != 1){
				continue;
			}
			topList.add(menu);
			menu.addAttribute("children", MenuCmp.getChildrenList(menu.getId(), menuList));
		}
		
		
		return new ResponseBody("result",topList);
	}
	
	/**
	 * 添加某个菜单
	 * @param context
	 * 			parent_id
	 * 			name,
	 * 			url,
	 * 			is_leaf,是否是叶子节点
	 * 			memo
	 * @return
	 * @throws Exception
	 */
	public ResponseBody addMenu(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody, 
				ParamDefine.Menu.parent_id,
				ParamDefine.Menu.name,
				ParamDefine.Menu.is_leaf
		);
		
		Long parentId = reqbody.getLong(ParamDefine.Menu.parent_id);
		String name = reqbody.getString(ParamDefine.Menu.name);
		String url = reqbody.getString(ParamDefine.Menu.url);
		String memo = reqbody.getString(ParamDefine.Menu.memo);
		Short isLeaf = reqbody.getShort(ParamDefine.Menu.is_leaf);
		
		long newid = dao().getSequence(BaseMenu.class);
		int layNo = 0;
		String layStr = null;
		if(parentId > 0){
			BaseMenu parentMenu = MenuCmp.query(parentId);
			if(parentMenu == null){
				throw ETUtil.buildException(ErrorDefine.MENU_PARENT_NOTEXIST);
			}
			layNo = parentMenu.getLayNo()+1;
			layStr = parentMenu.getLayStr()+"-"+newid;
		}else if(parentId == -1){
			layNo = 1;
			layStr = String.valueOf(newid);
		}else{
			ETUtil.buildInvalidOperException();
		}
		
		BaseMenu menu = new BaseMenu();
		
		menu.setId(newid);
		menu.setParentId(parentId);
		menu.setName(name);
		menu.setUrl(url);
		menu.setMemo(memo);
		menu.setIsLeaf(isLeaf);
		menu.setLayNo(layNo);
		menu.setLayStr(layStr);
		dao().insert(menu);
		
		return new ResponseBody("result",menu.getId());
	}
	
	/**
	 * 修改菜单
	 * @param context
	 * 			menu_id,要修改的菜单id
	 * 			name,
	 * 			url,
	 * 			memo
	 * @return
	 * @throws Exception
	 */
	public ResponseBody modifyMenu(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody,ParamDefine.Menu.menu_id);
		
		long menuId = reqbody.getLong(ParamDefine.Menu.menu_id);
		BaseMenu menuEntity = MenuCmp.checkMenuExsit(menuId);
		
		
		String name = reqbody.getString(ParamDefine.Menu.name);
		String url = reqbody.getString(ParamDefine.Menu.url);
		String memo = reqbody.getString(ParamDefine.Menu.memo);
		
		if(name != null){
			menuEntity.setName(name);
		}
		if(url != null){
			menuEntity.setUrl(url);
		}
		if(memo != null){
			menuEntity.setMemo(memo);
		}
		
		if(NullUtil.isNotEmpty(menuEntity._getSettedValue())){
			dao().updateById(menuEntity, menuId);
		}
		
		return null;
	}
	
	/**
	 * 删除菜单,一次只能删除一个,如果是目录的话其下面所有子菜单都会被删除
	 * @param context
	 * 			menu_id,要修改的菜单id
	 * @return
	 * @throws Exception
	 */
	public ResponseBody removeMenu(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody,ParamDefine.Menu.menu_id);
		
		long menuId = reqbody.getLong(ParamDefine.Menu.menu_id);
		BaseMenu menuEntity = MenuCmp.checkMenuExsit(menuId);
		
		if(menuEntity.getIsLeaf() == 1){
			//如果是叶子节点,只删除本身
			dao().deleteById(BaseMenu.class, menuId);
		}else{
			//如果是目录节点，删除本身及其下所有子菜单
			dao().delete(BaseMenu.class, new DBCondition(
				new DBCondition(BaseMenu.JField.id,menuId),
				new DBOrCondition(BaseMenu.JField.layStr,menuEntity.getLayStr()+"-%",DBOperator.LIKE)
			));
		}
		return null;
	}
	/**
	 * 查询所有系统参数列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:51:20
	 */
	public ResponseBody queryConfigList(MisRequestContext context) throws Exception {
		List<BaseConfig> configList = dao().queryAll(BaseConfig.class);
		return new ResponseBody("result",configList);
	}
	
	/**
	 * 查询某个系统参数值
	 * @param context
	 * 			code,要查询的系统参数编码
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:51:20
	 */
	public ResponseBody queryConfig(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Config.code
		);
		
		String code = reqbody.getString(ParamDefine.Config.code);
		
		List<BaseConfig> configList = dao().queryAll(BaseConfig.class);
		return new ResponseBody("result",configList);
	}
	
	/**
	 * 添加系统参数
	 * @param context
	 * 			name,
	 * 			code,
	 * 			value,
	 * 			type,1-开发级，2-业务级
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:03:41
	 */
	public ResponseBody addConfig(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Config.code,
			ParamDefine.Common.name,
			ParamDefine.Config.value,
			ParamDefine.Config.type
		);
		String code = reqbody.getString(ParamDefine.Config.code);
		String name = reqbody.getString(ParamDefine.Common.name);
		String value = reqbody.getString(ParamDefine.Config.value);
		Short type = reqbody.getShort(ParamDefine.Config.type);
		String memo = reqbody.getString(ParamDefine.Common.memo);
		
		BaseConfig configEntity = dao().querySingle(BaseConfig.class, new DBCondition(BaseConfig.JField.code,code));
		if(configEntity != null){
			throw ETUtil.buildException(ErrorDefine.CODE_DUPLICATE);
		}
		
		configEntity = new BaseConfig();
		configEntity.setName(name);
		configEntity.setCode(code);
		configEntity.setValue(value);
		configEntity.setType(type);
		configEntity.setMemo(memo);
		dao().insert(configEntity);
		
		return new ResponseBody("newid",configEntity.getId());
	}
	/**
	 * 修改系统参数。编码不能修改，因为编码是被程序引用到的，不能随意被修改
	 * @param context
	 * 			name,
	 * 			value,
	 * 			type,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:03:41
	 */
	public ResponseBody modifyConfig(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Config.config_id
		);
		long configId = reqbody.getLong(ParamDefine.Config.config_id);
		BaseConfig configEntity = dao().queryById(BaseConfig.class, configId);
		if(configEntity == null){
			throw ETUtil.buildException(ErrorDefine.CONFIG_NOTEXIST);
		}
		//String code = reqbody.getString(ParamDefine.Config.code);
		String name = reqbody.getString(ParamDefine.Common.name);
		String value = reqbody.getString(ParamDefine.Config.value);
		Short type = reqbody.getShort(ParamDefine.Config.type);
		String memo = reqbody.getString(ParamDefine.Common.memo);
		
		if(NullUtil.isNotEmpty(name)){
			configEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(value)){
			configEntity.setValue(value);
		}
		if(type != null){
			configEntity.setType(type);
		}
		if(memo != null){
			configEntity.setMemo(memo);
		}
		
		
		dao().updateById(configEntity, configId);
		
		return null;
	}
	public ResponseBody removeConfig(MisRequestContext context) throws Exception {
		RequestBody reqbody = context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Config.config_id
		);
		long configId = reqbody.getLong(ParamDefine.Config.config_id);
		
		dao().deleteById(BaseConfig.class, configId);
		return null;
	}
	
	
	
	/**
	 * 查询旅行票类别定义列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	/*public ResponseBody queryTicketCatalogList(MisRequestContext context) throws Exception {
		List<BaseTicketCatalog> catalogList = BaseDataCmp.queryTicketCatalogDefine();
		if(NullUtil.isEmpty(catalogList))
			return null;
		for(BaseTicketCatalog catalog : catalogList){
			catalog.setPic(ETUtil.buildPicUrl(catalog.getPic()));
		}
		return new ResponseBody("result",catalogList);
	}*/
	
	
	/**
	 * 查询系统中所定义的语种。
	 * 语种在枚举值表中进行配置
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson Wu
	 * @date 2015年9月14日
	 */
	public ResponseBody queryLangList(MisRequestContext context) throws Exception {
		List<BaseEnum> items = EnumCmp.getEnumTypeItems(EnumDefine.USER_LANG);
		return new ResponseBody("result",items);
	}
	
	/**
	 * 查询旅行票类型的所有标签定义列表
	 * @param context
	 * 			type,1-旅票标签，2-导游标签
	 * @return
	 * @throws Exception
	 */
	public ResponseBody queryTagDefineList(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Tag.type);
		
		Short type = context.getRequestBody().getShort(ParamDefine.Tag.type);
		List<BaseTag> tagList = BaseDataCmp.queryTagDefineList(type);
		return new ResponseBody("result",tagList);
	}
	
	/**
	 * 添加导游标签
	 * @param context
	 * 			name,标签名称
	 * 			type,1-旅票标签，2-导游标签
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:33:09
	 */
	public ResponseBody addTagDefine(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
				ParamDefine.Common.name,
				ParamDefine.Tag.type
		);
		BaseTag tag = new BaseTag();
		tag.setName(context.getRequestBody().getString(ParamDefine.Common.name));
		tag.setType(context.getRequestBody().getShort(ParamDefine.Tag.type));
		dao().insert(tag);
		
		CacheCmp.refreshTag();
		
		return new ResponseBody("newid",tag.getId());
	}
	/**
	 * 修改导游标签定义
	 * @param context
	 * 			tag_id,要修改的标签id
	 * 			name,新的标签名称
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:38:08
	 */
	public ResponseBody modifyTagDefine(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Tag.tag_id
		);
		
		long tagId = context.getRequestBody().getLong(ParamDefine.Tag.tag_id);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		
		BaseTag tag = new BaseTag();
		if(NullUtil.isNotEmpty(name)){
			tag.setName(name);
		}
		
		if(NullUtil.isNotEmpty(tag._getSettedValue())){
			dao().updateById(tag, tagId);
		}
		
		CacheCmp.refreshTag();
		
		return null;
	}
	/**
	 * 移除某个标签定义
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午5:55:18
	 */
	public ResponseBody removeTagDefine(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Tag.tag_id
		);
		dao().deleteById(BaseTag.class, context.getRequestBody().getLong(ParamDefine.Tag.tag_id));
		
		CacheCmp.refreshTag();
		
		return null;
	}
	
	
	/**
	 * 查询所有勋章定义列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:19:15
	 */
	public ResponseBody queryMedalList(MisRequestContext context) throws Exception {
		List<BaseMedal> medalList = dao().queryAll(BaseMedal.class);
		
		if(NullUtil.isEmpty(medalList))
			return null;
		
		for(BaseMedal medal : medalList){
			medal.setPic(ETUtil.buildPicUrl(medal.getPic()));
			ETUtil.clearDBEntityExtraAttr(medal);
		}
		
		return new ResponseBody("result",medalList);
	}
	/**
	 * 添加勋章定义
	 * @param context
	 * 			name,
	 * 			group_code,
	 * 			pic
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:20:44
	 */
	public ResponseBody addMedal(MisRequestContext context) throws Exception {
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Common.name
		);
		
		BaseMedal medal = new BaseMedal();
		medal.setId(dao().getSequence(BaseMedal.class));
		medal.setName(reqbody.getString(ParamDefine.Common.name));
		medal.setGroupCode(reqbody.getString(ParamDefine.Medal.group_code));
		
		//处理图片
		List<UploadFile> picList = reqbody.getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/medal/",medal.getId(),".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			medal.setPic(picPath+"?t="+System.currentTimeMillis());
		}
		dao().insert(medal);
		
		CacheCmp.refreshMedal();
		
		return new ResponseBody("newid",medal.getId());
	}
	
	/**
	 * 修改勋章定义
	 * @param context
	 * 			medal_id,
	 * 			name,
	 * 			group_code,
	 * 			pic
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:24:17
	 */
	public ResponseBody modifyMedal(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Medal.medal_id
		);
		long medalId = context.getRequestBody().getLong(ParamDefine.Medal.medal_id);
		
		BaseMedal medal = new BaseMedal();
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String groupCode = context.getRequestBody().getString(ParamDefine.Medal.group_code);
		if(NullUtil.isNotEmpty(name)){
			medal.setName(name);
		}
		if(NullUtil.isNotEmpty(groupCode)){
			medal.setGroupCode(groupCode);
		}
		
		//处理背景
		List<UploadFile> picList = ((UploadRequestBody)context.getRequestBody()).getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/medal/",medal.getId(),".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			medal.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(NullUtil.isNotEmpty(medal._getSettedValue())){
			dao().updateById(medal, medalId);
		}
		
		CacheCmp.refreshMedal();
		return null;
	}
	/**
	 * 移除勋章定义
	 * @param context
	 * 			medal_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:26:01
	 */
	public ResponseBody removeMedal(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Medal.medal_id
		);
		long medalId = context.getRequestBody().getLong(ParamDefine.Medal.medal_id);
		BaseMedal medal = dao().queryById(BaseMedal.class, medalId);
		
		dao().deleteById(BaseMedal.class, medalId);
		
		//删除对应的背景图片
		if(NullUtil.isNotEmpty(medal.getPic()) && !medal.getPic().startsWith("http")){
			FileUtil.delFile(ETUtil.buildPicPath(medal.getPic()));
		}
		
		CacheCmp.refreshMedal();
		return null;
	}
	
	/**
	 * 查询banner列表
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:27:22
	 */
	public ResponseBody queryBannerList(MisRequestContext context) throws Exception {
		List<BaseBanner> bannerList = dao().queryAll(BaseBanner.class);
		
		if(NullUtil.isEmpty(bannerList))
			return null;
		
		for(BaseBanner banner : bannerList){
			banner.setPic(ETUtil.buildPicUrl(banner.getPic()));
			ETUtil.clearDBEntityExtraAttr(banner);
		}
		
		return new ResponseBody("result",bannerList);
	}
	/**
	 * 添加banner
	 * @param context
	 * 			name,
	 * 			target_type,
	 * 			target_value,
	 * 			pic
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:27:53
	 */
	public ResponseBody addBanner(MisRequestContext context) throws Exception {
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.Common.name,
			ParamDefine.Banner.target_type,
			ParamDefine.Banner.target_value
		);
		
		BaseBanner banner = new BaseBanner();
		banner.setId(dao().getSequence(BaseBanner.class));
		banner.setName(reqbody.getString(ParamDefine.Common.name));
		banner.setTargetType(reqbody.getShort(ParamDefine.Banner.target_type));
		banner.setTargetValue(reqbody.getString(ParamDefine.Banner.target_value));
		//处理图片
		List<UploadFile> picList = reqbody.getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/banner/",banner.getId(),".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			banner.setPic(picPath);
		}
		dao().insert(banner);
		
		CacheCmp.refreshBanner();
		return new ResponseBody("newid",banner.getId());
	}
	/**
	 * 修改banner
	 * @param context
	 * 			banner_id,
	 * 			name,
	 * 			target_type,
	 * 			target_value,
	 * 			pic
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:32:09
	 */
	public ResponseBody modifyBanner(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Banner.banner_id
		);
		long bannerId = context.getRequestBody().getLong(ParamDefine.Banner.banner_id);
		
		BaseBanner banner = new BaseBanner();
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		Short targetType = context.getRequestBody().getShort(ParamDefine.Banner.target_type);
		String targetValue = context.getRequestBody().getString(ParamDefine.Banner.target_value);
		
		if(NullUtil.isNotEmpty(name)){
			banner.setName(name);
		}
		if(targetType != null){
			banner.setTargetType(targetType);
		}
		if(targetValue != null){
			banner.setTargetValue(targetValue);
		}
		
		//处理背景
		List<UploadFile> picList = ((UploadRequestBody)context.getRequestBody()).getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/banner/",bannerId,".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			banner.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(NullUtil.isNotEmpty(banner._getSettedValue())){
			dao().updateById(banner, bannerId);
		}
		
		CacheCmp.refreshBanner();
		
		return null;
	}
	
	/**
	 * 移除banner
	 * @param context
	 * 			banner_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:34:32
	 */
	public ResponseBody removeBanner(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Banner.banner_id
		);
		long bannerId = context.getRequestBody().getLong(ParamDefine.Banner.banner_id);
		BaseBanner banner = dao().queryById(BaseBanner.class, bannerId);
		
		dao().deleteById(BaseBanner.class, bannerId);
		
		//删除对应的背景图片
		if(NullUtil.isNotEmpty(banner.getPic()) && !banner.getPic().startsWith("http")){
			FileUtil.delFile(ETUtil.buildPicPath(banner.getPic()));
		}
		
		
		
		CacheCmp.refreshBanner();
		
		return null;
	}
	
	/**
	 * 查询所有奖励规则
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:59:47
	 */
	public ResponseBody queryRewardRuleList(MisRequestContext context) throws Exception {
		return new ResponseBody("result",RewardCmp.getAllRewardRuleList());
	}
	/**
	 * 添加奖励规则
	 * @param context
	 * 			name,
	 * 			action,
	 * 			trigger_value,
	 * 			trigger_period,
	 * 			trigger_target_id,
	 * 			reward_type,
	 * 			reward_value,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午7:00:45
	 */
	public ResponseBody addRewardRule(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Common.name,
			ParamDefine.RewardRule.action,
			ParamDefine.RewardRule.trigger_value,
			ParamDefine.RewardRule.trigger_period_type,
			ParamDefine.RewardRule.reward_type,
			ParamDefine.RewardRule.reward_value
		);
		RequestBody reqbody = context.getRequestBody();
		String action = reqbody.getString(ParamDefine.RewardRule.action);
		short period = reqbody.getShort(ParamDefine.RewardRule.trigger_period_type);
		if(!RewardCmp.isTriggerCycle(period) && !RewardCmp.isTriggerOnce(period)){
			ETUtil.buildException(BaseErrorDefine.SYS_PARAM_INVALID, ParamDefine.RewardRule.action.name());
		}
		BaseRewardRule rule = new BaseRewardRule();
		rule.setName(reqbody.getString(ParamDefine.Common.name));
		rule.setAction(action);
		rule.setTriggerValue(reqbody.getInt(ParamDefine.RewardRule.trigger_value));
		rule.setTriggerPeriodType(period);
		rule.setTriggerTargetId(reqbody.getLong(ParamDefine.RewardRule.trigger_target_id));
		rule.setRewardType(reqbody.getShort(ParamDefine.RewardRule.reward_type));
		rule.setRewardValue(reqbody.getLong(ParamDefine.RewardRule.reward_value));
		rule.setMemo(reqbody.getString(ParamDefine.Common.memo));
		
		dao().insert(rule);
		
		CacheCmp.refreshRewardRule();
		
		return new ResponseBody("newid",rule.getId());
	}
	/**
	 * 修改奖励规则
	 * @param context
	 * 			reward_rule_id,
	 * 			name,
	 * 			action,
	 * 			trigger_value,
	 * 			trigger_period,
	 * 			trigger_target_id,
	 * 			reward_type,
	 * 			reward_value,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午7:33:56
	 */
	public ResponseBody modifyRewardRule(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.RewardRule.reward_rule_id
		);
		RequestBody reqbody = context.getRequestBody();
		long ruleId = reqbody.getLong(ParamDefine.RewardRule.reward_rule_id);
		
		
		String name = reqbody.getString(ParamDefine.Common.name);
		String memo = reqbody.getString(ParamDefine.Common.memo);
		Integer triggerValue = reqbody.getInt(ParamDefine.RewardRule.trigger_value);
		Short triggerPeriodType = reqbody.getShort(ParamDefine.RewardRule.trigger_period_type);
		Long targetId = reqbody.getLong(ParamDefine.RewardRule.trigger_target_id);
		Short rewardType = reqbody.getShort(ParamDefine.RewardRule.reward_type);
		Long rewardValue = reqbody.getLong(ParamDefine.RewardRule.reward_value);
		
		BaseRewardRule rule = new BaseRewardRule();
		if(NullUtil.isNotEmpty(name)){
			rule.setName(name);
		}
		if(triggerValue != null){
			rule.setTriggerValue(triggerValue);
		}
		if(triggerPeriodType != null){
			rule.setTriggerPeriodType(triggerPeriodType);
		}
		if(targetId != null){
			rule.setTriggerTargetId(targetId);
		}
		if(rewardType != null){
			rule.setRewardType(rewardType);
		}
		if(rewardValue != null){
			rule.setRewardValue(rewardValue);
		}
		if(memo != null){
			rule.setMemo(memo);
		}
		
		if(NullUtil.isNotEmpty(rule._getSettedValue())){
			dao().updateById(rule, ruleId);
		}
		
		CacheCmp.refreshRewardRule();
		
		return null;
	}
	/**
	 * 移除奖励规则
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午8:12:51
	 */
	public ResponseBody removeRewardRule(MisRequestContext context) throws Exception {
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.RewardRule.reward_rule_id
		);
		long ruleId = context.getRequestBody().getLong(ParamDefine.RewardRule.reward_rule_id);
		dao().deleteById(BaseRewardRule.class, ruleId);
		
		CacheCmp.refreshRewardRule();
		return null;
	}
	
	/**
	 * 查询奖励事件
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:17:09
	 */
	public ResponseBody queryRewardActionList(MisRequestContext context) throws Exception {
		List<RewardRuleAction> result = new ArrayList<RewardRuleAction>();
		
		result.add(new RewardRuleAction(RewardCmp.ACTION_LOGIN,"登录"));
		result.add(new RewardRuleAction(RewardCmp.ACTION_CONSUME_CATALOG,"消费指定旅票类别"));
		result.add(new RewardRuleAction(RewardCmp.ACTION_CONSUME_ORDER,"游客消费订单"));
		result.add(new RewardRuleAction(RewardCmp.ACTION_COMPLETE_ORDER,"导游完成订单"));
		result.add(new RewardRuleAction(RewardCmp.ACTION_PUBLISH_TICKET,"导游发布旅票"));
		result.add(new RewardRuleAction(RewardCmp.ACTION_EXCHANGE,"积分兑换"));
		
		return new ResponseBody("result",result);
	}
	
	/**
	 * 查询枚举值类型列表
	 * @param context
	 * @return
	 * @author Wilson 
	 * @throws Exception 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @date 下午3:58:01
	 */
	public ResponseBody queryEnumTypeList(MisRequestContext context) throws Exception{
		List<BaseEnum> enumList = EnumCmp.getAllEnumTypes();
		ETUtil.clearDBEntityExtraAttr(enumList);
		
		return new ResponseBody("result",enumList);
	}
	/**
	 * 查询某个枚举类别下的所有枚举条目列表
	 * @param context
	 * 			type_code,枚举类别编码
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:11:25
	 */
	public ResponseBody queryEnumItemList(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), ParamDefine.Enum.type_code);
		String typeCode = context.getRequestBody().getString(ParamDefine.Enum.type_code);
		
		List<BaseEnum> enumList = EnumCmp.getEnumTypeItems(typeCode);
		ETUtil.clearDBEntityExtraAttr(enumList);
		
		return new ResponseBody("result",enumList);
	}
	/**
	 * 查询某个枚举类别下的某个枚举条目
	 * @param context
	 * 			type_code,枚举类别编码
	 * 			item_code,枚举类别下的某个条目编码
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:11:25
	 */
	public ResponseBody queryEnumItem(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.type_code,
			ParamDefine.Enum.item_code
		);
		String typeCode = context.getRequestBody().getString(ParamDefine.Enum.type_code);
		String itemCode = context.getRequestBody().getString(ParamDefine.Enum.item_code);
		
		BaseEnum enumEntity = EnumCmp.getEnumTypeItem(typeCode,itemCode);
		ETUtil.clearDBEntityExtraAttr(enumEntity);
		return new ResponseBody("result",enumEntity);
	}
	
	/**
	 * 新增枚举类别
	 * @param context
	 * 			type_code,
	 * 			name,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:23:58
	 */
	public ResponseBody addEnumType(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.type_code,
			ParamDefine.Common.name
		);
		String typeCode = context.getRequestBody().getString(ParamDefine.Enum.type_code);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		BaseEnum enumEntity = new BaseEnum();
		enumEntity.setParentId(-1L);
		enumEntity.setIsLeaf((short)0);//非叶子节点表示是枚举类别
		enumEntity.setCode(typeCode);
		enumEntity.setName(name);
		enumEntity.setMemo(memo);
		dao().insert(enumEntity);
		
		CacheCmp.refreshEnum(true);
		
		return new ResponseBody("newid",enumEntity.getId());
	}
	/**
	 * 修改枚举类别。
	 * @param context
	 * 			type_id,
	 * 			code,
	 * 			name,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:23:58
	 */
	public ResponseBody modifyEnumType(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.type_id
		);
		long enumId = context.getRequestBody().getLong(ParamDefine.Enum.type_id);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		
		BaseEnum enumEntity = new BaseEnum();
		if(NullUtil.isNotEmpty(name)){
			enumEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(code)){
			enumEntity.setCode(code);
		}
		if(memo != null){
			enumEntity.setMemo(memo);
		}
		if(NullUtil.isNotEmpty(enumEntity._getSettedValue())){
			dao().updateById(enumEntity, enumId);
		}
		
		CacheCmp.refreshEnum(true);
		return null;
	}
	/**
	 * 移除枚举值类别。会自动把该类别下的所有条目也移除掉。
	 * @param context
	 * 			type_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:31:48
	 */
	public ResponseBody removeEnumType(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.type_id
		);
		long enumId = context.getRequestBody().getLong(ParamDefine.Enum.type_id);
		
		dao().delete(BaseEnum.class, 
			new DBCondition(BaseEnum.JField.id,enumId),
			new DBOrCondition(BaseEnum.JField.parentId,enumId)
		);
		CacheCmp.refreshEnum(true);
		return null;
	}
	/**
	 * 新增枚举条目。条目必须归属到某个枚举类别下
	 * @param context
	 * 			type_id
	 * 			item_code,
	 * 			name,
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:23:58
	 */
	public ResponseBody addEnumItem(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.type_id,
			ParamDefine.Enum.item_code,
			ParamDefine.Common.name
		);
		long typeId = context.getRequestBody().getLong(ParamDefine.Enum.type_id);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		BaseEnum enumEntity = new BaseEnum();
		enumEntity.setParentId(typeId);
		enumEntity.setIsLeaf((short)1);//叶子节点表示是枚举条目
		enumEntity.setCode(code);
		enumEntity.setName(name);
		enumEntity.setMemo(memo);
		dao().insert(enumEntity);
		CacheCmp.refreshEnum(true);
		return new ResponseBody("newid",enumEntity.getId());
	}
	/**
	 * 修改枚举条目。
	 * @param context
	 * 			item_id,
	 * 			name,
	 * 			code
	 * 			memo
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:23:58
	 */
	public ResponseBody modifyEnumItem(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.item_id
		);
		long enumId = context.getRequestBody().getLong(ParamDefine.Enum.item_id);
		String name = context.getRequestBody().getString(ParamDefine.Common.name);
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String memo = context.getRequestBody().getString(ParamDefine.Common.memo);
		
		BaseEnum enumEntity = new BaseEnum();
		if(NullUtil.isNotEmpty(name)){
			enumEntity.setName(name);
		}
		if(NullUtil.isNotEmpty(code)){
			enumEntity.setCode(code);
		}
		if(memo != null){
			enumEntity.setMemo(memo);
		}
		if(NullUtil.isNotEmpty(enumEntity._getSettedValue())){
			dao().updateById(enumEntity, enumId);
		}
		CacheCmp.refreshEnum(true);
		return null;
	}
	/**
	 * 移除枚举值条目。
	 * @param context
	 * 			item_id
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:31:48
	 */
	public ResponseBody removeEnumItem(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.Enum.item_id
		);
		long enumId = context.getRequestBody().getLong(ParamDefine.Enum.item_id);
		
		dao().deleteById(BaseEnum.class,enumId);
		CacheCmp.refreshEnum(true);
		return null;
	}
	
	/*public ResponseBody queryHomepagePicList(MisRequestContext context) throws Exception{
		List<BaseEnum> enumList = EnumCmp.getEnumTypeItems(EnumDefine.HOMEPAGE);
		if(NullUtil.isEmpty(enumList))
			return null;
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(BaseEnum enm : enumList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("pic", ETUtil.buildPicUrl(enm.getCode()));
			map.put("id", enm.getId());
			result.add(map);
		}
		return new ResponseBody("result",result);
	}*/
	
	/*public ResponseBody addHomepagePic(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		
		BaseEnum enumType = EnumCmp.getEnumType(EnumDefine.HOMEPAGE);
		if(enumType == null){
			enumType = new BaseEnum();
			enumType.setName("官网首页");
			enumType.setCode(EnumDefine.HOMEPAGE);
			enumType.setParentId(-1L);
			enumType.setIsLeaf((short)0);
			dao().insert(enumType);
		}
		
		BaseEnum homepageItem = new BaseEnum();
		homepageItem.setId(dao().getSequence(BaseEnum.class));
		homepageItem.setParentId(enumType.getId());
		homepageItem.setIsLeaf((short)1);
		
		//处理图片
		List<UploadFile> picList = reqbody.getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/homepage/",homepageItem.getId(),".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			homepageItem.setCode(picPath+"?t="+System.currentTimeMillis());
		}
		dao().insert(homepageItem);
		
		CacheCmp.refreshEnum(false);//官网只在当前应用中，不需要刷新其它应用
		return new ResponseBody("newid",homepageItem.getId());
		
	}
	public ResponseBody modifyHomepagePic(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.HomePage.homepage_id
		);
		long homepageId = context.getRequestBody().getLong(ParamDefine.HomePage.homepage_id);
		
		BaseEnum enumItem = EnumCmp.getEnum(homepageId);
		BaseEnum enumType = EnumCmp.getEnum(enumItem.getParentId());
		if(enumType.getIsLeaf() != 0 || !enumType.getCode().equals(EnumDefine.HOMEPAGE)){
			throw ETUtil.buildInvalidOperException("数据异常");
		}
		
		//处理背景
		List<UploadFile> picList = ((UploadRequestBody)context.getRequestBody()).getUploadFiles();
		if(NullUtil.isNotEmpty(picList)){
			UploadFile file = picList.get(0);//只会有一个
			String picPath = CommonUtil.join("image/homepage/",homepageId,".",file.getExtendName());
			picPath = CCP.uploadImage(file, picPath,true);
			enumItem.setCode(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(NullUtil.isNotEmpty(enumItem._getSettedValue())){
			dao().updateById(enumItem, homepageId);
		}
		
		CacheCmp.refreshEnum(false);//官网只在当前应用中，不需要刷新其它应用
		return null;
	}
	public ResponseBody removeHomepagePic(MisRequestContext context) throws Exception{
		RequestUtil.checkEmptyParams(context.getRequestBody(), 
			ParamDefine.HomePage.homepage_id
		);
		long homepageId = context.getRequestBody().getLong(ParamDefine.HomePage.homepage_id);
		BaseEnum enm = CacheManager.getCacheHouse(BaseEnum.class).getById(homepageId);
		
		dao().deleteById(BaseEnum.class,homepageId);
		
		//删除对应的背景图片
		if(!enm.getCode().startsWith("http")){
			FileUtil.delFile(ETUtil.buildPicPath(enm.getCode()));
		}
		
		CacheCmp.refreshEnum(false);//官网只在当前应用中，不需要刷新其它应用
		return null;
	}*/
	
	
	
	/**
	 * 保存文章内容
	 * @param context
	 * 			code,文章编码
	 * 			article_id,文章id，因为文章内容在uploadPic的时候也会生成文章id，所以不能用文章id来判断是否是新增，而是需要根据code来查询一下是否存在
	 * 			content,文章内容，html
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午6:11:00
	 *//*
	public ResponseBody saveArticle(MisRequestContext context) throws Exception{
		String code = context.getRequestBody().getString(ParamDefine.Common.code);
		String content = context.getRequestBody().getString(ParamDefine.Article.content);
		Long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		
		BaseArticle article = BaseDataCmp.queryArticleByCode(code);
		
		if(article == null){
			article = new BaseArticle();
		}
		article.setContent(content);
		if(article.getId() == null){
			article.setCode(code);
			article.setId(articleId);
			DBHelper.getDao().insert(article);
		}else{
			DBHelper.getDao().updateById(article, article.getId());
		}
		
		return null;
	}*/
	
	/**
	 * 上传文章图文编辑中的图片。一次性只能上传一张
	 * @param context
	 * 			ticket_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:35:56
	 *//*
	public ResponseBody uploadArticlePics(MisRequestContext context) throws Exception{
		Long articleId = context.getRequestBody().getLong(ParamDefine.Article.article_id);
		UploadRequestBody requestBody = (UploadRequestBody)context.getRequestBody();
		
		List<UploadFile> files = requestBody.getUploadFiles();
		
		if(NullUtil.isEmpty(files))
			return null;
		if(articleId == null){
			articleId = DBHelper.getDao().getSequence(BaseArticle.class);
		}
		UploadFile file = files.get(0);
		String picPath = CommonUtil.join("/image/article/",articleId,"/",file.getFileName(),".",file.getExtendName());
		picPath = CCP.uploadFile(file, picPath,false);
		
		return new ResponseBody("newid",articleId).add("picPath",picPath).add("picUrl", ETUtil.buildPicUrl(picPath));
		
	}*/
	
	/**
	 * 查询app首页分类信息
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:39:37
	 */
	public ResponseBody queryMainCatalogList(MisRequestContext context) throws Exception{
		List<BaseMainCatalog> catalogList = dao().queryAll(BaseMainCatalog.class);
		if(NullUtil.isEmpty(catalogList)){
			return null;
		}
		for(BaseMainCatalog catalog : catalogList){
			catalog.setPic(ETUtil.buildPicUrl(catalog.getPic()));
			ETUtil.clearDBEntityExtraAttr(catalog);
		}
		return new ResponseBody("result",catalogList);
	}
	/**
	 * 修改/新增首页分类信息
	 * @param context
	 * 			main_catalog_id,如果为null表示新增，如果有值表示修改
	 * 			pic,二进制流
	 * 			country_code,
	 * 			title,
	 * 			summary
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:39:37
	 */
	public ResponseBody saveMainCatalog(MisRequestContext context) throws Exception{
		UploadRequestBody reqbody = (UploadRequestBody)context.getRequestBody();
		
		Long catalogId = context.getRequestBody().getLong(ParamDefine.MainCatalog.main_catalog_id);
		
		BaseMainCatalog catalogEntity = null;
		boolean isNew = catalogId == null;
		if(isNew){
			RequestUtil.checkEmptyParams(reqbody, 
				ParamDefine.MainCatalog.title,
				ParamDefine.MainCatalog.summary,
				ParamDefine.MainCatalog.country_code,
				ParamDefine.MainCatalog.pic
			);
			catalogEntity = new BaseMainCatalog();
			catalogId = dao().getSequence(BaseMainCatalog.class);
			catalogEntity.setId(catalogId);
		}else{
			catalogEntity = dao().queryById(BaseMainCatalog.class, catalogId);
			if(catalogEntity == null){
				return null;
			}
		}
		
		
		String title = reqbody.getString(ParamDefine.MainCatalog.title);
		String summary = reqbody.getString(ParamDefine.MainCatalog.summary);
		String countryCode = reqbody.getString(ParamDefine.MainCatalog.country_code);
		UploadFile picFile = reqbody.getUploadFile("pic");
		
		if(title != null){
			catalogEntity.setTitle(title);
		}
		if(summary != null){
			catalogEntity.setSummary(summary);
		}
		if(countryCode != null){
			catalogEntity.setCountryCode(countryCode);
		}
		
		if(picFile != null){
			String picPath = CommonUtil.join("image/catalog/",catalogId,".",picFile.getExtendName());
			picPath = CCP.uploadImage(picFile, picPath,true);
			catalogEntity.setPic(picPath+"?t="+System.currentTimeMillis());//加上时间戳，放置前端缓存
		}
		
		if(isNew){
			dao().insert(catalogEntity);
		}else if(DBHelper.isModified(catalogEntity)){
			dao().updateById(catalogEntity, catalogId);
		}
		
		return new ResponseBody("newid",catalogId);
	}
	/**
	 * 删除首页分类信息
	 * @param context
	 * 			main_catalog_id,必传
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午4:39:37
	 */
	public ResponseBody removeMainCatalog(MisRequestContext context) throws Exception{
		RequestBody reqbody = context.getRequestBody();
		
		RequestUtil.checkEmptyParams(reqbody, 
			ParamDefine.MainCatalog.main_catalog_id
		);
		Long catalogId = context.getRequestBody().getLong(ParamDefine.MainCatalog.main_catalog_id);
		dao().deleteById(BaseMainCatalog.class, catalogId);
		
		return null;
	}
}
