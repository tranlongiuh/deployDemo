package com.iuh.canteen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TestController {



    @GetMapping
    ResponseEntity<?> testApi() {

        return ResponseEntity.ok("testApi");
    }
}
