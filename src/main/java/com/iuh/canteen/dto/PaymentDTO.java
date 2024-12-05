package com.iuh.canteen.dto;

import com.iuh.canteen.common.PaymentMethod;
import com.iuh.canteen.common.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PaymentDTO Đối tượng truyền dữ liệuĐối tượng truyền dữ liệu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDTO {

    private Long id;

    private OrdersDTO ordersDTO;

    private PaymentMethod paymentMethod; // MOMO, ZALOPAY, BANK_CARD, etc.

    private PaymentStatus status; // PENDING, COMPLETED, FAILED

    private BigDecimal amount;
}
