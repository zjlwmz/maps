package cn.geofound.technology.service.impl;

import cn.geofound.technology.entity.PgFun;
import cn.geofound.technology.service.PgFunService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;

/**
 * (PgFunc)表服务实现类
 *
 * @author makejava
 * @since 2020-04-19 22:01:18
 */
@IocBean(args = {"refer:dao"})
public class PgFunServiceImpl extends BaseServiceImpl<PgFun> implements PgFunService {
    public PgFunServiceImpl(Dao dao) {
		super(dao);
	}
}