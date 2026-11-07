package com.kirana.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {
    private long totalOrders;
    private long todayOrders;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal todayRevenue = BigDecimal.ZERO;
    private long totalProducts;
    private long lowStockCount;
    private long totalCustomers;
    private long pendingOrders;
    private List<OrderDto> recentOrders = new ArrayList<>();

    public DashboardStatsDto() {}

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getTodayOrders() { return todayOrders; }
    public void setTodayOrders(long todayOrders) { this.todayOrders = todayOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public List<OrderDto> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderDto> recentOrders) { this.recentOrders = recentOrders; }
}
