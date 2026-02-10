<template>
  <div class="sub-page-container">
    <div class="page-header"><h2>진료 절차</h2></div>
    <div class="step-process">
      <div class="step">1. 접수/수납</div>
      <div class="arrow">▶</div>
      <div class="step">2. 진료과 방문</div>
      <div class="arrow">▶</div>
      <div class="step">3. 검사/처방</div>
      <div class="arrow">▶</div>
      <div class="step">4. 귀가/입원</div>
    </div>
  </div>
</template>
<style scoped>
.sub-page-container { max-width: 900px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 60px; }
.step-process { display: flex; justify-content: center; align-items: center; gap: 20px; }
.step { background: #fff; border: 2px solid #0171e9; color: #0171e9; padding: 20px 30px; border-radius: 50px; font-weight: 700; }
.arrow { color: #ccc; font-size: 20px; }
</style>