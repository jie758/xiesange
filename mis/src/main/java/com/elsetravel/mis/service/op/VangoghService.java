package com.elsetravel.mis.service.op;

import java.util.List;

import com.elsetravel.baseweb.ETUtil;
import com.elsetravel.baseweb.request.ResponseBody;
import com.elsetravel.baseweb.service.AbstractService;
import com.elsetravel.baseweb.service.ETServiceAnno;
import com.elsetravel.core.util.NullUtil;
import com.elsetravel.gen.dbentity.op.OpVangogh;
import com.elsetravel.mis.define.OperParamDefine;
import com.elsetravel.mis.request.MisRequestContext;
import com.elsetravel.orm.statement.query.QueryStatement;
@ETServiceAnno(name="op_vangogh",version="")
public class VangoghService extends AbstractService {
	/**
	 * 查询梵高展投票结果
	 * @param context
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 下午1:25:33
	 */
	public ResponseBody queryList(MisRequestContext context) throws Exception{
		List<OpVangogh> resultList = dao().query(new QueryStatement(OpVangogh.class)
			.appendOrderFieldDesc(OpVangogh.JField.voteCount)
		);
		if(NullUtil.isEmpty(resultList))
			return null;
		for(OpVangogh entity : resultList){
			ETUtil.clearDBEntityExtraAttr(entity);
			entity.setWechatOpenid(null);
			entity.setMobile(null);
		}
		return new ResponseBody("result",resultList);
	}
	
	/**
	 * 修改。目前只能修改投票数
	 * @param context
	 * 			signup_id,修改的报名记录id
	 * 			vote_count
	 * 		
	 * @return
	 * @throws Exception
	 * @author Wilson 
	 * @date 上午11:19:54
	 */
	public ResponseBody modify(MisRequestContext context) throws Exception{
		Integer voteCount = context.getRequestBody().getInt(OperParamDefine.Vangogh.vote_count);
		
		
		return null;
	}
}
