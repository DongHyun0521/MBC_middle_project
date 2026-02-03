// middleProject - com.mbc.mid.service - AdminService.java
package com.mbc.mid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mbc.mid.dao.AdminDao;
import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dto.*;

@Service
@Transactional
public class AdminService {
	
    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private MemDao memDao;

    // 행정직 회원 가입
    public void registerAdminStaff(AdminStaffJoinDto joinDto) {
        MemDto memDto = new MemDto();
        memDto.setId(joinDto.getId());
        memDto.setPassword(joinDto.getPassword());
        memDto.setName(joinDto.getName());
        memDto.setBirthday(joinDto.getBirthday());
        memDto.setGender(joinDto.getGender());
        memDto.setAddress(joinDto.getAddress());
        memDto.setAddressDetail(joinDto.getAddressDetail());
        memDto.setPhoneNumber(joinDto.getPhoneNumber());
        memDto.setEmail(joinDto.getEmail());
        memDto.setDel(0);

        memDao.addMem(memDto);

        AdminStaffDto adminDto = new AdminStaffDto();
        adminDto.setMemId(memDto.getMemId());
        adminDto.setRank(joinDto.getRank());
        adminDto.setEmpNumber(joinDto.getEmpNumber());
        adminDto.setAdminDeptId(joinDto.getAdminDeptId());
        adminDto.setSpotId(joinDto.getSpotId());
        adminDto.setStatus("재직");

        adminDao.addAdminStaff(adminDto);
    }
    
    // 행정직인지 확인
    public boolean isAdmin(Long memId) {
        return adminDao.checkAdminCount(memId) > 0;
    }
    
    // 공지사항 작성
    public void addNotice(NoticeDto noticeDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        noticeDto.setAdminStaffId(adminStaffId);
        adminDao.addNotice(noticeDto);
    }

    // FAQ 작성
    public void addFaq(FaqDto faqDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        faqDto.setAdminStaffId(adminStaffId);
        adminDao.addFaq(faqDto);
    }

    // 고객의소리 VOC 목록
    public List<Map<String, Object>> getUnansweredVocList() {
        return adminDao.getUnansweredVocList();
    }

    // 고객의소리 답글 작성
    public void replyVoc(VocDto replyDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        replyDto.setAdminStaffId(adminStaffId);
        
        replyDto.setMemId(memId); 
        
        replyDto.setTitle("Re: " + replyDto.getTitle());
        adminDao.addVocReply(replyDto);
        
        adminDao.updateVocStatus(replyDto.getParent());
    }
}