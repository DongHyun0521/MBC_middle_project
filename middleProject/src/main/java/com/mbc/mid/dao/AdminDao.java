package com.mbc.mid.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.AdminStaffDto;

@Mapper
@Repository
public interface AdminDao {
    void addAdminStaff(AdminStaffDto adminStaffDto);
    // 추후 Notice, FAQ, VOC 메소드 추가
}