package com.iuh.canteen.service;

import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.entity.Orders;

import java.math.BigDecimal;
import java.util.List;

public interface OrdersService {

    Orders addOrder(String username, List<DishesDTO> dishesList, String paymentMethod, Long selectedPromotionId,
                    String address);

    Boolean deleteOrder(Integer id);

    List<OrdersDTO> getAllOrders();

    Orders updateOrder(OrdersDTO order);

    BigDecimal calculateTotalSales();

    long countTotalOrders();

    List<TransactionData> findMonthlyTransactions();

    List<Orders> findTop5ByOrderByOrderDateDesc();

    Orders findById(Long orderId);

    /**
     * @param orders
     * @return
     */
    Boolean save(Orders orders);

    /**
     * Danh cho customer
     *
     * @param username
     * @return
     */
    List<OrdersDTO> getOrdersByUsername(String username);

    Boolean updateStatusByOrderItemId(Long itemId);

    Orders findByOrderItemId(Long itemId);

    List<OrdersDTO> getProcessingOrdersByUsername(String username);

    List<OrdersDTO> getWaitingOrdersByUsername(String username);

    boolean deleteOrder(String username, Long orderId);
}
