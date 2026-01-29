package com.mbc.mid.controller;

import java.util.Date;

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

    // 출차 시 요금 계산
    @PostMapping("/payment")
    public String pay(@RequestBody PaymentDto paymentDto) {
    	System.out.println("=> PaymentController: pay | "+ new Date());
        try {
            paymentService.processPayment(paymentDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}