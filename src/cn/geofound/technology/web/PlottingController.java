package cn.geofound.technology.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.technology.entity.Plotting;
import cn.geofound.technology.service.PlottingService;

/**
 * 标绘管理控制器
 * @author zhangjialu
 * @date 2019年4月20日 下午7:06:39
 */
@IocBean
@At("/plotting/")
public class PlottingController {
	
	/**
	 * 标绘管理service接口
	 */
	@Inject
	private PlottingService plottingService;
	
	
	/**
	 * 分页查询
	 * @param page
	 * @param limit
	 * @return
	 */
	@At("/listpage")
	@Ok("json")
	public Object listpage(@Param("page") Integer page, @Param("limit") Integer limit,@Param("keyword")String keyword) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Cnd cnd = Cnd.NEW().limit(page, limit);
			if(StringUtils.isNotBlank(keyword)){
				cnd.and("name", "like", "%"+keyword+"%");
			}
			cnd.desc("createDate");
			List<Plotting> query = plottingService.query(cnd);
			int count = plottingService.count(cnd);
			map.put("code", 0);
			map.put("data", query);
			map.put("msg", "");
			map.put("count", count);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("code", -1);
			map.put("msg", "");
		}
		return map;
	}
	
}
