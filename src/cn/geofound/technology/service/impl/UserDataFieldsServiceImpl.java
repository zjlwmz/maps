package cn.geofound.technology.service.impl;

import cn.geofound.technology.entity.UserDataFields;
import cn.geofound.technology.service.UserDataFieldsService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import java.util.List;

/**
 * (UserDataFields)表服务实现类
 *
 * @author makejava
 * @since 2020-07-14 14:51:59
 */
@IocBean(args = {"refer:dao"})
public class UserDataFieldsServiceImpl extends BaseServiceImpl<UserDataFields> implements UserDataFieldsService {
    public UserDataFieldsServiceImpl(Dao dao) {
		super(dao);
	}
}