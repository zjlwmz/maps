package cn.geofound.technology.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

/**
 * 商圈 城市
 * @author zhangjialu
 * @date 2019年5月23日 下午10:06:45
 */
@Table("trading_area.city")
public class City {

	@Name
	@Prev(els = {@EL("uuid()")})
	@Column
	private String id;
	
	
	/**
	 * 城市编码
	 */
	@Column("city_code")
	private String cityCode;
	
	
	/**
	 * 城市名称
	 */
	@Column("city_name")
	private String cityName;
	
	
	/**
	 * 城市名称
	 */
	@Column("city_name_en")
	private String cityNameEn;
	
	@Column
	private String type;
	
	
	/**
	 * 分组
	 */
	@Column("group_type")
	private String groupType;
	
	/**
	 * 中心点 x
	 */
	@Column("center_x")
	private Double centerX;

	
	/**
	 * 中心点 y
	 */
	@Column("center_y")
	private Double centerY;
	
	
	@Column
	private Integer zoom;
	
	@Column
	private String status;
	
	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCityName() {
		return cityName;
	}


	public void setCityName(String cityName) {
		this.cityName = cityName;
	}


	public String getCityNameEn() {
		return cityNameEn;
	}


	public void setCityNameEn(String cityNameEn) {
		this.cityNameEn = cityNameEn;
	}


	public String getGroupType() {
		return groupType;
	}


	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCityCode() {
		return cityCode;
	}


	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}


	public Double getCenterX() {
		return centerX;
	}


	public void setCenterX(Double centerX) {
		this.centerX = centerX;
	}


	public Double getCenterY() {
		return centerY;
	}


	public void setCenterY(Double centerY) {
		this.centerY = centerY;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getZoom() {
		return zoom;
	}


	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}


	
	
	
	
	
	
	
	
	
	
}
