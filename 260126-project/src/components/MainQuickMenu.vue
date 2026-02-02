<template>
  <div class="quick-menu-container">
    <div class="card-row">
      <div class="q-card" @click="$emit('reserve')">
        <div class="card-icon-img">📅</div>
        <h4>진료예약</h4>
        <span class="hover-tip">바로가기 ></span>
      </div>
      <div class="q-card" @click="$emit('doctor')">
        <div class="card-icon-img">👨‍⚕️</div>
        <h4>의료진찾기</h4>
        <span class="hover-tip">바로가기 ></span>
      </div>
      <div class="q-card" @click="$emit('dept')">
        <div class="card-icon-img">🩺</div>
        <h4>진료과찾기</h4>
        <span class="hover-tip">바로가기 ></span>
      </div>
      <div class="q-card" @click="$emit('check')">
        <div class="card-icon-img">🔍</div>
        <h4>예약조회</h4>
        <span class="hover-tip">바로가기 ></span>
      </div>
    </div>

    <div class="util-btn-row">
      <button class="util-btn main-blue">
        <div class="btn-left">
          <span class="btn-txt">증명서(진단서, 소견서 등) 출력</span>
        </div>
        <div class="btn-right">
          <span class="btn-icon">📄</span>
          <span class="arrow">→</span>
        </div>
      </button>

      <button class="util-btn sky-blue">
        <div class="btn-left">
          <span class="btn-txt emphasis">건강검진 안내 (예약/결과)</span>
        </div>
        <div class="btn-right">
          <span class="btn-icon">🏥</span>
          <span class="arrow dark">→</span>
        </div>
      </button>

      <button class="util-btn main-blue">
        <div class="btn-left">
          <span class="btn-txt">입/퇴원 안내 및 서류 발급</span>
        </div>
        <div class="btn-right">
          <span class="btn-icon">📋</span>
          <span class="arrow">→</span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue' // 💡 온마운티드 추가 🪄

defineEmits(['reserve', 'doctor', 'dept', 'check'])

// ❌ [스크롤 버그 해결] 진입 시 화면을 맨 위로 쥰나 강제로 올림 🚗
onMounted(() => {
  window.scrollTo(0, 0)
})
</script>

<style scoped>
/* 📌 공주님 원본 CSS 100% 보존 (절대 안 건드림 🧩) */
.quick-menu-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 🏢 상단 카드 스타일 🧩 */
.card-row { display: flex; gap: 20px; width: 100%; }
.q-card {
  flex: 1; 
  background: #ffffff; 
  height: 200px; 
  border-radius: 12px;
  border: 1px solid #f0f0f0; 
  box-shadow: 0 4px 12px rgba(0,0,0,0.03); 
  display: flex; 
  flex-direction: column;
  justify-content: center; 
  align-items: center; 
  cursor: pointer; 
  position: relative;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
}

.q-card h4 { 
  font-size: 18px; 
  font-weight: 500; 
  color: #4e4e4e; 
  margin-top: 15px;
  transition: 0.3s;
}

.q-card:hover {
  border-color: #0171e9;
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(1, 113, 233, 0.1);
}

.q-card:hover h4 { color: #0171e9; }

.hover-tip {
  position: absolute;
  bottom: -20px;
  font-size: 11px;
  color: #0171e9;
  font-weight: 600;
  transition: 0.3s;
  opacity: 0;
}

.q-card:hover .hover-tip {
  bottom: 20px;
  opacity: 1;
}

.card-icon-img { font-size: 36px; }

/* 🪄 하단 유틸리티 버튼 🚗 */
.util-btn-row { display: flex; gap: 15px; width: 100%; }

.util-btn {
  flex: 1;
  height: 90px;
  border-radius: 10px;
  border: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.btn-txt {
  color: #ffffff;
  font-size: 16px;
  font-weight: 400;
  text-align: left;
}

.btn-txt.emphasis { color: #404347; font-weight: 500; }

.btn-right { display: flex; align-items: center; gap: 10px; }
.btn-icon { font-size: 24px; opacity: 0.9; transition: 0.3s; }

.arrow {
  color: #ffffff;
  font-size: 18px;
  font-weight: 300;
  opacity: 0.5;
  transition: 0.3s;
}
.arrow.dark { color: #404347; }

.main-blue { background-color: #0171e9; }
.sky-blue { background-color: #a1d8f3; }

.util-btn:hover {
  transform: scale(1.02);
  filter: brightness(1.08);
  box-shadow: 0 8px 20px rgba(1, 113, 233, 0.2);
}

.util-btn:hover .arrow {
  opacity: 1;
  transform: translateX(5px);
}

.util-btn:hover .btn-icon {
  transform: scale(1.1);
}
</style>