package cn.geofound.common.utils.redis.base;

import java.util.Set;

import org.nutz.ioc.loader.annotation.IocBean;



import redis.clients.jedis.Jedis;

@IocBean
public class JedisSortSet extends JedisBase{

	/** 
     * 向集合中增加一条记录,如果这个值已存在，这个值对应的权重将被置为新的权重 
     * @param String  key 
     * @param double score 权重 
     * @param String  member 要加入的值， 
     * @return 状态码 1成功，0已存在member的值 
     * */  
    public long zadd(String key, double score, String member) {  
        Jedis jedis = getResource();  
        long s = jedis.zadd(key, score, member);  
        returnResource(jedis);  
        return s;  
    }  

    /*public long zadd(String key, Map<Double, String> scoreMembers) { 
        Jedis jedis = getResource(); 
        long s = jedis.zadd(key, scoreMembers); 
        returnResource(jedis); 
        return s; 
    }*/  

    /** 
     * 获取集合中元素的数量 
     * @param String  key 
     * @return 如果返回0则集合不存在 
     * */  
    public long zcard(String key) {  
        Jedis sjedis = getResource();  
        long len = sjedis.zcard(key);  
        returnResource(sjedis);  
        return len;  
    }  

    /** 
     * 获取指定权重区间内集合的数量 
     * @param String key 
     * @param double min 最小排序位置 
     * @param double max 最大排序位置 
     * */  
    public long zcount(String key, double min, double max) {  
        Jedis sjedis = getResource();  
        long len = sjedis.zcount(key, min, max);  
        returnResource(sjedis);  
        return len;  
    }  

    /** 
     * 获得set的长度 
     *  
     * @param key 
     * @return 
     */  
    public long zlength(String key) {  
        long len = 0;  
        Set<String> set = zrange(key, 0, -1);  
        len = set.size();  
        return len;  
    }  

    /** 
     * 权重增加给定值，如果给定的member已存在 
     * @param String  key 
     * @param double score 要增的权重 
     * @param String  member 要插入的值 
     * @return 增后的权重 
     * */  
    public double zincrby(String key, double score, String member) {  
        Jedis jedis = getResource();  
        double s = jedis.zincrby(key, score, member);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 返回指定位置的集合元素,0为第一个元素，-1为最后一个元素 
     * @param String key 
     * @param int start 开始位置(包含) 
     * @param int end 结束位置(包含) 
     * @return Set<String> 
     * */  
    public Set<String> zrange(String key, long start, long end) {  
        Jedis sjedis = getResource();   
        Set<String> set = sjedis.zrange(key, start, end);  
        returnResource(sjedis);  
        return set;  
    }  

    /** 
     * 返回指定权重区间的元素集合 
     * @param String key 
     * @param double min 上限权重 
     * @param double max 下限权重 
     * @return Set<String> 
     * */  
    public Set<String> zrangeByScore(String key, double min, double max) {  
        Jedis sjedis = getResource();   
        Set<String> set = sjedis.zrangeByScore(key, min, max);  
        returnResource(sjedis);  
        return set;  
    }  

    /** 
     * 【注意】
     * 如果member不存在 会出现空指针异常</br>
     * </br
     * 获取指定值在集合中的位置，集合排序从低到高 
     * @see zrevrank 
     * @param String key 
     * @param String member 
     * @return long 位置 
     * */  
    public long zrank(String key, String member) {
    	try{
		   	Jedis sjedis = getResource();   
	        long index = sjedis.zrank(key, member);  
	        returnResource(sjedis);  
	        return index;  
    	}catch (Exception e) {
			
		}
       return -1;//出现nil的时候
    }  

    /** 
     * 获取指定值在集合中的位置，集合排序从高到低 
     * @see zrank 
     * @param String key 
     * @param String member 
     * @return long 位置 
     * */  
    public long zrevrank(String key, String member) {  
        Jedis sjedis = getResource();   
        long index = sjedis.zrevrank(key, member);  
        returnResource(sjedis);  
        return index;  
    }  

    /** 
     * 从集合中删除成员 
     * @param String key 
     * @param String member  
     * @return 返回1成功 
     * */  
    public long zrem(String key, String member) {  
        Jedis jedis = getResource();
        long s = jedis.zrem(key, member);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 删除 
     * @param key 
     * @return 
     */  
    public long zrem(String key) {  
        Jedis jedis = getResource();  
        long s = jedis.del(key);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 删除给定位置区间的元素 
     * @param String  key 
     * @param int start 开始区间，从0开始(包含) 
     * @param int end 结束区间,-1为最后一个元素(包含) 
     * @return 删除的数量 
     * */  
    public long zremrangeByRank(String key, int start, int end) {  
        Jedis jedis = getResource();  
        long s = jedis.zremrangeByRank(key, start, end);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 删除给定权重区间的元素 
     * @param String key 
     * @param double min 下限权重(包含) 
     * @param double max 上限权重(包含) 
     * @return 删除的数量 
     * */  
    public long zremrangeByScore(String key, double min, double max) {  
        Jedis jedis = getResource();  
        long s = jedis.zremrangeByScore(key, min, max);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 获取给定区间的元素，原始按照权重由高到低排序 
     * @param String  key 
     * @param int start 
     * @param int end 
     * @return Set<String> 
     * */  
    public Set<String> zrevrange(String key, long start, long end) {  
        Jedis sjedis = getResource();   
        Set<String> set = sjedis.zrevrange(key, start, end);  
        returnResource(sjedis);  
        return set;  
    }  

    /** 
     * 获取给定值在集合中的权重 
     * @param String  key 
     * @param memeber 
     * @return double 权重 
     * */  
    public double zscore(String key, String memebr) {  
        Jedis sjedis = getResource();   
        Double score = sjedis.zscore(key, memebr);  
        returnResource(sjedis);  
        if (score != null)  
            return score;  
        return 0;  
    }  
    
    
    
}
