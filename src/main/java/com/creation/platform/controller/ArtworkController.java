/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 16:59:00
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-18 19:36:49
 * @FilePath: \build-one\src\main\java\com\creation\platform\controller\ArtworkController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creation.platform.entity.Artwork;
import com.creation.platform.entity.Result;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.vo.ArtworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    @GetMapping("/{id}")
    public Artwork getById(@PathVariable Long id) {
        return artworkService.getById(id);
    }

    @GetMapping("/list")
    public List<Artwork> list() {
        return artworkService.list();
    }

    @GetMapping("/page")
    public Page<Artwork> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Page<Artwork> page = new Page<>(current, size);
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Artwork::getUserId, userId);
        }
        if (categoryId != null) {
            wrapper.eq(Artwork::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Artwork::getStatus, status);
        }
        wrapper.orderByDesc(Artwork::getCreateTime);
        return artworkService.page(page, wrapper);
    }

    @PostMapping
    public boolean save(@RequestBody Artwork artwork) {
        return artworkService.save(artwork);
    }

    @PutMapping
    public boolean updateById(@RequestBody Artwork artwork) {
        return artworkService.updateById(artwork);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return artworkService.removeById(id);
    }

    @PutMapping("/view/{id}")
    public boolean incrementViewCount(@PathVariable Long id) {
        Artwork artwork = artworkService.getById(id);
        if (artwork != null) {
            artwork.setViewCount(artwork.getViewCount() + 1);
            return artworkService.updateById(artwork);
        }
        return false;
    }

    /**
     * 后台管理作品分页查询
     */
    @GetMapping("/admin/page")
    public Result<Page<ArtworkVO>> adminPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Page<ArtworkVO> page = artworkService.getAdminArtworkPage(current, size, title, categoryId, status);
        return Result.success(page);
    }

    /**
     * 后台管理作品审核
     */
    @PutMapping("/admin/audit")
    public Result<Boolean> audit(@RequestParam Long id, @RequestParam Integer targetStatus) {
        Artwork artwork = artworkService.getById(id);
        if (artwork == null) {
            return Result.error("作品不存在");
        }
        artwork.setStatus(targetStatus);
        boolean result = artworkService.updateById(artwork);
        return Result.success(result);
    }
}
