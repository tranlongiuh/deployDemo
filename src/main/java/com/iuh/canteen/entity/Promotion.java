package com.iuh.canteen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Promotion Phiếu giảm giá
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã giảm giá
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * Thông tin
     */
    @Column
    private String description;

    /**
     * Giá trị giảm giá theo phần trăm (vd: 10 cho 10%)
     */
    @Column(nullable = false)
    private BigDecimal discountPercentage;

    /**
     * Ngày bắt đầu
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * Số lượng
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Trạng thái
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Ngày kết thúc
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * Gian hàng áp dụng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_stall_id", nullable = false)
    private Stall stall;

    /**
     * Đơn hàng áp dụng
     */
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> orders;
}
