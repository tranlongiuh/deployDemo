package com.iuh.canteen.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iuh.canteen.common.OrderStatus;
import com.iuh.canteen.common.PaymentMethod;
import com.iuh.canteen.common.PaymentStatus;
import com.iuh.canteen.dto.DishesDTO;
import com.iuh.canteen.dto.OrderItemDTO;
import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.dto.PaymentDTO;
import com.iuh.canteen.dto.StallDTO;
import com.iuh.canteen.dto.TransactionData;
import com.iuh.canteen.entity.Dishes;
import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.Payment;
import com.iuh.canteen.entity.Stall;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.DishesRepository;
import com.iuh.canteen.repository.OrdersRepository;
import com.iuh.canteen.service.OrdersService;

@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private DishesServiceImpl dishesService;

	@Autowired
	private DishesRepository dishesRepository;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private StallServiceImpl stallService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * addOrder Tạo Đơn đặt hàng mới
	 *
	 * @param username
	 *            Tên người đặt
	 * @param dishesList
	 *            Danh sách Món ăn
	 * @param paymentMethod
	 *            Phương thức Thanh toán
	 * @param selectedPromotionId
	 *            Mã Phiếu giảm giá
	 * @param address
	 *            Địa chỉ giao hàng
	 * @return Orders
	 */
	@Override
	public Orders addOrder(String username, List<DishesDTO> dishesList,
			String paymentMethod, Long selectedPromotionId, String address) {

		Orders result = new Orders();
		// tao order moi
		try {
			User user = userService.loadUserByUsername(username);
			System.err.println(user);
			result.setUser(user);
			result.setStatus(OrderStatus.NEW);
			result.setCreatedAt(LocalDateTime.now());
			if (address.isEmpty()) {
				result.setAddress("");
			} else {
				result.setAddress(address);
			}
			List<OrderItem> orderItems = new ArrayList<>();
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (DishesDTO dishesDTO : dishesList) {
				BigDecimal itemPrice = dishesDTO.getPrice()
						.multiply(BigDecimal.valueOf(dishesDTO.getQuantity()));
				OrderItem orderItem = new OrderItem();
				orderItem.setOrders(result);
				Dishes dishes = dishesRepository.findById(dishesDTO.getId())
						.orElseThrow();
				dishesService.subtractQuantity(dishesDTO.getId(),
						dishesDTO.getQuantity());
				orderItem.setQuantity(dishesDTO.getQuantity());
				orderItem.setPrice(itemPrice);
				orderItem.setDishes(dishes);
				orderItem.setStatus(OrderStatus.NEW);
				orderItem.setCreateAt(result.getCreatedAt());
				orderItem.setStall(dishes.getStall());

				orderItems.add(orderItem);
				totalPrice = totalPrice.add(itemPrice);
			}
			result.setTotalPrice(totalPrice);
			result.setOrderItems(orderItems);
			Payment payment = new Payment();
			switch (paymentMethod) {
				case "DEFAULT" :
					payment.setPaymentMethod(PaymentMethod.DEFAULT);
					break;
				case "MOMO" :
					payment.setPaymentMethod(PaymentMethod.MOMO);
					break;
				case "ZALO_PAY" :
					payment.setPaymentMethod(PaymentMethod.ZALO_PAY);
					break;
				case "BANK_CARD" :
					payment.setPaymentMethod(PaymentMethod.BANK_CARD);
					break;
			}
			payment.setStatus(PaymentStatus.PENDING);
			payment.setAmount(BigDecimal.ZERO);
			payment.setOrders(result);
			result.setPayment(payment);
			result.setPromotion(null);
			result = ordersRepository.save(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public Boolean deleteOrder(Integer id) {

		return null;
	}

	@Override
	public List<OrdersDTO> getAllOrders() {

		return List.of();
	}

	@Override
	public Orders updateOrder(OrdersDTO order) {

		return null;
	}

	/**
	 * calculateTotalSales Tính tổng tiền đã thanh toán
	 *
	 * @return BigDecimal
	 */
	@Override
	public BigDecimal calculateTotalSales() {

		return ordersRepository.sumTotalPriceByStatus(OrderStatus.PAID);
	}

	/**
	 * countTotalOrders Tính tổng số Đơn hàng đã thanh toán
	 *
	 * @return long
	 */
	@Override
	public long countTotalOrders() {

		return ordersRepository.countByStatus(OrderStatus.PAID);
	}

	/**
	 * findMonthlyTransactions Tính tổng tiền đã thanh toán theo từng tháng
	 *
	 * @return List TransactionData
	 */
	@Override
	public List<TransactionData> findMonthlyTransactions() {

		List<Object[]> tempGroup = ordersRepository.findOrdersGroupedByMonth();
		List<TransactionData> result = new ArrayList<>();
		// Initialize TransactionData for each month (1 to 12)
		for (int i = 1; i <= 12; i++) {
			result.add(new TransactionData(String.valueOf(i), BigDecimal.ZERO,
					BigDecimal.ZERO));
		}
		for (Object[] item : tempGroup) {
			Integer month = (Integer) item[0]; // Get the month
			Orders order = (Orders) item[1]; // Get the Orders object
			// Find the TransactionData for the given month
			TransactionData temp = result.stream()
					.filter(data -> data.getMonth().equals(month.toString()))
					.findFirst().orElseThrow();
			if (temp != null && order != null
					&& order.getTotalPrice() != null) {
				BigDecimal totalSales = order.getTotalPrice();
				if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal percentage = stallService.getServiceFee()
							.divide(BigDecimal.valueOf(100));
					BigDecimal totalExpenses = totalSales.multiply(percentage);
					BigDecimal netIncome = totalSales.subtract(totalExpenses);
					// Accumulate the values instead of overwriting
					temp.setExpense(temp.getExpense().add(totalExpenses)); // Update
																			// total
																			// expenses
					temp.setIncome(temp.getIncome().add(netIncome)); // Update
																		// net
																		// income
				} else {
					System.err.println(
							"Error: totalSales is non-positive or invalid");
				}
			}
		}
		result.forEach(System.out::println);
		return result;
	}

	/**
	 * findTop5ByOrderByOrderDateDesc Lấy 5 đơn hàng gần nhất
	 *
	 * @return List Orders
	 */
	@Override
	public List<Orders> findTop5ByOrderByOrderDateDesc() {

		List<Orders> result;
		try {
			result = ordersRepository.findTop5ByOrderByCreatedAtDesc();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Lấy Đơn hàng theo Mã Đơn hàng
	 *
	 * @param orderId
	 *            Mã Đơn hàng
	 * @return Orders
	 */
	@Override
	public Orders findById(Long orderId) {

		Orders result;
		try {
			result = ordersRepository.findById(orderId).orElseThrow();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * save Lưu Đơn hàng
	 *
	 * @param orders
	 *            Đơn hàng
	 * @return true - lưu thành công
	 */
	@Override
	public Boolean save(Orders orders) {

		boolean result = false;
		try {
			ordersRepository.save(orders);
			result = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * getOrdersByUsername lấy danh sách đơn hàng cho Thực khách
	 *
	 * @param username
	 *            Tên Tài khoản
	 * @return List OrdersDTO
	 */
	@Override
	public List<OrdersDTO> getOrdersByUsername(String username) {

		List<OrdersDTO> result = new ArrayList<>();
		try {
			if (userService.existsByUsername(username)) {
				User user = userService.loadUserByUsername(username);
				if (user != null && user.getOrders() != null) {
					for (Orders order : user.getOrders()) {
						addOrderToResult(result, order);
					}
				}
			} else {
				System.err.println("Error: user not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * updateStatusByOrderItemId Cập nhật trạng thái đơn hàng theo Mã Đơn hàng
	 *
	 * @param itemId
	 *            Mã Đơn hàng
	 * @return true - cập nhật thành công
	 */
	@Override
	public Boolean updateStatusByOrderItemId(Long itemId) {

		try {
			Orders orders = findByOrderItemId(itemId);
			if (orders == null || orders.getOrderItems() == null
					|| orders.getOrderItems().isEmpty()) {
				throw new IllegalStateException(
						"Order or Order Items not found.");
			}
			// Variables to track status conditions
			boolean isAllCompleted = true;
			boolean isProcessing = false;
			boolean isAllCanceled = true;
			boolean isWaitingOrShipping = true;
			boolean hasPaid = false;
			boolean hasPending = false;
			boolean isNew = false;
			for (OrderItem orderItem : orders.getOrderItems()) {
				OrderStatus status = orderItem.getStatus();
				// Update flags based on current item status
				if (!status.equals(OrderStatus.COMPLETED))
					isAllCompleted = false;
				if (status.equals(OrderStatus.PROCESSING))
					isProcessing = true;
				if (!status.equals(OrderStatus.CANCELLED)) {
					isAllCanceled = false;
				} else {
					// refund
					Stall stallRefund = orderItem.getStall();
					User managerRefund = userService
							.findById(stallRefund.getManagerId());
					managerRefund.setBalance(managerRefund.getBalance()
							.subtract(orderItem.getPrice()));
					stallService.save(stallRefund);
					User user = orders.getUser();
					user.setBalance(
							user.getBalance().add(orderItem.getPrice()));
					userService.save(user);
					// new totalprice
					orders.setTotalPrice(orders.getTotalPrice()
							.subtract(orderItem.getPrice()));
				}
				if (status.equals(OrderStatus.PAID))
					hasPaid = true;
				if (status.equals(OrderStatus.PENDING))
					hasPending = true;
				if (status.equals(OrderStatus.NEW))
					isNew = true;
				if (!(status.equals(OrderStatus.WAITING)
						|| status.equals(OrderStatus.SHIPPING))) {
					isWaitingOrShipping = false;
				}
			}
			// Set order status based on conditions (priority order)
			if (isAllCompleted) {
				orders.setStatus(OrderStatus.COMPLETED);
			} else if (isWaitingOrShipping) {
				orders.setStatus(orders.getAddress() == null
						|| orders.getAddress().isEmpty()
								? OrderStatus.WAITING
								: OrderStatus.SHIPPING);
			} else if (isProcessing) {
				orders.setStatus(OrderStatus.PROCESSING);
			} else if (isAllCanceled) {
				orders.setStatus(OrderStatus.CANCELLED);
			} else if (hasPaid) {
				orders.setStatus(OrderStatus.PAID);
			} else if (hasPending) {
				orders.setStatus(OrderStatus.PENDING);
			} else if (isNew) {
				orders.setStatus(OrderStatus.NEW);
			} else {
				orders.setStatus(OrderStatus.PENDING); // Default to PENDING if
														// no other status
														// applies
			}
			// Save the updated order status
			ordersRepository.save(orders);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Failed to update order status", e);
		}
	}

	/**
	 * findByOrderItemId Lấy Đơn hàng theo Mã Mục đơn hàng có trong Đơn hàng
	 *
	 * @param itemId
	 *            Mã Mục đơn hàng
	 * @return Orders
	 */
	@Override
	public Orders findByOrderItemId(Long itemId) {

		Orders result;
		try {
			result = ordersRepository.findByOrderItemsId(itemId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * getProcessingOrdersByUsername Lấy danh sách Đơn hàng đang trong quá trình
	 * thực hiện cho Thực khách
	 *
	 * @param username
	 *            Tên Tài khoản
	 * @return List OrdersDTO
	 */
	@Override
	public List<OrdersDTO> getProcessingOrdersByUsername(String username) {

		List<OrdersDTO> result = new ArrayList<>();
		try {
			if (userService.existsByUsername(username)) {
				User user = userService.loadUserByUsername(username);
				if (user != null && user.getOrders() != null) {
					user.getOrders().stream().forEach(order -> {
						if (order.getStatus().equals(OrderStatus.PROCESSING)) {
							addOrderToResult(result, order);
						}
					});
				}
			} else {
				System.err.println("Error: user not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * getWaitingOrdersByUsername Lấy danh sách Đơn hàng đang trong quá trình
	 * chờ Thực khách đến nhận
	 *
	 * @param username
	 *            Tên Tài khoản
	 * @return List OrdersDTO
	 */
	@Override
	public List<OrdersDTO> getWaitingOrdersByUsername(String username) {

		List<OrdersDTO> result = new ArrayList<>();
		try {
			if (userService.existsByUsername(username)) {
				User user = userService.loadUserByUsername(username);
				if (user != null && user.getOrders() != null) {
					user.getOrders().stream().forEach(order -> {
						if (order.getStatus().equals(OrderStatus.WAITING)) {
							addOrderToResult(result, order);
						}
					});
				}
			} else {
				System.err.println("Error: user not found");
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return result;
	}

	@Override
	public boolean deleteOrder(String username, Long orderId) {

		boolean rs = false;
		try {
			if (userService.existsByUsername(username)) {
				if (ordersRepository.existsById(orderId)) {
					Orders orders = ordersRepository.findById(orderId)
							.orElseThrow();
					if (orders != null && orders.getUser().getUsername()
							.equals(username)) {
						ordersRepository.delete(orders);
						rs = true;
					}
				} else {
					System.err.println("Error: orders not found");
				}
			} else {
				System.err.println("Error: user not found");
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return rs;
	}

	/**
	 * addOrderToResult Chuyển đối tượng Đơn hàng sang dữ liệu và thêm vào danh
	 * sách
	 *
	 * @param result
	 *            List OrdersDTO
	 * @param order
	 *            Orders
	 */
	private void addOrderToResult(List<OrdersDTO> result, Orders order) {

		try {
			OrdersDTO orderDTO = modelMapper.map(order, OrdersDTO.class);
			List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
			order.getOrderItems().forEach(orderItem -> {
				OrderItemDTO orderItemDTO = modelMapper.map(orderItem,
						OrderItemDTO.class);
				if (orderItem.getDishes() != null) {
					orderItemDTO.setDishesDTO(modelMapper
							.map(orderItem.getDishes(), DishesDTO.class));
				}
				if (orderItem.getStall() != null) {
					orderItemDTO.setStallDTO(modelMapper
							.map(orderItem.getStall(), StallDTO.class));
				}
				orderItemDTOS.add(orderItemDTO);
			});
			orderDTO.setOrderItemDTOS(orderItemDTOS);

			if (order.getPayment() != null) {
				orderDTO.setPaymentDTO(
						modelMapper.map(order.getPayment(), PaymentDTO.class));
			}

			result.add(orderDTO);
		} catch (Exception e) {
			System.out.println("addOrderToResult error");
			e.printStackTrace();
		}
	}

}
