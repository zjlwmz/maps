package cn.geofound.common.utils.redis.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.loader.annotation.IocBean;

import redis.clients.jedis.Jedis;

/*
 * 常用命令：  
	hget,hset,hgetall 等。  
	应用场景：  
	我们简单举个实例来描述下Hash的应用场景，比如我们要存储一个用户信息对象数据，包含以下信息：  
           用户ID，为查找的key，  
           存储的value用户对象包含姓名name，年龄age，生日birthday 等信息，  
   	如果用普通的key/value结构来存储，主要有以下2种存储方式：  
           第一种方式将用户ID作为查找key,把其他信息封装成一个对象以序列化的方式存储，  
           如：set u001 "李三,18,20010101"  
           这种方式的缺点是，增加了序列化/反序列化的开销，并且在需要修改其中一项信息时，需要把整个对象取回，
           并且修改操作需要对并发进行保护，引入CAS等复杂问题。  
       	第二种方法是这个用户信息对象有多少成员就存成多少个key-value对儿，
   	   用用户ID+对应属性的名称作为唯一标识来取得对应属性的值，  
           如：mset user:001:name "李三 "user:001:age18 user:001:birthday "20010101"  
           虽然省去了序列化开销和并发问题，但是用户ID为重复存储，如果存在大量这样的数据，内存浪费还是非常可观的。  
          那么Redis提供的Hash很好的解决了这个问题，Redis的Hash实际是内部存储的Value为一个HashMap，  
        并提供了直接存取这个Map成员的接口，  
        
        如：hmset user:001 name "李三" age 18 birthday "20010101"     
            也就是说，Key仍然是用户ID,value是一个Map，这个Map的key是成员的属性名，value是属性值，  
            这样对数据的修改和存取都可以直接通过其内部Map的Key(Redis里称内部Map的key为field), 也就是通过   
    key(用户ID) + field(属性标签) 操作对应属性数据了，既不需要重复存储数据，
            也不会带来序列化和并发修改控制的问题。
            很好的解决了问题。  
  
          这里同时需要注意，Redis提供了接口(hgetall)可以直接取到全部的属性数据,但是如果内部Map的成员很多，
          那么涉及到遍历整个内部Map的操作，由于Redis单线程模型的缘故，这个遍历操作可能会比较耗时，
          而另其它客户端的请求完全不响应，这点需要格外注意。  
          
	  实现方式：  
	    上面已经说到Redis Hash对应Value内部实际就是一个HashMap，实际这里会有2种不同实现，
	    这个Hash的成员比较少时Redis为了节省内存会采用类似一维数组的方式来紧凑存储，而不会采用真正的HashMap结构，
	    对应的value redisObject的encoding为zipmap,当成员数量增大时会自动转成真正的HashMap,此时encoding为ht。
 */
/**
 * http://blog.csdn.net/gaogaoshan/article/details/41039581/
 * @Title 
 * @author zjlwm
 * @date 2016-12-22 下午3:13:56
 *
 */
@IocBean
public class JedisHash extends JedisBase{

	
	/** 
     * 从hash中删除指定的存储 
     * @param String key 
     * @param String  fieid 存储的名字 
     * @return 状态码，1成功，0失败 
     * */  
    public long hdel(String key, String fieid) {  
        Jedis jedis = getResource();  
        long s = jedis.hdel(key, fieid);  
        returnResource(jedis);  
        return s;  
    }  

    public long hdel(String key) {  
        Jedis jedis = getResource();  
        long s = jedis.del(key);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 测试hash中指定的存储是否存在 
     * @param String key 
     * @param String  fieid 存储的名字 
     * @return 1存在，0不存在 
     * */  
    public boolean hexists(String key, String fieid) {  
        Jedis sjedis = getResource();   
        boolean s = sjedis.hexists(key, fieid);  
        returnResource(sjedis);  
        return s;  
    }  

    /** 
     * 返回hash中指定存储位置的值 
     *  
     * @param String key 
     * @param String fieid 存储的名字 
     * @return 存储对应的值 
     * */  
    public String hget(String key, String fieid) {  
        Jedis sjedis = getResource();   
        String s = sjedis.hget(key, fieid);  
        returnResource(sjedis);  
        return s;  
    }  

    public byte[] hget(byte[] key, byte[] fieid) {  
        Jedis sjedis = getResource();   
        byte[] s = sjedis.hget(key, fieid);  
        returnResource(sjedis);  
        return s;  
    }  

    /** 
     * 以Map的形式返回hash中的存储和值 
     * @param String    key 
     * @return Map<Strinig,String> 
     * */  
    public Map<String, String> hgetAll(String key) {  
        Jedis sjedis = getResource();   
        Map<String, String> map = sjedis.hgetAll(key);  
        returnResource(sjedis);  
        return map;  
    }  

    /** 
     * 添加一个对应关系 
     * @param String  key 
     * @param String fieid 
     * @param String value 
     * @return 状态码 1成功，0失败，fieid已存在将更新，也返回0 
     * **/  
    public long hset(String key, String fieid, String value) {  
        Jedis jedis = getResource();  
        long s = jedis.hset(key, fieid, value);  
        returnResource(jedis);  
        return s;  
    }  

    public long hset(String key, String fieid, byte[] value) {  
        Jedis jedis = getResource();  
        long s = jedis.hset(key.getBytes(), fieid.getBytes(), value);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 添加对应关系，只有在fieid不存在时才执行 
     * @param String key 
     * @param String fieid 
     * @param String value 
     * @return 状态码 1成功，0失败fieid已存 
     * **/  
    public long hsetnx(String key, String fieid, String value) {  
        Jedis jedis = getResource();  
        long s = jedis.hsetnx(key, fieid, value);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 获取hash中value的集合 
     *  
     * @param String 
     *            key 
     * @return List<String> 
     * */  
    public List<String> hvals(String key) {  
        Jedis sjedis = getResource();   
        List<String> list = sjedis.hvals(key);  
        returnResource(sjedis);  
        return list;  
    }  

    /** 
     * 在指定的存储位置加上指定的数字，存储位置的值必须可转为数字类型 
     * @param String  key 
     * @param String  fieid 存储位置 
     * @param String long value 要增加的值,可以是负数 
     * @return 增加指定数字后，存储位置的值 
     * */  
    public long hincrby(String key, String fieid, long value) {  
        Jedis jedis = getResource();  
        long s = jedis.hincrBy(key, fieid, value);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 返回指定hash中的所有存储名字,类似Map中的keySet方法 
     * @param String key 
     * @return Set<String> 存储名称的集合 
     * */  
    public Set<String> hkeys(String key) {  
        Jedis sjedis = getResource();   
        Set<String> set = sjedis.hkeys(key);  
        returnResource(sjedis);  
        return set;  
    }  

    /** 
     * 获取hash中存储的个数，类似Map中size方法 
     * @param String  key 
     * @return long 存储的个数 
     * */  
    public long hlen(String key) {  
        Jedis sjedis = getResource();    
        long len = sjedis.hlen(key);  
        returnResource(sjedis);  
        return len;  
    }  

    /** 
     * 根据多个key，获取对应的value，返回List,如果指定的key不存在,List对应位置为null 
     * @param String  key 
     * @param String ... fieids 存储位置 
     * @return List<String> 
     * */  
    public List<String> hmget(String key, String... fieids) {  
        Jedis sjedis = getResource();   
        List<String> list = sjedis.hmget(key, fieids);  
        returnResource(sjedis);  
        return list;  
    }  

    public List<byte[]> hmget(byte[] key, byte[]... fieids) {  
        Jedis sjedis = getResource();    
        List<byte[]> list = sjedis.hmget(key, fieids);  
        returnResource(sjedis);  
        return list;  
    }  

    /** 
     * 添加对应关系，如果对应关系已存在，则覆盖 
     * @param Strin   key 
     * @param Map <String,String> 对应关系 
     * @return 状态，成功返回OK 
     * */  
    public String hmset(String key, Map<String, String> map) {  
        Jedis jedis = getResource();  
        String s = jedis.hmset(key, map);  
        returnResource(jedis);  
        return s;  
    }  

    /** 
     * 添加对应关系，如果对应关系已存在，则覆盖 
     * @param Strin key 
     * @param Map <String,String> 对应关系 
     * @return 状态，成功返回OK 
     * */  
    public String hmset(byte[] key, Map<byte[], byte[]> map) {  
        Jedis jedis = getResource();  
        String s = jedis.hmset(key, map);  
        returnResource(jedis);  
        return s;  
    }
    
}
