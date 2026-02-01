// middleProject - com.mbc.mid.dto - StaffJoinDto.java
package com.mbc.mid.dto;

public class StaffJoinDto {
    // MemDto
    private String id;
    private String password;
    private String name;
    private Integer birthday;
    private Integer gender;
    private String address;
    private String addressDetail;
    private String phoneNumber;
    private String email;

    // MedStaffDto
    private String role;
    private String licenseNumber;
    private Long deptId;
    private Long spotId;
	
    public StaffJoinDto() {}
	public StaffJoinDto(String id, String password, String name, Integer birthday, Integer gender, String address,
			String addressDetail, String phoneNumber, String email, String role, String licenseNumber, Long deptId, Long spotId) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.birthday = birthday;
		this.gender = gender;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.role = role;
		this.licenseNumber = licenseNumber;
		this.deptId = deptId;
		this.spotId = spotId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getBirthday() {
		return birthday;
	}
	public void setBirthday(Integer birthday) {
		this.birthday = birthday;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
		return "StaffJoinDto [id=" + id + ", password=" + password + ", name=" + name + ", birthday=" + birthday
				+ ", gender=" + gender + ", address=" + address + ", addressDetail=" + addressDetail + ", phoneNumber="
				+ phoneNumber + ", email=" + email + ", role=" + role + ", licenseNumber=" + licenseNumber + ", deptId="
				+ deptId + ", spotId=" + spotId + "]";
	}
}