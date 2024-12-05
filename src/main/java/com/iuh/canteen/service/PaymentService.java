package com.iuh.canteen.service;

public interface PaymentService {

    Boolean purchase(String username, Long orderId, String confirmPassword);
	boolean successPayment(Long orderId);
}
