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
    @Autowired private MemDao memDao;

    public int idCheck(String id) { return memDao.idCheck(id); }
    public void addMem(MemDto memDto) { memDao.addMem(memDto); }
    public MemDto login(MemDto memDto) { return memDao.login(memDto); }
    public String findId(MemDto memDto) { return memDao.findId(memDto); }
    public void delMem(String id) { memDao.delMem(id); }
    public MemDto getMemberInfo(String id) { return memDao.getMemberInfo(id); }
    public void updateMem(MemDto memDto) { memDao.updateMem(memDto); }

    public List<MemberVehicleDto> getMemberVehicleList(String id) { return memDao.getMemberVehicleList(id); }
    public void addVehi(MemberVehicleDto vehicleDto) { memDao.addVehi(vehicleDto); }
    public void updateVehi(MemberVehicleDto vehicleDto) { memDao.updateVehi(vehicleDto); }
    public void delVehi(String vehicleNum) { memDao.delVehi(vehicleNum); }
}