package com.creation.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creation.platform.entity.CreationRecord;
import com.creation.platform.entity.Result;
import com.creation.platform.service.CreationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/draft")
public class CreationRecordController {

    @Autowired
    private CreationRecordService creationRecordService;

    /**
     * 保存或更新草稿
     * MyBatis-Plus 会根据是否有 ID 自动判断是 Insert 还是 Update
     */
    @PostMapping("/save")
    public Result<Long> saveDraft(@RequestBody CreationRecord draft) {
        creationRecordService.saveOrUpdate(draft);
        return Result.success(draft.getId());
    }

    /**
     * 获取某人的草稿列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<CreationRecord>> getDraftList(@PathVariable Long userId) {
        LambdaQueryWrapper<CreationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreationRecord::getUserId, userId)
                    .orderByDesc(CreationRecord::getUpdateTime);
        return Result.success(creationRecordService.list(queryWrapper));
    }

    /**
     * 获取单篇草稿详情（用于继续编辑回显）
     */
    @GetMapping("/{id}")
    public Result<CreationRecord> getDraftDetail(@PathVariable Long id) {
        return Result.success(creationRecordService.getById(id));
    }

    /**
     * 删除草稿
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDraft(@PathVariable Long id) {
        creationRecordService.removeById(id);
        return Result.success();
    }
}
