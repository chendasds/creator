package com.creation.platform.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {

    /** 用户总数 */
    private Long totalUsers;

    /** 作品总数 */
    private Long totalArtworks;

    /** 今日新增作品 */
    private Long todayNewArtworks;

    /** 待审核作品数 */
    private Long pendingAuditCount;

    /** 分类占比数据 */
    private List<Map<String, Object>> categoryRatio;

    /** 七日趋势数据 */
    private Map<String, Object> trendData;
}
