package cn.geofound.technology.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.postgis.Point;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.geofound.common.utils.StringUtils;
import cn.geofound.technology.dao.BasicDao;
import cn.geofound.technology.dto.GeojsonPointDTO;
import cn.geofound.technology.service.BluetoothLocationService;
import cn.geofound.technology.service.PlottingService;

/**
 * 
 * @author zhangjialu
 * @date 2019年4月24日 下午10:26:59
 */
@IocBean
public class BluetoothLocationServiceImpl implements BluetoothLocationService {

	/**
	 * 标绘管理service接口
	 */
	@Inject
	private PlottingService plottingService;
	
	
	@Inject
	private BasicDao basicDao;
	
	
	
	@Override
	public String location() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 求俩个圆 相交的俩个点
	 * @param geojsonA 矩形区域面
	 * @param geojsonB 圆形区域面
	 */
	public String getTwoCirclesPoint(Double beacon1x, Double beacon1y, Double d1, Double beacon2x,Double beacon2y, Double d2){
		String pointGeojsonA="{\"type\":\"Point\",\"coordinates\":["+beacon1x+","+beacon1y+"]}";
		String pointGeojsonB="{\"type\":\"Point\",\"coordinates\":["+beacon2x+","+beacon2y+"]}";
		String ciclregeojsonA = plottingService.postgisHandleCircleOneGeojson(pointGeojsonA, d1);//plottingService.postgisHandleFunctionOneGeojson(pointGeojsonA, d1,"ST_Buffer");
		String ciclregeojsonB = plottingService.postgisHandleCircleOneGeojson(pointGeojsonB, d1);//plottingService.postgisHandleFunctionOneGeojson(pointGeojsonB, d2,"ST_Buffer");
		
		
		String boundaryLineA=plottingService.postgisHandleFunctionOneGeojson(ciclregeojsonA, "ST_Boundary");
		String boundaryLineB=plottingService.postgisHandleFunctionOneGeojson(ciclregeojsonB, "ST_Boundary");
		
		//两圆相交于两个点
		String intersectionGeojson=plottingService.postgisHandleFunctionTwoGeojson(boundaryLineA, boundaryLineB, "ST_Intersection");
		return intersectionGeojson;
	}
	
	/**
	 * 求从圆心过的已知直线方程式与圆的方程式相交与弧线一点
	 * @param x1 蓝牙经度
	 * @param y1 蓝牙经度
	 * @param center_x 弧线两点中点中点x
	 * @param center_y 弧线两点中点中点y
	 * @param raduis 人的位置与蓝牙位置圆半径
	 * @return
	 */
	public String arcIntersectionPoint(double beacon1x,double beacon1y,double center_x,double center_y,double raduis){
		//直线 A B C  (求C点)
		//蓝牙
		String geojsonA="{\"type\":\"Point\",\"coordinates\":["+beacon1x+","+beacon1y+"]}";
		String mercatorGeojsonA = plottingService.latlngs2Mercator(geojsonA);
		GeojsonPointDTO geojsonPointDTOA= JSON.parseObject(mercatorGeojsonA, GeojsonPointDTO.class);
		double x1 = geojsonPointDTOA.getCoordinates()[0];
		double y1 = geojsonPointDTOA.getCoordinates()[1];
		
		//弧线两点中点
		String geojsonB="{\"type\":\"Point\",\"coordinates\":["+center_x+","+center_y+"]}";
		String mercatorGeojsonB = plottingService.latlngs2Mercator(geojsonB);
		GeojsonPointDTO geojsonPointDTOB= JSON.parseObject(mercatorGeojsonB, GeojsonPointDTO.class);
		double x2 = geojsonPointDTOB.getCoordinates()[0];
		double y2 = geojsonPointDTOB.getCoordinates()[1];
		
		
		Point curPoint = new Point(x1,y1);// 当前坐标
		Point nextPoint = new Point(x2, y2);// 下个点坐标
		
		double d1 = plottingService.postgisHandleDistanceFunctionTwoGeojson(geojsonA, geojsonB);//getST_distance_sphere(id1, id2);//AB距离
		double d2 = raduis-d1;//BC距离
		
		double _d=Math.pow(raduis, 2)-Math.pow(d2,2);
		
		
		// 第一步：求得直线方程相关参数y=kx+b
		// 两点的坐标距离
		double k = (curPoint.y - nextPoint.y) * 1.0 / (curPoint.x - nextPoint.x);// 坐标直线斜率k
		double b = curPoint.y - k * curPoint.x;// 坐标直线b
		
		// 第二步：求得在直线y=kx+b上，距离当前坐标距离为L的某点
		
		//求弧线中点
		double x3 = (_d-(Math.pow(x1, 2)-Math.pow(x2, 2)+Math.pow(y1, 2)-Math.pow(y2, 2))-2*b*(y2-y1))/((2*x2-2*x1)+(2*k*y2-2*k*y1));
		double y3 = k*x3+b;
		
		String mercatorGeojson="{\"type\":\"Point\",\"coordinates\":["+x3+","+y3+"]}";
		String lonatGeojson = plottingService.mercator2Latlngs(mercatorGeojson);
		GeojsonPointDTO lonlatDTO= JSON.parseObject(lonatGeojson, GeojsonPointDTO.class);
		double lon = lonlatDTO.getCoordinates()[0];
		double lat = lonlatDTO.getCoordinates()[1];
		
		return lon+","+lat;
	}

	@Override
	public String location(Double beacon1x, Double beacon1y, Double d1, String effectiveArea1) {
		//1.获取人距离蓝牙位置的距离形成的圆
		String geojsonA="{\"type\":\"Point\",\"coordinates\":["+beacon1x+","+beacon1y+"]}";
		String ciclregeojson = plottingService.postgisHandleCircleOneGeojson(geojsonA, d1);//plottingService.postgisHandleFunctionOneGeojson(geojsonA, d1,"ST_Buffer");
		//人距离蓝牙位置的距离形成的圆是否在有效区域内部
		boolean is_winthin= plottingService.postgisRelationFunctionBygeojson(ciclregeojson, effectiveArea1, "ST_Within");
		
		//如果人距离蓝牙位置的距离形成的圆在有效区内-则直接返回蓝牙位置
		if(is_winthin){
			return beacon1x+","+beacon1y;
		}
		
		//2.人距离蓝牙位置的距离形成的圆是不在有效区域内部，而是相交的情况、
		//相交会出现两种情况、一段弧（2点交点）；两段弧（4个交点）
		String boundaryLineA=plottingService.postgisHandleFunctionOneGeojson(ciclregeojson, "ST_Boundary");
		String boundaryLineB=plottingService.postgisHandleFunctionOneGeojson(effectiveArea1, "ST_Boundary");
		
		String intersectionGeojson=plottingService.postgisHandleFunctionTwoGeojson(boundaryLineA, boundaryLineB, "ST_Intersection");
		if(StringUtils.isBlank(intersectionGeojson)){
			return null;
		}
		JSONObject intersectionJson=JSON.parseObject(intersectionGeojson);
		String type=intersectionJson.getString("type");
		if(!type.equals("MultiPoint")){
			return null;
		}
		JSONArray coordinates=intersectionJson.getJSONArray("coordinates");
		//两段弧（4个交点）
		if(coordinates.size()==4){
			//{"type":"MultiPoint","coordinates":[[111.210660156396,37.22985],[111.605493718332,34.836209],[115.093649057011,34.836209],[115.467462233299,37.22985]]}
			JSONArray xylist1=coordinates.getJSONArray(0);
			double x1=xylist1.getDoubleValue(0);
			double y1=xylist1.getDoubleValue(1);
			
			JSONArray xylist2=coordinates.getJSONArray(1);
			double x2=xylist2.getDoubleValue(0);
			double y2=xylist2.getDoubleValue(1);
			
			JSONArray xylist3=coordinates.getJSONArray(2);
			double x3=xylist3.getDoubleValue(0);
			double y3=xylist3.getDoubleValue(1);
			
			JSONArray xylist4=coordinates.getJSONArray(3);
			double x4=xylist4.getDoubleValue(0);
			double y4=xylist4.getDoubleValue(1);
			
			List<String>result =new ArrayList<String>();
			//1.1 (x1,y1)与(x2,y2)
			//验证这个点是否在有效区内部
			//根据两点中点坐标公式
			double x = (x1+x2)/2;
			double y= (y1+y2)/2;
			String xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
			boolean xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				result.add(x+","+y);
			}
			
			//1.2 (x2,y2)与(x3,y3)
			x = (x2+x3)/2;
			y= (y2+y3)/2;
			xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
			xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				result.add(x+","+y);
			}
			
			//1.3 (x3,y3)与(x4,y4)
			x = (x3+x4)/2;
			y= (y3+y4)/2;
			xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
			xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				result.add(x+","+y);
			}
			
			//1.4 (x4,y4)与(x1,y1)
			x = (x4+x1)/2;
			y= (y4+y1)/2;
			xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
			xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				result.add(x+","+y);
			}
			//应该只有俩个结果
			if(result.size()!=2){
				return null;
			}
			
			//计算矩形有效区域点质心点
			String centroidGeojson=plottingService.postgisHandleFunctionOneGeojson(ciclregeojson, "ST_Centroid");
			if(StringUtils.isBlank(centroidGeojson)){
				return null;
			}
			
			//计算俩个点距离矩形有效区域点质心点 的距离
			String xy1List=result.get(0);
			String xy1[]=xy1List.split(",");
			String xy1geojson="{\"type\":\"Point\",\"coordinates\":["+xy1[0]+","+xy1[1]+"]}";
			double dis1 = plottingService.postgisHandleDistanceFunctionTwoGeojson(xy1geojson, centroidGeojson);
			String xy2List=result.get(1);
			String xy2[]=xy2List.split(",");
			String xy2geojson="{\"type\":\"Point\",\"coordinates\":["+xy2[0]+","+xy2[1]+"]}";
			double dis2 = plottingService.postgisHandleDistanceFunctionTwoGeojson(xy2geojson, centroidGeojson);
			if(dis1>dis2){
				return xy2List;
			}
			return xy1List;
			
		}else if(coordinates.size()==2){
			//{"type":"MultiPoint","coordinates":[[115.863186142458,29.857282],[115.879051484143,30.29523]]}
			JSONArray xylist1=coordinates.getJSONArray(0);
			double x1=xylist1.getDoubleValue(0);
			double y1=xylist1.getDoubleValue(1);
			
			JSONArray xylist2=coordinates.getJSONArray(1);
			double x2=xylist2.getDoubleValue(0);
			double y2=xylist2.getDoubleValue(1);
			
			
			//根据两点中点坐标公式
			double x = (x1+x2)/2;
			double y= (y1+y2)/2;
			String xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
			//验证这个点是否在有效区内部
			boolean xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				//3.根据直线方程式与圆方程式求直线与圆点交点
				String xy=arcIntersectionPoint(beacon1x, beacon1y, x, y, d1);
				return xy;
			}
			return null;
		}else{
			return null;
		}
		
	}

	@Override
	public String location(Double beacon1x, Double beacon1y, Double d1, String effectiveAreaA, Double beacon2x,
			Double beacon2y, Double d2, String effectiveAreaB) {
		//1.0有效区域
		
		//beacon1.A 与 beacon2.A 相交或者包含
		//1.1 是否相交 是否是重叠 （相交与包含）
		String effectiveArea1="";
		boolean is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveAreaA, effectiveAreaB, "ST_Overlaps");
		if(is_intersects){//如果相交
			//返回两个几何对象的合并结果
			effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveAreaA, effectiveAreaB, "ST_Union");
		}else{
			effectiveArea1=effectiveAreaA;
		}
		
		//beacon1和beacon2之间的距离d12
		String beacon1Geojson="{\"type\":\"Point\",\"coordinates\":["+beacon1x+","+beacon1y+"]}";
		String beacon2Geojson="{\"type\":\"Point\",\"coordinates\":["+beacon2x+","+beacon2y+"]}";
		double d12=plottingService.postgisHandleDistanceFunctionTwoGeojson(beacon1Geojson, beacon2Geojson);
		
		//对d1和d2进行修正
		if(d1>d2){
			d1 = d1 - (d1-d2-d12)*d1/(d1+d2);
			d2 = d2 + (d1-d2-d12)*d2/(d1+d2);
		}else if(d1<d2){
			d2 = d2 - (d2-d1-d12)*d2/(d1+d2);
			d1 = d1 + (d2-d1-d12)*d1/(d1+d2);
		}
		
		if(d12>d1+d2){
			d1 = d1 + (d12-d1-d2)*d1/(d1+d2);
			d2 = d2 + (d12-d1-d2)*d2/(d1+d2);
		}
		
		/**
		 * (x-beacon1.x0)^2+(y-beacon1.y0)^2 = d1^2
		 * (x-beacon2.x0)^2+(y-beacon2.y0)^2 = d2^2
		 * 经过2.3.2的修正之后，此方程组应肯定有解(x1,y1),(x2,y2)。
		 * 
		 * 这里通过postgis 进行求解
		 */
		//获取圆的多边形区域geojson
		String ciclregeojsonA = plottingService.postgisHandleCircleOneGeojson(beacon1Geojson, d1);//plottingService.postgisHandleFunctionOneGeojson(beacon1Geojson, d1,"ST_Buffer");
		String ciclregeojsonB = plottingService.postgisHandleCircleOneGeojson(beacon2Geojson, d1);//plottingService.postgisHandleFunctionOneGeojson(beacon2Geojson, d2,"ST_Buffer");
		
		//提取圆多边形的边界线
		String boundaryLineA=plottingService.postgisHandleFunctionOneGeojson(ciclregeojsonA, "ST_Boundary");
		String boundaryLineB=plottingService.postgisHandleFunctionOneGeojson(ciclregeojsonB, "ST_Boundary");
		
		
		//求俩个圆边界线相交的俩个点
		String intersectionGeojson=plottingService.postgisHandleFunctionTwoGeojson(boundaryLineA, boundaryLineB, "ST_Intersection");
		if(StringUtils.isBlank(intersectionGeojson)){
			return null;
		}
		
		JSONObject intersectionJson=JSON.parseObject(intersectionGeojson);
		String type=intersectionJson.getString("type");
		if(!type.equals("MultiPoint")){
			return null;
		}
		JSONArray coordinates=intersectionJson.getJSONArray("coordinates");
		if(coordinates.size()!=2){
			return null;
		}
		
		JSONArray xylist1=coordinates.getJSONArray(0);
		double x1=xylist1.getDoubleValue(0);
		double y1=xylist1.getDoubleValue(1);
		
		JSONArray xylist2=coordinates.getJSONArray(1);
		double x2=xylist2.getDoubleValue(0);
		double y2=xylist2.getDoubleValue(1);
		
		
		//根据两点中点坐标公式
		double x = (x1+x2)/2;
		double y= (y1+y2)/2;
		String xygeojson="{\"type\":\"Point\",\"coordinates\":["+x+","+y+"]}";
		//验证俩个圆相交的俩个点点中点是否在有效区域以内
		boolean xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
		if(xyWithin){
			return x+","+y;
		}
		//如果不在有效去区域，则看相交x1y1是否在有区域区域以内
		xygeojson="{\"type\":\"Point\",\"coordinates\":["+x1+","+y1+"]}";
		xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
		if(xyWithin){
			return x1+","+y1;
		}
		//看相交x2y2是否在有区域区域以内
		xygeojson="{\"type\":\"Point\",\"coordinates\":["+x2+","+y2+"]}";
		xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
		if(xyWithin){
			return x2+","+y2;
		}
		
		return x+","+y;
		
	}

	@Override
	public String location(Double beacon1x, Double beacon1y, Double d1, String effectiveAreaA, Double beacon2x,
			Double beacon2y, Double d2, String effectiveAreaB, Double beacon3x, Double beacon3y, Double d3,
			String effectiveAreaC) {
		
		//beacon1.A 与 beacon2.A 相交或者包含
		String  effectiveArea1="";
		boolean is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveAreaA, effectiveAreaB, "ST_Overlaps");
		if(is_intersects){//如果相交
			//返回两个几何对象的合并结果
			effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveAreaA, effectiveAreaB, "ST_Union");
			is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveArea1, effectiveAreaC, "ST_Overlaps");
			if(is_intersects){//如果相交
				effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveArea1, effectiveAreaC, "ST_Union");
			}
		}
		is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveAreaA, effectiveAreaC, "ST_Overlaps");
		if(is_intersects){//如果相交
			effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveAreaA, effectiveAreaB, "ST_Union");
		}
		
		
		List<String>result=new ArrayList<String>();
		//求解beacon1+beacon2
		String intersectionGeojsonAB=getTwoCirclesPoint(beacon1x, beacon1y, d1, beacon2x, beacon2y, d2);
		JSONObject intersectionJsonAB=JSON.parseObject(intersectionGeojsonAB);
		JSONArray coordinates=intersectionJsonAB.getJSONArray("coordinates");
		JSONArray xylist1=coordinates.getJSONArray(0);
		double x12_1=xylist1.getDoubleValue(0);
		double y12_1=xylist1.getDoubleValue(1);
		
		JSONArray xylist2=coordinates.getJSONArray(1);
		double x12_2=xylist2.getDoubleValue(0);
		double y12_2=xylist2.getDoubleValue(1);
		
		//2.4.2.2
		if( (Math.pow(x12_1-beacon3x, 2)+Math.pow(y12_1-beacon3y, 2)>Math.pow(d3, 2))  && (Math.pow(x12_2-beacon3x, 2) + Math.pow(y12_2-beacon3y, 2) > Math.pow(d3, 2)) ){
			
			if( ( Math.pow(x12_1-beacon3x, 2)+Math.pow(y12_1-beacon3y, 2) ) < ( Math.pow(x12_2-beacon3x, 2)+Math.pow(y12_2-beacon3y, 2)) ){
				result.add(x12_1+","+y12_1);
			}else{
				result.add(x12_2+","+y12_2);
			}
			
		}else{
			if ( Math.pow(x12_2-beacon3x, 2)+Math.pow(y12_2-beacon3y, 2) < Math.pow(d3, 2) ){
				result.add(x12_2+","+y12_2);
			}else{
				result.add(x12_1+","+y12_1);
			}
		}

		
		
		//求解beacon1+beacon3
		String intersectionGeojsonAC=getTwoCirclesPoint(beacon1x, beacon1y, d1, beacon3x, beacon3y, d3);
		JSONObject intersectionJsonAC=JSON.parseObject(intersectionGeojsonAC);
		coordinates=intersectionJsonAC.getJSONArray("coordinates");
		xylist1=coordinates.getJSONArray(0);
		double x13_1=xylist1.getDoubleValue(0);
		double y13_1=xylist1.getDoubleValue(1);
		
		xylist2=coordinates.getJSONArray(1);
		double x13_2=xylist2.getDoubleValue(0);
		double y13_2=xylist2.getDoubleValue(1);
		
		//2.4.3.2
		if( ( Math.pow(x13_1-beacon2x, 2)+Math.pow(y13_1-beacon3y, 2) > Math.pow(d2, 2) ) && ( Math.pow(x13_2-beacon2x, 2)+Math.pow(y13_2-beacon2y, 2) > Math.pow(d2, 2) ) ){
			
			if( ( Math.pow(x13_1-beacon2x, 2)+Math.pow(y13_1-beacon2y, 2) ) < ( Math.pow(x13_2-beacon2x, 2)+Math.pow(y13_2-beacon2y, 2) ) ){
				result.add(x13_1+","+y13_1);
			}else{
				result.add(x13_2+","+y13_2);
			}
			
		}else{
			if ( Math.pow(x13_2-beacon2x, 2)+Math.pow(y13_2-beacon2y, 2) < Math.pow(d2, 2) ){
				result.add(x13_2+","+y13_2);
			}else{
				result.add(x13_1+","+y13_1);
			}
		}
		
		
		//2.4.4 求解beacon2+beacon3
		String intersectionGeojsonBC=getTwoCirclesPoint(beacon2x, beacon2y, d2, beacon3x, beacon3y, d3);
		JSONObject intersectionJsonBC=JSON.parseObject(intersectionGeojsonBC);
		coordinates=intersectionJsonBC.getJSONArray("coordinates");
		xylist1=coordinates.getJSONArray(0);
		double x23_1=xylist1.getDoubleValue(0);
		double y23_1=xylist1.getDoubleValue(1);
		
		xylist2=coordinates.getJSONArray(1);
		double x23_2=xylist2.getDoubleValue(0);
		double y23_2=xylist2.getDoubleValue(1);
		
		//2.4.4.2
		if( ( Math.pow(x23_1-beacon1x, 2)+Math.pow(y23_1-beacon1y, 2) > Math.pow(d1, 2) ) && ( Math.pow(x23_2-beacon1x, 2)+Math.pow(y23_2-beacon1y, 2) > Math.pow(d1, 2)) ){
			
			
			if( ( Math.pow(x23_1-beacon1x, 2)+Math.pow(y23_1-beacon1y, 2) ) < ( Math.pow(x23_2-beacon1x, 2)+Math.pow(y23_2-beacon1y, 2) ) ){
				result.add(x23_1+","+y23_1);
			}else{
				result.add(x23_2+","+y23_2);
			}
			
		}else{
			if ( Math.pow(x23_2-beacon1x, 2)+Math.pow(y23_2-beacon1y, 2) < Math.pow(d1, 2) ) {
				result.add(x23_2+","+y23_2);
			}else{
				result.add(x23_1+","+y23_1);
			}
		}
		
		//2.4.5 点筛选
		List<String>resultNew=new ArrayList<String>();
		for(String point:result){
			String xys[]=point.split(",");
			String xygeojson="{\"type\":\"Point\",\"coordinates\":["+xys[0]+","+xys[1]+"]}";
			boolean xyWithin = plottingService.postgisRelationFunctionBygeojson(xygeojson, effectiveArea1, "ST_Within");
			if(xyWithin){
				resultNew.add(point);
			}
		}
		
		
		//2.4.6 求平均
		double x_total=0d;
		double y_total=0d;
		for(String point:resultNew){
			String xys[]=point.split(",");
			x_total+=Double.parseDouble(xys[0]);
			y_total+=Double.parseDouble(xys[1]);
		}
		double x =x_total/resultNew.size();
		double y = y_total/resultNew.size();
		return x+","+y;
	}
	
	
	
	
	
	
	/**
	 * 三个蓝牙设备定位算法，没有参考说明，根据三个圆相交的区域为三点区域
	 * @param beacon1x
	 * @param beacon1y
	 * @param d1
	 * @param effectiveAreaA
	 * @param beacon2x
	 * @param beacon2y
	 * @param d2
	 * @param effectiveAreaB
	 * @param beacon3x
	 * @param beacon3y
	 * @param d3
	 * @param effectiveAreaC
	 * @return
	 */
	public String location2(Double beacon1x, Double beacon1y, Double d1, String effectiveAreaA, Double beacon2x,
			Double beacon2y, Double d2, String effectiveAreaB, Double beacon3x, Double beacon3y, Double d3,
			String effectiveAreaC) {
		
		//beacon1.A 与 beacon2.A 相交或者包含
		String  effectiveArea1="";
		boolean is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveAreaA, effectiveAreaB, "ST_Overlaps");
		if(is_intersects){//如果相交
			//返回两个几何对象的合并结果
			effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveAreaA, effectiveAreaB, "ST_Union");
			is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveArea1, effectiveAreaC, "ST_Overlaps");
			if(is_intersects){//如果相交
				effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveArea1, effectiveAreaC, "ST_Union");
			}
		}
		is_intersects = plottingService.postgisRelationFunctionBygeojson(effectiveAreaA, effectiveAreaC, "ST_Overlaps");
		if(is_intersects){//如果相交
			effectiveArea1=plottingService.postgisHandleFunctionTwoGeojson(effectiveAreaA, effectiveAreaB, "ST_Union");
		}
		
		//距离蓝牙1的圆的边界线
		String beacon1Geojson="{\"type\":\"Point\",\"coordinates\":["+beacon1x+","+beacon1y+"]}";
		String ciclregeojsonA = plottingService.postgisHandleCircleOneGeojson(beacon1Geojson, d1);//plottingService.postgisHandleFunctionOneGeojson(beacon1Geojson, d1,"ST_Buffer");
		
		
		//距离蓝牙2的圆的边界线
		String beacon2Geojson="{\"type\":\"Point\",\"coordinates\":["+beacon2x+","+beacon2y+"]}";
		String ciclregeojsonB = plottingService.postgisHandleCircleOneGeojson(beacon2Geojson, d1);// plottingService.postgisHandleFunctionOneGeojson(beacon2Geojson, d2,"ST_Buffer");
		
		
		//距离蓝牙3的圆的边界线
		String beacon3Geojson="{\"type\":\"Point\",\"coordinates\":["+beacon3x+","+beacon3y+"]}";
		String ciclregeojsonC = plottingService.postgisHandleCircleOneGeojson(beacon3Geojson, d1);//plottingService.postgisHandleFunctionOneGeojson(beacon3Geojson, d3,"ST_Buffer");
		
		//求俩个圆边界线相交的面
		String intersectionGeojson=plottingService.postgisHandleFunctionTwoGeojson(ciclregeojsonA, ciclregeojsonB, "ST_Intersection");
		if(StringUtils.isBlank(intersectionGeojson)){
			return null;
		}
		//求俩个圆边界线相交的面
		String intersectionGeojson2=plottingService.postgisHandleFunctionTwoGeojson(intersectionGeojson, ciclregeojsonC, "ST_Intersection");
		if(StringUtils.isBlank(intersectionGeojson)){
			return null;
		}
		
		String centerPointJson=plottingService.postgisHandleFunctionOneGeojson(intersectionGeojson2, "ST_Centroid");
		if(StringUtils.isBlank(centerPointJson)){
			return null;
		}	
		JSONObject centerPoint=JSON.parseObject(centerPointJson);
		String type = centerPoint.getString("type");
		JSONArray coordinates=centerPoint.getJSONArray("coordinates");
		JSONArray xylist1=coordinates.getJSONArray(0);
		double x1=xylist1.getDoubleValue(0);
		double y1=xylist1.getDoubleValue(1);
		return x1+","+y1;
	}

	
	

}
