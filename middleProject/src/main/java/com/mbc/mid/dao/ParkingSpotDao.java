package com.mbc.mid.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mbc.mid.dto.ParkingSpotDto;

@Mapper
@Repository
public interface ParkingSpotDao {

    // 전체 조회
    List<ParkingSpotDto> findAllSpots();

    // 층별 조회
    List<ParkingSpotDto> findByFloor(int floor);

    // ID로 단건 조회
    ParkingSpotDto findById(int spotId);

    // 가장 가까운 빈 자리 (전체)
    ParkingSpotDto findFirstEmpty();

    // 가장 가까운 빈 자리 (전체 - 층 우선, 거리 순)
    ParkingSpotDto findNearestEmptyAll();

    // 입차 
    int parkCar(@Param("spotId") int spotId, @Param("parkingLogId") Long parkingLogId);
    
     // 해당 차량 번호로 오늘 '진료완료'된 기록이 있는지 확인 (있으면 1, 없으면 0)
    int checkClinicVisit(String vehicleNum);
    // 출차할때 필요한것...
    public Integer findSpotIdByParkingLogId(Long parkingLogId);
    // 출차 (is_parked = false, parking_log_id = null)
    void exitCar(int spotId);

    // 전체 출차 (테스트용 초기화)
    void exitAllLogs();
    void exitAllSpots();

    // 주차 가능 자리 수 (floor 있으면 층별, 없으면 전체)
    int countAvailable(Map<String, Object> param);

    // 차량번호 검색
    List<ParkingSpotDto> searchNum(String vehicleNum);
}
