// middleProject - com.mbc.mid.dto - VocDto.java
package com.mbc.mid.dto;

public class VocDto {
    private Long vocId;             // PK
    private Long memId;             // FK
    private Long adminStaffId;      // FK
    private Integer ref;            // 그룹 번호
    private Integer step;           // 순서
    private Integer depth;          // 들여쓰기
    private String title;           // 제목
    private String content;         // 내용
    private String writeDate;       // 작성일
    private Integer parent;         // 부모글 ID
    private Boolean answerStatus;   // 답변 여부
    private Integer del;            // 삭제 여부
	public VocDto() {
		super();
	}
	public VocDto(Long vocId, Long memId, Long adminStaffId, Integer ref, Integer step, Integer depth, String title,
			String content, String writeDate, Integer parent, Boolean answerStatus, Integer del) {
		super();
		this.vocId = vocId;
		this.memId = memId;
		this.adminStaffId = adminStaffId;
		this.ref = ref;
		this.step = step;
		this.depth = depth;
		this.title = title;
		this.content = content;
		this.writeDate = writeDate;
		this.parent = parent;
		this.answerStatus = answerStatus;
		this.del = del;
	}
	public Long getVocId() {
		return vocId;
	}
	public void setVocId(Long vocId) {
		this.vocId = vocId;
	}
	public Long getMemId() {
		return memId;
	}
	public void setMemId(Long memId) {
		this.memId = memId;
	}
	public Long getAdminStaffId() {
		return adminStaffId;
	}
	public void setAdminStaffId(Long adminStaffId) {
		this.adminStaffId = adminStaffId;
	}
	public Integer getRef() {
		return ref;
	}
	public void setRef(Integer ref) {
		this.ref = ref;
	}
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
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
	public Integer getParent() {
		return parent;
	}
	public void setParent(Integer parent) {
		this.parent = parent;
	}
	public Boolean getAnswerStatus() {
		return answerStatus;
	}
	public void setAnswerStatus(Boolean answerStatus) {
		this.answerStatus = answerStatus;
	}
	public Integer getDel() {
		return del;
	}
	public void setDel(Integer del) {
		this.del = del;
	}
    
}
