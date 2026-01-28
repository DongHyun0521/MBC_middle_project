// middleProject - com.mbc.mid.dto - PaymentDto.java
package com.mbc.mid.dto;

import java.time.LocalDateTime;

public class PaymentDto {
	private Long payId;				// PK
    private Long parkingLogId;		// FK (parking_log.parking_log_id)
    private Long memId;				// FK (mem.mem_id)
    private Integer amount;			// 결제 금액
    private String payMethod;		// 결제 수단
    private LocalDateTime payDate;	// 결제 일시
    
	public PaymentDto() {}
	public PaymentDto(Long payId, Long parkingLogId, Long memId, Integer amount, String payMethod, LocalDateTime payDate) {
		this.payId = payId;
		this.parkingLogId = parkingLogId;
		this.memId = memId;
		this.amount = amount;
		this.payMethod = payMethod;
		this.payDate = payDate;
	}
	public Long getPayId() {
		return payId;
	}
	public void setPayId(Long payId) {
		this.payId = payId;
	}
	public Long getParkingLogId() {
		return parkingLogId;
	}
	public void setParkingLogId(Long parkingLogId) {
		this.parkingLogId = parkingLogId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public LocalDateTime getPayDate() {
		return payDate;
	}
	public void setPayDate(LocalDateTime payDate) {
		this.payDate = payDate;
	}
	@Override
	public String toString() {
		return "PaymentDto [payId=" + payId + ", parkingLogId=" + parkingLogId + ", memId=" + memId + ", amount="
				+ amount + ", payMethod=" + payMethod + ", payDate=" + payDate + "]";
	}
}
