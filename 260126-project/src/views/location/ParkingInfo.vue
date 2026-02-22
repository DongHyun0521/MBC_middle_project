<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>주차 이용 안내</h2>
      <p>병원을 이용하시는 고객님의 편의를 위해 최선을 다하겠습니다</p>
    </div>

    <div class="parking-summary">
      <div class="summary-card">
        <div class="icon-box">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#84c2e6" stroke-width="2.5"
            stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <div class="txt-box">
          <span class="label">초반 30분</span>
          <span class="val">무료 주차</span>
        </div>
      </div>
      <div class="summary-card highlight">
        <div class="icon-box">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.5"
            stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </div>
        <div class="txt-box">
          <span class="label">진료 예약 완료</span>
          <span class="val">2시간 무료</span>
        </div>
      </div>
    </div>

    <section class="parking-sec">
      <h3 class="sec-title">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
          style="margin-right:8px">
          <rect x="1" y="3" width="22" height="18" rx="2" ry="2" />
          <line x1="12" y1="18" x2="12" y2="18" />
        </svg>
        주차 요금 안내
      </h3>
      <table class="parking-tbl">
        <thead>
          <tr>
            <th>구분</th>
            <th>기본 요금</th>
            <th>추가 요금 (30분당)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="bold">회원 차량</td>
            <td>초반 30분 무료</td>
            <td class="main-txt">1,000원</td>
          </tr>
          <tr>
            <td class="bold">비회원 차량</td>
            <td>초반 30분 무료</td>
            <td class="sub-txt">2,000원</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="parking-sec">
      <h3 class="sec-title">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#005baa" stroke-width="2.5"
          style="margin-right:8px">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
          <polyline points="22 4 12 14.01 9 11.01" />
        </svg>
        예약 환자 혜택
      </h3>
      <div class="benefit-box">
        <div class="b-info">
          <p class="b-title">당일 진료 예약 고객</p>
          <p class="b-desc">진료 예약 상태가 <span class="status-point">‘완료’</span>인 경우에 한함</p>
        </div>
        <div class="b-val">
          <span class="time">2</span>시간 무료
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 및 텍스트 폰트 설정 */
.sub-page-container {
  max-width: 1000px;
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
  color: #333;
  margin-bottom: 10px;
  font-weight: 700;
}

.page-header p {
  color: #666;
  font-size: 18px;
}

/* 요약 카드 구역 */
.parking-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 50px;
}

.summary-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background: #fdfdfd;
  border-radius: 12px;
  border: 1px solid #bcbcbc9f;
}

.summary-card.highlight {
  background: #005baa;
  color: #fff;
  border-color: #005baa;
}

/* 메인 블루 적용 */
.icon-box {
  width: 60px;
  height: 60px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.summary-card.highlight .icon-box {
  background: rgba(255, 255, 255, 0.15);
}

.txt-box {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 18px;
  color: #888;
  margin-bottom: 4px;
}

.summary-card.highlight .label {
  color: #84c2e6;
  font-weight: 600;
}

/* 서브 컬러 활용 */
.val {
  font-size: 24px;
  font-weight: 800;
  color: #333;
}

.summary-card.highlight .val {
  color: #fff;
}

/* 섹션 및 제목 */
.parking-sec {
  margin-bottom: 60px
}

.sec-title {
  display: flex;
  align-items: center;
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 25px
}

/* 테이블 스타일 */
.parking-tbl {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}

.parking-tbl th {
  background: #f9fbff;
  color: #333;
  font-weight: 700;
  padding: 20px;
  border-top: 3px solid #005baa;
  border-bottom: 1px solid #bcbcbc9f;
  font-size: 20px;
}

/* 상단 메인 블루 */
.parking-tbl td {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #eee;
  color: #555;
  font-size: 18px;
}

.parking-tbl td.bold {
  font-weight: 700;
  color: #333;
}

.main-txt {
  color: #005baa;
  font-weight: 800;
}

/* 회원 1,000원 강조 */
.sub-txt {
  color: #666;
}

/* 예약 혜택 박스 */
.benefit-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  background: #fff;
  border: 2px dashed #84c2e6;
  border-radius: 12px;
}

/* 서브 블루 점선 */
.b-title {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.b-desc {
  color: #888;
  font-size: 18px;
}

.status-point {
  color: #fbb900;
  font-weight: 800;
}

/* 포인트 옐로우 활용 */
.b-val {
  font-size: 24px;
  font-weight: 700;
  color: #005baa;
}

.b-val .time {
  font-size: 40px;
  font-weight: 900;
  margin-right: 4px;
}
</style>