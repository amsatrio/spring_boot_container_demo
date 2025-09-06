package io.github.amsatrio.spring_boot_container_demo.module.m_biodata;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBiodataRequest {
    private Long id;

    @Length(max = 255, message = "fullname must be between 0-255 characters")
    @Pattern(regexp = "^[A-Za-z\\s.'-]+$", message = "fullname can only contain letters, spaces, periods, hyphens, and apostrophes")
    private String fullname;

    @Length(max = 15, message = "mobilePhone must be between 0-15 characters")
    @Pattern(regexp = "^\\+?[0-9]{10,14}$", message = "mobilePhone must be a valid phone number with 10-14 digits, optional + prefix")
    private String mobilePhone;

    private MultipartFile image;

    @Length(max = 255, message = "imagePath must be between 0-255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_/.-]*$", message = "imagePath can only contain alphanumeric characters, underscores, slashes, periods, and hyphens")
    private String imagePath;

    private Boolean isDelete = false;

    // === CONVERTER
    public void fromDto(MBiodataDto dto) {
        BeanUtils.copyProperties(dto, this);
    }

    public MBiodataDto toDto() {
        MBiodataDto dto = new MBiodataDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    // === GETTER


    // === SETTER
    public void setId(Long id) {
        this.id = id;
        if (id == null) {
            this.id = new Date().getTime();
        } else if (id < 0){
            this.id = new Date().getTime();
        }
    }

    @Override
    public String toString() {
        return "MBiodataRequest [id=" + id + ", fullname=" + fullname + ", mobilePhone=" + mobilePhone + ", imagePath=" + imagePath + ", isDelete=" + isDelete + "]";
    }

}
