package cn.geofound.technology.web;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.entity.City;
import cn.geofound.technology.entity.CityZoneNew;
import cn.geofound.technology.service.CityService;
import cn.geofound.technology.service.CityZoneNewService;

/**
 * 商圈控制器
 * @author zhangjialu
 * @date 2019年5月26日 上午11:58:45
 */
@IocBean
@At("/cityzone/")
public class CityZoneNewController {
	
	private static final Log log = Logs.get();
	
	/**
	 *  城市service接口
	 */
	@Inject
	private CityService cityService;
	
	
	@Inject
	private CityZoneNewService cityZoneNewService;
	
	/**
	 * 商圈 页面
	 * @param model
	 * @return
	 */
	@At("index")
	@Ok("re")
	public Object index(ViewModel model){
		List<City>cityList= cityService.query(Cnd.NEW().and("status", "=", "0"));
		model.addv("cityList", cityList);
		return "beetl:/platform/cityzone/index.html";
	}
	
	
	
	/**
	 * 获取城市列表
	 */
	@At("findCityList")
	@Ok("json")
	public Object findCityList(){
		try{
			List<City>list= cityService.query();
			return Result.success("ok",list);
		}catch (Exception e) {
			log.error("获取城市列表异常", e);
			return Result.error("获取城市列表异常");
		}
	}
	
	/**
	 * 获取城市列表
	 */
	@At("findCityZoneNewList")
	@Ok("json")
	public Object findCityZoneNewList(String cityCode){
		try{
			if(StringUtils.isNotBlank(cityCode)){
				List<CityZoneNew>list= cityZoneNewService.query(Cnd.NEW().and("cityCode", "=", cityCode));
				return Result.success("ok",list);
			}
			return Result.success("ok",new ArrayList<CityZoneNew>());
		}catch (Exception e) {
			log.error("获取城市列表异常", e);
			return Result.error("获取城市列表异常");
		}
	}
	
	
	/**
	 * 获取城市列表
	 */
	@At("findCityZoneNewTypeList")
	@Ok("json")
	public Object findCityZoneNewTypeList(String cityCode){
		try{
			if(StringUtils.isNotBlank(cityCode)){
				List<Record>list= cityZoneNewService.findCityZoneNewTypeList(cityCode);
				return Result.success("ok",list);
			}
			return Result.success("ok",new ArrayList<CityZoneNew>());
		}catch (Exception e) {
			log.error("获取城市列表异常", e);
			return Result.error("获取城市列表异常");
		}
	}
}
