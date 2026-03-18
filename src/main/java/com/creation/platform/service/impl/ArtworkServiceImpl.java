package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.entity.Artwork;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.vo.ArtworkVO;
import com.creation.platform.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<ArtworkVO> getAdminArtworkPage(Integer current, Integer size, String title, Long categoryId, Integer status) {
        Page<ArtworkVO> page = new Page<>(current, size);
        return artworkMapper.selectArtworkPage(page, title, categoryId, status);
    }

    @Override
    public DashboardVO getDashboardStats() {
        DashboardVO vo = new DashboardVO();

        // 1. 用户总数
        Long totalUsers = userMapper.selectCount(null);
        vo.setTotalUsers(totalUsers);

        // 2. 作品总数
        Long totalArtworks = artworkMapper.selectCount(null);
        vo.setTotalArtworks(totalArtworks);

        // 3. 今日新增作品
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        QueryWrapper<Artwork> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("create_time", todayStart);
        Long todayNewArtworks = artworkMapper.selectCount(todayWrapper);
        vo.setTodayNewArtworks(todayNewArtworks);

        // 4. 待审核作品数
        QueryWrapper<Artwork> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", 0);
        Long pendingAuditCount = artworkMapper.selectCount(pendingWrapper);
        vo.setPendingAuditCount(pendingAuditCount);

        // 5. 分类占比数据
        List<Map<String, Object>> categoryRatio = artworkMapper.selectCategoryRatio();
        vo.setCategoryRatio(categoryRatio);

        // 6. 七日趋势数据
        List<Map<String, Object>> trendList = artworkMapper.selectTrendData();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (Map<String, Object> item : trendList) {
            // 处理日期
            Object dateObj = item.get("dates");
            if (dateObj != null) {
                dates.add(dateObj.toString());
            }
            // 处理数量
            Object countObj = item.get("counts");
            if (countObj != null) {
                if (countObj instanceof Number) {
                    counts.add(((Number) countObj).longValue());
                } else {
                    counts.add(Long.parseLong(countObj.toString()));
                }
            }
        }
        Map<String, Object> trendData = new LinkedHashMap<>();
        trendData.put("dates", dates);
        trendData.put("counts", counts);
        vo.setTrendData(trendData);

        return vo;
    }
}
