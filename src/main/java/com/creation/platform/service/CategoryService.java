package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 保存分类（起死回生方案）
     * @param category 前端传来的分类数据
     * @return 保存结果
     */
    boolean saveWithRecovery(Category category);

    /**
     * 获取热门分类（按关联的已发布文章数倒序，取前8个）
     */
    List<Category> getHotCategories();
}
