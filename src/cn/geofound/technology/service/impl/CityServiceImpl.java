package cn.geofound.technology.service.impl;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.City;
import cn.geofound.technology.service.CityService;


/**
 * 城市service接口 实现
 * @author zhangjialu
 * @date 2019年5月26日 下午12:28:21
 */
@IocBean(args = {"refer:dao"})
public class CityServiceImpl extends BaseServiceImpl<City> implements CityService{

	public CityServiceImpl(Dao dao) {
		super(dao);
	}

}
