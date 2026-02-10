<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>건강이야기</h2>
      <p>서울에스병원 전문의가 들려주는 생생한 건강 정보</p>
    </div>

    <div v-if="mode === 'list'" class="list-wrap">

      <div class="top-controls">
        <div class="video-search">
          <input type="text" v-model="keyword" placeholder="검색어를 입력하세요" @keyup.enter="fetchList">
          <button class="s-btn" @click="fetchList">검색</button>
        </div>
        <button v-if="canManageStory" class="btn-write" @click="goWrite">콘텐츠 등록</button>
      </div>

      <div v-if="storyList.length > 0" class="video-grid">
        <div class="video-card" v-for="item in paginatedList" :key="item.healthStoryId" @click="goDetail(item)">
          <div class="thumb-box">
            <img v-if="item.fileUrl && isImage(item.fileName)" :src="item.fileUrl" class="thumb-img" alt="썸네일">
            <div v-else class="placeholder-thumb" :style="{ backgroundColor: getRandomColor() }">
              <span class="play-icon">▶</span>
            </div>
          </div>
          <div class="video-info">
            <p class="v-title">{{ item.title }}</p>
            <p class="v-author">{{ item.deptName || '홍보팀' }} | {{ item.writerName || '관리자' }}</p>
          </div>
        </div>
      </div>

      <div v-else class="no-data">등록된 콘텐츠가 없습니다.</div>

      <div class="pagination-area" v-if="storyList.length > 0">
        <button class="page-btn prev" :disabled="currentPage === 1" @click="currentPage--">&lt;</button>
        <button v-for="page in visiblePages" :key="page" class="page-btn number"
          :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
        <button class="page-btn next" :disabled="currentPage === totalPages" @click="currentPage++">&gt;</button>
      </div>
    </div>

    <div v-else-if="mode === 'detail'" class="detail-wrap">
      <div class="detail-header">
        <div class="dh-title">{{ selectedItem.title }}</div>
        <div class="dh-info">
          <span><strong>등록일</strong> {{ formatDate(selectedItem.writeDate) }}</span>
          <span class="bar">|</span>
          <span><strong>작성자</strong> {{ selectedItem.writerName || '홍보팀' }}</span>
          <span class="bar">|</span>
          <span><strong>조회수</strong> {{ selectedItem.readCount }}</span>
        </div>
      </div>

      <div class="detail-content">
        <div class="view-text" @click="handleLinkClick" v-html="formatContent(selectedItem.content)"></div>

        <div v-if="selectedItem.fileUrl" class="media-view">
          <img v-if="isImage(selectedItem.fileName)" :src="selectedItem.fileUrl" class="detail-media">
          <video v-else-if="isVideo(selectedItem.fileName)" :src="selectedItem.fileUrl" controls class="detail-media"></video>
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

      <div class="btn-group">
        <button class="btn-list" @click="goList">목록</button>
        <div class="admin-btns" v-if="canManageStory">
          <button class="btn-mod" @click="goEdit(selectedItem)">수정</button>
          <button class="btn-del" @click="deleteItem(selectedItem.healthStoryId)">삭제</button>
        </div>
      </div>
    </div>

    <div v-else-if="mode === 'write' || mode === 'edit'" class="write-wrap">
      <h3 class="write-title">{{ mode === 'write' ? '콘텐츠 등록' : '콘텐츠 수정' }}</h3>
      <div class="write-form">
        <div class="form-row">
          <label>제목</label>
          <div class="input-wrap">
            <input type="text" v-model="writeForm.title" placeholder="제목을 입력하세요" class="full-input">
          </div>
        </div>

        <div class="form-row">
          <label>썸네일/영상</label>
          <div class="input-wrap file-wrap">
            <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*, video/*">
            <div v-if="previewUrl" class="preview-box">
              <img v-if="isImageFile" :src="previewUrl" class="mini-img">
              <video v-else-if="isVideoFile" :src="previewUrl" class="mini-img"></video>
              <span v-else class="file-name-tag">📄 {{ selectedFile.name }}</span>
              <button class="btn-x" @click="removeFile">X</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <label>내용</label>
          <div class="input-wrap">
            <textarea v-model="writeForm.content" placeholder="내용을 입력하세요 (URL 입력 시 링크로 변환됩니다)"
              class="full-textarea"></textarea>
          </div>
        </div>
      </div>
      <div class="btn-group center">
        <button class="btn-cancel" @click="goList">취소</button>
        <button class="btn-save" @click="submitStory">{{ mode === 'write' ? '등록' : '수정' }}</button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// [API] health.js API 임포트
import { 
  getStoriesReq, getStoryDetailReq, 
  addStoryReq, editStoryReq, delStoryReq, 
  getAdminInfoReq 
} from '@/api/health';

const router = useRouter();

// ==========================================
// 1. 상태 변수
// ==========================================
const mode = ref('list');
const keyword = ref('');
const loginInfo = ref({});
const selectedItem = ref({});
const currentPage = ref(1);
const itemsPerPage = 9; 
const maxPageBtn = 5;
const storyList = ref([]);
const writeForm = ref({ healthStoryId: '', title: '', content: '' }); // PK: healthStoryId
const isPrTeamState = ref(false); 

// 파일 관련
const fileInput = ref(null);
const selectedFile = ref(null);
const previewUrl = ref(null);
const isImageFile = ref(false);
const isVideoFile = ref(false);


// 2. 권한 체크 (홍보팀 & 관리자)

// [1] isAdmin: 관리자 여부
const isAdmin = computed(() => String(loginInfo.value.loginType || loginInfo.value.role || '').toUpperCase() === 'ADMIN');

// [2] isPrTeam: 홍보팀 여부
const isPrTeam = computed(() => {
  if (!isAdmin.value) return false;      // 관리자 아니면 탈락
  if (isPrTeamState.value) return true;  // 서버(API)에서 확인된 상태면 통과

  const info = loginInfo.value || {};
  // 부서명 체크 (deptName, dept_name, adminDeptName 싹 다 확인)
  const dept = String(info.deptName ?? info.dept_name ?? info.adminDeptName ?? '').trim();

  return dept.includes('홍보');
});

// [3] 최종 권한 (글쓰기 버튼용)
const canManageStory = computed(() => {
  return isPrTeam.value; // 홍보팀이면(관리자 포함) 권한 있음
});


// ==========================================
// 3. 리스트 데이터 가공
// ==========================================
const paginatedList = computed(() => {
  let list = storyList.value;
  if (keyword.value) {
    list = list.filter(item => item.title.includes(keyword.value));
  }
  // [정렬] 최신순 (healthStoryId 내림차순)
  list.sort((a, b) => b.healthStoryId - a.healthStoryId);

  const start = (currentPage.value - 1) * itemsPerPage;
  return list.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  const len = keyword.value ? storyList.value.filter(i => i.title.includes(keyword.value)).length : storyList.value.length;
  return Math.ceil(len / itemsPerPage) || 1;
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


// ==========================================
// 4. 유틸리티 함수
// ==========================================
const formatDate = (d) => d ? String(d).substring(0, 10) : '';

const getRandomColor = () => {
  const colors = ['#eef2f3', '#f1f1f1', '#e8f0fe', '#f0f4f8', '#fff5f5', '#f6ffed'];
  return colors[Math.floor(Math.random() * colors.length)];
};

// [이미지 경로 처리] 백엔드 포트(8080) 명시
const getImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  // 백엔드 주소 붙이기 (프론트가 5173, 백엔드가 8080일 때 필수)
  return `http://localhost:8080${path}`; 
};

const isImage = (fileName) => {
  if (!fileName) return false;
  const ext = fileName.split('.').pop().toLowerCase();
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext);
};

const isVideo = (fileName) => {
  if (!fileName) return false;
  const ext = fileName.split('.').pop().toLowerCase();
  return ['mp4', 'webm', 'ogg', 'avi', 'mov'].includes(ext);
};

const formatContent = (text) => text ? text.replace(/\n/g, '<br>') : '';

const handleLinkClick = (e) => {
  const target = e.target.closest('a');
  if (target) {
    const href = target.getAttribute('href');
    if (href) {
      e.preventDefault();
      if (href.startsWith('http')) window.open(href, '_blank');
      else router.push(href);
    }
  }
};


// ==========================================
// 5. 기능 로직 (파일, 화면이동)
// ==========================================
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (!file) return;
  selectedFile.value = file;
  
  if (file.type.startsWith('image/')) {
    isImageFile.value = true;
    isVideoFile.value = false;
    previewUrl.value = URL.createObjectURL(file);
  } else if (file.type.startsWith('video/')) {
    isImageFile.value = false;
    isVideoFile.value = true;
    previewUrl.value = URL.createObjectURL(file);
  } else {
    isImageFile.value = false;
    isVideoFile.value = false;
    previewUrl.value = 'doc';
  }
};

const removeFile = () => {
  selectedFile.value = null;
  previewUrl.value = null;
  if (fileInput.value) fileInput.value.value = '';
};

// [상세 보기]
const goDetail = async (item) => { 
  try {
    const res = await getStoryDetailReq(item.healthStoryId);
    selectedItem.value = res.data;
    
    // 이미지/파일명 매핑
    selectedItem.value.fileUrl = getImageUrl(res.data.thumbnailImg);
    if(res.data.thumbnailImg) {
        // 경로에서 순수 파일명만 추출
        selectedItem.value.fileName = res.data.thumbnailImg.split('/').pop().split('_').pop(); 
    }
    
    mode.value = 'detail';
    window.scrollTo(0, 0);
  } catch(e) { console.error(e); }
};

const goList = () => { mode.value = 'list'; selectedItem.value = {}; fetchList(); };

const goWrite = () => { 
  if (!canManageStory.value) return alert("권한이 없습니다.");
  writeForm.value = { title: '', content: '' };
  removeFile();
  mode.value = 'write';
};

const goEdit = (item) => {
  if (!canManageStory.value) return alert("권한이 없습니다.");
  writeForm.value = { ...item };
  removeFile();
  mode.value = 'edit';
};


// ==========================================
// 6. 서버 통신 (CRUD)
// ==========================================
const fetchList = async () => {
  try {
    const res = await getStoriesReq(keyword.value ? { keyword: keyword.value } : null);
    // 리스트 데이터 가공 (이미지 경로 등)
    storyList.value = res.data.map(item => ({
        ...item,
        fileUrl: getImageUrl(item.thumbnailImg),
        fileName: item.thumbnailImg 
    })) || [];
  } catch (e) { storyList.value = []; }
};

const submitStory = async () => {
  if (!canManageStory.value) return;
  if (!writeForm.value.title || !writeForm.value.content) return alert("제목과 내용을 입력하세요.");

  try {
    const formData = new FormData();
    formData.append('title', writeForm.value.title);
    formData.append('content', writeForm.value.content);

    if (mode.value === 'edit') {
      formData.append('healthStoryId', writeForm.value.healthStoryId);
    } else {
      formData.append('del', 0);
    }

    if (selectedFile.value) {
      // [중요] DTO 변수명 'uploadFile'과 일치
      formData.append('uploadFile', selectedFile.value); 
    }

    let res = mode.value === 'write' ? await addStoryReq(formData) : await editStoryReq(formData);
    
    if (res.data === 'success') {
      alert("완료되었습니다.");
      goList();
    } else {
      alert("실패했습니다.");
    }
  } catch (e) { alert("오류 발생"); console.error(e); }
};

const deleteItem = async (id) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;
  try {
    await delStoryReq(id);
    alert("삭제되었습니다.");
    goList();
  } catch (e) { alert("오류 발생"); }
};

onMounted(async () => {
  const raw = sessionStorage.getItem('loginId');
  if (raw) loginInfo.value = JSON.parse(raw);

  // [디버깅] 로그인 정보 확인 1
  console.log(" - loginType:", loginInfo.value.loginType);
  console.log(" - 세션 부서명:", loginInfo.value.deptName, loginInfo.value.adminDeptName);

  if (isAdmin.value) {
    try {
      // 서버에 내 정보 다시 물어보기
      const res = await getAdminInfoReq();
      
      // [디버깅] 서버 응답 확인 2
      console.log(" - 전체 데이터:", res.data);
      
      if (res && res.data) {
        // 서버에서 준 부서명 확인
        const remoteDept = String(res.data.deptName ?? res.data.dept_name ?? '').trim();
        console.log(" - 서버 부서명:", remoteDept);
        
        // 홍보팀이면 상태값 true로 변경
        if (remoteDept.includes('홍보')) {
          console.log("홍보팀 확인 완료");
          isPrTeamState.value = true;
        } else {
          console.log("홍보팀 아님");
        }
      }
    } catch (e) { 
      console.log("관리자 정보 확인 실패:", e);
    }
  } else {
    console.log("관리자(ADMIN) 권한이 아닙니다.");
  }

  fetchList();
});
</script>

<style scoped>
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'pretendard';
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
  font-size: 15px;
}

/* 상단 컨트롤 */
.top-controls {
  display: flex;
  justify-content: center;
  position: relative;
  margin-bottom: 50px;
}

.video-search {
  display: flex;
  background: #f4f4f4;
  border-radius: 4px;
  padding: 5px;
  width: 100%;
  max-width: 600px;
}

.video-search input {
  flex: 1;
  background: none;
  border: none;
  padding: 10px 15px;
  outline: none;
  font-size: 14px;
}

.s-btn {
  background: #043264;
  color: #fff;
  border: none;
  padding: 0 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-write {
  position: absolute;
  right: 0;
  bottom: 0;
  background: #0171e9;
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 4px;
}

/* 그리드 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}

.video-card {
  cursor: pointer;
  transition: 0.3s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.video-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
}

.thumb-box {
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
  border: 1px solid #eee;
  position: relative;
}

.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-thumb {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.play-icon {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
}

.v-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
  height: 2.8em;
  overflow: hidden;
  display: block;
  word-wrap: break-word;
}

.v-author {
  font-size: 14px;
  color: #888;
}

/* 상세 화면 */
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
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
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
  min-height: 300px;
  color: #444;
  line-height: 1.6;
  border-bottom: 1px solid #eee;
}

.media-view {
  margin-top: 30px;
  text-align: center;
}

.detail-media {
  max-width: 100%;
  max-height: 600px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.view-text :deep(a) {
  color: #0171e9;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 600;
}

/* 파일 다운로드 */
.file-attach-area {
  padding: 20px 30px;
  background: #fdfdfd;
  border-bottom: 1px solid #eee;
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

/* 버튼 그룹 */
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

/* 글쓰기 폼 */
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

.form-row label {
  width: 140px;
  background: #f9f9f9;
  padding: 15px 20px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
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

/* 파일 첨부 */
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

/* 페이지네이션 */
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

.no-data {
  text-align: center;
  padding: 100px;
  color: #999;
}
</style>