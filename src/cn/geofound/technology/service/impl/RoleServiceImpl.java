package cn.geofound.technology.service.impl;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.entity.Menu;
import cn.geofound.technology.entity.Role;
import cn.geofound.technology.service.RoleService;


/**
 * 角色service接口实现
 * @author zhangjialu
 * @date 2018-6-10下午1:19:49
 */
@IocBean(args = {"refer:dao"})
public class RoleServiceImpl  extends BaseServiceImpl<Role> implements RoleService{

	public RoleServiceImpl(Dao dao) {
		super(dao);
	}

	@Override
	public List<Menu> findMenuList(String roleId) {
		String sqlstr="SELECT m.* from sys_role_menu rm ,sys_menu m where rm.menu_id=m.id and rm.role_id=@roleId ORDER BY sort asc";
		Sql sql=Sqls.create(sqlstr);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Menu.class));
		sql.params().set("roleId", roleId);
		dao().execute(sql);
		List<Menu>menuList=sql.getList(Menu.class);
		return menuList;
	}

}
