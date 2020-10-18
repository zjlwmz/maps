package cn.geofound.technology.entity;

import java.io.Serializable;
import org.nutz.dao.entity.annotation.*;

/**
 * (PgFunc)实体类
 *
 * @author makejava
 * @since 2020-04-19 22:01:18
 */
@Table("data.pg_fun")
public class PgFun implements Serializable {
    private static final long serialVersionUID = -66149164758778189L;
        
    @Column("id")
    private String id;
        
    @Column("name")
    private String name;
        
    @Column("label")
    private String label;

    @Column("type")
    private String type;

    @Column("params")
    private Object params;

        
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
        
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
        
    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}