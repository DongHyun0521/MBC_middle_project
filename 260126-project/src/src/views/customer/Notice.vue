<template>
    <div class="notice-container">
        <div class="page-header">
            <h2>병원 공지사항</h2>
            <p>S-HOSPITAL의 새로운 소식을 알려드립니다.</p>
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
                    <!-- 원무팀만 글쓰기 가능 -->
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
                        </td>
                        <td class="text-center">{{ formatDate(item.writeDate) }}</td>
                        <td class="text-center">{{ item.readCount }}</td>
                    </tr>
                </tbody>
            </table>

            <div class="pagination-area" v-if="noticeList.length > 0">
                <button class="page-btn prev" :disabled="currentPage === 1" @click="currentPage--">&lt;</button>
                <button v-for="page in visiblePages" :key="page" class="page-btn number"
                    :class="{ active: currentPage === page }" @click="currentPage = page">
                    {{ page }}
                </button>
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
                <pre>{{ selectedItem.content }}</pre>
            </div>
            <div class="btn-group">
                <button class="btn-list" @click="goList">목록</button>

                <!-- 원무팀만 수정/삭제 가능 -->
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
                    <input type="checkbox" v-model="writeForm.topFix" id="chkTop">
                    <label for="chkTop" class="chk-label">고정 공지로 등록</label>
                </div>
                <div class="form-row">
                    <label>제목</label>
                    <input type="text" v-model="writeForm.title" placeholder="제목을 입력하세요">
                </div>
                <div class="form-row">
                    <label>내용</label>
                    <textarea v-model="writeForm.content" placeholder="내용을 입력하세요"></textarea>
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
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { getNoticesReq, getNoticeDetailReq, addNoticeReq, editNoticeReq, delNoticeReq, getAdminInfoReq } from '@/api/customer';

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

// 1) 행정직/관리자 로그인 여부
/*const isAdmin = computed(() => {
  const info = loginInfo.value || {};
  return String(info.loginType || info.role || '').toUpperCase() === 'ADMIN';
});*/

const isWonmuState = ref(false);

const isAdmin = computed(() => {
  const info = loginInfo.value || {};
  return String(info.loginType || info.role || '').toUpperCase() === 'ADMIN';
});

// 2) 원무팀 여부 (부서명 필드가 있어야 true가 됨)
/*const isWonmuTeam = computed(() => {
  const info = loginInfo.value || {};
  const dept = String(
    info.deptName ?? info.dept_name ?? info.adminDeptName ?? info.med_dept_name ?? ''
  ).trim();
  return dept.includes('원무');
});*/

const isWonmuTeam = computed(() => {
  // 1. API로 확인된 값이 있으면 통과
  if (isWonmuState.value) return true;

  // 2. 기존 로직 (세션 정보 확인)
  const info = loginInfo.value || {};
  const dept = String(
    info.deptName ?? info.dept_name ?? info.adminDeptName ?? info.med_dept_name ?? ''
  ).trim();
  return dept.includes('원무');
});

// 3) 공지 관리 권한 = 원무팀 ADMIN만
const canManageNotice = computed(() => isAdmin.value && isWonmuTeam.value);

// 공통 가드
const guardWonmu = () => {
  if (!canManageNotice.value) {
    alert("공지사항 등록/수정/삭제는 원무팀만 가능합니다.");
    return false;
  }
  return true;
};

// 정렬 로직
const sortedList = computed(() => {
  return [...noticeList.value].sort((a, b) => {
    if (a.topFix !== b.topFix) return Number(b.topFix) - Number(a.topFix);
    return sortOrder.value === 'desc' ? b.noticeId - a.noticeId : a.noticeId - b.noticeId;
  });
});

// 페이징 로직
const paginatedList = computed(() => {
  let filtered = sortedList.value;
  if (keyword.value) filtered = filtered.filter(item => item.title.includes(keyword.value));
  const start = (currentPage.value - 1) * itemsPerPage;
  return filtered.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  const listLen = keyword.value
    ? sortedList.value.filter(item => item.title.includes(keyword.value)).length
    : sortedList.value.length;
  return Math.ceil(listLen / itemsPerPage) || 1;
});

const visiblePages = computed(() => {
  const currentGroup = Math.ceil(currentPage.value / maxPageBtn);
  const start = (currentGroup - 1) * maxPageBtn + 1;
  let end = start + maxPageBtn - 1;
  if (end > totalPages.value) end = totalPages.value;
  const pages = [];
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const formatDate = (dateStr) => dateStr ? String(dateStr).substring(0, 10) : '';

const isNew = (dateStr) => {
  if (!dateStr) return false;
  return (new Date() - new Date(dateStr)) / (1000 * 60 * 60 * 24) < 7;
};

const goDetail = async (item) => {
  try {
    const res = await getNoticeDetailReq(item.noticeId);
    selectedItem.value = res.data || item;
  } catch (e) {
    selectedItem.value = item;
  }
  mode.value = 'detail';
  window.scrollTo(0, 0);
};

const goList = () => {
  mode.value = 'list';
  selectedItem.value = {};
  getNoticeList();
};

const goWrite = () => {
  if (!guardWonmu()) return;
  writeForm.value = { title: '', content: '', topFix: false };
  mode.value = 'write';
};

const goEdit = (item) => {
  if (!guardWonmu()) return;
  writeForm.value = { ...item };
  mode.value = 'edit';
};

// 등록/수정 로직
const submitNotice = async () => {
  if (!guardWonmu()) return;

  if (!writeForm.value.title || !writeForm.value.content) {
    alert("제목과 내용을 입력해 주세요."); return;
  }

  try {
    const memIdRaw = loginInfo.value.memId ?? loginInfo.value.mem_id;
    const memId = Number(memIdRaw);
    if (!memId || Number.isNaN(memId)) {
      alert("로그인 정보(memId)가 없습니다."); return;
    }

    const payload = {
      ...writeForm.value,
      memId,
      del: 0
    };

    let res;
    if (mode.value === 'write') res = await addNoticeReq(payload);
    else res = await editNoticeReq(payload);

    if (res.data === 'success' || res.data === true) {
      alert(mode.value === 'write' ? "등록되었습니다." : "수정되었습니다.");
      goList();
    } else {
      alert("처리 실패");
    }
  } catch (e) {
    console.error(e);
    alert("오류가 발생했습니다.");
  }
};

const deleteNotice = async (id) => {
  if (!guardWonmu()) return;

  if (!confirm("정말 삭제하시겠습니까?")) return;
  try {
    const res = await delNoticeReq(id);
    if (res.data === 'success' || res.data === true) {
      alert("삭제되었습니다.");
      goList();
    } else { alert("삭제 실패"); }
  } catch (e) { alert("오류 발생"); }
};

const getNoticeList = async () => {
  try {
    const params = keyword.value ? { keyword: keyword.value } : null;
    const res = await getNoticesReq(params);
    noticeList.value = res.data || [];
  } catch (err) {
    noticeList.value = [];
  }
};

const handlePopState = () => {
  if (mode.value !== 'list') {
    mode.value = 'list';
    selectedItem.value = {};
  }
};

/*onMounted(() => {
  const raw = sessionStorage.getItem('loginId');
  if (raw) {
    try { loginInfo.value = JSON.parse(raw); }
    catch (e) { loginInfo.value = { id: raw }; }
  }
  getNoticeList();
  window.addEventListener('popstate', handlePopState);
});*/

onMounted(async () => {
  const raw = sessionStorage.getItem('loginId');
  if (raw) {
    try { loginInfo.value = JSON.parse(raw); }
    catch (e) { loginInfo.value = { id: raw }; }
  }

  // [추가] 관리자라면 백엔드에 부서 정보 확인 요청
  if (isAdmin.value) {
    try {
        const res = await getAdminInfoReq(); // /admin/my-info 호출
        if (res && res.data && res.data.isWonmu) {
            isWonmuState.value = true;
        }
    } catch (e) {
        console.error("관리자 정보 확인 실패", e);
    }
  }

  getNoticeList();
  window.addEventListener('popstate', handlePopState);
});

onUnmounted(() => {
  window.removeEventListener('popstate', handlePopState);
});
</script>

<style scoped>
/* 표 깨짐 방지: table-layout 제거 */
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
    font-family: 'Noto Sans KR', sans-serif;
}

.page-header {
    margin-bottom: 40px;
    text-align: center;
}

.page-header h2 {
    font-size: 32px;
    font-weight: 700;
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
    gap: 0;
    margin-top: 40px;
}

.page-btn {
    width: 40px;
    height: 40px;
    border: 1px solid #ddd;
    border-right: none;
    background: #fff;
    color: #666;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: 0.2s;
}

.page-btn:last-child {
    border-right: 1px solid #ddd;
}

.page-btn:hover:not(:disabled) {
    background: #f5f5f5;
    color: #333;
}

.page-btn:disabled {
    color: #ccc;
    cursor: default;
    background: #fafafa;
}

.page-btn.active {
    background: #0171e9;
    color: #fff;
    border-color: #0171e9;
    z-index: 1;
}

.detail-wrap {
    border-top: 2px solid #333;
    border-bottom: 1px solid #ddd;
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

.dh-info strong {
    color: #333;
    margin-right: 5px;
}

.bar {
    margin: 0 15px;
    color: #ddd;
}

.detail-content {
    padding: 40px 30px;
    min-height: 300px;
    color: #444;
    line-height: 1.6;
    font-size: 15px;
    border-bottom: 1px solid #eee;
    white-space: pre-wrap;
}

.btn-group {
    margin-top: 30px;
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
    transition: 0.2s;
}

.btn-list:hover {
    background: #f9f9f9;
}

.admin-btns {
    display: flex;
    gap: 5px;
}

.btn-mod,
.btn-del {
    padding: 12px 25px;
    border: none;
    color: #fff;
    font-weight: 600;
    cursor: pointer;
}

.btn-mod {
    background: #555;
}

.btn-del {
    background: #333;
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

.form-row:last-child {
    border-bottom: none;
}

.form-row label {
    width: 120px;
    background: #f9f9f9;
    padding: 15px 20px;
    font-weight: 600;
    color: #333;
    display: flex;
    align-items: center;
}

.form-row input[type="text"],
.form-row textarea {
    flex: 1;
    border: none;
    padding: 15px;
    font-size: 14px;
    outline: none;
}

.form-row textarea {
    height: 300px;
    resize: none;
}

.form-row input[type="checkbox"] {
    margin: 15px;
}

.chk-label {
    width: auto !important;
    background: none !important;
    padding: 0 !important;
    cursor: pointer;
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
    line-height: 1;
    margin-right: 5px;
}
</style>
