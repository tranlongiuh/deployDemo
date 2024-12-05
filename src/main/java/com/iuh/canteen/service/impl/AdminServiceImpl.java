package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.entity.Schedule;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private StallServiceImpl stallService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ScheduleServiceImpl scheduleService;

    /**
     * setFee Đặt phí cho gian hàng
     *
     * @param fee phí tính theo phần trăm (vd: fee=10 phí là 10%)
     * @return true - khi đặt phí thành công
     */
    @Override
    public Boolean setFee(Integer fee) {

        Boolean result = false;
        try {
            result = stallService.setFee(fee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * getStallStat Lấy thông tin Gian hàng
     *
     * @return List StallDTO với stallDTO không có dữ liệu của Danh sách Phiếu giảm giá, Danh sách Món ăn
     */
    @Override
    public List<StallDTO> getStallStat() {

        List<StallDTO> result = null;
        try {
            result = stallService.findAll();
            result.forEach(
                    stallDTO -> {
                        stallDTO.setPromotionDTOS(null);
                        stallDTO.setDishesDTOS(null);
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean createStall(String name, String username, String password, String email, String phone) {

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(new BCryptPasswordEncoder().encode(password));
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRoles("ROLE_MANAGER");
            newUser.setBalance(new BigDecimal("0.0"));
            newUser.setEnabled(true);
            newUser = userService.save(newUser);
            Stall newStall = new Stall();
            newStall.setName(name);
            newStall.setManagerId(newUser.getId());
            newStall.setServiceFee(BigDecimal.ZERO);
            newStall.setRevenue(BigDecimal.ZERO);
            newStall = stallService.save(newStall);
            if (newStall != null && newStall.getId() != null) {
                if (scheduleService.findByStallId(newStall.getId())
                                   .isEmpty()) {
                    for (int i = 1; i <= 7; i++) {
                        scheduleService.save(new Schedule(null, i, newStall, null));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

	public boolean resetPassword(Long id) {
		try {
            User manager = userService.findById(id);
            
            manager.setPassword(new BCryptPasswordEncoder().encode("12345678"));
           
            manager = userService.save(manager);
            
            if (manager != null) {     
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
	}
}
