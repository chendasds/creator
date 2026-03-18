package com.creation.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creation.platform.entity.Category;
import com.creation.platform.entity.Result;
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
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)));
    }

    @GetMapping("/page")
    public Result<Page<Category>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Category> page = new Page<>(current, size);
        return Result.success(categoryService.page(page, new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Category category) {
        return Result.success(categoryService.saveWithRecovery(category));
    }

    @PutMapping
    public Result<Boolean> updateById(@RequestBody Category category) {
        return Result.success(categoryService.updateById(category));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> removeById(@PathVariable Long id) {
        return Result.success(categoryService.removeById(id));
    }
}
