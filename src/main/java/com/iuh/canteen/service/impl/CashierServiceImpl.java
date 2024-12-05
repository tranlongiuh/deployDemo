package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.UserDTO;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.service.CashierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CashierServiceImpl implements CashierService {

    @Autowired
    private UserServiceImpl userService;

    /**
     * findCustomerByUsername Lấy dữ liệu Tài khoản của Thực khách theo Tên người dùng
     *
     * @param username Tên người dùng
     * @return UserDTO với Mã, Email, Số dư
     */
    @Override
    public UserDTO findCustomerByUsername(String username) {

        UserDTO result = new UserDTO();
        User tempUser = userService.loadUserByUsername(username);
        if (tempUser.getRoles()
                    .contains("ROLE_CUSTOMER")) {
            result.setId(tempUser.getId());
            result.setEmail(tempUser.getEmail());
            result.setBalance(tempUser.getBalance());
            result.setPhone(tempUser.getPhone());
            return result;
        }
        return null;
    }

    /**
     * depositForCustomer Nạp tiền vào Tài khoản của Thực khách
     *
     * @param idUser   Mã Tài khoản
     * @param money    Số tiền nạp
     * @param username Tên Thu ngân
     * @return
     */
    @Override
    public UserDTO depositForCustomer(Long idUser, Integer money, String username) {

        UserDTO result = new UserDTO();
        User tempUser = userService.findById(idUser);
        User cashier = userService.loadUserByUsername(username);
        if (tempUser != null) {
            tempUser.setBalance(tempUser.getBalance()
                                        .add(new BigDecimal(money)));
            cashier.setBalance(cashier.getBalance()
                                      .add(new BigDecimal(money)));
            userService.save(tempUser);
            result.setId(tempUser.getId());
            result.setEmail(tempUser.getEmail());
            result.setBalance(tempUser.getBalance());
        }
        return result;
    }
}
