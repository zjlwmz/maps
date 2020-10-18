package cn.geofound.technology.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

/**
* 车辆
*/
@Table("sys_user")
public class User {
		
	/**
	 * 用户id
	 */
	@Name
	@Prev(els = {@EL("uuid()")})
	@Column("id")
	private String id;
	
	/**
	 * 用户姓名
	 */
	@Column("username")
	private String username;
	
	/**
	 * 年龄
	 */
	@Column("age")
	private String age;
	
	/**
	 * 电话
	 */
	@Column("phone")
	private String phone;
	
	/**
	 * 登录名
	 */
	@Column("loginname")
	private String loginname;
	
	/**
	 * 密码
	 */
	@Column("password")
	private String passWord;
	
	/**
	 * 用户类型id
	 */
	@Column("user_type_id")
	private String userTypeId;
	
	/**
	 * 创建时间
	 */
	@Column("create_date")
	private Date createDate;
	
	/**
	 * 修改时间
	 */
	@Column("update_date")
	private Date updateDate;
	
	
	
	/**
	 * 角色ID
	 */
	@Column("role_id")
	private String roleId;
	
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(String userTypeId) {
		this.userTypeId = userTypeId;
	}


	
	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
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

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
	
	
	
}
