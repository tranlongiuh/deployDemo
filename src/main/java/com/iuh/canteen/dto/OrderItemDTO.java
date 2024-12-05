package com.iuh.canteen.dto;

import com.iuh.canteen.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItemDTO Đối tượng truyền dữ liệu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemDTO {

    private Long id;

    private LocalDateTime createAt;

    private LocalDateTime finishAt;

    private OrdersDTO ordersDTO;

    private DishesDTO dishesDTO;

    private StallDTO stallDTO;

    private Integer quantity;

    private BigDecimal price; // Giá tại thời điểm đặt hàng

    private OrderStatus status;
    
    private UserDTO userDTO;
}
