package com.iuh.canteen.controller;

import com.iuh.canteen.dto.ScheduleDTO;
import com.iuh.canteen.security.JwtUtil;
import com.iuh.canteen.service.impl.ScheduleServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ScheduleController Bộ điều khiển lịch trình
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ScheduleServiceImpl scheduleService;

    /**
     * addDishesToSchedule Thêm Món ăn vào Lịch trình
     *
     * @param day       Ngày thêm (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param dishesIds Danh sách Mã Món ăn
     * @param request
     * @return
     */
    @PostMapping("/addDishes")
    public ResponseEntity<String> addDishesToSchedule(@RequestParam("day") Integer day,
                                                      @RequestParam("dishesIds") List<Long> dishesIds,
                                                      HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            scheduleService.addDishesToSchedule(day, dishesIds, username);
            return ResponseEntity.ok("Dishes added to the schedule.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * removeDishesFromSchedule Xóa Món ăn trong Lịch trình
     *
     * @param day       Ngày xóa (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param dishesIds Danh sách Mã Món ăn
     * @param request
     * @return
     */
    @DeleteMapping("/removeDishes")
    public ResponseEntity<String> removeDishesFromSchedule(@RequestParam("day") Integer day,
                                                           @RequestParam("dishesIds") List<Long> dishesIds,
                                                           HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            scheduleService.removeDishesFromSchedule(day, dishesIds, username);
            return ResponseEntity.ok("Dishes removed from the schedule.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getScheduleByDay Lấy danh sách Món ăn có trong Lịch trình theo ngày
     *
     * @param day     Ngày (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param request
     * @return
     */
    @GetMapping("{day}")
    public ResponseEntity<ScheduleDTO> getScheduleByDay(@PathVariable("day") Integer day, HttpServletRequest request) {

        try {
            String jwt = jwtUtil.getJwtFromRequest(request);
            String username = jwtUtil.extractUsername(jwt);
            return ResponseEntity.ok(scheduleService.findByDay(day, username));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getScheduleByStallId Lấy danh sách Món ăn có trong Gian hàng trong ngày theo Mã Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @param request
     * @return
     */
    @GetMapping("/stall/{idStall}")
    public ResponseEntity<?> getScheduleByStallId(@PathVariable("idStall") Long idStall, HttpServletRequest request) {

        try {
            return ResponseEntity.ok(scheduleService.findToDayDishesByStallId(idStall));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getScheduleForCustomer Lấy danh sách Món ăn trong ngày
     *
     * @return
     */
    @GetMapping("/stalls")
    public ResponseEntity<?> getScheduleForCustomer() {

        try {
            return ResponseEntity.ok(scheduleService.getScheduleForCustomer());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

