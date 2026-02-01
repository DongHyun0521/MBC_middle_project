// middleProject - com.mbc.mid.dto - MedStaffDto.java
package com.mbc.mid.dto;

public class MedStaffDto {
	private Long staffId;			// PK
	private Long memId;				// FK (mem.memId)
	private String role;			// 직업
	private String licenseNumber;	// 면허 번호
	private String status;			// 재직 상태
	private Long deptId;			// FK(medDept.deptId)
	private Long spotId;			// FK(parkingSpot.spotId)
	
	public MedStaffDto() {}
	public MedStaffDto(Long staffId, Long memId, String role, String licenseNumber, String status, Long deptId, Long spotId) {
		this.staffId = staffId;
		this.memId = memId;
		this.role = role;
		this.licenseNumber = licenseNumber;
		this.status = status;
		this.deptId = deptId;
		this.spotId = spotId;
	}
	public Long getStaffId() {
		return staffId;
	}
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getSpotId() {
		return spotId;
	}
	public void setSpotId(Long spotId) {
		this.spotId = spotId;
	}
	@Override
	public String toString() {
		return "MedStaffDto [staffId=" + staffId + ", memId=" + memId + ", role=" + role + ", licenseNumber="
				+ licenseNumber + ", status=" + status + ", deptId=" + deptId + ", spotId=" + spotId + "]";
	}
}
