package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Artwork;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.vo.ArtworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Override
    public Page<ArtworkVO> getAdminArtworkPage(Integer current, Integer size, String title, Long categoryId, Integer status) {
        Page<ArtworkVO> page = new Page<>(current, size);
        return artworkMapper.selectArtworkPage(page, title, categoryId, status);
    }
}
