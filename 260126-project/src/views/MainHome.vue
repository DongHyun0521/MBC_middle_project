<template>
  <div class="home-content">
    <main>
      <section class="hero-section">
        <div class="hero-text">
          <h3>당신의 건강과 편안한 방문을 위해</h3>
          <p>최고의 의료진이 365일 함께합니다</p>
          <button class="reserve-btn" @click="goToReserve">간편 진료예약</button>
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
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToCarRegi = () => router.push('/vehiregi')
const goToParkingGuide = () => router.push('/parking-guide')
const goToReserve = () => alert('진료예약 페이지는 준비 중입니다')
const goToDoctor = () => alert('의료진 찾기 페이지는 준비 중입니다')
const goToLocation = () => alert('오시는 길 페이지는 준비 중입니다')
</script>

<style scoped>
/* 📌 메인 알맹이 스타일 (헤더/푸터 제외) */
.hero-section { background: linear-gradient(135deg, #f0f7ff 0%, #d9ebff 100%); height: 350px; display: flex; align-items: center; padding-left: 10%; }
.quick-menu { display: flex; justify-content: center; gap: 25px; margin-top: -50px; }
.quick-item { width: 150px; height: 150px; background: white; border-radius: 20px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: 0.3s; }
.info-section { display: flex; justify-content: center; gap: 40px; padding: 80px 50px; }
.floating-btn { position: fixed; right: 40px; bottom: 50px; width: 80px; height: 80px; background: #007bff; color: white; border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; box-shadow: 0 10px 20px rgba(0,123,255,0.4); cursor: pointer; z-index: 1000; }
/* ... (공주님이 주신 스타일 그대로 유지) */
</style>