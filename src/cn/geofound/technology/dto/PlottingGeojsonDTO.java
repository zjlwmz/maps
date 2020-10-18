package cn.geofound.technology.dto;

import org.nutz.dao.entity.annotation.Column;

public class PlottingGeojsonDTO {

	
	/**
	 * geom
	 */
	@Column("geom")
	private Object geom;
	
	public Object getGeom() {
		return geom;
	}

	public void setGeom(Object geom) {
		this.geom = geom;
	}
	
	
}
