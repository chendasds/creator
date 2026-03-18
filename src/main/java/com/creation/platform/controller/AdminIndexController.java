package com.creation.platform.controller;

import com.creation.platform.entity.Result;
import com.creation.platform.service.ArtworkService;
import com.creation.platform.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminIndexController {

    @Autowired
    private ArtworkService artworkService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        DashboardVO vo = artworkService.getDashboardStats();
        return Result.success(vo);
    }
}
