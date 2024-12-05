package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * PasswordResetToken Mã đặt lại mật khẩu
 */
@Entity
@Table
@Data
@RequiredArgsConstructor
public class PasswordResetToken {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email đăng ký tài khoản
     */
    @Column(nullable = false)
    private String email;

    /**
     * Mã xác thực
     */
    @Column(nullable = false)
    private String token;

    /**
     * Thời gian hết hiệu lực
     */
    @Column(nullable = false)
    private LocalDateTime expiration;

    /**
     * Thời gian tạo
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

