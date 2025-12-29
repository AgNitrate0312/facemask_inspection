package org.example.sensenebula.controller;

import org.example.sensenebula.model.User;
import org.example.sensenebula.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // 告诉 Spring：这个类里所有方法都返回 JSON（不会走页面跳转）
@RequestMapping("/user")  // 声明一个路由组 /user
public class UserController {

    @Autowired   // 自动注入 Service 层
    private UserService userService;

    /* 1. 新增用户
      请求方式：POST
      URL：http://localhost:8080/user/add
      参数：写在 请求体（Body） 里，用 JSON
      例子：{"classid":"1","name":"张三"}
   */
    @PostMapping("/add")   // = @RequestMapping(method = RequestMethod.POST)
    public User add(@RequestBody User user) {  // @RequestBody 把 JSON 反序列成 User 对象
        return userService.addUser(user);
    }

    /* 2. 根据班级编号查用户
       请求方式：GET
       URL：http://localhost:8080/user/class/1
       参数：直接嵌在 URL 里（{} 占位符）
    */
    @GetMapping("/class/{classid}")  // {} 是占位符，真实值写在 URL 里
    public List<User> byClass(@PathVariable String classid) {   // @PathVariable 从 URL 片段取出 classid来
        return userService.getUsersByClass(classid);
    }

    /* 3. 模糊查姓名
      请求方式：GET
      URL：http://localhost:8080/user/search?name=三
      参数：传统的 QueryString（?key=value）
   */
    @GetMapping("/search")
    public List<User> search(@RequestParam String name) {  // @RequestParam 把 ?name=xxx 取出来
        return userService.getUsersByNameLike(name);
    }

    /* 4. 删除用户
       请求方式：DELETE
       URL：http://localhost:8080/user/delete?id=2
       参数：写在 URL 路径里
    */
    @DeleteMapping("/delete")  // DELETE 请求
    public void delete(@RequestParam Long id) { // 把 ?id=2 参数中的 2 取出来
        userService.deleteUser(id);
    }
}