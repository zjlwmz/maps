package cn.geofound.technology.service;

import java.util.List;

import cn.geofound.framework.base.service.BaseService;
import cn.geofound.technology.entity.Menu;
import cn.geofound.technology.entity.Role;

/**
 * 角色service接口
 * @author zhangjialu
 * @date 2018-6-10下午1:18:55
 */
public interface RoleService  extends BaseService<Role>{

	/**
	 * 角色菜单
	 * @param roleId
	 * @return
	 */
	public List<Menu>findMenuList(String roleId);
}
