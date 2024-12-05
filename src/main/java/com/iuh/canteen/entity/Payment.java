package com.iuh.canteen.entity;

import com.iuh.canteen.common.PaymentMethod;
import com.iuh.canteen.common.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment Sự thanh toán
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    /**
     * Mã
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Đơn hàng
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    /**
     * Phương thức thanh toán
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Trạng thái thanh toán
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Số tiền thanh toán
     */
    @Column(nullable = false)
    private BigDecimal amount;
}