package cn.geofound.common.utils.redis.pub;

import redis.clients.jedis.JedisPubSub;


/**
 * 
 * http://blog.csdn.net/eguid_1/article/details/52600755
 * 
 * redis订阅者
 * 处理监听到的消息主体类实现
 * @Title 
 * @author zjlwm
 * @date 2016-12-23 下午10:38:54
 *
 */
public class RedisMQHandler extends JedisPubSub{

	@Override
	public void onMessage(String channel, String message) {
		 System.out.println("接收到一条推流消息，准备推流：");  
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
