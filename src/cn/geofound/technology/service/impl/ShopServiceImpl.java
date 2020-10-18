package cn.geofound.technology.service.impl;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.Shop;
import cn.geofound.technology.service.ShopService;



@IocBean(args = {"refer:dao"})
public class ShopServiceImpl extends BaseServiceImpl<Shop> implements ShopService{

	public ShopServiceImpl(Dao dao) {
		super(dao);
	}

	@Override
	public List<Record> findShopList(String city) {
		String sqlstr="SELECT x,y,money FROM data.shop where city='"+city+"'";
		Sql sql=Sqls.queryRecord(sqlstr);
		this.dao().execute(sql);
		return sql.getList(Record.class);
	}

}
