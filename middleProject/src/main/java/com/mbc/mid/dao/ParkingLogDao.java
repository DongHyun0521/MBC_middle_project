package com.mbc.mid.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.mid.dto.ParkingLogDto;

@Mapper
@Repository
public interface ParkingLogDao {
	void insertEntryLog(String vehicleNum);	// 차량 입차 시
	
    ParkingLogDto selectRecentEntryLog(String vehicleNum);	// 차량 출차 시, 최근부터 입차 기록 검색
    void updateExitLog(ParkingLogDto logDto); 				// 출차 기록 업데이트
    
    void updatePaymentStatus(Long id);	// 결제 상태 업데이트
}