package com.creation.platform.controller;

import com.creation.platform.entity.Result;
import com.creation.platform.service.PrivateMessageService;
import com.creation.platform.vo.ConversationVO;
import com.creation.platform.vo.PrivateMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class PrivateMessageController {

    @Autowired
    private PrivateMessageService privateMessageService;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations/{userId}")
    public Result<List<ConversationVO>> getConversations(@PathVariable Long userId) {
        return Result.success(privateMessageService.getConversationList(userId));
    }

    /**
     * 获取具体聊天记录
     */
    @GetMapping("/history/{currentUserId}/{targetUserId}")
    public Result<List<PrivateMessageVO>> getHistory(@PathVariable Long currentUserId, @PathVariable Long targetUserId) {
        return Result.success(privateMessageService.getChatHistory(currentUserId, targetUserId));
    }

    /**
     * 发送私信
     */
    @PostMapping("/send")
    public Result<PrivateMessageVO> send(@RequestBody Map<String, Object> params) {
        Long senderId = Long.valueOf(params.get("senderId").toString());
        Long receiverId = Long.valueOf(params.get("receiverId").toString());
        String content = params.get("content").toString();
        return Result.success(privateMessageService.sendMessage(senderId, receiverId, content));
    }

    /**
     * 获取未读消息总数
     */
    @GetMapping("/unreadCount/{userId}")
    public Result<Integer> getUnreadCount(@PathVariable Long userId) {
        return Result.success(privateMessageService.getTotalUnread(userId));
    }
}
