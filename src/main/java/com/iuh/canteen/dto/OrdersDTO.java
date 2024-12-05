package com.iuh.canteen.dto;

import com.iuh.canteen.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * OrdersDTO Đối tượng truyền dữ liệu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDTO {

    private Long id;

    private UserDTO userDTO;

    private OrderStatus status; // NEW, PROCESSING, COMPLETED, CANCELLED

    private LocalDateTime createdAt;

    private LocalDateTime finishAt;

    private BigDecimal totalPrice;

    private String address;

    private List<OrderItemDTO> orderItemDTOS;

    private PaymentDTO paymentDTO;

    private PromotionDTO promotionDTO;
}
