package org.example.sensenebula.service;

import org.example.sensenebula.utils.SenseApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 定义和人脸相关的 service 层服务，这一层的不同方法，可以获取到商汤边缘计算盒中不同接口的数据
 */
@Service
public class PeoplePhotoService {
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
     * 这个方法 调用 SenseApi.getSnapPhoto() 获取接口《3.13 抓拍图片条件查询》的数据
     * 并专门提取口罩识别信息
     *
     * @return 将json字段打包成 List<Map<String, Object>>对象返回
     */
    public List<Map<String, Object>> listMaskDetection() {
        // 复用 getSnapPhoto 获取数据
        List<Map<String, Object>> data = SenseApi.getSnapPhoto();

        List<Map<String, Object>> maskList = new ArrayList<>();
        for (Map<String, Object> rec : data) {
            // 1. 获取图片 base64
            String snapImg = SenseApi.getImgBase64((String) rec.get("snap_path"));

            // 2. 获取 face_attr
            Map<String, Object> attr = (Map<String, Object>) rec.get("face_attr");
            if (attr == null) {
                continue; // 如果没有属性信息，跳过
            }

            // 3. 解析口罩状态
            // st_respirator_full、st_respirator_nose、st_respirator_mouth分别表示佩戴、佩戴不完全、未佩戴
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

            // 4. 其他辅助信息
            String gender = "male".equals(attr.get("gender_code")) ? "男" : "女";
            
            // 5. 组装返回数据
            Map<String, Object> item = new HashMap<>();
            item.put("channel", rec.get("channel"));
            item.put("camera_name", rec.get("camera_name"));
            item.put("trigger", rec.get("trigger")); // 抓拍时间
            item.put("snap_img", snapImg);
            item.put("person_gender", gender);
            item.put("mask_status", respiratorStatusCN); // 口罩状态：佩戴/佩戴不完全/未佩戴
            item.put("st_age_value", attr.get("st_age_value")); // 年龄

            maskList.add(item);
        }
        return maskList;
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