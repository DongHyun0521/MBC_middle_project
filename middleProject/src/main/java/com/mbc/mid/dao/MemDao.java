// middleProject - com.mbc.mid.dao - MemDao.java
package com.mbc.mid.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;
import com.mbc.mid.dto.VocDto;

@Mapper
@Repository
public interface MemDao {
    int idCheck(String id);				// 아이디 중복 확인
    void addMem(MemDto memberDto);		// 회원 가입
    MemDto login(MemDto memberDto);		// 로그인
    String findId(MemDto memberDto);	// 아이디 찾기
    void delMem(String id);				// 회원 탈퇴
    MemDto getMemberInfo(String id);	// 내 정보
    void updateMem(MemDto memberDto);	// 내 정보 수정

    List<MemberVehicleDto> getMemberVehicleList(String id);	// 내 차량 목록
    void addVehi(MemberVehicleDto vehicleDto);				// 내 차량 추가
    void updateVehi(MemberVehicleDto vehicleDto);			// 내 차량 수정
    void delVehi(String vehicleNum);						// 내 차량 삭제
    
    int checkMemberVehicle(String vehicleNum);
    Long getMemIdByVehicle(String vehicleNum);
    
    void addVoc(VocDto vocDto);	// 고객의소리 작성
}