package com.iuh.canteen.entity;

import com.iuh.canteen.common.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orders Đơn hàng
 */
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Người đặt
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Trạng thái
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Thời gian đặt
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian kết thúc
     */
    @Column
    private LocalDateTime finishAt;

    /**
     * Tổng chi phí
     */
    @Column(nullable = false)
    private BigDecimal totalPrice;

    /**
     * Địa chỉ giao
     * Nếu để trống có nghĩa là bạn chọn phương thức nhận tại quầy
     */
    private String address;

    /**
     * Danh sách các Mục đơn hàng trong Đơn hàng
     */
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    /**
     * Sự thanh toán
     */
    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    /**
     * Mã giảm giá
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
}