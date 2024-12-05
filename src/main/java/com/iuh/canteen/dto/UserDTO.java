package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * UserDTO Đối tượng truyền dữ liệu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private Long imageId;

    private String username;

    private String email;

    private String password;

    private String password2;

    private String phone;

    private boolean enabled;

    private String verificationCode;

    private BigDecimal balance;

    private String roles;

    private List<OrdersDTO> ordersDTOS;
}
