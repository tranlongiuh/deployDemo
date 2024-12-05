package com.iuh.canteen.service.impl;

import com.iuh.canteen.dto.*;
import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.OrderItemRepository;
import com.iuh.canteen.repository.StallRepository;
import com.iuh.canteen.service.StallService;
import com.iuh.canteen.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StallServiceImpl implements StallService {

    @Autowired
    private StallRepository stallRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * getIdByManagerId Lấy Mã Gian hàng theo Mã Người quản lý
     *
     * @param managerId Mã Người quản lý
     * @return Long
     */
    @Override
    public Long getIdByManagerId(Long managerId) {

        Long result = null;
        try {
            result = stallRepository.findByManagerId(managerId)
                                    .getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * findAll Lấy toàn bộ danh sách Gian hàng
     *
     * @return List StallDTO
     */
    @Override
    public List<StallDTO> findAll() {

        List<StallDTO> result = new ArrayList<>();
        try {
            stallRepository.findAll()
                           .forEach(
                                   stall -> {
                                       List<DishesDTO> dishesDTOList = new ArrayList<>();
                                       List<PromotionDTO> promotionDTOS = new ArrayList<>();
                                       stall.getDishes()
                                            .forEach(
                                                    dishes -> {
                                                        StallDTO stallDTO = new StallDTO();
                                                        stallDTO.setId(stall.getId());
                                                        stallDTO.setName(stall.getName());
                                                        stallDTO.setImageId(stall.getImageId());
                                                        dishesDTOList.add(
                                                                new DishesDTO(dishes.getId(), dishes.getName(),
                                                                        dishes.getDescription(), dishes.getQuantity(),
                                                                        dishes.getReviews(), dishes.getStars()
                                                                        , dishes.getPrice(), dishes.getImageId(),
                                                                        dishes.getCategory(), null, stallDTO));
                                                    }
                                            );
                                       stall.getPromotions()
                                            .forEach(
                                                    promotion -> {
                                                        promotionDTOS.add(
                                                                new PromotionDTO(promotion.getId(), promotion.getCode(),
                                                                        promotion.getDescription(),
                                                                        promotion.getQuantity(),
                                                                        promotion.getDiscountPercentage(),
                                                                        promotion.getStartDate(), promotion.getActive(),
                                                                        promotion.getEndDate(), null, null));
                                                    }
                                            );
                                       UserDTO managerDTO = modelMapper.map(userService.findById(stall.getManagerId()),
                                               UserDTO.class);
                                       managerDTO.setPassword("");
                                       managerDTO.setPassword2("");
                                       result.add(new StallDTO(stall.getId(), stall.getName(), managerDTO,
                                               stall.getImageId(), stall.getRevenue(), stall.getServiceFee(),
                                               dishesDTOList,
                                               promotionDTOS));
                                   }
                           );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * save Lưu Gian hàng
     *
     * @param foodStall Gian hàng
     * @return Stall
     */
    @Override
    public Stall save(Stall foodStall) {

        return stallRepository.save(foodStall);
    }

    /**
     * findById Lấy Gian hàng theo Mã Gian hàng
     *
     * @param id Mã Gian hàng
     * @return Stall
     */
    @Override
    public Stall findById(Long id) {

        return stallRepository.findById(id)
                              .orElse(null);
    }

    /**
     * delete Xóa Gian hàng theo Mã Gian hàng
     *
     * @param id Mã Gian hàng
     * @return true - xóa thành công
     */
    @Override
    public Boolean delete(Long id) {

        try {
            stallRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * findByManagerId Lấy Gian hàng theo Mã Người quản lý
     *
     * @param userId Mã Người quản lý
     * @return Stall
     */
    @Override
    public Stall findByManagerId(Long userId) {

        return stallRepository.findByManagerId(userId);
    }

    /**
     * setFee Đặt phí dịch vụ cho Gian hàng
     *
     * @param fee phí dịch vụ tính theo phần trăm (vd: fee=10 - phí là 10%)
     * @return true - nếu đặt thành công
     */
    @Override
    public Boolean setFee(Integer fee) {

        boolean result = false;
        try {
            List<Stall> temp = stallRepository.findAll();
            temp.forEach(
                    stall -> {
                        stall.setServiceFee(BigDecimal.valueOf(fee));
                    }
            );
            stallRepository.saveAll(temp);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * getServiceFee Lấy mức phí dịch vụ trong căn tin
     *
     * @return BigDecimal
     */
    @Override
    public BigDecimal getServiceFee() {

        return stallRepository.findFirstByOrderByIdAsc()
                              .getServiceFee();
    }

    /**
     * findTop5ByOrderByOrderDateDesc Lấy danh sách 5 Đơn đặt hàng gần nhất trong Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @return List Orders
     */
    @Override
    public List<Orders> findTop5ByOrderByOrderDateDesc(Long idStall) {

        List<Orders> result = new ArrayList<>();
        try {
            Pageable topFive = PageRequest.of(0, 5);
            result = orderItemRepository.findTop5OrdersByStallId(idStall, topFive);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * findMonthlyTransactions Lấy dữ liệu theo từng tháng trong Gian hàng
     *
     * @param stallId Mã Gian hàng
     * @return List TransactionData
     */
    @Override
    public List<TransactionData> findMonthlyTransactions(Long stallId) {
        // Query the repository to get the grouped data
        List<Object[]> tempGroup = orderItemRepository.findSalesGroupedByMonthAndStall(stallId);
        List<TransactionData> result = new ArrayList<>();
        // Initialize TransactionData for each month (1 to 12)
        for (int i = 1; i <= 12; i++) {
            result.add(new TransactionData(String.valueOf(i), BigDecimal.ZERO, BigDecimal.ZERO));
        }
        for (Object[] item : tempGroup) {
            Integer month = (Integer) item[0];  // The month
            BigDecimal totalSales = (BigDecimal) item[1];  // Total sales for that month
            // Find the TransactionData for the given month
            TransactionData temp = result.stream()
                                         .filter(data -> data.getMonth()
                                                             .equals(month.toString()))
                                         .findFirst()
                                         .orElse(null);
            if (temp != null && totalSales != null) {
                // Calculate expenses based on a percentage of total sales
                BigDecimal percentage = getServiceFee()
                        .divide(BigDecimal.valueOf(100));
                BigDecimal totalExpenses = totalSales.multiply(percentage);
                BigDecimal netIncome = totalSales.subtract(totalExpenses);
                // Accumulate the values
                temp.setExpense(temp.getExpense()
                                    .add(totalExpenses)); // Update total expenses
                temp.setIncome(temp.getIncome()
                                   .add(netIncome));       // Update net income
            }
        }
        return result;
    }

    /**
     * calculateTotalSales tính tổng Doanh thu trong Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @return BigDecimal
     */
    @Override
    public BigDecimal calculateTotalSales(Long idStall) {

        List<OrderItem> orderItems = orderItemRepository.findByStallId(idStall);
        // Tính tổng tiền bằng cách nhân giá với số lượng của từng OrderItem
        BigDecimal totalSales = orderItems.stream()
                                          .map(item -> item.getPrice()
                                                           .multiply(BigDecimal.valueOf(item.getQuantity())))
                                          .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalSales;
    }

    /**
     * countCustomers tính số lượng Thực khách của Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @return long
     */
    @Override
    public long countCustomers(Long idStall) {

        return orderItemRepository.countDistinctCustomersByStallId(idStall);
    }

    /**
     * countTotalOrders tính số lượng Đơn đặt món của Gian hàng
     *
     * @param idStall Mã Gian hàng
     * @return long
     */
    @Override
    public long countTotalOrders(Long idStall) {

        return orderItemRepository.countDistinctOrdersByStallId(idStall);
    }

    @Override
    public StallDTO findByManagerName(String username) {

        StallDTO rs;
        User user = userService.loadUserByUsername(username);
        try {
            rs = modelMapper.map(stallRepository.findByManagerId(user.getId()), StallDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }
}
