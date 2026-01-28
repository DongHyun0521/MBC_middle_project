// middleProject - com.mbc.mid.dao - MemDao.java
package com.mbc.mid.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;

@Mapper
@Repository
public interface MemDao {
	int idCheck(String id);			// 아이디 중복 확인
    void addMem(MemDto memberDto);	// 회원 가입
    void delMem(String id);			// 회원 탈퇴 (del=0 -> del=1)
    
    MemDto login(MemDto memberDto);		// 로그인
    String findId(MemDto memberDto);	// 아이디 찾기
    
    MemDto getMemberInfo(String id);						// 마이페이지 (개인 정보)
    List<MemberVehicleDto> getMemberVehicleList(String id);	// 마이페이지 (차량 목록)
    void updateMem(MemDto memberDto);						// 회원 정보 수정
    
    void addVehi(MemberVehicleDto vehicleDto);		// 차량 등록
    void updateVehi(MemberVehicleDto vehicleDto);	// 차량 수정
    void delVehi(String vehicleNum);				// 차량 삭제
    
    int checkMemberVehicle(String vehicleNum);	// 차량 번호로 회원 차량인지 확인
    Long getMemIdByVehicle(String vehicleNum);	// 차량 번호로 회원ID 찾기
}