package com.creation.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("artwork_tag_relation")
public class ArtworkTagRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作品ID
     */
    private Long artworkId;

    /**
     * 标签ID
     */
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}
