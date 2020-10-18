package cn.geofound.technology.service.impl;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.User;
import cn.geofound.technology.service.UserService;

@IocBean(args = {"refer:dao"})
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService{

	public UserServiceImpl(Dao dao) {
		super(dao);
	}

}
