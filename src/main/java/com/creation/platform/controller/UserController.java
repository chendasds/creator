package com.creation.platform.controller;

import com.creation.platform.entity.User;
import com.creation.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @PostMapping
    public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping
    public boolean updateById(@RequestBody User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return userService.removeById(id);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();

        String msg = userService.register(username, password);
        if (msg != null) {
            result.put("success", false);
            result.put("message", msg);
        } else {
            result.put("success", true);
            result.put("message", "注册成功");
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();

        User user = userService.login(username, password);
        if (user == null) {
            result.put("success", false);
            result.put("message", "账号不存在或密码错误");
        } else {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", user);
        }
        return result;
    }
}
