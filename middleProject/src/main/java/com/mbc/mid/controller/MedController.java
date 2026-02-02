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
    @Autowired private MedService medService;
    @Autowired private MemService memService;

    // 의료진 가입
    @PostMapping("/staff/register")
    public String registerStaff(@RequestBody MedStaffJoinDto joinDto) {
        try { medService.registerMedStaff(joinDto); return "success"; } 
        catch (Exception e) { e.printStackTrace(); return "fail"; }
    }

    // 예약하기 (세션 필요)
    @PostMapping("/reservation")
    public String makeReservation(@RequestBody ReservationDto resDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null) return "fail";

        resDto.setMemId(member.getMemId());
        medService.createReservation(resDto);
        return "success";
    }

    // 내 예약 보기
    @GetMapping("/my-reservations")
    public List<Map<String, Object>> getMyReservations(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return null;
        MemDto member = memService.getMemberInfo(loginId);
        return medService.getReservationsByMember(member.getMemId());
    }

    // 부서 목록
    @GetMapping("/dept/list")
    public List<Map<String, Object>> getAllDepts() { return medService.getAllMedDepts(); }

    // 의사 목록
    @GetMapping("/staff/dept/{deptId}")
    public List<Map<String, Object>> getDoctorByDept(@PathVariable Long deptId) { return medService.getDoctorListByDept(deptId); }
}