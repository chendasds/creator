package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;

    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 性别: 0-保密 1-男 2-女
     */
    private Integer gender;

    /**
     * 隐私设置: 是否隐藏收藏 (0-公开 1-隐藏)
     */
    private Integer hideCollections;

    /**
     * 隐私设置: 是否关闭系统通知 (0-接收 1-关闭)
     */
    private Integer disableNotifications;

    /**
     * 隐私设置: 是否开启图片水印 (0-关闭 1-开启)
     */
    private Integer watermark;

    /**
     * 角色: 1-普通用户 2-管理员
     */
    private Integer role;

    /**
     * 账号状态: 1-正常 0-禁用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
