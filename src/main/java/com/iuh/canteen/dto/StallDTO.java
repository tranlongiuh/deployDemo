package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * StallDTO Đối tượng truyền dữ liệu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StallDTO {

    private Long id;

    private String name;

    private UserDTO manager;

    private Long imageId;

    private BigDecimal revenue;

    private BigDecimal serviceFee;

    private List<DishesDTO> dishesDTOS;

    private List<PromotionDTO> promotionDTOS;
}
