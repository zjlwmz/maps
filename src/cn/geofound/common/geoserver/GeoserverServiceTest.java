package cn.geofound.common.geoserver;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTBoundingBox;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.alibaba.fastjson.JSON;
/**
 * geoserver rest 服务接口
 * @author zhangjialu
 */
public class GeoserverServiceTest {

	public static String RESTURL = "http://192.168.1.114:8787/geoserver";
	public static String RESTUSER = "admin";
	public static String RESTPW = "geoserver";
	
	public static URL URL;
	public static GeoServerRESTManager manager;
	public static GeoServerRESTReader reader;
	public static GeoServerRESTPublisher publisher;
	static {
		try {
			URL = new URL(RESTURL);
			manager = new GeoServerRESTManager(URL, RESTUSER, RESTPW);
			reader = manager.getReader();
			publisher = manager.getPublisher();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	
	//创建工作空间
    public static void createWorkSpace() throws Exception{
        boolean created = publisher.createWorkspace("myWorkspace");
        System.out.println(created);
    }

    //罗列工作空间
    public static void showWorkSpace() throws Exception{
        RESTLayerList list = reader.getLayers();
        System.out.println(list.get(0).getName());
    }

    
    //罗列已发布shp
    //发布tiff数据
    @SuppressWarnings("deprecation")
	public static void publishTIFF()throws Exception{
    	//G:/tif/haihe-terra_MODIS-1km_20180101_qn.tif
    	//E:/1/33/haihe-terra_MODIS-1km_20180101_qn.tif
        File geotiff = new File("E:/tif/haihe-terra_MODIS-1km_20180101_qn.tif");
        System.out.println(geotiff);
        
        String workspace="etwatch";
        String layername="tif_test004";
        String storename=layername;
        boolean pc = publisher.publishExternalGeoTIFF(workspace, layername, geotiff,storename, "EPSG:4326", GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED, "tif_custom");
        
        RESTLayer layer =reader.getLayer(workspace, layername);
		RESTBoundingBox restBoundingBox=reader.getCoverage(layer).getLatLonBoundingBox();
		
		
		
        //boolean pc = publisher.publishGeoTIFF("etwatch", "tif1232", "tif1223",geotiff, "EPSG:4326", GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, "raster");
        //boolean pc = publisher.publishExternalGeoTIFF("etwatch", "tif123222001", geotiff, "tif123222001", "EPSG:4326", GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, "etwatch:g0");
        //boolean pc = publisher.publishExternalGeoTIFF("etwatch", "tif_test002", geotiff,"tif_test002", "EPSG:4326", GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED, "tif_custom");
        System.out.println(pc);

    }
    
    //发布shp服务
    public static void publishShp()throws Exception{
        File zipFile = new File("G:/shp/shp.zip");
        boolean published = publisher.publishShp("myWorkspace", "zhangjialu", "hengshui", zipFile, "EPSG:404000", "line");
        //boolean published = publisher.publishShp("myWorkspace", "zhangjialu", "hengshui", zipFile, "EPSG:4326", "default_point");
        System.out.println(published);
    }
    
    
    public static void main(String[] args) throws Exception{
        //createWorkSpace();
        //showWorkSpace();
        //publishShp();
        //publishTIFF();
        
        
        RESTStyle restStyle =reader.getStyle("etwatch", "g0");
        System.out.println(JSON.toJSON(restStyle));
    }
    
    
}
