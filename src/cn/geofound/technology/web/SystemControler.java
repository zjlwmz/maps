package cn.geofound.technology.web;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import cn.geofound.common.utils.DateUtils;
import cn.geofound.common.utils.MyCheckSession;
import cn.geofound.technology.entity.User;


/**
 * 系统管理
 * @author zhangjialu
 * @date 2018-6-9下午11:09:35
 */
@IocBean
@At("/system/")
@Filters(@By(type=MyCheckSession.class))
public class SystemControler extends BaseController{

	
	/**
	 * 系统设置
	 * @return
	 */
	@At(value={"systemlist"})
	@Ok("re")
	public Object vehiclelist(ViewModel model){
		addViewModel(model, "/system/systemlist");
		return "beetl:/systemlist.html";
	}
	
	/**
	 * 关于我们
	 * @return
	 */
	@At(value={"aboutOur"})
	@Ok("re")
	public Object aboutOur(ViewModel model,HttpSession httpSession){
		User user=(User) httpSession.getAttribute("user");
		model.addv("date", DateUtils.getDate("yyyy年MM月dd日"));
		model.addv("username", user.getUsername());
		return "beetl:/aboutOur.html";
	}
	
}
