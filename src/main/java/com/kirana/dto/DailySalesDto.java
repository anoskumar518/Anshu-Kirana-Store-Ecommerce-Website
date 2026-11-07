package com.kirana.dto;

import java.math.BigDecimal;

public class DailySalesDto {
    private String date;
    private long orderCount;
    private BigDecimal revenue;

    public DailySalesDto() {}

    public DailySalesDto(String date, long orderCount, BigDecimal revenue) {
        this.date = date;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
