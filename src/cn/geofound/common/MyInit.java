package cn.geofound.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import cn.geofound.common.utils.redis.JedisInitService;

public class MyInit implements Setup{
	
	
	private  ScheduledExecutorService scheduExec;
	
	private static final Log log = Logs.get();
	
	public void destroy(NutConfig config) {
		
	}
	
	public void init(NutConfig config) {
		try{
			log.info("初始化任务开始");
			this.scheduExec =  Executors.newScheduledThreadPool(2);
			PropertiesProxy propertiesProxy =config.getIoc().get(PropertiesProxy.class, "custom");
			//初始化延迟
			int initialDelay=Integer.parseInt(propertiesProxy.get("initialDelay"));
			//间隔时间
			int period=Integer.parseInt(propertiesProxy.get("initialDelay"));
			//任务1
			scheduExec.scheduleAtFixedRate(new Runnable() {
	            public void run() {
	                try {
	                	
	                } catch (Exception e) {  
	                   log.error("定位终端service接口", e);
	                }  
	            }  
	        }, initialDelay, period, TimeUnit.MILLISECONDS);
			log.info("初始化任务结束");
			
			
			
			//初始化缓存
			JedisInitService jedisInitService=Mvcs.ctx().getDefaultIoc().get(JedisInitService.class, "jedisInitService");
			if(null!=jedisInitService){
				jedisInitService.init();
				log.error("初始化缓存完成....");
			}
		}catch (Exception e) {
			log.error("系统初始化操作异常",e);
		}
	}
}
