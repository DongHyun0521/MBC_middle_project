<template>
  <div class="sub-page-container">
    <div class="page-header">
      <h2>주차 이용 안내</h2>
      <p>병원을 이용하시는 고객님의 편의를 위해 최선을 다하겠습니다</p>
    </div>

    <section class="parking-sec">
      <h3 class="sec-title"> 주차 요금 안내</h3>
      <table class="parking-tbl">
        <thead>
          <tr>
            <th>구분</th>
            <th>기본 요금</th>
            <th>추가 요금</th>
            <th>일일 최대</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>일반차량</td>
            <td>30분이내 무료</td>
            <td>10분당 500원</td>
            <td>20,000원</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="parking-sec">
      <h3 class="sec-title"> 무료 주차 안내 (환자 및 내원객)</h3>
      <table class="parking-tbl">
        <thead>
          <tr>
            <th>구분</th>
            <th>무료 주차 시간</th>
            <th>증빙 자료</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>외래 진료</td>
            <td>4시간 무료</td>
            <td>진료 예약증 / 영수증</td>
          </tr>
          <tr>
            <td>입/퇴원 당일</td>
            <td>8시간 무료</td>
            <td>입퇴원 결정서</td>
          </tr>
          <tr>
            <td>건강검진</td>
            <td>4시간 무료</td>
            <td>검진 안내문</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<style scoped>
.sub-page-container { max-width: 1000px; margin: 0 auto; padding: 80px 20px }
.page-header { text-align: center; margin-bottom: 60px }

.parking-sec { margin-bottom: 60px }
.sec-title { font-size: 20px; font-weight: 700; color: #333; margin-bottom: 20px }

.parking-tbl { width: 100%; border-collapse: collapse; background: #fff }
.parking-tbl th { background: #f8f9fa; color: #333; font-weight: 700; padding: 15px; border-top: 2px solid #043264; border-bottom: 1px solid #ddd }
.parking-tbl td { padding: 15px; text-align: center; border-bottom: 1px solid #eee; color: #666; font-size: 15px }
</style>