package com.mbc.mid.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.mbc.mid.dto.MemDto;
import com.mbc.mid.dto.MemberVehicleDto;

@Mapper
@Repository
public interface MemDao {
    int idCheck(String id);
    void addMem(MemDto memberDto); // Med, Admin 서비스에서도 호출함
    MemDto login(MemDto memberDto);
    String findId(MemDto memberDto);
    void delMem(String id);
    MemDto getMemberInfo(String id);
    void updateMem(MemDto memberDto);

    // 차량
    List<MemberVehicleDto> getMemberVehicleList(String id);
    void addVehi(MemberVehicleDto vehicleDto);
    void updateVehi(MemberVehicleDto vehicleDto);
    void delVehi(String vehicleNum);
    
    int checkMemberVehicle(String vehicleNum);
    Long getMemIdByVehicle(String vehicleNum);
}