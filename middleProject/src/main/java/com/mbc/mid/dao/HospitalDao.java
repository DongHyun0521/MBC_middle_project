// middleProject - com.mbc.mid.dao - HospitalDao.java
package com.mbc.mid.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mbc.mid.dto.MedStaffDto;
import com.mbc.mid.dto.ReservationDto;

@Mapper
@Repository
public class HospitalDao {

    @Autowired
    private SqlSession sqlSession;
    
    private static final String NS = "HospitalMapper.";

    // 의료진 회원가입
    public void addStaff(MedStaffDto staffDto) {
        sqlSession.insert(NS + "addStaff", staffDto);
    }

    // 예약 하기
    public void createReservation(ReservationDto resDto) {
        sqlSession.insert(NS + "createReservation", resDto);
    }
    
    // 부서별 의사 리스트
    public List<Map<String, Object>> getDoctorListByDept(Long deptId) {
        return sqlSession.selectList(NS + "getStaffListByDept", deptId);
    }

    // 회원별 예약 내역
    public List<Map<String, Object>> getReservationsByMember(Long memId) {
        return sqlSession.selectList(NS + "getReservationsByMember", memId);
    }

    // 의사별 예약 내역
    public List<Map<String, Object>> getReservationsByDoctor(Long doctorId) {
        return sqlSession.selectList(NS + "getReservationsByDoctor", doctorId);
    }
}