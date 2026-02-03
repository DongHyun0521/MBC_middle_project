// middleProject - com.mbc.mid.controller - MedController.java
package com.mbc.mid.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mbc.mid.dto.*;
import com.mbc.mid.service.MedService;
import com.mbc.mid.service.MemService;

@RestController
@RequestMapping("/med")
public class MedController {
	
    @Autowired
    private MedService medService;
    
    @Autowired
    private MemService memService;

    // 의료진 회원 가입
    @PostMapping("/staff/register")
    public String registerStaff(@RequestBody MedStaffJoinDto joinDto) {
        try {
        	medService.registerMedStaff(joinDto);
        	return "success";
        } 
        catch (Exception e) {
        	e.printStackTrace();
        	return "fail";
        }
    }

    // 예약하기
    @PostMapping("/reservation")
    public String makeReservation(@RequestBody ReservationDto resDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return "fail";

        resDto.setMemId(member.getMemId());
        medService.createReservation(resDto);
        return "success";
    }
    
    // 전체 의사 목록
    @GetMapping("/doctors")
    public List<Map<String, Object>> getAllDoctors() {
        return medService.getAllDoctors();
    }

    // 전체 의료 부서 목록
    @GetMapping("/dept/list")
    public List<Map<String, Object>> getAllDepts() {
    	return medService.getAllMedDepts();
    }

    // 부서별 의사 목록
    @GetMapping("/staff/dept/{deptId}")
    public List<Map<String, Object>> getDoctorByDept(@PathVariable Long deptId) {
    	return medService.getDoctorListByDept(deptId);
    }
    
    // 회원별 예약 목록
    @GetMapping("/my-reservations")
    public List<Map<String, Object>> getMyReservations(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return null;
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return null;

        return medService.getReservationsByMember(member.getMemId());
    }
    
    // 의사별 예약 목록
    @GetMapping("/doctor/schedule/{doctorId}")
    public List<Map<String, Object>> getDoctorSchedule(@PathVariable Long doctorId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return null;
        
        return medService.getReservationsByDoctor(doctorId);
    }
}