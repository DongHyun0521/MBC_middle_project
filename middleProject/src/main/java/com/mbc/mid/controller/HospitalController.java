// middleProject - com.mbc.mid.controller - HospitalController.java
package com.mbc.mid.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.ReservationDto;
import com.mbc.mid.dto.StaffJoinDto;
import com.mbc.mid.service.HospitalService;
import com.mbc.mid.service.MemService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class HospitalController {

    // mem하고 많이 겹치는거 같아서 그냥 memController로 이동
    
    @Autowired
    private MemService memService; // 세션 ID로 회원 정보 찾기 위해 추가

    // 1. 의료진 회원가입 (관리자 기능 혹은 공개 회원가입)
    // 세션 체크 없이 진행하거나, 관리자만 가능하게 할 수 있음. 일단 공개로 유지.
    
}