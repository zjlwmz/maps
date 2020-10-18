package cn.geofound.technology.web;


import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.geofound.common.utils.DateUtils;
import cn.geofound.common.utils.MyCheckSession;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.entity.User;
import cn.geofound.technology.service.UserService;


/**
 * 用户管理
 * @author zhangjialu
 * @date 2018-6-10下午1:50:34
 */
@IocBean
@At("/user")
@Filters(@By(type=MyCheckSession.class))
public class UserController {
	
	private static final Log log = Logs.get();
	
	@Inject
	private UserService userService;
	
	/**
	 * 车辆型号
	 * @return
	 */
	@At(value={"/modifypassword"})
	@Ok("re")
	public Object vehiclelist(ViewModel model,HttpSession httpSession){
		User user=(User) httpSession.getAttribute("user");
		model.addv("date", DateUtils.getDate("yyyy年MM月dd日"));
		model.addv("username", user.getUsername());
		return "beetl:/password.html";
	}
	
	
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@At("/addUser")
	@Ok("json")
	public Object addUser(@Param("..")User user){
		User insert = userService.insert(user);
		if(insert != null){
			return Result.success("添加成功");
		}
		return Result.error("添加失败");
	}
	
	
	/**
	 * 根据id查看个人信息
	 * @param id
	 * @return
	 */
	@At("/queryUser")
	@Ok("json")
	public Object queryUser(@Param("id")String id){
		
		if(StringUtils.isNotBlank(id)){
			return userService.fetch(id);
		}else{
			return Result.error("没有此用户id");
		}
	}
		
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	@At("/updateUserInfo")
	@Ok("json")
	public Object updateUserInfo(@Param("..")User user){
		try{
			if(StringUtils.isNotBlank(user.getId())){
				user.setCreateDate(new Date());
				int updateIgnoreNull = userService.updateIgnoreNull(user);
				if(updateIgnoreNull > 0){
					return Result.success("修改成功");
				}else{
					return Result.success("修改失败");
				}
			}else{
				return Result.error("没有此用户id");
			}
		}catch (Exception e) {
			log.error("修改密码", e);
			return Result.error("修改用户信息失败");
		}
		
	}
	
	/**
	 * 修改密码
	 * @param user
	 * @param newPassword
	 * @return
	 */
	@At("/updateUserPassWord")
	@Ok("json")
	public Object updateUserPassWord(@Param("oldpassword")String oldpassword,@Param("newpassword")String newPassword){
		try{
			if(StringUtils.isBlank(oldpassword)){
				return Result.error("旧密码不能为空");
			}
			
			if(StringUtils.isBlank(newPassword)){
				return Result.error("新密码不能为空");
			}
			
			
			User user=(User) Mvcs.getHttpSession().getAttribute("user");
			User localUser = userService.fetch(Cnd.where("id","=",user.getId()).and("password","=",oldpassword));
			if(localUser == null){
				return Result.error("旧密码不正确");
			}else{
				localUser.setPassWord(newPassword);
				localUser.setUpdateDate(new Date());
				
				int updateIgnoreNull = userService.updateIgnoreNull(localUser);
				if(updateIgnoreNull > 0){
					return Result.success("密码修改成功");
				}else{
					return Result.error("修改密码失败");
				}
			}
		}catch (Exception e) {
			log.error("修改密码", e);
			return Result.error("修改密码失败");
		}
	}
	
}
