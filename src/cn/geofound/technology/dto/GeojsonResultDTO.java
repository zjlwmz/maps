package cn.geofound.technology.dto;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table(value="geo_plotting")
public class GeojsonResultDTO {

	@Column("geojson")
	private String geojson;

	public String getGeojson() {
		return geojson;
	}

	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}
	
	
}
