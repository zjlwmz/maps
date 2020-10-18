package cn.geofound.technology.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.postgis.Point;

import com.alibaba.fastjson.JSON;

import cn.geofound.common.utils.IdGen;
import cn.geofound.common.utils.StringUtils;
import cn.geofound.framework.base.service.impl.BaseServiceImpl;
import cn.geofound.technology.dto.GeojsonPointDTO;
import cn.geofound.technology.dto.GeojsonResultDTO;
import cn.geofound.technology.dto.PlottingGeojsonDTO;
import cn.geofound.technology.entity.Plotting;
import cn.geofound.technology.service.PlottingService;

/**
 * 标绘管理service接口实现
 * @author zhangjialu
 * @date 2019年4月20日 下午7:04:57
 */
@IocBean(args = {"refer:dao"})
public class PlottingServiceImpl extends BaseServiceImpl<Plotting> implements PlottingService{

	public PlottingServiceImpl(Dao dao) {
		super(dao);
	}

	@Override
	public Plotting insertPlotting(Plotting plotting,String geojson) {
		String intoSql="insert into data.geo_plotting(id,name,geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date) values(@id,@name,ST_SetSRID(st_geomfromgeojson('$geojson'),4326),@geojson,@properties,@type,$radius,@latLngs,@remarks,@createDate,@updateDate)";
		Sql sql=Sqls.create(intoSql);
		String id = IdGen.uuid();
		sql.params().set("id", IdGen.uuid()).set("name", plotting.getName()).set("geojson", geojson).set("properties", StringUtils.EMPTY).set("type", plotting.getType()).set("latLngs", plotting.getLatLngs()).set("remarks", plotting.getRemarks()).set("createDate", plotting.getCreateDate()).set("updateDate", plotting.getUpdateDate());
		sql.vars().set("geojson", geojson).set("radius", plotting.getRadius());
		dao().execute(sql);
		plotting.setId(id);
		return plotting;
	}

	@Override
	public int updatePlotting(Plotting plotting,String geojson) {
		//String updateSql="update data.geo_plotting set name=@name,geom = st_geomfromgeojson('$geojson'),geojson=@geojson,properties=@properties,radius=@radius,lat_lngs=@latLngs,remarks=@remarks,create_date=@createDate,update_date=@updateDate where id=@id";
		
		StringBuffer updateSqlBuffere=new StringBuffer();
		updateSqlBuffere.append(" update data.geo_plotting set ");
		if(StringUtils.isNotBlank(plotting.getName())){
			updateSqlBuffere.append(" name=@name, ");
		}
		
		if(StringUtils.isNotBlank(geojson)){
			updateSqlBuffere.append(" geom = ST_SetSRID(st_geomfromgeojson('$geojson'),4326), ");
			updateSqlBuffere.append(" geojson=@geojson, ");
		}
		
		if(StringUtils.isNotBlank(plotting.getProperties())){
			updateSqlBuffere.append(" properties=@properties, ");
		}
		
		if(null!=plotting.getRadius()){
			updateSqlBuffere.append(" radius=@radius, ");
		}
		
		if(StringUtils.isNotBlank(plotting.getLatLngs())){
			updateSqlBuffere.append(" lat_lngs=@latLngs, ");
		}
		
		if(StringUtils.isNotBlank(plotting.getRemarks())){
			updateSqlBuffere.append(" remarks=@remarks, ");
		}
		
		updateSqlBuffere.append(" create_date=@updateDate ");
		
		updateSqlBuffere.append(" where id=@id");
		String updateSql =  updateSqlBuffere.toString();
		Sql sql=Sqls.create(updateSql);
		sql.params().set("id",plotting.getId()).set("name", plotting.getName()).set("geojson", geojson).set("properties", StringUtils.EMPTY).set("type", plotting.getType()).set("latLngs", plotting.getLatLngs()).set("remarks", plotting.getRemarks()).set("createDate", plotting.getCreateDate()).set("updateDate", plotting.getUpdateDate());
		sql.vars().set("geojson", geojson).set("radius", plotting.getRadius());
		dao().execute(sql);
		return 1;
	}
	

	@Override
	public List<Plotting> findListPlotting() {
		String sqllistjson="select id,name,st_asgeojson(geom) as geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date from data.geo_plotting";
		Sql sql=Sqls.create(sqllistjson);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		return sql.getList(Plotting.class);
	}

	@Override
	public int deleteById(String id) {
		String idsList[] = id.split(",");
		for(String plottingid :idsList){
			if(StringUtils.isNotBlank(plottingid)){
				String deleteSql="delete from data.geo_plotting where id=@id";
				Sql sql=Sqls.create(deleteSql);
				sql.params().set("id", plottingid);
				dao().execute(sql);
			}
		}
		
		return 0;
	}

	
	
	/**
	 * 获取标绘对象
	 * @param id
	 * @return
	 */
	public Plotting getPlottingById(String id){
		String sqllistjson="select id,name,st_asgeojson(geom) as geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date from data.geo_plotting where id=@id";
		Sql sql=Sqls.create(sqllistjson);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		List<Plotting>list  = sql.getList(Plotting.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	public Plotting getWebMercatorPlottingById(String id){
		String sqllistjson="select id,name,st_asgeojson(st_transform(geom,4527)) as geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date from data.geo_plotting where id=@id";
		Sql sql=Sqls.create(sqllistjson);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		List<Plotting>list  = sql.getList(Plotting.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 坐标转换
	 */
	public PlottingGeojsonDTO coordinateTransformation(String geojson){
		String sqlstr="SELECT st_asgeojson(st_transform(ST_SetSRID(st_geomfromgeojson('$geojson'),4527),4326)) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojson", geojson);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO>list  = sql.getList(PlottingGeojsonDTO.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	@Override
	public Boolean relationPostgisFunction(String id1, String id2, String function) {
		/**
		 with get_cicle as (
			SELECT * from gis_area where id='200'
		),
		get_rect as (
			SELECT * from gis_area where id='100'
		),
		get_cicle_rect_jiaoji as(
			select ST_Intersection(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2
		)
		select st_asgeojson(geom) from get_cicle_rect_jiaoji
		 */
		
		String sqlstr=" with get_cicle as ( "+
						" SELECT * from data.geo_plotting where id=@id1 "+
						" ), "+
						" get_rect as ( "+
						" SELECT * from data.geo_plotting where id=@id2 "+
						" ), "+
						" get_cicle_rect_jiaoji as( "+
						" select $function(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2 "+
						" ) "+
						" select st_asgeojson(geom) as geojson from get_cicle_rect_jiaoji ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id1", id1).set("id2", id2);
		sql.vars().set("function", function);
		sql.setCallback(Sqls.callback.bool());
		sql.setEntity(dao().getEntity(Boolean.class));
		dao().execute(sql);
		return sql.getBoolean();
	}
	
	
	
	/**
	 * 几何对象处理函数 postgis
	 * @return
	 */
	public Plotting processingPostgisFunction(String id1,String id2,String function){
		/**
		 with get_cicle as (
			SELECT 
				case when type='circle' then st_buffer(geom,radius) else geom
			 from gis_area where id='200'
		),
		get_rect as (
			SELECT 
			case when type='circle' then st_buffer(geom,radius) else geom
			 from gis_area where id='100'
		),
		get_cicle_rect_jiaoji as(
			select ST_Intersection(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2
		)
		select st_asgeojson(geom) from get_cicle_rect_jiaoji
		 */
		
		String sqlstr=" with get_cicle as ( "+
						" SELECT case when type='circle' then ST_Transform(ST_Buffer(ST_Transform(ST_Transform(geom,4527),4527),radius),4326) else geom end "+
						" from data.geo_plotting where id=@id1 "+
						" ), "+
						" get_rect as ( "+
						" SELECT case when type='circle' then ST_Transform(ST_Buffer(ST_Transform(ST_Transform(geom,4527),4527),radius),4326) else geom end "+
						 "from data.geo_plotting where id=@id2 "+
						" ), "+
						" get_cicle_rect_jiaoji as( "+
						" select $function(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2 "+
						" ) "+
						" select st_asgeojson(geom) as geom from get_cicle_rect_jiaoji ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id1", id1).set("id2", id2);
		sql.vars().set("function", function);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO> list= sql.getList(PlottingGeojsonDTO.class);
		
		Plotting plotting =new Plotting();
		if(list.size()>0){
			PlottingGeojsonDTO geojsonDTO = list.get(0);
			plotting.setGeom(geojsonDTO.getGeom());
			plotting.setName(function+"计算");
			return plotting;
		}
		return null;
	}
	
	
	
	
	/**
	 * 几何对象处理函数  获取圆的多边形
	 * @param a
	 * @return
	 */
	public Plotting getCircle(String id){
		//ST_Transform(ST_Buffer(ST_Transform(ST_Transform(geom,4527),4527),22.862),4326)
		String sqlstr="SELECT st_asgeojson(ST_Transform(ST_Buffer(ST_Transform(ST_Transform(geom,4527),4527),radius),4326)) as geom from data.geo_plotting where id=@id ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO> list= sql.getList(PlottingGeojsonDTO.class);
		
		Plotting plottingBuffere =new Plotting();
		if(list.size()>0){
			PlottingGeojsonDTO geojsonDTO = list.get(0);
			plottingBuffere.setGeom(geojsonDTO.getGeom());
			plottingBuffere.setName(" ST_Buffer 计算");
			return plottingBuffere;
		}
		return null;
	}
	
	/**
	 * 几何对象处理函数 【缓冲区】
	 * @param a
	 * @return
	 */
	public Plotting processingBufferPostgis(String id,Double radius){
		//ST_Transform(ST_Buffer(ST_Transform(ST_Transform(geom,4527),4527),22.862),4326)
		String sqlstr="SELECT st_asgeojson(ST_Transform(ST_Buffer(ST_Transform(geom,4527),"+radius+"),4527)) as geom from data.geo_plotting where id=@id ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO> list= sql.getList(PlottingGeojsonDTO.class);
		
		Plotting plottingBuffere =new Plotting();
		if(list.size()>0){
			PlottingGeojsonDTO geojsonDTO = list.get(0);
			plottingBuffere.setGeom(geojsonDTO.getGeom());
			plottingBuffere.setName(" ST_Buffer 计算");
			return plottingBuffere;
		}
		return null;
	}
	

	/**
	 * 根据经纬度点计算在地球曲面上的距离，单位米，地球半径取值6370986米 
	 */
	public Double getST_distance_sphere(String id1,String id2){
		String sqlstr=" with get_cicle as ( "+
				" SELECT * from data.geo_plotting where id=@id1 "+
				" ), "+
				" get_rect as ( "+
				" SELECT * from data.geo_plotting where id=@id2 "+
				" ), "+
				" get_cicle_rect_jiaoji as( "+
				" select ST_distance_sphere(t1.geom,t2.geom) as dis from get_cicle t1 ,get_rect t2 "+
				" ) "+
				" select dis from get_cicle_rect_jiaoji";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id1", id1).set("id2", id2);
		sql.setCallback(Sqls.callback.doubleValue());
		
		String sourceSql = sql.toString();
		double dis = dao().execute(Sqls.fetchDouble(sourceSql)).getDouble();
		return dis;
	}
	
	
	
	/**
	 * 几何对象处理函数 【合并为线】 MultiLineString to  LineString
	 * @param a
	 * @return
	 */
	public Plotting processingLineMergePostgis(String id){
		String sqlstr="select id,name,st_asgeojson(ST_LineMerge(geom)) as geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date from data.geo_plotting where id=@id";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		List<Plotting> list = sql.getList(Plotting.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	/**
	 * 几何函数存取函数
	 */
	public Plotting accessFunctionPostgis(String id,String function){
		String sqlstr="select id,name,st_asgeojson($function(geom)) as geom,geojson,properties,type,radius,lat_lngs,remarks,create_date,update_date from data.geo_plotting where id=@id";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.vars().set("function", function);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		List<Plotting> list = sql.getList(Plotting.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 几何函数存取函数
	 */
	public Plotting accessFunctionPostgis(String id1,String id2,String function){
		/**
		 with get_cicle as (
			SELECT * from gis_area where id='210'
		),
		get_rect as (
			SELECT * from gis_area where id='100'
		),
		get_cicle_rect_jiaoji as(
			select ST_SymDifference(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2
		)
		select st_asgeojson(geom) from get_cicle_rect_jiaoji
		 */
		
		
		String sqlstr=" with get_cicle as ( "+
					" SELECT * from data.geo_plotting where id=@id1 "+
					" ), "+
					" get_rect as ( "+
					" SELECT * from data.geo_plotting where id=@id2 "+
					" ), "+
					" get_cicle_rect_jiaoji as( "+
					" select ST_MakeLine(t1.geom,t2.geom) as geom from get_cicle t1 ,get_rect t2 "+
					" ) "+
					" select st_asgeojson(geom) as geom from get_cicle_rect_jiaoji";
		
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id1", id1).set("id2", id2);
		sql.vars().set("function", function);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO> list= sql.getList(PlottingGeojsonDTO.class);
		
		Plotting plotting =new Plotting();
		if(list.size()>0){
			PlottingGeojsonDTO geojsonDTO = list.get(0);
			plotting.setGeom(geojsonDTO.getGeom());
			plotting.setName(function+"计算");
			return plotting;
		}
		return null;
	}
	
	
	
	/**
	 * 已知两点坐标，求直线方程、距离其中一点距离为L的某点
	 * 已知直线方程式、求距离为d的坐标点
	 * @param id1 蓝牙圆心位置
	 * @param id2 弧线连接线中点位置
	 */
	public Plotting getLinearEquationAndDistince(String id1,String id2){
			Plotting plotting = new Plotting();
			Plotting cur = getWebMercatorPlottingById(id1);
			GeojsonPointDTO geojsonPointDTO1= JSON.parseObject(cur.getGeom().toString(), GeojsonPointDTO.class);
			double x1 = geojsonPointDTO1.getCoordinates()[0];
			double y1 = geojsonPointDTO1.getCoordinates()[1];
			
			Plotting next = getWebMercatorPlottingById(id2);
			
			GeojsonPointDTO geojsonPointDTO2= JSON.parseObject(next.getGeom().toString(), GeojsonPointDTO.class);
			double x2 = geojsonPointDTO2.getCoordinates()[0];
			double y2 = geojsonPointDTO2.getCoordinates()[1];
			
			
			Point curPoint = new Point(x1,y1);// 当前坐标
			Point nextPoint = new Point(x2, y2);// 下个点坐标
			
			//蓝牙圆半径
			double raduis =cur.getRadius();
			
			//直线 A B C  (求C点)
			//圆心到中点距离
			double d1 = getST_distance_sphere(id1, id2);//AB距离
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
			String geojson="{\"coordinates\":["+x3+","+y3+"],\"type\":\"Point\"}";
			PlottingGeojsonDTO plottingGeojsonDTO=coordinateTransformation(geojson);
			if(null!=plottingGeojsonDTO){
				plotting.setGeom(plottingGeojsonDTO.getGeom());
			}
			plotting.setName("弧线上点求解");
			plotting.setType("Point");
			return plotting;
			/*
			double distance = Math.sqrt(Math.pow(curPoint.x - nextPoint.x, 2)
					+ Math.pow(curPoint.y - nextPoint.y, 2));// 两点的坐标距离
			double lenthUnit = distance / 5;// 单位长度
			// 第一步：求得直线方程相关参数y=kx+b
			double k = (curPoint.y - nextPoint.y) * 1.0
					/ (curPoint.x - nextPoint.x);// 坐标直线斜率k
			double b = curPoint.y - k * curPoint.x;// 坐标直线b
			// 第二步：求得在直线y=kx+b上，距离当前坐标距离为L的某点
			// 一元二次方程Ax^2+Bx+C=0中,
			// 一元二次方程求根公式：
			// 两根x1,x2= [-B±√(B^2-4AC)]/2A
			// ①(y-y0)^2+(x-x0)^2=L^2;
			// ②y=kx+b;
			// 式中x,y即为根据以上lenthUnit单位长度(这里就是距离L)对应点的坐标
			// 由①②表达式得到:(k^2+1)x^2+2[(b-y0)k-x0]x+[(b-y0)^2+x0^2-L^2]=0
			double A = Math.pow(k, 2) + 1;// A=k^2+1;
			double B = 2 * ((b - curPoint.y) * k - curPoint.x);// B=2[(b-y0)k-x0];
			int m = 1;
			double L = m * lenthUnit;
			// C=(b-y0)^2+x0^2-L^2
			double C = Math.pow(b - curPoint.y, 2) + Math.pow(curPoint.x, 2)
					- Math.pow(L, 2);
			// 两根x1,x2= [-B±√(B^2-4AC)]/2A
			double x1 = (-B + Math.sqrt(Math.pow(B, 2) - 4 * A * C)) / (2 * A);
			double x2 = (-B - Math.sqrt(Math.pow(B, 2) - 4 * A * C)) / (2 * A);
			double x = 0;// 最后确定是在已知两点之间的某点
			if (x1 == x2) {
				x = x1;
			} else if (curPoint.x <= x1 && x1 <= nextPoint.x || nextPoint.x <= x1
					&& x1 <= curPoint.x) {
				x = x1;
			} else if (curPoint.x <= x2 && x2 <= nextPoint.x || nextPoint.x <= x2
					&& x2 <= curPoint.x) {
				x = x2;
			}
			double y = k * x + b;
			Point mPoint = new Point((int) x, (int) y);
			*/
			
	}
	
	
	/**
	 * 多面转单面
	 * @param a
	 * @param b
	 * @return
	 */
	public List<Plotting> multipolygon2polygon(String id){
		//SELECT st_asgeojson((ST_DUMP(geom)).geom::geometry(Polygon)) AS geom FROM data.geo_plotting where id='5fc67038738244f0b6f2cb14295f2087'
		String sqlstr="SELECT st_asgeojson((ST_DUMP(geom)).geom::geometry(Polygon)) AS geom FROM data.geo_plotting where id=@id ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(PlottingGeojsonDTO.class));
		dao().execute(sql);
		List<PlottingGeojsonDTO> list= sql.getList(PlottingGeojsonDTO.class);
		List<Plotting>plottingList=new ArrayList<Plotting>();
		for(PlottingGeojsonDTO plottingGeojsonDTO:list){
			Plotting plotting =new Plotting();
			plotting.setGeom(plottingGeojsonDTO.getGeom());
			plotting.setType("Polygon");
			plotting.setName(" ST_Buffer 计算");
			plottingList.add(plotting);
		}
		return plottingList;
	}
	
	public Boolean postgisRelationFunctionBygeojson(String geojsonA,String geojsonB,String methond){
		String sqlstr="select $function(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),ST_SetSRID(st_geomfromgeojson('$geojsonB'),4326))";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("function", methond).set("geojsonA", geojsonA).set("geojsonB", geojsonB);
		
		String sourceSql = sql.toString();
		String booeanstr = dao().execute(Sqls.fetchString(sourceSql)).getString();
		if(StringUtils.isNotBlank(booeanstr) && booeanstr.equals("t")){
			return true;
		}
		return false;
		
		
	}
	
	
	/**
	 * 处理函数
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionOneGeojson(String geojsonA,String methond){
		String sqlstr="select st_asgeojson($function(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326))) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("function", methond).set("geojsonA", geojsonA);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
	
	/**
	 * 获取缓冲区面多边形
	 * @param a
	 * @return
	 */
	public String postgisHandleCircleOneGeojson(String geojsonA,Double raduis){
		String sqlstr="SELECT st_asgeojson(ST_Transform(ST_Buffer(ST_Transform(ST_Transform(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),4527),4527),$radius),4326)) as geom ";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojsonA", geojsonA).set("radius", raduis);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
	
	/**
	 * 处理函数
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionOneGeojson(String geojsonA,Object value,String methond){
		String sqlstr="select st_asgeojson($function(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),@value)) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojsonA", geojsonA).set("function", methond);
		sql.params().set("value", value);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
	
	/**
	 * 处理函数
	 * @param geojsonA
	 * @param methond
	 * @return
	 */
	public String postgisHandleFunctionTwoGeojson(String geojsonA,String geojsonB,String methond){
		String sqlstr="select st_asgeojson($function(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),ST_SetSRID(st_geomfromgeojson('$geojsonB'),4326))) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("function", methond).set("geojsonA", geojsonA).set("geojsonB", geojsonB);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
	
	/**
	 * 两点距离计算
	 */
	public double postgisHandleDistanceFunctionTwoGeojson(String geojsonA,String geojsonB){
		String sqlstr="SELECT ST_distance_sphere(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),ST_SetSRID(st_geomfromgeojson('$geojsonB'),4326)) as dis ";
		//String sqlstr="select ST_distance_sphere(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),ST_SetSRID(st_geomfromgeojson('$geojsonB'),4326)) as dis";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojsonA", geojsonA).set("geojsonB", geojsonB);
		sql.setCallback(Sqls.callback.doubleValue());
		
		String sourceSql = sql.toString();
		double dis = dao().execute(Sqls.fetchDouble(sourceSql)).getDouble();
		return dis;
	}
	
	/**
	 * 多面转单面
	 * @param a
	 * @param b
	 * @return
	 */
	public List<GeojsonResultDTO> postgisMultipolygon2polygon(String geojsonA){
		String sqlstr="SELECT st_asgeojson((ST_DUMP(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326))).geom::geometry(Polygon)) AS geojson  ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("geojsonA", geojsonA);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(GeojsonResultDTO.class));
		dao().execute(sql);
		List<GeojsonResultDTO> list= sql.getList(GeojsonResultDTO.class);
		return list;
	}
	
	
	/**
	 * 多面转单面
	 * @param a
	 * @param b
	 * @return
	 */
	public List<GeojsonResultDTO> postgisMultipolygon2polygon2(String geojsonA,String goejsonB){
		
		String sqlstr="SELECT (ST_DUMP(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326))).geom::geometry(Polygon) AS geojson  ";
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("geojsonA", geojsonA);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(GeojsonResultDTO.class));
		dao().execute(sql);
		List<GeojsonResultDTO> list= sql.getList(GeojsonResultDTO.class);
		return list;
	}
	
	
	/**
	 * 几何数据拆分
	 * 如使用线拆分成俩个面
	 */
	public List<Plotting>postgisGeometrySplit(String id1,String id2){
		/**
		 WITH get_line as (
			select t1.geom from data.geo_plotting t1 where t1.id='86a70e66ef72460f9cd7701d1c57ff61'
		),
		get_poygon as (
			select t2.geom from data.geo_plotting t2 where t2.id='a9f758e33ec14bcbb346e1252dd8291b'
		),
		get_list_split as(
			-- select  st_dump(st_split(t4.geom,t3.geom)) as geom from get_line t3,get_poygon t4
			select st_asgeojson((ST_DUMP(st_split(t4.geom,t3.geom))).geom::geometry) AS geom from get_line t3,get_poygon t4
		)
		select  geom from get_list_split

		 */
		
		String sqlstr=" WITH get_line as ( "+
			" select t1.geom from data.geo_plotting t1 where t1.id=@idB "+
		" ), "+
		" get_poygon as ( "+
			" select t2.geom from data.geo_plotting t2 where t2.id=@idA "+
		" ), "+
		" get_list_split as( "+
			" select st_asgeojson((ST_DUMP(st_split(t4.geom,t3.geom))).geom::geometry) AS geom from get_line t3,get_poygon t4 "+
		" ) "+
		" select  geom from get_list_split ";
		
		Sql sql=Sqls.create(sqlstr);
		sql.params().set("idB", id2).set("idA", id1);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(Plotting.class));
		dao().execute(sql);
		List<Plotting> list= sql.getList(Plotting.class);
		return list;
		
	}
	
	
	
	
	
	/**
	 * 经纬度转墨卡托
	 * @param id
	 * @return
	 */
	public String latlngs2Mercator(String geojsonA){
		String sqlstr="select st_asgeojson(st_transform(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326),4527)) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojsonA", geojsonA);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
	
	
	/**
	 * 墨卡托转经纬度
	 */
	public String mercator2Latlngs(String geojsonA){
		String sqlstr="SELECT st_asgeojson(st_transform(ST_SetSRID(st_geomfromgeojson('$geojsonA'),4527),4326)) as geom";
		Sql sql=Sqls.create(sqlstr);
		sql.vars().set("geojsonA", geojsonA);
		
		String sourceSql = sql.toString();
		String geojson = dao().execute(Sqls.fetchString(sourceSql)).getString();
		return geojson;
	}
}
