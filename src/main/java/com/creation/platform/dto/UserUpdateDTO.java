package com.creation.platform.dto;

import lombok.Data;

/**
 * 用户更新请求 DTO
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;
}
