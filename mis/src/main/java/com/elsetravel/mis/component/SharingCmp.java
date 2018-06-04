package com.elsetravel.mis.component;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.gen.dbentity.user.UserSharing;
import com.elsetravel.mis.define.ErrorDefine;
import com.elsetravel.orm.DBHelper;

public class SharingCmp {
	/**
	 * 查询达人分享会记录
	 * @param topicId
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午3:14:46
	 */
	public static UserSharing querySharing(long sharingId) throws Exception{
		return DBHelper.getDao().queryById(UserSharing.class,sharingId);
	}
	
	public static UserSharing checkExist(long sharingId) throws Exception{
		UserSharing sharing = querySharing(sharingId);
		if(sharing == null){
			throw ETUtil.buildException(ErrorDefine.SHARING_NOTEXIST);
		}
		return sharing;
	}
	
	public static void transfer(UserSharing sharing) throws Exception{
		if(sharing == null)
			return;
		sharing.setCoverPic(ETUtil.buildPicUrl(sharing.getCoverPic()));
	}
}
