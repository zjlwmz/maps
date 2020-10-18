package cn.geofound.technology.web;

import javax.servlet.http.HttpSession;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewModel;

import cn.geofound.common.utils.DateUtils;
import cn.geofound.technology.entity.User;

public class BaseController {

	
	/**
	 * 设置默认参数视图
	 * @param model
	 */
	public void addViewModel(ViewModel model,String url){
		HttpSession httpSession = Mvcs.getHttpSession();
		User user=(User) httpSession.getAttribute("user");
		model.addv("date", DateUtils.getDate("yyyy年MM月dd日"));
		model.addv("username", user.getUsername());
		model.addv("currentUrl", url);
	}
}
