// middleProject - com.mbc.mid.dto - ReservationDto.java
package com.mbc.mid.dto;

public class ReservationDto {
	private Long reservationId;	// PK
	private Long memId;			// FK(mem.memId)
	private Long deptId;		// FK(medDept.deptId)
	private Long doctorId;		// FK(medStaff.staffId)
	private String reservationDate;		// 예약 날짜
	private String reservationTime;		// 예약 시간
	private String reservationType;		// 예약 종류
	private String visitType;			// 초진/재진
	private String reservationStatus;	// 예약 상태
	private String reservationMemo;		// 예약 관련 메모
	private String reservationMadeTime;	// 예약 당시 시간
	
	public ReservationDto() {}
	public ReservationDto(Long reservationId, Long memId, Long deptId, Long doctorId, String reservationDate,
			String reservationTime, String reservationType, String visitType, String reservationStatus,
			String reservationMemo, String reservationMadeTime) {
		this.reservationId = reservationId;
		this.memId = memId;
		this.deptId = deptId;
		this.doctorId = doctorId;
		this.reservationDate = reservationDate;
		this.reservationTime = reservationTime;
		this.reservationType = reservationType;
		this.visitType = visitType;
		this.reservationStatus = reservationStatus;
		this.reservationMemo = reservationMemo;
		this.reservationMadeTime = reservationMadeTime;
	}
	public Long getReservationId() {
		return reservationId;
	}
	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
	public String getReservationDate() {
		return reservationDate;
	}
	public void setReservationDate(String reservationDate) {
		this.reservationDate = reservationDate;
	}
	public String getReservationTime() {
		return reservationTime;
	}
	public void setReservationTime(String reservationTime) {
		this.reservationTime = reservationTime;
	}
	public String getReservationType() {
		return reservationType;
	}
	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}
	public String getVisitType() {
		return visitType;
	}
	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}
	public String getReservationStatus() {
		return reservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	public String getReservationMemo() {
		return reservationMemo;
	}
	public void setReservationMemo(String reservationMemo) {
		this.reservationMemo = reservationMemo;
	}
	public String getReservationMadeTime() {
		return reservationMadeTime;
	}
	public void setReservationMadeTime(String reservationMadeTime) {
		this.reservationMadeTime = reservationMadeTime;
	}
	@Override
	public String toString() {
		return "ReservationDto [reservationId=" + reservationId + ", memId=" + memId + ", deptId=" + deptId
				+ ", doctorId=" + doctorId + ", reservationDate=" + reservationDate + ", reservationTime="
				+ reservationTime + ", reservationType=" + reservationType + ", visitType=" + visitType
				+ ", reservationStatus=" + reservationStatus + ", reservationMemo=" + reservationMemo
				+ ", reservationMadeTime=" + reservationMadeTime + "]";
	}
}
