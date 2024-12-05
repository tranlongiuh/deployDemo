package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DashboardStats Thông tin chung trong Bảng điều khiển
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {

    private BigDecimal totalSales;

    private BigDecimal totalExpenses;

    private long totalCustomers;

    private long totalOrders;
}
