import axios from "axios";

// 1. Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', 
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
});

// ==========================================
// 1. 공지사항 (Notice) API - AdminController
// ==========================================

// 공지사항 목록 조회
export const getNoticesReq = (params) => {
    return url.get('/admin/notice/list', { params });
}

// 공지사항 상세 조회 (경로에 detail 확인!)
export const getNoticeDetailReq = (id) => {
    return url.get(`/admin/notice/detail/${id}`);
}

// [관리자] 공지사항 등록
export const addNoticeReq = (data) => {
    return url.post('/admin/notice/write', data);
}

// [관리자] 공지사항 수정
export const editNoticeReq = (data) => {
    return url.put('/admin/notice/update', data);
}

// [관리자] 공지사항 삭제
export const delNoticeReq = (id) => {
    return url.put(`/admin/notice/delete/${id}`);
}


// ==========================================
// 2. 자주 묻는 질문 (FAQ) API - AdminController
// ==========================================

// FAQ 목록 조회
export const getFaqsReq = (category) => {
    return url.get('/admin/faq/list', { params: { category } });
}

// [관리자] FAQ 등록
export const addFaqReq = (data) => {
    return url.post('/admin/faq/write', data);
}

// [관리자] FAQ 수정
export const editFaqReq = (data) => {
    return url.put('/admin/faq/update', data);
}

// [관리자] FAQ 삭제
export const delFaqReq = (id) => {
    return url.put(`/admin/faq/delete/${id}`);
}


// ==========================================
// 3. 고객의 소리 (VOC) API 
// ==========================================

// ------------------------------------------
// [A. 일반 회원용] (MemController)
// ------------------------------------------

// 내 VOC 목록 조회 (이름을 getMyVocsReq로 변경하여 Voc.vue와 통일!)
export const getMyVocsReq = (filter) => {
    return url.get('/member/voc/list', { params: { filter } });
}

// 글 작성
export const addVocReq = (data) => {
    return url.post('/member/voc/write', data);
}

// 글 수정
export const editVocReq = (data) => {
    return url.put('/member/voc/update', data);
}

// 글 삭제 (회원 본인 삭제)
export const delVocReq = (id) => {
    return url.delete(`/member/voc/delete/${id}`);
}

// ------------------------------------------
// [B. 원무팀 관리자용] (AdminController)
// ------------------------------------------

// 전체 목록 조회 (필터 포함)
export const getAdminVocListReq = (filter) => {
    return url.get('/admin/voc/list', { params: { filter } });
}

// 상세 조회 (삭제된 글 포함 확인용)
export const getAdminVocDetailReq = (id) => {
    return url.get(`/admin/voc/detail/${id}`);
}

// 답변 등록 (주석 해제!)
export const addVocReplyReq = (data) => {
    return url.post('/admin/voc/reply', data);
}

// 글 강제 삭제 (휴지통 이동 -> del=1)
export const delVocByAdminReq = (id) => {
    return url.delete(`/admin/voc/delete/${id}`);
}

// 글 복구 (휴지통 -> del=0)
export const restoreVocReq = (id) => {
    return url.put(`/admin/voc/restore/${id}`);
}

// 추가추가
export const getAdminInfoReq = () => {
    return url.get('/admin/my-info'); // 백엔드 컨트롤러 URL
};