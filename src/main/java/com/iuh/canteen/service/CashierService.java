package com.iuh.canteen.service;

import com.iuh.canteen.dto.UserDTO;

public interface CashierService {

    UserDTO findCustomerByUsername(String username);

    UserDTO depositForCustomer(Long idUser, Integer money, String username);
}
