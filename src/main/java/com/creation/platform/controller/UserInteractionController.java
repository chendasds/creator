package com.creation.platform.controller;

import com.creation.platform.dto.InteractionDTO;
import com.creation.platform.entity.Result;
import com.creation.platform.service.UserInteractionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interaction")
public class UserInteractionController {

    @Autowired
    private UserInteractionService userInteractionService;

    /**
     * 切换互动状态（点赞/收藏）
     * 状态翻转：已点赞/收藏则取消，未点赞/收藏则新增
     *
     * @param request HTTP请求（用于获取当前登录用户ID）
     * @param dto    互动信息 DTO
     * @return true-当前已点赞/已收藏，false-当前未点赞/未收藏
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggle(HttpServletRequest request, @RequestBody InteractionDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        boolean currentState = userInteractionService.toggleInteraction(userId, dto);
        return Result.success(currentState);
    }

    /**
     * 检查用户是否已点赞/收藏
     *
     * @param request        HTTP请求（用于获取当前登录用户ID）
     * @param artworkId      作品ID
     * @param interactionType 互动类型: 1-点赞 2-收藏
     * @return true-已点赞/已收藏，false-未点赞/未收藏
     */
    @GetMapping("/check")
    public Result<Boolean> check(
            HttpServletRequest request,
            @RequestParam Long artworkId,
            @RequestParam Integer interactionType) {
        Long userId = (Long) request.getAttribute("userId");
        boolean existed = userInteractionService.hasInteracted(userId, artworkId, interactionType);
        return Result.success(existed);
    }
}
