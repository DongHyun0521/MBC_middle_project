<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>질환백과</h2>
      <p>궁금한 질환에 대한 정확한 정보를 검색해 보세요.</p>
    </div>

    <div class="search-bar-wrap">
      <input type="text" placeholder="질환명을 입력해 주세요" class="search-input">
      <button class="search-btn">🔍</button>
    </div>

    <div class="alphabet-index">
      <span v-for="char in indexList" :key="char" class="idx-item">{{ char }}</span>
    </div>

    <div class="disease-grid">
      <div class="disease-card" v-for="item in commonDiseases" :key="item">
        <div class="disease-icon">🩺</div>
        <p class="disease-name">{{ item }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const indexList = ['ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ', 'A-Z'];
const commonDiseases = ['고혈압', '당뇨병', '위염', '아토피 피부염', '백내장', '공황장애', '디스크', '협심증', '비염', '천식', '간염', '골다공증'];
</script>

<style scoped>
.sub-page-container { max-width: 1100px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 50px; }
.page-header h2 { font-size: 32px; font-weight: 700; color: #111; }

.search-bar-wrap { max-width: 600px; margin: 0 auto 40px; display: flex; border: 2px solid #005baa; border-radius: 4px; overflow: hidden; }
.search-input { flex: 1; padding: 15px; border: none; outline: none; font-size: 16px; }
.search-btn { background: #005baa; border: none; padding: 0 25px; color: #fff; cursor: pointer; }

.alphabet-index { display: flex; justify-content: center; gap: 10px; flex-wrap: wrap; margin-bottom: 50px; }
.idx-item { padding: 8px 12px; border: 1px solid #eee; font-size: 14px; color: #666; cursor: pointer; border-radius: 4px; }
.idx-item:hover { background: #f0f7ff; color: #005baa; border-color: #005baa; }

.disease-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.disease-card { border: 1px solid #eee; padding: 30px 20px; text-align: center; border-radius: 8px; transition: 0.2s; cursor: pointer; background: #fff; }
.disease-card:hover { transform: translateY(-5px); border-color: #005baa; box-shadow: 0 10px 20px rgba(0,0,0,0.05); }
.disease-icon { font-size: 30px; margin-bottom: 15px; }
.disease-name { font-weight: 600; color: #333; }
</style>