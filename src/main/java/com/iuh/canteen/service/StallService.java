package com.iuh.canteen.service;

import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.Stall;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface StallService {

    Long getIdByManagerId(Long managerId);

    public List<StallDTO> findAll();

    public Stall save(Stall foodStall);

    public Stall findById(Long id);

    public Boolean delete(Long id);

    Stall findByManagerId(Long managerId);

    Boolean setFee(Integer fee);

    BigDecimal getServiceFee();

    List<Orders> findTop5ByOrderByOrderDateDesc(Long idStall);

    List<TransactionData> findMonthlyTransactions(Long idStall);

    BigDecimal calculateTotalSales(Long idStall);

    long countCustomers(Long idStall);

    long countTotalOrders(Long idStall);

    StallDTO findByManagerName(String username);
}
