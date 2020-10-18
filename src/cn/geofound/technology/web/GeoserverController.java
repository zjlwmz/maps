package cn.geofound.technology.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.geofound.common.geoserver.GeoserverService;
import cn.geofound.framework.base.Result;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;

/**
 * geoserver控制器
 * @author zhangjialu
 * @date 2019年7月28日 下午5:04:42
 */
@IocBean
@At("/geoserver/")
public class GeoserverController {

	private static final Log log = Logs.get();
	
	/**
	 * geoserver rest 服务接口
	 */
	@Inject
	private GeoserverService geoserverService;
	
	
	/**
	 * 发布geoserver工作空间
	 * workspace
	 */
	@Inject("java:$custom.get('geoserver.workspacename')")
	private String geoserverWorkspaceName;
	
	/**
	 * 发布geoserver url地址
	 * geoserverDomainUrl
	 */
	@Inject("java:$custom.get('geoserver.domain.url')")
	private String geoserverDomainUrl;
	
	
	/**
	 * 
	 * @param name
	 * @param model
	 * @return
	 */
	@At
	@Ok("re")
	public Object getLyersView(@Param("name")String name,ViewModel model){
		return "beetl:/public/geoserver/"+name+".html";
	}
	
	
	
	/**
	 * 图层查询
	 */
	@At("layer/?")
	public Object getLayer(String layername){
		try{
			String workspace=geoserverWorkspaceName;
			String layernames[] = layername.split(":");
			String layer=layername;
			if(layernames.length==2){
				workspace = layernames[0];
				layer = layernames[1];
			}
			
			Map<String,Object>resultMap=new HashMap<String, Object>();
			
			RESTLayer restLayer =geoserverService.getReader().getLayer(workspace, layer);
			if(null==restLayer){
				return Result.error("图层不存在");
			}
			resultMap.put("layername", layername);
			
			if(restLayer.getType() == RESTLayer.Type.VECTOR){
				RESTFeatureType restFeatureType =geoserverService.getFeatureType(restLayer);
				/**
				 * bbox 范围
				 */
				List<Double>bbox=geoserverService.getVectorBbox(restFeatureType);
				resultMap.put("bbox", bbox);
				String crs=restFeatureType.getCRS();
				resultMap.put("crs", crs);
			}else if(restLayer.getType() == RESTLayer.Type.RASTER){
				RESTCoverage restCoverage =geoserverService.getCoverage(restLayer);
				/**
				 * bbox 范围
				 */
				List<Double>bbox=geoserverService.getRasterBbox(restCoverage);
				resultMap.put("bbox", bbox);
				
				String crs=restCoverage.getCRS();
				resultMap.put("crs", crs);
			}
			
			//图层请求地址
			String url=geoserverDomainUrl+"/"+workspace+"/wms?";
			resultMap.put("url", url);
			
			return Result.success("ok",resultMap);
		}catch (Exception e) {
			log.error("图层查询异常", e);
			return Result.error("图层查询异常");
		}
	}
	
	
	/**
	 * 图层查询
	 */
	@At("layergroup/?")
	public Object getLayerGroup(String layername){
		try{
			String workspace=geoserverWorkspaceName;
			String layernames[] = layername.split(":");
			String layer=layername;
			if(layernames.length==2){
				workspace = layernames[0];
				layer = layernames[1];
			}
			
			Map<String,Object>resultMap=new HashMap<String, Object>();
			
			RESTLayerGroup restLayer =geoserverService.getReader().getLayerGroup(workspace, layer);
			if(null==restLayer){
				return Result.error("图层不存在");
			}
			resultMap.put("layername", layername);
			
			/**
			 * bbox 范围
			 */
			List<Double>bbox= new ArrayList<Double>();
			bbox.add(restLayer.getMinX());
			bbox.add(restLayer.getMinY());
			
			bbox.add(restLayer.getMaxX());
			bbox.add(restLayer.getMaxY());
			resultMap.put("bbox", bbox);
			//图层请求地址
			String url=geoserverDomainUrl+"/"+workspace+"/wms?";
			resultMap.put("url", url);
			
			return Result.success("ok",resultMap);
		}catch (Exception e) {
			log.error("图层查询异常", e);
			return Result.error("图层查询异常");
		}
	}
	
	
}
