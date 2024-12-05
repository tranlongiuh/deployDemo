package com.iuh.canteen.entity;

import com.iuh.canteen.common.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem Mục đơn hàng
 * là mục được lưu trữ trong Đơn Hàng để xác định Món ăn và một số thông tin bổ sung
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Thời gian tạo
     */
    @Column(nullable = false)
    private LocalDateTime createAt;

    /**
     * Thời gian kết thúc trạng thái (vd: khi Trạng thái Mục đơn hàng là PAID. Có nghĩa là đã thanh toán vào lúc finishAt)
     */
    private LocalDateTime finishAt;

    /**
     * Đơn hàng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Orders orders;

    /**
     * Món ăn
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Dishes dishes;

    /**
     * Gian hàng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stall_id")
    private Stall stall;

    /**
     * Số lượng Món ăn
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Giá tại thời điểm đặt hàng. Cũng là giá thanh toán.
     * Khi Gian hàng giảm giá Món ăn sau khi bạn đặt hàng,
     * bạn vẫn sẽ tính tiền theo giá ban đầu khi bạn đặt tức trước khi giảm giá
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Trạng thái
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
}