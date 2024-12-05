package com.iuh.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * TransactionData Dữ liệu cho bảng thống kê
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionData {

    private String month;

    private BigDecimal expense;

    private BigDecimal income;
}
