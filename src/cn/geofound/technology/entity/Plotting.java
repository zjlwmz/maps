package cn.geofound.technology.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;


/**
 * 标绘管理
 * @author zhangjialu
 * @date 2018-6-8下午4:08:16
 */
@Table("data.geo_plotting")
public class Plotting {

	@Name
	@Column("id")
	public String id;
	
	
	/**
	 * geom
	 */
	@Column("geom")
	private Object geom;
	
	/**
	 * type类型
	 */
	@Column("type")
	private String type;
	
	/**
	 * 名称
	 */
	@Column("name")
	private String name;

	/**
	 * 属性
	 */
	@Column("properties")
	private String properties;
	
	/**
	 * 半径
	 */
	@Column("radius")
	private Double radius;
	
	
	/**
	 * leaflat json
	 */
	@Column("lat_lngs")
	private String latLngs;
	
	
	/**
	 * 备注
	 */
	@Column("remarks")
	private String remarks;
	
	/**
	 * 创建时间
	 */
	@Column("create_date")
	private Date createDate;
	

	/**
	 * 更新时间
	 */
	@Column("update_date")
	private Date updateDate;
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getGeom() {
		return geom;
	}

	public void setGeom(Object geom) {
		this.geom = geom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public String getLatLngs() {
		return latLngs;
	}

	public void setLatLngs(String latLngs) {
		this.latLngs = latLngs;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	
	
	
	
}
