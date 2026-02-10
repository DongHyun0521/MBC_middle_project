<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>자가진단</h2>
      <p>간단한 체크리스트를 통해 본인의 건강 상태를 확인해 보세요.</p>
    </div>

    <div class="checkup-grid">
      <div class="check-card" v-for="item in checkupList" :key="item.title">
        <div class="check-top">
          <span class="category">{{ item.category }}</span>
          <span class="icon">{{ item.icon }}</span>
        </div>
        <h4>{{ item.title }}</h4>
        <p>{{ item.desc }}</p>
        <button class="start-btn">진단 시작하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
const checkupList = [
  { category: '마음건강', icon: '🧠', title: '우울증 자가진단', desc: '최근 마음이 답답하고 무기력하신가요?' },
  { category: '눈건강', icon: '👁️', title: '안구건조증 체크', desc: '눈이 침침하고 이물감이 느껴지시나요?' },
  { category: '생활습관', icon: '🚭', title: '금연 성공 가능성', desc: '나의 흡연 의존도는 얼마나 될까요?' },
  { category: '관절건강', icon: '🦴', title: '손목터널증후군', desc: '손끝이 저릿하고 통증이 있으신가요?' }
];
</script>

<style scoped>
.sub-page-container { max-width: 1000px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 60px; }

.checkup-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 30px; }
.check-card { background: #fff; border: 1px solid #eee; padding: 40px; border-radius: 12px; transition: 0.3s; }
.check-card:hover { border-color: #0171e9; box-shadow: 0 15px 30px rgba(1, 113, 233, 0.1); }

.check-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.category { font-size: 13px; color: #0171e9; font-weight: 700; background: #f0f7ff; padding: 4px 12px; border-radius: 50px; }
.icon { font-size: 30px; }

h4 { font-size: 20px; font-weight: 700; margin-bottom: 10px; color: #222; }
p { color: #888; font-size: 15px; margin-bottom: 30px; }

.start-btn { width: 100%; padding: 15px; background: #fff; border: 1px solid #ddd; border-radius: 8px; font-weight: 600; color: #555; cursor: pointer; transition: 0.2s; }
.start-btn:hover { background: #0171e9; color: #fff; border-color: #0171e9; }
</style>