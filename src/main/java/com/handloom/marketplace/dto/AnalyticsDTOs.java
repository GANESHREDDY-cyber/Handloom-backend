package com.handloom.marketplace.dto;

import java.math.BigDecimal;

public class AnalyticsDTOs {

    public static class DashboardStats {
        private Long totalUsers;
        private Long totalArtisans;
        private Long totalProducts;
        private Long totalOrders;
        private BigDecimal revenue;
        private Long pendingApprovals;
        public DashboardStats() {}
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        public Long getTotalArtisans() { return totalArtisans; }
        public void setTotalArtisans(Long totalArtisans) { this.totalArtisans = totalArtisans; }
        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Long getPendingApprovals() { return pendingApprovals; }
        public void setPendingApprovals(Long pendingApprovals) { this.pendingApprovals = pendingApprovals; }
    }
}
