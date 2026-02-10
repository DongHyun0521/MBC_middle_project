<template>
  <section class="notice-area">
    <div class="top-section">
      <div class="notice-box">
        <div class="section-header">
          <h3>공지사항</h3>
          <span class="plus-btn" @click="router.push('/notice')">전체보기 +</span>
        </div>
        <ul class="notice-list">
          <li v-for="item in notices" :key="item.noticeId" @click="router.push(`/notice/${item.noticeId}`)">
            <span class="n-title">{{ item.title }}</span>
            <span class="n-date">{{ formatDate(item.writeDate) }}</span>
          </li>
          <li v-if="notices.length === 0" class="empty-txt">최신 공지사항을 불러오는 중입니다...</li>
        </ul>
      </div>

      <div class="parking-guide-box" @click="router.push('/parkinginfo')">
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
        <span class="plus-btn" @click="router.push('/story')">전체보기 +</span>
      </div>
      <div class="health-grid">
        <div class="health-card" v-for="(story, sidx) in healthStories" :key="sidx" @click="router.push('/story')">
          <div class="h-img-box">
            <div class="placeholder-thumb" :style="{ backgroundColor: story.color }">
              <span class="play-icon">▶</span>
            </div>
            <span class="h-tag">{{ story.dept }}</span>
          </div>
          <div class="h-text-box">
            <h4 class="h-title">{{ story.title }}</h4>
            <p class="h-desc">{{ story.doctor }} 교수</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticesReq } from '@/api/customer'

const router = useRouter()

const notices = ref([])
const healthStories = ref([
  { dept: '비뇨의학과', title: '[S-HOSPITAL] 몸이 보내는 전립선비대증 신호 3가지', doctor: '김장환', color: '#eef2f3' },
  { dept: '내분비내과', title: '[S-HOSPITAL] 몸이 보내는 남성 갱년기 신호', doctor: '구철룡', color: '#f1f1f1' },
  { dept: '정형외과', title: '[S-HOSPITAL] 생활 속 거북목을 부르는 자세 3가지', doctor: '이병호', color: '#e8f0fe' }
])

// 데이터 로드 (오직 날짜 기준!)
const fetchNotices = async () => {
  try {
    const res = await getNoticesReq()
    let rawList = res.data?.data || res.data || []

    // 고정글 무시하고 순수하게 날짜 내림차순 정렬
    const sortedList = [...rawList].sort((a, b) => {
      return new Date(b.writeDate) - new Date(a.writeDate)
    })

    // 5개만 할당
    notices.value = sortedList.slice(0, 5)
  } catch (e) {
    console.error("공지사항 연동 실패", e)
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return String(date).substring(0, 10).replace(/-/g, '.')
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notice-area {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 60px;
  font-family: 'pretendard';
}

/* 📌 5개 출력에 맞춰 높이 380px로 확장 */
.top-section {
  display: flex;
  gap: 25px;
  height: 380px;
}

.notice-box {
  flex: 1.5;
  background: #fff;
  border: 1px solid #eee;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.plus-btn {
  color: #999;
  cursor: pointer;
  font-size: 14px;
}

.notice-list {
  list-style: none;
  padding: 10px;
  margin: 0;
}

/* 📌 간격 12px로 살짝 조정 (황금비율) */
.notice-list li {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #f8f9fa;
  cursor: pointer;
}

.notice-list li:last-child {
  border-bottom: none;
}

.n-title {
  font-size: 16px;
  color: #555;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 80%;
}

.n-date {
  font-size: 14px;
  color: #999;
}

/* 주차 안내 박스 (높이 늘어나면 이미지도 같이 늘어남) */
.parking-guide-box {
  flex: 1;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.p-bg-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.parking-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 35px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: 0.3s;
  z-index: 2;
}

.p-icon {
  font-size: 40px;
  margin-bottom: 15px;
}

.parking-overlay h4 {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.parking-overlay p {
  font-size: 14px;
  line-height: 1.6;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.p-arrow {
  font-size: 15px;
  font-weight: 700;
}

/* 건강이야기 그리드 (동일) */
.health-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}

.health-card {
  background: #fff;
  overflow: hidden;
  border: 1px solid #eee;
  transition: 0.3s;
  cursor: pointer;
}

.health-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.h-img-box {
  position: relative;
  height: 180px;
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
  color: rgba(255, 255, 255, 0.5);
}

.h-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  background: #007bff;
  color: white;
  padding: 4px 10px;
  border-radius: 5px;
  font-size: 11px;
  font-weight: 700;
}

.h-text-box {
  padding: 20px;
}

.h-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  height: 2.8em;
  overflow: hidden;
}

.h-desc {
  font-size: 14px;
  color: #777;
  margin: 0;
}

.empty-txt {
  color: #bbb;
  text-align: center;
  padding: 20px 0;
  font-size: 14px;
  list-style: none;
}
</style>