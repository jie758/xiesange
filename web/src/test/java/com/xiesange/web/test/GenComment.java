package com.xiesange.web.test;

import java.util.Date;
import java.util.List;

import com.xiesange.core.util.ClassUtil;
import com.xiesange.core.util.DateUtil;
import com.xiesange.gen.dbentity.orders.OrdersComment;
import com.xiesange.orm.DBHelper;
import com.xiesange.web.test.frame.TestCmp;

public class GenComment {
	public static final String PATH_CONFIG = "/config.xml";
	public static final String PATH_CACHE = "/cache.xml";

	public static void main(String[] args) throws Exception {
		TestCmp.init114();
		//TestCmp.initLocalhost();
		List<Comment> cmtList = ClassUtil.newList();
		
		cmtList.add(new Comment(5,"2016-08-11","宝贝已收到，有点小惊喜，小海鲜能够做到给人惊喜，所下的功夫从细节处体现出来，早上刮胡子拿条鱼照一下真能看得见，哈哈！能吃上你家的小海鲜也是有缘，五星好评支持！"));
		cmtList.add(new Comment(5,"2016-08-12","妈妈说很新鲜，那是真的新鲜~"));
		cmtList.add(new Comment(5,"2016-08-15","第n次买了，鱼很新鲜，除了价格有波动，品质还是有保证的。"));
		cmtList.add(new Comment(5,"2016-08-17","小白鲳长大了"));
		cmtList.add(new Comment(5,"2016-08-17","有比上次的大了一点～小的可以煎～煮汤蒸，我觉的小的很好我很喜欢，因为便宜哈哈哈"));
		cmtList.add(new Comment(5,"2016-08-18","最喜欢这个小鲳鱼非常新鲜，比超市的好多了，相当好吃。"));
		cmtList.add(new Comment(5,"2016-08-20","虽然小，但味道真的很鲜美。"));
		cmtList.add(new Comment(5,"2016-09-01","鲳鱼很好很新鲜，很强大，以后就在这买了"));
		cmtList.add(new Comment(3,"2016-09-01","新鲜一般般，下次不会买了"));
		cmtList.add(new Comment(5,"2016-09-04","新鲜，但是不大。"));
		cmtList.add(new Comment(5,"2016-09-04","只记得吃了，忘了确认-_-#很新鲜"));
		
		cmtList.add(new Comment(5,"2016-09-07","新鲜，包装实在！"));
		cmtList.add(new Comment(5,"2016-09-09","好，非常鲜，就是有点小贵。。。。"));
		cmtList.add(new Comment(5,"2016-09-10","特别新鲜的海鱼"));
		cmtList.add(new Comment(5,"2016-09-15","鱼虽然小，可是真新鲜！最好的品种，鱼肉细嫩鲜美！"));
		cmtList.add(new Comment(5,"2016-09-15","我不能说，小的好吃，不然下次小的就轮不到我了"));
		cmtList.add(new Comment(5,"2016-09-15","鲳鱼很大很新鲜，顺丰也很快，收到货冰袋都还没化，夏天还能吃到这么新鲜的大鲳鱼，感觉买的很值！"));
		cmtList.add(new Comment(5,"2016-09-18","鲳鱼因为中秋台风的影响是节后发出的，2斤先到，2斤后到，儿子喜欢吃。我囤着等他回家清蒸。那个鲜嫩没话说的。"));
		cmtList.add(new Comment(5,"2016-09-20","收到货秤了一下，去除箩筐后有550克，量很足，鱼身光泽没有黄点说明这是新鲜鱼，比我上次在另一家天猫店买的强多了，给好评！吃完还会来！！！"));
		cmtList.add(new Comment(4,"2016-09-21","这种鱼肉嫩而且块头大，这次叫老板给我发了两斤一条的送亲戚，亲戚高兴的不得了，下次还来"));		
		cmtList.add(new Comment(5,"2016-09-22","买了两条送人，客人反馈很好，说下次再给他来点~！"));		
		cmtList.add(new Comment(5,"2016-09-25","刚收到的货很新鲜冰块都还没化"));		
		cmtList.add(new Comment(5,"2016-09-27","再次回购了，让我百吃不厌的一款鱼"));		
		cmtList.add(new Comment(5,"2016-09-27","很正宗，很新鲜，口感细腻柔软，我小孩也吃好多，这个营养很丰富，又新鲜又干净的，可以放心给小孩子吃的。东西太好了，一定要如实评价出来，让更多的人都可以买到好的东西"));		
		cmtList.add(new Comment(5,"2016-09-28","老公，看到后，哈喇子~都要流出来了！！！买海产品以后就认定你家了"));		
		cmtList.add(new Comment(5,"2016-09-29","有大有小，大部分都挺新鲜，还没吃，有十条大小不等的鱼。"));
		
		
		
		
		cmtList.add(new Comment(5,"2016-10-01","这是老板推荐的，以前没吃过。但是鱼个头挺大的，这鱼煮汤还挺好喝的。中午不吃，老妈叫我煎了。手艺不行。晚上再吃啊！"));		
		cmtList.add(new Comment(5,"2016-10-03","小鲳鱼啊，小鲳鱼你怎么这么新鲜呢，我决定抛弃小黄花了，哈哈，，"));		
		cmtList.add(new Comment(5,"2016-10-06","鱼很新鲜，宝宝很爱吃，以后还会回购。"));		
		cmtList.add(new Comment(5,"2016-10-08","鱼很好，就是在国庆期间物流有点慢，都化冻了。"));		
		cmtList.add(new Comment(5,"2016-10-11","收到时还没化冰，袋子中看到一点血水，等吃的时候再评价新鲜程度吧。"));		
		cmtList.add(new Comment(5,"2016-10-11","非常新鲜，天气凉了买海鲜，不怕坏"));		
		cmtList.add(new Comment(5,"2016-10-13","这个第一次买，还没吃，看着很新鲜。"));		
		cmtList.add(new Comment(5,"2016-10-13","看着挺好的，想蒸着吃，又怕不会做把鱼浪费了"));		
		cmtList.add(new Comment(5,"2016-10-14","天冷就是好，还没有化冻就已经送到了。"));		
		cmtList.add(new Comment(4,"2016-10-15","嗯，不错，要是价格能更优惠点就更好了"));		
		cmtList.add(new Comment(5,"2016-10-18","一包六条，还行。尝尝告诉大家。"));		
		cmtList.add(new Comment(5,"2016-10-19","这个好吃，嫩啊"));		
		
		
		//List<TripComment> comList = DBHelper.getDao().queryAll(TripComment.class);
		
		
		long prodId = 10L;
		long userIdStart = 10000946L;
		for(int i=0;i<cmtList.size();i++){
			Comment c = cmtList.get(i);
			int grade = c.grade;
			Date date = DateUtil.str2Date(c.date, DateUtil.DATE_FORMAT_EN_B_YYYYMMDD);
			
			OrdersComment cmtEntity = new OrdersComment();
			cmtEntity.setProductId(prodId);
			cmtEntity.setGrade(grade);
			cmtEntity.setComment(c.comment);
			cmtEntity.setSn(-1L);
			cmtEntity.setCreateTime(date);
			cmtEntity.setOrderId(-1L);
			cmtEntity.setUserId(userIdStart--);
			cmtEntity.setStatus((short)1);
			
			DBHelper.getDao().insert(cmtEntity);
		}
	}
	
	private static class Comment{
		private int grade;
		private String comment;
		private String date;
		
		public Comment(int grade,String date,String comment){
			this.grade = grade;
			this.date = date;
			this.comment = comment;
		}
		
		
	}
}
