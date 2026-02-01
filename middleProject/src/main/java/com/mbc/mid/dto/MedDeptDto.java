// middleProject - com.mbc.mid.dto - MedDeptDto.java
package com.mbc.mid.dto;

public class MedDeptDto {
	private Long deptId;				// PK
	private String deptName;			// 부서 이름
	private String officeLocation;		// 부서 위치
	private String officePhoneNumber;	// 부서 전화번호
	
	public MedDeptDto() {}
	public MedDeptDto(Long deptId, String deptName, String officeLocation, String officePhoneNumber) {
		this.deptId = deptId;
		this.deptName = deptName;
		this.officeLocation = officeLocation;
		this.officePhoneNumber = officePhoneNumber;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getOfficeLocation() {
		return officeLocation;
	}
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}
	public String getOfficePhoneNumber() {
		return officePhoneNumber;
	}
	public void setOfficePhoneNumber(String officePhoneNumber) {
		this.officePhoneNumber = officePhoneNumber;
	}
	@Override
	public String toString() {
		return "MedDeptDto [deptId=" + deptId + ", deptName=" + deptName + ", officeLocation=" + officeLocation
				+ ", officePhoneNumber=" + officePhoneNumber + "]";
	}
}
