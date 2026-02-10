<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>미션 / 비전</h2>
      <p>S-HOSPITAL이 나아가는 길과 약속입니다.</p>
    </div>

    <section class="mv-section">
      <div class="label">MISSION</div>
      <div class="content-box mission-bg">
        <h3>인류를 질병으로부터 자유롭게 한다</h3>
        <p>우리는 최상의 진료, 연구, 교육을 통해 인류의 건강과 삶의 질 향상에 기여합니다.</p>
      </div>
    </section>

    <section class="mv-section">
      <div class="label">VISION</div>
      <div class="vision-grid">
        <div class="v-card">
          <div class="v-icon">🏥</div>
          <h4>환자 중심 병원</h4>
          <p>환자의 안전과 편의를<br>최우선 가치로 삼는 병원</p>
        </div>
        <div class="v-card">
          <div class="v-icon">🔬</div>
          <h4>연구 중심 병원</h4>
          <p>끊임없는 연구로<br>의학 발전을 선도하는 병원</p>
        </div>
        <div class="v-card">
          <div class="v-icon">🌏</div>
          <h4>글로벌 리더</h4>
          <p>세계적 수준의 의료로<br>신뢰받는 글로벌 병원</p>
        </div>
      </div>
    </section>

    <section class="mv-section">
      <div class="label">CORE VALUES</div>
      <div class="core-values">
        <span>탁월성 (Excellence)</span>
        <span>존중 (Respect)</span>
        <span>혁신 (Innovation)</span>
        <span>협력 (Teamwork)</span>
      </div>
    </section>
  </div>
</template>

<style scoped>
.sub-page-container { max-width: 1000px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 70px; }
.page-header h2 { font-size: 36px; font-weight: 700; color: #111; }
.page-header p { font-size: 16px; color: #666; margin-top: 10px; }

.mv-section { margin-bottom: 80px; text-align: center; }
.label { font-size: 14px; font-weight: 800; color: #0171e9; letter-spacing: 2px; margin-bottom: 15px; }

.content-box { background: #f8f9fa; padding: 60px; border-radius: 8px; }
.mission-bg { background: linear-gradient(135deg, #0171e9 0%, #004b9e 100%); color: #fff; }
.mission-bg h3 { font-size: 32px; margin-bottom: 15px; font-weight: 700; }
.mission-bg p { font-size: 18px; opacity: 0.9; font-weight: 300; }

.vision-grid { display: flex; gap: 20px; }
.v-card { flex: 1; border: 1px solid #eee; padding: 40px 20px; border-radius: 8px; transition: 0.3s; background: #fff; }
.v-card:hover { transform: translateY(-5px); box-shadow: 0 10px 30px rgba(0,0,0,0.05); border-color: #0171e9; }
.v-icon { font-size: 40px; margin-bottom: 20px; }
.v-card h4 { font-size: 20px; font-weight: 700; color: #333; margin-bottom: 10px; }
.v-card p { font-size: 15px; color: #666; line-height: 1.5; }

.core-values { display: flex; justify-content: center; gap: 30px; flex-wrap: wrap; }
.core-values span { border: 1px solid #ddd; padding: 15px 40px; border-radius: 50px; font-weight: 600; color: #555; font-size: 16px; }
</style>