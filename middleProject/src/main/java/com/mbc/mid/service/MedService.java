package com.mbc.mid.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mbc.mid.dao.MedDao;
import com.mbc.mid.dao.MemDao; // MemDao 주입 (회원테이블 저장용)
import com.mbc.mid.dto.*;

@Service
@Transactional
public class MedService {
    @Autowired private MedDao medDao;
    @Autowired private MemDao memDao; // 기본 회원정보 처리를 위해 필요

    // 의료진 회원가입
    public void registerMedStaff(MedStaffJoinDto joinDto) {
        // 1. 기본 회원정보 저장 (MemDao 사용)
        MemDto memDto = new MemDto();
        memDto.setId(joinDto.getId());
        memDto.setPassword(joinDto.getPassword());
        memDto.setName(joinDto.getName());
        memDto.setBirthday(joinDto.getBirthday());
        memDto.setGender(joinDto.getGender());
        memDto.setAddress(joinDto.getAddress());
        memDto.setAddressDetail(joinDto.getAddressDetail());
        memDto.setPhoneNumber(joinDto.getPhoneNumber());
        memDto.setEmail(joinDto.getEmail());
        memDto.setDel(0);
        
        memDao.addMem(memDto); // DB 실행 후 memId 생성됨

        // 2. 의료진 상세정보 저장 (MedDao 사용)
        MedStaffDto staffDto = new MedStaffDto();
        staffDto.setMemId(memDto.getMemId());
        staffDto.setRole(joinDto.getRole());
        staffDto.setLicenseNumber(joinDto.getLicenseNumber());
        staffDto.setMedDeptId(joinDto.getMedDeptId());
        staffDto.setSpotId(joinDto.getSpotId());
        staffDto.setStatus("재직");

        medDao.addMedStaff(staffDto);
    }

    public void createReservation(ReservationDto resDto) {
        if(resDto.getReservationStatus() == null) resDto.setReservationStatus("예약완료");
        medDao.createReservation(resDto);
    }

    public List<Map<String, Object>> getAllMedDepts() { return medDao.getAllMedDepts(); }
    public List<Map<String, Object>> getDoctorListByDept(Long medDeptId) { return medDao.getDoctorListByDept(medDeptId); }
    public List<Map<String, Object>> getReservationsByMember(Long memId) { return medDao.getReservationsByMember(memId); }
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) { return medDao.getReservationsByDoctor(doctorId); }
}