package com.iuh.canteen.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iuh.canteen.dto.DashboardStats;
import com.iuh.canteen.dto.OrderItemDTO;
import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.OrderItemService;
import com.iuh.canteen.service.StallService;
import com.iuh.canteen.service.impl.DashboardServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Bộ điều khiển thông tin trong bảng điều khiển
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private DashboardServiceImpl dashboardService;

	@Autowired
	private StallService stallService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * getDashboardStats Lấy thông tin chung
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/stats")
	public ResponseEntity<DashboardStats> getDashboardStats(
			HttpServletRequest request) {

		DashboardStats result;
		try {
			String jwt = jwtUtil.getJwtFromRequest(request);
			String username = jwtUtil.extractUsername(jwt);
			result = dashboardService.getCanteenStats(username);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}

	/**
	 * getMonthlyTransactions Lấy dữ liệu biểu đồ hằng tháng
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/transactions")
	public ResponseEntity<?> getMonthlyTransactions(
			HttpServletRequest request) {

		List<TransactionData> result;
		try {
			String jwt = jwtUtil.getJwtFromRequest(request);
			String username = jwtUtil.extractUsername(jwt);
			result = dashboardService.getMonthlyTransactions(username);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}

	/**
	 * getRecentOrders Lấy dữ liệu Đơn đặt hàng gần nhất
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/recent-orders")
	public ResponseEntity<List<OrderItemDTO>> getRecentOrders(
			HttpServletRequest request,
			@RequestParam("selectedDate") String selectedDate) {

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			LocalDateTime dateTime = LocalDateTime.parse(selectedDate,
					formatter);

			LocalDate date = dateTime.toLocalDate();
			LocalDateTime startOfDay = LocalDateTime.of(date,
					LocalTime.MIDNIGHT);
			LocalDateTime endOfDay = startOfDay.plusDays(1);

			System.out.println("Start of the day: " + startOfDay);
			System.out.println("End of the day: " + endOfDay);
			String jwt = jwtUtil.getJwtFromRequest(request);
			String username = jwtUtil.extractUsername(jwt);
			// Stall stall = stallService.findByManagerId(user.getId());
			StallDTO stallDTO = stallService.findByManagerName(username);
			Stall stall = stallService.findById(stallDTO.getId());
			List<OrderItemDTO> orderItemDTOs = orderItemService
					.findTodayOrderItemByStallAndTime(stall, startOfDay,
							endOfDay);
			return ResponseEntity.ok().body(orderItemDTOs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}
}
