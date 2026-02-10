<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>인사말</h2>
      <p>S-HOSPITAL 병원장의 인사말을 전합니다.</p>
    </div>

    <div class="greeting-content">
      <div class="ceo-img-box">
        <div class="placeholder-img">
          <span>Hospital Director Image</span>
        </div>
      </div>
      <div class="text-box">
        <h3 class="slogan">"생명을 최우선으로,<br>환자 중심의 치유를 실천합니다."</h3>
        <p class="desc">
          안녕하십니까, 서울에스병원 병원장 <strong>김에스</strong>입니다.<br><br>
          저희 병원은 지난 50여 년간 '사랑과 봉사'의 정신을 바탕으로 지역 사회와 함께 성장해 왔습니다.
          단순히 질병을 치료하는 공간을 넘어, 환자분들의 마음까지 어루만지는 따뜻한 치유의 공간이 되고자 합니다.<br><br>
          최첨단 의료 장비와 우수한 의료진을 갖추고, 세계적인 수준의 진료 시스템을 구축하여
          가장 신뢰받는 병원, 가장 안전한 병원이 될 것을 약속드립니다.<br><br>
          여러분의 건강한 삶을 위해 서울에스병원의 모든 의료진은 오늘도 최선을 다하겠습니다.<br>
          감사합니다.
        </p>
        <div class="sign-area">
          <span class="position">S-HOSPITAL 병원장</span>
          <span class="name">김 에 스</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sub-page-container { max-width: 1200px; margin: 0 auto; padding: 80px 20px; }
.page-header { text-align: center; margin-bottom: 60px; }
.page-header h2 { font-size: 36px; font-weight: 700; color: #111; margin-bottom: 10px; }
.page-header p { font-size: 16px; color: #666; }

.greeting-content { display: flex; gap: 60px; align-items: flex-start; }
.ceo-img-box { flex: 1; max-width: 400px; }
.placeholder-img { width: 100%; height: 500px; background-color: #f4f4f4; display: flex; align-items: center; justify-content: center; color: #aaa; border-radius: 4px; }

.text-box { flex: 1.5; padding-top: 20px; }
.slogan { font-size: 32px; font-weight: 300; line-height: 1.4; color: #0171e9; margin-bottom: 30px; border-left: 5px solid #333; padding-left: 20px; }
.desc { font-size: 16px; line-height: 1.8; color: #444; text-align: justify; margin-bottom: 50px; }
.sign-area { text-align: right; border-top: 1px solid #ddd; padding-top: 20px; }
.position { font-size: 15px; color: #666; margin-right: 15px; }
.name { font-size: 24px; font-weight: 700; color: #333; }
</style>