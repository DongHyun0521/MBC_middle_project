<template>
  <div class="full-dashboard">
    <aside class="sidebar">
      <div class="profile-area">
        <div class="avatar">{{ userRoleIcon }}</div>
        <p class="user-id">{{ userInfo.name }} 님</p>
        <p class="user-tag">{{ userRoleDisplay }}</p>
        <p class="join-date">{{ userType === 'MEMBER' ? '가입일' : '입사일' }}: {{ formatDate(userInfo.createTime ||
          userInfo.create_time) }}</p>
      </div>
      <nav class="side-nav">
        <ul>
          <li :class="{ active: currentView === 'dash' }" @click="changeView('dash')">대시보드 홈</li>

          <li :class="{ active: currentView === 'res' }" @click="changeView('res')">내 진료 예약 내역</li>

          <template v-if="isDoctor">
            <li :class="{ active: currentView === 'doc_res' }" @click="changeView('doc_res')">진료 업무 일정</li>
            <li :class="{ active: currentView === 'doc_history' }" @click="changeView('doc_history')">담당 환자 조회</li>
          </template>

          <template v-if="isNurse">
            <li :class="{ active: currentView === 'nur_work' }" @click="changeView('nur_work')">진료 업무 일정</li>
            <li :class="{ active: currentView === 'nur_schedule' }" @click="changeView('nur_schedule')">근무 일정 (Shift)
            </li>
            <li :class="{ active: currentView === 'nur_ward' }" @click="changeView('nur_ward')">병동 현황</li>
          </template>

          <template v-if="userType === 'ADMIN'">
            <li :class="{ active: currentView === 'admin_voc' }" @click="changeView('admin_voc')">고객의 소리 (VOC)</li>
            <li :class="{ active: currentView === 'admin_todo' }" @click="changeView('admin_todo')">업무 관리 (To-Do)</li>
          </template>

          <li :class="{ active: currentView === 'vehi' }" @click="changeView('vehi')">차량 관리</li>
          <li :class="{ active: currentView === 'edit' }" @click="changeView('edit')">개인 정보 수정</li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <header class="dashboard-header">
        <div class="welcome-text">
          <h2>{{ getTimeGreeting() }}, <span class="blue-txt">{{ userInfo.name }}</span>님</h2>
          <p class="current-time">{{ currentTime }}</p>
        </div>
      </header>

      <div v-if="currentView === 'dash'" class="dash-home-grid">
        <section class="dash-card profile-card">
          <div class="card-head">
            <h3>👤 내 정보 요약</h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">소속/주소</span>
              <p class="val">{{ userType === 'MEMBER' ? userInfo.address : (userInfo.adminDeptName || userInfo.deptName
                ||
                '소속없음') }}</p>
            </div>
            <div class="info-item"><span class="label">연락처</span>
              <p class="val">{{ userInfo.phoneNumber }}</p>
            </div>
            <div class="info-item"><span class="label">이메일</span>
              <p class="val">{{ userInfo.email }}</p>
            </div>
            <div v-if="userType !== 'MEMBER'" class="info-item">
              <span class="label">직책</span>
              <p class="val">{{ userInfo.role || userInfo.rank || '직원' }}</p>
            </div>
          </div>
        </section>

        <template v-if="isDoctor || isNurse">
          <section class="dash-card">
            <div class="card-head">
              <h3>🩺 오늘 진료 현황</h3>
            </div>
            <div class="stat-grid">
              <div class="stat-box blue"><span>{{doctorSchedules.filter(s => s.reservation_status === '예약').length
                  }}</span>명<br>예약 대기</div>
              <div class="stat-box"><span>{{doctorSchedules.filter(s => s.reservation_status === '완료').length
                  }}</span>명<br>진료 완료</div>
            </div>
          </section>
        </template>

        <template v-if="userType === 'ADMIN'">
          <section class="dash-card">
            <div class="card-head">
              <h3>⚡ 긴급 업무</h3>
            </div>
            <div class="stat-grid">
              <div class="stat-box red"><span>{{vocList.filter(v => !v.answerStatus).length}}</span>건<br>VOC 미답변</div>
              <div class="stat-box"><span>{{todoList.filter(t => !t.done).length}}</span>건<br>미완료 업무</div>
            </div>
          </section>
        </template>

        <section class="dash-card">
          <div class="card-head">
            <h3>📅 나의 병원 예약</h3>
          </div>
          <div v-if="upcomingRes" class="res-highlight">
            <span class="d-day">D-{{ calculateDday(upcomingRes.reservation_date) }}</span>
            <p class="res-time-txt">{{ upcomingRes.reservation_date }} ({{ upcomingRes.reservation_time }})</p>
            <p class="res-doc-txt">{{ upcomingRes.dept_name }} | {{ upcomingRes.doctor_name }} 의사</p>
          </div>
          <div v-else class="empty-res">예정된 예약이 없습니다</div>
        </section>
      </div>

      <div v-if="currentView === 'doc_res' || currentView === 'nur_work'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>👨‍⚕️ 진료 업무 일정</h3>
            <div class="toggle-group">
              <button :class="{ active: docViewMode === 'calendar' }" @click="docViewMode = 'calendar'">달력</button>
              <button :class="{ active: docViewMode === 'list' }" @click="docViewMode = 'list'">리스트</button>
            </div>
          </div>

          <div v-if="docViewMode === 'calendar'" class="calendar-wrap">
            <div class="cal-header">
              <button @click="changeMonth(-1)">&lt;</button>
              <h4>{{ calYear }}년 {{ calMonth }}월</h4>
              <button @click="changeMonth(1)">&gt;</button>
            </div>
            <div class="cal-grid">
              <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day }}</div>
              <div v-for="(date, idx) in calendarDays" :key="idx"
                :class="['cal-cell', { 'diff-month': !date.isCurrentMonth, 'today': isToday(date.fullDate) }]">
                <span class="day-num">{{ date.day }}</span>
                <div class="cal-events">
                  <div v-for="evt in getEventsForDate(date.fullDate)" :key="evt.reservation_id"
                    :class="['cal-dot', getBadgeClass(evt.reservation_status)]">
                    {{ evt.reservation_time.substring(0, 5) }} {{ evt.patient_name }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-else>
            <div class="filter-tabs">
              <button v-for="st in ['전체', '예약', '완료', '취소']" :key="st"
                :class="['filter-btn', { active: docFilter === st }]" @click="docFilter = st">
                {{ st }}
              </button>
            </div>
            <table class="hospital-tbl">
              <thead>
                <tr>
                  <th>날짜</th>
                  <th>시간</th>
                  <th>환자명</th>
                  <th>상태</th>
                  <th>메모</th>
                  <th class="txt-center">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="sc in filteredDoctorSchedules" :key="sc.reservation_id">
                  <td class="bold-text">{{ formatDate(sc.reservation_date) }}</td>
                  <td>{{ String(sc.reservation_time).substring(0, 5) }}</td>
                  <td class="bold-blue">{{ sc.patient_name }}</td>
                  <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{ sc.reservation_status
                      }}</span></td>
                  <td>{{ sc.reservation_memo || '-' }}</td>
                  <td class="txt-center">
                    <button v-if="isDoctor && sc.reservation_status === '예약'" class="btn-complete"
                      @click="completeTreatment(sc)">
                      진료 완료
                    </button>
                    <span v-else>-</span>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="filteredDoctorSchedules.length === 0" class="empty-msg">내역이 없습니다.</div>
          </div>
        </div>
      </div>

      <div v-if="currentView === 'nur_schedule'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>📅 근무 일정표 (Shift)</h3>
            <p class="sub-desc">날짜를 클릭하여 근무 메모를 남기세요.</p>
          </div>
          <div class="calendar-wrap">
            <div class="cal-header">
              <button @click="changeMonth(-1)">&lt;</button>
              <h4>{{ calYear }}년 {{ calMonth }}월</h4>
              <button @click="changeMonth(1)">&gt;</button>
            </div>
            <div class="cal-grid">
              <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day }}</div>
              <div v-for="(date, idx) in calendarDays" :key="idx"
                :class="['cal-cell', { 'diff-month': !date.isCurrentMonth }]" @click="addShiftMemo(date.fullDate)">
                <span class="day-num">{{ date.day }}</span>
                <div class="cal-events">
                  <div v-if="getShiftMemo(date.fullDate)" class="cal-dot shift">
                    {{ getShiftMemo(date.fullDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="currentView === 'admin_todo'" class="view-section">
        <div class="split-view">
          <div class="section-card flex-1">
            <div class="card-head">
              <h3>📆 업무 달력</h3>
            </div>
            <div class="calendar-wrap mini">
              <div class="cal-header">
                <button @click="changeMonth(-1)">&lt;</button>
                <span>{{ calYear }}.{{ calMonth }}</span>
                <button @click="changeMonth(1)">&gt;</button>
              </div>
              <div class="cal-grid">
                <div v-for="d in calendarDays" :key="d.fullDate" :class="['cal-cell', { 'today': isToday(d.fullDate) }]">
                  <span class="day-num">{{ d.day }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="section-card flex-1">
            <div class="card-head">
              <h3>To-Do List</h3>
            </div>
            <div class="todo-input-box">
              <input v-model="newTodo" @keyup.enter="addTodo" placeholder="할 일을 입력하세요" />
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

      <div v-if="currentView === 'admin_voc'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>고객의 소리 관리</h3>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>상태</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="voc in vocList" :key="voc.vocId">
                <td>
                  <span v-if="!voc.answerStatus" class="red-alert">🔴 미답변</span>
                  <span v-else class="badge-gray">답변완료</span>
                </td>
                <td>{{ voc.title }}</td>
                <td>{{ voc.writerName }}</td>
                <td>{{ formatDate(voc.writeDate) }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="vocList.length === 0" class="empty-msg">접수된 VOC가 없습니다.</div>
        </div>
      </div>

      <div v-if="currentView === 'doc_history'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>담당 환자 조회</h3>
          </div>
          <div class="empty-msg">준비중입니다.</div>
        </div>
      </div>
      <div v-if="currentView === 'nur_ward'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>병동 현황</h3>
          </div>
          <div class="empty-msg">병동 시스템 연동 준비중</div>
        </div>
      </div>

      <div v-if="currentView === 'vehi'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>차량 관리</h3><button @click="router.push('/vehiregi')" class="btn-add-sm">+ 새 차량 등록</button>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>차량번호</th>
                <th>차종/유종</th>
                <th class="txt-center">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="car in myVehicles" :key="car.vehicleNum">
                <td class="bold-blue">{{ car.vehicleNum }}</td>
                <td>{{ car.vehicleType }} / {{ car.fuelType }}</td>
                <td class="txt-center"><button @click="deleteVehicle(car.vehicleNum)" class="btn-cancel-table">차량
                    삭제</button></td>
              </tr>
            </tbody>
          </table>
          <div v-if="myVehicles.length === 0" class="empty-msg">등록된 차량이 없습니다.</div>
        </div>
      </div>

      <div v-if="currentView === 'res'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>내 진료 예약 내역 (개인)</h3>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>진료과/의료진</th>
                <th>예약일시</th>
                <th>상태</th>
                <th class="txt-center">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="res in myReservations" :key="res.reservation_id">
                <td class="bold-blue">{{ res.dept_name }} <span>({{ res.doctor_name }} 의사)</span></td>
                <td>{{ formatDate(res.reservation_date) }} {{ res.reservation_time }}</td>
                <td><span :class="['status-badge', res.reservation_status === '예약' ? 'active' : 'done']">{{
                  res.reservation_status }}</span></td>
                <td class="txt-center"><button v-if="res.reservation_status === '예약'" class="btn-cancel-table"
                    @click="cancelRes(res.reservation_id)">예약취소</button><span v-else>-</span></td>
              </tr>
            </tbody>
          </table>
          <div v-if="myReservations.length === 0" class="empty-msg">예약 내역이 없습니다</div>
        </div>
      </div>

      <div v-if="currentView === 'edit'" class="view-section centered">
        <div class="edit-card-wrap">
          <div class="section-card">
            <div class="card-head">
              <h3>개인 정보 수정</h3>
            </div>
            <div class="edit-form">
              <div class="f-row"><span>아이디</span>
                <p class="readonly-val">{{ userInfo.id }}</p>
              </div>
              <div class="f-row"><span>이름</span><input v-model="userInfo.name" type="text" /></div>
              <template v-if="userType !== 'MEMBER'">
                <div class="f-row"><span>소속 부서</span>
                  <p class="readonly-val">{{ userInfo.adminDeptName || userInfo.deptName || '소속없음' }}</p>
                </div>
                <div class="f-row"><span>직급/직책</span>
                  <p class="readonly-val">{{ userInfo.rank || userInfo.role }}</p>
                </div>
                <div class="f-row"><span>입사일</span>
                  <p class="readonly-val">{{ formatDate(userInfo.createTime || userInfo.create_time) }}</p>
                </div>
              </template>
              <template v-if="userType === 'MEMBER'">
                <div class="f-row"><span>생년월일</span>
                  <p class="readonly-val">{{ formatBirthday(userInfo.birthday) }}</p>
                </div>
                <div class="f-row"><span>주소</span>
                  <div class="addr-box-flex"><input v-model="userInfo.address" type="text" readonly
                      @click="openPostcode" placeholder="주소 검색" /><button type="button" @click="openPostcode"
                      class="btn-addr-search">검색</button></div>
                </div>
                <div class="f-row"><span>상세주소</span><input v-model="userInfo.addressDetail" type="text"
                    id="detailAddr" /></div>
              </template>
              <div class="f-row"><span>연락처</span><input v-model="userInfo.phoneNumber" type="text" /></div>
              <div class="f-row"><span>이메일</span><input v-model="userInfo.email" type="email" /></div>
              <button @click="saveUserInfo" class="btn-blue-full">정보 업데이트</button>
            </div>
          </div>
          <div class="section-card mt-30">
            <div class="card-head">
              <h3>비밀번호 변경</h3>
            </div>
            <div class="edit-form">
              <div class="f-row"><span>새 비밀번호</span>
                <div class="pw-field-box"><input v-model="pwData.newPw" type="password" placeholder="변경할 비밀번호 입력" />
                </div>
              </div>
              <button @click="handlePasswordUpdate" class="btn-blue-full gray">비밀번호 변경 완료</button>
            </div>
          </div>
          <div class="withdraw-box"><span class="withdraw-link" @click="startWithdraw">회원 탈퇴</span></div>
        </div>
      </div>
    </main>

    <div v-if="isAuthModalOpen" class="modal-overlay">
      <div class="modal-card auth-modal">
        <h3>보안을 위해 비밀번호를 입력해 주세요</h3>
        <div class="pw-field-box mb-25"><input v-model="authPw" type="password" class="auth-pw-input"
            @keyup.enter="verifyAccess" /></div>
        <div class="modal-btns"><button @click="verifyAccess" class="btn-modal-confirm">확인</button><button
            @click="cancelAccess" class="btn-modal-cancel">취소</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { updateInfoReq, withdrawReq } from '@/api/member'
import { getMyResReq, cancelResReq, getAllDoctorsReq, getDocSchedReq } from '@/api/reservation'
import { getVehiReq, delVehiReq } from '@/api/vehicle'
import { getAdminVocListReq } from '@/api/customer' // VOC 목록 가져오는 API import

const router = useRouter()
const currentView = ref('dash')
const userInfo = ref({})
const userType = ref('MEMBER')
const myReservations = ref([])
const doctorSchedules = ref([])
const myVehicles = ref([])
const vocList = ref([]) // 행정직용 VOC 목록
const currentTime = ref("")
const isAuthModalOpen = ref(false)
const authPw = ref("")
const pwData = ref({ newPw: '' })

// === 캘린더 & 필터 설정 ===
const docViewMode = ref('calendar') // 의사는 달력이 기본!
const docFilter = ref('전체')
const calYear = ref(new Date().getFullYear())
const calMonth = ref(new Date().getMonth() + 1)

// === 행정직 To-Do List (간단 로컬 기능) ===
const newTodo = ref('')
const todoList = ref([
  { text: '부서 회의 준비', done: false },
  { text: '비품 재고 파악', done: true }
])
const addTodo = () => { if (newTodo.value.trim()) { todoList.value.push({ text: newTodo.value, done: false }); newTodo.value = ''; } }
const removeTodo = (idx) => { todoList.value.splice(idx, 1); }

// === 간호사 근무표 메모 (간단 로컬 기능) ===
const nurseShiftMemos = ref({}) // {'2024-05-10': 'Day 근무', ...}
const addShiftMemo = (dateStr) => {
  if (!isNurse.value) return;
  const memo = prompt(`${dateStr} 근무 내용을 입력하세요 (예: Day, Eve, Night)`, nurseShiftMemos.value[dateStr] || '');
  if (memo !== null) nurseShiftMemos.value[dateStr] = memo;
}
const getShiftMemo = (dateStr) => nurseShiftMemos.value[dateStr]

// === 역할 구분 ===
const isDoctor = computed(() => { if (userType.value !== 'MED') return false; const r = (userInfo.value.role || '').toUpperCase(); return r === '의사' || r === 'DOCTOR'; });
const isNurse = computed(() => { if (userType.value !== 'MED') return false; const r = (userInfo.value.role || '').toUpperCase(); return r === '간호사' || r === 'NURSE'; });

const userRoleIcon = computed(() => { if (userType.value === 'ADMIN') return '💼'; if (isDoctor.value) return '👨‍⚕️'; if (isNurse.value) return '💉'; if (userType.value === 'MED') return '🏥'; return '👤'; });

const userRoleDisplay = computed(() => {
  if (userType.value === 'MEMBER') return `${formatBirthday(userInfo.value.birthday)} | ${userInfo.value.gender == 1 ? '남' : '여'}`;
  return `${userInfo.value.adminDeptName || userInfo.value.deptName || '소속없음'} | ${userInfo.value.role || userInfo.value.rank || '직원'}`;
});

const upcomingRes = computed(() => myReservations.value.find(r => r.reservation_status === '예약'))

const filteredDoctorSchedules = computed(() => {
  if (docFilter.value === '전체') return doctorSchedules.value;
  return doctorSchedules.value.filter(s => s.reservation_status === docFilter.value);
});

// 캘린더 생성
const calendarDays = computed(() => {
  const year = calYear.value;
  const month = calMonth.value - 1;
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const days = [];
  for (let i = 0; i < firstDay.getDay(); i++) days.push({ day: '', isCurrentMonth: false });
  for (let d = 1; d <= lastDay.getDate(); d++) {
    const mm = month + 1 < 10 ? '0' + (month + 1) : (month + 1); const dd = d < 10 ? '0' + d : d;
    days.push({ day: d, isCurrentMonth: true, fullDate: `${year}-${mm}-${dd}` });
  }
  return days;
});

const changeMonth = (delta) => {
  let newMonth = calMonth.value + delta;
  if (newMonth > 12) { calYear.value++; newMonth = 1; }
  else if (newMonth < 1) { calYear.value--; newMonth = 12; }
  calMonth.value = newMonth;
}
const isToday = (str) => { const t = new Date(); const d = new Date(str); return t.toDateString() === d.toDateString(); }
const getEventsForDate = (str) => {
  const clean = str.replace(/-/g, '');
  return doctorSchedules.value.filter(s => String(s.reservation_date).replace(/-/g, '') === clean && s.reservation_status !== '취소');
}
const getBadgeClass = (s) => s === '예약' ? 'active' : (s === '완료' ? 'done' : 'cancel');

// 뷰 변경 및 데이터 로드
const changeView = (view) => {
  if (view === 'edit') isAuthModalOpen.value = true;
  else {
    currentView.value = view;
    if (view === 'vehi') fetchVehicles();
    if (view === 'res') fetchReservations();

    // 직군별 데이터 로드
    if ((isDoctor.value || isNurse.value) && (view === 'doc_res' || view === 'nur_work')) fetchDoctorSchedules();
    if (userType.value === 'ADMIN' && (view === 'admin_voc' || view === 'dash')) fetchVocList();
  }
};

const verifyAccess = () => { if (authPw.value === userInfo.value.password) { isAuthModalOpen.value = false; currentView.value = 'edit'; authPw.value = ''; } else alert('비번 불일치'); };
const cancelAccess = () => { isAuthModalOpen.value = false; authPw.value = ''; };
const openPostcode = () => { new window.daum.Postcode({ oncomplete: (data) => userInfo.value.address = data.roadAddress }).open(); };
const formatDate = (d) => { if (!d) return '-'; const s = String(d); if (s.length === 8) return `${s.substring(0, 4)}-${s.substring(4, 6)}-${s.substring(6, 8)}`; try { return new Date(d).toISOString().split('T')[0]; } catch (e) { return s; } }
const formatBirthday = (b) => b ? String(b).replace(/(\d{4})(\d{2})(\d{2})/, '$1.$2.$3') : '';
const calculateDday = (d) => { if (!d) return 0; const diff = new Date(String(d).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3')) - new Date(); return Math.ceil(diff / (1000 * 3600 * 24)); };
const getTimeGreeting = () => { const h = new Date().getHours(); return h < 12 ? '좋은 아침입니다' : h < 18 ? '즐거운 오후입니다' : '편안한 저녁입니다'; };

// API
const fetchReservations = async () => { try { const res = await getMyResReq(); myReservations.value = res.data; } catch (e) { } }
const fetchVehicles = async () => { try { const res = await getVehiReq(); myVehicles.value = res.data; } catch (e) { } }

// 의사/간호사 스케줄 (전체 의사 목록에서 내 ID 매칭)
const fetchDoctorSchedules = async () => {
  try {
    const docListRes = await getAllDoctorsReq();
    const doctors = docListRes.data || [];
    // 로그인한 ID와 일치하는 의사 찾기
    const myDoctorInfo = doctors.find(doc => doc.user_id === userInfo.value.id);
    if (myDoctorInfo) {
      const schedRes = await getDocSchedReq(myDoctorInfo.staff_id);
      doctorSchedules.value = schedRes.data || [];
    }
  } catch (e) { console.error(e); }
}

// [New] 진료 완료 처리 (임시 로직: DB업데이트 API 필요하지만 일단 프론트 반영)
const completeTreatment = (scheduleItem) => {
  if (confirm(`${scheduleItem.patient_name} 님 진료를 완료 처리하시겠습니까?`)) {
    scheduleItem.reservation_status = '완료'; // 화면 즉시 반영
    // 실제로는 여기서 await updateReservationStatus(id, '완료'); 호출해야 함
    alert("진료 완료 처리되었습니다.");
  }
}

// [New] 행정직 VOC 목록
const fetchVocList = async () => {
  try {
    // 'all'로 호출해서 미답변인거 필터링해서 씀
    const res = await getAdminVocListReq('all');
    vocList.value = res.data || [];
  } catch (e) { console.error(e); }
}

const cancelRes = async (id) => { if (confirm("취소?")) { try { await cancelResReq(id); fetchReservations(); alert("취소됨"); } catch (e) { } } }
const deleteVehicle = async (n) => { if (!confirm(`차량 [${n}] 삭제?`)) return; try { const res = await delVehiReq(n); if (res.data === 'success') { alert("삭제"); fetchVehicles(); } else alert("실패"); } catch (e) { } }
const saveUserInfo = async () => { try { await updateInfoReq(userInfo.value); alert("수정완료"); const s = JSON.parse(sessionStorage.getItem('loginId') || '{}'); sessionStorage.setItem('loginId', JSON.stringify({ ...s, ...userInfo.value })); } catch (e) { alert("실패"); } }
const handlePasswordUpdate = async () => { if (!pwData.value.newPw) return; try { await updateInfoReq({ ...userInfo.value, password: pwData.value.newPw }); alert("비번변경됨. 재로그인."); sessionStorage.clear(); router.push('/login'); } catch (e) { } }
const startWithdraw = async () => { if (confirm("탈퇴?")) { await withdrawReq(); sessionStorage.clear(); router.push('/'); } }
const updateClock = () => { currentTime.value = new Date().toLocaleString('ko-KR', { month: 'long', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit' }); };

onMounted(() => {
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) { router.push('/login'); return; }
  try {
    const parsed = JSON.parse(loginData);
    userInfo.value = parsed;
    if (sessionStorage.getItem('loginType')) userType.value = sessionStorage.getItem('loginType');
    else userType.value = parsed.role ? 'MED' : 'MEMBER';
    if ((parsed.loginType || '').toUpperCase() === 'ADMIN') userType.value = 'ADMIN';
  } catch (e) { }

  if (userType.value === 'ADMIN') currentView.value = 'admin_todo';
  else if (userType.value === 'MED') {
    if (isDoctor.value) { currentView.value = 'doc_res'; docViewMode.value = 'calendar'; } // 의사는 달력 먼저
    else if (isNurse.value) currentView.value = 'nur_schedule';
    else currentView.value = 'dash';
  } else currentView.value = 'dash';

  fetchReservations();
  fetchVehicles();
  updateClock(); setInterval(updateClock, 1000);
});
</script>

<style scoped>
/* 기존 스타일 + New Style */
.full-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f4f7fa;
  width: 100%;
}

.sidebar {
  width: 260px;
  background-color: #404347;
  color: #fff;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  flex-shrink: 0;
}

.profile-area {
  padding: 40px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.avatar {
  font-size: 40px;
  margin-bottom: 10px;
}

.user-id {
  font-size: 18px;
  font-weight: 700;
}

.user-tag,
.join-date {
  font-size: 12px;
  color: #aaa;
  margin-top: 4px;
}

.side-nav {
  flex: 1;
  padding: 20px 0;
}

.side-nav li {
  padding: 15px 30px;
  cursor: pointer;
  color: #ccc;
  transition: 0.2s;
  font-size: 14px;
}

.side-nav li:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
}

.side-nav li.active {
  background: #0171e9;
  color: #fff;
  font-weight: 600;
  border-right: 4px solid #a1d8f3;
}

.main-content {
  flex: 1;
  padding: 50px;
  overflow-y: auto;
}

.dashboard-header {
  margin-bottom: 40px;
}

.blue-txt {
  color: #0171e9;
  font-weight: 700;
}

.current-time {
  font-size: 14px;
  color: #888;
  margin-top: 5px;
}

.dash-home-grid {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.dash-card {
  flex: 1;
  min-width: 300px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
  border: 1px solid #eee;
}

.card-head h3 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 700;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  border-bottom: 1px solid #f9f9f9;
  padding-bottom: 8px;
}

.info-item .label {
  width: 80px;
  color: #999;
  font-weight: 600;
  flex-shrink: 0;
}

.info-item .val {
  color: #444;
}

.stat-grid {
  display: flex;
  gap: 10px;
}

.stat-box {
  flex: 1;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  color: #666;
}

.stat-box span {
  display: block;
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 5px;
}

.stat-box.blue span {
  color: #0171e9;
}

.stat-box.red span {
  color: #dc3545;
}

.hospital-tbl {
  width: 100%;
  border-collapse: collapse;
}

.hospital-tbl th,
.hospital-tbl td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
  font-size: 14px;
}

.hospital-tbl th {
  background: #f9f9f9;
  color: #666;
  font-weight: 600;
}

.bold-blue {
  color: #0171e9;
  font-weight: 600;
}

.bold-text {
  font-weight: 700;
  color: #333;
}

.btn-add-sm {
  background: #0171e9;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  margin-left: auto;
}

.btn-cancel-table {
  padding: 4px 8px;
  border: 1px solid #eee;
  background: #fff;
  color: #e03131;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.btn-complete {
  padding: 4px 8px;
  border: 1px solid #0171e9;
  background: #fff;
  color: #0171e9;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
}

.btn-complete:hover {
  background: #0171e9;
  color: #fff;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.active {
  background: #e3f2fd;
  color: #0171e9;
}

.status-badge.done {
  background: #f1f3f5;
  color: #868e96;
}

.status-badge.cancel {
  background: #ffebee;
  color: #c92a2a;
}

.toggle-group {
  display: flex;
  gap: 5px;
  margin-left: auto;
}

.toggle-group button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
  font-size: 13px;
}

.toggle-group button.active {
  background: #0171e9;
  color: #fff;
  border-color: #0171e9;
}

.res-highlight {
  background: #f0f7ff;
  padding: 20px;
  border-radius: 4px;
  border-left: 4px solid #0171e9;
}

.d-day {
  font-size: 24px;
  font-weight: 800;
  color: #0171e9;
  display: block;
  margin-bottom: 5px;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.auth-modal {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  width: 400px;
  text-align: center;
}

.auth-pw-input {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-btns button {
  padding: 10px 20px;
  margin: 0 5px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 600;
}

.btn-modal-confirm {
  background: #0171e9;
  color: #fff;
}

.btn-modal-cancel {
  background: #eee;
}

.view-section.centered {
  display: flex;
  justify-content: center;
}

.edit-card-wrap {
  width: 100%;
  max-width: 600px;
}

.f-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #f9f9f9;
  padding-bottom: 10px;
}

.f-row span {
  width: 100px;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.f-row input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.btn-blue-full {
  width: 100%;
  padding: 15px;
  background: #0171e9;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: 700;
  cursor: pointer;
}

.empty-msg {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-res {
  text-align: center;
  padding: 30px 0;
  color: #aaa;
}

/* 필터 탭 */
.filter-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.filter-btn {
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  padding: 5px 10px;
}

.filter-btn.active {
  color: #0171e9;
  border-bottom: 2px solid #0171e9;
}

/* 캘린더 */
.calendar-wrap {
  margin-top: 20px;
}

.cal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.cal-header h4 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.cal-header button {
  background: #fff;
  border: 1px solid #ddd;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border: 1px solid #eee;
}

.cal-day-head {
  text-align: center;
  padding: 10px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  font-size: 13px;
}

.cal-cell {
  min-height: 100px;
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 5px;
  position: relative;
  cursor: pointer;
}

.cal-cell:hover {
  background: #fcfcfc;
}

.cal-cell:nth-child(7n) {
  border-right: none;
}

.cal-cell.diff-month {
  background: #fcfcfc;
  color: #ddd;
}

.cal-cell.today {
  background: #f0f9ff;
}

.day-num {
  font-size: 12px;
  font-weight: 600;
  display: block;
  margin-bottom: 5px;
}

.cal-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cal-dot {
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 4px;
  background: #eee;
  color: #555;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cal-dot.active {
  background: #e3f2fd;
  color: #0171e9;
}

.cal-dot.done {
  background: #eee;
  color: #aaa;
  text-decoration: line-through;
}

.cal-dot.shift {
  background: #fff3bf;
  color: #e67700;
  font-weight: bold;
}
.split-view {
  display: flex;
  gap: 20px;
}

.flex-1 {
  flex: 1;
}

.calendar-wrap.mini .cal-cell {
  min-height: 40px;
  text-align: center;
}

.todo-input-box {
  display: flex;
  gap: 5px;
  margin-bottom: 15px;
}

.todo-input-box input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.todo-input-box button {
  background: #333;
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.todo-list {
  list-style: none;
  padding: 0;
}

.todo-list li {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.todo-list li input {
  margin-right: 10px;
}

.todo-list li span.done {
  text-decoration: line-through;
  color: #aaa;
}

.btn-del-x {
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
  font-size: 18px;
  margin-left: auto;
}

.btn-del-x:hover {
  color: #dc3545;
}

.red-alert {
  color: #dc3545;
  font-weight: 700;
  font-size: 13px;
}

.badge-gray {
  background: #eee;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}
</style>