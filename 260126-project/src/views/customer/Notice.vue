<template>
    <div class="notice-container">
        <div class="page-header">
            <h2>병원 공지사항</h2>
            <p>서울에스병원의 새로운 소식을 알려드립니다.</p>
        </div>

        <div v-if="mode === 'list'" class="list-wrap">
            <div class="top-controls">
                <div class="total-count">총 <span>{{ noticeList.length }}</span>건</div>
                <div class="right-area">
                    <select v-model="sortOrder" class="sort-select">
                        <option value="desc">최신순</option>
                        <option value="asc">오래된 순</option>
                    </select>
                    <div class="search-box">
                        <input type="text" v-model="keyword" placeholder="검색어 입력" @keyup.enter="getNoticeList">
                        <button class="btn-search" @click="getNoticeList">검색</button>
                    </div>
                    <button v-if="canManageNotice" class="btn-write" @click="goWrite">글쓰기</button>
                </div>
            </div>

            <table class="notice-table">
                <colgroup>
                    <col width="60px" />
                    <col width="*" />
                    <col width="120px" />
                    <col width="80px" />
                </colgroup>
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>등록일</th>
                        <th>조회</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="paginatedList.length === 0">
                        <td colspan="4" class="no-data">등록된 공지사항이 없습니다.</td>
                    </tr>
                    <tr v-for="item in paginatedList" :key="item.noticeId" @click="goDetail(item)" class="notice-row"
                        :class="{ 'pinned': item.topFix }">
                        <td class="text-center">
                            <span v-if="item.topFix" class="badge-red">공지</span>
                            <span v-else>{{ item.noticeId }}</span>
                        </td>
                        <td class="text-left title-cell">
                            {{ item.title }}
                            <span v-if="isNew(item.writeDate)" class="icon-new">N</span>
                            <span v-if="item.hasFile || item.fileName" class="clip-icon">📎</span>
                        </td>
                        <td class="text-center">{{ formatDate(item.writeDate) }}</td>
                        <td class="text-center">{{ item.readCount }}</td>
                    </tr>
                </tbody>
            </table>

            <div class="pagination-area" v-if="noticeList.length > 0">
                <button class="page-btn prev" :disabled="currentPage === 1" @click="currentPage--">&lt;</button>
                <button v-for="page in visiblePages" :key="page" class="page-btn number"
                    :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
                <button class="page-btn next" :disabled="currentPage === totalPages"
                    @click="currentPage++">&gt;</button>
            </div>
        </div>

        <div v-else-if="mode === 'detail'" class="detail-wrap">
            <div class="detail-header">
                <div class="dh-title">
                    <span v-if="selectedItem.topFix" class="badge-red">공지</span>
                    {{ selectedItem.title }}
                </div>
                <div class="dh-info">
                    <span><strong>등록일</strong> {{ formatDate(selectedItem.writeDate) }}</span>
                    <span class="bar">|</span>
                    <span><strong>작성자</strong> 관리자</span>
                    <span class="bar">|</span>
                    <span><strong>조회수</strong> {{ selectedItem.readCount }}</span>
                </div>
            </div>

            <div class="detail-content">
                <div class="view-text" @click="handleLinkClick" v-html="formatContent(selectedItem.content)"></div>

                <div v-if="isImage(selectedItem.fileName)" class="img-preview-box">
                    <img :src="selectedItem.fileUrl" alt="공지 이미지" class="attached-img">
                </div>
            </div>

            <div class="file-attach-area">
                <span class="fa-label">첨부파일</span>
                <div class="fa-content">
                    <a v-if="selectedItem.fileName" :href="selectedItem.fileUrl" download class="file-link">
                        {{ selectedItem.fileName }}
                    </a>
                    <span v-else class="no-file">첨부된 파일이 없습니다.</span>
                </div>
            </div>

            <div class="nav-links">
                <div class="nav-item prev" @click="prevItem ? goDetail(prevItem) : null"
                    :class="{ disabled: !prevItem }">
                    <span class="nav-label">이전글 ▲</span>
                    <span class="nav-title">{{ prevItem ? prevItem.title : '이전 글이 없습니다.' }}</span>
                </div>
                <div class="nav-item next" @click="nextItem ? goDetail(nextItem) : null"
                    :class="{ disabled: !nextItem }">
                    <span class="nav-label">다음글 ▼</span>
                    <span class="nav-title">{{ nextItem ? nextItem.title : '다음 글이 없습니다.' }}</span>
                </div>
            </div>
            
            <div class="btn-group">
                <button class="btn-list" @click="goList">목록</button>
                <div class="admin-btns" v-if="canManageNotice">
                    <button class="btn-mod" @click="goEdit(selectedItem)">수정</button>
                    <button class="btn-del" @click="deleteNotice(selectedItem.noticeId)">삭제</button>
                </div>
            </div>
        </div>

        <div v-else-if="mode === 'write' || mode === 'edit'" class="write-wrap">
            <h3 class="write-title">{{ mode === 'write' ? '공지사항 등록' : '공지사항 수정' }}</h3>
            <div class="write-form">
                <div class="form-row">
                    <label>상단 고정</label>
                    <div class="input-wrap">
                        <input type="checkbox" v-model="writeForm.topFix" id="chkTop">
                        <label for="chkTop" class="chk-label">고정 공지로 등록</label>
                    </div>
                </div>
                <div class="form-row">
                    <label>제목</label>
                    <div class="input-wrap">
                        <input type="text" v-model="writeForm.title" placeholder="제목을 입력하세요" class="full-input">
                    </div>
                </div>

                <div class="form-row">
                    <label>첨부파일</label>
                    <div class="input-wrap file-wrap">
                        <input type="file" ref="fileInput" @change="handleFileChange"
                            accept="image/*, .pdf, .hwp, .doc, .docx">

                        <div v-if="previewUrl" class="preview-box">
                            <img v-if="isImageFile" :src="previewUrl" class="mini-img">
                            <span v-else class="file-name-tag">📄 {{ selectedFile.name }}</span>
                            <button class="btn-x" @click="removeFile">X</button>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <label>내용</label>
                    <div class="input-wrap">
                        <textarea v-model="writeForm.content" placeholder="내용을 입력해 주세요" class="full-textarea"></textarea>
                    </div>
                </div>
            </div>
            <div class="btn-group center">
                <button class="btn-cancel" @click="goList">취소</button>
                <button class="btn-save" @click="submitNotice">{{ mode === 'write' ? '등록' : '수정' }}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
    getNoticesReq, getNoticeDetailReq,
    addNoticeReq, editNoticeReq, delNoticeReq,
    getAdminInfoReq
} from '@/api/customer';

const route = useRoute();
const router = useRouter();

const mode = ref('list');
const keyword = ref('');
const loginInfo = ref({});
const selectedItem = ref({});
const sortOrder = ref('desc');
const currentPage = ref(1);
const itemsPerPage = 10;
const maxPageBtn = 5;
const noticeList = ref([]);
const writeForm = ref({ noticeId: '', title: '', content: '', topFix: false });
const isWonmuState = ref(false);

// [추가] 파일 업로드를 위한 상태 변수들
const fileInput = ref(null);      // <input type="file"> 태그 참조용
const selectedFile = ref(null);   // 실제 선택된 파일 객체
const previewUrl = ref(null);     // 미리보기용 임시 URL
const isImageFile = ref(false);   // 이미지 파일인지 여부 체크


// 1. 권한 체크 (관리자 & 원무과)

// [관리자 확인] 로그인 정보에 'ADMIN'이 있는지 확인
const isAdmin = computed(() => {
    // 로그인 정보가 없으면 빈 객체({})로 (에러 방지)
    const info = loginInfo.value || {};
    
    const userRole = info.loginType || info.role || '';

    if (String(userRole).toUpperCase() === 'ADMIN') {
        return true; 
    } else {
        return false;
    }
});

// [원무과 확인]
const isWonmuTeam = computed(() => {
    // 서버에서 이미 확인된 상태면 바로 통과
    if (isWonmuState.value === true) {
        return true;
    }

    // 로그인 정보에서 부서명(deptName) 확인
    const info = loginInfo.value || {};
    const deptName = String(info.deptName ?? '').trim(); // 공백 제거

    if (deptName.includes('원무')) {
        return true;
    } else {
        return false;
    }
});

const canManageNotice = computed(() => {
    if (isAdmin.value && isWonmuTeam.value) {
        return true;
    } else {
        return false;
    }
});


// 2. 목록 정렬 & 페이징 (데이터 가공)
const sortedList = computed(() => {
    // 원본 데이터를 건드리지 않기 위해 복사본(...)
    const list = [...noticeList.value];

    return list.sort((a, b) => {
        if (a.topFix !== b.topFix) { // 고정된 글(true)이 위로 올라오게
            return Number(b.topFix) - Number(a.topFix);
        }
        if (sortOrder.value === 'desc') {
            return b.noticeId - a.noticeId; // 최신순 (큰 숫자가 위로)
        } else {
            return a.noticeId - b.noticeId; // 오래된순 (작은 숫자가 위로)
        }
    });
});

// [페이징] 현재 페이지에 보여줄 10개만
const paginatedList = computed(() => {
    let targetList = sortedList.value; // 정렬된 리스트 가져오기

    // 검색어 필터링
    if (keyword.value) {
        targetList = targetList.filter(item => {
            return item.title.includes(keyword.value); // 제목에 검색어 포함 여부 확인
        });
    }

    // 잘라낼 범위 계산 (예: 1페이지면 0번부터 10개)
    const startIndex = (currentPage.value - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    // 해당 범위만큼 잘라서 반환
    return targetList.slice(startIndex, endIndex);
});

// [전체 페이지 수] 총 몇 페이지인지 계산
const totalPages = computed(() => {
    let count = 0;
    if (keyword.value) {
        count = sortedList.value.filter(item => item.title.includes(keyword.value)).length;
    } else {
        count = sortedList.value.length;
    }
    // 올림 처리
    const pages = Math.ceil(count / itemsPerPage);

    // 글이 하나도 없어도 최소 1페이지 노출
    if (pages === 0) return 1;
    return pages;
});

// [페이지 버튼] 화면에 보여줄 버튼 번호들 (예: 1, 2, 3, 4, 5)
const visiblePages = computed(() => {
    // 현재 페이지가 속한 그룹 계산 (예: 6페이지면 2번째 그룹)
    const currentGroup = Math.ceil(currentPage.value / maxPageBtn);
    
    // 그룹의 시작 번호와 끝 번호 계산
    const startPage = (currentGroup - 1) * maxPageBtn + 1;
    let endPage = startPage + maxPageBtn - 1;

    if (endPage > totalPages.value) { // 끝 번호가 전체 페이지보다 크면 안 됨
        endPage = totalPages.value;
    }
    const pages = [];
    for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
    }
    return pages;
});



// 3. 상세 페이지 네비게이션 (이전글/다음글)
// [현재 글 위치 찾기] 지금 보고 있는 글이 리스트에서 몇 번째인지
const currentIndex = computed(() => {
    // 글 정보가 없으면 -1 반환
    if (!selectedItem.value.noticeId) return -1;

    // ID가 같은 것 순서(index)찾기
    return sortedList.value.findIndex(item => item.noticeId === selectedItem.value.noticeId);
});

const prevItem = computed(() => {
    // 맨 처음 글이거나(index <= 0) 글을 못 찾았으면 없음(null)
    if (currentIndex.value <= 0) {
        return null;
    }
    // 내 앞 번호 글 반환
    return sortedList.value[currentIndex.value - 1];
});

const nextItem = computed(() => {
    // 맨 마지막 글이거나 글을 못 찾았으면 없음(null)
    if (currentIndex.value === -1 || currentIndex.value >= sortedList.value.length - 1) {
        return null;
    }
    // 내 뒷 번호 글 반환
    return sortedList.value[currentIndex.value + 1];
});

// 4. 유틸리티 함수 
const formatDate = (dateStr) => dateStr ? String(dateStr).substring(0, 10) : '';
const isNew = (dateStr) => dateStr ? (new Date() - new Date(dateStr)) / (1000 * 60 * 60 * 24) < 1 : false;
const guardWonmu = () => { if (!canManageNotice.value) { alert("권한이 없습니다."); return false; } return true; };

// [추가] 파일명 확장자를 확인해서 이미지인지 판단 (jpg, png 등)
const isImage = (fileName) => {
    if (!fileName) return false;
    const ext = fileName.split('.').pop().toLowerCase();
    return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext);
};

// [추가] 줄바꿈 문자를 HTML 태그 <br>로 변환 (v-html용)
const formatContent = (text) => text ? text.replace(/\n/g, '<br>') : '';

// [추가] 본문 내 링크 클릭 시, 새 창이 아닌 SPA 방식으로 부드럽게 이동
const handleLinkClick = (e) => {
    const target = e.target.closest('a'); // 클릭된 요소가 <a> 태그인지 확인
    if (target) {
        const href = target.getAttribute('href');
        // 내부 링크('/'로 시작)만 가로채서 라우터로 이동
        if (href && href.startsWith('/')) {
            e.preventDefault();
            router.push(href);
        }
    }
};

// 5. 화면 이동 및 연동 로직
const goDetail = async (item) => { router.push(`/notice/${item.noticeId}`); };
const goList = () => { router.push('/notice'); mode.value = 'list'; selectedItem.value = {}; getNoticeList(); };
const loadDetailData = async (id) => {
    try {
        const res = await getNoticeDetailReq(id);
        selectedItem.value = res.data;
        mode.value = 'detail';
        window.scrollTo(0, 0);
    } catch (e) { mode.value = 'list'; }
};

// [추가] 파일 선택 시 처리 핸들러
const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    // 파일 타입 확인 후 미리보기 URL 생성
    if (file.type.startsWith('image/')) {
        isImageFile.value = true;
        previewUrl.value = URL.createObjectURL(file);
    } else {
        isImageFile.value = false;
        previewUrl.value = 'doc'; // 문서 파일용 표시
    }
};

// [추가] 선택한 파일 삭제 (초기화)
const removeFile = () => {
    selectedFile.value = null;
    previewUrl.value = null;
    isImageFile.value = false;
    if (fileInput.value) fileInput.value.value = ''; // input 태그 값도 비워줘야 재선택 가능
};

// [수정] 글쓰기 진입 시 파일 정보 초기화 추가
const goWrite = () => {
    if (!guardWonmu()) return;
    writeForm.value = { title: '', content: '', topFix: false };
    removeFile(); // 파일 초기화
    mode.value = 'write';
};

// [수정] 수정 모드 진입 시 파일 정보 초기화 추가
const goEdit = (item) => {
    if (!guardWonmu()) return;
    writeForm.value = { ...item };
    removeFile(); // 파일 초기화
    mode.value = 'edit';
};

watch(() => route.params.id, (newId) => { if (newId) loadDetailData(newId); else mode.value = 'list'; });

// 6. 서버 통신 로직 (CRUD)
const submitNotice = async () => {
    if (!guardWonmu()) return;
    if (!writeForm.value.title || !writeForm.value.content) { alert("내용을 입력해주세요"); return; }

    try {
        const memId = Number(loginInfo.value.memId || 0);

        // [추가/수정] 일반 객체(JSON) 대신 FormData(택배상자) 사용
        const formData = new FormData();
        formData.append('title', writeForm.value.title);
        formData.append('content', writeForm.value.content);
        formData.append('topFix', writeForm.value.topFix); // boolean 값도 문자열로 변환되어 전송됨
        formData.append('memId', memId);

        // 수정 모드일 때는 ID가 필요하고, 신규일 때는 삭제여부 0으로 설정
        if (mode.value === 'edit') {
            formData.append('noticeId', writeForm.value.noticeId);
        } else {
            formData.append('del', 0);
        }

        // 파일이 선택되어 있다면 상자에 담기 ('file'이라는 이름으로 백엔드가 받음)
        if (selectedFile.value) {
            formData.append('file', selectedFile.value);
        }

        // FormData를 API로 전송 (기존 addNoticeReq, editNoticeReq 함수가 받아서 처리)
        let res = mode.value === 'write' ? await addNoticeReq(formData) : await editNoticeReq(formData);

        if (res.data === 'success' || res.data === true) {
            alert("완료되었습니다.");
            goList();
        }
    } catch (e) { alert("오류가 발생했습니다."); }
};

const deleteNotice = async (id) => {
    if (!guardWonmu()) return;
    if (!confirm("정말 삭제하시겠습니까?")) return;
    try {
        const res = await delNoticeReq(id);
        if (res.data === 'success' || res.data === true) { alert("삭제되었습니다."); goList(); }
    } catch (e) { alert("오류가 발생했습니다."); }
};

const getNoticeList = async () => {
    try {
        const params = keyword.value ? { keyword: keyword.value } : null;
        const res = await getNoticesReq(params);
        noticeList.value = res.data || [];
    } catch (err) { noticeList.value = []; }
};

// 7. 페이지 시작
onMounted(async () => {
    const raw = sessionStorage.getItem('loginId');
    if (raw) loginInfo.value = JSON.parse(raw);
    if (isAdmin.value) {
        try { const res = await getAdminInfoReq(); if (res?.data?.isWonmu) isWonmuState.value = true; } catch (e) { }
    }
    await getNoticeList();
    if (route.params.id) loadDetailData(route.params.id);
});
</script>

<style scoped>
.notice-table {
    width: 100%;
    border-top: 2px solid #333;
    border-bottom: 1px solid #333;
    border-collapse: collapse;
}

.notice-container {
    max-width: 1000px;
    margin: 80px auto;
    padding: 0 20px;
    font-family: 'pretendard';
}

.page-header {
    margin-bottom: 40px;
    text-align: center;
}

.page-header h2 {
    font-size: 32px;
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
}

.page-header p {
    color: #666;
    font-size: 15px;
}

.top-controls {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 15px;
}

.total-count {
    font-size: 14px;
    color: #666;
}

.total-count span {
    color: #0171e9;
    font-weight: 700;
}

.right-area {
    display: flex;
    gap: 10px;
}

.sort-select {
    padding: 0 10px;
    border: 1px solid #ddd;
    font-size: 13px;
    outline: none;
    height: 34px;
    cursor: pointer;
    color: #555;
}

.search-box {
    display: flex;
}

.search-box input {
    border: 1px solid #ddd;
    padding: 8px 10px;
    font-size: 13px;
    outline: none;
    width: 200px;
    height: 34px;
    box-sizing: border-box;
}

.btn-search {
    background: #333;
    color: #fff;
    border: none;
    padding: 0 15px;
    cursor: pointer;
    font-size: 13px;
    height: 34px;
}

.btn-write {
    background: #0171e9;
    color: #fff;
    border: none;
    padding: 0 20px;
    font-weight: 600;
    cursor: pointer;
    height: 34px;
}

.notice-table th {
    background: #f8f9fa;
    padding: 15px 0;
    font-size: 14px;
    color: #333;
    border-bottom: 1px solid #ddd;
    font-weight: 600;
}

.notice-table td {
    padding: 15px 10px;
    border-bottom: 1px solid #eee;
    font-size: 14px;
    color: #555;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.notice-row {
    cursor: pointer;
    transition: background 0.2s;
}

.notice-row:hover {
    background: #fbfbfb;
}

.notice-row.pinned {
    background-color: #f0f7ff;
}

.notice-row.pinned td {
    font-weight: 600;
    color: #333;
}

.icon-new {
    display: inline-block;
    background: #ff4d4f;
    color: #fff;
    font-size: 10px;
    padding: 1px 4px;
    margin-left: 5px;
    border-radius: 2px;
    vertical-align: middle;
}

.text-center {
    text-align: center;
}

.text-left {
    text-align: left;
    padding-left: 20px;
}

.no-data {
    text-align: center;
    padding: 50px;
    color: #999;
}

.pagination-area {
    display: flex;
    justify-content: center;
    margin-top: 40px;
}

.page-btn {
    width: 40px;
    height: 40px;
    border: 1px solid #ddd;
    background: #fff;
    color: #666;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
}

.page-btn.active {
    background: #0171e9;
    color: #fff;
    border-color: #0171e9;
}

.detail-wrap {
    border-top: 2px solid #333;
}

.detail-header {
    background: #f8f9fa;
    padding: 25px 30px;
    border-bottom: 1px solid #eee;
}

.dh-title {
    font-size: 22px;
    font-weight: 700;
    color: #333;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.dh-info {
    font-size: 13px;
    color: #777;
}

.bar {
    margin: 0 15px;
    color: #ddd;
}

.detail-content {
    padding: 40px 30px;
    min-height: 200px; /* [수정] 최소 높이 조정 */
    color: #444;
    line-height: 1.6;
    font-size: 15px;
    border-bottom: 1px solid #eee;
}

.btn-group {
    margin-top: 60px;
    display: flex;
    justify-content: space-between;
}

.btn-list {
    padding: 12px 40px;
    background: #fff;
    border: 1px solid #333;
    color: #333;
    font-weight: 600;
    cursor: pointer;
}

.admin-btns {
    display: flex;
    gap: 5px;
}

.btn-mod {
    padding: 12px 25px;
    background: #555;
    color: #fff;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

.btn-del {
    padding: 12px 25px;
    background: #333;
    color: #fff;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

.write-wrap {
    border-top: 2px solid #333;
    padding-top: 30px;
}

.write-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 20px;
    text-align: center;
}

.write-form {
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
}

.form-row {
    display: flex;
    border-bottom: 1px solid #eee;
}

/* [수정 후] : .form-row의 '직계 자식' 라벨에만 적용 (제목용 라벨만 해당됨) */
.form-row > label {
   width: 120px;
   background: #f9f9f9;
   padding: 15px 20px;
   font-weight: 600;
   color: #333;
   display: flex;
   align-items: center;
}

/* 체크박스 옆 텍스트 스타일 */
.chk-label {
  margin-left: 8px; /* 체크박스와 글씨 사이 간격 */
  font-size: 15px;
  color: #333;
  cursor: pointer;
  width: auto; /* 혹시 모르니 width 자동 */
}

/* 체크박스랑 글씨 높이 딱 맞게 정렬 */
.input-wrap {
  display: flex;
  align-items: center;
}

/* 체크박스 크기 살짝 키움 (선택사항) */
.input-wrap input[type="checkbox"] {
  width: 17px;
  height: 17px;
  margin: 0;
  cursor: pointer;
}

.input-wrap {
    flex: 1;
    padding: 10px 15px;
    display: flex;
    align-items: center;
}

.full-input {
    width: 100%;
    border: none;
    padding: 5px;
    font-size: 14px;
    outline: none;
}

.full-textarea {
    width: 100%;
    height: 300px;
    border: none;
    padding: 5px;
    font-size: 14px;
    outline: none;
    resize: none;
    font-family: 'pretendard';
}

.btn-group.center {
    justify-content: center;
    gap: 10px;
}

.btn-cancel {
    padding: 12px 40px;
    background: #fff;
    border: 1px solid #ccc;
    cursor: pointer;
}

.btn-save {
    padding: 12px 40px;
    background: #0171e9;
    color: #fff;
    border: none;
    cursor: pointer;
    font-weight: 600;
}

.badge-red {
    background-color: #ff4d4f;
    color: #fff;
    font-size: 11px;
    font-weight: 600;
    padding: 3px 6px;
    border-radius: 4px;
    display: inline-block;
    margin-right: 5px;
}

.nav-links {
    border-bottom: 1px solid #000000;
    margin-bottom: 30px;
}

.nav-item {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}

.nav-label {
    font-size: 13px;
    font-weight: 600;
    width: 80px;
    color: #333;
}

.nav-title {
    font-size: 14px;
    color: #555;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
    text-decoration: underline darkgray;
    transition: 0.3s;  /* 0.3초 동안 부드럽게 바뀌는 효과 (선택사항) */
    cursor: pointer;
}

.nav-title:hover {
    font-size: 14px;
    color: #c55b5b;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
    text-decoration: underline #c55b5b;
}

/* [추가] 상세화면 링크 스타일 (v-html 내부 적용용) */
.view-text :deep(a) {
    color: #0171e9;
    text-decoration: underline;
    cursor: pointer;
    font-weight: 600;
}

/* [추가] 파일 첨부 관련 스타일 */
.clip-icon {
    font-size: 14px;
    margin-left: 5px;
}

.file-wrap {
    display: flex;
    align-items: center;
    gap: 10px;
}

.preview-box {
    display: flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #eee;
    padding: 5px;
    border-radius: 4px;
}

.mini-img {
    width: 40px;
    height: 40px;
    object-fit: cover;
}

.file-name-tag {
    font-size: 12px;
    color: #666;
}

.btn-x {
    background: #999;
    color: #fff;
    border: none;
    border-radius: 50%;
    width: 18px;
    height: 18px;
    font-size: 11px;
    cursor: pointer;
}

/* [추가] 상세화면 파일 다운로드 영역 (본문 박스 밖으로) */
.file-attach-area {
    margin-top: 0;
    padding: 20px 20px;
    background: #fdfdfd;
    border-bottom: 1px solid #d2d2d2;
    display: flex;
    align-items: center;
}

.fa-label {
    font-weight: 600;
    margin-right: 20px;
    font-size: 14px;
    color: #333;
}

.fa-content {
    flex: 1;
}

.file-link {
    color: #555;
    text-decoration: none;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
}

.file-link:hover {
    color: #0171e9;
    text-decoration: underline;
}

.no-file {
    color: #999;
    font-size: 14px;
}

.img-preview-box {
    margin-top: 20px;
    text-align: center;
}

.attached-img {
    max-width: 100%;
    max-height: 600px;
    border: 1px solid #ddd;
}
</style>