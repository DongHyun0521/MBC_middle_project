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
import com.mbc.mid.dto.VocDto;

@Service
@Transactional
public class MemService {
    @Autowired private MemDao memDao;

    // 아이디 중복 확인
    public int idCheck(String id) {
    	System.out.println("=> MemService: idCheck | "+ new Date());
    	return memDao.idCheck(id);
    }
    
    // 회원 가입
    public void addMem(MemDto memDto) {
    	System.out.println("=> MemService: addMem | "+ new Date());
    	memDao.addMem(memDto);
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
    
    // 회원 탈퇴
    public void delMem(String id) {
    	System.out.println("=> MemService: delMem | "+ new Date());
    	memDao.delMem(id);
    }
    
    // 내 정보
    public MemDto getMemberInfo(String id) {
    	System.out.println("=> MemService: getMemberInfo | "+ new Date());
    	return memDao.getMemberInfo(id);
    }
    
    // 내 정보 수정
    public void updateMem(MemDto memDto) {
    	System.out.println("=> MemService: updateMem | "+ new Date());
    	memDao.updateMem(memDto);
    }
    
    // 내 차량 목록
    public List<MemberVehicleDto> getMemberVehicleList(String id) {
    	System.out.println("=> MemService: getMemberVehicleList | "+ new Date());
    	return memDao.getMemberVehicleList(id);
    }
    
    // 내 차량 추가
    public void addVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: addVehi | "+ new Date());
    	memDao.addVehi(vehicleDto);
    }
    
    // 내 차량 수정
    public void updateVehi(MemberVehicleDto vehicleDto) {
    	System.out.println("=> MemService: updateVehi | "+ new Date());
    	memDao.updateVehi(vehicleDto);
    }
    
    // 내 차량 삭제
    public void delVehi(String vehicleNum) {
    	System.out.println("=> MemService: delVehi | "+ new Date());
    	memDao.delVehi(vehicleNum);
    }
    
    // 고객의소리 작성
    public void addVoc(VocDto vocDto) {
        memDao.addVoc(vocDto);
    }
}