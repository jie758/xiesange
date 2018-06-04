package com.xiesange.orm.sql;


/**
 * 用于构建1=2这种阻拦式查询的查询条件对象。
 * 有的场景下需要有一个恒不成立的查询条件，就用本对象。
 * 本对象不会构建其它sql语句，只会构建" and 1=2"
 * @author wuyujie Oct 21, 2014 7:12:20 PM
 *
 */
public class BlockDBCondition extends DBCondition{
}
