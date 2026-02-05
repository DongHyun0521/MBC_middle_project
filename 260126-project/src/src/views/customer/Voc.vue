<template>
  <div class="voc-container">
    <div class="page-header">
      <h2>고객의 소리</h2>
      <p>병원 이용 중 불편하셨던 점이나 칭찬하고 싶은 점을 편하게 남겨주세요.</p>
    </div>

    <div class="list-controls">
      <div class="status-tabs">
        <button v-for="tab in currentTabs" :key="tab.id"
          :class="['tab-btn', { active: filterStatus === tab.id }]" @click="changeFilter(tab.id)">
          {{ tab.label }}
        </button>
      </div>
      <div class="right-area">
        <button v-if="!isAdmin" class="btn-write" @click="openWriteModal">문의 작성</button>
      </div>
    </div>

    <div class="voc-list">
      <div v-if="vocList.length === 0" class="no-data">등록된 게시글이 없습니다.</div>

      <div v-for="item in vocList" :key="item.vocId" class="voc-item"
        :class="{ 'is-open': openItemId === item.vocId }">

        <div class="voc-header" @click="toggleItem(item)">
          <div class="vh-left">
            <span :class="['status-badge', getStatusClass(item)]">{{ getStatusText(item) }}</span>
            <span class="vh-title">{{ item.title }}</span>
            <span v-if="item.secret === 1" class="lock-icon">🔒</span>
          </div>

          <div class="vh-right">
            <span class="vh-writer">{{ getWriterName(item) }}</span>
            <span class="vh-date">{{ formatDate(item.writeDate || item.write_date) }}</span>
            <span class="vh-arrow">{{ openItemId === item.vocId ? '▲' : '▼' }}</span>
          </div>
        </div>

        <transition name="slide-fade">
          <div v-if="openItemId === item.vocId" class="voc-body">

            <div class="q-section">
              <div class="icon-box q-icon">Q</div>
              <div class="content-box">
                <pre class="content-text">{{ item.content }}</pre>

                <div v-if="!isAdmin && !item.answerStatus" class="action-row">
                  <button @click.stop="openEditModal(item)">수정</button>
                  <span class="divider"></span>
                  <button @click.stop="deleteVocUser(item.vocId)" class="btn-del">삭제</button>
                </div>

                <div v-if="canManageDeleted" class="action-row">
                  <button v-if="item.del === 0" @click.stop="deleteVocAdmin(item.vocId)"
                    class="btn-del">보관함 이동</button>
                  <button v-else @click.stop="restoreVoc(item.vocId)" class="btn-restore">복구</button>
                </div>
              </div>
            </div>

            <div class="a-section">
              <div class="icon-box a-icon" :class="{ 'waiting': !item.answerStatus }">A</div>

              <div class="answer-wrapper">

                <div v-if="item.answerStatus" class="answer-card">
                  <div class="answer-header">
                    <span class="admin-badge">병원 관리자</span>
                    <span class="answer-date">{{ formatDate(item.answerWriteDate || item.answer_write_date) }}</span>
                  </div>

                  <div class="answer-body">
                    <pre
                      v-if="editingReplyId !== item.vocId">{{ item.answerContent || item.answer_content }}</pre>

                    <div v-else class="edit-mode-box">
                      <textarea v-model="replyText"></textarea>
                      <div class="edit-btns">
                        <button @click="updateReply(item)" class="save">저장</button>
                        <button @click="cancelEditReply" class="cancel">취소</button>
                      </div>
                    </div>
                  </div>

                  <div v-if="isWonmu && editingReplyId !== item.vocId" class="answer-footer">
                    <button @click="startEditReply(item)">수정</button>
                    <button @click="deleteReply(item)" class="del">삭제</button>
                  </div>
                </div>

                <div v-else-if="canReply && item.del === 0" class="reply-input-box">
                  <p class="input-guide">답변을 작성해주세요.</p>
                  <textarea v-model="replyText" placeholder="내용 입력..."></textarea>
                  <div class="input-actions">
                    <button @click="submitReply(item)" class="btn-submit">답변 등록</button>
                  </div>
                </div>

                <div v-else class="waiting-card">
                  <p v-if="item.del === 1">삭제된 게시글입니다.</p>
                  <p v-else>⏳ <strong>답변 대기중</strong>
                    <br>담당자가 내용을 확인하고 있습니다. 조금만 기다려주세요.</p>
                </div>

              </div>
            </div>

          </div>
        </transition>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3>{{ isEditing ? '문의 수정' : '새 문의 작성' }}</h3>
          <button class="close-icon" @click="showModal = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="writeForm.title" class="modal-input" placeholder="제목을 입력하세요">
          <textarea v-model="writeForm.content" class="modal-textarea"
            placeholder="문의하실 내용을 입력하세요"></textarea>
        </div>
        <div class="modal-foot">
          <button class="btn-gray" @click="showModal = false">취소</button>
          <button class="btn-blue" @click="submitWrite">{{ isEditing ? '수정완료' : '등록하기' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getMyVocsReq, addVocReq, editVocReq, delVocReq,
  getAdminVocListReq,
  addVocReplyReq, editVocReplyReq, delVocReplyReq,
  delVocByAdminReq, restoreVocReq, getAdminInfoReq
} from '@/api/customer';

const filterStatus = ref('all');
const openItemId = ref(null);
const editingReplyId = ref(null);
const replyText = ref('');
const showModal = ref(false);
const isEditing = ref(false);

const loginInfo = ref({});
const vocList = ref([]);
const writeForm = ref({ vocId: '', title: '', content: '' });
const isWonmuState = ref(false);

const isAdmin = computed(() => String(loginInfo.value.loginType || loginInfo.value.role || '').toUpperCase() === 'ADMIN');
const isWonmu = computed(() => {
  if (!isAdmin.value) return false;
  if (isWonmuState.value) return true;
  const info = loginInfo.value || {};
  const dept = String(info.deptName ?? info.dept_name ?? info.adminDeptName ?? '').trim();
  return dept.includes('원무');
});
const canReply = computed(() => isAdmin.value && isWonmu.value);
const canManageDeleted = computed(() => isAdmin.value && isWonmu.value);

const currentTabs = computed(() => {
  const tabs = [{ id: 'all', label: '전체' }, { id: 'unanswered', label: '미답변' }, { id: 'answered', label: '답변완료' }];
  if (isWonmu.value) tabs.push({ id: 'deleted', label: '휴지통' });
  return tabs;
});

// 작성자 이름 표시 로직
const getWriterName = (item) => {
    // 1. DB에서 넘어온 이름이 있으면 우선 사용
    if (item.writerName || item.writer_name || item.name) {
        return item.writerName || item.writer_name || item.name;
    }
    // 2. 없는데 관리자가 아니다(내 글 목록이다)? -> 로그인한 내 이름 사용
    if (!isAdmin.value && loginInfo.value.name) {
        return loginInfo.value.name;
    }
    // 3. 다 없으면 익명
    return '익명';
};

const fetchList = async () => {
  try {
    let res;
    if (isAdmin.value) {
      if (filterStatus.value === 'deleted' && !isWonmu.value) { filterStatus.value = 'all'; return; }
      res = await getAdminVocListReq(filterStatus.value);
    } else {
      res = await getMyVocsReq(filterStatus.value);
    }
    vocList.value = res.data || [];
  } catch (e) { vocList.value = []; }
};

const changeFilter = (id) => { filterStatus.value = id; openItemId.value = null; fetchList(); };

const toggleItem = (item) => {
  if (openItemId.value === item.vocId) openItemId.value = null;
  else { openItemId.value = item.vocId; replyText.value = ''; editingReplyId.value = null; }
};

const openWriteModal = () => { isEditing.value = false; writeForm.value = { title: '', content: '' }; showModal.value = true; };
const openEditModal = (item) => { isEditing.value = true; writeForm.value = { ...item }; showModal.value = true; };
const submitWrite = async () => {
  if (!writeForm.value.title) return alert("제목 입력");
  try {
    const memId = Number(loginInfo.value.memId || loginInfo.value.id || 0);
    const payload = { ...writeForm.value, memId, del: 0, answerStatus: false };
    let res;
    if (isEditing.value) res = await editVocReq(payload);
    else res = await addVocReq(payload);
    if (res.data === 'success' || res.data === true) { alert("완료"); showModal.value = false; fetchList(); }
  } catch (e) { }
};
const deleteVocUser = async (id) => { if (confirm("삭제?")) { await delVocReq(id); alert("삭제됨"); fetchList(); } };

const submitReply = async (item) => {
  if (!replyText.value) return alert("내용 입력");
  const memId = Number(loginInfo.value.memId || loginInfo.value.id);
  try {
    const payload = { vocId: Number(item.vocId), answerContent: replyText.value, answerStatus: true, del: 0, memId };
    await addVocReplyReq(payload);
    alert("등록 완료"); fetchList();
  } catch (e) { alert("등록 실패"); }
};

const startEditReply = (item) => { replyText.value = item.answerContent || item.answer_content; editingReplyId.value = item.vocId; };
const cancelEditReply = () => { editingReplyId.value = null; replyText.value = ''; };
const updateReply = async (item) => {
  if (!replyText.value) return alert("내용 입력");
  try {
    const payload = { vocId: Number(item.vocId), answerContent: replyText.value, answerStatus: true, memId: Number(loginInfo.value.memId || loginInfo.value.id) };
    await editVocReplyReq(payload);
    alert("수정 완료"); fetchList(); editingReplyId.value = null;
  } catch (e) { alert("오류"); }
};
const deleteReply = async (item) => {
  if (!confirm("답변 삭제?")) return;
  try { await delVocReplyReq(item.vocId); alert("삭제 완료"); fetchList(); } catch (e) { alert("오류"); }
};

const deleteVocAdmin = async (id) => { if (confirm("보관함 삭제?")) { await delVocByAdminReq(id); fetchList(); } };
const restoreVoc = async (id) => { if (confirm("복구?")) { await restoreVocReq(id); fetchList(); } };

const getStatusText = (item) => item.del === 1 ? '삭제됨' : (item.answerStatus ? '답변완료' : '대기중');
const getStatusClass = (item) => item.del === 1 ? 'st-red' : (item.answerStatus ? 'st-blue' : 'st-gray');
const formatDate = (d) => d ? String(d).substring(0, 10) : '';

onMounted(async () => {
  const raw = sessionStorage.getItem('loginId');
  if (raw) loginInfo.value = JSON.parse(raw);
  if (isAdmin.value) {
    try { const res = await getAdminInfoReq(); if (res?.data?.isWonmu) isWonmuState.value = true; } catch (e) { }
  }
  fetchList();
});
</script>

<style scoped>
.voc-container {
  max-width: 900px;
  margin: 60px auto;
  padding: 0 20px;
  font-family: 'Noto Sans KR', sans-serif;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 800;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #888;
  font-size: 14px;
}

/* 탭 & 버튼 */
.list-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-tabs {
  display: flex;
  gap: 5px;
  background: #f8f9fa;
  padding: 5px;
  /* border-radius: 20px; */
}

.tab-btn {
  padding: 8px 18px;
  /* border-radius: 20px; */
  border: none;
  background: transparent;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  transition: 0.2s;
}

.tab-btn.active {
  background: #fff;
  color: #0171e9;
  font-weight: 800;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.btn-write {
  background: #0171e9;
  color: #fff;
  border: none;
  padding: 10px 24px;
  /* border-radius: 25px; */
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: 0.2s;
  box-shadow: 0 4px 12px rgba(1, 113, 233, 0.2);
}

.btn-write:hover {
  background: #015bbd;
  transform: translateY(-2px);
}

/* 리스트 */
.voc-list {
  border-top: 2px solid #333;
}

.voc-item {
  background: #fff;
  border-bottom: 1px solid #eee;
  transition: all 0.2s;
}

.voc-item.is-open {
  background: #fdfdfd;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.01);
}

/* 헤더 */
.voc-header {
  display: flex;
  justify-content: space-between;
  padding: 25px 20px;
  cursor: pointer;
  align-items: center;
  transition: background 0.1s;
}

.voc-header:hover {
  background: #f8f9fa;
}

.vh-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.status-badge {
  padding: 4px 10px;
  /* border-radius: 6px; */
  font-size: 11px;
  font-weight: 700;
  color: #fff;
}

.st-gray {
  background: #adb5bd;
}

.st-blue {
  background: #0171e9;
}

.st-red {
  background: #fa5252;
}

.vh-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
}

.vh-right {
  display: flex;
  gap: 30px;
  font-size: 14px;
  color: #888;
  align-items: center;
}

.vh-writer {
  min-width: 60px;
  text-align: right;
  color: #555;
  font-weight: 500;
}

.vh-date {
  font-family: 'Roboto', sans-serif;
  font-size: 13px;
}

.vh-arrow {
  color: #ccc;
  font-size: 10px;
}

/* 바디 (아코디언) */
.voc-body {
  padding: 30px 40px;
  border-top: 1px solid #f1f3f5;
}

/* 공통 섹션 스타일 */
.q-section,
.a-section {
  display: flex;
  gap: 20px;
}

.q-section {
  margin-bottom: 30px;
}

/* 질문과 답변 사이 간격 */

/* 아이콘 (Q/A) */
.icon-box {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 16px;
  flex-shrink: 0;
}

.q-icon {
  background: #e9ecef;
  color: #495057;
}

.a-icon {
  background: #0171e9;
  color: #fff;
}

.a-icon.waiting {
  background: #dee2e6;
  color: #fff;
}

/* 내용 박스들 */
.content-box {
  flex: 1;
  padding-top: 5px;
}

.content-text {
  white-space: pre-wrap;
  font-size: 15px;
  color: #444;
  line-height: 1.6;
  font-family: inherit;
  margin: 0;
}

.action-row {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  font-size: 13px;
  align-items: center;
}

.action-row button {
  background: none;
  border: none;
  cursor: pointer;
  color: #888;
  font-size: 13px;
  padding: 0;
  text-decoration: underline;
}

.action-row .btn-del {
  color: #fa5252;
}

.action-row .btn-restore {
  color: #40c057;
}

.divider {
  width: 1px;
  height: 12px;
  background: #ddd;
}

/* 답변 카드 (중요) */
.answer-wrapper {
  flex: 1;
}

.answer-card {
  background: #f0f7ff;
  border: 1px solid #e1ecf9;
  /* border-radius: 12px; */
  padding: 25px;
  position: relative;
}

/* 말풍선 꼬리 효과 */
.answer-card::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 12px;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid #f0f7ff;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #dae7f7;
  align-items: center;
}

.admin-badge {
  font-size: 13px;
  font-weight: 800;
  color: #0171e9;
  background: #fff;
  padding: 3px 8px;
  /* border-radius: 4px; */
  border: 1px solid #bcdbfb;
}

.answer-date {
  font-size: 12px;
  color: #888;
}

.answer-body pre {
  white-space: pre-wrap;
  font-size: 15px;
  color: #333;
  line-height: 1.6;
  font-family: inherit;
  margin: 0;
}

.answer-footer {
  text-align: right;
  margin-top: 15px;
}

.answer-footer button {
  background: none;
  border: none;
  color: #666;
  font-size: 12px;
  cursor: pointer;
  text-decoration: underline;
  margin-left: 10px;
}

.answer-footer .del {
  color: #fa5252;
}

/* 답변 수정창 */
.reply-edit-box textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #ccc;
  /* border-radius: 4px; */
  resize: none;
  background: #fff;
}

.edit-btns {
  text-align: right;
  margin-top: 8px;
}

.edit-btns button {
  font-size: 12px;
  padding: 5px 10px;
  /* border-radius: 4px; */
  cursor: pointer;
  margin-left: 5px;
  border: none;
}

.edit-btns .save {
  background: #0171e9;
  color: #fff;
}

.edit-btns .cancel {
  background: #eee;
  color: #333;
}

/* 답변 입력창 (관리자) */
.reply-input-box {
  background: #f8f9fa;
  border: 1px solid #eee;
  /* border-radius: 12px; */
  padding: 20px;
}

.input-guide {
  font-size: 14px;
  font-weight: 700;
  color: #555;
  margin-bottom: 10px;
}

.reply-input-box textarea {
  width: 100%;
  height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  /* border-radius: 6px; */
  resize: none;
  background: #fff;
}

.reply-input-box textarea:focus {
  outline: none;
  border-color: #0171e9;
}

.input-actions {
  text-align: right;
  margin-top: 10px;
}

.btn-submit {
  background: #333;
  color: #fff;
  border: none;
  padding: 8px 20px;
  /* border-radius: 4px; */
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

/* 대기중 카드 */
.waiting-card {
  background: #fff;
  border: 1px dashed #ced4da;
  /* border-radius: 12px; */
  padding: 25px;
  text-align: center;
  color: #868e96;
  font-size: 14px;
}

/* 모달 */
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

.modal-card {
  background: #fff;
  width: 520px;
  /* border-radius: 16px; */
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.modal-head {
  padding: 20px 25px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-head h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 700;
}

.close-icon {
  background: none;
  border: none;
  font-size: 24px;
  color: #aaa;
  cursor: pointer;
}

.modal-body {
  padding: 25px;
}

.modal-input {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  /* border-radius: 8px; */
  margin-bottom: 15px;
  font-size: 14px;
}

.modal-textarea {
  width: 100%;
  height: 200px;
  padding: 14px;
  border: 1px solid #ddd;
  /* border-radius: 8px; */
  resize: none;
  font-size: 14px;
  font-family: inherit;
}

.modal-foot {
  padding: 15px 25px;
  background: #f8f9fa;
  text-align: right;
  border-top: 1px solid #eee;
}

.btn-gray {
  background: #fff;
  border: 1px solid #ddd;
  color: #555;
  padding: 10px 20px;
  /* border-radius: 8px; */
  margin-right: 8px;
  cursor: pointer;
  font-weight: 600;
}

.btn-blue {
  background: #0171e9;
  border: none;
  color: #fff;
  padding: 10px 24px;
  /* border-radius: 8px; */
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 3px 6px rgba(1, 113, 233, 0.2);
}

.no-data {
  text-align: center;
  padding: 80px;
  color: #adb5bd;
  font-size: 15px;
  border-bottom: 1px solid #eee;
}
</style>