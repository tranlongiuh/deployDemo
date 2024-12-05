package com.iuh.canteen.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.OrdersServiceImpl;
import com.iuh.canteen.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrdersController Bộ điều khiển đơn hàng
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * addOrder Tạo Đơn hàng mới
     *
     * @param request
     * @param dishesListJson      Danh sách Món ăn dạng Json
     * @param paymentMethod       Phương thức thanh toán
     * @param selectedPromotionId Mã Phiếu giảm giá
     * @param address             Địa chỉ giao hàng
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addOrder(HttpServletRequest request,
                                      @RequestParam("dishesList") String dishesListJson,
                                      @RequestParam("paymentMethod") String paymentMethod,
                                      @RequestParam("selectedPromotionId") Long selectedPromotionId,
                                      @RequestParam("address") String address,
                                      @RequestParam("amount") BigDecimal amount) {

        OrdersDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            // kiem tra so du
            if (userService.checkAmount(username, amount)) {
                ObjectMapper objectMapper = new ObjectMapper();
                if (dishesListJson == null || dishesListJson.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                List<DishesDTO> dishesList = objectMapper.readValue(dishesListJson,
                        new TypeReference<List<DishesDTO>>() {

                        });
                result = modelMapper.map(
                        ordersService.addOrder(username, dishesList, paymentMethod, selectedPromotionId,
                                address), OrdersDTO.class);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(201)
                                     .body("Số dư không đủ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getOrders Lấy danh sách Đơn đặt hàng
     *
     * @param request
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getOrders(HttpServletRequest request) {

        List<OrdersDTO> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = ordersService.getOrdersByUsername(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getProcessingOrders Lấy danh sách Đơn đặt hàng đang trong quá trình nấu
     *
     * @param request
     * @return
     */
    @GetMapping("/processing")
    public ResponseEntity<?> getProcessingOrders(HttpServletRequest request) {

        List<OrdersDTO> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = ordersService.getProcessingOrdersByUsername(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * getWaitingOrders Lấy danh sách Đơn đặt hàng đang đợi Thực khách đến nhận
     *
     * @param request
     * @return
     */
    @GetMapping("/waiting")
    public ResponseEntity<?> getWaitingOrders(HttpServletRequest request) {

        List<OrdersDTO> result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = ordersService.getWaitingOrdersByUsername(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteOrders(HttpServletRequest request, @RequestParam Long orderId) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (ordersService.deleteOrder(username, orderId)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }
}
