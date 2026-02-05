<template>
  <section class="integrated-notice-area">
    <div class="top-section">
      <div class="notice-box">
        <div class="section-header">
          <h3>공지사항</h3>
          <span class="plus-btn" @click="$emit('moreNotice')">+</span>
        </div>
        <ul class="notice-list">
          <li v-for="(item, idx) in notices" :key="idx">
            <span class="n-title">{{ item.title }}</span>
            <span class="n-date">{{ item.date }}</span>
          </li>
        </ul>
      </div>

      <div class="parking-guide-box" @click="$emit('parking')">
        <img src="@/assets/parking.jpg" class="p-bg-img" alt="주차장 배경 이미지">

        <div class="parking-overlay">
          <div class="p-icon">🅿️</div>
          <h4>주차 이용 안내</h4>
          <p>외래/입원 고객을 위한<br>주차 요금 및 이용 방법 안내</p>
          <div class="p-arrow">더 알아보기 →</div>
        </div>
      </div>
    </div>

    <div class="bottom-section">
      <div class="section-header">
        <h3>건강이야기</h3>
        <span class="plus-btn" @click="$emit('moreHealth')">전체보기 +</span>
      </div>
      <div class="health-grid">
        <div class="health-card" v-for="(story, sidx) in healthStories" :key="sidx">
          <div class="h-img-box">
            <img :src="story.img" :alt="story.title">
            <span class="h-tag">{{ story.tag }}</span>
          </div>
          <div class="h-text-box">
            <h4 class="h-title">{{ story.title }}</h4>
            <p class="h-desc">{{ story.desc }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

// 부모(MainHome)에게 "나 이거 눌렀어!"라고 알려주는 전령들
defineEmits(['parking', 'moreNotice', 'moreHealth'])

// 공지사항 샘플 데이터(나중에 백엔드 API랑 연결할 데이터 저장소)
const notices = ref([
  { title: '[안내] S-HOSPITAL 설 연휴 진료 일정 안내', date: '2026.01.28' },
  { title: '[공고] 2026년 상반기 신규 의료진 채용 공고', date: '2026.01.25' },
  { title: '[뉴스] 스마트 주차 시스템 도입 안내', date: '2026.01.20' }
])

// 건강이야기 샘플 데이터
const healthStories = ref([
  { tag: '영양가이드', title: '면역력을 높이는 슈퍼푸드', desc: '우리 몸을 지키는 식단 가이드', img: 'https://picsum.photos/400/250?random=1' },
  { tag: '생활습관', title: '꿀잠 자는 법! 수면 가이드', desc: '매일 아침을 개운하게 시작하세요', img: 'https://picsum.photos/400/250?random=2' },
  { tag: '운동요법', title: '집에서 하는 거북목 스트레칭', desc: '굽은 목과 어깨 통증을 싹 날리세요', img: 'https://picsum.photos/400/250?random=3' }
])
</script>

<style scoped>
/* 중앙 정렬 컨테이너 */
.integrated-notice-area {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  /* 🪄 하단 패딩을 100px에서 20px로 쥰나 줄여서 푸터랑 가깝게 함 */
  padding: 40px 20px 20px; 
  display: flex;
  flex-direction: column;
  gap: 60px;
}

/* 1단: 공지사항 & 주차안내 스타일 */
.top-section { display: flex; gap: 25px; height: 300px; }

.notice-box { flex: 1.5; background: #fff; border: 1px solid #eee; border-radius: 20px; padding: 30px; box-shadow: 0 5px 15px rgba(0,0,0,0.03); }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.section-header h3 { font-size: 22px; font-weight: 800; color: #333; margin: 0; }
.plus-btn { color: #999; cursor: pointer; font-size: 18px; }
.notice-list { list-style: none; padding: 0; margin: 0; }
.notice-list li { display: flex; justify-content: space-between; padding: 15px 0; border-bottom: 1px solid #f8f9fa; cursor: pointer; }
.n-title { font-size: 15px; color: #555; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.n-date { font-size: 13px; color: #999; }

/* 주차안내 박스 (IMG 레이어) */
.parking-guide-box { flex: 1; border-radius: 20px; overflow: hidden; position: relative; cursor: pointer; }
.p-bg-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; z-index: 1; }
.parking-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.7); /* 쥰나 어두운 박스 오버레이 🧩 */
  color: white; padding: 35px; display: flex; flex-direction: column; justify-content: center;
  transition: 0.3s; z-index: 2;
}
.parking-guide-box:hover .parking-overlay { background: rgba(0, 86, 179, 0.9); }
.p-icon { font-size: 40px; margin-bottom: 15px; }
.parking-overlay h4 { font-size: 22px; font-weight: 800; margin: 0 0 10px 0; }
.parking-overlay p { font-size: 14px; line-height: 1.6; opacity: 0.9; margin: 0 0 20px 0; }
.p-arrow { font-size: 15px; font-weight: 700; }

/* 2단: 건강이야기 스타일 */
.health-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 25px; }
.health-card { background: #fff; border-radius: 20px; overflow: hidden; border: 1px solid #eee; transition: 0.3s; cursor: pointer; }
.health-card:hover { transform: translateY(-10px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.h-img-box { position: relative; height: 180px; }
.h-img-box img { width: 100%; height: 100%; object-fit: cover; }
.h-tag { position: absolute; top: 15px; left: 15px; background: #007bff; color: white; padding: 4px 10px; border-radius: 5px; font-size: 11px; font-weight: 700; }
.h-text-box { padding: 20px; }
.h-title { font-size: 17px; font-weight: 800; color: #333; margin: 0 0 8px 0; line-height: 1.4; }
.h-desc { font-size: 14px; color: #777; margin: 0; }
</style>