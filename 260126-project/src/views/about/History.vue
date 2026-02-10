<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>병원 연혁</h2>
      <p>S-HOSPITAL이 걸어온 발자취입니다.</p>
    </div>

    <div class="timeline-wrap">
      <div class="timeline-item" v-for="(yearGroup, idx) in historyData" :key="idx">
        <div class="year-col">
          <h3>{{ yearGroup.year }}</h3>
        </div>
        <div class="content-col">
          <ul>
            <li v-for="(event, i) in yearGroup.events" :key="i">
              <span class="month">{{ event.month }}</span>
              <span class="desc">{{ event.text }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const historyData = ref([
  {
    year: '2026',
    events: [
      { month: '03', text: 'S-HOSPITAL 그랜드 오픈' },
      { month: '01', text: '차세대 병원정보시스템(HIS) 구축 완료' }
    ]
  },
  {
    year: '2025',
    events: [
      { month: '11', text: '본관 준공 및 안전 진단 통과' },
      { month: '05', text: '스마트 병동 시범 운영' },
      { month: '02', text: '의료기관 인증 획득' }
    ]
  },
  {
    year: '2024',
    events: [
      { month: '09', text: '암센터 설립 기공식' },
      { month: '01', text: '서울 S 의료재단 설립' }
    ]
  }
]);
</script>

<style scoped>
.sub-page-container { max-width: 900px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 60px; }
.page-header h2 { font-size: 36px; font-weight: 700; color: #111; margin-bottom: 10px; }
.page-header p { font-size: 16px; color: #666; }

.timeline-wrap { position: relative; border-left: 2px solid #eee; margin-left: 100px; padding-bottom: 50px; }
.timeline-item { display: flex; margin-bottom: 60px; position: relative; }
.timeline-item:last-child { margin-bottom: 0; }

/* 연도 원형 포인트 */
.year-col { position: absolute; left: -100px; width: 100px; text-align: right; padding-right: 30px; }
.year-col h3 { font-size: 28px; font-weight: 800; color: #0171e9; margin: 0; line-height: 1; }
.timeline-item::before {
  content: ''; position: absolute; left: -6px; top: 5px;
  width: 10px; height: 10px; background: #fff; border: 3px solid #0171e9; border-radius: 50%;
}

.content-col { padding-left: 30px; }
.content-col ul { list-style: none; padding: 0; margin: 0; }
.content-col li { display: flex; margin-bottom: 15px; align-items: flex-start; }
.content-col .month { font-weight: 700; color: #333; width: 40px; margin-right: 15px; font-size: 16px; }
.content-col .desc { color: #555; font-size: 16px; line-height: 1.5; }
</style>