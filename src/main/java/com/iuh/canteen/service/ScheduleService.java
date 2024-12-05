package com.iuh.canteen.service;

import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.ScheduleDTO;
import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.entity.Schedule;

import java.util.List;

public interface ScheduleService {

    Boolean addDishesToSchedule(Integer day, List<Long> dishesIds, String username);

    Boolean removeDishesFromSchedule(Integer day, List<Long> dishesIds, String username);

    Schedule save(Schedule schedule);

    // Additional methods you might want to implement
    Schedule getScheduleByDayAndStall(Integer day, Long stallId);

    List<Schedule> getSchedulesForStall(Long stallId);

    ScheduleDTO findByDay(Integer day, String username);

    List<Schedule> findByDishesId(Long id);

    List<DishesDTO> findToDayDishesByStallId(Long idStall);

    List<StallDTO> getScheduleForCustomer();

    List<Schedule> findByStallId(Long id);
}

