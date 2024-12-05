package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * PromotionDTO Đối tượng truyền dữ liệu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromotionDTO {

    private Long id;

    private String code;

    private String description;

    private Integer quantity;

    private BigDecimal discountPercentage; // VD: 10 cho 10%

    private LocalDate startDate;

    private Boolean active;

    private LocalDate endDate;

    private StallDTO stallDTOl;

    private List<OrdersDTO> ordersDTOS;
}
