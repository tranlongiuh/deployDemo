package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Stall Gian hàng
 */
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Stall {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên
     */
    @Column(nullable = false)
    private String name;

    /**
     * Mã Người quản lý
     */
    @Column(nullable = false)
    private Long managerId;

    /**
     * Mã Hình ảnh
     */
    private Long imageId;

    /**
     * Doanh thu
     */
    private BigDecimal revenue;

    /**
     * Phí dịch vụ (tính theo phần trăm)
     */
    private BigDecimal serviceFee;

    /**
     * Danh sách Món ăn
     */
    @OneToMany(mappedBy = "stall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dishes> dishes;

    /**
     * Danh sách Phiếu giảm giá
     */
    @OneToMany(mappedBy = "stall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Promotion> promotions;
}
