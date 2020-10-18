package cn.geofound.common.utils.redis.sub;

import redis.clients.jedis.JedisPubSub;

/**
 * @Title redis订阅者
 * @author zjlwm
 * @date 2016-12-23 下午10:21:52
 *
 */
public class JedisSubscribe extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub
		
	}

}
