package com.creation.platform.vo;

import lombok.Data;

/**
 * 用户创作数据统计 VO
 */
@Data
public class UserStatsVO {

    /**
     * 已发布文章数
     */
    private Integer articleCount;

    /**
     * 作品总浏览量
     */
    private Integer totalViews;

    /**
     * 作品获赞总数
     */
    private Integer totalLikes;

    /**
     * 粉丝数（暂无关注系统，默认返回0）
     */
    private Integer fanCount;
}
