package cn.geofound.technology.dto;

/**
 * 标绘上传数据格式DTO
 * @author zhangjialu
 * @date 2019年4月20日 下午7:36:50
 */
public class PlottingParamsDTO {

	private String id;
	
	/**
	 * 名称
	 */
	private String name;
	
	
	/**
	 * geojson 格式
	 */
	private String geojson;
	
	/**
	 * leaflet 数据格式
	 */
	private String  latLngs;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGeojson() {
		return geojson;
	}


	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}


	public String getLatLngs() {
		return latLngs;
	}


	public void setLatLngs(String latLngs) {
		this.latLngs = latLngs;
	}
	
	
	
}
