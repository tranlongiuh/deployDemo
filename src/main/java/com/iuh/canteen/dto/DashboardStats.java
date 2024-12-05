package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DashboardStats Thông tin chung trong Bảng điều khiển
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {

	private String totalSales;

	private String totalExpenses;

	private long totalCustomers;

	private long totalOrders;
}
