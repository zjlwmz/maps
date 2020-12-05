package cn.geofound.technology.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.geofound.framework.base.Result;
import com.alibaba.fastjson.JSON;
import org.nutz.dao.Cnd;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
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

	@Inject("java:$custom.get('api.domain')")
	private String apiDomain;



	
	@At(value={"/login"})
	@GET
	@Ok("re")
	public Object login(ViewModel model,HttpSession httpSession){
		User user=(User) httpSession.getAttribute("user");
		if(null==httpSession.getAttribute("user")){
			return "beetl:/platform/login.html";
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
	public Object doLogin(@Param("loginname")String loginName,@Param("password")String password,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession,ViewModel model){
		try{
			if(StringUtils.isBlank(loginName)){
				return Result.error("登录名称不能为空");
			}
			
			if(StringUtils.isBlank(password)){
				return Result.error("密码名称不能为空");
			}

			NutMap nutMap = new NutMap();
			nutMap.put("loginName",loginName);
			nutMap.put("password",password);
			Response responseHttp = Http.post2(apiDomain+"/s/login/login",nutMap,3000);
			if(responseHttp.isOK()){
				String content = responseHttp.getContent();
				if(StringUtils.isNotBlank(content)){
					Result result = JSON.parseObject(content,Result.class);
					if(result.getCode() == 0){
						Map<String,Object> data = (Map<String, Object>) result.getData();
						String token = data.get("token").toString();
						CookieUtils.setCookie(response, "token", token,60*60*24*15);//15天
					}

					return result;
				}
			}
			return Result.error("登录失败");
		}catch (Exception e) {
			log.error("登录失败", e);
			return Result.error("登录失败");
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
