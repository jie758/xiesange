package com.xiesange.gen.dbentity.orders;

import com.xiesange.orm.DBEntity;
import com.xiesange.orm.statement.field.BaseJField;
import com.xiesange.orm.annotation.DBTableAnno;

@DBTableAnno(name = "ORDERS",primaryKey="ID",indexes="CODE(CODE)")
public class Orders extends DBEntity implements net.sf.cglib.proxy.Factory{
    private Long id;
    private String code;
    private Long userId;
    private Short buyType;
    private String name;
    private Long origSum;
    private Long sum;
    private Long expressSum;
    private String mobile;
    private String address;
    private java.util.Date expressDate;
    private String expressNo;
    private Integer expressWeight;
    private Long expressCost;
    private Long cost;
    private Long purchaseId;
    private Long couponId;
    private Long promotionId;
    private Long groupbuyId;
    private Short payChannel;
    private String payAccount;
    private String transactionCode;
    private Short channel;
    private Short status;
    private Short sts;
    private String memo;
    private java.util.Date createTime;
    private java.util.Date updateTime;
    private java.util.Date expireTime;
    private Long sn;

    public void setId(Long id){
	    this.id = id;
		super._setFieldValue(Orders.JField.id, id);
    }
    public Long getId(){
	    return this.id;
    }
	
    public void setCode(String code){
	    this.code = code;
		super._setFieldValue(Orders.JField.code, code);
    }
    public String getCode(){
	    return this.code;
    }
	
    public void setUserId(Long userId){
	    this.userId = userId;
		super._setFieldValue(Orders.JField.userId, userId);
    }
    public Long getUserId(){
	    return this.userId;
    }
	
    public void setBuyType(Short buyType){
	    this.buyType = buyType;
		super._setFieldValue(Orders.JField.buyType, buyType);
    }
    public Short getBuyType(){
	    return this.buyType;
    }
	
    public void setName(String name){
	    this.name = name;
		super._setFieldValue(Orders.JField.name, name);
    }
    public String getName(){
	    return this.name;
    }
	
    public void setOrigSum(Long origSum){
	    this.origSum = origSum;
		super._setFieldValue(Orders.JField.origSum, origSum);
    }
    public Long getOrigSum(){
	    return this.origSum;
    }
	
    public void setSum(Long sum){
	    this.sum = sum;
		super._setFieldValue(Orders.JField.sum, sum);
    }
    public Long getSum(){
	    return this.sum;
    }
	
    public void setExpressSum(Long expressSum){
	    this.expressSum = expressSum;
		super._setFieldValue(Orders.JField.expressSum, expressSum);
    }
    public Long getExpressSum(){
	    return this.expressSum;
    }
	
    public void setMobile(String mobile){
	    this.mobile = mobile;
		super._setFieldValue(Orders.JField.mobile, mobile);
    }
    public String getMobile(){
	    return this.mobile;
    }
	
    public void setAddress(String address){
	    this.address = address;
		super._setFieldValue(Orders.JField.address, address);
    }
    public String getAddress(){
	    return this.address;
    }
	
    public void setExpressDate(java.util.Date expressDate){
	    this.expressDate = expressDate;
		super._setFieldValue(Orders.JField.expressDate, expressDate);
    }
    public java.util.Date getExpressDate(){
	    return this.expressDate;
    }
	
    public void setExpressNo(String expressNo){
	    this.expressNo = expressNo;
		super._setFieldValue(Orders.JField.expressNo, expressNo);
    }
    public String getExpressNo(){
	    return this.expressNo;
    }
	
    public void setExpressWeight(Integer expressWeight){
	    this.expressWeight = expressWeight;
		super._setFieldValue(Orders.JField.expressWeight, expressWeight);
    }
    public Integer getExpressWeight(){
	    return this.expressWeight;
    }
	
    public void setExpressCost(Long expressCost){
	    this.expressCost = expressCost;
		super._setFieldValue(Orders.JField.expressCost, expressCost);
    }
    public Long getExpressCost(){
	    return this.expressCost;
    }
	
    public void setCost(Long cost){
	    this.cost = cost;
		super._setFieldValue(Orders.JField.cost, cost);
    }
    public Long getCost(){
	    return this.cost;
    }
	
    public void setPurchaseId(Long purchaseId){
	    this.purchaseId = purchaseId;
		super._setFieldValue(Orders.JField.purchaseId, purchaseId);
    }
    public Long getPurchaseId(){
	    return this.purchaseId;
    }
	
    public void setCouponId(Long couponId){
	    this.couponId = couponId;
		super._setFieldValue(Orders.JField.couponId, couponId);
    }
    public Long getCouponId(){
	    return this.couponId;
    }
	
    public void setPromotionId(Long promotionId){
	    this.promotionId = promotionId;
		super._setFieldValue(Orders.JField.promotionId, promotionId);
    }
    public Long getPromotionId(){
	    return this.promotionId;
    }
	
    public void setGroupbuyId(Long groupbuyId){
	    this.groupbuyId = groupbuyId;
		super._setFieldValue(Orders.JField.groupbuyId, groupbuyId);
    }
    public Long getGroupbuyId(){
	    return this.groupbuyId;
    }
	
    public void setPayChannel(Short payChannel){
	    this.payChannel = payChannel;
		super._setFieldValue(Orders.JField.payChannel, payChannel);
    }
    public Short getPayChannel(){
	    return this.payChannel;
    }
	
    public void setPayAccount(String payAccount){
	    this.payAccount = payAccount;
		super._setFieldValue(Orders.JField.payAccount, payAccount);
    }
    public String getPayAccount(){
	    return this.payAccount;
    }
	
    public void setTransactionCode(String transactionCode){
	    this.transactionCode = transactionCode;
		super._setFieldValue(Orders.JField.transactionCode, transactionCode);
    }
    public String getTransactionCode(){
	    return this.transactionCode;
    }
	
    public void setChannel(Short channel){
	    this.channel = channel;
		super._setFieldValue(Orders.JField.channel, channel);
    }
    public Short getChannel(){
	    return this.channel;
    }
	
    public void setStatus(Short status){
	    this.status = status;
		super._setFieldValue(Orders.JField.status, status);
    }
    public Short getStatus(){
	    return this.status;
    }
	
    public void setSts(Short sts){
	    this.sts = sts;
		super._setFieldValue(Orders.JField.sts, sts);
    }
    public Short getSts(){
	    return this.sts;
    }
	
    public void setMemo(String memo){
	    this.memo = memo;
		super._setFieldValue(Orders.JField.memo, memo);
    }
    public String getMemo(){
	    return this.memo;
    }
	
    public void setCreateTime(java.util.Date createTime){
	    this.createTime = createTime;
		super._setFieldValue(Orders.JField.createTime, createTime);
    }
    public java.util.Date getCreateTime(){
	    return this.createTime;
    }
	
    public void setUpdateTime(java.util.Date updateTime){
	    this.updateTime = updateTime;
		super._setFieldValue(Orders.JField.updateTime, updateTime);
    }
    public java.util.Date getUpdateTime(){
	    return this.updateTime;
    }
	
    public void setExpireTime(java.util.Date expireTime){
	    this.expireTime = expireTime;
		super._setFieldValue(Orders.JField.expireTime, expireTime);
    }
    public java.util.Date getExpireTime(){
	    return this.expireTime;
    }
	
    public void setSn(Long sn){
	    this.sn = sn;
		super._setFieldValue(Orders.JField.sn, sn);
    }
    public Long getSn(){
	    return this.sn;
    }
	


public enum JField implements BaseJField{
        id("ID","BIGINT",19,Long.class,false),
        code("CODE","VARCHAR",32,String.class,false),
        userId("USER_ID","BIGINT",19,Long.class,false),
        buyType("BUY_TYPE","TINYINT",3,Short.class,false),
        name("NAME","VARCHAR",64,String.class,true),
        origSum("ORIG_SUM","BIGINT",19,Long.class,true),
        sum("SUM","BIGINT",19,Long.class,true),
        expressSum("EXPRESS_SUM","BIGINT",19,Long.class,true),
        mobile("MOBILE","VARCHAR",32,String.class,true),
        address("ADDRESS","VARCHAR",512,String.class,true),
        expressDate("EXPRESS_DATE","DATE",10,java.util.Date.class,true),
        expressNo("EXPRESS_NO","VARCHAR",50,String.class,true),
        expressWeight("EXPRESS_WEIGHT","INT",10,Integer.class,true),
        expressCost("EXPRESS_COST","BIGINT",19,Long.class,true),
        cost("COST","BIGINT",19,Long.class,true),
        purchaseId("PURCHASE_ID","BIGINT",19,Long.class,true),
        couponId("COUPON_ID","BIGINT",19,Long.class,true),
        promotionId("PROMOTION_ID","BIGINT",19,Long.class,true),
        groupbuyId("GROUPBUY_ID","BIGINT",19,Long.class,true),
        payChannel("PAY_CHANNEL","TINYINT",3,Short.class,true),
        payAccount("PAY_ACCOUNT","VARCHAR",128,String.class,true),
        transactionCode("TRANSACTION_CODE","VARCHAR",128,String.class,true),
        channel("CHANNEL","TINYINT",3,Short.class,true),
        status("STATUS","TINYINT",3,Short.class,false),
        sts("STS","TINYINT",3,Short.class,true),
        memo("MEMO","VARCHAR",1000,String.class,true),
        createTime("CREATE_TIME","DATETIME",19,java.util.Date.class,false),
        updateTime("UPDATE_TIME","DATETIME",19,java.util.Date.class,false),
        expireTime("EXPIRE_TIME","DATETIME",19,java.util.Date.class,true),
        sn("SN","BIGINT",19,Long.class,false);
        
        private String colName;
        private String colTypeName;
        private Integer length;
		private Class<?> javaType;
		private boolean nullable;
		
        private JField(String colName,String colTypeName,Integer length,Class<?> javaType,boolean nullable){
            this.colName = colName;
            this.colTypeName = colTypeName;
            this.length = length;
			this.javaType = javaType;
			this.nullable = nullable;
    	}
            
    	@Override
    	public String getName()
    	{
            return this.name();
    	}
    
        @Override
        public String getColName()
        {
            return this.colName;
        }
        @Override
        public String getColTypeName()
        {
            return this.colTypeName;
        }
        @Override
        public Integer getLength()
        {
            return length;
        }
        @Override
        public boolean getNullable()
        {
            return this.nullable;
        }
		@Override
        public String getTableName()
        {
            return "ORDERS";
        }
		@Override
        public Class<?> getJavaType()
        {
            return this.javaType;
        }

        @Override
        public Class<? extends DBEntity> getEntityClass()
        {
            return com.xiesange.gen.dbentity.orders.Orders.class;
        }
    }
	

}