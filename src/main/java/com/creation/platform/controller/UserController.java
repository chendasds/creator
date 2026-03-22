/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 16:34:25
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-19 16:54:51
 * @FilePath: \build-one\src\main\java\com\creation\platform\controller\UserController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform.controller;

import com.creation.platform.dto.UserUpdateDTO;
import com.creation.platform.entity.Result;
import com.creation.platform.entity.User;
import com.creation.platform.service.UserService;
import com.creation.platform.utils.JwtUtils;
import com.creation.platform.vo.UserStatsVO;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        User user = userService.login(username, password);
        if (user == null) {
            return Result.error(401, "账号不存在或密码错误");
        }

        // 生成 JWT Token
        String token = JwtUtils.generateToken(user.getId());

        // 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", token);

        return Result.success("登录成功", data);
    }

    /**
     * 获取个人资料（脱敏）
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getProfile(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UserUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = userService.updateProfile(userId, dto);
        if (success) {
            return Result.success("更新成功", null);
        } else {
            return Result.error(500, "更新失败");
        }
    }

    /**
     * 获取当前登录用户的创作数据统计
     * 需要登录，从 JWT 中获取当前用户ID
     */
    @GetMapping("/stats")
    public Result<UserStatsVO> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(userService.getUserStats(userId));
    }
}
