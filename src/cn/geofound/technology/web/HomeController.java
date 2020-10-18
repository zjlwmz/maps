package cn.geofound.technology.web;

import cn.geofound.technology.entity.PgFun;
import cn.geofound.technology.service.PgFunService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.util.List;

/**
 * 首页控制器
 * @author zhangjialu
 * @date 2018-12-3 下午9:38:27
 */
@IocBean
@At("/")
public class HomeController {

	@Inject
	private PgFunService pgFunService;

	
	/**
	 * 首页 
	 * @return
	 */
	@At(value={"","home"})
	@Ok("beetl:/public/index.html")
	public Object home(){
		return null;
	}
	
	/**
	 * 地图
	 * @return
	 */
	@At(value="map")
	@Ok("beetl:/public/map.html")
	public Object map(){
		return null;
	}
	
	
	/**
	 * postgis操作
	 * @return
	 */
	@At(value="postgis")
	@Ok("re")
	public Object postgis(ViewModel viewModel){
		List<PgFun>pgFunList =pgFunService.query();
		viewModel.put("pgFunList",pgFunList);
		return "beetl:/public/postgis/index.html";
	}

	
	
	
	/**
	 * 亲戚关系查询
	 * @return
	 */
	@At(value="relationship")
	@Ok("beetl:/public/relationship.html")
	public Object relationship(){
		return null;
	}
	
	
	/**
	 * 进行中任务
	 * @return
	 */
	@At(value="havehand")
	@Ok("beetl:/public/havehand.html")
	public Object havehand(){
		return null;
	}
	
	
	/**
	 * ui展示
	 * @return
	 */
	@At(value="ui")
	@Ok("beetl:/public/ui.html")
	public Object ui(){
		return null;
	}
	
	
	/**
	 * 更多
	 * @return
	 */
	@At(value="more")
	@Ok("beetl:/public/more.html")
	public Object more(){
		return null;
	}
	
	
	/**
	 * 自定义地址
	 * @return
	 */
	@At(value="custom")
	@Ok("re")
	public Object custom(String name){
		return "beetl:/"+name+".html";
	}
}
