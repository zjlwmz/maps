package cn.geofound.technology.entity;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author zhangjialu
 * @date 2020/6/29 11:43 上午
 */
@Table(value = "district")
public class District {
    @Name
    @Column("district_code")
    private String  district_code;


    @Column("district_level")
    private Integer  district_level;


    @Column("district_name")
    private String  district_name;


    @Column("father_name")
    private String  father_name;

    @Column("grandfather_name")
    private String  grandfather_name;

    @Column("has_children")
    private Integer  has_children;

    @Column
    private String  shape;


    public String getDistrict_code() {
        return district_code;
    }

    public void setDistrict_code(String district_code) {
        this.district_code = district_code;
    }

    public Integer getDistrict_level() {
        return district_level;
    }

    public void setDistrict_level(Integer district_level) {
        this.district_level = district_level;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getGrandfather_name() {
        return grandfather_name;
    }

    public void setGrandfather_name(String grandfather_name) {
        this.grandfather_name = grandfather_name;
    }

    public Integer getHas_children() {
        return has_children;
    }

    public void setHas_children(Integer has_children) {
        this.has_children = has_children;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
