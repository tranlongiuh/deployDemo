package com.iuh.canteen.controller;

import com.iuh.canteen.dto.LoginResponse;
import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.PasswordResetTokenService;
import com.iuh.canteen.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController Bộ điều khiển xác thực người dùng
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    /**
     * registerUser Đăng ký tài khoản dành cho Thực khách
     *
     * @param email    Email
     * @param phone    Số điện thoại
     * @param username Tên người dùng
     * @param password Mật khẩu
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String email, @RequestParam String phone,
                                          @RequestParam String username, @RequestParam String password) {

        UserDTO newUser = new UserDTO();
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setUsername(username);
        newUser.setPassword(password);
        if (!userService.existsByEmail(newUser.getEmail())) {
            if (!userService.existsByUsername(newUser.getUsername())) {
                if (userService.registerUser(newUser)) {
                    return new ResponseEntity<>("Đăng ký thành công! Vui lòng kiểm tra email của bạn để xác thực.",
                            HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Đăng ký thất bại!",
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Username đã tồn tại!",
                        HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>("Email đã tồn tại!",
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * verifyUser Xác thực tài khoản cho Thực khách
     *
     * @param code Mã nhận tại email được đăng ký
     * @return
     */
    @GetMapping("/enable")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {

        if (userService.existsByVerificationCode(code)) {
            if (userService.enable(code)) {
                return new ResponseEntity<>("Xác thực thành công!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Xác thực không thành công!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Không tìm thấy mã xác thực của bạn!", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * login Đăng nhập
     *
     * @param username Tên người dùng
     * @param password Mật khẩu
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String username, @RequestParam String password) {

        if (userService.existsByUsername(username)) {
            if (userService.verify(username, password)) {
                String role = userService.getResponseRole(username);
                String jwt = userService.login(username);
                return ResponseEntity
                        .ok(new LoginResponse(jwt, role));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(new LoginResponse()
                                     );
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new LoginResponse());
        }
    }

    /**
     * forgotPassword Gửi yêu cầu đặt lại mật khẩu
     *
     * @param email Email đã đăng ký tài khoản
     * @return
     */
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {

        if (userService.existsByEmail(email)) {
            if (userService.sendMailResetPassword(email)) {
                return ResponseEntity.ok("Hãy kiểm tra email của bạn!");
            } else {
                System.out.println("Không gửi được email đến người nhận!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Không gửi được email đến người nhận!");
            }
        } else {
            System.out.println("Email không tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Email không tồn tại!");
        }
    }

    /**
     * getReset Xác thực người dùng trước khi đặt lại mật khẩu
     *
     * @param token Mã xác thực được gửi đên Email đăng ký tài khoản
     * @return
     */
    @PostMapping("/getReset")
    public ResponseEntity<?> getReset(@RequestParam("token") String token) {

        if (passwordResetTokenService.existsByToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Invalid token");
        }
    }

    /**
     * resetPassword Đặt lại mật khẩu mới
     *
     * @param token    Mã xác thực được gửi đên Email đăng ký tài khoản
     * @param password Mật khẩu mới
     * @return
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("password") String password) {

        if (passwordResetTokenService.existsByToken(token)) {
            if (userService.resetPassword(token, password)) {
                return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Đặt lại mật khẩu thất bại!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Token không tồn tại!");
        }
    }

    /**
     * setPassword2 Tạo mật khẩu cấp 2
     *
     * @param password2 Mật khẩu cấp 2
     * @param request
     * @return
     */
    @PostMapping("/setPass2")
    public ResponseEntity<?> setPassword2(@RequestParam("password2") String password2, HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            if (userService.setPassword2(username, password2)) {
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
