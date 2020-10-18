package cn.geofound.technology.web;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * 地图页面控制器
 * @author zhangjialu
 * @date 2019年4月20日 下午10:00:23
 */
@IocBean
@At("/view")
public class DmapViewsContrller {

	
	/**
	 * 标绘页面
	 * @param name
	 * @param model
	 * @return
	 */
	@At("/plotting/form")
	@Ok("re")
	public Object plottingForm(ViewModel model){
		return "beetl:/platform/plotting/plottingForm.html";
	}
	
	
}
