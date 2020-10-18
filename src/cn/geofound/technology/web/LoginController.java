package cn.geofound.technology.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import cn.geofound.common.utils.CookieUtils;
import cn.geofound.common.utils.DateUtils;
import cn.geofound.common.utils.StringUtils;
import cn.geofound.technology.entity.Menu;
import cn.geofound.technology.entity.User;
import cn.geofound.technology.service.RoleService;
import cn.geofound.technology.service.UserService;



/**
 * 登录控制器
 * @author zhangjialu
 * @date 2018-6-4上午10:09:17
 */
@IocBean
@At("/")
public class LoginController {

	private static final Log log = Logs.get();
	
	public static final String cookie_userId="wyjc_user_id";
	
	/**
	 * 用户service接口
	 */
	@Inject
	private UserService userService;
	
	
	/**
	 * 角色service接口
	 */
	@Inject
	private RoleService roleService;
	
	@At(value={"/login"})
	@GET
	@Ok("re")
	public Object login(ViewModel model,HttpSession httpSession){
		User user=(User) httpSession.getAttribute("user");
		if(null==httpSession.getAttribute("user")){
			return "beetl:/login.html";
		}
		model.addv("date", DateUtils.getDate("yyyy年MM月dd日"));
		model.addv("username", user.getUsername());
		return "redirect:/";
	}
	
	/**
	 * 用户登录
	 * @return
	 */
	@At("/login")
	@POST
	@Ok("re")
	public Object doLogin(@Param("loginname")String loginname,@Param("password")String password,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession,ViewModel model){
		try{
			if(StringUtils.isBlank(loginname)){
				model.addv("msg", "登录名称不能为空");
				return "beetl:/login.html";
			}
			
			if(StringUtils.isBlank(password)){
				model.addv("msg", "密码名称不能为空");
				return "beetl:/login.html";
			}
			
			User user=userService.fetch(Cnd.where("loginname", "=", loginname).and("passWord","=", password));
			if(null==user){
				model.addv("msg", "用户密码错误");
				return "beetl:/login.html";
			}
			model.addv("date", DateUtils.getDate("yyyy年MM月dd日"));
			model.addv("username", user.getUsername());
			CookieUtils.setCookie(response, cookie_userId, user.getId());
			List<Menu>menuList=roleService.findMenuList(user.getRoleId());
			httpSession.setAttribute("user", user);
			httpSession.setAttribute("menuList", menuList);
			return "redirect:/vehicle/monitoring";
		}catch (Exception e) {
			log.error("用户登录", e);
			model.addv("msg", "请联系管理员");
			return "redirect:/vehicle/monitoring";
		}
	}
	
	
	
	
	/**
	 * 登录退出
	 * @return
	 */
	@At("/loginout")
	@Ok("re")
	public Object loginout(HttpServletRequest request,HttpServletResponse response){
		CookieUtils.removeCookie(request, response, cookie_userId);
		request.getSession().removeAttribute("user");
		request.getSession().removeAttribute("menuList");
		
		return "redirect:/login";
	}
	
}
