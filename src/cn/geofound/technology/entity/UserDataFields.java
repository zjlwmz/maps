package cn.geofound.technology.entity;

import java.io.Serializable;
import org.nutz.dao.entity.annotation.*;

/**
 * (UserDataFields)实体类
 *
 * @author makejava
 * @since 2020-07-14 14:51:59
 */
@Table("core.user_data_fields")
public class UserDataFields implements Serializable {
    private static final long serialVersionUID = -54829196043664043L;
        
    @Column("id")
    private String id;
        
    @Column("name")
    private String name;
        
    @Column("type")
    private String type;
        
    @Column("uid")
    private String uid;

        
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
        
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
        
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public UserDataFields(){
        super();
    }

    public UserDataFields(String name,String type){
        super();
        this.name = name;
        this.type = type;
    }

}