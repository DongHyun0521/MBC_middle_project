package com.mbc.mid.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dao.ParkingLogDao;
import com.mbc.mid.dao.ParkingSpotDao;
import com.mbc.mid.dao.PaymentDao;
import com.mbc.mid.dto.EntryPathResponse;
import com.mbc.mid.dto.ExitPreviewResponse;
import com.mbc.mid.dto.ParkingLogDto;
import com.mbc.mid.dto.ParkingSpotDto;
import com.mbc.mid.dto.PaymentDto;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotDao dao;

    @Autowired
    private ParkingLogDao parkingLogDao;

    @Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private MemDao memDao;

    private static final int PREPAY_GRACE_MINUTES = 30;

    // 1. 요금 계산 (진료할인 2시간 포함)
    public int calculateFee(long minutes, boolean isMember, boolean hasClinicVisit) {
        int freeMinutes = 30;					// 기본 30분 무료
        if (hasClinicVisit) freeMinutes += 120;	// 진료 상태 = '완료' 시, 120분 무료

        if (minutes <= freeMinutes) return 0;

        long chargeMinutes = minutes - freeMinutes;
        int unit = (int) Math.ceil(chargeMinutes / 30.0);
        int rate = isMember ? 1000 : 2000;
        int totalAmount = unit * rate;

        long days = (minutes / (24 * 60)) + 1;
        int dailyLimit = isMember ? 15000 : 30000;
        
        return Math.min(totalAmount, (int)(days * dailyLimit));
    }

    // 2. 출차 미리보기
    public ExitPreviewResponse getExitPreview(int spotId) {
        ParkingSpotDto spot = dao.findById(spotId);
        if (spot == null || spot.getParkingLogId() == null) {
            throw new RuntimeException("주차 정보 없음");
        }

        ParkingLogDto log = parkingLogDao.findById(spot.getParkingLogId().intValue());
        
        //boolean isMember = parkingLogDao.isMemberVehicle(log.getVehicleNum()) > 0;
        boolean isMember = memDao.checkMemberVehicle(log.getVehicleNum()) > 0;
        
        boolean hasClinicVisit = paymentDao.checkClinicVisit(log.getVehicleNum()) > 0;

        LocalDateTime now = LocalDateTime.now();
        long minutes = Duration.between(log.getEntryTime(), now).toMinutes();
        int amount = calculateFee(minutes, isMember, hasClinicVisit);

        ExitPreviewResponse res = new ExitPreviewResponse();
        res.setSpotId(spotId);
        res.setVehicleNum(log.getVehicleNum());
        res.setEntryTime(log.getEntryTime());
        res.setNowTime(now);
        res.setMember(isMember);
        res.setFree(amount == 0);
        res.setTotalMinutes((int) minutes);
        res.setAmount(amount);
        // Vue에서 진료 확인 배지를 띄우기 위해 필드 추가(필요시 ExitPreviewResponse DTO에도 추가 필요)
        // res.setHasClinicVisit(hasClinicVisit); 

        return res;
    }

    // 3. 사전정산
    public Map<String, Object> prepay(int spotId) {
        ParkingSpotDto spot = dao.findById(spotId);
        if (spot == null || spot.getParkingLogId() == null) {
            throw new RuntimeException("주차 정보 없음");
        }

        ParkingLogDto log = parkingLogDao.findById(spot.getParkingLogId().intValue());
        if (log.getPaymentStatus() != null && log.getPaymentStatus()) {
            throw new RuntimeException("이미 정산 완료된 차량입니다");
        }

        //boolean isMember = parkingLogDao.isMemberVehicle(log.getVehicleNum()) > 0;
        boolean isMember = memDao.checkMemberVehicle(log.getVehicleNum()) > 0;
        
        boolean hasClinicVisit = paymentDao.checkClinicVisit(log.getVehicleNum()) > 0;
        long minutes = Duration.between(log.getEntryTime(), LocalDateTime.now()).toMinutes();
        int amount = calculateFee(minutes, isMember, hasClinicVisit);

        PaymentDto payment = new PaymentDto();
        payment.setParkingLogId(log.getParkingLogId());
        payment.setAmount(amount);
        paymentDao.insertPayment(payment);

        parkingLogDao.updatePaymentStatus(log.getParkingLogId());

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleNum", log.getVehicleNum());
        result.put("amount", amount);
        result.put("isFree", amount == 0);
        result.put("message", amount == 0 ? "무료 대상입니다." : amount + "원 정산 완료");
        result.put("graceMinutes", PREPAY_GRACE_MINUTES);
        return result;
    }

    // 4. 출차 상태 확인 (Controller: checkExit)
    public Map<String, Object> checkExit(String vehicleNum) {
        ParkingLogDto log = parkingLogDao.selectRecentEntryLog(vehicleNum);
        if (log == null) throw new RuntimeException("입차 기록 없음");

        //boolean isMember = parkingLogDao.isMemberVehicle(vehicleNum) > 0;
        boolean isMember = memDao.checkMemberVehicle(vehicleNum) > 0;
        
        boolean hasClinicVisit = paymentDao.checkClinicVisit(vehicleNum) > 0;
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleNum", vehicleNum);
        result.put("isMember", isMember);
        result.put("hasClinicVisit", hasClinicVisit);

        if (log.getPaymentStatus() != null && log.getPaymentStatus()) {
            PaymentDto payment = paymentDao.findByParkingLogId(log.getParkingLogId());
            if (payment != null) {
                long minSincePay = Duration.between(payment.getPayDate(), now).toMinutes();
                if (minSincePay <= PREPAY_GRACE_MINUTES) {
                    result.put("status", "PREPAID_OK");
                } else {
                    int addFee = calculateFee(minSincePay - PREPAY_GRACE_MINUTES + 30, isMember, hasClinicVisit);
                    result.put("status", "PREPAID_OVER");
                    result.put("additionalFee", addFee);
                }
            }
        } else {
            long totalMin = Duration.between(log.getEntryTime(), now).toMinutes();
            int amount = calculateFee(totalMin, isMember, hasClinicVisit);
            result.put("status", "NOT_PAID");
            result.put("amount", amount);
        }
        return result;
    }

    // 5. 출차 실행 (Controller: processExit)
    public void processExit(String vehicleNum) {
        ParkingLogDto log = parkingLogDao.selectRecentEntryLog(vehicleNum);
        if (log == null) throw new RuntimeException("입차 기록 없음");

        //boolean isMember = parkingLogDao.isMemberVehicle(vehicleNum) > 0;
        boolean isMember = memDao.checkMemberVehicle(vehicleNum) > 0;
        
        boolean hasClinicVisit = paymentDao.checkClinicVisit(vehicleNum) > 0;
        LocalDateTime now = LocalDateTime.now();

        // 유예시간 검증
        if (log.getPaymentStatus() != null && log.getPaymentStatus()) {
            PaymentDto payment = paymentDao.findByParkingLogId(log.getParkingLogId());
            if (payment != null) {
                long minSincePay = Duration.between(payment.getPayDate(), now).toMinutes();
                if (minSincePay > PREPAY_GRACE_MINUTES) {
                    int addFee = calculateFee(minSincePay - PREPAY_GRACE_MINUTES + 30, isMember, hasClinicVisit);
                    throw new RuntimeException("유예시간 초과 - 추가 결제 필요: " + addFee + "원");
                }
            }
        }

        Integer spotId = dao.findSpotIdByParkingLogId(log.getParkingLogId());
        if (spotId != null) dao.exitCar(spotId);
        
        log.setExitTime(now);
        parkingLogDao.updateExitLog(log);
    }

    // 6. 입차 로직 (parkCar, dummyParkCar)
    public EntryPathResponse parkCar(int spotId, String vehicleNum) {
        ParkingLogDto newLog = new ParkingLogDto();
        newLog.setVehicleNum(vehicleNum);
        parkingLogDao.insertEntryLog(newLog);
        
        ParkingLogDto log = parkingLogDao.selectRecentEntryLog(vehicleNum);
        int updated = dao.parkCar(spotId, log.getParkingLogId());
        if (updated == 0) {
            parkingLogDao.deleteById(log.getParkingLogId());
            throw new RuntimeException("주차칸 선점 실패");
        }

        ParkingSpotDto spot = dao.findById(spotId);
        EntryPathResponse res = new EntryPathResponse();
        res.setSpotId(spot.getSpotId().intValue());
        res.setFloor(spot.getFloor());
        res.setZone(spot.getZone());
        res.setSpotNumber(spot.getSpotNumber());
        res.setVehicleNum(vehicleNum);
        return res;
    }

    public EntryPathResponse dummyParkCar(int spotId, String vehicleNum) {
        ParkingLogDto existingLog = parkingLogDao.selectRecentEntryLog(vehicleNum);
        if (existingLog != null && existingLog.getExitTime() == null) {
            throw new RuntimeException("이미 입차된 차량");
        }
        return parkCar(spotId, vehicleNum);
    }

    // 7. 기타 공통 (Controller 매칭 완료)
    public List<ParkingSpotDto> findAllSpots() { return dao.findAllSpots(); }
    public List<ParkingSpotDto> findByFloor(int floor) { return dao.findByFloor(floor); }
    public ParkingSpotDto recommendSpot() { return dao.findFirstEmpty(); }
    public void exit(int spotId) { dao.exitCar(spotId); } // Controller exit 매칭
    public void exitAll() { dao.exitAllLogs(); dao.exitAllSpots(); }
    public List<ParkingSpotDto> searchNum(String vehicleNum) { return dao.searchNum(vehicleNum); }
    public int getAvailableAll() { return dao.countAvailable(new HashMap<>()); }
    public int getAvailableByFloor(int floor) {
        Map<String, Object> param = new HashMap<>();
        param.put("floor", floor);
        return dao.countAvailable(param);
    }
    public Integer findSpotIdByParkingLogId(Long id) { return dao.findSpotIdByParkingLogId(id); }
    public void payAdditional(Long parkingLogId, int amount) {
        PaymentDto payment = new PaymentDto();
        payment.setParkingLogId(parkingLogId);
        payment.setAmount(amount);
        paymentDao.insertPayment(payment);
    }
}