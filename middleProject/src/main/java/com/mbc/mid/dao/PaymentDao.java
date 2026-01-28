package com.mbc.mid.dao;

import com.mbc.mid.dto.PaymentDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentDao {
    void insertPayment(PaymentDto paymentDto);	// 결제 기록 저장
}