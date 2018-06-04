package com.elsetravel.mis.service;

import java.util.List;

import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.gen.dbentity.user.User;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.statement.field.CustDBField;
import com.elsetravel.orm.statement.field.summary.CountQueryField;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="report",version="")
public class ReportService extends AbstractService {
	
	public ResponseBody queryUser(MisRequestContext context) throws Exception {
		List<User> userList = dao().query(new QueryStatement(User.class)
			.appendQueryField(new CustDBField("CREATE_TIME","%Y-%m-%d"))
			.appendQueryField(CountQueryField.getInstance())
		);
		
		return new ResponseBody("result",userList);
	}
}
