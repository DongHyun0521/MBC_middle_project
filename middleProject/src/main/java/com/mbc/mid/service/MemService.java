// middleProject - com.mbc.mid.service - MemService.java
package com.mbc.mid.service;

import java.util.Date;
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
    	System.out.println("=> MemService: idCheck | "+ new Date());
        return memDao.idCheck(id);
    }

    // 회원가입
    public void addMem(MemDto memDto) {
    	System.out.println("=> MemService: addMem | "+ new Date());
        memDao.addMem(memDto);
    }
    
    // 회원탈퇴 (del=0 -> del=1)
    public void delMem(String id) {
    	System.out.println("=> MemService: delMem | "+ new Date());
        memDao.delMem(id);
    }

    // 로그인
    public MemDto login(MemDto memDto) {
    	System.out.println("=> MemService: login | "+ new Date());
        return memDao.login(memDto);
    }

    // 아이디 찾기
    public String findId(MemDto memDto) {
    	System.out.println("=> MemService: findId | "+ new Date());
        return memDao.findId(memDto);
    }

    // 마이페이지 (개인정보)
    public MemDto getMemberInfo(String id) {
    	System.out.println("=> MemService: getMemberInfo | "+ new Date());
        return memDao.getMemberInfo(id);
    }

    // 마이페이지 (차량목록)
    public List<MemberVehicleDto> getMemberVehicleList(String id) {
    	System.out.println("=> MemService: getMemberVehicleList | "+ new Date());
        return memDao.getMemberVehicleList(id);
    }

    // 회원정보 수정
    public void updateMem(MemDto memDto) {
    	System.out.println("=> MemService: updateMem | "+ new Date());
        memDao.updateMem(memDto);
    }

    // 차량 등록
    public void addVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: addVehi | "+ new Date());
        memDao.addVehi(vehicleDto);
    }
    
    // 차량 수정
    public void updateVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: updateVehi | "+ new Date());
        memDao.updateVehi(vehicleDto);
    }

    // 차량 삭제
    public void delVehi(String vehicleNum) {
    	System.out.println("=> MemService: delVehi | "+ new Date());
        memDao.delVehi(vehicleNum);
    }
}