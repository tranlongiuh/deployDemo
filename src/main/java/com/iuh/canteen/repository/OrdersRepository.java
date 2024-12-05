package com.iuh.canteen.repository;

import com.iuh.canteen.common.OrderStatus;
import com.iuh.canteen.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT SUM(o.totalPrice) FROM Orders o")
    BigDecimal calculateTotalSales();

    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = :orderStatus")
    BigDecimal sumTotalPriceByStatus(@Param("orderStatus") OrderStatus orderStatus);

    long countByStatus(OrderStatus orderStatus);

    @Query("SELECT MONTH(o.createdAt) AS month, o FROM Orders o ORDER BY month")
    List<Object[]> findOrdersGroupedByMonth();

    List<Orders> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT MONTH(o.createdAt) AS month, SUM(o.totalPrice) AS totalSales " +
            "FROM Orders o " +
            "JOIN o.orderItems oi " +
            "WHERE oi.stall.id = :stallId " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY month")
    List<Object[]> findOrdersGroupedByMonthAndStall(@Param("stallId") Long stallId);

    Orders findByOrderItemsId(Long itemId);
}
