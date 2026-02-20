// middleProject - com.mbc.mid.dto - ParkingSpotDto.java
package com.mbc.mid.dto;

public class ParkingSpotDto {
    private Long spotId;            		// PK
    private Long parkingLogId;      		// FK (현재 주차된 차의 로그)
    private Integer floor;          		// 층
    private String zone;            		// 구역
    private Integer spotNumber;     		// 번호
    private Integer distanceFromEntrance;	// 입구 거리
    private Boolean isParked;       		// 주차 여부
    
    // 추가
    private String vehicleNum;      		// 차량번호 (parking_log JOIN 결과)
    private String entryTime;       		// 입차시간 (차량번호 검색용)
    // 추가 끝
    
	public ParkingSpotDto() {
		super();
	}
	public ParkingSpotDto(Long spotId, Long parkingLogId, Integer floor, String zone, Integer spotNumber,
			Integer distanceFromEntrance, Boolean isParked) {
		super();
		this.spotId = spotId;
		this.parkingLogId = parkingLogId;
		this.floor = floor;
		this.zone = zone;
		this.spotNumber = spotNumber;
		this.distanceFromEntrance = distanceFromEntrance;
		this.isParked = isParked;
	}
	public Long getSpotId() {
		return spotId;
	}
	public void setSpotId(Long spotId) {
		this.spotId = spotId;
	}
	public Long getParkingLogId() {
		return parkingLogId;
	}
	public void setParkingLogId(Long parkingLogId) {
		this.parkingLogId = parkingLogId;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public Integer getSpotNumber() {
		return spotNumber;
	}
	public void setSpotNumber(Integer spotNumber) {
		this.spotNumber = spotNumber;
	}
	public Integer getDistanceFromEntrance() {
		return distanceFromEntrance;
	}
	public void setDistanceFromEntrance(Integer distanceFromEntrance) {
		this.distanceFromEntrance = distanceFromEntrance;
	}
	public Boolean getIsParked() {
		return isParked;
	}
	public void setIsParked(Boolean isParked) {
		this.isParked = isParked;
	}
    
	// 추가
    public String getVehicleNum() {
        return vehicleNum;
    }
    public void setVehicleNum(String vehicleNum) {
        this.vehicleNum = vehicleNum;
    }
    public String getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }
    // 추가 끝
}