package com.iuh.canteen.controller;

import com.iuh.canteen.dto.DashboardStats;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.DashboardServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bộ điều khiển thông tin trong bảng điều khiển
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * getDashboardStats Lấy thông tin chung
     *
     * @param request
     * @return
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats(HttpServletRequest request) {

        DashboardStats result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = dashboardService.getCanteenStats(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getMonthlyTransactions Lấy dữ liệu biểu đồ hằng tháng
     *
     * @param request
     * @return
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getMonthlyTransactions(HttpServletRequest request) {

        List<TransactionData> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = dashboardService.getMonthlyTransactions(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getRecentOrders Lấy dữ liệu Đơn đặt hàng gần nhất
     *
     * @param request
     * @return
     */
    @GetMapping("/recent-orders")
    public ResponseEntity<?> getRecentOrders(HttpServletRequest request) {

        List<OrdersDTO> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = dashboardService.getRecentOrders(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }
}
