package cn.geofound.technology.service.impl;

import org.nutz.dao.Dao;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.Menu;
import cn.geofound.technology.service.MenuService;


/**
 * 菜单servie接口实现
 * @author zhangjialu
 * @date 2018-6-10下午1:26:28
 */
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService{

	public MenuServiceImpl(Dao dao) {
		super(dao);
	}

}
