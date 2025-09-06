package io.github.amsatrio.spring_boot_container_demo.module.m_biodata;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBiodataDto implements Serializable {
    private Long id;
    private String fullname;
    private String mobilePhone;
    private byte[] image;
    private String imagePath;

    private Long createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOn;

    private Long modifiedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedOn;

    private Long deletedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deletedOn;

    private Boolean isDelete = false;

    // === GETTER

    public byte[] getImage() {
        return image != null ? image.clone() : null;
    }

    public Date getCreatedOn() {
        return Optional.ofNullable(this.createdOn).orElse(null);
    }

    public Date getModifiedOn() {
        return Optional.ofNullable(this.modifiedOn).orElse(null);
    }

    public Date getDeletedOn() {
        return Optional.ofNullable(this.deletedOn).orElse(null);
    }


    // === SETTER

    public void setImage(byte[] image) {
        this.image = image != null ? image.clone() : null;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = (createdOn != null)
                ? new Date(createdOn.getTime())
                : null;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = (modifiedOn != null)
                ? new Date(modifiedOn.getTime())
                : null;
    }

    public void setDeletedOn(Date deletedOn) {
        this.deletedOn = (deletedOn != null)
                ? new Date(deletedOn.getTime())
                : null;
    }

}
