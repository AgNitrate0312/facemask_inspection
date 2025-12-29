package org.example.sensenebula.service;

import org.example.sensenebula.model.MaskDetectionRecord;
import org.example.sensenebula.repository.MaskDetectionRepository;
import org.example.sensenebula.task.MaskSyncTask;
import org.example.sensenebula.utils.SenseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/***
 * 定义和人脸相关的 service 层服务，这一层的不同方法，可以获取到商汤边缘计算盒中不同接口的数据
 */
@Service
public class PeoplePhotoService {

    @Autowired
    private MaskDetectionRepository maskRepository;

    @Autowired
    private MaskSyncTask maskSyncTask;

    /**
     * 这个方法 调用 SenseApi.getSnapPhoto() 获取接口《3.13 抓拍图片条件查询》的数据
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listSnapPhoto() {
        // data中包含了《3.13 抓拍图片条件查询》全部字段值
        List<Map<String, Object>> data = SenseApi.getSnapPhoto();

        // 2. 构造前端列表
        List<Map<String, Object>> faceList = new ArrayList<>();
        for (Map<String, Object> rec : data) {
            // 2.1 抓拍图 base64
            String snapImg = SenseApi.getImgBase64((String) rec.get("snap_path"));

            // 2.2 face_attr 子对象
            Map<String, Object> attr = (Map<String, Object>) rec.get("face_attr");

            // 2.3 小转换
            String gender = "male".equals(attr.get("gender_code")) ? "男" : "女";
            String mustache = "mustache_style_type_none".equals(attr.get("mustache_style")) ? "无" : "有";
            String cap = "hat_style_type_none".equals(attr.get("cap_style")) ? "无" : "有";

            // 2.4 拼装前端字段
            Map<String, Object> item = new HashMap<>();
            item.put("channel", rec.get("channel"));
            item.put("camera_name", rec.get("camera_name"));
            item.put("trigger", rec.get("trigger"));
            item.put("snap_img", snapImg);
            item.put("st_age_value", attr.get("st_age_value"));
            item.put("person_gender", gender);
            item.put("mustache_style", mustache);
            item.put("glass_style", attr.get("glass_style"));
            item.put("cap_style", cap);

            faceList.add(item);
        }
        return faceList;
    }

    /**
     * 这个方法 调用 SenseApi.getWarnFaces() 获取接口《3.16 告警图片条件查询》的数据
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listWarnFaces() {
        // data中包含了《3.16 告警图片条件查询》全部字段值
        List<Map<String, Object>> data = SenseApi.getWarnFaces("21计科02班");

        // 2. 构造前端需要的列表
        List<Map<String, Object>> faceList = new ArrayList<>();
        for (Map<String, Object> rec : data) {
            // 2.1 调两次 base64（库图 + 抓拍图）
            String img = SenseApi.getImgBase64((String) rec.get("img_path"));
            String snapImg = SenseApi.getImgBase64((String) rec.get("snap_path"));

            // 2.2 性别转换：1→男，其它→女
            String gender = "1".equals(rec.get("person_gender")) ? "男" : "女";

            // 2.3 拼装前端字段
            Map<String, Object> item = new HashMap<>();
            item.put("camera_name", rec.get("camera_name"));
            item.put("channel", rec.get("channel"));
            item.put("create_time", rec.get("create_time"));
            item.put("img", img);
            item.put("snap_img", snapImg);
            item.put("lib_name", rec.get("lib_name"));
            item.put("person_name", rec.get("person_name"));
            item.put("person_gender", gender);
            item.put("similarity", rec.get("similarity"));

            faceList.add(item);
        }
        return faceList;
    }

    /**
     * 这个方法 调用 SenseApi.getAnatomySnapPhoto() 获取接口《3.22 人体抓拍图片条件查询》的数据
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listAnatomySnapPhoto() {
        // data中包含了《3.22 人体抓拍图片条件查询》中record字段值
        List<Map<String, Object>> data = SenseApi.getAnatomySnapPhoto();
        // 可以添加自己的处理数据逻辑

        // 2. 构造前端列表
        List<Map<String, Object>> faceList = new ArrayList<>();
        for (Map<String, Object> rec : data) {
            // 2.1 抓拍图 base64
            String snapImg = SenseApi.getImgBase64((String) rec.get("snap_path"));

            // 2.2 body_attr 子对象
            Map<String, Object> attr = (Map<String, Object>) rec.get("body_attr");

            // 2.3 小转换
            String gender = "male".equals(attr.get("gender_code")) ? "男" : "女";
            String cap = "hat_style_type_none".equals(attr.get("cap_style")) ? "无" : "有";
            String smoking = "st_smoking_without".equals(attr.get("st_smoking")) ? "否" : "是";
            String respirator = "st_respirator_without".equals(attr.get("st_respirator_v2")) ? "无" : "有";
            String reflective = "st_reflective_clothes_without".equals(attr.get("st_reflective_clothes")) ? "无" : "有";

            // 2.4 拼装前端字段
            Map<String, Object> item = new HashMap<>();
            item.put("channel", rec.get("channel"));
            item.put("camera_name", rec.get("camera_name"));
            item.put("trigger", rec.get("trigger"));
            item.put("snap_img", snapImg);
            item.put("st_age", attr.get("st_age"));
            item.put("person_gender", gender);
            item.put("cap_style", cap);
            item.put("coat_color", attr.get("coat_color"));
            item.put("st_smoking", smoking);
            item.put("st_respirator_v2", respirator);
            item.put("st_reflective_clothes", reflective);

            faceList.add(item);
        }
        return faceList;
    }

    /**
     * 【已修改】改为查询本地数据库 mask_detection_record 表
     * 
     * @return 将 Entity 转换为 List<Map<String, Object>> 保持前端兼容
     */
    public List<Map<String, Object>> listMaskDetection() {
        // 1. 查询数据库，按时间倒序
        List<MaskDetectionRecord> records = maskRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 2. 转换为前端需要的 Map 结构
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (MaskDetectionRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            // 基础字段
            item.put("id", record.getId());
            item.put("camera_name", record.getCameraName());
            item.put("trigger", record.getSnapTime()); 
            item.put("snap_img", record.getSnapImgBase64());
            item.put("person_gender", record.getPersonGender());
            item.put("st_age_value", record.getAgeValue());
            item.put("mask_status", record.getMaskStatus());
            
            // 扩展字段（数据库特有）
            item.put("handle_status", record.getHandleStatus());
            item.put("remark", record.getRemark());
            item.put("create_time", record.getCreateTime());

            resultList.add(item);
        }
        return resultList;
    }

    /**
     * 手动触发同步（用于测试）
     */
    public void triggerSync() {
        maskSyncTask.syncMaskData();
    }

    /**
     * 修改口罩识别记录
     * @param id 记录ID
     * @param handleStatus 处理状态 (0-未处理, 1-已处理, 2-忽略)
     * @param remark 备注信息
     * @return 是否成功
     */
    public boolean updateMaskRecord(Long id, Integer handleStatus, String remark) {
        Optional<MaskDetectionRecord> optional = maskRepository.findById(id);
        if (optional.isPresent()) {
            MaskDetectionRecord record = optional.get();
            if (handleStatus != null) {
                record.setHandleStatus(handleStatus);
            }
            if (remark != null) {
                record.setRemark(remark);
            }
            maskRepository.save(record);
            return true;
        }
        return false;
    }

    /**
     * 删除口罩识别记录
     * @param id 记录ID
     * @return 是否成功
     */
    public boolean deleteMaskRecord(Long id) {
        if (maskRepository.existsById(id)) {
            maskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 这个方法 调用 SenseApi.getCarSnapPhoto() 获取接口《4.9 车辆抓拍记录查询》的数据
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listCarSnapPhoto() {
        // data中包含了《4.9 车辆抓拍记录查询》中record字段值
        List<Map<String, Object>> data = SenseApi.getCarSnapPhoto();
        // 可以添加自己的处理数据逻辑

        return data;
    }

    /**
     * 这个方法 调用 SenseApi.getWarnCar() 获取接口《4.10 车辆告警记录查询》的数据
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listWarnCar() {
        // data中包含了《4.10 车辆告警记录查询》中record字段值
        List<Map<String, Object>> data = SenseApi.getWarnCar();
        // 可以添加自己的处理数据逻辑

        return data;
    }
}
