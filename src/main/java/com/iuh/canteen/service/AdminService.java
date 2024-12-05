package com.iuh.canteen.service;

import com.iuh.canteen.dto.StallDTO;

import java.util.List;

public interface AdminService {

    Boolean setFee(Integer fee);

    List<StallDTO> getStallStat();

    boolean createStall(String name, String username, String password, String email, String phone);
}
