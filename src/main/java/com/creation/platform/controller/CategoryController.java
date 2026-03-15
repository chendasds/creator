package com.creation.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creation.platform.entity.Category;
import com.creation.platform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/list")
    public List<Category> list() {
        return categoryService.list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @GetMapping("/page")
    public Page<Category> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Category> page = new Page<>(current, size);
        return categoryService.page(page, new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @PostMapping
    public boolean save(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping
    public boolean updateById(@RequestBody Category category) {
        return categoryService.updateById(category);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return categoryService.removeById(id);
    }
}
