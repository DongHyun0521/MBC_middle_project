package com.mbc.mid.dto;

import java.time.LocalDateTime;

public class ExitPreviewResponse {

    private int spotId;
    private String vehicleNum;
    private LocalDateTime entryTime;
    private LocalDateTime nowTime;
    private boolean isFree;
    private boolean isMember;
    private int totalMinutes;
    private int amount;

    public ExitPreviewResponse() {}

    public int getSpotId() { return spotId; }
    public void setSpotId(int spotId) { this.spotId = spotId; }

    public String getVehicleNum() { return vehicleNum; }
    public void setVehicleNum(String vehicleNum) { this.vehicleNum = vehicleNum; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getNowTime() { return nowTime; }
    public void setNowTime(LocalDateTime nowTime) { this.nowTime = nowTime; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { this.isFree = free; }

    public boolean isMember() { return isMember; }
    public void setMember(boolean member) { this.isMember = member; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
