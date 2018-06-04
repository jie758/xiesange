package com.elsetravel.mis.component;

import java.util.List;

import com.elsetravel.core.util.DateUtil;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.mis.MisStaff;
import com.elsetravel.gen.dbentity.sys.SysLogin;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.request.MisAccessToken;
import com.elsetravel.orm.FieldPair;
import com.elsetravel.orm.pojo.JoinQueryData;
import com.elsetravel.orm.sql.DBCondition;
import com.elsetravel.orm.sql.DBOperator;
import com.elsetravel.orm.statement.query.QueryStatement;

public class MisCmp {
	public static MisAccessToken queryMisAccessToken(String token, short sysType) throws Exception {
		QueryStatement st = new QueryStatement(SysLogin.class,
				new DBCondition(SysLogin.JField.token, token),
				new DBCondition(SysLogin.JField.sysType, sysType),
				new DBCondition(SysLogin.JField.expireTime, DateUtil.now(),DBOperator.GREAT_EQUALS));
		
		st.appendJoin(MisStaff.class, new FieldPair(SysLogin.JField.userId,MisStaff.JField.id));
		
		List<JoinQueryData> list = (List<JoinQueryData>)st.execute();
		if(NullUtil.isEmpty(list)){
			return null;
		}
		JoinQueryData joinResult = list.get(0);
		SysLogin sysLogin = joinResult.getResult(SysLogin.class);
		MisStaff user = joinResult.getResult(MisStaff.class);
		return new MisAccessToken(sysLogin,user);
		
	}
}
