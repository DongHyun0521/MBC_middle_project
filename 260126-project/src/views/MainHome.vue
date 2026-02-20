<template>
  <div class="home-container" @scroll="handleInternalScroll">
    <main class="snap-wrapper">
      <Section1HeroQuickCard class="snap-section" @reserve="goToReserve" @doctor="goToDoctor" @dept="goToDept"
        @check="goToCheck" @vehiregi="goToCarRegi" @location="goToLocation" />

      <Section2SearchUtility class="snap-section" />
      <Section3HealthStory class="snap-section" />
      <Section4NoticeParking class="snap-section" @parking="goToParkingGuide" />

      <footer class="main-home-footer">
        <div class="footer-inner wide-footer">
          <div class="f-row f-top">
            <div class="f-logo-box">
              <img src="@/assets/txtlogo2.png" alt="S-HOSPITAL" class="f-logo-img">
            </div>
            <div class="f-links">
              <span class="bold">개인정보처리방침</span>
              <span class="bar">|</span>
              <span>이용약관</span>
              <span class="bar">|</span>
              <span>환자의 권리와 의무</span>
            </div>
          </div>

          <div class="f-row f-middle">
            <div class="f-info-text">
              <span>서울특별시 종로구 종로 69 S-HOSPITAL</span>
              <span class="bar">|</span>
              <span>대표자: 엠비씨</span>
              <span class="bar">|</span>
              <span>사업자등록번호: 123-45-67890</span>
            </div>

            <div class="f-sns-side">
              <a href="#" class="sns-link" title="유튜브">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path
                    d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z" />
                  <polygon points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02" />
                </svg>
              </a>
              <a href="#" class="sns-link" title="인스타그램">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
                  stroke-linecap="round" stroke-linejoin="round">
                  <rect x="2" y="2" width="20" height="20" rx="5" ry="5" />
                  <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
                  <line x1="17.5" y1="6.5" x2="17.51" y2="6.5" />
                </svg>
              </a>
              <a href="#" class="sns-link" title="네이버 블로그">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                  stroke-linecap="round" stroke-linejoin="round">
                  <path d="M4 4h16v16H4z" />
                  <path d="M9 8v8" />
                  <path d="M9 12h4a2 2 0 0 0 0-4H9" />
                  <path d="M9 16h5a2 2 0 0 0 0-4H9" />
                </svg>
              </a>
            </div>
          </div>

          <div class="f-row f-bottom-info">
            <span>대표전화: <b class="blue-txt">1588-0000</b></span>
            <span class="bar">|</span>
            <span>응급의료센터: 02-123-4567</span>
            <span class="bar">|</span>
            <span>이메일: help@s-hospital.com</span>
          </div>

          <div class="footer-copyright">
            <p>Copyright © S-HOSPITAL. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Section1HeroQuickCard from '@/components/Section1-Hero&QuickCard.vue'
import Section2SearchUtility from '@/components/Section2-Search&Utility.vue'
import Section3HealthStory from '@/components/Section3-HealthStory.vue'
import Section4NoticeParking from '@/components/Section4-Notice&Parking.vue'

const router = useRouter()

const emit = defineEmits(['mainScroll'])

const handleInternalScroll = (e) => {
  // 내부 컨테이너의 스크롤이 50px를 넘으면 헤더 색상 반전을 위해 true 전송
  emit('mainScroll', e.target.scrollTop > 50)
}

const goToCarRegi = () => router.push('/vehiregi')
const goToParkingGuide = () => router.push('/parkinginfo')
const goToReserve = () => router.push('/reservation')
const goToDoctor = () => router.push('/doctorsearch')
const goToDept = () => router.push('/deptsearch')
const goToCheck = () => router.push('/checkreservation')
const goToLocation = () => router.push('/location')
</script>

<style scoped>
.home-container {
  height: 100vh;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  /* 자석 기능 유지 */
  scrollbar-width: none;
  position: relative;
}

.home-container::-webkit-scrollbar {
  display: none;
}

.snap-wrapper {
  width: 100%;
}

.snap-section {
  height: 100vh;
  scroll-snap-align: start;
  scroll-snap-stop: always;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.main-home-footer {
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
  padding: 60px 0;
  scroll-snap-align: end;
}

.footer-inner {
  max-width: 1800px;
  margin: 0 auto;
  padding: 0 4%;
  width: 100%;
  text-align: left;
}

.f-logo-img {
  filter: grayscale(1);
  height: 50px;
  width: auto;
  object-fit: contain;
  opacity: 0.5;
}

.f-links {
  display: flex;
  gap: 20px;
  font-size: 16px;
  color: #888;
  margin-bottom: 15px;
}

.f-links .bold {
  font-weight: 700;
  color: #333;
}

.footer-info {
  font-size: 15px;
  color: #888;
  margin-bottom: 10px;
  line-height: 1.6;
}

.info-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 5px;
  flex-wrap: wrap;
}

.blue-txt {
  color: #005baa;
  font-weight: 700;
}

.bar {
  color: #ddd;
}

.copy {
  font-size: 13px;
  color: #aaa;
  margin-top: 20px;
}

.main-home-footer {
  width: 100%;
  background-color: #f8f8f8;
  padding: 40px 0;
  border-top: 1px solid #eee;
  font-family: 'pretendard', sans-serif;
}

.footer-inner.wide-footer {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 40px;
  text-align: left;
}

.f-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 9px;
}

.f-logo-img {
  filter: grayscale(1);
  height: 50px;
  width: auto;
  object-fit: contain;
  opacity: 0.5;
}

.f-logo-img:hover {
  filter: grayscale(0);
  opacity: 1;
}

.f-links {
  font-size: 17px;
  color: #666;
  display: flex;
  gap: 15px;
}

.f-links .bold {
  font-weight: 700;
  color: #005baa;
}

.bar {
  color: #ddd;
  margin: 0 5px;
}

.f-info-text,
.f-bottom-info {
  font-size: 15px;
  margin-bottom: -10px;
  color: #888;
  line-height: 1.2;
}

.f-bottom-info {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 30px;
}

.blue-txt {
  color: #005baa;
  font-weight: 800;
}

/* SNS 아이콘 스타일.png */
.f-sns-side {
  display: flex;
  gap: 10px;
}

.sns-link {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 50%;
  color: #777;
  transition: 0.3s;
}

.sns-link:hover {
  background-color: #005baa;
  color: #fff;
  border-color: #005baa;
  transform: translateY(-3px);
}

.footer-copyright {
  font-size: 13px;
  color: #bbb;
  border-top: 1px solid #f2f2f2;
  padding-top: 20px;
}
</style>