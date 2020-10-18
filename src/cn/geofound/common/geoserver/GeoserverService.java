package cn.geofound.common.geoserver;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTBoundingBox;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;



import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
/**
 * geoserver rest 服务接口
 * @author zhangjialu
 */
@IocBean
public class GeoserverService {

	private static final Log log = Logs.get();
	
	@Inject("java:$custom.get('geoserver.url')")
	private  String RESTURL;//= "http://localhost:8787/geoserver";
	
	@Inject("java:$custom.get('geoserver.username')")
	private String RESTUSER;//= "admin";
	
	@Inject("java:$custom.get('geoserver.password')")
	private String RESTPW;//= "geoserver";
	
	public static URL URL;
	private static GeoServerRESTManager manager;
	private static GeoServerRESTReader reader;
	private static GeoServerRESTPublisher publisher;
	
	
	public GeoServerRESTPublisher getPublisher(){
		if(null==publisher){
			try {
				URL = new URL(RESTURL);
				manager = new GeoServerRESTManager(URL, RESTUSER, RESTPW);
				reader = manager.getReader();
				publisher = manager.getPublisher();
			} catch (MalformedURLException e) {
				log.error("getPublisher", e);
			}
		}
		return publisher;
	}
	
	public GeoServerRESTReader getReader(){
		if(null==reader){
			getPublisher();
		}
		return reader;
	}
	
	//创建工作空间
    public void createWorkSpace() throws Exception{
        boolean created = getPublisher().createWorkspace("myWorkspace");
        System.out.println(created);
    }

    //罗列工作空间
    public  void showWorkSpace() throws Exception{
        RESTLayerList list = getReader().getLayers();
        System.out.println(list.get(0).getName());
    }

    
    //罗列已发布shp
    //发布tiff数据
    public  boolean publishTIFF(String fileName,String workspace,String storename,String layername,String srs,String defaultStyle) throws Exception{
    	File geotiff = new File(fileName);
    	boolean pc = getPublisher().publishExternalGeoTIFF(workspace, storename, geotiff,layername, srs, GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED, defaultStyle);
    	System.out.println(pc);
    	//        boolean pc = getPublisher().publishGeoTIFF(workspace,storename, layername,geotiff, srs, GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, defaultStyle,null);
//      boolean pc = publisher.publishGeoTIFF("myWorkspace", "tif", "resttestdem",geotiff, "EPSG:4326", GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, "raster");
        return pc;
    }
    
    //发布shp服务
    public  void publishShp()throws Exception{
        File zipFile = new File("G:/shp/shp.zip");
        boolean published = getPublisher().publishShp("myWorkspace", "zhangjialu", "hengshui", zipFile, "EPSG:404000", "line");
        //boolean published = publisher.publishShp("myWorkspace", "zhangjialu", "hengshui", zipFile, "EPSG:4326", "default_point");
        System.out.println(published);
    }
    
    
    /**
     * 获取矢量文件类型
     * @param layer
     * @return
     */
    public RESTFeatureType getFeatureType(RESTLayer layer){
    	RESTFeatureType restFeatureType =getReader().getFeatureType(layer);
    	return restFeatureType;
    }
    
    /**
     * 获取栅格文件类型
     * @param layer
     * @return
     */
    public RESTCoverage getCoverage(RESTLayer layer){
    	RESTCoverage restCoverage =getReader().getCoverage(layer);
    	return restCoverage;
    }
    
    
    /**
     * 获取图层bbox范围
     * @param layer
     * @return
     */
    public List<Double> getBbox(RESTLayer layer){
    	List<Double>bbox=new ArrayList<Double>();
    	if(layer.getType() == RESTLayer.Type.VECTOR){
    		RESTFeatureType restFeatureType =getReader().getFeatureType(layer);
    		RESTBoundingBox restBoundingBox=restFeatureType.getLatLonBoundingBox();
    		bbox.add(restBoundingBox.getMinX());
    		bbox.add(restBoundingBox.getMinY());
    		bbox.add(restBoundingBox.getMaxX());
    		bbox.add(restBoundingBox.getMaxY());
    	}else if(layer.getType() == RESTLayer.Type.RASTER){
    		RESTCoverage restCoverage =getReader().getCoverage(layer);
    		RESTBoundingBox restBoundingBox=restCoverage.getLatLonBoundingBox();
    		
    		bbox.add(restBoundingBox.getMinX());
    		bbox.add(restBoundingBox.getMinY());
    		bbox.add(restBoundingBox.getMaxX());
    		bbox.add(restBoundingBox.getMaxY());
    	}
    	
		return bbox;
    }
    
    /**
     * 获取栅格图层bbox范围
     * @param layer
     * @return
     */
    public List<Double> getRasterBbox(RESTCoverage restCoverage){
    	List<Double>bbox=new ArrayList<Double>();
		RESTBoundingBox restBoundingBox=restCoverage.getLatLonBoundingBox();
		
		bbox.add(restBoundingBox.getMinX());
		bbox.add(restBoundingBox.getMinY());
		bbox.add(restBoundingBox.getMaxX());
		bbox.add(restBoundingBox.getMaxY());
    	
		return bbox;
    }
    
    /**
     * 获取矢量图层bbox范围
     * @param layer
     * @return
     */
    public List<Double> getVectorBbox(RESTFeatureType restFeatureType){
    	List<Double>bbox=new ArrayList<Double>();
		RESTBoundingBox restBoundingBox=restFeatureType.getLatLonBoundingBox();
		bbox.add(restBoundingBox.getMinX());
		bbox.add(restBoundingBox.getMinY());
		bbox.add(restBoundingBox.getMaxX());
		bbox.add(restBoundingBox.getMaxY());
    	
		return bbox;
    }
    
    /**
     * 获取图例
     */
    public Map<String, Object> getLegend(RESTLayer layer,String workspace){
    	Map<String, Object> rules = new HashMap<String, Object>();
    	try{
    		String styleName =layer.getDefaultStyle();
    		String styleNames[]=styleName.split(":");
            String sld = reader.getSLD(styleNames[0], styleNames[1]);//reader.getSLD(styleName);
            if(sld!=null){
            	StringReader sr = new StringReader(sld);
                InputSource is = new InputSource(sr);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = (Document) builder.parse(is);
                NodeList colorMapEntryList = doc.getElementsByTagName("ColorMapEntry");
                
                List<Map<String, Object>> legends = new ArrayList<Map<String, Object>>();
                
                for(int i=0;i<colorMapEntryList.getLength();i++){
                	Node colorMapEntry =colorMapEntryList.item(i);
                	System.out.println(colorMapEntry);
                	NamedNodeMap namedNodeMap =colorMapEntry.getAttributes();
                	String color = namedNodeMap.getNamedItem("color").getTextContent();
                	String quantity = namedNodeMap.getNamedItem("quantity").getTextContent();
                	String label = namedNodeMap.getNamedItem("label").getTextContent();
                	Map<String, Object> legend = new HashMap<String, Object>();
                	legend.put("label", label);
                	legend.put("quantity", quantity);
                	legend.put("color", color);
                	legends.add(legend);
                }
                rules.put("rules",legends);
            }
    	}catch (Exception e) {
			
		}
    	return rules;
    	
    }
    
    /**
     * 获取图例
     */
    public Map<String, Object> getLegend2(RESTLayer layer,String workspace){
    	Map<String, Object> rules = new HashMap<String, Object>();
    	try{
    		String styleName =layer.getDefaultStyle();
    		String styleNames[]=styleName.split(":");
            String sld = reader.getSLD(styleNames[0], styleNames[1]);//reader.getSLD(styleName);
            if(sld!=null){
            	StringReader sr = new StringReader(sld);
                InputSource is = new InputSource(sr);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = (Document) builder.parse(is);
                NodeList nodeNames = doc.getElementsByTagName("sld:Name");
                NodeList nodeTitles = doc.getElementsByTagName("ogc:Literal");
                NodeList nodeFields = doc.getElementsByTagName("ogc:PropertyName");
                
                List<Map<String, Object>> legends = new ArrayList<Map<String, Object>>();
                rules.put("field", nodeFields.item(0).getTextContent().toLowerCase());
                for(int i=0;i<nodeTitles.getLength();i++){
                    Node name = nodeNames.item(i+2);
                    Node title = nodeTitles.item(i);
                    Map<String, Object> legend = new HashMap<String, Object>();
                    legend.put("title",title.getTextContent());
                    legend.put("rule",name.getTextContent());
                    legends.add(legend);
                }
                rules.put("rules",legends);
            }
    	}catch (Exception e) {
			
		}
    	return rules;
    	
    }
    
    
    public static void main(String[] args) throws Exception{
//        createWorkSpace();
//        showWorkSpace();
        
        //publishShp();
    	
        //publishTIFF();
    }
    
    
}
