package com.xiesange.orm.sql;

public enum DBOperator
{
    EQUALS("=",true),
    NOT_EQUALS("!=",true),
    GREAT(">",true),
    LESS("<",true),
    GREAT_EQUALS(">=",true),
    LESS_EQUALS("<=",true),
    IN("IN",false),
    NOT_IN("NOT IN",false),
    IS("IS",true),
    IS_NOT("IS NOT",true),
    LIKE("LIKE",true),
    NOT_LIKE("NOT LIKE",true),
    IS_NULL("IS NULL",true),
    IS_NOT_NULL("IS NOT NULL",true),;
    
    private String expression;
    private boolean isSingleValue;
    
    
    //标志该关系运算符是否是单个值，比如=,!=,>,<等右边都只会有一个值；而in,not in右边会有多个值
    
    private DBOperator(String expression,boolean singleValue){
        this.expression = expression;
        this.isSingleValue = singleValue;
    }
    public String getExpression() {
        return expression;
    }
    public boolean isSingleValue(){
        return isSingleValue;
    }
    
    /**
     * 根据传入的字符串形式的运算符，得到对应的DBOperator对象。
     * 例如，getInstance(">"),返回的是DBOperator.GREAT对象；getInstance("!="),返回的是DBOperator.NOT_EQUALS对象；
     * @param operatorStr
     * @return,对应的DBOperator对象
     * @throws Exception，如果传入的运算符不合法，匹配不到对应的Operator对象，则会报错
     */
    public static DBOperator getInstance(String expression) throws Exception{
    	DBOperator[] operators = DBOperator.values();
    	for(DBOperator operator : operators){
    		if(operator.getExpression().equalsIgnoreCase(expression))
    			return operator;
    	}
    	
    	throw new Exception("Invalid sql operator : "+expression);
    }
    
}
