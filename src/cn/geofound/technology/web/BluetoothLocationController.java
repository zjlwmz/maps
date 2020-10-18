package cn.geofound.technology.web;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.service.BluetoothLocationService;

/**
 * 蓝牙控制器
 * @author zhangjialu
 * @date 2019年4月27日 下午6:03:16
 */
@IocBean
@At("/api/bluetoothLocation")
public class BluetoothLocationController {

	private static final Log log = Logs.get();
	
	/**
	 * 蓝牙定位service
	 */
	@Inject
	private BluetoothLocationService bluetoothLocationService;
	
	/**
	 * 蓝牙定位
	 * @return
	 */
	@At("/location")
	@Ok("json")
	public Object location(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1){
		try{
			if(null==beacon1x || null==beacon1y || null==d1){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea1)){
				return Result.error("蓝牙1有效期区域不能为空");
			}
			String xy=bluetoothLocationService.location(beacon1x, beacon1y, d1, effectiveArea1);
			return Result.success("ok", xy);
		}catch (Exception e) {
			log.error("蓝牙定位异常", e);
			return Result.error("蓝牙定位异常");
		}
	}
	
	
	
	
	/**
	 * 蓝牙定位
	 * @return
	 */
	@At("/location2")
	@Ok("json")
	public Object location2(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1,
			Double beacon2x,Double beacon2y,Double d2,String effectiveArea2){
		try{
			if(null==beacon1x || null==beacon1y || null==d1){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea1)){
				return Result.error("蓝牙1有效期区域不能为空");
			}
			
			if(null==beacon2x || null==beacon2y || null==d2){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea2)){
				return Result.error("蓝牙2有效期区域不能为空");
			}
			
			String xy=bluetoothLocationService.location(beacon1x, beacon1y, d1, effectiveArea1, beacon2x, beacon2y, d2, effectiveArea2);
			return Result.success("ok", xy);
		}catch (Exception e) {
			log.error("蓝牙定位异常", e);
			return Result.error("蓝牙定位异常");
		}
	}
	
	
	
	/**
	 * 蓝牙定位
	 * @return
	 */
	@At("/location3")
	@Ok("json")
	public Object location3(Double beacon1x,Double beacon1y,Double d1,String effectiveArea1,
			Double beacon2x,Double beacon2y,Double d2,String effectiveArea2,
			Double beacon3x,Double beacon3y,Double d3,String effectiveArea3){
		try{
			//蓝牙1
			if(null==beacon1x || null==beacon1y || null==d1){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea1)){
				return Result.error("蓝牙1有效期区域不能为空");
			}
			
			//蓝牙2
			if(null==beacon2x || null==beacon2y || null==d2){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea2)){
				return Result.error("蓝牙2有效期区域不能为空");
			}
			
			//蓝牙3
			if(null==beacon3x || null==beacon3y || null==d3){
				return Result.error("经纬度、半径不能为空");
			}
			if(StringUtils.isBlank(effectiveArea3)){
				return Result.error("蓝牙2有效期区域不能为空");
			}
			
			String xy=bluetoothLocationService.location(beacon1x, beacon1y, d1, effectiveArea1, beacon2x, beacon2y, d2, effectiveArea2, beacon3x, beacon3y, d3, effectiveArea3);
			return Result.success("ok", xy);
		}catch (Exception e) {
			log.error("蓝牙定位异常", e);
			return Result.error("蓝牙定位异常");
		}
	}
	
	
}
