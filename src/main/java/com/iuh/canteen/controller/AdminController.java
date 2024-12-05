package com.iuh.canteen.controller;

import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController Bộ điều khiển dành cho quản trị viên
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    /**
     * getStallStat Lấy dữ liệu phí của các gian hàng
     *
     * @return
     */
    @GetMapping("/stallStat")
    ResponseEntity<?> getStallStat() {

        List<StallDTO> result;
        try {
            result = adminService.getStallStat();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    /**
     * setFee Đặt giá trị tính phí cho gian hàng
     *
     * @param fee Phí được tính với đơn vị phần trăm fee/100
     * @return
     */
    @PutMapping("/setFee")
    ResponseEntity<?> setFee(@RequestParam("fee") Integer fee) {

        try {
            if (adminService.setFee(fee)) {
                return ResponseEntity.ok()
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/createStall")
    ResponseEntity<?> createStall(@RequestParam("name") String name, @RequestParam("username") String username,
                                  @RequestParam("password") String password, @RequestParam("email") String email,
                                  @RequestParam("phone") String phone) {

        try {
            if (adminService.createStall(name, username, password, email, phone)) {
                return ResponseEntity.status(201)
                                     .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest()
                             .build();
    }
}
