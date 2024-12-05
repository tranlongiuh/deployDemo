package com.iuh.canteen.repository;

import com.iuh.canteen.entity.Schedule;
import com.iuh.canteen.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByDayAndStall(@Param("day") Integer day, @Param("stall") Stall stall);

    List<Schedule> findByStall(Stall stall);

    @Query("SELECT s FROM Schedule s JOIN s.dishesList d WHERE d.id = :dishesId")
    List<Schedule> findByDishesId(@Param("dishesId") Long dishesId);

    List<Schedule> findByStallId(Long id);
}
