package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ScheduleDTO Đối tượng truyền dữ liệu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleDTO {

    private Long id;

    private Integer day;

    private StallDTO stallDTO;

    private List<DishesDTO> dishesDTOS;
}
