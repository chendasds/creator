package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Category;
import com.creation.platform.mapper.CategoryMapper;
import com.creation.platform.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public boolean saveWithRecovery(Category category) {
        // 穿透查询，无视逻辑删除
        Category existCategory = baseMapper.selectByNameIgnoreLogicDelete(category.getName());

        if (existCategory != null) {
            if (existCategory.getIsDeleted() == 1) {
                // 1. 发现被作废的数据 -> 覆盖属性 -> 执行复活
                existCategory.setDescription(category.getDescription());
                existCategory.setSortOrder(category.getSortOrder());
                baseMapper.resurrectCategory(existCategory);
                // 2. 复活成功后，立刻 return，终止后续逻辑
                return true;
            } else {
                // 3. 发现存活的数据 -> 抛出异常
                throw new RuntimeException("该分类名称已存在，请更换");
            }
        }

        // 4. 数据库中完全没有同名数据 -> 执行正常的新增
        return this.save(category);
    }
}
