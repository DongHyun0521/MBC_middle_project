// middleProject - com.mbc.mid.dto - NoticeDto.java
package com.mbc.mid.dto;

public class NoticeDto {
    private Long noticeId;          // PK
    private Long adminStaffId;      // FK
    private Boolean topFix;         // 상단 고정
    private String title;           // 제목
    private String content;         // 내용
    private String writeDate;       // 작성일
    private Integer readCount;      // 조회수
	public NoticeDto() {
		super();
	}
	public NoticeDto(Long noticeId, Long adminStaffId, Boolean topFix, String title, String content, String writeDate,
			Integer readCount) {
		super();
		this.noticeId = noticeId;
		this.adminStaffId = adminStaffId;
		this.topFix = topFix;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.readCount = readCount;
	}
	public Long getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public Boolean getTopFix() {
		return topFix;
	}
	public void setTopFix(Boolean topFix) {
		this.topFix = topFix;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public Integer getReadCount() {
		return readCount;
	}
	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
    
}
