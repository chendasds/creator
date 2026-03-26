package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.dto.ArtworkPublishDTO;
import com.creation.platform.entity.Artwork;
import com.creation.platform.vo.ArtworkVO;
import com.creation.platform.vo.DashboardVO;
import com.creation.platform.vo.InteractionTrendVO;
import java.util.List;
public interface ArtworkService extends IService<Artwork> {

    /**
     * 后台管理作品分页查询（联表）
     */
    Page<ArtworkVO> getAdminArtworkPage(Integer current, Integer size, String title, Long categoryId, Integer status);

    /**
     * 公开作品流分页查询
     * 查询 status=1 且 is_deleted=0 的作品，按时间倒序返回
     *
     * @param tagId       标签ID（可选，为 null 时不过滤）
     * @param categoryId  分类ID（可选，为 null 时不过滤）
     * @param userId      用户ID（可选，用于查询指定用户的作品列表）
     * @param followerId  关注者ID（可选，为非空时仅返回该用户关注的人发布的作品）
     * @param sortType   排序方式（可选，默认 recommend；recommend=热度推荐，time=最新发布）
     * @param keyword    关键字（可选，同时模糊匹配标题和简介）
     */
    Page<ArtworkVO> getFeedPage(Integer current, Integer size, Long tagId, Long categoryId, Long userId, Long followerId, String sortType, String keyword);

    /**
     * 获取作品详情
     * 根据ID查询作品详情，同时将浏览量+1，并返回当前用户的点赞/收藏状态
     *
     * @param id     作品ID
     * @param userId 当前登录用户ID（可为null，表示未登录）
     * @return 作品详情（包含作者名、分类名、点赞/收藏状态），不存在则返回null
     */
    ArtworkVO getArtworkDetail(Long id, Long userId);

    /**
     * 获取仪表盘统计数据（管理员全局统计）
     */
    DashboardVO getDashboardStats();

    /**
     * 获取创作者个人数据统计
     *
     * @param userId 创作者用户ID
     * @return 创作者统计数据（作品数、总浏览量、总点赞数、粉丝数）
     */
    DashboardVO getCreatorDashboardStats(Long userId);

    /**
     * 获取创作者近7天互动趋势（每日点赞数和收藏数）
     *
     * @param userId 创作者用户ID
     * @return 近7天每日互动数据列表
     */
    List<InteractionTrendVO> getRecentInteractionTrend(Long userId);

    /**
     * 发布作品
     * 将作品信息存入数据库，并自动计算字数
     *
     * @param userId  当前登录用户ID
     * @param dto     发布信息DTO
     * @return 新增成功后的作品ID
     */
    Long publishArtwork(Long userId, ArtworkPublishDTO dto);

    /**
     * 修改正式作品（包含标签更新）
     * 更新作品基本信息，并同步更新标签关联关系
     *
     * @param id  作品ID
     * @param dto 修改信息DTO
     */
    void updateArtwork(Long id, ArtworkPublishDTO dto);
}
