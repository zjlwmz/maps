package cn.geofound.technology.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;


/**
 * 门店
 * @author zhangjialu
 * @date 2019年8月22日 下午9:35:56
 */
@Table("data.shop")
public class Shop {

	
	@Name
	@Prev(els = {@EL("uuid()")})
	@Column
	private String id;
	
	
	@Column
	private String address;
	
	@Column
	private String addressnew;
	
	@Column
	private Double distance;
	
	@Column
	private Double money;
	
	
	//位置的附加信息，是否精确查找。1为精确查找，即准确打点；0为不精确，即模糊打点。
	@Column
	private Integer precise;
	
	/**
	 * confidence
		描述打点绝对精度（即坐标点的误差范围）。
		confidence=100，解析误差绝对精度小于20m；
		confidence≥90，解析误差绝对精度小于50m；
		confidence≥80，解析误差绝对精度小于100m；
		confidence≥75，解析误差绝对精度小于200m；
		confidence≥70，解析误差绝对精度小于300m；
		confidence≥60，解析误差绝对精度小于500m；
		confidence≥50，解析误差绝对精度小于1000m；
		confidence≥40，解析误差绝对精度小于2000m；
		confidence≥30，解析误差绝对精度小于5000m；
		confidence≥25，解析误差绝对精度小于8000m；
		confidence≥20，解析误差绝对精度小于10000m；
	 */
	@Column
	private Integer confidence;
	
	/**
	 * 描述地址理解程度。分值范围0-100，分值越大，服务对地址理解程度越高（建议以该字段作为解析结果判断标准）；
		当comprehension值为以下值时，对应的准确率如下：
		comprehension=100，解析误差100m内概率为91%，误差500m内概率为96%；
		comprehension≥90，解析误差100m内概率为89%，误差500m内概率为96%；
		comprehension≥80，解析误差100m内概率为88%，误差500m内概率为95%；
		comprehension≥70，解析误差100m内概率为84%，误差500m内概率为93%；
		comprehension≥60，解析误差100m内概率为81%，误差500m内概率为91%；
		comprehension≥50，解析误差100m内概率为79%，误差500m内概率为90%；
		//解析误差：地理编码服务解析地址得到的坐标位置，与地址对应的真实位置间的距离。
	 */
	@Column
	private Integer comprehension;
	
	@Column
	private Double x;
	
	
	@Column
	private Double y;
	
	
	@Column
	private String remarks;
	
	
	@Column
	private String city;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getAddressnew() {
		return addressnew;
	}


	public void setAddressnew(String addressnew) {
		this.addressnew = addressnew;
	}


	public Double getDistance() {
		return distance;
	}


	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public Double getMoney() {
		return money;
	}


	public void setMoney(Double money) {
		this.money = money;
	}


	public Double getX() {
		return x;
	}


	public void setX(Double x) {
		this.x = x;
	}


	public Double getY() {
		return y;
	}


	public void setY(Double y) {
		this.y = y;
	}


	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Integer getPrecise() {
		return precise;
	}


	public void setPrecise(Integer precise) {
		this.precise = precise;
	}


	public Integer getConfidence() {
		return confidence;
	}


	public void setConfidence(Integer confidence) {
		this.confidence = confidence;
	}


	public Integer getComprehension() {
		return comprehension;
	}


	public void setComprehension(Integer comprehension) {
		this.comprehension = comprehension;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}

	

	
	
}
