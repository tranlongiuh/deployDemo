package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse Thông tin phản hồi sau khi đăng nhập
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;

    private String role;
}
