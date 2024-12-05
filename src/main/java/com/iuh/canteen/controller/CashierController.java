package com.iuh.canteen.controller;

import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.CashierServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CashierController Bộ điều khiển dành cho Thu ngân
 */
@RestController
@RequestMapping("/api/cashier")
public class CashierController {

    @Autowired
    private CashierServiceImpl cashierService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * getUser Tìm thực khách bằng Tên người dùng
     *
     * @param username Tên người dùng
     * @return
     */
    @GetMapping("/find/{username}")
    ResponseEntity<?> getUser(@PathVariable String username) {

        UserDTO result;
        try {
            result = cashierService.findCustomerByUsername(username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound()
                                 .build();
        }
    }

    /**
     * deposit Nạp tiền cho Thực khách
     *
     * @param idUser Mã Tài khoản của Thực khách
     * @param money  Số tiền nạp
     * @return
     */
    @PostMapping("/deposit")
    ResponseEntity<?> deposit(@RequestParam("idUser") Long idUser, @RequestParam("money") Integer money,
                              HttpServletRequest request) {

        UserDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = cashierService.depositForCustomer(idUser, money, username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound()
                                 .build();
        }
    }
}
