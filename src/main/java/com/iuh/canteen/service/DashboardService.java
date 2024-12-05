package com.iuh.canteen.service;

import com.iuh.canteen.dto.DashboardStats;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.dto.TransactionData;

import java.util.List;

public interface DashboardService {

    DashboardStats getCanteenStats(String username);

    List<TransactionData> getMonthlyTransactions(String username);

    List<OrdersDTO> getRecentOrders(String username);
}
