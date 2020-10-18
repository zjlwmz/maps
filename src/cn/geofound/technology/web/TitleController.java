package cn.geofound.technology.web;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.common.utils.title.BoundingBox;
import cn.geofound.common.utils.title.TitleUtils;
import cn.geofound.technology.service.TitleService;

/**
 * 瓦片控制器
 * @author zhangjialu
 * @date 2019年5月25日 上午9:11:08
 */

@IocBean
@At("/api/title/")
public class TitleController {
	private static final Log log = Logs.get();
	
	
	@Inject
	private TitleService titleService;
	
	/**
	 * 矢量瓦片加载 一
	 * @param name
	 * @param model
	 * @return
	 */
	@At("/index")
	@Ok("re")
	public Object index(ViewModel model){
		return "beetl:/platform/vetor/index.html";
	}
	
	
	/**
	 * 矢量瓦片加载2二
	 * @param name
	 * @param model
	 * @return
	 */
	@At("/index2")
	@Ok("re")
	public Object index2(ViewModel model){
		return "beetl:/platform/vetor/index2.html";
	}
	
	
	/**
	 * 矢量瓦片加载2二
	 * @param name
	 * @param model
	 * @return
	 */
	@At("/index3")
	@Ok("re")
	public Object index3(ViewModel model){
		return "beetl:/platform/vetor/index3.html";
	}
	
	
	
	/**
	 * 矢量瓦片加载2二
	 * @param name
	 * @param model
	 * @return
	 */
	@At("/index4")
	@Ok("re")
	public Object index4(ViewModel model){
		return "beetl:/platform/vetor/leaflet_vector_grid_001.html";
	}

	
	
	
	/**
	 * 点获取瓦片
	 * @return
	 */
	@At("getTitle/?/?/?")
	@Ok("raw")
	public Object getTitle(Integer z,Integer x,Integer y){
		try{
			BoundingBox boundingBox =TitleUtils.tile2boundingBox(x, y, z);
			return titleService.getST_AsMVT_forPoints(boundingBox);
		}catch (Exception e) {
			log.error("获取瓦片异常", e);
			return null;
		}
	}
	
	
	
	
	/**
	 * 线获取瓦片
	 * @return
	 */
	@At("getTitle2/?/?/?")
	@Ok("raw")
	public Object getTitle2(Integer z,Integer x,Integer y){
		try{
			BoundingBox boundingBox =TitleUtils.tile2boundingBox(x, y, z);
			return titleService.getST_AsMVT(boundingBox);
		}catch (Exception e) {
			log.error("获取瓦片异常", e);
			return null;
		}
	}
	
	
	/**
	 * 面获取瓦片
	 * @return
	 */
	@At("getTitle3/?/?/?")
	@Ok("raw")
	public Object getTitle3(Integer z,Integer x,Integer y){
		try{
			BoundingBox boundingBox =TitleUtils.tile2boundingBox(x, y, z);
			return titleService.getST_AsMVT_forPolygon(boundingBox);
		}catch (Exception e) {
			log.error("获取瓦片异常", e);
			return null;
		}
	}
	
	
	/**
	 * 面获取瓦片
	 * @return
	 */
	@At("getTitle4/?/?/?")
	@Ok("raw")
	public Object getTitle4(Integer z,Integer x,Integer y){
		try{
			String layername="polygon";
			String dataid="69e90a6928b34d7d8ac5bd01efdc0efa";
			//dataid="0a4e2ee034e211e9a5b6db1290f03679";
			if(StringUtils.isBlank(layername)){
				return null;
			}
			if(StringUtils.isBlank(dataid)){
				return null;
			}
			BoundingBox boundingBox =TitleUtils.tile2boundingBox(x, y, z);
			BoundingBox extend = new BoundingBox(113.695690959386,29.9718000581867,115.075731530401,31.3607888700044);
			
			if((extend.west<=boundingBox.west && boundingBox.west<=extend.east) && (extend.south<=boundingBox.south && boundingBox.south <=extend.north)){
				return titleService.getST_AsMVT_forPolygon(boundingBox,layername,dataid);
			}
			return null;
		}catch (Exception e) {
			log.error("获取瓦片异常", e);
			return null;
		}
	}

	
	/**
	 * 面获取瓦片
	 * @return
	 */
	@At("getTitle5/?/?/?")
	@Ok("raw")
	public Object getTitle5(Integer z,Integer x,Integer y){
		try{
			String layername="point";
			String dataid="zaiyinqiyefenbujingying";
			if(StringUtils.isBlank(layername)){
				return null;
			}
			if(StringUtils.isBlank(dataid)){
				return null;
			}
			BoundingBox boundingBox =TitleUtils.tile2boundingBox(x, y, z);
			
			BoundingBox extend = new BoundingBox(113.707885742379,29.9815063475391,115.066894530763,31.334899901925);
			
			if((extend.west<=boundingBox.west && boundingBox.west<=extend.east) && (extend.south<=boundingBox.south && boundingBox.south <=extend.north)){
				return titleService.getST_AsMVT_forPolygon(boundingBox,layername,dataid);
			}
			return null;
		}catch (Exception e) {
			log.error("获取瓦片异常", e);
			return null;
		}
	}
	
	
	
}
