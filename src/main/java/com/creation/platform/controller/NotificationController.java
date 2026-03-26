package com.creation.platform.controller;

import com.creation.platform.entity.Result;
import com.creation.platform.service.NotificationService;
import com.creation.platform.vo.NotificationVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list/{userId}")
    public Result<List<NotificationVO>> getList(@PathVariable Long userId) {
        return Result.success(notificationService.getNotificationList(userId));
    }

    @PutMapping("/readAll/{userId}")
    public Result<Void> readAll(@PathVariable Long userId) {
        notificationService.readAll(userId);
        return Result.success();
    }

    @GetMapping("/unreadCount/{userId}")
    public Result<Integer> getUnreadCount(@PathVariable Long userId) {
        return Result.success(notificationService.getUnreadCount(userId));
    }

    /**
     * 删除单条通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        boolean success = notificationService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 发送全站广播公告
     */
    @PostMapping("/broadcast")
    public Result<Void> sendBroadcast(HttpServletRequest request, @RequestBody Map<String, String> body) {
        Long adminId = (Long) request.getAttribute("userId");
        String content = body.get("content");

        if (content == null || content.trim().isEmpty()) {
            return Result.error("公告内容不能为空");
        }

        // 请确保你的 NotificationService 接口和实现类里依然保留了 broadcast 方法
        notificationService.broadcast(adminId, content);
        return Result.success();
    }
}