<template>
    <div class="voc-container">
        <div class="page-header">
            <h2>고객의 소리 (VOC)</h2>
            <p>병원 이용 중 불편하셨던 점이나 칭찬하고 싶은 점을 남겨주세요.</p>
        </div>

        <div v-if="mode === 'list'" class="list-controls">
            <div class="status-tabs">
                <button v-for="tab in currentTabs" :key="tab.id"
                    :class="['tab-btn', { active: filterStatus === tab.id }]" @click="changeFilter(tab.id)">
                    {{ tab.label }}
                </button>
            </div>

            <div class="right-area">
                <button v-if="!isAdmin" class="btn-write" @click="openWriteModal">문의하기</button>
            </div>
        </div>

        <div v-if="mode === 'list'" class="voc-list">
            <div v-if="vocList.length === 0" class="no-data">
                해당하는 게시글이 없습니다.
            </div>

            <div v-for="item in vocList" :key="item.vocId" class="voc-item" @click="goDetail(item)">
                <div class="voc-top">
                    <span :class="['badge', getStatusClass(item)]">
                        {{ getStatusText(item) }}
                    </span>
                    <span class="voc-date">{{ formatDate(item.writeDate) }}</span>
                </div>
                <div class="voc-title">
                    {{ item.title }}
                </div>
                <div class="voc-info">
                    <span>작성자: {{ maskName(item.writerName) }}</span>
                    <span v-if="isWonmu && item.del === 1" class="text-danger">
                        (삭제 예정)
                    </span>
                </div>
            </div>
        </div>

        <div v-else-if="mode === 'detail'" class="detail-wrap">
            <div class="detail-header">
                <div class="dh-top">
                    <span :class="['badge', getStatusClass(selectedItem)]">
                        {{ getStatusText(selectedItem) }}
                    </span>
                    <span class="dh-date">{{ formatDate(selectedItem.writeDate) }}</span>
                </div>
                <div class="dh-title">{{ selectedItem.title }}</div>
                <div class="dh-writer">작성자: {{ maskName(selectedItem.writerName) }}</div>
            </div>

            <div class="detail-content">
                <pre>{{ selectedItem.content }}</pre>
            </div>

            <div v-if="isAdmin && selectedItem.del === 0" class="admin-reply-area">
                <h4>병원 측 답변</h4>

                <div v-if="selectedItem.answerStatus" class="reply-box">
                    <div class="reply-meta">
                        <strong>관리자 답변</strong>
                        <span>{{ formatDate(selectedItem.answerWriteDate) }}</span>
                    </div>
                    <pre class="reply-text">{{ selectedItem.answerContent }}</pre>
                </div>

                <!-- 원무팀만 답변 입력/등록 -->
                <div v-else-if="canReply" class="reply-input-box">
                    <textarea v-model="replyText" placeholder="고객님께 드릴 답변을 작성해주세요."></textarea>
                    <button @click="submitReply">답변 등록</button>
                </div>

                <div v-else class="no-auth-msg">
                    ※ 답변 등록 권한이 없습니다. (원무팀 전용)
                </div>
            </div>

            <div v-if="!isAdmin && selectedItem.answerStatus" class="user-reply-view">
                <div class="reply-header">🏥 병원 답변 ({{ formatDate(selectedItem.answerWriteDate) }})</div>
                <pre>{{ selectedItem.answerContent }}</pre>
            </div>

            <div class="btn-group">
                <button class="btn-list" @click="mode = 'list'">목록</button>

                <div v-if="!isAdmin && !selectedItem.answerStatus" class="action-btns">
                    <button class="btn-mod" @click="openEditModal(selectedItem)">수정</button>
                    <button class="btn-del" @click="deleteVocUser(selectedItem.vocId)">삭제</button>
                </div>

                <!-- 원무팀만 관리자 삭제/복구 -->
                <div v-if="canManageDeleted" class="action-btns">
                    <button v-if="selectedItem.del === 0" class="btn-del" @click="deleteVocAdmin(selectedItem.vocId)">
                        삭제 (보관함)
                    </button>

                    <template v-else>
                        <button class="btn-restore" @click="restoreVoc(selectedItem.vocId)">복구</button>
                    </template>
                </div>
            </div>
        </div>

        <div v-if="showModal" class="modal-overlay">
            <div class="modal-content">
                <h3>{{ isEditing ? '문의 수정' : '문의하기' }}</h3>
                <input type="text" v-model="writeForm.title" class="modal-input" placeholder="제목">
                <textarea v-model="writeForm.content" class="modal-textarea" placeholder="내용"></textarea>
                <div class="modal-footer">
                    <button class="btn-confirm" @click="submitWrite">{{ isEditing ? '수정' : '등록' }}</button>
                    <button class="btn-cancel" @click="showModal = false">취소</button>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
    getMyVocsReq, addVocReq, editVocReq, delVocReq, // 환자용
    getAdminVocListReq, getAdminVocDetailReq, addVocReplyReq, delVocByAdminReq, restoreVocReq // 관리자용
, getAdminInfoReq } from '@/api/customer';

const mode = ref('list');
const filterStatus = ref('all');
const replyText = ref('');
const showModal = ref(false);
const isEditing = ref(false);

const loginInfo = ref({});
const vocList = ref([]);
const selectedItem = ref({});
const writeForm = ref({ vocId: '', title: '', content: '' });

// 추가추가
const isWonmuState = ref(false);

// ADMIN 로그인 여부
const isAdmin = computed(() => {
    const info = loginInfo.value || {};
    return String(info.loginType || info.role || '').toUpperCase() === 'ADMIN';
});

// 원무팀 여부 (부서명 필드가 있어야 true)
/*const isWonmu = computed(() => {
    if (!isAdmin.value) return false;

    const info = loginInfo.value || {};
    // 여기 deptName이 비어있으면 프론트에서 원무팀 판별이 절대 안 됨
    const dept = String(
        info.deptName ?? info.dept_name ?? info.adminDeptName ?? info.med_dept_name ?? ''
    ).trim();

    // 디버깅 로그 (원하면 나중에 지워도 됨)
    console.log("loginInfo:", info);
    console.log("dept:", dept);

    return dept.includes('원무');
});*/
// 추가추가
const isWonmu = computed(() => {
    if (!isAdmin.value) return false;

    // 1. 백엔드 API를 통해 확인된 값이 있으면 최우선 적용
    if (isWonmuState.value) return true;

    // 2. (보조) 세션 스토리지 정보 확인 (기존 로직 유지)
    const info = loginInfo.value || {};
    const dept = String(
        info.deptName ?? info.dept_name ?? info.adminDeptName ?? info.med_dept_name ?? ''
    ).trim();
    
    return dept.includes('원무');
});

// 답변 권한: 원무팀 ADMIN만
const canReply = computed(() => isAdmin.value && isWonmu.value);

// 삭제/복구/휴지통 권한: 원무팀 ADMIN만
const canManageDeleted = computed(() => isAdmin.value && isWonmu.value);

// 공통 가드
const guardWonmu = (msg = "원무팀만 가능한 기능입니다.") => {
    if (!isAdmin.value) { alert("관리자 로그인이 필요합니다."); return false; }
    if (!isWonmu.value) { alert(msg); return false; }
    return true;
};

// 탭 메뉴 설정
const currentTabs = computed(() => {
    const tabs = [
        { id: 'all', label: '전체' },
        { id: 'unanswered', label: '미답변' },
        { id: 'answered', label: '답변완료' }
    ];
    if (isWonmu.value) {
        tabs.push({ id: 'deleted', label: '휴지통(30일)' });
    }
    return tabs;
});

// 리스트 조회
const fetchList = async () => {
    try {
        let res;
        if (isAdmin.value) {
            if (filterStatus.value === 'deleted' && !isWonmu.value) {
                alert("접근 권한이 없습니다.");
                filterStatus.value = 'all';
                return;
            }
            res = await getAdminVocListReq(filterStatus.value);
        } else {
            res = await getMyVocsReq(filterStatus.value);
        }
        vocList.value = res.data || [];
    } catch (e) {
        console.error(e);
        vocList.value = [];
    }
};

const changeFilter = (status) => {
    filterStatus.value = status;
    fetchList();
};

// 상세 조회
const goDetail = async (item) => {
    try {
        let detailData = item;
        if (isAdmin.value) {
            const res = await getAdminVocDetailReq(item.vocId);
            if (!res.data) { alert("권한이 없거나 삭제된 글입니다."); return; }
            detailData = res.data;
        }
        selectedItem.value = detailData;
        mode.value = 'detail';
        replyText.value = '';
    } catch (e) { alert("오류 발생"); }
};

// =======================
// [환자] 글쓰기/수정/삭제
// =======================
const openWriteModal = () => {
    isEditing.value = false;
    writeForm.value = { title: '', content: '' };
    showModal.value = true;
};

const openEditModal = (item) => {
    isEditing.value = true;
    writeForm.value = { vocId: item.vocId, title: item.title, content: item.content };
    showModal.value = true;
};

const submitWrite = async () => {
    if (!writeForm.value.title || !writeForm.value.content) {
        alert("제목과 내용을 입력해주세요."); return;
    }

    try {
        const currentUserMemId = loginInfo.value.memId || loginInfo.value.id || 0;

        const payload = {
            title: writeForm.value.title,
            content: writeForm.value.content,
            memId: Number(currentUserMemId),
            del: 0,
            answerStatus: false,
            secret: 0
        };

        console.log("전송 데이터:", payload);

        let res;
        if (isEditing.value) {
            payload.vocId = writeForm.value.vocId;
            res = await editVocReq(payload);
        } else {
            res = await addVocReq(payload);
        }

        if (res.data === 'success' || res.data === true) {
            alert(isEditing.value ? "수정되었습니다." : "등록되었습니다.");
            showModal.value = false;
            fetchList();
        } else {
            alert("처리 실패");
        }
    } catch (e) {
        console.error("에러 발생:", e);
        alert("오류가 발생했습니다.");
    }
};

const deleteVocUser = async (id) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    try {
        const res = await delVocReq(id);
        if (res.data === 'success' || res.data === true) {
            alert("삭제되었습니다.");
            mode.value = 'list';
            fetchList();
        } else { alert("삭제 실패"); }
    } catch (e) { alert("오류 발생"); }
};

// =======================
// [원무팀] 답변/삭제/복구
// =======================
const submitReply = async () => {
    if (!guardWonmu("답변 등록은 원무팀만 가능합니다.")) return;

    if (!replyText.value) { alert("답변 내용을 입력하세요"); return; }
    try {
        const memIdRaw = loginInfo.value.memId ?? loginInfo.value.mem_id;
        const memId = Number(memIdRaw);
        if (!memId || Number.isNaN(memId)) { alert("로그인 정보(memId)가 없습니다."); return; }

        const payload = {
            vocId: selectedItem.value.vocId,
            answerTitle: '답변입니다',
            answerContent: replyText.value,
            memId
        };

        const res = await addVocReplyReq(payload);
        if (res.data === 'success' || res.data === true) {
            alert("답변이 등록되었습니다.");
            selectedItem.value.answerStatus = true;
            selectedItem.value.answerContent = replyText.value;
            selectedItem.value.answerWriteDate = new Date();
        } else { alert("등록 실패"); }
    } catch (e) { alert("오류 발생"); }
};

const deleteVocAdmin = async (id) => {
    if (!guardWonmu("삭제(보관함 이동)는 원무팀만 가능합니다.")) return;

    if (!confirm("삭제하시겠습니까? (휴지통으로 이동되며 30일 후 영구 삭제)")) return;
    try {
        const res = await delVocByAdminReq(id);
        if (res.data === 'success' || res.data === true) {
            alert("휴지통으로 이동되었습니다.");
            mode.value = 'list';
            fetchList();
        } else { alert("삭제 실패"); }
    } catch (e) { alert("오류 발생"); }
};

const restoreVoc = async (id) => {
    if (!guardWonmu("복구는 원무팀만 가능합니다.")) return;

    if (!confirm("글을 복구하시겠습니까?")) return;
    try {
        const res = await restoreVocReq(id);
        if (res.data === 'success' || res.data === true) {
            alert("복구되었습니다.");
            mode.value = 'list';
            fetchList();
        } else { alert("복구 실패"); }
    } catch (e) { alert("오류 발생"); }
};

// 유틸리티
const getStatusText = (item) => {
    if (item.del === 1) return '삭제됨';
    if (item.answerStatus) return '답변완료';
    return '대기중';
};

const getStatusClass = (item) => {
    if (item.del === 1) return 'badge-red';
    if (item.answerStatus) return 'badge-blue';
    return 'badge-gray';
};

const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return String(dateStr).substring(0, 10);
};

const maskName = (name) => {
    if (!name) return '';
    if (isAdmin.value) return name;
    if (name.length <= 2) return name[0] + '*';
    return name[0] + '*' + name[name.length - 1];
};

/*onMounted(() => {
    const raw = sessionStorage.getItem('loginId');
    if (raw) {
        try { loginInfo.value = JSON.parse(raw); }
        catch (e) { loginInfo.value = { id: raw }; }
    }
    fetchList();
});*/
// 추가추가
onMounted(async () => {
    // 1. 세션 스토리지 로드
    const raw = sessionStorage.getItem('loginId');
    if (raw) {
        try { loginInfo.value = JSON.parse(raw); }
        catch (e) { loginInfo.value = { id: raw }; }
    }

    // 2. 관리자라면 백엔드에 정확한 부서 정보(원무과 여부) 확인 요청
    if (isAdmin.value) {
        try {
            const res = await getAdminInfoReq(); // /admin/my-info 호출
            if (res && res.data) {
                // 백엔드가 "isWonmu: true"를 주면 상태 업데이트
                if (res.data.isWonmu) {
                    isWonmuState.value = true;
                    
                    // 권한이 확인되었으므로, 탭 메뉴 등을 갱신하기 위해 강제 반응성 트리거가 필요할 수 있음
                    // 하지만 computed가 isWonmuState를 바라보므로 자동 갱신됨
                }
            }
        } catch (e) {
            console.error("관리자 정보 확인 실패:", e);
        }
    }

    // 3. 목록 조회
    fetchList();
});
</script>

<style scoped>

.voc-container {
    max-width: 900px;
    margin: 80px auto;
    padding: 0 20px;
    font-family: 'Noto Sans KR', sans-serif;
}

.page-header {
    text-align: center;
    margin-bottom: 40px;
}

.page-header h2 {
    font-size: 32px;
    font-weight: 700;
    color: #333;
    margin-bottom: 10px;
}

.page-header p {
    color: #666;
}

.list-controls {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 20px;
    border-bottom: 2px solid #333;
    padding-bottom: 15px;
}

.status-tabs {
    display: flex;
    gap: 5px;
}

.tab-btn {
    padding: 8px 16px;
    border-radius: 20px;
    border: 1px solid #ddd;
    background: #fff;
    cursor: pointer;
    font-size: 14px;
    color: #555;
    transition: 0.2s;
}

.tab-btn.active {
    background: #333;
    color: #fff;
    border-color: #333;
    font-weight: 600;
}

.right-area {
    display: flex;
    gap: 10px;
}

.btn-write {
    background: #0171e9;
    color: #fff;
    border: none;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 600;
}

.voc-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.voc-item {
    background: #fff;
    border: 1px solid #eee;
    padding: 20px 25px;
    border-radius: 8px;
    cursor: pointer;
    transition: 0.2s;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
}

.voc-item:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
    border-color: #0171e9;
}

.voc-top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 13px;
    color: #888;
}

.voc-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
}

.voc-info {
    font-size: 13px;
    color: #666;
    display: flex;
    gap: 10px;
}

.text-danger {
    color: #dc3545;
    font-weight: bold;
}

.badge {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 11px;
    font-weight: 600;
}

.badge-gray {
    background: #eee;
    color: #555;
}

.badge-blue {
    background: #e3f2fd;
    color: #0171e9;
}

.badge-red {
    background: #ffebee;
    color: #d32f2f;
}

.detail-wrap {
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 40px;
    background: #fff;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.03);
}

.detail-header {
    border-bottom: 1px solid #eee;
    padding-bottom: 20px;
    margin-bottom: 30px;
}

.dh-top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
}

.dh-title {
    font-size: 24px;
    font-weight: 700;
    color: #333;
    margin-bottom: 10px;
}

.dh-writer {
    font-size: 14px;
    color: #666;
    text-align: right;
}

.detail-content {
    font-size: 16px;
    line-height: 1.6;
    color: #444;
    min-height: 150px;
    white-space: pre-wrap;
}

.detail-content pre,
.reply-text {
    font-family: 'Noto Sans KR', sans-serif;
    white-space: pre-wrap;
    margin: 0;
}

.admin-reply-area {
    margin-top: 40px;
    background: #f8f9fa;
    padding: 25px;
    border-radius: 8px;
    border: 1px dashed #ccc;
}

.reply-input-box textarea {
    width: 100%;
    height: 100px;
    padding: 10px;
    border: 1px solid #ddd;
    margin-bottom: 10px;
    resize: none;
}

.reply-input-box button {
    background: #333;
    color: #fff;
    border: none;
    padding: 8px 20px;
    cursor: pointer;
    float: right;
}

.user-reply-view {
    margin-top: 40px;
    background: #f0f7ff;
    padding: 25px;
    border-radius: 8px;
    border-left: 5px solid #0171e9;
}

.reply-header {
    font-weight: 700;
    color: #0171e9;
    margin-bottom: 10px;
}

.reply-meta {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 13px;
    color: #666;
    border-bottom: 1px solid #ddd;
    padding-bottom: 5px;
}

.no-auth-msg {
    color: #888;
    text-align: center;
    padding: 20px;
}

.btn-group {
    margin-top: 40px;
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #eee;
    padding-top: 20px;
}

.btn-list {
    padding: 10px 30px;
    background: #fff;
    border: 1px solid #ccc;
    cursor: pointer;
}

.action-btns {
    display: flex;
    gap: 8px;
}

.btn-mod {
    background: #555;
    color: #fff;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
}

.btn-del {
    background: #333;
    color: #fff;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
}

.btn-restore {
    background: #28a745;
    color: #fff;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #fff;
    padding: 30px;
    width: 500px;
    border-radius: 8px;
}

.modal-input,
.modal-textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    margin-bottom: 10px;
}

.modal-textarea {
    height: 150px;
    resize: none;
}

.modal-footer {
    text-align: right;
    margin-top: 10px;
}

.btn-confirm {
    background: #0171e9;
    color: #fff;
    padding: 8px 20px;
    border: none;
    margin-right: 5px;
    cursor: pointer;
}

.btn-cancel {
    background: #eee;
    padding: 8px 20px;
    border: none;
    cursor: pointer;
}

.no-data {
    text-align: center;
    padding: 50px;
    color: #999;
}
</style>
