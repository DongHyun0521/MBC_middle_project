// middleProject - com.mbc.mid.service - MedService.java
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
	
    @Autowired
    private MedDao medDao;
    
    @Autowired
    private MemDao memDao;

    // 의료진 회원 가입
    public void registerMedStaff(MedStaffJoinDto joinDto) {
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
        
        memDao.addMem(memDto);

        MedStaffDto staffDto = new MedStaffDto();
        staffDto.setMemId(memDto.getMemId());
        staffDto.setRole(joinDto.getRole());
        staffDto.setLicenseNumber(joinDto.getLicenseNumber());
        staffDto.setMedDeptId(joinDto.getMedDeptId());
        staffDto.setSpotId(joinDto.getSpotId());
        staffDto.setStatus("재직");

        medDao.addMedStaff(staffDto);
    }

    // 예약하기
    public void createReservation(ReservationDto resDto) {
        if(resDto.getReservationStatus() == null)
        	resDto.setReservationStatus("예약완료");
        medDao.createReservation(resDto);
    }
    
    // 전체 의사 목록
    public List<Map<String, Object>> getAllDoctors() {
        return medDao.getAllDoctors();
    }

    // 전체 의료 부서 목록
    public List<Map<String, Object>> getAllMedDepts() {
    	return medDao.getAllMedDepts();
    }
    
    // 부서별 의사 목록
    public List<Map<String, Object>> getDoctorListByDept(Long medDeptId) {
    	return medDao.getDoctorListByDept(medDeptId);
    }
    
    // 회원별 예약 목록
    public List<Map<String, Object>> getReservationsByMember(Long memId) {
    	return medDao.getReservationsByMember(memId);
    }
    
    // 의사별 예약 목록
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) {
    	return medDao.getReservationsByDoctor(doctorId);
    }
}