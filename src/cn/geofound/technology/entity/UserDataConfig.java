package cn.geofound.technology.entity;

import java.io.Serializable;
import org.nutz.dao.entity.annotation.*;

/**
 * (UserDataConfig)实体类
 *
 * @author makejava
 * @since 2020-07-13 18:20:02
 */
@Table("core.user_data_config")
public class UserDataConfig implements Serializable {
    private static final long serialVersionUID = 879670987315846658L;

    @Name
    @Prev(els = {@EL("uuid()")})
    @Column("id")
    private String id;
        
    @Column("uid")
    private String uid;
        
    @Column("config")
    private Object config;

        
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
        
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
        
    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

}