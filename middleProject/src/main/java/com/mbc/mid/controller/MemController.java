// middleProject - com.mbc.mid.controller - MemController.java
package com.mbc.mid.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;
import com.mbc.mid.dto.ReservationDto;
import com.mbc.mid.dto.StaffJoinDto;
import com.mbc.mid.service.HospitalService;
import com.mbc.mid.service.MemService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/member")
//@CrossOrigin(origins = "http://localhost:5173")
public class MemController {

    @Autowired
    private MemService memService;
    
    @Autowired
    private HospitalService hospitalService;

    // 아이디 중복 확인 (return 0 -> 중복X)
    @GetMapping("/idcheck")
    public boolean checkId(@RequestParam("id") String id) {
    	System.out.println("=> MemController: checkId | "+ new Date());
        return memService.idCheck(id) == 0;
    }

    // 회원 가입
    @PostMapping("/regi")
    public String signUp(@RequestBody MemDto memDto) {
    	System.out.println("=> MemController: signUp | "+ new Date());
        try {
            memService.addMem(memDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 회원 탈퇴 (del=0 -> del=1)
    @DeleteMapping("/withdraw")
    public String withdraw(HttpSession session) {
    	System.out.println("=> MemController: withdraw | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return "fail";

        memService.delMem(id);
        session.invalidate();
        return "success";
    }

    // 로그인
    @PostMapping("/login")
    public MemDto login(@RequestBody MemDto memDto, HttpSession session) {
    	System.out.println("=> MemController: login | "+ new Date());
        MemDto loginUser = memService.login(memDto);
        
        if (loginUser != null) {
            session.setAttribute("loginId", loginUser.getId());
            return loginUser;
        } else {
            return null;
        }
    }
    
    // 아이디 찾기
    @PostMapping("/find-id")
    public String findId(@RequestBody MemDto memDto) {
    	System.out.println("=> MemController: findId | "+ new Date());
        return memService.findId(memDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
    	System.out.println("=> MemController: logout | "+ new Date());
        session.invalidate();
        return "success";
    }

    // 마이 페이지 (개인 정보)
    @GetMapping("/mypage")
    public MemDto getMyInfo(HttpSession session) {
    	System.out.println("=> MemController: getMyInfo | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return null;
        
        return memService.getMemberInfo(id);
    }

    // 마이 페이지 (차량 목록)
    @GetMapping("/vehicles")
    public List<MemberVehicleDto> getMyVehicles(HttpSession session) {
    	System.out.println("=> MemController: getMyVehicles | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return null;
        
        return memService.getMemberVehicleList(id);
    }
    
    // 회원 정보 수정
    @PutMapping("/mypageUpdate")
    public String updateInfo(@RequestBody MemDto memDto, HttpSession session) {
    	System.out.println("=> MemController: updateInfo | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return "fail";

        memDto.setId(id);
        memService.updateMem(memDto);
        return "success";
    }

    // 차량 등록
    @PostMapping("/vehiRegi")
    public String addVehicle(@RequestBody MemberVehicleDto vehicleDto, @RequestParam(value = "id", required = false) String paramId, HttpSession session) {
    	System.out.println("=> MemController: addVehicle | "+ new Date());
    	String id = (String) session.getAttribute("loginId");
        
        if (id == null)
            id = paramId;
        
        if (id == null)
        	return "fail";

        MemDto member = memService.getMemberInfo(id);
        
        if (member != null) {
            vehicleDto.setMemId(member.getMemId());
            memService.addVehi(vehicleDto);
            return "success";
        }
        return "fail";
    }
    
    // 차량 수정
    @PutMapping("/vehiUpdate")
    public String updateVehicle(@RequestBody MemberVehicleDto vehicleDto, HttpSession session) {
    	System.out.println("=> MemController: updateVehicle | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        if (id == null)
        	return "fail";

        MemDto member = memService.getMemberInfo(id);
        
        if (member != null) {
            vehicleDto.setMemId(member.getMemId());
            
            try {
                memService.updateVehi(vehicleDto);
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                return "fail";
            }
        }
        return "fail";
    }

    // 차량 삭제
    @DeleteMapping("/vehiDelete")
    public String deleteVehicle(@RequestParam("vehicleNum") String vehicleNum) {
    	System.out.println("=> MemController: deleteVehicle | "+ new Date());
        memService.delVehi(vehicleNum);
        return "success";
    }
    
    // ========== 진료 예약 추가 ==========
    
    // 의료진 회원가입
    @PostMapping("/staffRegi")
    public String registerStaff(@RequestBody StaffJoinDto joinDto) {
        try {
            hospitalService.registerMedStaff(joinDto);
            return "success";
        } catch (Exception e) {
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

        try {
            MemDto member = memService.getMemberInfo(loginId);
            if (member == null)
            	return "fail";

            resDto.setMemId(member.getMemId());
            hospitalService.createReservation(resDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 부서별 의사 목록
    @GetMapping("/staff/dept/{deptId}")
    public List<Map<String, Object>> getDoctorByDept(@PathVariable Long deptId) {
        return hospitalService.getDoctorListByDept(deptId);
    }

    // 회원별 예약 내역
    @GetMapping("/my-reservations")
    public List<Map<String, Object>> getMyReservations(HttpSession session) {
        // 세션 확인
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
            return null;

        // 본인 확인
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return null;

        // 본인의 memId로 조회
        return hospitalService.getReservationsByMember(member.getMemId());
    }

    // 의사별 예약 내역
    // 의사도 로그인을 해야 볼 수 있으므로 세션 적용 가능하지만, 
    // 의사는 staffId가 필요하므로 일단 기존 방식 유지하거나 보안 강화 가능.
    // 여기서는 일단 세션 체크만 추가하고 파라미터는 유지합니다.
    @GetMapping("/reservation/doctor/{doctorId}")
    public List<Map<String, Object>> getDoctorSchedule(@PathVariable Long doctorId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return null;
        
        // *심화: 여기서 loginId가 실제 doctorId의 주인인지 확인하는 로직이 있으면 더 좋음*
        
        return hospitalService.getReservationsByDoctor(doctorId);
    }
}