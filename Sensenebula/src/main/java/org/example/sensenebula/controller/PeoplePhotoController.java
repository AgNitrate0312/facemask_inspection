package org.example.sensenebula.controller;

import org.example.sensenebula.service.PeoplePhotoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PeoplePhotoController {
    // 声明一个服务层的对象，后续直接通过这个对象调用里面的方法
    private final PeoplePhotoService peoplePhotoService;

    // 构造器
    public PeoplePhotoController(PeoplePhotoService photoService) {
        this.peoplePhotoService = photoService;
    }


    /***
     * 描述信息：获取全部的抓拍图片记录，当用户访问 /getSnapPhoto 会自动执行这个函数 getSnapPhoto
     * <p>
     * GetMapping注解表示 /getSnapPhoto 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getSnapPhoto")
    public List<Map<String, Object>> getSnapPhoto() {
        // 调用service层逻辑，直接返回原始 List<Map<String, Object>> 字符串，Spring 会自动按 application/json 返回
        return peoplePhotoService.listSnapPhoto();
    }

    /***
     * 描述信息：获取全部的告警图片记录，当用户访问 /getWarnFaces 会自动执行这个函数 getWarnFaces
     * <p>
     * GetMapping注解表示 /getWarnFaces 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getWarnFaces")
    public List<Map<String, Object>> getWarnFaces() {
        // 调用service层逻辑，直接返回原始 List<Map<String, Object>> 字符串，Spring 会自动按 application/json 返回
        return peoplePhotoService.listWarnFaces();
    }

    /***
     * 描述信息：获取人体抓拍图片记录，当用户访问 /getAnatomySnapPhoto 会自动执行这个函数 getAnatomySnapPhoto
     * <p>
     * GetMapping注解表示 /getAnatomySnapPhoto 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getAnatomySnapPhoto")
    public List<Map<String, Object>> getAnatomySnapPhoto() {
        // 调用service层逻辑，直接返回原始 List<Map<String, Object>> 字符串，Spring 会自动按 application/json 返回
        return peoplePhotoService.listAnatomySnapPhoto();
    }

    /***
     * 描述信息：获取车辆抓拍图片记录，当用户访问 /getCarSnapPhoto 会自动执行这个函数 getCarSnapPhoto
     * <p>
     * GetMapping注解表示 /getCarSnapPhoto 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getCarSnapPhoto")
    public List<Map<String, Object>> getCarSnapPhoto() {
        // 调用service层逻辑，直接返回原始 List<Map<String, Object>> 字符串，Spring 会自动按 application/json 返回
        return peoplePhotoService.listCarSnapPhoto();
    }

    /***
     * 描述信息：获取车辆告警图片抓拍记录，当用户访问 /getWarnCar 会自动执行这个函数 getWarnCar
     * <p>
     * GetMapping注解表示 /getWarnCar 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getWarnCar")
    public List<Map<String, Object>> getWarnCar() {
        // 调用service层逻辑，直接返回原始 List<Map<String, Object>> 字符串，Spring 会自动按 application/json 返回
        return peoplePhotoService.listWarnCar();
    }

    /***
     * 描述信息：获取口罩识别记录，当用户访问 /getMaskDetection 会自动执行这个函数 getMaskDetection
     * <p>
     * GetMapping注解表示 /getMaskDetection 这个路径的请求方式必须是GET方式
     *
     * @return json字段
     */
    @GetMapping("/getMaskDetection")
    public List<Map<String, Object>> getMaskDetection() {
        return peoplePhotoService.listMaskDetection();
    }
}