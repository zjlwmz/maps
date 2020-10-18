package cn.geofound.technology.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * 城市商圈
 * @author zhangjialu
 * @date 2019年5月23日 下午10:58:58
 */
@Table("trading_area.city_zone_new")
public class CityZoneNew {

	@Column
	private String id;
	
	@Column
	private Double lng;
	
	@Column
	private Double lat;
	
	@Column
	private String name;
	
	@Column("describe")
	private String desc;
	
	@Column
	private String path;
	
	@Column("city_code")
	private String cityCode;
	
	
	@Column
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
}
