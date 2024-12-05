package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.DashboardStats;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.service.DashboardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private StallServiceImpl stallService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * getCanteenStats Lấy Thông tin chung của căng tin dựa trên quyền của Tài khoản truy cập
     *
     * @param username tên Tài khoản
     * @return DashboardStats
     */
    @Override
    public DashboardStats getCanteenStats(String username) {

        if (!username.isEmpty()) {
            if (userService.existsByUsername(username)) {
                User user = userService.loadUserByUsername(username);
                if (user
                        .getRoles()
                        .contains("MANAGER")) {
                    Long idUser = user.getId();
                    Long idStall = stallService.findByManagerId(idUser)
                                               .getId();
                    return new DashboardStats(stallService.calculateTotalSales(idStall), BigDecimal.ZERO,
                            stallService.countCustomers(idStall), stallService.countTotalOrders(idStall));
                } else {
                    BigDecimal totalExpenses = BigDecimal.ZERO;
                    BigDecimal totalSales = ordersService.calculateTotalSales();
                    // Check for null or negative total sales
                    if (totalSales == null || totalSales.compareTo(BigDecimal.ZERO) == -1) {
                        System.err.println("Error: totalSales is null or non-positive");
                        totalSales = BigDecimal.ZERO;
                    } else {
                        // Calculate 5% of totalSales as expenses
                        BigDecimal percentage = stallService.getServiceFee()
                                                            .divide(BigDecimal.valueOf(100));
                        totalExpenses = totalSales.multiply(percentage);
                        // Subtract totalExpenses from totalSales
                        totalSales = totalSales.subtract(totalExpenses);
                    }
                    long totalCustomers = userService.countCustomers();
                    long totalOrders = ordersService.countTotalOrders();
                    return new DashboardStats(totalSales, totalExpenses, totalCustomers, totalOrders);
                }
            } else {
                System.err.println("user not found");
            }
        } else {
            System.err.println("username is empty");
        }
        return new DashboardStats();
    }

    /**
     * getMonthlyTransactions Lấy thông tin thống kê theo từng tháng dựa trên quyền của Tài khoản truy cập
     *
     * @param username tên Tài khoản
     * @return List TransactionData
     */
    @Override
    public List<TransactionData> getMonthlyTransactions(String username) {

        List<TransactionData> result = new ArrayList<>();
        if (!username.isEmpty()) {
            if (userService.existsByUsername(username)) {
                User user = userService.loadUserByUsername(username);
                if (user
                        .getRoles()
                        .contains("MANAGER")) {
                    Long idUser = user.getId();
                    Long idStall = stallService.findByManagerId(idUser)
                                               .getId();
                    result = stallService.findMonthlyTransactions(idStall);
                } else {
                    result = ordersService.findMonthlyTransactions();
                }
            } else {
                System.err.println("user not found");
            }
        } else {
            System.err.println("username is empty");
        }
        return result;
    }

    /**
     * getRecentOrders Lấy thông tin đơn hàng gần nhất dựa trên quyền của Tài khoản truy cập
     *
     * @param username tên Tài khoản
     * @return List OrdersDTO
     */
    @Override
    public List<OrdersDTO> getRecentOrders(String username) {

        List<OrdersDTO> result = new ArrayList<>();
        if (!username.isEmpty()) {
            if (userService.existsByUsername(username)) {
                User user = userService.loadUserByUsername(username);
                List<Orders> orders;
                if (user
                        .getRoles()
                        .contains("MANAGER")) {
                    Long idUser = user.getId();
                    Long idStall = stallService.findByManagerId(idUser)
                                               .getId();
                    orders = stallService.findTop5ByOrderByOrderDateDesc(idStall);
                } else {
                    orders = ordersService.findTop5ByOrderByOrderDateDesc();
                }
                processOrders(result, orders);
            } else {
                System.err.println("User not found");
            }
        } else {
            System.err.println("Username is empty");
        }
        return result;
    }

    /**
     * processOrders Chuyển đổi đối tượng sang dữ liệu
     *
     * @param result List OrdersDTO
     * @param orders List Orders
     */
    private void processOrders(List<OrdersDTO> result, List<Orders> orders) {

        orders.forEach(order -> {
            OrdersDTO ordersDTO = modelMapper.map(order, OrdersDTO.class);
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(order.getUser()
                                     .getUsername());
            ordersDTO.setUserDTO(userDTO);
            result.add(ordersDTO);
        });
    }
}

