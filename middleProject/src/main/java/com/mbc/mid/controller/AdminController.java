// middleProject - com.mbc.mid.controller - AdminController.java
package com.mbc.mid.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mbc.mid.dto.AdminStaffJoinDto;
import com.mbc.mid.dto.FaqDto;
import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.NoticeDto;
import com.mbc.mid.dto.VocDto;
import com.mbc.mid.service.AdminService;
import com.mbc.mid.service.MemService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired 
    private AdminService adminService;
    
    @Autowired 
    private MemService memService;

    // 행정직 회원 가입
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

    // 공지사항 작성
    @PostMapping("/notice/write")
    public String writeNotice(@RequestBody NoticeDto noticeDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return "fail";

        try {
            adminService.addNotice(noticeDto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // FAQ 작성
    @PostMapping("/faq/write")
    public String writeFaq(@RequestBody FaqDto faqDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return "fail";

        try {
            adminService.addFaq(faqDto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 미답변 VOC 목록
    @GetMapping("/voc/unanswered")
    public List<Map<String, Object>> getUnansweredVocList(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return null;
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return null;

        boolean isAdmin = adminService.isAdmin(member.getMemId());
        if (!isAdmin)
            return null;
        
        return adminService.getUnansweredVocList();
    }

    // 고객의소리 답글 작성
    @PostMapping("/voc/reply")
    public String replyVoc(@RequestBody VocDto replyDto, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null)
        	return "fail";
        
        MemDto member = memService.getMemberInfo(loginId);
        if (member == null)
        	return "fail";

        try {
            adminService.replyVoc(replyDto, member.getMemId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}