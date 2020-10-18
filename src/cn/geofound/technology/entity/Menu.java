package cn.geofound.technology.entity;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;


/**
 * 菜单管理
 * @author zhangjialu
 * @date 2018-6-10下午1:20:57
 */
@Table("menu")
public class Menu implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 菜单id
	 */
	@Name
	@Prev(els = {@EL("uuid()")})
	@Column("id")
	private String id;
	
	
	/**
	 * 菜单名称
	 */
	@Column("name")
	private String name;
	
	
	/**
	 * 序号
	 */
	@Column("sort")
	private Integer sort;
	
	
	/**
	 * 菜单url
	 */
	@Column("url")
	private String url;
	
	/**
	 * target
	 */
	@Column("target")
	private String target;
	
	
	/**
	 * 图标
	 */
	@Column("icon")
	private String icon;
	
	
	
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getSort() {
		return sort;
	}


	public void setSort(Integer sort) {
		this.sort = sort;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target;
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


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
	
	
	
}
