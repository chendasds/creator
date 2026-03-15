package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Tag;
import com.creation.platform.mapper.TagMapper;
import com.creation.platform.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
