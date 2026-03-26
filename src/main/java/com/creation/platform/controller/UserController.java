/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 16:34:25
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-19 16:54:51
 * @FilePath: \build-one\src\main\java\com\creation\platform\controller\UserController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform.controller;

import com.creation.platform.dto.PasswordUpdateDTO;
import com.creation.platform.dto.UserSettingsDTO;
import com.creation.platform.dto.UserUpdateDTO;
import com.creation.platform.entity.Result;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.NotificationService;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

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
     * 修改当前登录用户密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request, @RequestBody PasswordUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        // 依靠 GlobalExceptionHandler 处理业务异常（如原密码错误）
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功，请重新登录", null);
    }

    /**
     * 更新账号隐私与偏好设置
     */
    @PutMapping("/settings")
    public Result<Void> updateSettings(HttpServletRequest request, @RequestBody UserSettingsDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = userService.updateSettings(userId, dto);
        return success ? Result.success("设置更新成功", null) : Result.error(500, "设置更新失败");
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

    /**
     * 获取公开的用户数据面板（不需要登录）
     * 用于展示他人主页的作品统计信息
     */
    @GetMapping("/public/stats/{id}")
    public Result<UserStatsVO> getPublicUserStats(@PathVariable Long id) {
        return Result.success(userService.getUserStats(id));
    }

    /**
     * 根据关键字搜索用户（模糊匹配昵称和用户名，最多返回5条）
     */
    @GetMapping("/public/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(java.util.Collections.emptyList());
        }
        return Result.success(userMapper.searchUsers(keyword.trim()));
    }

    /**
     * 后台管理：分页查询用户列表（支持按用户名/昵称搜索，按状态过滤）
     */
    @GetMapping("/admin/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<User>> adminPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        // 模糊匹配用户名或昵称
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        }
        // 按状态过滤
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);
        return Result.success(userService.page(page, wrapper));
    }

    /**
     * 后台管理：修改用户信息并推送系统通知（防弹增强版）
     */
    @PutMapping("/admin/update")
    public Result<Void> adminUpdateUser(HttpServletRequest request, @RequestBody User user) {
        if (user.getId() == null) return Result.error("用户ID不能为空");

        Long adminId = (Long) request.getAttribute("userId");
        User oldUser = userService.getById(user.getId());
        if (oldUser == null) return Result.error("用户不存在");

        StringBuilder changes = new StringBuilder();

        // --- 核心修复：增加 null 检查，只有前端传了值且与旧值不同才记录 ---

        // 1. 角色变更
        if (user.getRole() != null && !user.getRole().equals(oldUser.getRole())) {
            changes.append("【权限】改为").append(user.getRole() == 2 ? "管理员" : "普通用户").append("; ");
        }
        // 2. 状态变更
        if (user.getStatus() != null && !user.getStatus().equals(oldUser.getStatus())) {
            changes.append("【状态】改为").append(user.getStatus() == 1 ? "正常" : "禁用").append("; ");
        }
        // 3. 昵称重置
        if (user.getNickname() != null && !user.getNickname().equals(oldUser.getNickname())) {
            changes.append("【昵称】已被重置; ");
        }
        // 4. 手机号变更
        if (user.getPhone() != null && !user.getPhone().equals(oldUser.getPhone())) {
            changes.append("【手机号】已更新; ");
        }
        // 5. 性别变更
        if (user.getGender() != null && !user.getGender().equals(oldUser.getGender())) {
            String g = user.getGender() == 1 ? "男" : (user.getGender() == 2 ? "女" : "保密");
            changes.append("【性别】改为").append(g).append("; ");
        }

        // 执行更新
        userService.updateById(user);

        // 发送通知
        if (changes.length() > 0) {
            notificationService.sendNotification(
                    user.getId(), adminId, 5, null,
                    "管理员修改了您的账号资料: " + changes.toString()
            );
        }
        return Result.success();
    }
}
