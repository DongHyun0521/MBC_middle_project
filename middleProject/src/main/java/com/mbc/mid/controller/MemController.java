// middleProject - com.mbc.mid.controller - MemController.java
package com.mbc.mid.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;
import com.mbc.mid.dto.VocDto;
import com.mbc.mid.service.MemService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/member")
//@CrossOrigin(origins = "http://localhost:5173")
public class MemController {

    @Autowired
    private MemService memService;

    // 아이디 중복 확인
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
    
    // 회원 탈퇴
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

    // 내 정보
    @GetMapping("/mypage")
    public MemDto getMyInfo(HttpSession session) {
    	System.out.println("=> MemController: getMyInfo | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return null;
        
        return memService.getMemberInfo(id);
    }

    // 내 차량 목록
    @GetMapping("/vehicles")
    public List<MemberVehicleDto> getMyVehicles(HttpSession session) {
    	System.out.println("=> MemController: getMyVehicles | "+ new Date());
        String id = (String) session.getAttribute("loginId");
        
        if (id == null)
        	return null;
        
        return memService.getMemberVehicleList(id);
    }
    
    // 내 정보 수정
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

    // 내 차량 등록
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
    
    // 내 차량 수정
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

    // 내 차량 삭제
    @DeleteMapping("/vehiDelete")
    public String deleteVehicle(@RequestParam("vehicleNum") String vehicleNum) {
    	System.out.println("=> MemController: deleteVehicle | "+ new Date());
        memService.delVehi(vehicleNum);
        return "success";
    }
    
    // 고객의소리 작성
    @PostMapping("/voc/write")
    public String writeVoc(@RequestBody VocDto vocDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return "fail";

        vocDto.setMemId(member.getMemId());
        memService.addVoc(vocDto);
        return "success";
    }
}