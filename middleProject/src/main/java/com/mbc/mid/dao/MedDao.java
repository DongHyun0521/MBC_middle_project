package com.mbc.mid.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.MedStaffDto;
import com.mbc.mid.dto.ReservationDto;

@Mapper
@Repository
public interface MedDao {
    void addMedStaff(MedStaffDto staffDto);
    void createReservation(ReservationDto resDto);
    List<Map<String, Object>> getAllMedDepts();
    List<Map<String, Object>> getDoctorListByDept(Long medDeptId);
    List<Map<String, Object>> getReservationsByMember(Long memId);
    List<Map<String, Object>> getReservationsByDoctor(Long doctorId);
}