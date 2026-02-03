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
              <option v-for="d in depts" :key="d.dept_id" :value="d.dept_id">{{ d.dept_name }}</option>
            </select>
            <select v-if="form.dept_id" v-model="form.medStaffId" @change="resetDateTime" class="res-select">
              <option value="">담당 의사 선택</option>
              <option v-for="s in doctors" :key="s.staff_id" :value="s.staff_id">{{ s.name }} 의사</option>
            </select>
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.staff_id">
          <label>환자 정보 입력</label>
          <div class="grid-row">
            <input type="text" v-model="form.patient_name" placeholder="환자 성함" class="res-input">
            <input type="text" v-model="form.patient_phone" placeholder="연락처 (- 제외)" class="res-input">
          </div>
        </div>

        <div class="input-section transition-box" v-if="form.patient_name && form.patient_phone">
          <label>예약 날짜</label>
          <input type="date" v-model="form.reservation_date" @change="fetchBookedSlots" class="res-input date-input" :min="today">
        </div>

        <div class="input-section transition-box" v-if="form.reservation_date">
          <label>예약 시간 <span class="sub-label">(30분 단위)</span></label>
          <div class="time-grid">
            <label v-for="time in allTimeSlots" :key="time" class="time-tile" :class="{ 'booked': isBooked(time) }">
              <input type="radio" v-model="form.reservation_time" :value="time" :disabled="isBooked(time)" class="hide-radio">
              <span class="time-txt">{{ time }}</span>
            </label>
          </div>
        </div>

        <button v-if="form.reservation_time" @click="submitReservation" class="res-submit-btn">예약 확정하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { postReservation, getDepts, getDoctorsByDept } from '@/api/reservation';
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const depts = ref([]); const doctors = ref([]); const bookedSlots = ref([]) 
const isFixedDoc = ref(false)

const form = reactive({
  memId: 0, // mem_id -> memId (카멜케이스)
  medDeptId: '', // dept_id -> medDeptId
  doctorId: '', // staff_id -> doctorId
  // patient_name, patient_phone은 백엔드 ReservationDto에 필드가 없다면
  // reservationMemo 등에 합쳐서 보내거나 백엔드 DTO에 추가해야 함.
  // 여기서는 DTO에 해당 필드가 없으므로 memo에 합치는 방식 예시:
  reservationMemo: '', 
  reservationDate: '', // reservation_date -> reservationDate
  reservationTime: '', // reservation_time -> reservationTime
  visitType: '초진', // visit_type -> visitType
  reservationStatus: '예약' // reservation_status -> reservationStatus
})

const today = new Date().toISOString().split('T')[0]
const allTimeSlots = computed(() => {
  const slots = []; for (let h = 9; h < 19; h++) {
    const hour = h < 10 ? `0${h}` : h
    slots.push(`${hour}:00`, `${hour}:30`)
  }
  return slots
})

const isBooked = (time) => bookedSlots.value.includes(time)

const fetchBookedSlots = async () => {
  // 백엔드 미구현 상태이므로 빈 배열 처리 (추후 구현 필요)
  bookedSlots.value = []; 
}

const fetchDoctors = async () => {
  if(!form.medDeptId) return
  // [수정] API 함수 사용
  const res = await getDoctorsByDept(form.medDeptId)
  doctors.value = res.data
}

// 🔄 의사 잘못 골랐을 때 다시 선택 모드로 리셋 🚗
const resetToGeneral = () => {
  isFixedDoc.value = false; form.staff_id = ''; form.dept_id = '';
  router.replace('/reservation') // 쿼리 지우기
}

const submitReservation = async () => {
  try {
    // 환자 정보를 메모에 저장 (백엔드 DTO에 patient_name이 없다면)
    form.reservationMemo = `환자명:${form.patient_name}, 연락처:${form.patient_phone}`;
    
    // [수정] API 함수 사용 (postReservation)
    await postReservation(form) 
    alert('예약이 완료되었습니다! 🎉')
    router.push('/checkreservation') // 완료 후 조회 페이지로 이동 추천
  } catch (err) { 
    alert('다시 시도해 주세요 🧩') 
  }
}

onMounted(async () => {
  // [수정] API 함수 사용
  const res = await getDepts(); 
  depts.value = res.data
  
  if (route.query.docId) {
    form.doctorId = Number(route.query.docId)
    isFixedDoc.value = true
  }
})
</script>

<style scoped>
/* 🏥 기존 스타일 100% 유지하면서 [의료진 요약 카드] 디자인만 추가 ✨ */
.reservation-page-wrap { display: flex; justify-content: center; padding: 100px 20px; min-height: 120vh; background-color: #f4f7fa; }
.reservation-card { width: 100%; max-width: 800px; background: #fff; padding: 60px 50px; border-radius: 4px; border: 1px solid #eee; box-shadow: 0 15px 40px rgba(0,0,0,0.05); height: fit-content; margin-bottom: 100px; }

/* 🩺 선택된 의료진 요약 박스 (쥰나 고급짐 🪄) */
.fixed-doc-card {
  background: #f0f7ff; border: 1px solid #a1d8f3;
  padding: 25px 30px; border-radius: 4px; margin-bottom: 35px;
  display: flex; flex-direction: column; gap: 10px;
}
.fixed-label { font-size: 13px; color: #0171e9; font-weight: 700; }
.fixed-info { display: flex; align-items: center; gap: 15px; }
.f-dept { font-size: 18px; color: #404347; font-weight: 600; }
.f-doc { font-size: 18px; color: #0171e9; font-weight: 700; }
.btn-change { 
  margin-left: auto; background: #fff; border: 1px solid #ddd;
  padding: 5px 12px; font-size: 12px; border-radius: 4px; color: #999;
  cursor: pointer; transition: 0.2s;
}
.btn-change:hover { border-color: #0171e9; color: #0171e9; }

/* 기타 스타일 (생략 없이 100% 반영 🧩) */
.res-header { text-align: center; margin-bottom: 50px; }
.res-header h2 { font-size: 30px; color: #404347; margin-bottom: 12px; font-weight: 700; }
.title-bar { width: 40px; height: 3px; background: #0171e9; margin: 0 auto 20px; }
.res-desc { font-size: 15px; color: #888; font-weight: 300; }
.res-form-body { display: flex; flex-direction: column; gap: 35px; }
.input-section label { display: block; font-size: 14px; font-weight: 600; color: #4e4e4e; margin-bottom: 12px; }
.res-input, .res-select { width: 100%; padding: 15px; border: 1px solid #ddd; border-radius: 4px; font-size: 15px; background: #f9f9f9; transition: 0.3s; }
.grid-row { display: flex; gap: 15px; }
.grid-row > * { flex: 1; }
.time-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-top: 10px; }
.time-tile { height: 50px; display: flex; justify-content: center; align-items: center; border: 1px solid #ddd; border-radius: 4px; background: #f9f9f9; cursor: pointer; transition: 0.2s; }
.time-txt { font-size: 14px; color: #666; font-weight: 500; }
.hide-radio { display: none !important; }
.time-tile:hover:not(.booked) { border-color: #0171e9; background: #f0f7ff; }
.time-tile:has(.hide-radio:checked) { background: #0171e9; border-color: #0171e9; }
.time-tile:has(.hide-radio:checked) .time-txt { color: #fff; font-weight: 600; }
.time-tile.booked { background: #eee; border-color: #ddd; cursor: not-allowed; opacity: 0.6; }
.time-tile.booked .time-txt { color: #bbb; text-decoration: line-through; }
.res-submit-btn { width: 100%; padding: 20px; background: #0171e9; color: #fff; border: none; border-radius: 4px; font-size: 18px; font-weight: 600; cursor: pointer; transition: 0.3s; margin-top: 20px; }
.transition-box { animation: fadeIn 0.5s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>