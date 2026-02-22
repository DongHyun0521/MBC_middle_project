<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>오시는 길</h2>
      <p>S-HOSPITAL로 오시는 빠르고 편한 길을 안내해 드립니다</p>
    </div>

    <div class="map-container-wrap">
      <div id="kakao-map" ref="mapContainer" class="kakao-map-box"></div>

      <div class="map-info-card">
        <div class="info-item">
          <span class="label">주소</span>
          <p class="addr">경기도 성남시 수정구 금토로80번길 56, 위든타워 C동 서울에스병원</p>
        </div>
        <div class="info-item">
          <span class="label">대표전화</span>
          <p class="tel">1588-8282</p>
        </div>
      </div>
    </div>

    <div class="transport-info">
      <section class="info-section">
        <h3 class="sec-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
            style="margin-right:10px">
            <rect x="7" y="5" width="10" height="14" rx="2" ry="2" />
            <path d="M7 11h10M9 15h0M15 15h0" />
          </svg>
          지하철 이용 시
        </h3>
        <div class="route-box">
          <p class="route-main">신분당선 · 경강선 <b class="point-blue">판교역</b> 하차</p>
          <p class="route-sub">2번 출구(동편) 육교 건너편 버스 정류장에서 310번, 55번 환승</p>
        </div>
      </section>

      <section class="info-section">
        <h3 class="sec-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
            style="margin-right:10px">
            <path
              d="M19 17h2l.64-2.54c.24-.95.36-1.92.36-2.9V10c0-1.1-.9-2-2-2H4c-1.1 0-2 .9-2 2v1.56c0 .98.12 1.95.36 2.9L3 17h2" />
            <path d="M7 11h10M5 17v2a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-2M15 17v2a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-2" />
          </svg>
          버스 이용 시
        </h3>
        <div class="route-box">
          <p class="route-main"><b class="point-blue">위든타워</b> 정류장 하차</p>
          <ul class="bus-list">
            <li><span>일반버스</span> 310, 382</li>
            <li><span>마을버스</span> 55</li>
          </ul>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

const mapContainer = ref(null);

onMounted(() => {
  if (window.kakao && window.kakao.maps) {
    window.kakao.maps.load(() => {
      initMap();
    });
  } else {
    console.error("카카오 지도 스크립트 로드 실패");
  }
});

const initMap = () => {
  const container = mapContainer.value;
  const options = {
    center: new window.kakao.maps.LatLng(37.407668, 127.0901995), 
    level: 3
  };

  const map = new window.kakao.maps.Map(container, options);

  // 럭셔리 마커 추가
  const markerPosition = new window.kakao.maps.LatLng(37.407668, 127.0901995);
  const marker = new window.kakao.maps.Marker({
    position: markerPosition
  });
  marker.setMap(map);
};
</script>

<style scoped>
/* =====================================================================
   [1] 레이아웃
   ===================================================================== */
.sub-page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 80px 20px;
  font-family: 'Pretendard', sans-serif !important;
}

.page-header {
  text-align: center;
  margin-bottom: 60px
}

.page-header h2 {
  font-size: 42px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
}

/* 스타일 가이드 반영 */
.page-header p {
  font-size: 18px;
  color: #666;
}

/* =====================================================================
   [2] 지도 영역 디자인 (Kakao Map Box)
   ===================================================================== */
.map-container-wrap {
  position: relative;
  margin-bottom: 50px;
  overflow: hidden;
  border: 1px solid #bcbcbc9f;
  /* 은은한 구분선 컬러 */
}

.kakao-map-box {
  width: 100%;
  height: 500px;
  background: #f9fbff;
}

.map-info-card {
  position: absolute;
  bottom: 30px;
  left: 30px;
  background: #fff;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-top: 4px solid #005baa;
  /* 메인 블루 포인트 */
  z-index: 10;
}

.info-item {
  margin-bottom: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  font-size: 16px;
  color: #84c2e6;
  font-weight: 700;
  display: block;
  margin-bottom: 5px;
}

/* 서브 블루 활용 */
.info-item p {
  font-size: 18px;
  color: #333;
  font-weight: 500;
  margin: 0;
}

/* =====================================================================
   [3] 교통 안내 섹션 디자인
   ===================================================================== */
.transport-info {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.info-section {
  padding: 40px;
  background: #fff;
  border: 1px solid #bcbcbc9f;
}

.sec-title {
  display: flex;
  align-items: center;
  font-size: 26px;
  font-weight: 700;
  /* 스타일 가이드 반영 */
  color: #333;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.route-main {
  font-size: 20px;
  color: #333;
  font-weight: 600;
  margin-bottom: 10px;
}

.route-sub {
  font-size: 18px;
  color: #666;
  line-height: 1.6;
}

.point-blue {
  color: #005baa;
}

/* 메인 블루 활용 */

/* 버스 리스트 디자인 */
.bus-list {
  list-style: none;
  padding: 0;
  margin-top: 15px;
}

.bus-list li {
  font-size: 18px;
  color: #555;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.bus-list li span {
  background: #f4f6f8;
  color: #888;
  font-size: 14px;
  font-weight: 700;
  padding: 4px 10px;
  margin-right: 12px;
  min-width: 70px;
  text-align: center;
}
</style>