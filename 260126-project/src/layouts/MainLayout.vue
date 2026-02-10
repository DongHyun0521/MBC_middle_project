<template>
  <div id="home-layout">
    
    <div class="top-util-bar">
      <div class="util-inner">
        <template v-if="!isLogin">
          <span @click="router.push('/login')">로그인</span>
          <span @click="router.push('/regi')">회원가입</span>
        </template>
        <template v-else>
          <span class="user-info"><b>{{ loginName }}</b>님</span>
          <span @click="handleLogout" class="logout-btn-txt">로그아웃</span>
        </template>
        <span @click="router.push('/mypage')">마이페이지</span>
        <span @click="router.push('/location')">오시는길</span>
      </div>
    </div>

    <header class="main-header" @mouseenter="isMegamenuOpen = true" @mouseleave="isMegamenuOpen = false">
      <div class="header-inner">
        <div class="logo-box" @click="router.push('/')">
          <img src="@/assets/txtlogo2.png" alt="S-HOSPITAL" class="main-logo-img">
        </div>

        <nav class="nav-menu">
          <span class="nav-item" @click="goPage('/greeting')">병원소개</span>
          <span class="nav-item" @click="goPage('/doctorsearch')">의료진/진료과</span>
          <span class="nav-item" @click="goPage('/reservation')">진료예약/안내</span>
          <span class="nav-item" @click="goPage('/story')">건강정보</span>
          <span class="nav-item" @click="goPage('/notice')">고객서비스</span>
        </nav>
      </div>

      <div :class="['megamenu-panel', { active: isMegamenuOpen }]">
        <div class="megamenu-inner">
          <div class="menu-col">
            <p class="col-title">병원소개</p>
            <ul>
              <li @click="goPage('/greeting')">인사말</li>
              <li @click="goPage('/history')">연혁</li>
              <li @click="goPage('/mission')">미션/비전</li>
              <li @click="goPage('/location')">오시는 길</li>
              <li @click="goPage('/parkinginfo')">주차안내</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">의료진/진료과</p>
            <ul>
              <li @click="goPage('/doctorsearch')">의료진 찾기</li>
              <li @click="goPage('/deptsearch')">진료과 찾기</li>
              <li @click="goPage('/centerclinic')">센터/클리닉</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">진료예약</p>
            <ul>
              <li @click="goPage('/reservation')">진료예약</li>
              <li @click="goPage('/checkreservation')">예약내역 조회</li>
              <li @click="goPage('/guide')">진료안내</li>
              <li @click="goPage('/process')">진료절차</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">건강정보</p>
            <ul>
              <li @click="goPage('/disease')">질환백과</li>
              <li @click="goPage('/checkup')">자가진단</li>
              <li @click="goPage('/story')">건강이야기</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">고객서비스</p>
            <ul>
              <li @click="goPage('/notice')">공지사항</li>
              <li @click="goPage('/faq')">FAQ</li>
              <li @click="goPage('/voc')" v-if="showVoc">고객의 소리</li>
            </ul>
          </div>
        </div>
      </div>
    </header>

    <div v-if="!isMainPage" class="breadcrumb-bar">
      <div class="breadcrumb-inner">
        
        <span class="home-icon" @click="router.push('/')">
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/></svg>
        </span>
        
        <span class="divider">&gt;</span>
        
        <div class="crumb-dropdown" @mouseenter="openDepth1 = true" @mouseleave="openDepth1 = false">
          <div class="current-select">
            {{ currentCategoryInfo.categoryName || '서울에스병원' }}
            <svg class="arrow-icon" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 9l6 6 6-6"/></svg>
          </div>
          
          <ul v-show="openDepth1" class="dropdown-list">
            <li v-for="cat in siteMap" :key="cat.name" @click="goPage(cat.list[0].path)">
              {{ cat.name }}
            </li>
          </ul>
        </div>
        
        <span class="divider">&gt;</span>

        <div class="crumb-dropdown" @mouseenter="openDepth2 = true" @mouseleave="openDepth2 = false">
          <div class="current-select active">
            {{ currentMenuName }}
            <svg class="arrow-icon" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 9l6 6 6-6"/></svg>
          </div>

          <ul v-show="openDepth2" class="dropdown-list">
            <li v-for="page in currentCategoryInfo.pages" :key="page.path" 
                :class="{ 'on': route.path.startsWith(page.path) }"
                @click="goPage(page.path)">
              {{ page.name }}
            </li>
          </ul>
        </div>

      </div>
    </div>

    <main class="content-body">
      <router-view />
    </main>

    <footer class="main-footer">
      <div class="footer-inner">
        <div class="footer-top">
          <div class="f-logo">
            <img src="@/assets/txtlogo2.png" alt="S-HOSPITAL" class="f-logo-img">
            <!-- <span class="f-logo-text">S-HOSPITAL</span> -->
          </div>
          <div class="f-links">
            <span class="bold">개인정보처리방침</span>
            <span class="bar">|</span>
            <span>이용약관</span>
            <span class="bar">|</span>
            <span>환자의 권리와 의무</span>
          </div>
        </div>
        <div class="footer-info">
          <div class="info-row">
            <span>서울특별시 종로구 종로 69 S-HOSPITAL</span>
            <span class="bar">|</span>
            <span>대표자: 엠비씨</span>
            <span class="bar">|</span>
            <span>사업자등록번호: 123-45-67890</span>
          </div>
          <div class="info-row">
            <span>대표전화: <b class="blue-txt">1588-0000</b></span>
            <span class="bar">|</span>
            <span>응급의료센터: 02-123-4567</span>
            <span class="bar">|</span>
            <span>이메일: help@s-hospital.com</span>
          </div>
        </div>
        <div class="footer-bottom">
          <p class="copy">Copyright © S-HOSPITAL. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <div class="floating-group">
      <button class="f-btn top-btn" @click="scrollToTop">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="m18 15-6-6-6 6"/></svg>
        <span class="f-text">TOP</span>
      </button>
      <button class="f-btn chat-btn" @click="openChat">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        <span class="f-text">챗봇</span>
      </button>
      <button class="f-btn car-btn" @click="router.push('/vehiregi')">
        <svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><circle cx="17" cy="17" r="2"/></svg>
        <span class="f-text">차량등록</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter(); 
const route = useRoute();   

// 1. 상태 변수
const isLogin = ref(false);        
const loginName = ref('');         
const loginInfo = ref(null);       
const isMegamenuOpen = ref(false); 

// [추가] 드롭다운 열림/닫힘 상태 관리 변수
const openDepth1 = ref(false); // 대분류용
const openDepth2 = ref(false); // 소분류용

// ★ 2. [신규] 전체 사이트 구조 정의
const siteMap = [
  {
    name: '병원소개',
    list: [
      { name: '인사말', path: '/greeting' },
      { name: '연혁', path: '/history' },
      { name: '미션/비전', path: '/mission' },
      { name: '오시는 길', path: '/location' },
      { name: '주차안내', path: '/parkinginfo' }
    ]
  },
  {
    name: '의료진/진료과',
    list: [
      { name: '의료진 찾기', path: '/doctorsearch' },
      { name: '진료과 찾기', path: '/deptsearch' },
      { name: '센터/클리닉', path: '/centerclinic' }
    ]
  },
  {
    name: '진료예약',
    list: [
      { name: '진료예약 신청', path: '/reservation' },
      { name: '예약내역 조회', path: '/checkreservation' },
      { name: '진료안내', path: '/guide' },
      { name: '진료절차', path: '/process' }
    ]
  },
  {
    name: '건강정보',
    list: [
      { name: '질환백과', path: '/disease' },
      { name: '자가진단', path: '/checkup' },
      { name: '건강이야기', path: '/story' }
    ]
  },
  {
    name: '고객서비스',
    list: [
      { name: '공지사항', path: '/notice' },
      { name: 'FAQ', path: '/faq' },
      { name: '고객의 소리', path: '/voc' },
      { name: '차량등록', path: '/vehiregi' }
    ]
  },
  {
    name: '회원서비스',
    list: [
      { name: '로그인', path: '/login' },
      { name: '회원가입', path: '/regi' }
    ]
  },
  {
    name: '마이페이지',
    list: [
      { name: '나의 정보', path: '/mypage' }
    ]
  }
];

// 기존 menuMap은 호환성을 위해 유지 (단순 이름 조회용)
const menuMap = {
  'doctorsearch': { parent: '의료진/진료과', current: '의료진 찾기' },
  'deptsearch':   { parent: '의료진/진료과', current: '진료과 찾기' },
  'centerclinic': { parent: '의료진/진료과', current: '센터/클리닉' },
  'reservation':      { parent: '진료예약', current: '진료예약 신청' },
  'checkreservation': { parent: '진료예약', current: '예약내역 조회' },
  'guide':            { parent: '진료예약', current: '진료안내' },
  'process':          { parent: '진료예약', current: '진료절차' },
  'notice':   { parent: '고객서비스', current: '공지사항' },
  'faq':      { parent: '고객서비스', current: 'FAQ' },
  'voc':      { parent: '고객서비스', current: '고객의 소리' },
  'vehiregi': { parent: '고객서비스', current: '차량등록' },
  'login': { parent: '회원서비스', current: '로그인' },
  'regi':  { parent: '회원서비스', current: '회원가입' },
  'mypage': { parent: '마이페이지', current: '나의 정보' },
  'location': { parent: '병원소개', current: '오시는 길' },
  'greeting': { parent: '병원소개', current: '인사말' },
  'history':  { parent: '병원소개', current: '연혁' },
  'mission':  { parent: '병원소개', current: '미션/비전' },
  'disease': { parent: '건강정보', current: '질환백과' },
  'checkup': { parent: '건강정보', current: '자가진단' },
  'stroy':   { parent: '건강정보', current: '건강이야기' },
  'search':  { parent: '통합검색', current: '검색결과' },
  'parking': { parent: 'HOME', current: '주차 이용 안내' },
}

// 3. 기능 함수
const goPage = (path) => {
  isMegamenuOpen.value = false; 
  router.push(path);            
}

const checkLogin = () => {
  const loginData = sessionStorage.getItem('loginId')
  if (loginData) {
    try {
      const user = JSON.parse(loginData)
      isLogin.value = true;
      loginName.value = user.name || user.id;
      loginInfo.value = user;
    } catch (e) {
      isLogin.value = true;
      loginName.value = loginData;
    }
  } else {
    isLogin.value = false;
    loginInfo.value = null;
  }
}

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    sessionStorage.removeItem('loginId');
    isLogin.value = false;
    loginInfo.value = null;
    router.push('/');
  }
}

const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })
const openChat = () => alert('상담 챗봇 서비스 준비 중입니다')


// 4. Computed (자동 계산 속성)
const showVoc = computed(() => {
  if (!isLogin.value) return false; 
  if (loginInfo.value && loginInfo.value.loginType === 'MED') return false; 
  return true; 
});

const isMainPage = computed(() => route.path === '/' || route.path === '/mainhome')

// ★ [업그레이드] 현재 보고 있는 페이지가 어느 카테고리(대분류)에 속하는지 찾기
const currentCategoryInfo = computed(() => {
  const currentPath = route.path;
  
  // siteMap을 하나씩 훑어봅니다.
  for (const group of siteMap) {
    // 그룹 안에 있는 페이지 목록(list) 중에, 현재 주소랑 시작 부분이 같은 게 있나 확인
    const found = group.list.find(page => currentPath.startsWith(page.path) && page.path !== '/'); 
    
    if (found) {
      // 찾았으면 그 그룹 정보(이름, 형제들 목록)를 반환
      return {
        categoryName: group.name, 
        pages: group.list         
      };
    }
  }
  
  // 만약 못 찾으면(예: 메인페이지) 빈 껍데기
  return { categoryName: '', pages: [] };
});

// [현재 페이지 이름] 
// siteMap에서 찾거나, 없으면 기존 menuMap에서 찾기
const currentMenuName = computed(() => {
  // 1. siteMap에서 현재 경로와 일치하는 메뉴 이름 찾기
  const foundInSiteMap = currentCategoryInfo.value.pages.find(p => route.path.startsWith(p.path));
  if (foundInSiteMap) return foundInSiteMap.name;

  // 2. 없으면 기존 방식(menuMap)으로 찾기
  const key = route.path.replace('/', '').split('/')[0]
  return menuMap[key]?.current || '페이지'
})

// 5. Watch & Mounted
watch(() => route.path, () => { 
  checkLogin(); 
  isMegamenuOpen.value = false;
  // 드롭다운도 페이지 이동하면 닫히게 설정
  openDepth1.value = false;
  openDepth2.value = false;
})

onMounted(() => {
  checkLogin();
})
</script>

<style scoped>
#home-layout {
  font-family: 'pretendard';
  width: 100%;
  position: relative;
}

/* 1. 상단 유틸 바 */
.top-util-bar {
  background-color: #404347;
  height: 35px;
  width: 100%;
  position: relative;
  z-index: 1002;
}

.util-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.util-inner span {
  color: #ccc;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}

.logout-btn-txt {
  color: #ffb4b4 !important;
  font-weight: 400;
}

/* 2. 메인 헤더 */
.main-header {
  background-color: #ffffff;
  height: 80px;
  width: 100%;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-box {
  display: flex;
  align-items: center;
  cursor: pointer;
  flex-shrink: 0; /* 로고 찌그러지지 않도록 */
  margin-left: -6px;
}

.main-logo-img {
  height: 48px;
  width: auto;
}

.nav-menu {
  max-width: 1200px;
  display: flex;
  gap: 80px;
  height: 100%;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.nav-item {
  font-size: 22px;
  font-weight: 600;
  color: #222;
  cursor: pointer;
  transition: 0.2s;
}

.nav-item:hover {
  color: #005baa;
}

/* 2-1. 메가메뉴 */
.megamenu-panel {
  position: absolute;
  top: 80px;
  left: 0;
  width: 100%;
  background: #ffffff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  height: 0;
  overflow: hidden;
  transition: height 0.3s ease-in-out;
  z-index: 999;
}

.megamenu-panel.active {
  height: 274px;
}

.megamenu-inner {
  width: 100%;
  max-width: 1200px;  
  margin: 0 auto;     
  padding: 40px 20px; 
  display: flex;
  justify-content: space-between;
  gap: 45px;
}

.menu-col {
  border-left: 1px solid #f5f5f5;
  padding-left: 20px;
}

.menu-col:first-child {
  border-left: none;
  padding-left: 0;
}

.col-title {
  font-size: 20px;
  font-weight: 600;
  color: #005baa;
  margin-bottom: 20px;
}

.menu-col ul li {
  list-style: none;
  font-size: 16px;
  color: #666;
  margin-bottom: 12px;
  cursor: pointer;
  transition: color 0.2s;
}

.menu-col ul li:hover {
  color: #005baa;
  text-decoration: underline;
}

/* ★ 3. 브레드크럼 (업그레이드 스타일) ★ */
.breadcrumb-bar {
  background-color: #005baa; /* 병원 메인 블루 */
  height: 50px;
  width: 100%;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.breadcrumb-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  color: #fff;
  font-size: 14px;
}

.home-icon {
  display: flex;
  align-items: center;
  cursor: pointer;
  opacity: 0.8;
  padding: 0 10px;
}

.divider {
  margin: 0 15px;
  color: rgba(255,255,255,0.3);
  font-size: 12px;
}

/* 드롭다운 컨테이너 (이 박스 안에 글자랑 목록이 다 들어감) */
.crumb-dropdown {
  position: relative; /* 목록이 이 박스 기준으로 열리게 함 */
  height: 100%;
  display: flex;
  align-items: center;
  cursor: pointer;
}

/* 현재 선택된 글자 영역 */
.current-select {
  display: flex;
  align-items: center;
  gap: 8px; /* 글자랑 화살표 사이 간격 */
  padding: 0 15px;
  height: 100%;
  color: rgba(255,255,255,0.8);
  font-weight: 500;
  transition: all 0.2s;
}

/* 마우스 올렸을 때 배경색 변화 */
.current-select:hover,
.crumb-dropdown:hover .current-select {
  background-color: #044884; /* 약간 더 어두운 남색 */
  color: #fff;
}

/* 현재 페이지(소분류)는 더 진하게 표시 */
.current-select.active {
  color: #fff;
  font-weight: 700;
}

.arrow-icon {
  opacity: 0.6;
}

/* 숨겨진 드롭다운 목록 (ul) */
.dropdown-list {
  position: absolute; /* 둥둥 떠있게 */
  top: 50px;        /* 바 높이만큼 아래로 */
  left: 0;
  min-width: 180px; /* 최소 너비 */
  background: #fff;
  border: 1px solid #ddd;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1); /* 그림자 */
  padding: 10px 0;
  z-index: 2000;    /* 다른 요소보다 위에 */
  list-style: none;
}

/* 목록의 각 항목 (li) */
.dropdown-list li {
  padding: 10px 20px;
  font-size: 14px;
  color: #555;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap; /* 줄바꿈 금지 */
}

/* 목록 항목 호버 효과 */
.dropdown-list li:hover {
  background-color: #f4f7fa; /* 연한 하늘색 배경 */
  color: #005baa;            /* 글자는 브랜드 컬러 */
  font-weight: 600;
}

/* 현재 보고 있는 페이지는 목록에서도 진하게 */
.dropdown-list li.on {
  color: #005baa;
  font-weight: 700;
  background-color: #f9f9f9;
}

/* 4. 푸터 */
.main-footer {
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
  padding: 40px 0 40px;
  margin-top: 100px;
  width: 100%;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e5e5;
  padding-bottom: 25px;
}

.f-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  opacity: 0.6;
}

.f-logo-img {
  height: 50px;
  width: auto;
  filter: grayscale(100%);
}

.f-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.f-links span {
  font-size: 17px;
  color: #888;
  cursor: pointer;
}

.f-links .bold {
  font-weight: 700;
  color: #333;
}

.f-links .bar {
  color: #ddd;
  cursor: default;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  font-size: 15px;
  color: #888;
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  align-items: center;
}

.blue-txt {
  color: #005baa;
  font-weight: 700;
  font-size: 19px;
}

.footer-bottom {
  border-top: 1px solid #e5e5e5;
  padding-top: 20px;
}

.copy {
  font-size: 12px;
  color: #aaa;
  font-family: Arial, sans-serif;
}

/* 플로팅 버튼 */
.floating-group {
  position: fixed;
  right: 20px;
  bottom: 50px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 2000;
}

.f-btn {
  width: 55px;
  height: 55px;
  border-radius: 50%;
  border: 1px solid #e5e5e5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.f-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.12);
}

.top-btn { color: #555; }
.chat-btn { background: #6a7c86 !important; color: #fff; border: none; }
.car-btn { background: #005baa !important; color: #fff; border: none; }

.f-text {
  font-size: 12px;
  font-weight: 00;
  margin-top: 1px;
  letter-spacing: -1.2px;
}
</style>