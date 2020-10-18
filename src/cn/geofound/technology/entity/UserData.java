package cn.geofound.technology.entity;

import java.util.Date;
import java.io.Serializable;
import org.nutz.dao.entity.annotation.*;

/**
 * (UserData)实体类
 *
 * @author makejava
 * @since 2020-07-14 14:58:22
 */
@Table("core.user_data")
public class UserData implements Serializable {
    private static final long serialVersionUID = -23209081925812320L;
        
    @Column("uid")
    private String uid;
        
    @Column("user_id")
    private String userId;
        
    @Column("name")
    private String name;
        
    @Column("file_name")
    private String fileName;
        
    @Column("file_uid")
    private String fileUid;
        
    @Column("created_at")
    private Date createdAt;
        
    @Column("updated_at")
    private Date updatedAt;
        
    @Column("count")
    private Integer count;
        
    @Column("convert_start")
    private Date convertStart;
        
    @Column("convert_end")
    private Date convertEnd;
        
    @Column("sr")
    private String sr;
        
    @Column("geom_field")
    private String geomField;
        
    @Column("fid_field")
    private String fidField;
        
    @Column("geom_type")
    private String geomType;
        
    @Column("convert_job_uid")
    private String convertJobUid;
        
    @Column("imported")
    private Object imported;
        
    @Column("name_fti")
    private Object nameFti;
        
    @Column("thumbnail")
    private Object thumbnail;
        
    @Column("error")
    private String error;
        
    @Column("storage")
    private Long storage;
        
    @Column("extent")
    private String extent;
        
    @Column("deleted_at")
    private Date deletedAt;
        
    @Column("share_status")
    private String shareStatus;
        
    @Column("metadata")
    private String metadata;
    /**
    * 备注说明
    */    
    @Column("remarks")
    private String remarks;
        
    @Column("import_date")
    private Date importDate;
    /**
    * 状态 (0:未下载;1:已下载)
    */    
    @Column("status")
    private String status;
        
    @Column("max_res")
    private String maxRes;
        
    @Column("description")
    private String description;

        
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
        
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
        
    public String getFileUid() {
        return fileUid;
    }

    public void setFileUid(String fileUid) {
        this.fileUid = fileUid;
    }
        
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
        
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
        
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
        
    public Date getConvertStart() {
        return convertStart;
    }

    public void setConvertStart(Date convertStart) {
        this.convertStart = convertStart;
    }
        
    public Date getConvertEnd() {
        return convertEnd;
    }

    public void setConvertEnd(Date convertEnd) {
        this.convertEnd = convertEnd;
    }
        
    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }
        
    public String getGeomField() {
        return geomField;
    }

    public void setGeomField(String geomField) {
        this.geomField = geomField;
    }
        
    public String getFidField() {
        return fidField;
    }

    public void setFidField(String fidField) {
        this.fidField = fidField;
    }
        
    public String getGeomType() {
        return geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }
        
    public String getConvertJobUid() {
        return convertJobUid;
    }

    public void setConvertJobUid(String convertJobUid) {
        this.convertJobUid = convertJobUid;
    }
        
    public Object getImported() {
        return imported;
    }

    public void setImported(Object imported) {
        this.imported = imported;
    }
        
    public Object getNameFti() {
        return nameFti;
    }

    public void setNameFti(Object nameFti) {
        this.nameFti = nameFti;
    }
        
    public Object getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Object thumbnail) {
        this.thumbnail = thumbnail;
    }
        
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
        
    public Long getStorage() {
        return storage;
    }

    public void setStorage(Long storage) {
        this.storage = storage;
    }
        
    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }
        
    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
        
    public String getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(String shareStatus) {
        this.shareStatus = shareStatus;
    }
        
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
        
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
        
    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }
        
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
        
    public String getMaxRes() {
        return maxRes;
    }

    public void setMaxRes(String maxRes) {
        this.maxRes = maxRes;
    }
        
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}