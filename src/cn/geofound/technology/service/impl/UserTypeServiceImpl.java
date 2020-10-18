package cn.geofound.technology.service.impl;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.UserType;
import cn.geofound.technology.service.UserTypeService;

@IocBean(args = {"refer:dao"})
public class UserTypeServiceImpl extends BaseServiceImpl<UserType> implements UserTypeService{

	public UserTypeServiceImpl(Dao dao) {
		super(dao);
	}

}
