package com.mbc.mid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.mid.dto.PaymentDto;
import com.mbc.mid.service.PaymentService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment")
    public String pay(@RequestBody PaymentDto paymentDto) {
        try {
            paymentService.processPayment(paymentDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}