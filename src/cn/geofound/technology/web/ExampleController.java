package cn.geofound.technology.web;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;


/**
 * 地图demo例子
 * @author zhangjialu
 * @date 2018-12-3 下午10:27:27
 */
@IocBean
@At("/example/")
public class ExampleController {

	/**
	 * 例子
	 * @param name
	 * @param model
	 * @return
	 */
	@At
	@Ok("re")
	public Object index(@Param("name")String name,ViewModel model){
		return "beetl:/"+name+".html";
	}
	
}
