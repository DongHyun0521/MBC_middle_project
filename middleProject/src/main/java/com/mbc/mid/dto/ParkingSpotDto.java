// middleProject - com.mbc.mid.dto - ParkingSpotDto.java
package com.mbc.mid.dto;

public class ParkingSpotDto {
	private Long spotId;		// PK
	private Long parkingLogId;	// FK (parking_log.parking_log_id)
    private Integer floor;		// 층수
    private Integer rowNum;		// 행 번호
    private Integer columnNum;	// 열 번호
    private Boolean isParked;	// 주차 여부
    
	public ParkingSpotDto() {}
	public ParkingSpotDto(Long spotId, Long parkingLogId, Integer floor, Integer rowNum, Integer columnNum, Boolean isParked) {
		this.spotId = spotId;
		this.parkingLogId = parkingLogId;
		this.floor = floor;
		this.rowNum = rowNum;
		this.columnNum = columnNum;
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
	public Integer getRowNum() {
		return rowNum;
	}
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	public Integer getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(Integer columnNum) {
		this.columnNum = columnNum;
	}
	public Boolean getIsParked() {
		return isParked;
	}
	public void setIsParked(Boolean isParked) {
		this.isParked = isParked;
	}
	@Override
	public String toString() {
		return "ParkingSpotDto [spotId=" + spotId + ", parkingLogId=" + parkingLogId + ", floor=" + floor + ", rowNum="
				+ rowNum + ", columnNum=" + columnNum + ", isParked=" + isParked + "]";
	}
}
