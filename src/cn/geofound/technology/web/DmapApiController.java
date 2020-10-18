package cn.geofound.technology.web;

import java.util.Date;
import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.Result;
import cn.geofound.technology.dto.PlottingParamsDTO;
import cn.geofound.technology.entity.Plotting;
import cn.geofound.technology.service.PlottingService;

/**
 * 地图API控制器
 * @author zhangjialu
 * @date 2019年4月20日 下午7:10:06
 */
@IocBean
@At("/api")
public class DmapApiController {

	
	private static final Log log = Logs.get();
	
	/**
	 * 标绘管理service接口
	 */
	@Inject
	private PlottingService plottingService;
	
	/**
	 * 添加标绘
	 * @return
	 */
	@At("/plotting/insert")
	@Ok("json")
	public Object addPlotting(@Param("..")PlottingParamsDTO paramsDTO){
		try{
			Plotting plotting =new  Plotting();
			String geojson=paramsDTO.getGeojson();
			if(StringUtils.isBlank(geojson)){
				return Result.error("geometry 数据不能为空");
			}
			JSONObject geojsonObj=JSON.parseObject(geojson);
			JSONObject geometry=geojsonObj.getJSONObject("geometry");
			JSONObject properties = geojsonObj.getJSONObject("properties");
			String type="";
			if(null!=properties && properties.containsKey("type")){
				type = properties.getString("type");
			}else{
				type = geometry.getString("type");
			}
			plotting.setType(type);
			plotting.setName(paramsDTO.getName());
			plotting.setRadius(0d);
			plotting.setLatLngs(paramsDTO.getLatLngs());
			plotting.setCreateDate(new Date());
			plotting.setUpdateDate(plotting.getCreateDate());
			//圆
			if(type.equals("circle") || type.equals("circlemarker")){
				Double radius = properties.getDouble("radius");
				plotting.setRadius(radius);
			}
			plottingService.insertPlotting(plotting,JSON.toJSONString(geometry));
			return Result.success("保存成功");
		}catch (Exception e) {
			log.error("添加标绘异常", e);
			return Result.error();
		}
	}
	
	
	/**
	 * 修改标绘
	 * @return
	 */
	@At("/plotting/update")
	@Ok("json")
	public Object updatePlotting(@Param("..")PlottingParamsDTO paramsDTO){
		try{
			if(StringUtils.isBlank(paramsDTO.getId())){
				return Result.error("数据ID不能为空");
			}
			Plotting plotting =new  Plotting();
			String geojson=paramsDTO.getGeojson();
			if(StringUtils.isBlank(geojson)){
				return Result.error("geometry 数据不能为空");
			}
			
			if(StringUtils.isBlank(paramsDTO.getId())){
				return Result.error("数据ID不能为空");
			}
			JSONObject geojsonObj=JSON.parseObject(geojson);
			JSONObject geometry=geojsonObj.getJSONObject("geometry");
			JSONObject properties = geojsonObj.getJSONObject("properties");
			String type = properties.getString("type");
			plotting.setId(paramsDTO.getId());
			plotting.setType(type);
			plotting.setName(paramsDTO.getName());
			plotting.setRadius(0d);
			plotting.setUpdateDate(new Date());
			plotting.setLatLngs(paramsDTO.getLatLngs());
			//圆
			if(type.equals("circle") || type.equals("circlemarker")){
				Double radius = properties.getDouble("radius");
				plotting.setRadius(radius);
			}
			plottingService.updatePlotting(plotting, JSON.toJSONString(geometry));
			return Result.success("保存成功");
		}catch (Exception e) {
			log.error("添加标绘异常", e);
			return Result.error();
		}
	}
	
	/**
	 * 标绘查询
	 */
	@At("/plotting/list")
	@Ok("json")
	public Object findplottingList(){
		try{
			List<Plotting>list=plottingService.findListPlotting();
			return Result.success("ok",list);
		}catch (Exception e) {
			log.error("标绘查询异常", e);
			return Result.error();
		}
	}
	
	
	
	/**
	 * 标绘删除
	 */
	@At("/plotting/delete")
	@Ok("json")
	public Object deleteplotting(String id){
		try{
			plottingService.deleteById(id);
			return Result.success("删除成功");
		}catch (Exception e) {
			log.error("标绘删除异常", e);
			return Result.error();
		}
	}
	
	
	/**
	 * 几何对象关系函数
	 */
	@At("/plotting/relation")
	@Ok("json")
	public Object relationPostgis(String a,String b,String methond){
		try{
			if(StringUtils.isBlank(a) || StringUtils.isBlank(b) || StringUtils.isBlank(methond)){
				return Result.error("参数缺少");
			}
			plottingService.relationPostgisFunction(a, b, methond);
			return Result.success("查询关系成功");
		}catch (Exception e) {
			log.error("标绘删除异常", e);
			return Result.error();
		}
	}
	
	
	/**
	 * 几何对象处理函数
	 */
	@At("/plotting/processing")
	@Ok("json")
	public Object processingPostgis(String a,String b,String methond,Double raduis){
		try{
			if(StringUtils.isBlank(a) && StringUtils.isBlank(b) && StringUtils.isBlank(methond)){
				return Result.error("参数缺少");
			}
			if(StringUtils.isBlank(methond)){
				return Result.error("参数缺少");
			}
			//获取圆的多边形
			if(methond.equals("get_circle")){
				Plotting  plotting=plottingService.getCircle(a);
				return Result.success("处理成功",plotting);
			}
			
			//缓冲区
			if(methond.equals("ST_Buffer")){
				if(null == raduis){
					return Result.error("缓冲区半径缺少");
				}
				Plotting  plotting=plottingService.processingBufferPostgis(a,raduis);
				return Result.success("处理成功",plotting);
			}
			
			//【合并为线】 MultiLineString to  LineString
			else if(methond.equals("ST_LineMerge")){
				Plotting  plotting=plottingService.processingLineMergePostgis(a);
				return Result.success("处理成功",plotting);
			}
			else if(methond.equals("ST_Centroid")){
				Plotting  plotting=plottingService.accessFunctionPostgis(a,methond);
				return Result.success("处理成功",plotting);
			}
			// 获取线起点、终点
			else if(methond.equals("ST_StartPoint") || methond.equals("ST_EndPoint") || methond.equals("ST_Boundary")){
				Plotting  plotting=plottingService.accessFunctionPostgis(a, methond);
				return Result.success("处理成功",plotting);
			}
			//点合并成线
			else if(methond.equals("ST_MakeLine")){
				Plotting  plotting=plottingService.accessFunctionPostgis(a, b, methond);
				return Result.success("处理成功",plotting);
			}
			// 弧线中点
			else if(methond.equals("Arc_Midpoint")){
				Plotting  plotting=plottingService.getLinearEquationAndDistince(a, b);
				return Result.success("处理成功",plotting);
			}
			//多面转单面
			else if(methond.equals("ST_Dump")){
				List<Plotting>  plottingList=plottingService.multipolygon2polygon(a);
				return Result.success("处理成功",plottingList);
			}
			//几何对象裁剪  如 面 被线进行裁剪
			else if(methond.equals("postgisGeometrySplit")){
				List<Plotting>  plottingList=plottingService.postgisGeometrySplit(a,b);
				return Result.success("处理成功",plottingList);
			}
			else{
				Plotting  plotting=plottingService.processingPostgisFunction(a, b, methond);
				return Result.success("处理成功",plotting);
			}
			
		}catch (Exception e) {
			log.error("标绘删除异常", e);
			return Result.error();
		}
	}
	
	
}
