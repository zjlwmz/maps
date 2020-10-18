package cn.geofound.technology.service;

import java.util.List;

import cn.geofound.framework.base.service.BaseService;
import cn.geofound.technology.dto.GeojsonResultDTO;
import cn.geofound.technology.entity.Plotting;

/**
 * 标绘管理service接口
 * @author zhangjialu
 */
public interface PlottingService extends BaseService<Plotting>{

	/**
	 * 添加标绘
	 * @param plotting
	 * @return
	 */
	public Plotting insertPlotting(Plotting plotting,String geojson);
	
	
	/**
	 * 更新标绘
	 * @param plotting
	 * @return
	 */
	public int updatePlotting(Plotting plotting,String geojson);
	
	
	/**
	 * 标绘列表查询
	 * @return
	 */
	public List<Plotting>findListPlotting();
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public int deleteById(String id);
	
	/**
	 * 获取标绘对象
	 * @param id
	 * @return
	 */
	public Plotting getPlottingById(String id);
	
	/**
	 * 关系函数 postgis
	 * @return
	 */
	public Boolean relationPostgisFunction(String id1,String id2,String function);
	
	
	
	/**
	 * 几何对象处理函数 postgis
	 * @return
	 */
	public Plotting processingPostgisFunction(String id1,String id2,String function);


	/**
	 * 几何对象处理函数 获取圆的多边形
	 * @param a
	 * @return
	 */
	public Plotting getCircle(String a);
	
	/**
	 * 几何对象处理函数 【缓冲区】
	 * @param a
	 * @return
	 */
	public Plotting processingBufferPostgis(String a,Double raduis);
	
	
	/**
	 * 几何对象处理函数 【合并为线】 MultiLineString to  LineString
	 * @param a
	 * @return
	 */
	public Plotting processingLineMergePostgis(String a);
	
	/**
	 * 几何函数存取函数
	 */
	public Plotting accessFunctionPostgis(String id,String methond);
	
	/**
	 * 几何函数存取函数
	 */
	public Plotting accessFunctionPostgis(String a,String b,String methond);
	
	
	/**
	 * [蓝牙算法测试]
	 * 已知两点坐标，求直线方程、距离其中一点距离为L的某点
	 * 已知直线方程式、求距离为d的坐标点
	 */
	public Plotting getLinearEquationAndDistince(String a,String b);

	/**
	 * 多面转单面
	 * @param a
	 * @param b
	 * @return
	 */
	public List<Plotting> multipolygon2polygon(String id);
	
	
	
	//--------------------以下接口数据采用geojson来处理--------------------
	
	/**
	 * 几何对象关系函数
	 * @param geojsonA
	 * @param geojsonB
	 * @param methond
	 * @return
	 */
	public Boolean postgisRelationFunctionBygeojson(String geojsonA,String geojsonB,String methond);
	

	/**
	 * 几何对象处理函数【单个几何对象】
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionOneGeojson(String geojsonA,String methond);
	
	
	/**
	 * 获取缓冲区面多边形
	 * @param a
	 * @return
	 */
	public String postgisHandleCircleOneGeojson(String geojsonA,Double raduis);
	
	
	/**
	 * 几何对象处理函数【单个几何对象】
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionOneGeojson(String geojsonA,Object value,String methond);
	
	/**
	 * 几何对象处理函数【两个几何对象】
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionTwoGeojson(String geojsonA,String geojsonB,String methond);
	
	
	/**
	 * 两点距离计算
	 */
	public double postgisHandleDistanceFunctionTwoGeojson(String geojsonA,String geojsonB);
	
	
	/**
	 * 多面转单面
	 * @param a
	 * @param b
	 * @return
	 */
	public List<GeojsonResultDTO> postgisMultipolygon2polygon(String geojsonA);
	
	
	
	/**
	 * 几何数据拆分
	 * 如使用线拆分成俩个面
	 */
	public List<Plotting>postgisGeometrySplit(String geojsonA,String goejsonB);
	
	/**
	 * 经纬度2墨卡托
	 * @param geojsonA
	 * @return
	 */
	public String latlngs2Mercator(String geojsonA);
	
	
	/**
	 * 墨卡托转经纬度
	 */
	public String mercator2Latlngs(String geojsonA);

	
}
