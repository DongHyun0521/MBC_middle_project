// middleProject - com.mbc.mid.dao - AdminDao.java
package com.mbc.mid.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.AdminStaffDto;
import com.mbc.mid.dto.FaqDto;
import com.mbc.mid.dto.NoticeDto;
import com.mbc.mid.dto.VocDto;

@Mapper
@Repository
public interface AdminDao {
    void addAdminStaff(AdminStaffDto adminStaffDto);	// 행정직 회원 가입
    int checkAdminCount(Long memId);					// 행정직인지 확인
    Long getAdminStaffIdByMemId(Long memId);			// 회원ID로 행정직인지 확인
    
    void addNotice(NoticeDto noticeDto);	// 공지사항 작성
    void addFaq(FaqDto faqDto);				// FAQ 작성
    
    List<Map<String, Object>> getUnansweredVocList();	// 미답변 고객의소리 목록
    void addVocReply(VocDto vocDto);					// 고객의소리 답글
    void updateVocStatus(Long vocId);					// 고객의소리 미답변->답변완료
}