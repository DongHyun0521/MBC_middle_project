<template>
    <div class="member-dash-wrapper">
        <div v-if="currentView === 'dash'" class="dash-home-grid">
            <section class="dash-card profile-card">
                <div class="card-head">
                    <h3>내 정보 요약</h3>
                </div>
                <div class="info-list">
                    <div class="info-item">
                        <span class="label">주소</span>
                        <p class="val">{{ userInfo.address }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">전화번호</span>
                        <p class="val">{{ formatPhone(userInfo.phoneNumber) }}</p>
                    </div>
                    <div class="info-item">
                        <span class="label">생년월일</span>
                        <p class="val">{{ formatDate(userInfo.birthday) }}</p>
                    </div>
                    <div class="info-item"><span class="label">이메일</span>
                        <p class="val">{{ userInfo.email }}</p>
                    </div>
                </div>
            </section>

            <section class="dash-card">
                <div class="card-head">
                    <h3>나의 병원 예약</h3>
                </div>
                <div v-if="upcomingRes" class="res-highlight">
                    <span class="d-day">D-{{ calculateDday(upcomingRes.reservation_date) }}</span>
                    <p class="res-time-txt">{{ upcomingRes.reservation_time }}</p>
                    <p class="res-doc-txt">{{ upcomingRes.med_dept_name }} | {{ upcomingRes.doctor_name }} 의사</p>
                </div>
                <div v-else class="empty-res">예정된 예약이 없습니다</div>
            </section>
        </div>

        <div v-if="currentView === 'res'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>내 진료 예약 내역</h3>
                </div>
                <div class="filter-tabs">
                    <button v-for="tab in ['전체', '예약', '완료', '취소']" :key="tab"
                        :class="['filter-btn', { active: resFilter === tab }]" @click="resFilter = tab">{{ tab
                        }}</button>
                </div>
                <table class="hospital-tbl">
                    <thead>
                        <tr>
                            <th>진료과</th>
                            <th>담당의</th>
                            <th>예약일시</th>
                            <th>상태</th>
                            <th class="txt-center">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="res in filteredMyReservations" :key="res.reservation_id">
                            <td class="bold-blue">{{ res.med_dept_name }}</td>
                            <td class="bold-blue">{{ res.doctor_name }}</td>
                            <td>{{ formatDate(res.reservation_date) }}</td>
                            <td><span :class="['status-badge', res.reservation_status === '예약' ? 'active' : 'done']">{{
                                res.reservation_status
                                    }}</span></td>
                            <td class="txt-center">
                                <button v-if="res.reservation_status === '예약'" class="btn-cancel-table"
                                    @click="cancelRes(res.reservation_id)">예약취소</button>
                                <span v-else>-</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div v-if="filteredMyReservations.length === 0" class="empty-msg">
                    {{ resFilter === '전체' ? '예약 내역이 없습니다' : '해당 내역이 없습니다' }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
// ref: 데이터와 화면을 연결해서, 데이터만 바꾸면 화면도 알아서 변하도록 하는 프레임워크
// vue: 일반 데이터를 Vue가 실시간으로 감시할 수 있는 반응형 주머니에 담는 도구. 주머니 속 내용물을 꺼낼 땐 .value를 꼭 붙여야 함
// onMounted: 화면(DOM)이 그려지자마자 이것 먼저 실행하라고 시키는 시작 버튼이며, 주로 서버에서 데이터를 처음 가져올 때 사용
// computed: 결과값을 캐싱(저장)해둬서 똑같은 계산을 반복 안 함. 연결된 데이터가 바뀔 때만 다시 계산해서 효율적

import { ref, onMounted, computed } from 'vue'
import { getMyResReq, cancelResReq } from '@/api/reservation'

// Props 정의: 부모 컴포넌트(MyPage.vue)에서 넘겨받는 데이터
const props = defineProps({
    userInfo: Object,       // 로그인한 정보
    currentView: String     // 현재 클릭한 메뉴 탭 이름
})

// 서버에서 받아온 전체 예약 리스트
const myReservations = ref([])
// 현재 사용자가 선택한 필터 상태 (기본값: 전체)
const resFilter = ref('전체')

// ======================================= 가공 데이터 로직 (Computed) =======================================
// [예약 요약] 전체 목록 중 상태가 '예약'인 것들 중 가장 빠른 것 하나 찾기
const upcomingRes = computed(() =>
    myReservations.value.find(r => r.reservation_status === '예약')
)

// [목록 필터링] 사용자가 선택한 탭(resFilter)에 맞는 데이터만 걸러내기
const filteredMyReservations = computed(() => {
    if (resFilter.value === '전체') {
        return myReservations.value
    }
    return myReservations.value.filter(r => r.reservation_status === resFilter.value)
})

// ======================================= 화면 표시용 유틸리티 함수 =======================================
// [전화번호] 숫자만 있는 번호에 하이픈(-) 추가
const formatPhone = (phone) => {
    if (!phone) {
        return '-'
    }
    const clean = String(phone).replace(/[^0-9]/g, '')
    if (clean.length === 11) {
        return clean.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
    }
    if (clean.length === 10) {
        return clean.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3')
    }
    return phone
}

// [날짜] 날짜 형식 '20260222' -> '2026년 02월 22일' 형태로 변환
const formatDate = (date) => {
    if (!date) {
        return '-'
    }
    const s = String(date)
    if (s.length === 8) {
        return `${s.substring(0, 4)}년 ${s.substring(4, 6)}월 ${s.substring(6, 8)}일`
    }
    try {
        const d = new Date(date)
        const mm = d.getMonth() + 1 < 10 ? `0${d.getMonth() + 1}` : d.getMonth() + 1
        const dd = d.getDate() < 10 ? `0${d.getDate()}` : d.getDate()
        return `${d.getFullYear()}년 ${mm}월 ${dd}일`
    } catch (e) { return s }
}

// [D-Day] 예약 날짜와 오늘 날짜의 차이를 계산 (밀리초 단위 계산 후 일 단위로 변환)
const calculateDday = (dateStr) => {
    if (!dateStr) return 0
    const formattedDate = String(dateStr).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3')
    const diff = new Date(formattedDate) - new Date()
    return Math.ceil(diff / (1000 * 3600 * 24))
}

// ======================================= 서버 통신 로직 =======================================
// [조회] 내 예약 내역 서버에서 가져오기
const fetchReservations = async () => {
    try {
        const res = await getMyResReq()
        myReservations.value = res.data
    } catch (e) {
        console.error("예약 조회 실패", e)
    }
}

// [취소] 특정 예약ID를 서버에 보내 취소 처리 요청
const cancelRes = async (id) => {
    if (!confirm("정말 예약을 취소하시겠습니까?")) return
    try {
        await cancelResReq(id)
        fetchReservations() // 취소 후 목록 새로고침
        alert("예약 취소가 완료되었습니다")
    } catch (e) {
        alert("오류가 발생했습니다")
    }
}

// 화면이 열리자마자 서버에서 예약 데이터를 가져옴
onMounted(() => {
    fetchReservations()
})
</script>

<style scoped>
/* 대시보드 홈 - 그리드 레이아웃 */
.dash-home-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 40px;
    margin-top: 10px;
}

/* 섹션 카드 & 헤더 */
.dash-card,
.section-card {
    background: #ffffff;
    border: 1px solid #eee;
    padding: 40px;
    margin-bottom: 20px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.card-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.card-head h3 {
    padding: 8px;
    font-size: 1.4rem;
    font-weight: 700;
    color: #005baa;
}

.label {
    padding: 8px;
    font-size: 20px;
}

.val {
    padding: 8px 8px 16px;
    font-size: 18px;
    margin-bottom: 18px;
    border-bottom: 1px solid #f5f5f5;
}

/*  필터 버튼 그룹 */
.filter-tabs {
    padding-left: 8px;
    display: flex;
    gap: 12px;
    margin-bottom: 25px;
    padding-bottom: 15px;
    border-bottom: 1px solid #f5f5f5;
}

.filter-btn {
    padding: 10px 20px;
    background-color: #fff;
    border: 1px solid #ddd;
    color: #888;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.filter-btn:hover {
    background-color: #f8f9fa;
}

/* 활성화된 탭 (메인 블루) */
.filter-btn.active {
    background-color: #005baa;
    color: #fff;
    border-color: #005baa;
}

/*  병원 공통 테이블  */
.hospital-tbl {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

.hospital-tbl th {
    background-color: #f8f9fa;
    padding: 30px;
    text-align: left;
    font-weight: 600;
    color: #555;
    border-bottom: 2px solid #eee;
    font-size: 18px;
}

.hospital-tbl td {
    padding: 18px 30px;
    border-bottom: 1px solid #f0f0f0;
    color: #444;
    font-size: 18px;
    vertical-align: middle;
}

.hospital-tbl {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

.bold-blue {
    color: #005baa;
    font-weight: 700;
}

/*  버튼 및 배지 스타일 */
/* 관리 버튼 */
.res-cancel-btn {
    background-color: #fff;
    color: #888;
    border: 1px solid #ddd;
    padding: 10px 20px;
    font-weight: 600;
    cursor: pointer;
    font-size: 16px;
    transition: all 0.2s;
}

.res-cancel-btn:hover {
    background-color: #f8f8f8;
    color: #dc3545;
    border-color: #dc3545;
}

/* 상태 배지 */
.status-badge {
    display: inline-block;
    padding: 6px 14px;
    font-size: 14px;
    font-weight: 700;
}

.status-badge.active {
    background: #e7f3ff;
    color: #005baa;
}

.status-badge.done {
    background: #f5f5f5;
    color: #999;
}

/*  정보 요약 및 기타 유틸리티 */
.info-list .info-item {
    font-size: 17px;
    margin-bottom: 12px;
}

.info-item .label {
    width: 100px;
    color: #888;
    font-weight: 700;
}

.bold-blue {
    color: #005baa;
    font-weight: 700;
}

.txt-center {
    text-align: left;
}

/* 예약 하이라이트 (D-Day) */
.res-highlight {
    background: #f0f7ff;
    padding: 30px;
    text-align: center;
}

.d-day {
    display: inline-block;
    background: #005baa;
    color: #fff;
    padding: 12px 18px;
    font-weight: 600;
    font-size: 60px;
    margin-bottom: 20px;
}

.res-time-txt {
    font-size: 26px;
    font-weight: 700;
    color: #444;
    margin-bottom: 5px;
}

.res-doc-txt {
    font-size: 22px;
    font-weight: 500;
    color: #666;

}

/* 해당하는 내역이 없습니다 메세지 */
.empty-msg {
    font-size: 20px;
    margin-top: 30px;
    text-align: center;
}

/* 예정된 예약이 없습니다 */
.empty-res {
    font-size: 20px;
    padding: 8px;
}
</style>