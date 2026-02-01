// middleProject - com.mbc.mid.service - HospitalService.java
package com.mbc.mid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.mid.dao.HospitalDao;
import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dto.MedStaffDto;
import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.ReservationDto;
import com.mbc.mid.dto.StaffJoinDto;

@Service
@Transactional
public class HospitalService {

    @Autowired
    private MemDao memDao;
    
    @Autowired
    private HospitalDao hospitalDao;

    // 의료진 회원가입
    public void registerMedStaff(StaffJoinDto joinDto) {
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

        memDao.addMem(memDto);	// memId
        MedStaffDto staffDto = new MedStaffDto();
        
        staffDto.setMemId(memDto.getMemId());	// FK memId
        staffDto.setRole(joinDto.getRole());
        staffDto.setLicenseNumber(joinDto.getLicenseNumber());
        staffDto.setDeptId(joinDto.getDeptId());
        staffDto.setSpotId(joinDto.getSpotId());
        staffDto.setStatus("재직");

        hospitalDao.addStaff(staffDto);
    }

    // 예약 하기
    public void createReservation(ReservationDto resDto) {
        if(resDto.getReservationStatus() == null) {
            resDto.setReservationStatus("예약완료");
        }
        hospitalDao.createReservation(resDto);
    }
    
    // 부서별 의사 리스트
    public List<Map<String, Object>> getDoctorListByDept(Long deptId) {
        return hospitalDao.getDoctorListByDept(deptId);
    }

    // 회원별 예약 내역
    public List<Map<String, Object>> getReservationsByMember(Long memId) {
        return hospitalDao.getReservationsByMember(memId);
    }
    
    // 의사별 예약 내역
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) {
        return hospitalDao.getReservationsByDoctor(doctorId);
    }
}