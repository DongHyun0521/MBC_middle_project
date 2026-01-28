<template>
  <div class="main-container">
    <header class="main-header">
      <div class="logo">S-HOSPITAL</div>
      <nav class="nav-menu">
        <span>진료안내</span>
        <span>의료진소개</span>
        <span class="active">주차안내</span>
        <span>고객센터</span>
      </nav>
      <div class="user-menu">
        <button @click="goToLogin">로그인</button>
        <button @click="goToRegi">회원가입</button>
      </div>
    </header>

    <main>
      <section class="hero-section">
        <div class="hero-text">
          <h3>당신의 건강과 편안한 방문을 위해</h3>
          <p>최고의 의료진이 365일 함께합니다</p>
          <button class="reserve-btn">간편 진료예약</button>
        </div>
      </section>

      <section class="quick-menu">
        <div class="quick-item" @click="goToReserve">
          <span class="icon">🏥</span>
          <span class="label">진료예약</span>
        </div>
        <div class="quick-item" @click="goToDoctor">
          <span class="icon">👨‍⚕️</span>
          <span class="label">의료진찾기</span>
        </div>
        <div class="quick-item" @click="goToCarRegi">
          <span class="icon">🚗</span>
          <span class="label">차량등록</span>
        </div>
        <div class="quick-item" @click="goToLocation">
          <span class="icon">📍</span>
          <span class="label">오시는길</span>
        </div>
      </section>

      <section class="info-section">
        <div class="notice-box">
          <h3>병원 공지사항</h3>
          <ul>
            <li>[안내] 2026년 동절기 진료 시간 변경</li>
            <li>[소식] 최첨단 정밀 검진 장비 도입</li>
          </ul>
        </div>
        
        <div class="parking-guide-box">
          <h3>🅿️ 주차 이용 안내</h3>
          <div class="guide-content">
            <p><strong>이용 요금:</strong> 외래 진료 시 4시간 무료</p>
            <p><strong>운영 시간:</strong> 24시간 연중무휴</p>
            <button class="detail-btn" @click="goToParkingGuide">자세히 보기</button>
          </div>
        </div>
      </section>
    </main>

    <div class="floating-btn" @click="goToCarRegi">
      <span class="f-icon">🚗</span>
      <span class="f-text">차량등록</span>
    </div>

    <footer class="main-footer">
      <p>(01234) 서울특별시 건국구 냠냠로 1004</p>
      <p>Copyright © Gongju Hospital. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

// 페이지 이동 함수들
const goToLogin = () => router.push('/login');
const goToRegi = () => router.push('/regi');
const goToCarRegi = () => router.push('/vehiregi'); // 차량등록 페이지(생성예정)
const goToParkingGuide = () => router.push('/parking-guide'); // 주차안내 페이지(생성예정)

// 아직 페이지가 없는 함수들 (구조만)
const goToReserve = () => alert('진료예약 페이지는 준비 중입니다');
const goToDoctor = () => alert('의료진 찾기 페이지는 준비 중입니다');
const goToLocation = () => alert('오시는 길 페이지는 준비 중입니다');
const goToStatus = () => {};
</script>

<style scoped>
/* 전체 컨테이너 */
.main-container { width: 100%; position: relative; color: #333; }

/* 헤더: 신뢰감 있는 블루 포인트 */
.main-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 50px; border-bottom: 2px solid #f0f4f8; }
.logo { font-size: 24px; font-weight: bold; color: #007bff; }
.nav-menu span { margin: 0 20px; cursor: pointer; font-weight: 500; transition: 0.2s; }
.nav-menu span:hover { color: #007bff; }
.active { color: #007bff; border-bottom: 2px solid #007bff; }

/* 히어로 섹션 */
.hero-section { background: linear-gradient(135deg, #f0f7ff 0%, #d9ebff 100%); height: 350px; display: flex; align-items: center; padding-left: 10%; }
.hero-text h3 { font-size: 32px; margin-bottom: 10px; }
.reserve-btn { padding: 12px 25px; background: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; margin-top: 20px; }

/* 퀵 메뉴 (4구조) */
.quick-menu { display: flex; justify-content: center; gap: 30px; margin-top: -40px; } /* 배너 위에 살짝 걸치게 */
.quick-item { 
  width: 160px; height: 160px; background: white; border-radius: 20px; 
  box-shadow: 0 10px 20px rgba(0,0,0,0.05); display: flex; flex-direction: column; 
  align-items: center; justify-content: center; cursor: pointer; transition: 0.3s;
}
.quick-item:hover { transform: translateY(-10px); }
.quick-item .icon { font-size: 40px; margin-bottom: 10px; }
.quick-item .label { font-weight: bold; font-size: 16px; }

/* 정보 섹션 */
.info-section { display: flex; justify-content: center; gap: 50px; padding: 80px 50px; }
.notice-box, .parking-guide-box { width: 450px; padding: 30px; background: white; border-radius: 15px; border: 1px solid #eee; }
.parking-guide-box { border-left: 5px solid #007bff; } /* 강조 포인트 */
.count { color: #007bff; font-weight: bold; font-size: 20px; }
.detail-btn { margin-top: 15px; background: none; border: 1px solid #007bff; color: #007bff; padding: 5px 15px; border-radius: 20px; cursor: pointer; }

/* 📌 플로팅 버튼 */
.floating-btn {
  position: fixed; right: 40px; bottom: 60px; width: 85px; height: 85px;
  background: #007bff; color: white; border-radius: 50%;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  box-shadow: 0 8px 16px rgba(0,123,255,0.3); cursor: pointer; z-index: 1000;
}
.floating-btn .f-text { font-size: 12px; font-weight: bold; margin-top: 2px; }

.main-footer { background: #333; color: #ccc; padding: 40px; text-align: center; font-size: 14px; }
</style>