package com.kirana.controller;

import com.kirana.dto.ApiResponse;
import com.kirana.dto.DailySalesDto;
import com.kirana.dto.DashboardStatsDto;
import com.kirana.dto.TopSellingProductDto;
import com.kirana.dto.UserDto;
import com.kirana.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboardStatistics()));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<DailySalesDto>>> getSales() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDailySalesChartData()));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopSellingProductDto>>> getTopProducts() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getTopSellingProducts()));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<UserDto>>> getCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAllCustomers()));
    }
}
