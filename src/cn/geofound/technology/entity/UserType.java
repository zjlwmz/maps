package cn.geofound.technology.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

/**
* 用户类型
*/
@Table("user_type")
public class UserType {
			
	/**
	 * 用户类型id
	 */
	@Name
	@Prev(els = {@EL("uuid()")})
	@Column("usertype_id")
	private String userTypeId;
	
	
	/**
	 * 类型名称
	 */
	@Column("usertype_name")
	private String userTypeName;


	public String getUserTypeId() {
		return userTypeId;
	}
	
	/**
	 * 创建时间
	 */
	@Column("create_date")
	private String createDate;
	
	/**
	 * 修改时间
	 */
	@Column("update_date")
	private String updateDate;


	public void setUserTypeId(String userTypeId) {
		this.userTypeId = userTypeId;
	}


	public String getUserTypeName() {
		return userTypeName;
	}


	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
}
