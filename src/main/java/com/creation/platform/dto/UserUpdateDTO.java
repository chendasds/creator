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

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 性别: 0-保密 1-男 2-女
     */
    private Integer gender;
}
