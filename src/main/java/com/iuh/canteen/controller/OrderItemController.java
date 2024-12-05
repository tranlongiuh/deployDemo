package com.iuh.canteen.controller;

import com.iuh.canteen.dto.OrderItemDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.security.SignatureUtils;
import com.iuh.canteen.service.impl.OrderItemServiceImpl;
import com.iuh.canteen.service.impl.OrdersServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Bộ điều khiển mục đơn hàng
 */
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemServiceImpl orderItemService;

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * getOrderItems Lấy danh sách Mục đơn hàng
     *
     * @param request
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getOrderItems(HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            List<OrderItemDTO> result = orderItemService.getOrderItems(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                                 .body("Error fetching order items");
        }
    }

    /**
     * takeOrderItems Gian hàng nhận và bắt đầu thực hiện Món ăn có trong Mục đơn hàng
     *
     * @param request
     * @param id      Mã Mục đơn hàng
     * @return
     */
    @PutMapping("/making/{id}")
    public ResponseEntity<?> takeOrderItems(HttpServletRequest request, @PathVariable Long id) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            orderItemService.updateOrderItemStatus(id, username, "PROCESSING");
            return ResponseEntity.ok("Order is being processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                                 .body("Error taking order item");
        }
    }

    /**
     * finishOrderItems Gian hàng hoàn thàn Món ăn có trong Mục đơn hàng
     *
     * @param request
     * @param id      Mã Mục đơn hàng
     * @return
     */
    @PutMapping("/finish/{id}")
    public ResponseEntity<?> finishOrderItems(HttpServletRequest request, @PathVariable Long id) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            orderItemService.updateOrderItemStatus(id, username, "WAITING");
            return ResponseEntity.ok("Order has been finished");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                                 .body("Error finishing order item");
        }
    }

    /**
     * cancelOrderItems Gian hàng hủy Món ăn có trong Mục đơn hàng
     *
     * @param request
     * @param id      Mã Mục đơn hàng
     * @return
     */
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrderItems(HttpServletRequest request, @PathVariable Long id) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            orderItemService.updateOrderItemStatus(id, username, "CANCELLED");
            return ResponseEntity.ok("Order has been cancelled");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                                 .body("Error canceling order item");
        }
    }

    /**
     * verifyOrder Gian hàng xác thực Mục đơn hàng mà khách hàng muốn nhận (Quét mã QR)
     *
     * @param idOrder   Mã Mục đơn hàng mà khách hàng muốn nhận
     * @param signature Chữ ký gồm Mã Mục đơn hàng mà khách hàng muốn nhận và Khóa bảo mật của hệ thống
     * @param request
     * @return
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOrder(@RequestParam Long idOrder, @RequestParam String signature,
                                         HttpServletRequest request) {

        List<OrderItemDTO> rs;
        try {
            boolean isValid = SignatureUtils.verifySignature(idOrder.toString(), signature);
            if (isValid) {
                String jwt = jwtUtil.getJwtFromRequest(request);
                String username = jwtUtil.extractUsername(jwt);
                rs = orderItemService.takingItemInStall(idOrder, username);
                if (!rs.isEmpty()) {
                    return ResponseEntity.ok(rs);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                         .body("Invalid signature");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error verifying signature");
        }
    }

    /**
     * giveItem Giao Món ăn có trong Mục đơn hàng
     *
     * @param id      Mã Mục đơn hàng
     * @param request
     * @return
     */
    @PostMapping("/give/{id}")
    public ResponseEntity<?> giveItem(@PathVariable Long id,
                                      HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (orderItemService.giveItem(id, username)) {
                ordersService.updateStatusByOrderItemId(id);
                return ResponseEntity.ok()
                                     .build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error verifying signature");
        }
    }
}
