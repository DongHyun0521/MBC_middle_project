// middleProject - com.mbc.mid.service - OcrService.java
package com.mbc.mid.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.mid.dao.ParkingLogDao;
import com.mbc.mid.dao.PaymentDao;
import com.mbc.mid.dto.ParkingLogDto;
import com.mbc.mid.dto.PaymentDto;

@Service
@Transactional
public class PaymentService {
	
    @Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private ParkingSpotService parkingSpotService;

    // 결제 정보 저장
    public void processPayment(PaymentDto paymentDto) {
    	System.out.println("=> PaymentService: processPayment | "+ new Date());
        paymentDao.insertPayment(paymentDto);
        parkingLogDao.updatePaymentStatus(paymentDto.getParkingLogId());
        ParkingLogDto log = parkingLogDao.findById(paymentDto.getParkingLogId().intValue());
        if (log != null) parkingSpotService.processExit(log.getVehicleNum());
    }
}
