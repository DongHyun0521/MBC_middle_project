// middleProject - com.mbc.mid.dto - MemberVehicleDto.java
package com.mbc.mid.dto;

public class MemberVehicleDto {
	private Long vehicleId;		// PK
    private Long memId;			// FK (mem.mem_id)
    private String vehicleNum;	// 차량 번호 (회원이 등록)
    private String vehicleType;	// 차종
    private String fuelType;	// 연료
    
	public MemberVehicleDto() {}
	public MemberVehicleDto(Long vehicleId, Long memId, String vehicleNum, String vehicleType, String fuelType) {
		this.vehicleId = vehicleId;
		this.memId = memId;
		this.vehicleNum = vehicleNum;
		this.vehicleType = vehicleType;
		this.fuelType = fuelType;
	}
	public Long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public String getVehicleNum() {
		return vehicleNum;
	}
	public void setVehicleNum(String vehicleNum) {
		this.vehicleNum = vehicleNum;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	@Override
	public String toString() {
		return "MemberVehicleDto [vehicleId=" + vehicleId + ", memId=" + memId + ", vehicleNum=" + vehicleNum
				+ ", vehicleType=" + vehicleType + ", fuelType=" + fuelType + "]";
	}
}
