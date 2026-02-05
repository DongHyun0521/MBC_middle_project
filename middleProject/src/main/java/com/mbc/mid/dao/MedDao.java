// middleProject - com.mbc.mid.dao - MedDao.java
package com.mbc.mid.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.MedStaffDto;
import com.mbc.mid.dto.ReservationDto;

@Mapper
@Repository
public interface MedDao {
    void addMedStaff(MedStaffDto staffDto);	// 의료진 회원 가입
    int checkDoctorCount(Long memId);		// 의사인지 확인
    
    void createReservation(ReservationDto resDto);																	// 예약하기
    int cancelReservation(@Param("reservationId") Long reservationId, @Param("memId") Long memId);					// 회원이 예약 취소하기 (예약->취소)
    Long getMedStaffIdByMemId(Long memId);																			// 해당 의사인지 확인
    int cancelReservationByDoctor(@Param("reservationId") Long reservationId, @Param("doctorId") Long doctorId);	// 의사가 예약 강제 취소하기 (예약->취소)
    
    List<Map<String, Object>> getAllDoctors();						// 전체 의사 목록
    List<Map<String, Object>> getAllMedDepts();						// 전체 의료 부서 목록
    List<Map<String, Object>> getDoctorListByDept(Long medDeptId);	// 의료 부서별 의사 목록
    
    List<Map<String, Object>> getReservationsByMember(Long memId);		// 회원별 예약 목록
    List<Map<String, Object>> getReservationsByDoctor(Long doctorId);	// 의사별 예약 목록
}