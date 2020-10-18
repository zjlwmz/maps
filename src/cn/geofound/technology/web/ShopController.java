package cn.geofound.technology.web;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.geofound.framework.base.Result;
import cn.geofound.technology.entity.Shop;
import cn.geofound.technology.service.ShopService;

/**
 * 门店控制器
 * @author zhangjialu
 * @date 2019年8月22日 下午9:09:44
 */
@IocBean
@At("/shop/")
public class ShopController {

	private static final Log log = Logs.get();
	
	@Inject
	private ShopService shopService;
	
	/**
	 * 
	 * @param name
	 * @param model
	 * @return
	 */
	@At
	@Ok("re")
	public Object test(@Param("name")String name,ViewModel model){
		return "beetl:/public/shop/"+name+".html";
	}
	
	/**
	 * 
	 * @param name
	 * @param model
	 * @return
	 */
	@At
	@Ok("re")
	public Object index(ViewModel model){
		return "beetl:/public/shop/index.html";
	}
	
	/**
	 * 门店查询
	 */
	@At("list")
	@Ok("json")
	public Object findShopList(String city){
		try{
			List<Record>list= shopService.findShopList(city);
			return Result.success("ok",list);
		}catch (Exception e) {
			log.error("门店查询异常", e);
			return Result.error("门店查询异常");
		}
	}
	
	/**
	 * 门店查询
	 */
	@At("list2")
	@Ok("json")
	public Object findShopList2(){
		try{
			List<Shop>list= shopService.query(Cnd.NEW());
			return Result.success("ok",list);
		}catch (Exception e) {
			log.error("门店查询异常", e);
			return Result.error("门店查询异常");
		}
	}
}
