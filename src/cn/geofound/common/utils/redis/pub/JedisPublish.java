package cn.geofound.common.utils.redis.pub;

import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.common.utils.redis.base.JedisBase;


import redis.clients.jedis.Jedis;


/**
 * 
 * @Title 消息发布工具类型
 * @author zjlwm
 * @date 2016-12-23 下午10:25:38
 * 
 */
@IocBean
public class JedisPublish extends JedisBase {

	/**
	 * 发布一个消息
	 * 
	 * @param channel
	 * @param message
	 */
	public void publishMsg(String channel, String message) {
		Jedis jedis = getResource();
		try {
			jedis = getResource();
			jedis.publish(channel, message);
		} catch (Exception e) {
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 发布一个消息
	 * 
	 * @param channel
	 * @param message
	 */
	public void publishMsg(byte[] channel, byte[] message) {
		Jedis jedis = getResource();
		try {
			jedis = getResource();
			jedis.publish(channel, message);
		} catch (Exception e) {
		} finally {
			returnResource(jedis);
		}
	}

}
