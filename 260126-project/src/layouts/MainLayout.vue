<!-- <template>
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
          <img src="@/assets/logo.png" alt="S-HOSPITAL" class="main-logo-img">
          <h1 class="logo-text">S-HOSPITAL</h1>
        </div>

        <nav class="nav-menu">
          <span class="nav-item">병원소개</span>
          <span class="nav-item" @click="goPage('/doctorsearch')">의료진/진료과</span>
          <span class="nav-item" @click="goPage('/reservation')">진료예약</span>
          <span class="nav-item">건강정보</span>
          <span class="nav-item">고객서비스</span>
        </nav>
      </div>

      <div :class="['megamenu-panel', { active: isMegamenuOpen }]">
        <div class="megamenu-inner">
          <div class="menu-col">
            <p class="col-title">병원소개</p>
            <ul>
              <li>인사말</li>
              <li>연혁</li>
              <li>미션/비전</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">의료진/진료과</p>
            <ul>
              <li @click="goPage('/doctorsearch')">의료진 찾기</li>
              <li @click="goPage('/deptsearch')">진료과 찾기</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">진료예약</p>
            <ul>
              <li @click="goPage('/reservation')">진료예약 신청</li>
              <li @click="goPage('/checkreservation')">예약내역 조회</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">건강정보</p>
            <ul>
              <li>질환백과</li>
              <li>자가진단</li>
              <li @click="goPage('/health-story')">건강이야기</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">고객서비스</p>
            <ul>
              <li @click="goPage('/notice')">공지사항</li>
              <li @click="goPage('/faq')">FAQ</li>
              <li @click="goPage('/voc')">고객의 소리</li>
            </ul>
          </div>
        </div>
      </div>
    </header>

    <main class="content-body">
      <router-view />
    </main>

    <footer class="main-footer">
      <div class="footer-inner">
        <div class="footer-top">
          <div class="f-logo">
            <img src="@/assets/logo.png" alt="S-HOSPITAL" class="f-logo-img">
            <span class="f-logo-text">S-HOSPITAL</span>
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
      <button class="f-btn top-btn" @click="scrollToTop"><span class="f-icon">▲</span><span class="f-text">TOP</span></button>
      <button class="f-btn chat-btn" @click="openChat"><span class="f-icon">💬</span><span class="f-text">챗봇</span></button>
      <button class="f-btn car-btn" @click="router.push('/vehiregi')"><span class="f-icon">🚗</span><span class="f-text">차량등록</span></button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter(); const route = useRoute()
const isLogin = ref(false); const loginName = ref('')
const isMegamenuOpen = ref(false)

const goPage = (path) => {
  isMegamenuOpen.value = false; router.push(path)
}

const checkLogin = () => {
  const loginData = sessionStorage.getItem('loginId')
  if (loginData) {
    try {
      const user = JSON.parse(loginData)
      isLogin.value = true; 
      loginName.value = user.name || user.id
    } catch (e) {
      isLogin.value = true;
      loginName.value = loginData;
    }
  } else { 
    isLogin.value = false 
  }
}

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    sessionStorage.removeItem('loginId');
    isLogin.value = false; 
    router.push('/')
  }
}

const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })
const openChat = () => alert('상담 챗봇 서비스 준비 중입니다')

watch(() => route.path, () => { checkLogin(); isMegamenuOpen.value = false; })
onMounted(() => checkLogin())
</script>

<style scoped>
#home-layout { width: 100%; position: relative; }

/* 상단 유틸 바 */
.top-util-bar { background-color: #404347; height: 32px; width: 100%; position: relative; z-index: 1002; }
.util-inner { width: 100%; max-width: 1200px; margin: 0 auto; padding: 0 20px; display: flex; justify-content: flex-end; align-items: center; gap: 20px; height: 100%; }
.util-inner span { color: #ccc; font-size: 11px; cursor: pointer; transition: 0.2s; }
.logout-btn-txt { color: #ffb4b4 !important; font-weight: 500; }

/* 메인 헤더 */
.main-header { background-color: #ffffff; height: 80px; width: 100%; border-bottom: 1px solid #f0f0f0; position: sticky; top: 0; z-index: 1000; }
.header-inner { width: 100%; max-width: 1200px; margin: 0 auto; height: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center; }
.logo-box { display: flex; align-items: center; cursor: pointer; gap: 10px; flex-shrink: 0; }
.main-logo-img { height: 38px; width: auto; }
.logo-text { font-size: 22px; font-weight: 600; color: #404347; letter-spacing: -1px; }
.nav-menu { display: flex; gap: 45px; height: 100%; align-items: center; flex: 1; justify-content: flex-end; }
.nav-item { font-size: 16px; font-weight: 500; color: #4e4e4e; cursor: pointer; transition: 0.2s; }
.nav-item:hover { color: #0171e9; }

/* 메가메뉴 판넬 */
.megamenu-panel { position: absolute; top: 80px; left: 0; width: 100%; background: #ffffff; border-bottom: 1px solid #eee; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08); height: 0; overflow: hidden; transition: height 0.2s ease-in-out; z-index: 999; }
.megamenu-panel.active { height: 260px; }
.megamenu-inner { width: 100%; max-width: 1200px; margin: 0 auto; padding: 45px 20px; display: flex; justify-content: space-between; }
.menu-col { flex: 1; }
.col-title { font-size: 16px; font-weight: 700; color: #0171e9; margin-bottom: 20px; }
.menu-col ul li { list-style: none; font-size: 14px; color: #777; margin-bottom: 12px; cursor: pointer; }

/* 푸터 칼각 정렬 */
.main-footer { background-color: #ffffff; border-top: 1px solid #eee; padding: 60px 0 40px; margin-top: 100px; width: 100%; }
.footer-inner { max-width: 1200px; margin: 0 auto; padding: 0 20px; display: flex; flex-direction: column; gap: 30px; }
.footer-top { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f9f9f9; padding-bottom: 25px; }
.f-logo { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.f-logo-img { height: 30px; width: auto; }
.f-logo-text { font-weight: 600; font-size: 18px; color: #4e4e4e; opacity: 0.8; }
.f-links { display: flex; gap: 15px; align-items: center; }
.f-links span { font-size: 14px; color: #777; cursor: pointer; }
.f-links .bold { font-weight: 600; color: #404347; }
.f-links .bar { color: #eee; cursor: default; }
.footer-info { display: flex; flex-direction: column; gap: 10px; }
.info-row { font-size: 13px; color: #777; display: flex; gap: 15px; font-weight: 300; align-items: center; }
.blue-txt { color: #0171e9; font-weight: 600; }
.footer-bottom { border-top: 1px solid #f9f9f9; padding-top: 20px; }
.copy { font-size: 12px; color: #aaa; }

/* 플로팅 버튼 */
.floating-group { position: fixed; right: 30px; bottom: 40px; display: flex; flex-direction: column; gap: 12px; z-index: 2000; }
.f-btn { width: 55px; height: 55px; border-radius: 50%; border: 1px solid #eee; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; background: #fff; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); transition: 0.3s; }
.top-btn { color: #777; }
.chat-btn { background: #404347 !important; color: #fff; border: none; }
.car-btn { background: #0171e9 !important; color: #fff; border: none; }
.f-icon { font-size: 18px; margin-bottom: 2px; }
.f-text { font-size: 9px; font-weight: 700; }
</style> -->

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
          <img src="@/assets/txtlogo.png" alt="S-HOSPITAL" class="main-logo-img">
          <!-- <h1 class="logo-text">S-HOSPITAL</h1> -->
        </div>

        <nav class="nav-menu">
          <span class="nav-item">병원소개</span>
          <span class="nav-item" @click="goPage('/doctorsearch')">의료진/진료과</span>
          <span class="nav-item" @click="goPage('/reservation')">진료예약</span>
          <span class="nav-item">건강정보</span>
          <span class="nav-item">고객서비스</span>
        </nav>
      </div>

      <div :class="['megamenu-panel', { active: isMegamenuOpen }]">
        <div class="megamenu-inner">
          <div class="menu-col">
            <p class="col-title">병원소개</p>
            <ul>
              <li>인사말</li>
              <li>연혁</li>
              <li>미션/비전</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">의료진/진료과</p>
            <ul>
              <li @click="goPage('/doctorsearch')">의료진 찾기</li>
              <li @click="goPage('/deptsearch')">진료과 찾기</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">진료예약</p>
            <ul>
              <li @click="goPage('/reservation')">진료예약 신청</li>
              <li @click="goPage('/checkreservation')">예약내역 조회</li>
            </ul>
          </div>
          <div class="menu-col">
            <p class="col-title">건강정보</p>
            <ul>
              <li>질환백과</li>
              <li>자가진단</li>
              <li @click="goPage('/health-story')">건강이야기</li>
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

    <main class="content-body">
      <router-view />
    </main>

    <footer class="main-footer">
      <div class="footer-inner">
        <div class="footer-top">
          <div class="f-logo">
            <img src="@/assets/logo.png" alt="S-HOSPITAL" class="f-logo-img">
            <span class="f-logo-text">S-HOSPITAL</span>
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
      <button class="f-btn top-btn" @click="scrollToTop"><span class="f-icon">▲</span><span
          class="f-text">TOP</span></button>
      <button class="f-btn chat-btn" @click="openChat"><span class="f-icon">💬</span><span
          class="f-text">챗봇</span></button>
      <button class="f-btn car-btn" @click="router.push('/vehiregi')"><span class="f-icon">🚗</span><span
          class="f-text">차량등록</span></button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter();
const route = useRoute()

const isLogin = ref(false);
const loginName = ref('')
const loginInfo = ref(null); // 로그인 전체 정보 저장용
const isMegamenuOpen = ref(false)

const goPage = (path) => {
  isMegamenuOpen.value = false;
  router.push(path)
}

const checkLogin = () => {
  const loginData = sessionStorage.getItem('loginId')
  if (loginData) {
    try {
      const user = JSON.parse(loginData)
      isLogin.value = true;
      loginName.value = user.name || user.id
      loginInfo.value = user; // 정보 통째로 저장
    } catch (e) {
      isLogin.value = true;
      loginName.value = loginData;
    }
  } else {
    isLogin.value = false;
    loginInfo.value = null;
  }
}

// [핵심 로직] VOC 메뉴 보여줄지 판단
const showVoc = computed(() => {
  // 1. 로그아웃 상태면 안 보임
  if (!isLogin.value) return false;

  // 2. 의료진(MED)이면 안 보임
  if (loginInfo.value && loginInfo.value.loginType === 'MED') return false;

  // 3. 그 외(일반회원, 관리자)는 보임
  return true;
});

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    sessionStorage.removeItem('loginId');
    isLogin.value = false;
    loginInfo.value = null;
    router.push('/')
  }
}

const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })
const openChat = () => alert('상담 챗봇 서비스 준비 중입니다')

watch(() => route.path, () => { checkLogin(); isMegamenuOpen.value = false; })
onMounted(() => checkLogin())
</script>

<style scoped>
#home-layout {
  width: 100%;
  position: relative;
}

/* 상단 유틸 바 */
.top-util-bar {
  background-color: #404347;
  height: 32px;
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
  font-size: 11px;
  cursor: pointer;
  transition: 0.2s;
}

.logout-btn-txt {
  color: #ffb4b4 !important;
  font-weight: 500;
}

/* 메인 헤더 */
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
  gap: 10px;
  flex-shrink: 0;
}

.main-logo-img {
  height: 100px;
  width: auto;
}

.logo-text {
  font-size: 22px;
  font-weight: 600;
  color: #404347;
  letter-spacing: -1px;
}

.nav-menu {
  display: flex;
  gap: 45px;
  height: 100%;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.nav-item {
  font-size: 16px;
  font-weight: 500;
  color: #4e4e4e;
  cursor: pointer;
  transition: 0.2s;
}

.nav-item:hover {
  color: #043264;
}

/* 메가메뉴 판넬 */
.megamenu-panel {
  position: absolute;
  top: 80px;
  left: 0;
  width: 100%;
  background: #ffffff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  height: 0;
  overflow: hidden;
  transition: height 0.2s ease-in-out;
  z-index: 999;
}

.megamenu-panel.active {
  height: 260px;
}

.megamenu-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 45px 20px;
  display: flex;
  justify-content: space-between;
}

.menu-col {
  flex: 1;
}

.col-title {
  font-size: 16px;
  font-weight: 800;
  color: #043264;
  margin-bottom: 20px;
}

.menu-col ul li {
  list-style: none;
  font-size: 14px;
  color: #777;
  margin-bottom: 12px;
  cursor: pointer;
}

/* 푸터 칼각 정렬 */
.main-footer {
  background-color: #ffffff;
  border-top: 1px solid #eee;
  padding: 60px 0 40px;
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
  border-bottom: 1px solid #f9f9f9;
  padding-bottom: 25px;
}

.f-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.f-logo-img {
  height: 30px;
  width: auto;
}

.f-logo-text {
  font-weight: 600;
  font-size: 18px;
  color: #4e4e4e;
  opacity: 0.8;
}

.f-links {
  display: flex;
  gap: 15px;
  align-items: center;
}

.f-links span {
  font-size: 14px;
  color: #777;
  cursor: pointer;
}

.f-links .bold {
  font-weight: 600;
  color: #404347;
}

.f-links .bar {
  color: #eee;
  cursor: default;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  font-size: 13px;
  color: #777;
  display: flex;
  gap: 15px;
  font-weight: 300;
  align-items: center;
}

.blue-txt {
  color: #043264;
  font-weight: 600;
}

.footer-bottom {
  border-top: 1px solid #f9f9f9;
  padding-top: 20px;
}

.copy {
  font-size: 12px;
  color: #aaa;
}

/* 플로팅 버튼 */
.floating-group {
  position: fixed;
  right: 30px;
  bottom: 40px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 2000;
}

.f-btn {
  width: 55px;
  height: 55px;
  border-radius: 50%;
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  transition: 0.3s;
}

.top-btn {
  color: #777;
}

.chat-btn {
  background: #404347 !important;
  color: #fff;
  border: none;
}

.car-btn {
  background: #043264 !important;
  color: #fff;
  border: none;
}

.f-icon {
  font-size: 18px;
  margin-bottom: 2px;
}

.f-text {
  font-size: 9px;
  font-weight: 700;
}
</style>