package cn.geofound.technology.service.impl;

import cn.geofound.technology.entity.UserData;
import cn.geofound.technology.service.UserDataService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import java.util.List;

/**
 * (UserData)表服务实现类
 *
 * @author makejava
 * @since 2020-07-14 14:58:22
 */
@IocBean(args = {"refer:dao"})
public class UserDataServiceImpl extends BaseServiceImpl<UserData> implements UserDataService {
    public UserDataServiceImpl(Dao dao) {
		super(dao);
	}
}