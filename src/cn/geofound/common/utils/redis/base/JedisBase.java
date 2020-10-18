package cn.geofound.common.utils.redis.base;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

@IocBean
public class JedisBase {

	private  Logger logger = LoggerFactory.getLogger(JedisBase.class);
	
	/**
	 * app reidis 缓存连接池
	 */
	@Inject
	private JedisPool appReadpool;
	

	
	
	/**
	 * 默认缓存时间
	 */
    private final int expire = 60000; 
	
	/** 
     * 设置过期时间 
     * @author ruan 2013-4-11 
     * @param key 
     * @param seconds 
     */  
    public void expire(String key, int seconds) {  
        if (seconds <= 0) {   
            return;  
        }
        Jedis jedis =null;
        try{
        	 jedis = getResource();
        	 jedis.expire(key, seconds);  
             returnResource(jedis);  
        }catch (Exception e) {
        	logger.warn("get {} = {}", key, e);
		}
       
    }  
    
    
    
    /** 
     * 设置默认过期时间 
     * @author ruan 2013-4-11 
     * @param key 
     */  
    public void expire(String key) {  
        expire(key, expire);  
    } 
    
    
    
    
    
    
	
	
	
	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public  Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = appReadpool.getResource();
		} catch (JedisException e) {
			logger.error("getResource.", e);
//			returnBrokenResource(jedis);
			returnResource(jedis);
			throw e;
		}
		return jedis;
	}

//	/**
//	 * 归还资源
//	 * 
//	 * @param jedis
//	 * @param isBroken
//	 */
//	public  void returnBrokenResource(Jedis jedis) {
//		if (jedis != null) {
//			appReadpool.returnBrokenResource(jedis);
//		}
//	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public  void returnResource(Jedis jedis) {
		if (jedis != null) {
			appReadpool.returnResource(jedis);
		}
	}
	
	
}
