package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.Artwork;
import com.creation.platform.vo.ArtworkVO;

public interface ArtworkService extends IService<Artwork> {

    /**
     * 后台管理作品分页查询（联表）
     */
    Page<ArtworkVO> getAdminArtworkPage(Integer current, Integer size, String title, Long categoryId, Integer status);
}
