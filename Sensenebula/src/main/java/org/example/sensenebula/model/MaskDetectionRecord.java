package org.example.sensenebula.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mask_detection_record")
public class MaskDetectionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 唯一标识(使用snap_path) */
    @Column(name = "snap_id", unique = true, length = 128)
    private String snapId;

    /** 摄像头名称 */
    @Column(name = "camera_name", length = 64)
    private String cameraName;

    /** 通道号 */
    @Column(name = "channel")
    private Integer channel;

    /** 抓拍时间(trigger) */
    @Column(name = "snap_time", length = 32)
    private String snapTime;

    /** 抓拍图片Base64 */
    @Lob
    @Column(name = "snap_img_base64")
    private String snapImgBase64;

    /** 性别: 男/女 */
    @Column(name = "person_gender", length = 10)
    private String personGender;

    /** 年龄 */
    @Column(name = "age_value")
    private Integer ageValue;

    /** 口罩状态: 佩戴/佩戴不完全/未佩戴/未知 */
    @Column(name = "mask_status", length = 20)
    private String maskStatus;

    /** 处理状态: 0-未处理, 1-已处理, 2-忽略 */
    @Column(name = "handle_status")
    private Integer handleStatus = 0;

    /** 备注 */
    @Column(name = "remark")
    private String remark;

    /** 入库时间 */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSnapId() {
        return snapId;
    }

    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getSnapTime() {
        return snapTime;
    }

    public void setSnapTime(String snapTime) {
        this.snapTime = snapTime;
    }

    public String getSnapImgBase64() {
        return snapImgBase64;
    }

    public void setSnapImgBase64(String snapImgBase64) {
        this.snapImgBase64 = snapImgBase64;
    }

    public String getPersonGender() {
        return personGender;
    }

    public void setPersonGender(String personGender) {
        this.personGender = personGender;
    }

    public Integer getAgeValue() {
        return ageValue;
    }

    public void setAgeValue(Integer ageValue) {
        this.ageValue = ageValue;
    }

    public String getMaskStatus() {
        return maskStatus;
    }

    public void setMaskStatus(String maskStatus) {
        this.maskStatus = maskStatus;
    }

    public Integer getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(Integer handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
