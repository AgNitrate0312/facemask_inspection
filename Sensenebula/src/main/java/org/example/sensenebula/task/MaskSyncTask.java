package org.example.sensenebula.task;

import org.example.sensenebula.model.MaskDetectionRecord;
import org.example.sensenebula.repository.MaskDetectionRepository;
import org.example.sensenebula.utils.SenseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时任务：同步口罩识别抓拍记录到本地数据库
 */
@Component
public class MaskSyncTask {

    @Autowired
    private MaskDetectionRepository maskRepository;

    /**
     * 每隔 1 分钟执行一次同步
     * fixedRate = 60000 毫秒
     */
    @Scheduled(fixedRate = 60000)
    public void syncMaskData() {
        System.out.println("开始同步口罩抓拍数据...");
        try {
            // 1. 调用边缘盒子接口获取最新抓拍数据
            List<Map<String, Object>> data = SenseApi.getSnapPhoto();
            if (data == null || data.isEmpty()) {
                System.out.println("本次未获取到抓拍数据");
                return;
            }

            int newCount = 0;
            for (Map<String, Object> rec : data) {
                // 2. 获取唯一标识 snap_path
                String snapId = (String) rec.get("snap_path");
                
                // 3. 查重：如果数据库已存在该记录，则跳过
                if (maskRepository.findBySnapId(snapId) != null) {
                    continue; 
                }

                // 4. 解析并保存新记录
                saveRecord(rec, snapId);
                newCount++;
            }
            
            System.out.println("同步完成，新增记录数：" + newCount);

        } catch (Exception e) {
            System.err.println("同步口罩数据异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveRecord(Map<String, Object> rec, String snapId) {
        try {
            // 1. 获取 face_attr
            Map<String, Object> attr = (Map<String, Object>) rec.get("face_attr");
            if (attr == null) return; // 必须有人脸属性才入库

            // 2. 解析字段
            // 图片 Base64
            String snapImg = SenseApi.getImgBase64(snapId);
            
            // 口罩状态
            String respiratorStatusRaw = (String) attr.get("st_respirator");
            String respiratorStatusCN;
            if ("st_respirator_full".equals(respiratorStatusRaw)) {
                respiratorStatusCN = "佩戴";
            } else if ("st_respirator_nose".equals(respiratorStatusRaw)) {
                respiratorStatusCN = "佩戴不完全";
            } else if ("st_respirator_mouth".equals(respiratorStatusRaw)) {
                respiratorStatusCN = "未佩戴";
            } else {
                respiratorStatusCN = "未知";
            }

            // 性别
            String gender = "male".equals(attr.get("gender_code")) ? "男" : "女";
            
            // 年龄
            Integer age = null;
            Object ageObj = attr.get("st_age_value");
            if (ageObj != null) {
                try {
                    // 先转为 Double（因为JSON中的数字可能是小数），再取整
                    double d = Double.parseDouble(ageObj.toString());
                    age = (int) d;
                } catch (NumberFormatException e) {
                    System.err.println("年龄解析失败: " + ageObj);
                }
            }

            // 3. 构建实体对象
            MaskDetectionRecord record = new MaskDetectionRecord();
            record.setSnapId(snapId);
            record.setCameraName((String) rec.get("camera_name"));
            if (rec.get("channel") != null) {
                try {
                    record.setChannel(Integer.parseInt(rec.get("channel").toString()));
                } catch (Exception ignored) {}
            }
            record.setSnapTime((String) rec.get("trigger"));
            record.setSnapImgBase64(snapImg);
            record.setPersonGender(gender);
            record.setAgeValue(age);
            record.setMaskStatus(respiratorStatusCN);
            
            // 默认状态
            record.setHandleStatus(0); // 未处理
            
            // 4. 保存到数据库
            maskRepository.save(record);

        } catch (Exception e) {
            System.err.println("保存单条记录失败 snapId=" + snapId + ": " + e.getMessage());
        }
    }
}
