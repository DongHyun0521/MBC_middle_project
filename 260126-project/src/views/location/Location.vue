<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>오시는 길</h2>
      <p>S-HOSPITAL로 오시는 빠르고 편한 길을 안내해 드립니다</p>
    </div>

    <div class="map-placeholder">
      <div class="map-info">
        <p class="addr"><b>주소 :</b> 서울특별시 종로구 종로 69 S-HOSPITAL</p>
        <p class="tel"><b>대표전화 :</b> 1588-0000</p>
      </div>
    </div>

    <div class="transport-info">
      <div class="info-section">
        <h4 class="subway-title">🚇 지하철 이용 시</h4>
        <p>1호선 <b>종각역</b> 3번 출구 도보 5분</p>
        <p>1, 3, 5호선 <b>종로3가역</b> 1번 출구 도보 10분</p>
      </div>

      <div class="info-section">
        <h4 class="bus-title">🚌 버스 이용 시</h4>
        <p><b>종로2가 사거리 하차 :</b> 101, 103, 143, 150, 201, 260, 262, 270, 271, 273, 370, 470, 501, 720</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sub-page-container { max-width: 1000px; margin: 0 auto; padding: 80px 20px }
.page-header { text-align: center; margin-bottom: 50px }
.page-header h2 { font-size: 32px; font-weight: 700; color: #111 }

.map-placeholder { width: 100%; height: 450px; background: #f4f4f4; border: 1px solid #eee; position: relative; margin-bottom: 40px; border-radius: 8px }
.map-info { position: absolute; bottom: 20px; left: 20px; background: #fff; padding: 20px; border-radius: 4px; box-shadow: 0 5px 15px rgba(0,0,0,0.1) }
.map-info p { margin: 5px 0; font-size: 14px; color: #444 }

.transport-info { display: flex; flex-direction: column; gap: 30px }
.info-section { padding: 30px; border-radius: 8px; background: #fff; border: 1px solid #eee }
.info-section h4 { font-size: 18px; font-weight: 700; margin-bottom: 15px }
.subway-title { color: #0052a4; border-bottom: 2px solid #0052a4; display: inline-block }
.bus-title { color: #34b00d; border-bottom: 2px solid #34b00d; display: inline-block }
.info-section p { margin-top: 10px; color: #666; font-size: 15px }
</style>