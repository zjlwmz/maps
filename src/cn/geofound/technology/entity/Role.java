package cn.geofound.technology.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;


/**
 * 角色管理
 * @author zhangjialu
 * @date 2018-6-10下午1:16:30
 */
@Table("role")
public class Role {

	/**
	 * 用户id
	 */
	@Name
	@Prev(els = {@EL("uuid()")})
	@Column("id")
	private String id;
	
	/**
	 * 角色名称
	 */
	@Column("name")
	private String name;
	
	
	
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
