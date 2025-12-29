package org.example.sensenebula.utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public final class SenseApi {

    private static final RestTemplate RT = new RestTemplate();
    private static final String URL = "http://192.168.1.101:80/api/json";
    private static final String USER = "admin";
    private static final String PWD = "admin123!";

    /**
     * 全局 SessionId，调用 login() 后生效 在别的地方直接使用SenseApi.sessionId即可取出
     */
    public static String sessionId;

    /**
     * 一键登录，成功后将 sessionId 写入上面变量
     */
    public static void login() {
        String body = "{\"msg_id\":\"257\",\"user_name\":\"" + USER + "\",\"user_pwd\":\"" + PWD + "\"}";
        LoginResp resp = RT.postForObject(URL, body, LoginResp.class);
        if (resp == null || resp.getCode() != 0) {
            throw new RuntimeException("登录失败 code=" + (resp == null ? "null" : resp.getCode()));
        }
        // 设置全局 sessionId 供其它函数使用
        sessionId = resp.getData();
    }

    /**
     * 描述：这个方法直接像边缘计算盒子发送请求 请求的是接口 3.13 抓拍图片条件查询
     * <p>
     * 返回 data 里的 record 列表（List<Map>）
     */
    public static List<Map<String, Object>> getSnapPhoto() {
        String url = "http://192.168.1.101/api/json";
        // 设置json请求体 {msg_id:1037}
        String body = "{\"msg_id\":\"1037\"}";
        // 设置请求头
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("sessionid", sessionId);          // 全局 sessionId

        // 如果第一次请求数据时，如果因为 sessionId 过期导致请求失败，则自动第二次重试
        // 如果sessionId没有过期，第一次请求数据直接成功这个for只会执行一次
        for (int i = 0; i < 2; i++) {           // 最多两次
            HttpEntity<String> entity = new HttpEntity<>(body, h);
            // 将边缘计算盒的数据转为map，root中为保存的数据
            Map<String, Object> root = RT.postForObject(url, entity, Map.class);
            // 判断code是不是-141，如果是则说明sessionid过期了
            int code = (Integer) root.get("code");
            if (code == -141) {                 // 过期
                login();                        // 重新登录→更新全局变量
                h.set("sessionid", sessionId);  // header中换新的 sessionId
                continue;                       // 二次执行for循环重新发送请求
            }

            /* 成功：取出 data.record 列表 */
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            return (List<Map<String, Object>>) data.get("record");
        }
        throw new RuntimeException("重试仍失败");
    }

    /**
     * 描述：这个方法直接像边缘计算盒子发送请求 请求的是接口  3.16 告警图片条件查询
     * 返回 data 里的 record 列表（List<Map>）
     */
    public static List<Map<String, Object>> getWarnFaces(String libName) {
        String url = "http://192.168.1.101/api/json";
        // 设置json请求体 {msg_id:1036,lib_name:libName,qry_len:30}
        String body = "{\"msg_id\":\"1036\",\"lib_name\":\"" + libName + "\",\"qry_len\":30}";
        // 设置请求头
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("sessionid", sessionId);          // 全局 sessionId

        // 如果第一次请求数据时，如果因为 sessionId 过期导致请求失败，则自动第二次重试
        // 如果sessionId没有过期，第一次请求数据直接成功这个for只会执行一次
        for (int i = 0; i < 2; i++) {           // 最多两次
            HttpEntity<String> entity = new HttpEntity<>(body, h);
            // 将边缘计算盒的数据转为map，root中为保存的数据
            Map<String, Object> root = RT.postForObject(url, entity, Map.class);
            // 判断code是不是-141，如果是则说明sessionid过期了
            int code = (Integer) root.get("code");
            if (code == -141) {                 // 过期
                login();                        // 重新登录→更新全局变量
                h.set("sessionid", sessionId);  // header中换新的 sessionId
                continue;                       // 二次执行for循环重新发送请求
            }

            /* 成功：取出 data.record 列表 */
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            return (List<Map<String, Object>>) data.get("record");
        }
        throw new RuntimeException("重试仍失败");
    }

    /**
     * 描述：这个方法直接像边缘计算盒子发送请求 请求的是接口  3.22 人体抓拍图片条件查询
     * 返回 data 里的 record 列表（List<Map>）
     */
    public static List<Map<String, Object>> getAnatomySnapPhoto() {
        String url = "http://192.168.1.101/api/json";
        // 设置json请求体 {msg_id:1046}
        String body = "{\"msg_id\":\"1046\"}";
        // 设置请求头
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("sessionid", sessionId);          // 全局 sessionId

        // 如果第一次请求数据时，如果因为 sessionId 过期导致请求失败，则自动第二次重试
        // 如果sessionId没有过期，第一次请求数据直接成功这个for只会执行一次
        for (int i = 0; i < 2; i++) {           // 最多两次
            HttpEntity<String> entity = new HttpEntity<>(body, h);
            // 将边缘计算盒的数据转为map，root中为保存的数据
            Map<String, Object> root = RT.postForObject(url, entity, Map.class);
            // 判断code是不是-141，如果是则说明sessionid过期了
            int code = (Integer) root.get("code");
            if (code == -141) {                 // 过期
                login();                        // 重新登录→更新全局变量
                h.set("sessionid", sessionId);  // header中换新的 sessionId
                continue;                       // 二次执行for循环重新发送请求
            }

            /* 成功：取出 data.record 列表 */
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            return (List<Map<String, Object>>) data.get("record");
        }
        throw new RuntimeException("重试仍失败");
    }

    /**
     * ---------- 添加这个函数 ----------
     * 描述：从后台获取全部的车辆抓拍图片，这个方法直接像边缘计算盒子发送请求 请求的是接口  4.9 车辆抓拍记录查询
     * 返回 data 里的 record 列表（List<Map>）
     */
    public static List<Map<String, Object>> getCarSnapPhoto() {
        String url = "http://192.168.1.101/api/json";
        // 设置json请求体 {msg_id:1793}
        String body = "{\"msg_id\":\"1793\"}";
        // 设置请求头
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("sessionid", sessionId);          // 全局 sessionId

        // 如果第一次请求数据时，如果因为 sessionId 过期导致请求失败，则自动第二次重试
        // 如果sessionId没有过期，第一次请求数据直接成功这个for只会执行一次
        for (int i = 0; i < 2; i++) {           // 最多两次
            HttpEntity<String> entity = new HttpEntity<>(body, h);
            // 将边缘计算盒的数据转为map，root中为保存的数据
            Map<String, Object> root = RT.postForObject(url, entity, Map.class);
            // 判断code是不是-141，如果是则说明sessionid过期了
            int code = (Integer) root.get("code");
            if (code == -141) {                 // 过期
                login();                        // 重新登录→更新全局变量
                h.set("sessionid", sessionId);  // header中换新的 sessionId
                continue;                       // 二次执行for循环重新发送请求
            }

            /* 成功：取出 data.record 列表 */
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            return (List<Map<String, Object>>) data.get("record");
        }
        throw new RuntimeException("重试仍失败");
    }

    /**
     * ---------- 添加这个函数 ----------
     * 描述：从后台获取全部的车辆图片告警信息记录，这个方法直接像边缘计算盒子发送请求 请求的是接口  4.10 车辆告警记录查询
     * 返回 data 里的 record 列表（List<Map>）
     */
    public static List<Map<String, Object>> getWarnCar() {
        String url = "http://192.168.1.101/api/json";
        // 设置json请求体 {msg_id:1794}
        String body = "{\"msg_id\":\"1794\"}";
        // 设置请求头
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("sessionid", sessionId);          // 全局 sessionId

        // 如果第一次请求数据时，如果因为 sessionId 过期导致请求失败，则自动第二次重试
        // 如果sessionId没有过期，第一次请求数据直接成功这个for只会执行一次
        for (int i = 0; i < 2; i++) {           // 最多两次
            HttpEntity<String> entity = new HttpEntity<>(body, h);
            // 将边缘计算盒的数据转为map，root中为保存的数据
            Map<String, Object> root = RT.postForObject(url, entity, Map.class);
            // 判断code是不是-141，如果是则说明sessionid过期了
            int code = (Integer) root.get("code");
            if (code == -141) {                 // 过期
                login();                        // 重新登录→更新全局变量
                h.set("sessionid", sessionId);  // header中换新的 sessionId
                continue;                       // 二次执行for循环重新发送请求
            }

            /* 成功：取出 data.record 列表 */
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            return (List<Map<String, Object>>) data.get("record");
        }
        throw new RuntimeException("重试仍失败");
    }

    /**
     * 根据图片相对路径，拉取商汤图片并返回 base64 字符串（不含 data:image/jpeg;base64, 前缀）
     * 404 自动重试一次，其余异常直接抛
     */
    public static String getImgBase64(String urlSuffix) {
        String fullUrl = "http://192.168.1.101/ws/" + urlSuffix.replaceFirst("^/+", "");
        if (sessionId == null) login();
        for (int i = 0; i < 2; i++) {
            HttpHeaders h = new HttpHeaders();
            h.set("sessionid", sessionId);
            ResponseEntity<byte[]> r = RT.exchange(fullUrl, HttpMethod.GET,
                    new HttpEntity<>(h), byte[].class);
            if (r.getStatusCode() == HttpStatus.OK)
                return Base64.getEncoder().encodeToString(r.getBody());
            if (r.getStatusCode() == HttpStatus.NOT_FOUND) {
                login();
                continue;
            }
            throw new RuntimeException("HTTP " + r.getStatusCode());
        }
        throw new RuntimeException("图片不存在（404）且已重试：" + fullUrl);
    }


    /* 内部DTO */
    private static class LoginResp {
        private int code;
        private String data;

        public int getCode() {
            return code;
        }

        public String getData() {
            return data;
        }
    }

    private SenseApi() {
    }
}