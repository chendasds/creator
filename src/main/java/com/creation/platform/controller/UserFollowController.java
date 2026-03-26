/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 16:34:25
 * @LastEditors: your name
 * @LastEditTime: 2026-03-24
 * @FilePath: \build-one\src\main\java\com\creation\platform\controller\UserFollowController.java
 * @Description: 用户关注 Controller
 */
package com.creation.platform.controller;

import com.creation.platform.entity.Result;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.UserFollowMapper;
import com.creation.platform.service.UserFollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private UserFollowMapper userFollowMapper;

    /**
     * 切换关注状态
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleFollow(HttpServletRequest request, @RequestBody Map<String, Long> params) {
        Long followerId = (Long) request.getAttribute("userId");
        Long followeeId = params.get("followeeId");
        if (followerId == null || followeeId == null || followerId.equals(followeeId)) {
            return Result.error(400, "参数错误或不能关注自己");
        }
        boolean success = userFollowService.toggleFollow(followerId, followeeId);
        return Result.success(success);
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check")
    public Result<Boolean> checkFollow(HttpServletRequest request, @RequestParam Long followeeId) {
        Long followerId = (Long) request.getAttribute("userId");
        if (followerId == null) {
            return Result.success(false);
        }
        Integer isDeleted = userFollowMapper.checkFollowStatus(followerId, followeeId);
        boolean isFollowing = (isDeleted != null && isDeleted == 0);
        return Result.success(isFollowing);
    }

    /**
     * 获取关注列表（公开接口，无需登录）
     */
    @GetMapping("/following/{userId}")
    public Result<List<User>> getFollowings(@PathVariable Long userId) {
        return Result.success(userFollowService.getFollowings(userId));
    }

    /**
     * 获取粉丝列表（公开接口，无需登录）
     */
    @GetMapping("/followers/{userId}")
    public Result<List<User>> getFollowers(@PathVariable Long userId) {
        return Result.success(userFollowService.getFollowers(userId));
    }
}
