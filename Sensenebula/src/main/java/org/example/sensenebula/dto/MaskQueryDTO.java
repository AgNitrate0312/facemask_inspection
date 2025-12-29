package org.example.sensenebula.dto;

public class MaskQueryDTO {
    private Integer channel;
    private String cameraName;
    private String maskStatus;
    private String personGender;
    
    // 时间范围：前端传 "YYYY-MM-DD HH:mm:ss"
    private String startTime;
    private String endTime;
    
    // 年龄范围
    private Integer minAge;
    private Integer maxAge;
    
    // 分页参数
    private int page = 1;
    private int size = 10;

    // Getters and Setters
    public Integer getChannel() { return channel; }
    public void setChannel(Integer channel) { this.channel = channel; }
    
    public String getCameraName() { return cameraName; }
    public void setCameraName(String cameraName) { this.cameraName = cameraName; }
    
    public String getMaskStatus() { return maskStatus; }
    public void setMaskStatus(String maskStatus) { this.maskStatus = maskStatus; }
    
    public String getPersonGender() { return personGender; }
    public void setPersonGender(String personGender) { this.personGender = personGender; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
