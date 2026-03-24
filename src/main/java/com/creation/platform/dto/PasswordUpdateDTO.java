package com.creation.platform.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    /** 旧密码（明文） */
    private String oldPassword;
    /** 新密码（明文） */
    private String newPassword;
}
