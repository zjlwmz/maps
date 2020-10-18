package cn.geofound.technology.service.impl;

import cn.geofound.technology.entity.UserDataConfig;
import cn.geofound.technology.service.UserDataConfigService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import java.util.List;

/**
 * (UserDataConfig)表服务实现类
 *
 * @author makejava
 * @since 2020-07-13 18:20:02
 */
@IocBean(args = {"refer:dao"})
public class UserDataConfigServiceImpl extends BaseServiceImpl<UserDataConfig> implements UserDataConfigService {
    public UserDataConfigServiceImpl(Dao dao) {
		super(dao);
	}
}