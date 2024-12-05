package com.iuh.canteen.controller;

import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.OrdersServiceImpl;
import com.iuh.canteen.service.impl.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PaymentController Bộ điều khiển thanh toán
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * payment Thanh toán Đơn đặt hàng
     *
     * @param orderId         Mã Đơn đặt hàng
     * @param confirmPassword Mật khẩu cấp 2
     * @param request
     * @return
     */
    @PostMapping
    ResponseEntity<?> payment(@RequestParam Long orderId, @RequestParam String confirmPassword,
                              HttpServletRequest request) {

        OrdersDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (paymentService.purchase(username, orderId, confirmPassword)) {
                result = modelMapper.map(ordersService.findById(orderId), OrdersDTO.class);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }
}
