package cn.geofound.technology.service;

import java.util.List;

import org.nutz.dao.entity.Record;

import cn.geofound.framework.base.service.BaseService;
import cn.geofound.technology.entity.Shop;

public interface ShopService extends BaseService<Shop>{

	List<Record>findShopList(String city);
}
