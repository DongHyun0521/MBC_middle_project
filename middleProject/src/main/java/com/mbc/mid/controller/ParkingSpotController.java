package com.mbc.mid.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mbc.mid.dto.EntryPathResponse;
import com.mbc.mid.dto.ExitPreviewResponse;
import com.mbc.mid.dto.ParkingSpotDto;
import com.mbc.mid.service.ParkingSpotService;

@RestController
@RequestMapping("/parking/spot")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;

    // =====================
    // 전체 조회
    // =====================
    @GetMapping("/list")
    public List<ParkingSpotDto> list() {
        return service.findAllSpots();
    }

    // =====================
    // 층별 조회
    // =====================
    @GetMapping("/list/floor/{floor}")
    public List<ParkingSpotDto> listByFloor(@PathVariable int floor) {
        return service.findByFloor(floor);
    }

    // =====================
    // 출차 미리보기
    // =====================
    @GetMapping("/exit/preview/{spotId}")
    public ExitPreviewResponse previewExit(@PathVariable int spotId) {
        return service.getExitPreview(spotId);
    }

    // =====================
    // 차량 번호 검색
    // =====================
    @GetMapping("/searchNum")
    public List<ParkingSpotDto> searchNum(@RequestParam String vehicleNum) {
        return service.searchNum(vehicleNum);
    }

    // =====================
    // 사전정산
    // =====================
    @PostMapping("/prepay/{spotId}")
    public Map<String, Object> prepay(@PathVariable int spotId) {
        return service.prepay(spotId);
    }

    // =====================
    // 출차 정산 확인 (OCR 출차 시)
    // =====================
    @GetMapping("/exit/check")
    public Map<String, Object> checkExit(@RequestParam String vehicleNum) {
        return service.checkExit(vehicleNum);
    }

    // =====================
    // 출차 실행
    // =====================
    @PostMapping("/exit/process")
    public void processExit(@RequestParam String vehicleNum) {
        service.processExit(vehicleNum);
    }

    // =====================
    // 출차 시 추가정산 결제
    // =====================
    @PostMapping("/exit/pay-additional")
    public void payAdditional(@RequestParam Long parkingLogId, @RequestParam int amount) {
        service.payAdditional(parkingLogId, amount);
    }

    // =====================
    // 더미 입차 (테스트용)
    // =====================
    @PostMapping("/entry/dummy")
    public EntryPathResponse dummyEntry() {
        ParkingSpotDto spot = service.recommendSpot();
        if (spot == null) {
            throw new RuntimeException("주차 가능한 공간이 없습니다.");
        }
        String vehicleNum = generateDummyVehicleNum();
        return service.dummyParkCar(spot.getSpotId().intValue(), vehicleNum);
    }

    // =====================
    // 출차 (기존 — spot만 해제)
    // =====================
    @PostMapping("/exit/{spotId}")
    public void exit(@PathVariable int spotId) {
        service.exit(spotId);
    }

    // =====================
    // 전체 출차 (테스트용 초기화)
    // =====================
    @PostMapping("/reset/all")
    public void exitAll() {
        service.exitAll();
    }

    // =====================
    // 주차 가능 수 (전체 or 층별)
    // =====================
    @GetMapping("/count/available")
    public int countAvailable(@RequestParam(required = false) Integer floor) {
        if (floor != null) {
            return service.getAvailableByFloor(floor);
        }
        return service.getAvailableAll();
    }

    // =====================
    // 더미 차량번호 생성
    // =====================
    private String generateDummyVehicleNum() {
        String[] mid = {"가","나","다","라","마","바","사","아","자","차"};
        int front = (int) (Math.random() * 90) + 10;
        int back  = (int) (Math.random() * 9000) + 1000;

        return front + mid[(int)(Math.random() * mid.length)] + back;
    }
}
