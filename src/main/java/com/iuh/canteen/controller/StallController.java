package com.iuh.canteen.controller;

import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.StallService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StallController Bộ điều khiển gian hàng
 */
@RestController
@RequestMapping("/api/stalls")
public class StallController {

    @Autowired
    private StallService stallService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/details")
    ResponseEntity<?> getDetails(HttpServletRequest request) {

        StallDTO result;
        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            result = stallService.findByManagerName(username);
            return ResponseEntity.ok()
                                 .body(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound()
                             .build();
    }
}
