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
	 * 地图页面
	 * @param model
	 * @return
	 */
	@At("/basemap/form")
	@Ok("re")
	public Object basemapForm(ViewModel model){
		return "beetl:/platform/basemap/layerForm.html";
	}


	/**
	 * 地图页面
	 * @param model
	 * @return
	 */
	@At("/map/form")
	@Ok("re")
	public Object mapForm(ViewModel model){
		return "beetl:/platform/plotting/mapForm.html";
	}



	/**
	 * 地图页面
	 * @param model
	 * @return
	 */
	@At("/map/layer/list")
	@Ok("re")
	public Object layerList(ViewModel model){
		return "beetl:/platform/plotting/layerList.html";
	}

	/**
	 * 地图页面
	 * @param model
	 * @return
	 */
	@At("/map/layer/form")
	@Ok("re")
	public Object layerForm(ViewModel model){
		return "beetl:/platform/plotting/layerForm.html";
	}

	
	/**
	 * 标绘页面
	 * @param model
	 * @return
	 */
	@At("/plotting/form")
	@Ok("re")
	public Object plottingForm(ViewModel model){
		return "beetl:/platform/plotting/plottingForm.html";
	}
	
	
}
