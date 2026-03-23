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
import com.creation.platform.dto.ArtworkPublishDTO;
import com.creation.platform.entity.Artwork;
import com.creation.platform.entity.Result;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.vo.ArtworkVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    /**
     * 公开作品流接口
     * 查询所有已发布(status=1)且未删除(is_deleted=0)的作品，按时间倒序分页返回
     *
     * @param current     当前页码，默认第1页
     * @param size        每页数量，默认10条
     * @param tagId       标签ID（可选，为 null 时返回全部作品）
     * @param categoryId  分类ID（可选，为 null 时返回全部作品）
     * @param userId      用户ID（可选，为 null 时返回全局作品流，为非空时返回该用户的作品列表）
     * @param isFollowFeed 关注流标识（可选，true 时仅返回当前登录用户关注的人发布的作品）
     * @return 分页后的作品列表（包含作者昵称和分类名称）
     */
    @GetMapping("/feed")
    public Result<Page<ArtworkVO>> getFeed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "false") Boolean isFollowFeed) {
        Long followerId = null;
        if (isFollowFeed) {
            followerId = (Long) request.getAttribute("userId");
            if (followerId == null) {
                return Result.error(401, "请先登录查看关注动态");
            }
        }
        Page<ArtworkVO> page = artworkService.getFeedPage(current, size, tagId, categoryId, userId, followerId);
        return Result.success(page);
    }

    /**
     * 作品详情接口
     * 根据ID查询作品详情，同时将浏览量+1，并返回当前用户的点赞/收藏状态
     *
     * @param request HTTP请求（用于获取当前登录用户ID）
     * @param id      作品ID
     * @return 作品详情（包含作者昵称、分类名称、点赞/收藏状态）
     */
    @GetMapping("/detail/{id}")
    public Result<ArtworkVO> getDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        ArtworkVO artworkVO = artworkService.getArtworkDetail(id, userId);
        if (artworkVO == null) {
            return Result.error("作品不存在");
        }
        return Result.success(artworkVO);
    }

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

    /**
     * 发布作品接口
     * 需要登录，从 JWT 中获取当前用户ID
     *
     * @param request HTTP请求（用于获取当前登录用户ID）
     * @param dto     发布信息DTO
     * @return 新增成功后的作品ID
     */
    @PostMapping("/publish")
    public Result<Long> publish(HttpServletRequest request, @RequestBody ArtworkPublishDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        Long artworkId = artworkService.publishArtwork(userId, dto);
        return Result.success(artworkId);
    }
}
