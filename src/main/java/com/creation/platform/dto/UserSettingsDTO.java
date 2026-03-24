package com.creation.platform.dto;

import lombok.Data;

@Data
public class UserSettingsDTO {
    /**
     * 手机号
     */
    private String phone;

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
}
