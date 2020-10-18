package cn.geofound.technology.service.impl;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.CityZoneNew;
import cn.geofound.technology.service.CityZoneNewService;


/**
 * 城市商圈 service 接口实现
 * @author zhangjialu
 * @date 2019年5月26日 下午12:52:01
 */
@IocBean(args = {"refer:dao"})
public class CityZoneNewServiceImpl extends BaseServiceImpl<CityZoneNew> implements CityZoneNewService{

	public CityZoneNewServiceImpl(Dao dao) {
		super(dao);
	}

	
	public List<Record> findCityZoneNewTypeList(String cityCode){
		String sqlTypestr="select type,city_code from trading_area.city_zone_new where city_code=@cityCode GROUP BY type,city_code";
		Sql sql = Sqls.queryRecord(sqlTypestr);
		sql.params().set("cityCode", cityCode);
		dao().execute(sql);
		List<Record>recordList= sql.getList(Record.class);
		for(Record record:recordList){
			String type = record.getString("type");
			
			String sqlListtr="select * from trading_area.city_zone_new where city_code=@cityCode and type=@type";
			Sql sqlList = Sqls.queryRecord(sqlListtr);
			sqlList.params().set("cityCode", cityCode).set("type", type);
			dao().execute(sqlList);
			List<Record>recordInfoList= sqlList.getList(Record.class);
			record.set("list", recordInfoList);
		}
		return recordList;
	}
}
