package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("creation_record")
public class CreationRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 草稿标题
     */
    private String title;

    /**
     * 草稿内容
     */
    private String content;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 创作说明/灵感记录
     */
    private String description;

    /**
     * 字数统计
     */
    private Integer wordCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
