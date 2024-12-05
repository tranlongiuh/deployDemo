package com.iuh.canteen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iuh.canteen.service.PaymentService;

@RequestMapping("/api/momo")
@RestController
public class MomoController {

	@Autowired
	private PaymentService paymentService;

	@GetMapping("/success-payment/{orderId}")
	public ResponseEntity<?> successPayment(@PathVariable Long orderId) {
		System.out.println("successPayment");
		try {

			if (paymentService.successPayment(orderId)) {
				System.out.println("successPayment ok");
				return ResponseEntity.ok().body(null);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().body(null);

	}
}
