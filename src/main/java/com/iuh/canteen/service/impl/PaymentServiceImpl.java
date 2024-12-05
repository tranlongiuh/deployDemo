package com.iuh.canteen.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iuh.canteen.common.OrderStatus;
import com.iuh.canteen.common.PaymentMethod;
import com.iuh.canteen.common.PaymentStatus;
import com.iuh.canteen.entity.OrderItem;
import com.iuh.canteen.entity.Orders;
import com.iuh.canteen.entity.Payment;
import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.PaymentRepository;
import com.iuh.canteen.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private OrdersServiceImpl ordersService;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * purchase Thanh toán Đơn đặt hàng
	 *
	 * @param username
	 *            Tên Tài khoản
	 * @param orderId
	 *            Mã Đơn đặt hàng
	 * @param confirmPassword
	 *            Mật khẩu cấp 2
	 * @return true - nếu thanh toán thành công
	 */
	@Override
	public Boolean purchase(String username, Long orderId,
			String confirmPassword) {

		try {
			User user = userService.loadUserByUsername(username);
			if (user.getPassword2().equals(confirmPassword)) {
				Orders orders = ordersService.findById(orderId);
				if (orders != null) {
					if (orders.getTotalPrice()
							.compareTo(user.getBalance()) == -1) {
						// true
						Payment payment = orders.getPayment();
						if (payment.getPaymentMethod()
								.equals(PaymentMethod.DEFAULT)) {
							// tru tien
							user.setBalance(user.getBalance()
									.subtract(orders.getTotalPrice())
							// Specify scale and rounding mode
							);
							userService.save(user);
							// cap nhat trang thai don, cong tien cho manager

							orders.setStatus(OrderStatus.PAID);
							List<OrderItem> orderItems = getOrderItems(orders);
							orders.setOrderItems(orderItems);

							ordersService.save(orders);
							// cap nhat trang thai thanh toan
							// TODO: ap ma giam gia
							payment.setAmount(orders.getTotalPrice());
							payment.setStatus(PaymentStatus.COMPLETED);
							paymentRepository.save(payment);
							System.out.println("payment success");
							return true;
						}
					}
				} else {
					System.err.println("orders is null");
				}
			} else {
				System.err.println("user password2 not match");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * getOrderItems Lấy danh sách Mục đơn hàng trong Gian hàng
	 *
	 * @param orders
	 *            Đơn đặt hàng
	 * @return List OrderItem
	 */
	private List<OrderItem> getOrderItems(Orders orders) {

		List<OrderItem> orderItems = orders.getOrderItems();
		orderItems.forEach(orderItem -> {
			User manager = userService
					.findById(orderItem.getStall().getManagerId());
			manager.setBalance(manager.getBalance().add(orderItem.getPrice()));
			orderItem.setStatus(OrderStatus.PAID);
		});
		return orderItems;
	}

	@Override
	public boolean successPayment(Long orderId) {
		boolean rs = false;
		try {
			Orders orders = ordersService.findById(orderId);
			System.out.println("successPayment findById orderId " + orderId);
			if (orders != null) {

				Payment payment = orders.getPayment();
				if (payment.getPaymentMethod().equals(PaymentMethod.MOMO)) {

					// cap nhat trang thai don, cong tien cho manager

					orders.setStatus(OrderStatus.PAID);
					List<OrderItem> orderItems = getOrderItems(orders);
					orders.setOrderItems(orderItems);

					ordersService.save(orders);
					// cap nhat trang thai thanh toan
					// TODO: ap ma giam gia
					payment.setAmount(orders.getTotalPrice());
					payment.setStatus(PaymentStatus.COMPLETED);
					paymentRepository.save(payment);
					System.out.println("payment success");
					return true;
				}

			} else {
				System.err.println("orders is null");
			}
		} catch (Exception e) {
			throw e;
		}
		return rs;
	}
}
