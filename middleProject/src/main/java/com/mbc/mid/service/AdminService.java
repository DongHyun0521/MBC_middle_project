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
    
    // 행정 부서 전체 목록
    public List<Map<String, Object>> getAllAdminDepts() {
        return adminDao.getAllAdminDepts();
    }
    
    // ========== 공지사항 ==========
    
    // 공지사항 전체 목록
    public List<NoticeDto> getAllNoticeList() {
        return adminDao.getAllNoticeList();
    }
    
    // 공지사항 상세 보기
    public NoticeDto getNoticeDetail(Long noticeId) {
        return adminDao.getNoticeDetail(noticeId);
    }
    
    // 공지사항 조회수 증가
    public void increaseNoticeReadCount(Long noticeId) {
        adminDao.increaseNoticeReadCount(noticeId);
    }
    
    // 공지사항 작성
    public void addNotice(NoticeDto noticeDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        noticeDto.setAdminStaffId(adminStaffId);
        adminDao.addNotice(noticeDto);
    }
    
    // 공지사항 수정
    public int updateNotice(NoticeDto noticeDto) {
        return adminDao.updateNotice(noticeDto);
    }
    
    // 공지사항 삭제 (del=1)
    public int deleteNotice(Long noticeId) {
    	return adminDao.deleteNotice(noticeId);
    }
    
    // ========== FAQ ==========
    
    // FAQ 전체/카테고리별 목록
    public List<FaqDto> getFaqList(String category) {
        if (category == null || category.isEmpty()) return adminDao.getAllFaqList();
        else return adminDao.getFaqListByCategory(category);
    }

    // FAQ 작성
    public void addFaq(FaqDto faqDto, Long memId) {
        Long adminStaffId = adminDao.getAdminStaffIdByMemId(memId);
        faqDto.setAdminStaffId(adminStaffId);
        adminDao.addFaq(faqDto);
    }
    
    // FAQ 수정
    public int updateFaq(FaqDto faqDto) {
    	return adminDao.updateFaq(faqDto);
    }
    
    // FAQ 삭제 (del=1)
    public int deleteFaq(Long faqId) {
    	return adminDao.deleteFaq(faqId);
    }
    
    // ========== 고객의소리 ==========
    
    // 행정의 부서 이름 확인
    public String getAdminDeptName(Long memId) {
        return adminDao.getAdminDeptName(memId);
    }
    
    // 무슨 부서(원무)인지 확인
    public boolean isWonMu(Long memId) {
        String deptName = adminDao.getAdminDeptName(memId);
        return deptName != null && deptName.contains("원무");
    }
    
    // 고객의소리 목록 (전체/미답변/답변완료/삭제)
    public List<VocDto> getAllVocList(String filter) {
        if (filter == null || filter.isEmpty()) filter = "all";
        return adminDao.getAllVocList(filter);
    }
    
    // 고객의소리 상세 보기
    public VocDto getVocDetail(Long vocId) {
        return adminDao.getVocDetail(vocId);
    }
    
    // 고객의소리 강제 삭제 (del=1)
    public int deleteVocByAdmin(Long vocId) {
        return adminDao.deleteVocByAdmin(vocId);
    }
    
    // 고객의소리 복구 (del=0)
    public int restoreVoc(Long vocId) {
        return adminDao.restoreVoc(vocId);
    }

    // 고객의소리 답글 작성
    public int addReply(VocDto vocDto, Long memId) {
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.addReply(vocDto);
    }

    // 고객의소리 답글 수정
    public int updateReply(VocDto vocDto, Long memId) {
        Long staffId = adminDao.getAdminStaffIdByMemId(memId);
        vocDto.setAdminStaffId(staffId);
        return adminDao.updateReply(vocDto);
    }

    // 고객의소리 답글 삭제 (NULL)
    public int deleteReply(Long vocId) {
        return adminDao.deleteReply(vocId);
    }
}