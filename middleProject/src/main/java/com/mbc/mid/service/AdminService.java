package com.mbc.mid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mbc.mid.dao.AdminDao;
import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dto.*;

@Service
@Transactional
public class AdminService {
    @Autowired private AdminDao adminDao;
    @Autowired private MemDao memDao;

    public void registerAdminStaff(AdminStaffJoinDto joinDto) {
        // 1. 기본 회원정보 저장
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

        // 2. 행정직원 상세정보 저장
        AdminStaffDto adminDto = new AdminStaffDto();
        adminDto.setMemId(memDto.getMemId());
        adminDto.setRank(joinDto.getRank());
        adminDto.setEmpNumber(joinDto.getEmpNumber());
        adminDto.setAdminDeptId(joinDto.getAdminDeptId());
        adminDto.setSpotId(joinDto.getSpotId());
        adminDto.setStatus("재직");

        adminDao.addAdminStaff(adminDto);
    }
}