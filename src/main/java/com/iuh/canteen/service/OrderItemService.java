package com.iuh.canteen.service;

import com.iuh.canteen.dto.OrderItemDTO;

import java.util.List;

public interface OrderItemService {

    List<OrderItemDTO> getOrderItems(String username);

    Boolean updateOrderItemStatus(Long id, String username, String status);

    List<OrderItemDTO> takingItemInStall(Long idOrder, String username);

    boolean giveItem(Long id, String username);
}
