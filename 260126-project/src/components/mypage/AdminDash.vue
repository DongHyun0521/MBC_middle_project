<template>
    <div class="admin-dash-wrapper">
        <div v-if="currentView === 'dash'" class="dash-home-grid">
            <section class="dash-card profile-card">
                <div class="card-head">
                    <h3>내 정보 요약</h3>
                </div>
                <div class="info-list">
                    <div class="info-item"><span class="label">소속</span>
                        <p class="val">{{ userInfo.adminDeptName || '행정지원팀' }}</p>
                    </div>
                    <div class="info-item"><span class="label">전화번호</span>
                        <p class="val">{{ formatPhone(userInfo.phoneNumber) }}</p>
                    </div>
                    <div class="info-item"><span class="label">생년월일</span>
                        <p class="val">{{ formatDate(userInfo.birthday) }}</p>
                    </div>
                    <div class="info-item"><span class="label">이메일</span>
                        <p class="val">{{ userInfo.email }}</p>
                    </div>
                    <div class="info-item"><span class="label">직책</span>
                        <p class="val">{{ userInfo.rank || '직원' }}</p>
                    </div>
                </div>
            </section>

            <section class="dash-card">
                <div class="card-head">
                    <h3>긴급 업무 현황</h3>
                </div>
                <div class="stat-grid">
                    <div v-if="userInfo.isWonmu" class="stat-box red" @click="currentView = 'admin_voc'">
                        <span>{{vocList.filter(v => !v.answerStatus).length}}건</span><br>VOC 미답변
                    </div>
                    <div v-if="userInfo.adminDeptName === '홍보팀'" class="stat-box blue"
                        @click="currentView = 'admin_event'">
                        <span>{{ hospitalEvents.length }}건</span><br>이달의 행사
                    </div>
                    <div class="stat-box" @click="currentView = 'admin_todo'">
                        <span>{{todoList.filter(t => !t.done).length}}건</span><br>미완료 업무
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

        <div v-if="currentView === 'admin_todo'" class="view-section">
            <div class="split-view">
                <div class="section-card flex-calendar">
                    <div class="card-head">
                        <h3>개인 업무 달력</h3>
                    </div>
                    <div class="calendar-wrap mini">
                        <div class="cal-header">
                            <div class="month-selector">
                                <button @click="changeMonth(-1)">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M15 18l-6-6 6-6" />
                                    </svg>
                                </button>
                                <span style="font-size: 20px; font-weight: 700;">{{ calYear }}년{{ calMonth }}월</span>
                                <button @click="changeMonth(1)">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M9 18l6-6-6-6" />
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="cal-grid">
                            <div v-for="d in calendarDays" :key="d.fullDate"
                                :class="['cal-cell', { 'today': isToday(d.fullDate) }]">
                                <span class="day-num">{{ d.day }}</span>

                                <div class="cal-events">
                                    <div v-for="(evt, idx) in getEventsForDate(d.fullDate)" :key="idx"
                                        class="event-bar orange-bar">
                                        {{ evt.title }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="section-card flex-todo">
                    <div class="card-head">
                        <h3>To-Do List</h3>
                    </div>
                    <div class="todo-input-box">
                        <input v-model="newTodo" @keyup.enter="addTodo" placeholder="업무 내용을 입력하세요" />
                        <button @click="addTodo">추가</button>
                    </div>
                    <ul class="todo-list">
                        <li v-for="(todo, i) in todoList" :key="i">
                            <input type="checkbox" v-model="todo.done" />
                            <span :class="{ done: todo.done }">{{ todo.text }}</span>
                            <button @click="removeTodo(i)" class="btn-del-x">×</button>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div v-if="currentView === 'admin_event'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>병원 행사 일정 관리</h3>
                </div>
                <div class="calendar-wrap full">
                    <div class="cal-header">
                        <div class="month-selector">
                            <button @click="changeMonth(-1)">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2">
                                    <path d="M15 18l-6-6 6-6" />
                                </svg>
                            </button>
                            <h4>{{ calYear }}년 {{ calMonth }}월 전체 행사 일정</h4>
                            <button @click="changeMonth(1)">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2">
                                    <path d="M9 18l6-6-6-6" />
                                </svg>
                            </button>
                        </div>
                    </div>
                    <div class="cal-grid">
                        <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day
                        }}</div>
                        <div v-for="(date, idx) in calendarDays" :key="idx"
                            :class="['cal-cell', { 'diff-month': !date.isCurrentMonth }]">
                            <span class="day-num">{{ date.day }}</span>
                            <div class="cal-events">
                                <div v-for="evt in getHospitalEvents(date.fullDate)" :key="evt.id"
                                    class="event-bar blue-bar">
                                    {{ evt.title }}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="currentView === 'admin_voc'" class="view-section">
            <div class="section-card">
                <div class="card-head">
                    <h3>VOC 관리</h3>
                    <div class="filter-tabs">
                        <button v-for="f in ['전체', '미답변', '답변완료', '휴지통']" :key="f"
                            :class="['filter-btn', { active: vocFilter === f }]" @click="vocFilter = f">{{ f }}</button>
                    </div>
                </div>
                <table class="hospital-tbl">
                    <thead>
                        <tr>
                            <th>상태</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th class="txt-center">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="voc in filteredVocList" :key="voc.vocId">
                            <td><span :class="voc.answerStatus ? 'badge-gray' : 'red-alert'">{{ voc.answerStatus ?
                                    '답변완료' : '미답변' }}</span></td>
                            <td class="txt-left">{{ voc.title }}</td>
                            <td>{{ voc.writerName }}</td>
                            <td>{{ formatDate(voc.writeDate) }}</td>
                            <td class="txt-center">
                                <button v-if="!voc.answerStatus" class="btn-action-small"
                                    @click="goToVocReply(voc.vocId)">답변하기</button>
                                <span v-else>-</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!--[일반회원 화면] 내 진료 예약 내역 -->
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
                            <td class="txt-center"><button v-if="res.reservation_status === '예약'"
                                    class="btn-cancel-table" @click="cancelRes(res.reservation_id)">예약취소</button><span
                                    v-else>-</span></td>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getMyResReq, cancelResReq } from '@/api/reservation'
import { getAdminVocListReq } from '@/api/customer'

const props = defineProps({
    userInfo: Object,
    currentView: String
})
const router = useRouter()
const route = useRoute()

// [데이터 주머니] 
const myReservations = ref([])   // 내 개인 예약 목록
const vocList = ref([])          // VOC 리스트
const vocFilter = ref('전체')     // VOC 상태 필터 (전체/미답변/답변완료/휴지통)
const resFilter = ref('전체')     // 내 예약 필터

// [달력 상태]
const calYear = ref(new Date().getFullYear())
const calMonth = ref(new Date().getMonth() + 1)

// ======================================= 행정팀 전용 가짜 데이터 (Mock) =======================================
// To-Do List
const newTodo = ref('')
const todoList = ref([
    { text: '2026년 상반기 예산 보고서 검토', done: false },
    { text: '원내 보안 점검 및 비상 연락망 현행화', done: true },
    { text: '행정팀 주간 회의 주재 (14:00)', done: false },
    { text: '홍보용 병원 브로슈어 시안 승인', done: false },
    { text: '신규 비품 구매 기안 결재', done: true },
    { text: '신규 의료장비 도입 관련 기안 작성', done: false },
    { text: '부서별 보안 점검 리스트 수거', done: false },
    { text: '3월 병원 홍보 영상 시안 확인', done: false },
    { text: '원내 식당 위생 점검 결과 보고', done: true },
    { text: '주차 시스템 정기 점검 공지 발송', done: false },
    { text: '신규 채용 간호사 오리엔테이션 자료 준비', done: false },
    { text: '전체 의료진 소방 안전 교육 명단 확인', done: false },
    { text: '병원 홈페이지 고객 후기 모니터링', done: true },
    { text: '연말 정산 관련 안내문 게시', done: true },
    { text: '노후 PC 교체 사업 업체 미팅', done: false }
])

// 개인 업무 일정
const personalEvents = ref([
    { date: '2026-02-10', title: '부서장 회의' },
    { date: '2026-02-15', title: '연차' },
    { date: '2026-02-21', title: '외부 교육' },
    { date: '2026-02-24', title: '시스템 점검' },
    { date: '2026-02-27', title: '월간 성과 보고' },
    { date: '2026-03-05', title: '팀 워크샵' },
    { date: '2026-03-12', title: '분기 결산 보고' },
    { date: '2026-03-18', title: '타 부서 협조 미팅' },
    { date: '2026-03-25', title: '인사 고과 면담' },
    { date: '2026-04-01', title: '경영진 미팅' },
    { date: '2026-04-08', title: '개인 건강검진' },
    { date: '2026-04-15', title: '오전 반차' },
    { date: '2026-04-22', title: '총무팀 합동 점검' }
])

// 병원 전체 행사 일정
const hospitalEvents = ref([
    { id: 1, date: '2026-02-14', title: '사랑나눔 헌혈 캠페인' },
    { id: 2, date: '2026-02-21', title: '건강세미나 (심혈관)' },
    { id: 3, date: '2026-02-28', title: '2월 우수사원 시상' },
    { id: 4, date: '2026-03-02', title: '개원기념 축하 행사' },
    { id: 5, date: '2026-03-10', title: '지역아동센터 의료 봉사' },
    { id: 6, date: '2026-03-15', title: '신규 입사자 전산 교육' },
    { id: 7, date: '2026-03-20', title: '춘계 환우 힐링 콘서트' },
    { id: 8, date: '2026-03-27', title: '환자 안전의 날 캠페인' },
    { id: 9, date: '2026-04-05', title: '식목일 기념 병원 정원 가꾸기' },
    { id: 10, date: '2026-04-12', title: '무료 치매 예방 검진 사업' },
    { id: 11, date: '2026-04-20', title: '의료 봉사 활동 (남양주)' },
    { id: 12, date: '2026-04-28', title: '분기별 화재 대피 훈련' }
])

// ======================================= 가공 데이터 로직 (Computed) =======================================
// [원무팀] VOC 필터링 로직
const filteredVocList = computed(() => {
    if (vocFilter.value === '전체') return vocList.value;
    if (vocFilter.value === '미답변') return vocList.value.filter(v => !v.answerStatus);
    if (vocFilter.value === '답변완료') return vocList.value.filter(v => v.answerStatus);
    return []; // 휴지통 로직 필요시 추가
});

// [홍보팀] 달력 날짜별 행사 찾기
const getHospitalEvents = (dateStr) => hospitalEvents.value.filter(e => e.date === dateStr);

// [공통] 개인 일정 점 표시용
const getEventsForDate = (str) => {
    if (!str) return [];
    const cleanTarget = str.replace(/-/g, '').substring(0, 8);
    return personalEvents.value.filter(e => {
        const eDate = String(e.date).replace(/-/g, '').substring(0, 8);
        return eDate === cleanTarget;
    });
};

// [개인] 내 예약 필터링 & D-Day
const filteredMyReservations = computed(() => {
    if (resFilter.value === '전체') return myReservations.value;
    return myReservations.value.filter(r => r.reservation_status === resFilter.value);
});
const upcomingRes = computed(() => myReservations.value.find(r => r.reservation_status === '예약'));

// [달력] 날짜 생성 로직
const calendarDays = computed(() => {
    const year = calYear.value;
    const month = calMonth.value - 1;
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const days = [];

    // 첫째 주 빈칸 채우기
    for (let i = 0; i < firstDay.getDay(); i++) days.push({ day: '', isCurrentMonth: false });
    // 날짜 채우기
    for (let d = 1; d <= lastDay.getDate(); d++) {
        const mm = month + 1 < 10 ? '0' + (month + 1) : (month + 1);
        const dd = d < 10 ? '0' + d : d;
        days.push({ day: d, isCurrentMonth: true, fullDate: `${year}-${mm}-${dd}` });
    }
    return days;
});
// ======================================= 기능 및 서버 통신 =======================================

// [VOC] 답변하기 버튼 클릭 시 이동
const goToVocReply = (vocId) => {
    router.push({
        path: '/voc',
        query: { filter: 'unanswered', targetId: vocId }
    });
};

// [To-Do] 관리
const addTodo = () => { if (newTodo.value.trim()) { todoList.value.unshift({ text: newTodo.value, done: false }); newTodo.value = ''; } };
const removeTodo = (idx) => { if (confirm("삭제하시겠습니까?")) todoList.value.splice(idx, 1); };

// [공통] 달력 월 이동
const changeMonth = (delta) => {
    let newMonth = calMonth.value + delta;
    if (newMonth > 12) { calYear.value++; newMonth = 1; }
    else if (newMonth < 1) { calYear.value--; newMonth = 12; }
    calMonth.value = newMonth;
};

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

// [API] VOC 전체 목록
const fetchVocList = async () => {
    try {
        const res = await getAdminVocListReq('all');
        vocList.value = res.data || [];
    } catch (e) {
        console.error("VOC 로딩 실패", e);
    }
};

// [유틸]
const formatPhone = (phone) => {
    if (!phone) return '-';
    const clean = String(phone).replace(/[^0-9]/g, '');
    if (clean.length === 11) {
        return clean.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    } else if (clean.length === 10) {
        return clean.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }
    return phone;
};

const formatDate = (date) => {
    if (!date) return '-'
    const s = String(date)

    // 1. YYYYMMDD 형태인 경우 (예: 20260208)
    if (s.length === 8) {
        return `${s.substring(0, 4)}년 ${s.substring(4, 6)}월 ${s.substring(6, 8)}일`
    }

    // 2. 날짜 객체나 ISO 스트링인 경우 (예: 2026-02-08T...)
    try {
        const d = new Date(date)
        const year = d.getFullYear()
        const month = d.getMonth() + 1
        const day = d.getDate()

        // 월/일이 10보다 작을 때 앞에 0을 붙이기 위함
        const mm = month < 10 ? `0${month}` : month
        const dd = day < 10 ? `0${day}` : day

        return `${year}년 ${mm}월 ${dd}일`
    } catch (e) {
        return s
    }
}

const calculateDday = (dateStr) => {
    // 예: dateStr = "20260206" (YYYYMMDD)
    if (!dateStr) return 0;

    // 날짜 변환 (YYYY-MM-DD로 만들기)
    const formattedDate = String(dateStr).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3');
    // 결과: "2026-02-06"

    // Date 객체 생성
    const targetDate = new Date(formattedDate);
    const today = new Date();

    // 차이 계산 (밀리초 단위)
    const diff = targetDate - today;

    // 밀리초 -> '일(Day)' 단위로 변환
    // Math.ceil : 소수점 올림 처리
    return Math.ceil(diff / (1000 * 3600 * 24));
};

const isToday = (str) => {
    const t = new Date();
    const d = new Date(str);
    return t.toDateString() === d.toDateString();
};

onMounted(() => {
    fetchVocList()         // 고객의 소리 리스트
    fetchReservations()    // 개인 예약 로드
})

</script>

<style scoped>
/*  기본 레이아웃 */
.admin-dash-wrapper {
    width: 100%;
}

.dash-home-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 40px;
    margin-top: 10px;
}

.dash-card,
.section-card {
    background: #fff;
    border: 1px solid #eee;
    padding: 40px;
    margin-bottom: 20px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/*  폰트 및 컬러*/
.card-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
}

.card-head h3 {
    padding: 8px;
    font-size: 1.7rem;
    font-weight: 800;
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

/*  정보 요약 및 기타 유틸리티 */
.info-list .info-item {
    font-size: 17px;
    margin-bottom: 12px;
}

.info-item .label {
    width: 100px;
    color: #888;
    font-weight: 700;
}

/*  긴급 업무 현황 (Stat Box) */
.stat-grid {
    display: flex;
    gap: 20px;
}

.stat-box {
    flex: 1;
    padding: 30px 20px;
    background: #f8f9fa;
    border: 1px solid #eee;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #666;
    cursor: pointer;
    transition: 0.2s;
}

.stat-box:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-box span {
    display: block;
    font-size: 40px;
    font-weight: 800;
    margin-bottom: -12px;
}

.stat-box.red span {
    color: #dc3545;
}

.stat-box.blue span {
    color: #005baa;
}

/* 달력 디자인 (중앙 정렬 & 트렌디 버튼) */
.cal-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
    position: relative;
    padding: 10px 0;
}

.month-selector {
    display: flex;
    align-items: center;
    gap: 40px;
}

.month-selector h4 {
    font-size: 26px;
    font-weight: 800;
    color: #333;
    margin: 0;
}

.month-selector button {
    background: #fff;
    border: 1px solid #eee;
    border-radius: 4px;
    padding: 10px;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    color: #555;
}

.month-selector button:hover {
    background: #f8f9fa;
    border-color: #005baa;
    color: #005baa;
    transform: translateY(-2px);
}

.cal-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    background-color: #eee;
    gap: 1px;
    border: 1px solid #eee;
}

.cal-day-head {
    background: #f8f9fa;
    padding: 15px;
    text-align: center;
    font-weight: 700;
    font-size: 16px;
}

.cal-cell {
    background: #fff;
    min-height: 120px;
    padding: 15px;
}

.calendar-wrap.mini .cal-cell {
    min-height: 80px;
}

.day-num {
    font-size: 16px;
    font-weight: 600;
    color: #444;
}

/*  필터 탭 */
.filter-tabs {
    display: flex;
    gap: 12px;
    margin-bottom: 15px;
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

.filter-btn.active {
    background-color: #005baa;
    color: #fff;
    border-color: #005baa;
}


/* 병원 공통 테이블 */
.hospital-tbl {
    width: 100%;
    border-collapse: collapse;
}

.hospital-tbl th {
    background-color: #f8f9fa;
    padding: 30px;
    text-align: left;
    font-weight: 600;
    font-size: 18px;
    border-bottom: 2px solid #eee;
}

.hospital-tbl td {
    padding: 18px 30px;
    border-bottom: 1px solid #f0f0f0;
    font-size: 18px;
    vertical-align: middle;
}

.bold-blue {
    color: #005baa;
    font-weight: 700;
}

/* 기타 유틸리티 (D-Day, To-Do, 배지) */
.res-highlight {
    background: #f0f7ff;
    padding: 30px;
    text-align: center;
    border-radius: 8px;
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

.status-badge {
    padding: 6px 14px;
    font-size: 14px;
    font-weight: 700;
    display: inline-block;
    border-radius: 4px;
}

.status-badge.active {
    background: #e7f3ff;
    color: #005baa;
}

.status-badge.done {
    background: #f5f5f5;
    color: #999;
}

.status-badge.red {
    background: #fff1f0;
    color: #dc3545;
}

.btn-cancel-table {
    background: #fff;
    color: #888;
    border: 1px solid #ddd;
    padding: 10px 20px;
    font-weight: 600;
    cursor: pointer;
    font-size: 16px;
}

.btn-action-small {
    background: #005baa;
    color: #fff;
    border: none;
    padding: 8px 16px;
    font-weight: 700;
    font-size: 14px;
    cursor: pointer;
}

.split-view {
    display: flex;
    gap: 20px; 
    align-items: flex-start; 
}

/*  달력 영역 */
.flex-calendar {
    flex: 1.7; 
    min-width: 0; /*  내용이 넘쳐도 박스가 안 깨지게 방어 */
}

.flex-todo {
    flex: 1;
    min-width: 300px; 
}

/*  달력 셀 높이 보정 */
.calendar-wrap.mini .cal-cell {
    min-height: 100px; 
}


.todo-input-box {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.todo-input-box input {
    flex: 1;
    padding: 12px;
    border: 1px solid #ddd;
    font-size: 16px;
}

.todo-input-box button {
    padding: 0 20px;
    background: #005baa;
    color: #fff;
    border: none;
    font-weight: 700;
}

.todo-list {
    list-style: none;
    padding: 0;
}

.todo-list li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;
}

.todo-list span.done {
    text-decoration: line-through;
    color: #ccc;
}

.event-bar {
    padding: 4px 8px;
    font-size: 12px;
    font-weight: 700;
    border-radius: 4px;
    margin-top: 4px;
    white-space: nowrap;      /*  글자 줄바꿈 방지 */
    overflow: hidden;         /*  넘치는 글자 숨김 */
    text-overflow: ellipsis;  /*  말줄임표(...) 표시 */
}

/*  개인 업무 일정: 오렌지 톤 */
.orange-bar {
    background-color: #fff9e6; 
    color: #fbb900; 
    border-left: 3px solid #fbb900;
}

/*  병원 행사 일정: 블루 톤*/
.blue-bar {
    background-color: #e7f3ff;
    color: #005baa;
    border-left: 3px solid #005baa;
}

/*  달력 셀 높이 보정 (일정이 들어갈 공간 확보) */
.cal-cell {
    background: #fff;
    min-height: 120px; /* 충분한 높이 */
    padding: 15px;
    display: flex;
    flex-direction: column;
}

.calendar-wrap.mini .cal-cell {
    min-height: 100px; /* To-Do 미니 달력용 */
}

.btn-del-x {
    background: none;
    border: none;
    color: #ccc;
    font-size: 20px;
    cursor: pointer;
    margin-left: auto;
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