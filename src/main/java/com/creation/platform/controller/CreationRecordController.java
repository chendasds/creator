package com.creation.platform.controller;

import com.creation.platform.entity.CreationRecord;
import com.creation.platform.service.CreationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creation-record")
public class CreationRecordController {

    @Autowired
    private CreationRecordService creationRecordService;

    @GetMapping("/{id}")
    public CreationRecord getById(@PathVariable Long id) {
        return creationRecordService.getById(id);
    }

    @GetMapping("/list")
    public List<CreationRecord> list() {
        return creationRecordService.list();
    }

    @GetMapping("/user/{userId}")
    public List<CreationRecord> listByUserId(@PathVariable Long userId) {
        return creationRecordService.lambdaQuery()
                .eq(CreationRecord::getUserId, userId)
                .list();
    }

    @PostMapping
    public boolean save(@RequestBody CreationRecord creationRecord) {
        return creationRecordService.save(creationRecord);
    }

    @PutMapping
    public boolean updateById(@RequestBody CreationRecord creationRecord) {
        return creationRecordService.updateById(creationRecord);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return creationRecordService.removeById(id);
    }
}
