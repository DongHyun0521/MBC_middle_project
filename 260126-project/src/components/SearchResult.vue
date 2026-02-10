<template>
  <div class="search-result-wrap">
    
    <div class="result-header">
      <h2>
        <span class="highlight">'{{ queryKeyword }}'</span> 검색 결과 
        <span class="count">총 {{ totalCount }} 건</span>
      </h2>
    </div>

    <section v-if="filteredMenus.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>메뉴 바로가기 <span class="num">({{ filteredMenus.length }})</span></h3>
      </div>
      <div class="simple-list-container">
        <div 
          v-for="(menu, idx) in filteredMenus" 
          :key="idx" 
          class="simple-list-item" 
          @click="router.push(menu.path)"
        >
          <span class="txt">{{ menu.name }}</span>
          <span class="arrow">&gt;</span>
        </div>
      </div>
    </section>

    <section v-if="filteredDoctors.length > 0 || filteredDepts.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>의료진 / 진료과 <span class="num">({{ filteredDoctors.length + filteredDepts.length }})</span></h3>
      </div>
      
      <ul v-if="filteredDepts.length > 0" class="dept-list">
        <li v-for="dept in filteredDepts" :key="dept.med_dept_id" @click="goReservation({ deptId: dept.med_dept_id })">
          <div class="dept-info">
            <span class="badge">진료과</span>
            <strong>{{ dept.dept_name }}</strong>
          </div>
          <div class="link-txt">예약하기 &gt;</div>
        </li>
      </ul>

      <div v-if="filteredDoctors.length > 0" class="doc-list-wrap">
        <div v-for="doc in filteredDoctors" :key="doc.staff_id" class="doc-item" @click="goReservation({ docId: doc.staff_id, deptId: doc.med_dept_id })">
          <div class="doc-profile">
            <img v-if="doc.profile_img" :src="doc.profile_img" class="real-img" />
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#555" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <div class="doc-info-text">
            <p class="dept">{{ doc.dept_name }}</p>
            <p class="name">{{ doc.name }} 교수</p>
          </div>
          <div class="link-btn">진료예약</div>
        </div>
      </div>
    </section>

    <section v-if="filteredNotices.length > 0 || filteredFaqs.length > 0" class="result-section">
      <div class="sec-title-box">
        <h3>게시판 / 소식 <span class="num">({{ filteredNotices.length + filteredFaqs.length }})</span></h3>
      </div>
      
      <div v-if="filteredNotices.length > 0" class="board-container">
        <h4 class="sub-h4">공지사항</h4>
        <ul>
          <li v-for="notice in filteredNotices" :key="notice.noticeId" @click="router.push(`/notice/${notice.noticeId}`)">
            <span class="b-title">{{ notice.title }}</span>
            <span class="b-date">{{ notice.writeDate || notice.date }}</span>
          </li>
        </ul>
      </div>

      <div v-if="filteredFaqs.length > 0" class="board-container">
        <h4 class="sub-h4">FAQ (자주 묻는 질문)</h4>
        <ul>
          <li v-for="faq in filteredFaqs" :key="faq.faqId" @click="router.push('/faq')">
            <div class="b-left">
              <span class="q-mark">Q.</span>
              <span class="b-title">{{ faq.question || faq.title }}</span>
            </div>
          </li>
        </ul>
      </div>
    </section>

    <div v-if="totalCount === 0" class="no-result">
      <div class="icon-box">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/><path d="M8 11h6"/></svg>
      </div>
      <p class="main-msg">'{{ queryKeyword }}'에 대한 검색 결과가 없습니다.</p>
      <p class="sub-msg">검색어의 철자가 정확한지 다시 한번 확인해 주세요.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// [API] 기존 파일 재사용 (새 파일 생성 X)
import { getAllDoctorsReq, getDeptsReq } from '@/api/reservation' 
import { getNoticesReq, getFaqsReq } from '@/api/customer'

const route = useRoute()
const router = useRouter()

// 현재 검색어
const queryKeyword = ref('')

// ============================================
// 1. 데이터 State
// ============================================

// [메뉴 리스트] 여기에 메뉴를 추가하면 검색 결과에 나옵니다!
const siteMenus = [
  { name: '진료 예약', path: '/reservation' },
  { name: '예약 조회', path: '/checkreservation' },
  { name: '의료진 소개', path: '/doctorsearch' },
  { name: '진료과 안내', path: '/deptsearch' }, // 추가됨
  { name: '오시는 길', path: '/location' },
  { name: '공지사항', path: '/notice' },
  { name: '자주 묻는 질문 (FAQ)', path: '/faq' },
  { name: '차량 등록', path: '/vehiregi' },
  { name: '마이페이지', path: '/mypage' },
  { name: '건강이야기', path: '/health-story' }, // (임시 경로)
  { name: '질환백과', path: '/disease-dict' },   // (임시 경로)
  { name: '자가진단', path: '/self-check' },     // (임시 경로)
]

// 서버 데이터 저장소
const allDoctors = ref([]) 
const allDepts = ref([])   
const allNotices = ref([]) 
const allFaqs = ref([])    

// ============================================
// 2. 검색 필터링 로직 (Computed)
// ============================================

// [메뉴] 사이트 메뉴 이름 검색
const filteredMenus = computed(() => {
  if (!queryKeyword.value) return []
  return siteMenus.filter(m => m.name.includes(queryKeyword.value))
})

// [진료과] 진료과 이름 검색 (데이터 없을 경우 빈문자열 처리)
const filteredDepts = computed(() => {
  if (!queryKeyword.value) return []
  return allDepts.value.filter(d => (d.dept_name || '').includes(queryKeyword.value))
})

// [의료진] 의사 이름 OR 진료과 이름으로 검색
const filteredDoctors = computed(() => {
  if (!queryKeyword.value) return []
  return allDoctors.value.filter(d => 
    (d.name || '').includes(queryKeyword.value) || 
    (d.dept_name || '').includes(queryKeyword.value)
  )
})

// [공지사항] 글 제목 검색
const filteredNotices = computed(() => {
  if (!queryKeyword.value) return []
  return allNotices.value.filter(n => (n.title || '').includes(queryKeyword.value))
})

// [FAQ] 질문 내용 OR 제목 검색
const filteredFaqs = computed(() => {
  if (!queryKeyword.value) return []
  return allFaqs.value.filter(f => 
    (f.question || '').includes(queryKeyword.value) || 
    (f.title || '').includes(queryKeyword.value)
  )
})

// [전체 건수] 각 필터링 결과의 개수 합산
const totalCount = computed(() => {
  return filteredMenus.value.length + 
         filteredDepts.value.length + 
         filteredDoctors.value.length + 
         filteredNotices.value.length + 
         filteredFaqs.value.length
})

// ============================================
// 3. API 호출
// ============================================

const fetchData = async () => {
  try {
    // 4가지 데이터를 병렬로 한 번에 가져옴
    const [docRes, deptRes, noticeRes, faqRes] = await Promise.all([
      getAllDoctorsReq(),
      getDeptsReq(),
      getNoticesReq(),
      getFaqsReq()
    ])

    allDoctors.value = docRes.data || []
    allDepts.value = deptRes.data || []
    allNotices.value = noticeRes.data || []
    allFaqs.value = faqRes.data || []

  } catch (e) {
    console.error("데이터 로딩 실패:", e)
  }
}

// 예약 페이지로 이동 (쿼리 포함)
const goReservation = (query) => {
  router.push({ path: '/reservation', query })
}

onMounted(() => {
  queryKeyword.value = route.query.q || ''
  fetchData()
})

watch(() => route.query.q, (newVal) => {
  queryKeyword.value = newVal || ''
})
</script>

<style scoped>
.search-result-wrap {
  font-family: 'pretendard';
  max-width: 1000px;
  margin: 0 auto;
  padding: 60px 20px;
  min-height: 70vh;
}

/* 헤더 */
.result-header {
  border-bottom: 2px solid #222;
  padding-bottom: 20px;
  margin-bottom: 40px;
}

.result-header h2 {
  font-size: 26px;
  color: #111;
  font-weight: 700;
}

.highlight {
  color: #0171e9;
}

.count {
  font-size: 16px;
  color: #666;
  margin-left: 10px;
  font-weight: 400;
}

/* 섹션 공통 */
.result-section {
  margin-bottom: 60px;
}

.sec-title-box {
  margin-bottom: 10px;
  border-bottom: 1px solid #333; /* 섹션 제목 밑줄 */
  padding-bottom: 12px;
}

.sec-title-box h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.sec-title-box .num {
  font-size: 14px;
  color: #0171e9;
  margin-left: 4px;
  font-weight: 600;
}

/* 1. 메뉴 리스트 (수정됨: 깔끔한 라인 스타일) */
.simple-list-container {
  border-top: 1px solid #eee; /* 상단 마감선 */
}

.simple-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 10px; /* 넉넉한 터치 영역 */
  border-bottom: 1px solid #eee; /* 구분선 */
  cursor: pointer; /* 손가락 커서 필수! */
  background: #fff;
  transition: all 0.2s ease; /* 부드러운 전환 */
}

/* [중요] 마우스 올렸을 때 효과 */
.simple-list-item:hover {
  background-color: #f8f9fa; /* 연한 회색 배경 */
}

.simple-list-item:hover .txt {
  color: #0171e9; /* 글자색 파랗게 */
  font-weight: 600;
}

.simple-list-item .txt {
  font-size: 16px;
  color: #333;
  transition: 0.2s;
}

.simple-list-item .arrow {
  color: #ccc;
  font-family: monospace; /* 화살표 모양 예쁘게 */
  font-weight: bold;
}

/* 2. 진료과 리스트 */
.dept-list {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.dept-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: 0.2s;
}

.dept-list li:hover {
  background-color: #f8f9fa;
}

.dept-info {
  display: flex;
  align-items: center;
}

.dept-info .badge {
  background-color: #f1f3f5;
  color: #495057;
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 4px;
  margin-right: 10px;
}

.dept-info strong {
  font-size: 16px;
  color: #333;
}

.link-txt {
  font-size: 13px;
  color: #888;
  font-weight: 500;
}

.dept-list li:hover .link-txt {
  color: #0171e9; /* 호버 시 텍스트 파랗게 */
}

/* 3. 의료진 리스트 */
.doc-list-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.doc-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 6px;
  cursor: pointer;
  background: #fff;
  transition: 0.2s;
}

.doc-item:hover {
  border-color: #0171e9;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05); /* 살짝 뜨는 효과 */
}

.doc-profile {
  width: 40px;
  height: 40px;
  background-color: #f1f3f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  overflow: hidden;
}

.real-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doc-info-text {
  flex: 1;
}

.doc-info-text .dept {
  font-size: 12px;
  color: #0171e9;
  font-weight: 600;
  margin-bottom: 2px;
}

.doc-info-text .name {
  font-size: 15px;
  font-weight: 700;
  color: #333;
}

.link-btn {
  font-size: 13px;
  background-color: #fff;
  border: 1px solid #ddd;
  padding: 6px 12px;
  border-radius: 4px;
  color: #555;
  font-weight: 500;
  transition: 0.2s;
}

.doc-item:hover .link-btn {
  background-color: #0171e9;
  color: #fff;
  border-color: #0171e9;
}

/* 4. 게시판 그룹 */
.board-container {
  margin-bottom: 30px;
}

.sub-h4 {
  font-size: 15px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 600;
  border-left: 3px solid #ddd;
  padding-left: 8px;
}

.board-container ul {
  list-style: none;
  padding: 0;
  border-top: 1px solid #999; /* 게시판은 상단 라인 조금 진하게 */
}

.board-container li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 5px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.board-container li:hover {
  background-color: #f8f9fa;
}

.board-container li:hover .b-title {
  text-decoration: underline; /* 호버 시 밑줄 */
}

.b-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.b-title {
  font-size: 15px;
  color: #333;
}

.b-date {
  font-size: 13px;
  color: #999;
}

.q-mark {
  font-weight: 800;
  color: #0171e9;
  margin-right: 5px;
}

/* 5. 검색 결과 없음 */
.no-result {
  text-align: center;
  padding: 100px 0;
  color: #888;
}

.no-result .icon-box {
  margin-bottom: 20px;
}

.main-msg {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 600;
}

.sub-msg {
  font-size: 14px;
  color: #999;
}
</style>