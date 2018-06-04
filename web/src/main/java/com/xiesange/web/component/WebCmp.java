package com.xiesange.web.component;

import java.util.List;

import com.xiesange.baseweb.define.BaseConsDefine;
import com.xiesange.core.util.DateUtil;
import com.xiesange.core.util.NullUtil;
import com.xiesange.gen.dbentity.sys.SysLogin;
import com.xiesange.gen.dbentity.user.User;
import com.xiesange.orm.FieldPair;
import com.xiesange.orm.pojo.JoinQueryData;
import com.xiesange.orm.sql.DBCondition;
import com.xiesange.orm.sql.DBOperator;
import com.xiesange.orm.statement.query.QueryStatement;
import com.xiesange.web.CustAccessToken;

public class WebCmp {
	public static CustAccessToken queryCustAccessToken(String token,BaseConsDefine.SYS_TYPE sysType) throws Exception {
		QueryStatement st = new QueryStatement(SysLogin.class,
				new DBCondition(SysLogin.JField.token, token),
				new DBCondition(SysLogin.JField.sysType, sysType.value()),
				new DBCondition(SysLogin.JField.expireTime, DateUtil.now(),
						DBOperator.GREAT_EQUALS));
		
		st.appendJoin(User.class, new FieldPair(SysLogin.JField.userId,User.JField.id));
		
		List<JoinQueryData> list = (List<JoinQueryData>)st.execute();
		if(NullUtil.isEmpty(list)){
			return null;
		}
		JoinQueryData joinResult = list.get(0);
		SysLogin sysLogin = joinResult.getResult(SysLogin.class);
		User user = joinResult.getResult(User.class);
		return new CustAccessToken(sysLogin,user);
		
	}
}
