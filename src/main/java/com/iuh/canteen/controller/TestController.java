package com.iuh.canteen.controller;

import com.iuh.canteen.dto.OrdersDTO;
import com.iuh.canteen.repository.OrdersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TestController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    ResponseEntity<?> testApi() {

        return ResponseEntity.ok(modelMapper.map(ordersRepository.findByOrderItemsId(15L)
                , OrdersDTO.class));
    }
}
