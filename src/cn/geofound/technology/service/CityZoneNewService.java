package cn.geofound.technology.service;

import java.util.List;

import org.nutz.dao.entity.Record;

import cn.geofound.framework.base.service.BaseService;
import cn.geofound.technology.entity.CityZoneNew;


/**
 * 城市商圈 service
 * @author zhangjialu
 * @date 2019年5月26日 下午12:50:56
 */
public interface CityZoneNewService extends BaseService<CityZoneNew>{

	public List<Record> findCityZoneNewTypeList(String cityCode);
}
