package cn.geofound.common.utils.redis.base;

import java.util.List;
import java.util.Set;

import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;


@IocBean
public class JedisKeys extends JedisBase{
	
	private  Logger logger = LoggerFactory.getLogger(JedisKeys.class);
	
	 /** 
     * 清空所有key 
     */  
    public String flushAll() {
        Jedis jedis = null;
        String result=null;
        try{
        	jedis = getResource();
        	result = jedis.flushAll();  
        	returnResource(jedis);
        }catch (Exception e) {
        	logger.warn("get {} = {}", e);
		}
        return result;  
    }  

    
    
    /** 
     * 更改key 
     * @param String oldkey 
     * @param String  newkey 
     * @return 状态码 
     * */  
	public String changeKeyName(String oldkey, String newkey) {
		return rename(SafeEncoder.encode(oldkey), SafeEncoder.encode(newkey));
	} 

    /** 
     * 更改key,仅当新key不存在时才执行 
     * @param String oldkey 
     * @param String newkey  
     * @return 状态码 
     * */
    public long renamenx(String oldkey, String newkey) {  
    	 Jedis jedis = null;
         Long status=null;
         try{
         	jedis = getResource();
         	status = jedis.renamenx(oldkey, newkey);   
         	returnResource(jedis);
         }catch (Exception e) {
         	logger.warn("get {} = {}", e);
 		}
        return status; 
    }  

    /** 
     * 更改key 
     * @param String oldkey 
     * @param String newkey 
     * @return 状态码 
     * */  
    public String rename(byte[] oldkey, byte[] newkey) {  
        Jedis jedis = getResource();  
        String status = jedis.rename(oldkey, newkey);  
        returnResource(jedis);  
        return status;  
    }  

    /** 
     * 设置key的过期时间，以秒为单位 
     * @param String key 
     * @param 时间,已秒为单位 
     * @return 影响的记录数 
     * */  
    public long expired(String key, int seconds) {  
        Jedis jedis = getResource();  
        long count = jedis.expire(key, seconds);  
        returnResource(jedis);  
        return count;  
    }  

    /** 
     * 设置key的过期时间,它是距历元（即格林威治标准时间 1970 年 1 月 1 日的 00:00:00，格里高利历）的偏移量。 
     * @param String key 
     * @param 时间,已秒为单位 
     * @return 影响的记录数 
     * */  
    public long expireAt(String key, long timestamp) {  
        Jedis jedis = getResource();  
        long count = jedis.expireAt(key, timestamp);  
        returnResource(jedis);  
        return count;  
    }  

    /** 
     * 查询key的过期时间 
     * @param String key 
     * @return 以秒为单位的时间表示 
     * */  
    public long ttl(String key) {  
        //ShardedJedis sjedis = getShardedJedis();  
        Jedis sjedis=getResource();   
        long len = sjedis.ttl(key);  
        returnResource(sjedis);  
        return len;  
    }  

    /** 
     * 取消对key过期时间的设置 
     * @param key 
     * @return 影响的记录数 
     * */  
    public long persist(String key) {  
        Jedis jedis = getResource();  
        long count = jedis.persist(key);  
        returnResource(jedis);  
        return count;  
    }  

    /** 
     * 删除keys对应的记录,可以是多个key 
     * @param String  ... keys 
     * @return 删除的记录数 
     * */  
    public long del(String... keys) {  
        Jedis jedis = getResource();  
        long count = jedis.del(keys);  
        returnResource(jedis);  
        return count;  
    }  

    /** 
     * 删除keys对应的记录,可以是多个key 
     * @param String .. keys 
     * @return 删除的记录数 
     * */  
    public long del(byte[]... keys) {  
        Jedis jedis = getResource();  
        long count = jedis.del(keys);  
        returnResource(jedis);  
        return count;  
    }  

    /** 
     * 判断key是否存在 
     * @param String key 
     * @return boolean 
     * */  
    public boolean exists(String key) {  
        //ShardedJedis sjedis = getShardedJedis();  
        Jedis sjedis=getResource();    
        boolean exis = sjedis.exists(key);  
        returnResource(sjedis);  
        return exis;  
    }  

    /** 
     * 对List,Set,SortSet进行排序,如果集合数据较大应避免使用这个方法 
     * @param String key 
     * @return List<String> 集合的全部记录 
     * **/  
    public List<String> sort(String key) {  
        //ShardedJedis sjedis = getShardedJedis();  
        Jedis sjedis=getResource();    
        List<String> list = sjedis.sort(key);  
        returnResource(sjedis);  
        return list;  
    }  

    /** 
     * 对List,Set,SortSet进行排序或limit 
     * @param String key 
     * @param SortingParams parame 定义排序类型或limit的起止位置. 
     * @return List<String> 全部或部分记录 
     * **/  
    public List<String> sort(String key, SortingParams parame) {  
        Jedis sjedis=getResource();   
        List<String> list = sjedis.sort(key, parame);  
        returnResource(sjedis);  
        return list;  
    }  

    /** 
     * 返回指定key存储的类型 
     * @param String key 
     * @return String string|list|set|zset|hash 
     * **/  
    public String type(String key) {  
        Jedis sjedis=getResource();    
        String type = sjedis.type(key);   
        returnResource(sjedis);  
        return type;  
    }  

    /** 
     * 查找所有匹配给定的模式的键 
     * @param String  key的表达式,*表示多个，？表示一个 
     * */  
    public Set<String> keys(String pattern) {  
        Jedis jedis = getResource();  
        Set<String> set = jedis.keys(pattern);  
        returnResource(jedis);  
        return set;  
    }  
    
    
}
