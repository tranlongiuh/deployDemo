package com.iuh.canteen.dto;

import com.iuh.canteen.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DishesDTO Đối tượng truyền dữ liệu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishesDTO {

    private Long id;

    private String name;

    private String description;

    private Integer quantity;

    private Integer reviews;

    private Integer stars;

    private BigDecimal price;

    private Long imageId;

    private Category category;

    private List<OrderItemDTO> orderItemDTOS;

    private StallDTO stallDTO;
}
