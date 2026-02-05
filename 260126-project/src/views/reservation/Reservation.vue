<template>
  <div class="reservation-page-wrap">
    <div class="reservation-card">
      <div class="res-header">
        <h2>진료 예약 신청</h2>
        <div class="title-bar"></div>
        <p class="res-desc">정보를 확인하고 예약을 진행해 주세요</p>
      </div>

      <div class="res-form-body">
        <div v-if="isFixedDoc" class="fixed-doc-card transition-box">
          <div class="fixed-label">선택하신 의료진</div>
          <div class="fixed-info">
            <span class="f-dept">{{ route.query.deptName }}</span>
            <span class="f-doc">{{ route.query.docName }} 의사</span>
            <button @click="resetToGeneral" class="btn-change">변경</button>
          </div>
        </div>

        <div v-else class="input-section">
          <label>진료 정보</label>
          <div class="grid-row">
            <select v-model="form.medDeptId" @change="fetchDoctors" class="res-select">
              <option value="">진료과 선택</option>
              <option v-for="d in depts" :key="d.med_dept_id" :value="d.med_dept_id">
                {{ d.dept_name }}
              </option>
            </select>

            <select v-if="form.medDeptId" v-model="form.doctorId" @change="resetDateTime" class="res-select">
              <option value="">담당 의사 선택</option>
              <option v-for="s in doctors" :key="s.staff_id" :value="s.staff_id">
                {{ s.staff_name }} 의사
              </option>
            </select>
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.doctorId">
          <label>환자 정보 입력</label>
          <div class="grid-col-group">
            <div class="input-wrapper">
              <label class="sub-label-sm">성함</label>
              <input type="text" v-model="form.patientName" @input="handleNameInput" placeholder="한글 2자 이상 입력"
                class="res-input" :class="{ 'input-error': form.patientName && !isValidName }">
              <p v-if="form.patientName && !isValidName" class="error-msg">정확한 한글 이름을 입력해 주세요.</p>
            </div>

            <div class="input-wrapper">
              <label class="sub-label-sm">휴대폰 번호</label>
              <div class="phone-split-group">
                <select v-model="phonePart1" class="res-select phone-part">
                  <option value="010">010</option>
                  <option value="011">011</option>
                </select>
                <span class="dash">-</span>
                <input type="text" v-model="phonePart2" ref="phone2Ref" @input="handlePhonePart2" maxlength="4"
                  class="res-input phone-part center-txt">
                <span class="dash">-</span>
                <input type="text" v-model="phonePart3" ref="phone3Ref" @input="handlePhonePart3" maxlength="4"
                  class="res-input phone-part center-txt">
              </div>
              <p v-if="(phonePart2 || phonePart3) && !isValidPhone" class="error-msg">휴대폰 번호를 모두 입력해 주세요.</p>
            </div>
          </div>
        </div>

        <div class="input-section transition-box" v-if="isValidName && isValidPhone">
          <label>예약 날짜</label>
          <div class="custom-calendar-container">
            <div class="date-display-box" @click="toggleCalendar" :class="{ active: showCalendar }">
              <span v-if="form.reservationDate" class="selected-date-txt">{{ form.reservationDate }}</span>
              <span v-else class="placeholder">날짜를 선택해 주세요</span>
              <span class="calendar-icon">📅</span>
            </div>

            <div v-if="showCalendar" class="calendar-popup">
              <div class="cal-header">
                <button @click="prevMonth" class="cal-nav-btn">&lt;</button>
                <span class="cal-title">{{ currentYear }}년 {{ currentMonth }}월</span>
                <button @click="nextMonth" class="cal-nav-btn">&gt;</button>
              </div>
              <div class="cal-grid">
                <div v-for="d in ['일', '월', '화', '수', '목', '금', '토']" :key="d" class="cal-day-name">{{ d }}</div>
                <div v-for="n in startDayOfWeek" :key="'empty' + n" class="cal-day empty"></div>
                <div v-for="date in daysInMonth" :key="date" class="cal-day"
                  :class="{ 'selected': isSelected(date), 'disabled': isPastDate(date), 'today': isToday(date) }"
                  @click="selectDate(date)">
                  {{ date }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.reservationDate">
          <label>예약 시간</label>
          <div class="time-grid">
            <!-- [수정] label/radio 제거 + button disabled로 마감 처리 -->
            <button v-for="time in allTimeSlots" :key="time" type="button" class="time-btn"
              :class="{ 'selected': form.reservationTime === time }" :disabled="isBooked(time)"
              @click="form.reservationTime = time">
              <span class="time-txt">{{ time }}</span>
              <span v-if="isBooked(time)" class="booked-label">마감</span>
            </button>
          </div>
        </div>

        <button v-if="form.reservationTime" @click="submitReservation" class="res-submit-btn"
          :disabled="isBooked(form.reservationTime)">
          {{ isBooked(form.reservationTime) ? '이미 예약된 시간입니다' : '예약 확정하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDeptsReq, getDoctorsReq, addResReq, getDocSchedReq } from '@/api/reservation.js'

const route = useRoute();
const useRouterInstance = useRouter();
const depts = ref([]);
const doctors = ref([]);
const bookedSlots = ref([])
const isFixedDoc = ref(false)

const phonePart1 = ref('010')
const phonePart2 = ref('')
const phonePart3 = ref('')
const phone2Ref = ref(null)
const phone3Ref = ref(null)

const showCalendar = ref(false)
const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth() + 1)

const form = reactive({
  memId: '', medDeptId: '', doctorId: '', patientName: '', patientPhone: '',
  reservationDate: '', reservationTime: '', visitType: '초진', reservationStatus: '예약', reservationMemo: '홈페이지 예약'
})

const todayStr = now.toISOString().split('T')[0]

const daysInMonth = computed(() => new Date(currentYear.value, currentMonth.value, 0).getDate())
const startDayOfWeek = computed(() => new Date(currentYear.value, currentMonth.value - 1, 1).getDay())
const toggleCalendar = () => showCalendar.value = !showCalendar.value
const prevMonth = () => { if (currentMonth.value === 1) { currentMonth.value = 12; currentYear.value--; } else { currentMonth.value--; } }
const nextMonth = () => { if (currentMonth.value === 12) { currentMonth.value = 1; currentYear.value++; } else { currentMonth.value++; } }
const formatDateString = (day) => { const m = currentMonth.value < 10 ? `0${currentMonth.value}` : currentMonth.value; const d = day < 10 ? `0${day}` : day; return `${currentYear.value}-${m}-${d}` }
const isSelected = (day) => form.reservationDate === formatDateString(day)
const isToday = (day) => formatDateString(day) === todayStr
const isPastDate = (day) => formatDateString(day) < todayStr
const selectDate = (day) => { if (isPastDate(day)) return; form.reservationDate = formatDateString(day); showCalendar.value = false; fetchBookedSlots(); }

const allTimeSlots = computed(() => {
  const slots = []; for (let h = 9; h < 19; h++) {
    const hour = h < 10 ? `0${h}` : h
    slots.push(`${hour}:00`, `${hour}:30`)
  }
  return slots
})

const isValidName = computed(() => /^[가-힣]{2,}$/.test(form.patientName));
const handleNameInput = (e) => form.patientName = e.target.value.replace(/[^가-힣]/g, '');
const isValidPhone = computed(() => phonePart2.value.length >= 3 && phonePart3.value.length === 4);
const handlePhonePart2 = (e) => { phonePart2.value = e.target.value.replace(/[^0-9]/g, ''); if (phonePart2.value.length === 4) phone3Ref.value.focus(); }
const handlePhonePart3 = (e) => phonePart3.value = e.target.value.replace(/[^0-9]/g, '');

const isBooked = (time) => {
  return bookedSlots.value.some(bookedTime => bookedTime.trim() === time.trim());
}

const fetchBookedSlots = async () => {
  // 의사 선택 + 날짜 선택이 되어야만 "그 의사의 그 날짜" 예약 현황을 조회할 수 있음
  // 둘 중 하나라도 없으면 조회 의미가 없어서 그냥 종료
  if (!form.doctorId || !form.reservationDate) return

  try {
    // 백엔드에 "이 의사의 전체 예약 스케줄" 요청
    // (프론트는 여기서 받은 데이터로 "이미 예약된 시간"을 계산해서 버튼을 disable 처리함)
    const res = await getDocSchedReq(form.doctorId)

    // null 방어
    // 백엔드가 null을 주거나(세션 문제 등), 또는 예상치 못한 값이 올 수 있음
    // .filter를 쓰려면 반드시 배열이어야 해서, 배열 아니면 빈 배열로 처리
    const data = Array.isArray(res.data) ? res.data : []

    // 프론트에서 선택한 날짜(YYYY-MM-DD)를
    // 백엔드/DB 값이랑 비교하기 쉽게 YYYYMMDD 형태로 통일
    const targetDate = form.reservationDate.replaceAll('-', '') // ex) 2026-02-04 -> 20260204

    // bookedSlots.value에는 "그 날짜에 이미 예약된 시간(HH:mm)"만 담을 예정
    bookedSlots.value = data
      .filter(item => {
        // [1] 예약 상태 필터링
        // 취소된 예약까지 마감 처리하면 안 되니까
        // 상태가 '예약'인 건만 "마감"으로 간주
        // (백엔에서도 중복 체크할 때 '예약'만 중복으로 잡고 있음)
        const status = String(item.reservation_status || item.reservationStatus || '예약').trim()
        if (status !== '예약') return false

        // [2] 날짜 정규화(핵심) -> 이 예약이 내가 고른 날짜인가를 걸러냄
        // DB/백엔에서 오는 날짜는 형태가 제각각일 수 있음
        // - "YYYY-MM-DD"
        // - "YYYY-MM-DD 00:00:00"
        // - "YYYY-MM-DDT00:00:00"
        // - "YYYYMMDD"
        // 그래서 "비교 가능한 형태(YYYYMMDD)"로 무조건 통일해줌

        let dbDate = String(item.reservation_date || item.reservationDate || '').trim()

        // "2026-02-04T00:00:00" 같은 ISO 문자열이면 T 앞부분만 날짜임
        if (dbDate.includes('T')) dbDate = dbDate.split('T')[0]

        // "2026-02-04 00:00:00" 같이 공백으로 datetime이 오면 공백 앞만 날짜임
        if (dbDate.includes(' ')) dbDate = dbDate.split(' ')[0]

        // 하이픈 제거해서 YYYYMMDD로 통일
        dbDate = dbDate.replaceAll('-', '')

        // 선택한 날짜(targetDate)와 DB 날짜(dbDate)가 같으면 그 항목만 통과
        return dbDate === targetDate
      })
      .map(item => {
        // [3] 시간 정규화(핵심) -> 남은 예약들에서 시간만 뽑아서 bookedSlots 배열 만들기
        // DB/백엔에서 오는 시간도 형태가 제각각일 수 있음
        // - "09:00:00"
        // - "2026-02-04 09:00:00"
        // - "2026-02-04T09:00:00"
        // - "09:00"
        // 그래서 결국 우리가 비교에 쓰는 "HH:mm" 으로 통일해서 뽑아냄

        let t = String(item.reservation_time || item.reservationTime || '').trim()

        // ISO 형식이면 T 뒤가 시간 파트임
        if (t.includes('T')) t = t.split('T')[1]

        // 공백 포함 datetime이면 공백 뒤가 시간 파트임
        if (t.includes(' ')) t = t.split(' ')[1]

        // "09:00:00" -> "09:00" 으로 맞추기 위해 앞 5글자만 자름
        return t.substring(0, 5) // HH:mm
      })

    // 디버깅
    // 여기 값이 ["09:00","09:30"] 이런 식으로 나오면 isBooked가 true가 됨
    // → 버튼에 disabled가 붙음
    // → :disabled CSS가 발동
    console.log("bookedSlots:", bookedSlots.value)

  } catch (err) {
    // 에러가 나면 bookedSlots를 빈 배열로 초기화
    // (이거 안 하면 이전 날짜에서 남아있던 마감 시간이 계속 남아있을 수 있음)
    console.error('스케줄 에러', err)
    bookedSlots.value = []
  }
}



const fetchDoctors = async () => { if (!form.medDeptId) return; try { const res = await getDoctorsReq(form.medDeptId); doctors.value = res.data; } catch (err) { } }
const resetDateTime = () => { form.reservationDate = ''; form.reservationTime = ''; }
const resetToGeneral = () => { isFixedDoc.value = false; form.doctorId = ''; form.medDeptId = ''; useRouterInstance.replace('/reservation'); }

const submitReservation = async () => {
  if (!isValidName.value || !isValidPhone.value) return;

  // 1. 전송 데이터 준비
  form.patientPhone = `${phonePart1.value}-${phonePart2.value}-${phonePart3.value}`;
  const loginInfo = JSON.parse(sessionStorage.getItem('loginId') || '{}');
  const fullDateTime = `${form.reservationDate}T${form.reservationTime}:00`;
  const finalDeptId = Number(form.medDeptId || route.query.deptId);

  if (!finalDeptId || finalDeptId === 0) {
    alert("진료과 정보가 올바르지 않습니다. 다시 선택해 주세요.");
    return;
  }

  const resDto = {
    memId: Number(loginInfo.memId || loginInfo.mem_id),
    medDeptId: finalDeptId,
    doctorId: Number(form.doctorId),
    reservationDate: Number(form.reservationDate.replaceAll('-', '')),
    reservationTime: fullDateTime,
    patientName: form.patientName,
    patientPhone: form.patientPhone,
    reservationType: '일반예약',
    visitType: form.visitType,
    reservationStatus: '예약',
    reservationMemo: form.reservationMemo
  }

  try {
    const res = await addResReq(resDto);

    // [수정 핵심] 백엔드 응답에 따른 메시지 분기
    if (res.data === 'success' || res.data === true) {
      alert('예약이 완료되었습니다!');
      useRouterInstance.push('/checkreservation');
    } else if (res.data === 'duplicate') {
      alert('이미 예약된 시간입니다. 다른 시간을 선택해 주세요.');
      fetchBookedSlots(); // 최신 목록 새로고침
    } else {
      alert('예약에 실패했습니다. 입력 정보를 확인해주세요.');
    }
  } catch (err) {
    console.error("예약 실패 상세:", err);
    alert('서버 통신 중 오류가 발생했습니다.');
  }
}

onMounted(async () => {
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) {
    alert("로그인이 필요합니다.");
    useRouterInstance.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }

  try {
    const res = await getDeptsReq();
    depts.value = res.data;
    if (!route.query.deptId && route.query.deptName) {
      const found = depts.value.find(d => d.dept_name === route.query.deptName);
      if (found) form.medDeptId = found.med_dept_id;
    } else if (route.query.deptId) {
      form.medDeptId = Number(route.query.deptId);
    }
  } catch (err) { }

  if (route.query.docId) {
    form.doctorId = Number(route.query.docId);
    if (!form.medDeptId && route.query.deptId) form.medDeptId = Number(route.query.deptId);
    isFixedDoc.value = true;
    fetchBookedSlots();
  }
})
</script>

<style scoped>
.reservation-page-wrap {
  display: flex;
  justify-content: center;
  padding: 100px 20px;
  min-height: 120vh;
  background-color: #f4f7fa;
}

.reservation-card {
  width: 100%;
  max-width: 700px;
  background: #fff;
  padding: 60px 50px;
  border-radius: 8px;
  border: 1px solid #eee;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05);
  height: fit-content;
  margin-bottom: 100px;
}

.res-header {
  text-align: center;
  margin-bottom: 50px;
}

.res-header h2 {
  font-size: 30px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 700;
}

.title-bar {
  width: 40px;
  height: 3px;
  background: #0171e9;
  margin: 0 auto 20px;
}

.res-desc {
  font-size: 15px;
  color: #888;
  font-weight: 300;
}

.res-form-body {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* [수정] 섹션 제목 label만 잡도록 범위 제한 */
.input-section>label {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
  border-left: 4px solid #0171e9;
  padding-left: 10px;
}

.sub-label-sm {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 600;
}

.res-input,
.res-select {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 15px;
  background: #fff;
  transition: 0.2s;
  color: #333;
}

.res-input:focus,
.res-select:focus {
  border-color: #0171e9;
  outline: none;
  box-shadow: 0 0 0 3px rgba(1, 113, 233, 0.1);
}

.grid-row {
  display: flex;
  gap: 15px;
}

.grid-row>* {
  flex: 1;
}

.grid-col-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.phone-split-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.phone-part {
  text-align: center;
}

.dash {
  font-weight: 700;
  color: #ccc;
}

.center-txt {
  text-align: center;
}

.input-error {
  border-color: #e03131;
  background-color: #fff5f5;
}

.error-msg {
  font-size: 12px;
  color: #e03131;
  margin-top: 5px;
}

/* 커스텀 달력 */
.custom-calendar-container {
  position: relative;
  width: 100%;
}

.date-display-box {
  width: 100%;
  padding: 18px;
  border: 2px solid #eee;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  transition: 0.2s;
}

.date-display-box:hover,
.date-display-box.active {
  border-color: #0171e9;
  box-shadow: 0 5px 15px rgba(1, 113, 233, 0.1);
}

.calendar-popup {
  position: absolute;
  top: 110%;
  left: 0;
  width: 100%;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  z-index: 100;
  padding: 20px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.cal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.cal-title {
  font-size: 20px;
  font-weight: 800;
  color: #333;
}

.cal-nav-btn {
  background: none;
  border: 1px solid #eee;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  font-weight: bold;
  color: #666;
  transition: 0.2s;
}

.cal-nav-btn:hover {
  background: #f0f7ff;
  color: #0171e9;
  border-color: #0171e9;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  text-align: center;
}

.cal-day-name {
  font-weight: 600;
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.cal-day {
  height: 45px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  color: #333;
  transition: 0.2s;
}

.cal-day:hover:not(.empty):not(.disabled) {
  background-color: #f0f7ff;
  color: #0171e9;
  font-weight: 700;
}

.cal-day.selected {
  background-color: #0171e9;
  color: #fff !important;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(1, 113, 233, 0.4);
}

.cal-day.disabled {
  color: #ddd;
  cursor: not-allowed;
}

.cal-day.today {
  border: 1px solid #0171e9;
  color: #0171e9;
  font-weight: 700;
}

/* [수정] 시간 선택 버튼 스타일 */
.time-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.hide-radio {
  display: none !important;
}

/* 기본 상태: 아주 연한 회색 테두리 (파란색 아님) */
.time-btn {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  color: #555;
  border-left: 1px solid #e0e0e0 !important;
}

.time-txt {
  font-size: 15px;
  font-weight: 500;
}

/* 마우스 올렸을 때 */
.time-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #aaa;
  color: #333;
}

/* 선택됨: 꽉 찬 남색/파란색 */
.time-btn.selected {
  background: #0171e9;
  border-color: #0171e9 !important;
  color: #fff;
  box-shadow: 0 4px 10px rgba(1, 113, 233, 0.3);
  transform: translateY(-1px);
}

.time-btn.selected .time-txt {
  font-weight: 700;
}

/* [수정 핵심] 예약 마감: 진짜 disabled + 회색 + 클릭 불가 */
.time-btn:disabled {
  background: #f8f9fa !important;
  border: 1px solid #e9ecef !important;
  border-left: 1px solid #e9ecef !important;
  color: #adb5bd !important;
  cursor: not-allowed !important;
  opacity: 0.7;
  box-shadow: none !important;
  transform: none !important;
}

.time-btn:disabled .time-txt {
  color: #adb5bd !important;
  text-decoration: line-through;
}

.time-btn:disabled:hover {
  background: #f8f9fa !important;
  box-shadow: none !important;
  transform: none !important;
}

.booked-label {
  font-size: 10px;
  color: #ff6b6b;
  font-weight: 700;
  position: absolute;
  bottom: 4px;
}

.fixed-doc-card {
  background: #f0f7ff;
  border: 1px solid #a1d8f3;
  padding: 25px 30px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.fixed-label {
  font-size: 13px;
  color: #0171e9;
  font-weight: 700;
}

.fixed-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.f-dept {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.f-doc {
  font-size: 18px;
  color: #0171e9;
  font-weight: 700;
}

.btn-change {
  margin-left: auto;
  background: #fff;
  border: 1px solid #ddd;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: 0.2s;
}

.res-submit-btn {
  width: 100%;
  padding: 20px;
  background: #0171e9;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 20px;
  box-shadow: 0 10px 20px rgba(1, 113, 233, 0.15);
}

.res-submit-btn:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 15px 25px rgba(1, 113, 233, 0.25);
}

.transition-box {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
