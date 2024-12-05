package com.iuh.canteen.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.Stall;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findByStallId(Long stallId);

	@Query("SELECT COUNT(DISTINCT o.user.id) " + "FROM OrderItem oi "
			+ "JOIN oi.orders o " + "WHERE oi.stall.id = :stallId")
	long countDistinctCustomersByStallId(@Param("stallId") Long stallId);

	// @Query("SELECT COUNT(DISTINCT oi.orders.id) FROM OrderItem oi WHERE
	// oi.stall.id = :stallId")
	// long countDistinctOrdersByStallId(@Param("stallId") Long stallId);
	@Query("SELECT COUNT(DISTINCT oi.orders.id) FROM OrderItem oi WHERE oi.stall.id = :stallId AND oi.orders.status != 'CANCELLED'")
	long countDistinctOrdersByStallId(@Param("stallId") Long stallId);

	@Query("SELECT DISTINCT oi.orders FROM OrderItem oi "
			+ "WHERE oi.stall.id = :stallId "
			+ "ORDER BY oi.orders.createdAt DESC")
	List<Orders> findTop5OrdersByStallId(@Param("stallId") Long stallId,
			Pageable pageable);

	@Query("SELECT MONTH(o.createdAt) AS month, SUM(oi.price * oi.quantity) AS totalSales "
			+ "FROM OrderItem oi " + "JOIN oi.orders o "
			+ "WHERE oi.stall.id = :stallId " + "GROUP BY MONTH(o.createdAt) "
			+ "ORDER BY month")
	List<Object[]> findSalesGroupedByMonthAndStall(
			@Param("stallId") Long stallId);

	List<OrderItem> findByStallIdOrderByOrdersCreatedAt(Long stallId);

	List<OrderItem> findByOrdersIdAndStallId(Long idOrder, Long idStall);

	@Query("SELECT o FROM OrderItem o WHERE o.stall = :stall AND o.createAt BETWEEN :start AND :end")
	List<OrderItem> findTodayOrderItemByStall(@Param("stall") Stall stall,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

}
