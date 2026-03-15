package com.creation.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creation.platform.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
