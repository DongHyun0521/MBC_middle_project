<template>
  <div class="banner-wrapper">
    <section class="hero-section">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h2 class="hero-title">당신의 건강과 편안한 방문을 위해</h2>
        <p class="hero-subtitle">최고의 의료진이 365일 함께합니다</p>
      </div>
    </section>

    <section class="search-bar-section">
      <div class="search-inner">
        <span class="search-label">통합검색</span>
        <div class="search-input-box">
          <input type="text" v-model="totalSearchQuery" placeholder="검색어를 입력하세요 (의료진, 진료과 등)" @keyup.enter="onSearch">
          <button class="search-btn" @click="onSearch">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const totalSearchQuery = ref('')

const onSearch = () => {
  if (!totalSearchQuery.value.trim()) {
    alert('검색어를 입력해 주세요')
    return
  }
  console.log('검색 실행:', totalSearchQuery.value)
}
</script>

<style scoped>
.banner-wrapper {
  width: 100%;
}

.hero-section {
  width: 100%;
  height: 450px;
  position: relative;
  display: flex;
  align-items: center;
  padding: 0 10%;
  overflow: hidden;
  background-image: url('@/assets/MainImg.jpg');
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-title {
  color: white;
  font-size: 32px;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 15px;
}

.hero-subtitle {
  color: white;
  font-size: 20px;
  font-weight: 400;
  text-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
}

.search-bar-section {
  width: 100%;
  background-color: #15487e;
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.search-inner {
  width: 100%;
  max-width: 1100px;
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 0 20px;
}

.search-label {
  color: white;
  font-size: 18px;
  font-weight: 500;
  white-space: nowrap;
}

.search-input-box {
  position: relative;
  flex: 1;
}

.search-input-box input {
  width: 100%;
  padding: 12px 25px;
  padding-right: 60px;
  border-radius: 40px;
  border: none;
  font-size: 16px;
  outline: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.search-btn {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  background: #007bff;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}

.search-btn:hover {
  background: #0056b3;
}
</style>