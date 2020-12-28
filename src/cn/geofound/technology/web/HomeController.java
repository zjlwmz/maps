package cn.geofound.technology.web;

import cn.geofound.common.utils.CookieUtils;
import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.entity.PgFun;
import cn.geofound.technology.service.PgFunService;
import com.alibaba.fastjson.JSON;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 * @author zhangjialu
 * @date 2018-12-3 下午9:38:27
 */
@IocBean
@At("/")
public class HomeController {

	private static final Log log = Logs.get();


	@Inject("java:$custom.get('api.domain')")
	private String apiDomain;

	@Inject
	private PgFunService pgFunService;

	public String getToken(){
		String etWatchUserCookie= CookieUtils.getCookie(Mvcs.getReq(), "token");
		if(StringUtils.isBlank(etWatchUserCookie)){
			etWatchUserCookie = Mvcs.getReq().getParameter("token");
		}
		return etWatchUserCookie;
	}


	/**
	 * 首页 
	 * @return
	 */
	@At(value={"","home"})
	@Ok("beetl:/public/index.html")
	public Object home(ViewModel viewModel){
		try{
			String token = getToken();
			if(StringUtils.isNotBlank(token)){
				NutMap nutMap = new NutMap();
				nutMap.put("token",token);
				Response responseHttp = Http.post2(apiDomain+"/s/user/info",nutMap,30000);
				if(responseHttp.isOK()){
					String content = responseHttp.getContent();
					if(StringUtils.isNotBlank(content)){
						Result result = JSON.parseObject(content,Result.class);
						if(result.getCode() == 0){
							Map<String,Object> userInfo = (Map<String, Object>) result.getData();
							viewModel.put("userInfo",userInfo);
						}
					}
				}
			}
		}catch (Exception e){
			log.error("获取用户信息异常",e);
		}


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
	 * 地图-百度
	 * @return
	 */
	@At(value="baidu")
	@Ok("beetl:/public/baidu.html")
	public Object baidu(){
		return null;
	}


	/**
	 * 地图-mapbox
	 * @return
	 */
	@At(value="mapbox")
	@Ok("beetl:/public/mapbox.html")
	public Object mapbox(){
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
