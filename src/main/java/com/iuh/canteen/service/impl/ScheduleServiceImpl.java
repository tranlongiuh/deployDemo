package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.ScheduleDTO;
import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.entity.Dishes;
import com.iuh.canteen.entity.Schedule;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.repository.DishesRepository;
import com.iuh.canteen.repository.ScheduleRepository;
import com.iuh.canteen.repository.StallRepository;
import com.iuh.canteen.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DishesRepository dishesRepository;

    @Autowired
    private StallRepository stallRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private StallServiceImpl stallService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * addDishesToSchedule Thêm món ăn vào Lịch trình
     *
     * @param day       Ngày thêm (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param dishesIds Danh sách Mã Món ăn
     * @param username  Tên Tài khoản thực hiện
     * @return true - thêm thành công
     */
    @Override
    public Boolean addDishesToSchedule(Integer day, List<Long> dishesIds, String username) {

        boolean result = false;
        try {
            Long idUser = userService.getIdByUsername(username);
            Stall stall = stallRepository.findByManagerId(idUser);
            Schedule schedule = scheduleRepository.findByDayAndStall(day, stall)
                                                  .orElseThrow();
            // Lấy danh sách các món ăn hiện có trong schedule
            List<Long> existingDishesIds = schedule.getDishesList()
                                                   .stream()
                                                   .map(Dishes::getId)  // Lấy ID của các món hiện có
                                                   .collect(Collectors.toList());
            System.out.println("existingDishesIds " + existingDishesIds);
            // Lọc ra các món ăn chưa có trong schedule
            List<Dishes> dishesToAdd = dishesRepository.findAllById(dishesIds)
                                                       .stream()
                                                       .filter(dishes -> !existingDishesIds.contains(dishes.getId()))
                                                       .collect(Collectors.toList());
            System.out.println("dishesToAdd " + dishesToAdd);
            // Chỉ thêm các món chưa có sẵn
            if (!dishesToAdd.isEmpty()) {
                schedule.getDishesList()
                        .addAll(dishesToAdd);
                scheduleRepository.save(schedule);
                result = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * removeDishesFromSchedule Xóa món ăn trong Lịch trình
     *
     * @param day       Ngày xóa (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param dishesIds Danh sách Mã Món ăn
     * @param username  Tên Tài khoản thực hiện
     * @return true - xóa thành công
     */
    @Override
    public Boolean removeDishesFromSchedule(Integer day, List<Long> dishesIds, String username) {

        boolean result = false;
        try {
            Long idUser = userService.getIdByUsername(username);
            Stall stall = stallRepository.findByManagerId(idUser);
            Schedule schedule = scheduleRepository.findByDayAndStall(day, stall)
                                                  .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
            List<Dishes> dishesToRemove = dishesRepository.findAllById(dishesIds);
            schedule.getDishesList()
                    .removeAll(dishesToRemove);
            scheduleRepository.save(schedule);
            result = true;
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Lưu Lịch trình
     *
     * @param schedule Lịch trình
     * @return Schedule
     */
    @Override
    public Schedule save(Schedule schedule) {

        return scheduleRepository.save(schedule);
    }

    /**
     * getScheduleByDayAndStall Lấy Lịch trình theo ngày trong Gian hàng
     *
     * @param day     Ngày (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param stallId Mã Gian hàng
     * @return Schedule
     */
    @Override
    public Schedule getScheduleByDayAndStall(Integer day, Long stallId) {

        Stall stall = stallRepository.findById(stallId)
                                     .orElseThrow(() -> new EntityNotFoundException("Stall not found"));
        return scheduleRepository.findByDayAndStall(day, stall)
                                 .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    }

    /**
     * getSchedulesForStall Lấy toàn bộ Lịch trình trong Gian hàng
     *
     * @param stallId Mã Gian hàng
     * @return List Schedule
     */
    @Override
    public List<Schedule> getSchedulesForStall(Long stallId) {

        Stall stall = stallRepository.findById(stallId)
                                     .orElseThrow(() -> new EntityNotFoundException("Stall not found"));
        return scheduleRepository.findByStall(stall);
    }

    /**
     * findByDay Lấy Lịch trình trong Gian hàng theo Ngày
     *
     * @param day      Ngày (vd: Thứ 2 - day=1, Thứ 3 - day=2,...)
     * @param username Tên Tài Khoản
     * @return Schedule
     */
    @Override
    public ScheduleDTO findByDay(Integer day, String username) {

        ScheduleDTO rs = new ScheduleDTO();
        Long idUser = userService.getIdByUsername(username);
        Stall stall = stallService.findByManagerId(idUser);
        // Fetch the Schedule with dishes using the repository
        Schedule temp = scheduleRepository.findByDayAndStall(day, stall)
                                          .orElse(null);
        List<DishesDTO> dishesDTOList = new ArrayList<>();
        temp.getDishesList()
            .forEach(m -> dishesDTOList.add(modelMapper.map(m, DishesDTO.class)));
        rs.setDishesDTOS(dishesDTOList);
        System.out.println("rs " + rs);
        return rs;
    }

    /**
     * findByDishesId Lấy danh sách Lịch trình theo Mã Món ăn
     *
     * @param id Mã Món ăn
     * @return List Schedule
     */
    @Override
    public List<Schedule> findByDishesId(Long id) {

        return scheduleRepository.findByDishesId(id);
    }

    /**
     * findToDayDishesByStallId Lấy danh sách Món ăn trong ngày theo Mã Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @return List DishesDTO
     */
    @Override
    public List<DishesDTO> findToDayDishesByStallId(Long idStall) {

        List<DishesDTO> rs = new ArrayList<>();
        try {
            Stall stall = stallService.findById(idStall);
            Schedule temp = scheduleRepository.findByDayAndStall(LocalDate.now()
                                                                          .getDayOfWeek()
                                                                          .getValue(), stall)
                                              .orElse(null);
            temp.getDishesList()
                .forEach(
                        dishes -> {
                            DishesDTO dto = modelMapper.map(dishes, DishesDTO.class);
                            dto.setStallDTO(modelMapper.map(dishes.getStall(), StallDTO.class));
                            rs.add(dto);
                        }
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @Override
    public List<StallDTO> getScheduleForCustomer() {

        System.out.println("getScheduleForCustomer...");
        List<StallDTO> rs = stallService.findAll();
        System.out.println("List<StallDTO> \n" + rs);
        // Thiết lập danh sách món ăn cho từng StallDTO
        rs.forEach(stallDTO -> stallDTO.setDishesDTOS(findToDayDishesByStallId(stallDTO.getId())));
        // Lọc các StallDTO có số lượng DishesDTO lớn hơn 1
        rs = rs.stream()
               .filter(stallDTO -> stallDTO.getDishesDTOS() != null && stallDTO.getDishesDTOS()
                                                                               .size() >= 1)
               .collect(Collectors.toList());
        return rs;
    }

    @Override
    public List<Schedule> findByStallId(Long id) {

        return scheduleRepository.findByStallId(id);
    }
}
