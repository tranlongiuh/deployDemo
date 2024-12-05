package com.iuh.canteen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Tài khoản
 */
@Entity
@Table
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên Tài khoản
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Email
     */
    @Column(unique = true)
    private String email;

    /**
     * Mã Hình ảnh
     */
    private Long imageId;

    /**
     * Mật khẩu
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    /**
     * Mật khẩu cấp 2
     */
    private String password2;

    /**
     * Số điện thoại
     */
    @Column(nullable = false, unique = true)
    private String phone;

    /**
     * Trạng thái
     */
    private boolean enabled;

    /**
     * Mã xác thực
     */
    private String verificationCode;

    /**
     * Số dư
     */
    private BigDecimal balance;

    /**
     * Quyền truy cập
     */
    @Column(nullable = false)
    private String roles;

    /**
     * Danh sách Đơn đặt hàng
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Orders> orders = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Arrays.stream(roles.split(","))
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }
}
