package com.iuh.canteen.service.impl;

import com.iuh.canteen.common.OrderStatus;
import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.OrderItemDTO;
import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.repository.OrderItemRepository;
import com.iuh.canteen.service.OrderItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StallServiceImpl stallService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private OrdersServiceImpl ordersService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * getOrderItems Lấy danh sách Mục trong đơn đặt hàng cho Thực khách
     *
     * @param username Tên Tài Khoản
     * @return List OrderItemDTO
     */
    @Override
    public List<OrderItemDTO> getOrderItems(String username) {

        List<OrderItemDTO> result = new ArrayList<>();
        try {
            if (!username.isEmpty()) {
                Long stallId = stallService.getIdByManagerId(userService.getIdByUsername(username));
                orderItemRepository.findByStallIdOrderByOrdersCreatedAt(stallId)
                                   .forEach(
                                           orderItem -> {
                                               if (!orderItem.getStatus()
                                                             .equals(OrderStatus.WAITING) && !orderItem.getStatus()
                                                                                                       .equals(OrderStatus.COMPLETED)) {
                                                   OrderItemDTO orderItemDTO = modelMapper.map(orderItem,
                                                           OrderItemDTO.class);
                                                   orderItemDTO.setDishesDTO(
                                                           modelMapper.map(orderItem.getDishes(), DishesDTO.class));
                                                   result.add(orderItemDTO);
                                               }
                                           }
                                   );
            } else {
                System.err.println("username is empty");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * updateOrderItemStatus Cập nhật Trạng thái Mục đơn hàng trong Đơn đặt hàng
     *
     * @param itemId   Mã Mục đơn hàng
     * @param username Tên Tài khoản
     * @param status   Trạng thái
     * @return true - cập nhật thành công
     */
    @Override
    public Boolean updateOrderItemStatus(Long itemId, String username, String status) {

        boolean result = false;
        try {
            if (!username.isEmpty()) {
                OrderItem orderItem = orderItemRepository.findById(itemId)
                                                         .orElseThrow();
                if (orderItem.getStall()
                             .getManagerId()
                             .equals(userService.getIdByUsername(username))) {
                    switch (status) {
                        case "PROCESSING":
                            orderItem.setStatus(OrderStatus.PROCESSING);
                            break;
                        case "WAITING":
                            orderItem.setStatus(OrderStatus.WAITING);
                            orderItem.setFinishAt(LocalDateTime.now());
                            break;
                        case "PAID":
                            orderItem.setStatus(OrderStatus.PAID);
                            break;
                    }
                    orderItemRepository.save(orderItem);
                    ordersService.updateStatusByOrderItemId(itemId);
                    result = true;
                } else {
                    System.err.println("stall not have permission");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * takingItemInStall Lấy toàn bộ Món đã hoàn thành trong Gian hàng với Mã Đơn hàng
     *
     * @param idOrder  Mã Đơn hàng
     * @param username Tên Tài khoản thực hiện
     * @return
     */
    @Override
    public List<OrderItemDTO> takingItemInStall(Long idOrder, String username) {

        List<OrderItemDTO> rs = new ArrayList<>();
        try {
            Long managerId = userService.getIdByUsername(username);
            Stall stall = stallService.findByManagerId(managerId);
            List<OrderItem> orderItems = orderItemRepository.findByOrdersIdAndStallId(idOrder, stall.getId());
            orderItems
                    .forEach(orderItem -> {
                        if (orderItem.getStatus()
                                     .equals(OrderStatus.WAITING)) {
                            // TODO: takingItemInStall
                            OrderItemDTO item = modelMapper.map(orderItem, OrderItemDTO.class);
                            item.setDishesDTO(modelMapper.map(orderItem.getDishes(), DishesDTO.class));
                            rs.add(item);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    /**
     * giveItem Giao món ăn đã hoành thành trong Gian hàng theo Mã Món ăn
     *
     * @param id       Mã Món ăn
     * @param username Tên Tài khoản thực hiện
     * @return true - giao thành công
     */
    @Override
    public boolean giveItem(Long id, String username) {

        boolean rs = false;
        try {
            Long managerId = userService.getIdByUsername(username);
            Stall stall = stallService.findByManagerId(managerId);
            OrderItem orderItem = orderItemRepository.findById(id)
                                                     .orElse(null);
            if (orderItem != null && orderItem.getStall()
                                              .equals(stall)) {
                orderItem.setStatus(OrderStatus.COMPLETED);
                orderItem.setFinishAt(LocalDateTime.now());
                orderItemRepository.save(orderItem);
                rs = true;
            } else {
                System.out.println("stall not have permission");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }
}
