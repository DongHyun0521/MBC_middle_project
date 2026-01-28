// middleProject - com.mbc.mid.service - MemService.java
package com.mbc.mid.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;

@Service
@Transactional
public class MemService {

    @Autowired
    private MemDao memDao;
    
    // 아이디 중복 확인 (0 -> 중복X)
    public int idCheck(String id) {
        return memDao.idCheck(id);
    }

    // 회원가입
    public void addMem(MemDto memDto) {
        memDao.addMem(memDto);
    }
    
    // 회원탈퇴 (del=0 -> del=1)
    public void delMem(String id) {
        memDao.delMem(id);
    }

    // 로그인
    public MemDto login(MemDto memDto) {
        return memDao.login(memDto);
    }

    // 아이디 찾기
    public String findId(MemDto memDto) {
        return memDao.findId(memDto);
    }

    // 마이페이지 (개인정보)
    public MemDto getMemberInfo(String id) {
        return memDao.getMemberInfo(id);
    }

    // 마이페이지 (차량목록)
    public List<MemberVehicleDto> getMemberVehicleList(String id) {
        return memDao.getMemberVehicleList(id);
    }

    // 회원정보 수정
    public void updateMem(MemDto memDto) {
        memDao.updateMem(memDto);
    }

    // 차량 등록
    public void addVehi(MemberVehicleDto vehicleDto) {
        memDao.addVehi(vehicleDto);
    }
    
    // 차량 수정
    public void updateVehi(MemberVehicleDto vehicleDto) {
        memDao.updateVehi(vehicleDto);
    }

    // 차량 삭제
    public void delVehi(String vehicleNum) {
        memDao.delVehi(vehicleNum);
    }
}