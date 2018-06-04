package com.xiesange.baseweb.service;

import org.apache.log4j.Logger;

import com.xiesange.baseweb.CommonDao;
import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.SpringUtil;
public abstract class AbstractService {
	protected Logger logger = LogUtil.getLogger(this.getClass());
	protected CommonDao dao(){
		return ((CommonDao)SpringUtil.getBeanByName("commonDao"));
	}
}
