package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Artwork;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.service.ArtworkService;
import org.springframework.stereotype.Service;

@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {
}
