package com.mbc.mid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mbc.mid.dto.AdminStaffJoinDto;
import com.mbc.mid.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AdminService adminService;

    // 행정직원 등록
    @PostMapping("/staff/register")
    public String registerAdminStaff(@RequestBody AdminStaffJoinDto joinDto) {
        try {
            adminService.registerAdminStaff(joinDto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}