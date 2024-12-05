package com.iuh.canteen.service;

import java.time.LocalDateTime;
import java.util.List;

import com.iuh.canteen.dto.OrderItemDTO;
import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Stall;

public interface OrderItemService {

	List<OrderItemDTO> getOrderItems(String username);

	Boolean updateOrderItemStatus(Long id, String username, String status);

	List<OrderItemDTO> takingItemInStall(Long idOrder, String username);

	boolean giveItem(Long id, String username);

	List<OrderItem> findTodayOrderItemByStall(Stall stall);

	List<OrderItemDTO> findTodayOrderItemByStallAndTime(Stall stall,
			LocalDateTime startOfDay, LocalDateTime endOfDay);
}
