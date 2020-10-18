package cn.geofound.technology.service.impl;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import cn.geofound.common.utils.title.BoundingBox;
import cn.geofound.technology.dao.BasicGeoHeyDao;
import cn.geofound.technology.service.TitleService;


/**
 * 瓦片service接口 实现
 * @author zhangjialu
 * @date 2019年5月25日 上午9:40:24
 */
@IocBean
public class TitleServiceImpl implements TitleService {

	/**
	 * 基本数据库操作类
	 */
	@Inject
	private BasicGeoHeyDao basicGeoHeyDao;
	
	
	/**
	 * 线图层
	 */
	@Override
	public Object getST_AsMVT(BoundingBox boundingBox) {
		
		/**
		 
		 	SELECT
				ST_AsMVT ( tile, 'polygons', 4096, 'geom' ) AS tile 
			FROM
				(
			SELECT
				w._id,
				ST_AsMVTGeom ( w.the_geom, Box2D ( ST_MakeEnvelope ( - 179.560546875, 84.83422451455142,- 179.47265625, 84.84213194172231, 4326 )), 4096, 0, TRUE ) AS geom 
			FROM
				DATA.t_184bf53030f611e9a5b6db1290f03679 w 
				) tile 
			WHERE
				tile.geom IS NOT NULL  
		
		 
		 */
		String sqlstr="SELECT ST_AsMVT(tile, 'lines', 4096, 'geom') AS tile FROM ( SELECT w._id , ST_AsMVTGeom(w.the_geom, Box2D(ST_MakeEnvelope(@xmin,@ymin,@xmax,@ymax, 4326)), 4096, 0, true) AS geom FROM data.t_c5a04972d9944523991d0e9a930fb0c7 w ) tile WHERE tile.geom IS NOT NULL";
		Sql sql=Sqls.fetchRecord(sqlstr);
		sql.params().set("xmin", boundingBox.west).set("xmax", boundingBox.east).set("ymin", boundingBox.south).set("ymax", boundingBox.north);
		Record  record=basicGeoHeyDao.getDao().execute(sql).getObject(Record.class);
		return record.get("tile");
	}
	
	
	
	
	/**
	 * 点图层
	 * @param boundingBox
	 * @return
	 */
	public Object getST_AsMVT_forPoints(BoundingBox boundingBox){
		String sqlstr="SELECT ST_AsMVT(tile, 'points', 4096, 'geom') AS tile FROM ( SELECT w._id , ST_AsMVTGeom(w.the_geom_webmercator, Box2D(ST_MakeEnvelope(@xmin,@ymin,@xmax,@ymax, 4326)), 4096, 0, true) AS geom FROM data.t_0a4e2ee034e211e9a5b6db1290f03679 w ) tile WHERE tile.geom IS NOT NULL";
		Sql sql=Sqls.fetchRecord(sqlstr);
		sql.params().set("xmin", boundingBox.west).set("xmax", boundingBox.east).set("ymin", boundingBox.south).set("ymax", boundingBox.north);
		Record  record=basicGeoHeyDao.getDao().execute(sql).getObject(Record.class);
		return record.get("tile");
	}
	
	
	/**
	 * 面图层
	 * @param boundingBox
	 * @return
	 */
	public Object getST_AsMVT_forPolygon(BoundingBox boundingBox){//ST_Transform(w.the_geom,4326)
		//String sqlstr="SELECT ST_AsMVT(tile, 'polygons', 4096, 'geom') AS tile FROM ( SELECT w._id , ST_AsMVTGeom(ST_Transform(w.the_geom,4326), Box2D(ST_MakeEnvelope(@xmin,@ymin,@xmax,@ymax, 4326)), 4096, 0, true) AS geom FROM data.t_17138f397ced4c9d9218417de6665f8e w ) tile WHERE tile.geom IS NOT NULL";
		String sqlstr="SELECT ST_AsMVT(tile, 'polygons', 4096, 'geom') AS tile FROM ( SELECT w.district_code , ST_AsMVTGeom(w.the_geom, Box2D(ST_MakeEnvelope(@xmin,@ymin,@xmax,@ymax, 4326)), 4096, 0, true) AS geom FROM district w  ) tile WHERE tile.geom IS NOT NULL";
		
		Sql sql=Sqls.fetchRecord(sqlstr);
		sql.params().set("xmin", boundingBox.west).set("xmax", boundingBox.east).set("ymin", boundingBox.south).set("ymax", boundingBox.north);
		Record  record=basicGeoHeyDao.getDao().execute(sql).getObject(Record.class);
		return record.get("tile");
	}
	
	
	
	/**
	 * 矢量切片获取
	 * @param boundingBox
	 * @return
	 */
	public Object getST_AsMVT_forPolygon(BoundingBox boundingBox,String layername,String dataid){//ST_Transform(w.the_geom,4326)
		String sqlstr="SELECT ST_AsMVT(tile, @layername, 4096, 'geom') AS tile FROM ( SELECT w._id , ST_AsMVTGeom(w.the_geom, Box2D(ST_MakeEnvelope(@xmin,@ymin,@xmax,@ymax, 4326)), 4096, 0, true) AS geom FROM $tableName w  where ST_Intersects(w.the_geom,ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326)) =true or  ST_Touches(w.the_geom,ST_SetSRID(st_geomfromgeojson('$geojsonA'),4326)) =true  ) tile WHERE tile.geom IS NOT NULL";
		
		StringBuffer geomWhereBuffere=new StringBuffer();
		geomWhereBuffere.append("{");
			geomWhereBuffere.append(" \"type\": \"Polygon\",");
				geomWhereBuffere.append(" \"coordinates\": [ [");
					geomWhereBuffere.append(" ["+boundingBox.west+", "+boundingBox.south+"], ");
					geomWhereBuffere.append(" ["+boundingBox.east+", "+boundingBox.south+"], ");
					geomWhereBuffere.append(" ["+boundingBox.east+", "+boundingBox.north+"], ");
					geomWhereBuffere.append(" ["+boundingBox.west+", "+boundingBox.north+"], ");
					geomWhereBuffere.append(" ["+boundingBox.west+", "+boundingBox.south+"] ");
			geomWhereBuffere.append("]]");
		geomWhereBuffere.append("}");
		String geojsonA=geomWhereBuffere.toString();
		Sql sql=Sqls.fetchRecord(sqlstr);
		sql.params().set("layername", layername).set("xmin", boundingBox.west).set("xmax", boundingBox.east).set("ymin", boundingBox.south).set("ymax", boundingBox.north);
		String tableName = "data.t_"+dataid;
		sql.vars().set("tableName", tableName).set("geojsonA", geojsonA);
		Record  record=basicGeoHeyDao.getDao().execute(sql).getObject(Record.class);
		return record.get("tile");
	}

	

}
