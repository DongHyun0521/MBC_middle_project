// middleProject - com.mbc.mid.dao - PaymentDao.java
package com.mbc.mid.dao;

import com.mbc.mid.dto.PaymentDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentDao {
    void insertPayment(PaymentDto paymentDto);	// 결제 기록 저장
    
    // 추가
    int checkClinicVisit(String vehicleNum);			// 진료완료 여부 확인 (당일 진료완료 시 무료)
    PaymentDto findByParkingLogId(Long parkingLogId);	// 사전정산 조회 (출차 시 pay_date 확인용)
    // 추가 끝
}