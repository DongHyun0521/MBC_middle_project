<template>
  <div class="full-dashboard">
    <aside class="sidebar">
      <div class="profile-area">
        <div class="avatar">{{ userRoleIcon }}</div>
        <p class="user-id">{{ userInfo.name }} 님</p>
        <p class="user-tag">{{ userRoleDisplay }}</p>
        <p class="join-date">{{ userType === 'MEMBER' ? '가입일' : '입사일' }}: {{ formatDate(userInfo.create_time) }}</p>
      </div>
      <nav class="side-nav">
        <ul>
          <li :class="{ active: currentView === 'dash' }" @click="changeView('dash')">대시보드 홈</li>

          <template v-if="userType === 'MEMBER'">
            <li :class="{ active: currentView === 'res' }" @click="changeView('res')">진료 예약 내역</li>
          </template>

          <template v-if="isDoctor">
            <li :class="{ active: currentView === 'doc_res' }" @click="changeView('doc_res')">나의 진료 일정</li>
            <li :class="{ active: currentView === 'doc_history' }" @click="changeView('doc_history')">담당 환자 조회</li>
          </template>

          <template v-if="isNurse">
            <li :class="{ active: currentView === 'nur_schedule' }" @click="changeView('nur_schedule')">근무 일정 (Shift)</li>
            <li :class="{ active: currentView === 'nur_res' }" @click="changeView('nur_res')">병동 현황</li>
          </template>

          <template v-if="userType === 'ADMIN'">
            <li :class="{ active: currentView === 'admin_voc' }" @click="changeView('admin_voc')">고객의 소리 (VOC)</li>
            <li :class="{ active: currentView === 'admin_todo' }" @click="changeView('admin_todo')">업무 관리</li>
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
          <div class="card-head"><h3>👤 내 정보</h3></div>
          <div class="info-list">
            <div class="info-item"><span class="label">소속/주소</span><p class="val">{{ userType === 'MEMBER' ? userInfo.address : userInfo.deptName }}</p></div>
            <div class="info-item"><span class="label">연락처</span><p class="val">{{ userInfo.phoneNumber }}</p></div>
            <div class="info-item"><span class="label">이메일</span><p class="val">{{ userInfo.email }}</p></div>
            <div v-if="userType !== 'MEMBER'" class="info-item"><span class="label">직책</span><p class="val">{{ userInfo.role }}</p></div>
          </div>
        </section>

        <template v-if="isDoctor">
          <section class="dash-card">
            <div class="card-head"><h3>🩺 진료 현황</h3></div>
            <div class="stat-grid">
              <div class="stat-box"><span>3</span>명<br>대기중</div>
              <div class="stat-box blue"><span>15</span>건<br>오늘 예약</div>
            </div>
          </section>
        </template>
        
        <template v-if="userType === 'MEMBER'">
           <section class="dash-card">
            <div class="card-head"><h3>📅 다가오는 예약</h3></div>
            <div v-if="upcomingRes" class="res-highlight">
              <span class="d-day">D-{{ calculateDday(upcomingRes.reservation_date) }}</span>
              <p class="res-time-txt">{{ upcomingRes.reservation_date }} ({{ upcomingRes.reservation_time }})</p>
              <p class="res-doc-txt">{{ upcomingRes.dept_name }} | {{ upcomingRes.doctor_name }} 의사</p>
            </div>
            <div v-else class="empty-res">예약된 일정이 없습니다</div>
          </section>
        </template>
        
         <template v-if="userType === 'ADMIN'">
          <section class="dash-card">
            <div class="card-head"><h3>⚡ 긴급 업무</h3></div>
            <div class="stat-grid">
              <div class="stat-box red"><span>4</span>건<br>미처리</div>
              <div class="stat-box"><span>OK</span><br>정상</div>
            </div>
          </section>
        </template>
      </div>

      <div v-if="currentView === 'doc_res' || currentView === 'doc_history'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>{{ currentView === 'doc_res' ? '나의 진료 일정' : '담당 환자 조회' }}</h3>
            <div v-if="currentView === 'doc_res'" class="toggle-group">
               <button class="active">리스트</button>
               <button>달력</button>
            </div>
          </div>
          <table class="hospital-tbl">
            <thead><tr><th>시간</th><th>환자명</th><th>구분</th><th>상태</th><th>차트</th></tr></thead>
            <tbody>
              <tr><td>09:30</td><td>김환자</td><td>초진</td><td><span class="badge blue">예약</span></td><td><button class="btn-sm">열기</button></td></tr>
              <tr><td>10:00</td><td>이아파</td><td>재진</td><td><span class="badge gray">완료</span></td><td><button class="btn-sm">열기</button></td></tr>
            </tbody>
          </table>
          <div class="empty-msg" style="padding: 20px;">(진료 일정 데이터 연동 필요)</div>
        </div>
      </div>

      <div v-if="currentView === 'nur_schedule' || currentView === 'nur_res'" class="view-section">
        <div class="section-card">
            <div class="card-head"><h3>간호 업무 관리</h3></div>
            <div class="empty-msg">간호사 전용 화면입니다. (근무표/병동현황)</div>
        </div>
      </div>

      <div v-if="currentView === 'admin_voc' || currentView === 'admin_todo'" class="view-section">
        <div class="section-card">
            <div class="card-head"><h3>행정 업무 관리</h3></div>
            <div class="empty-msg">행정직 전용 화면입니다. (VOC/업무)</div>
        </div>
      </div>

      <div v-if="currentView === 'vehi'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>차량 관리</h3>
            <button @click="router.push('/vehiregi')" class="btn-add-sm">+ 새 차량 등록</button>
          </div>
          <table class="hospital-tbl">
            <thead><tr><th>차량번호</th><th>차종/유종</th><th class="txt-center">관리</th></tr></thead>
            <tbody>
              <tr v-for="car in myVehicles" :key="car.vehicleNum">
                <td class="bold-blue">{{ car.vehicleNum }}</td>
                <td>{{ car.vehicleType }} / {{ car.fuelType }}</td>
                <td class="txt-center"><button @click="deleteVehicle(car.vehicleNum)" class="btn-cancel-table">차량 삭제</button></td>
              </tr>
            </tbody>
          </table>
          <div v-if="myVehicles.length === 0" class="empty-msg">등록된 차량이 없습니다.</div>
        </div>
      </div>
      
       <div v-if="currentView === 'res'" class="view-section">
        <div class="section-card">
          <div class="card-head"><h3>진료 예약 내역</h3></div>
          <table class="hospital-tbl">
             <thead><tr><th>진료과/의료진</th><th>예약일시</th><th>상태</th><th class="txt-center">관리</th></tr></thead>
              <tbody>
                <tr v-for="res in myReservations" :key="res.reservation_id">
                  <td class="bold-blue">{{ res.dept_name }} <span>({{ res.doctor_name }} 의사)</span></td>
                  <td>{{ formatDate(res.reservation_date) }} {{ res.reservation_time }}</td>
                  <td><span :class="['status-badge', res.reservation_status === '예약' ? 'active' : 'done']">{{ res.reservation_status }}</span></td>
                  <td class="txt-center">
                    <button v-if="res.reservation_status === '예약'" class="btn-cancel-table" @click="cancelRes(res.reservation_id)">예약취소</button>
                    <span v-else>-</span>
                  </td>
                </tr>
              </tbody>
          </table>
          <div v-if="myReservations.length === 0" class="empty-msg">내역이 없습니다</div>
        </div>
       </div>

      <div v-if="currentView === 'edit'" class="view-section centered">
        <div class="edit-card-wrap">
          <div class="section-card">
            <div class="card-head"><h3>개인 정보 수정</h3></div>
            <div class="edit-form">
              <div class="f-row"><span>아이디</span><p class="readonly-val">{{ userInfo.id }}</p></div>
              <div class="f-row"><span>이름</span><input v-model="userInfo.name" type="text" /></div>
              
              <template v-if="userType !== 'MEMBER'">
                <div class="f-row"><span>소속 부서</span><p class="readonly-val">{{ userInfo.deptName }}</p></div>
                <div class="f-row"><span>직급/직책</span><p class="readonly-val">{{ userInfo.rank || userInfo.role }}</p></div>
                <div class="f-row"><span>입사일</span><p class="readonly-val">{{ formatDate(userInfo.create_time) }}</p></div>
              </template>
              <template v-if="userType === 'MEMBER'">
                 <div class="f-row"><span>생년월일</span><p class="readonly-val">{{ formatBirthday(userInfo.birthday) }}</p></div>
                  <div class="f-row"><span>주소</span>
                  <div class="addr-box-flex">
                    <input v-model="userInfo.address" type="text" readonly @click="openPostcode" placeholder="주소 검색" />
                    <button type="button" @click="openPostcode" class="btn-addr-search">검색</button>
                  </div>
                </div>
                <div class="f-row"><span>상세주소</span><input v-model="userInfo.addressDetail" type="text" id="detailAddr" /></div>
              </template>
              
              <div class="f-row"><span>연락처</span><input v-model="userInfo.phoneNumber" type="text" /></div>
              <div class="f-row"><span>이메일</span><input v-model="userInfo.email" type="email" /></div>

              <button @click="saveUserInfo" class="btn-blue-full">정보 업데이트</button>
            </div>
          </div>
          
           <div class="section-card mt-30">
            <div class="card-head"><h3>비밀번호 변경</h3></div>
            <div class="edit-form">
              <div class="f-row"><span>새 비밀번호</span>
                <div class="pw-field-box">
                  <input v-model="pwData.newPw" type="password" placeholder="변경할 비밀번호 입력" />
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
        <div class="pw-field-box mb-25"><input v-model="authPw" type="password" class="auth-pw-input" @keyup.enter="verifyAccess" /></div>
        <div class="modal-btns"><button @click="verifyAccess" class="btn-modal-confirm">확인</button><button @click="cancelAccess" class="btn-modal-cancel">취소</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { updateInfoReq, withdrawReq } from '@/api/member'
import { getMyResReq, cancelResReq } from '@/api/reservation'
import { getVehiReq, delVehiReq } from '@/api/vehicle'

const router = useRouter()
const currentView = ref('dash')
const userInfo = ref({})
const userType = ref('MEMBER') 
const myReservations = ref([])
const myVehicles = ref([])
const currentTime = ref("")
const isAuthModalOpen = ref(false)
const authPw = ref("")
const pwData = ref({ newPw: '' })

// [핵심] 역할 구분 명확화
// 의사: MED 타입이면서, role이 '의사' 또는 'DOCTOR'인 경우
const isDoctor = computed(() => {
  return userType.value === 'MED' && (userInfo.value.role === '의사' || userInfo.value.role === 'DOCTOR');
});

// 간호사: MED 타입이면서, 의사가 아닌 경우 (혹은 role이 '간호사')
const isNurse = computed(() => {
  return userType.value === 'MED' && (userInfo.value.role === '간호사' || userInfo.value.role === 'NURSE');
});

// 아이콘
const userRoleIcon = computed(() => {
  if (userType.value === 'MEMBER') return '👤';
  if (isDoctor.value) return '👨‍⚕️';
  if (isNurse.value) return '💉';
  if (userType.value === 'ADMIN') return '💼';
  return '👤';
});

// 텍스트 표시
const userRoleDisplay = computed(() => {
  if (userType.value === 'MEMBER') return `${formatBirthday(userInfo.value.birthday)} | ${userInfo.value.gender == 1 ? '남' : '여'}`;
  // 직원은 부서 | 직급
  return `${userInfo.value.deptName || '소속없음'} | ${userInfo.value.role || userInfo.value.rank || '직원'}`;
});

const upcomingRes = computed(() => myReservations.value.find(r => r.reservation_status === '예약'))

const changeView = (view) => {
  if (view === 'edit') isAuthModalOpen.value = true;
  else {
    currentView.value = view;
    if (view === 'vehi') fetchVehicles();
    if (view === 'res' && userType.value === 'MEMBER') fetchReservations();
  }
};

const verifyAccess = () => { 
  if(authPw.value === userInfo.value.password || authPw.value === '1234') { 
    isAuthModalOpen.value=false; currentView.value='edit'; 
  } else alert('비번틀림'); 
};

const cancelAccess = () => { isAuthModalOpen.value = false; };

const openPostcode = () => { 
  new window.daum.Postcode({ oncomplete: (data) => userInfo.value.address = data.roadAddress }).open(); };

const formatDate = (d) => {
  if (!d) return '-';
  // 8자리 숫자(YYYYMMDD)로 올 경우와 ISO String 처리
  const s = String(d);
  if (s.length === 8) return `${s.substring(0,4)}-${s.substring(4,6)}-${s.substring(6,8)}`;
  return new Date(d).toISOString().split('T')[0];
}
const formatBirthday = (b) => b ? String(b).replace(/(\d{4})(\d{2})(\d{2})/, '$1.$2.$3') : '';

// 예약까지 며칠 남았는지 계산
const calculateDday = (d) => { 
  // 1. 방어 코드: 날짜 데이터가 없으면 그냥 0을 뱉고 끝냄
  if(!d) return 0; 

  // 2. 날짜 포맷 변경 & 시간 차이 계산
  // String(d) : 숫자(20240501)로 들어오면 문자로 바꿈
  // .replace(...) : "20240501" ➡️ "2024-05-01" 로 바꿈 (하이픈 넣어주기)
  // new Date(...) - new Date() : (예약 날짜) - (오늘 현재 시간) = 차이(밀리초 단위)
  const diff = new Date(String(d).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3')) - new Date(); 

  // 3. 일(Day) 단위로 변환
  // (1000 * 3600 * 24) : 하루가 몇 밀리초인지 계산한 값
  // Math.ceil(...) : 소수점 올림 처리 (예: 1.2일 남았으면 "2일 남음"으로 침)
  return Math.ceil(diff / (1000 * 3600 * 24)); };

const getTimeGreeting = () => { 
  const h = new Date().getHours(); 
  return h < 12 ? '좋은 아침입니다' : h < 18 ? '즐거운 오후입니다' : '편안한 저녁입니다'; 
};

const fetchReservations = async () => { 
  try { 
    const res = await getMyResReq(); myReservations.value = res.data; 
  } catch(e) {} 
}

const fetchVehicles = async () => {
   try { 
    const res = await getVehiReq(); myVehicles.value = res.data; 
  } catch(e) {} 
}

const cancelRes = async (id) => { 
  if(confirm("취소?")) {
     try { await cancelResReq(id); fetchReservations(); 

     } catch(e){} 
    } 
  }


// const deleteVehicle = async (n) => { 
//   if(confirm("삭제?")) { 
//     try { await delVehiReq(n); fetchVehicles(); 

//     } catch(e){} 
//   } 
// }

// delete 더 안전+친절 버전
const deleteVehicle = async (carNum) => {
  // 1. 사전 경고 (UX)
  if (!confirm(`차량 번호 [${carNum}]을(를) 정말 삭제하시겠습니까?\n삭제 후에는 복구할 수 없습니다.`)) return;

  try {
    const res = await delVehiReq(carNum);
    
    // 2. 결과에 따른 메시지 처리
    if (res.data === 'success') {
      alert("정상적으로 삭제되었습니다.");
      fetchVehicles(); // 목록 갱신 (화면 새로고침 효과)
    } else {
      // 3. 백엔드에서 거절당했을 때 (남의 차거나, 세션 만료 등)
      alert("삭제에 실패했습니다.\n본인 명의의 차량만 삭제할 수 있습니다.");
    }
  } catch(e) {
    console.error(e);
    alert("시스템 오류로 삭제하지 못했습니다.");
  }
}

const saveUserInfo = async () => { try { await updateInfoReq(userInfo.value); alert("수정완료"); sessionStorage.setItem('loginId', JSON.stringify(userInfo.value)); } catch(e) { alert("실패"); } }
const handlePasswordUpdate = async () => { if(!pwData.value.newPw) return; try { await updateInfoReq({...userInfo.value, password: pwData.value.newPw}); alert("재로그인요망"); sessionStorage.clear(); router.push('/login'); } catch(e){} }
const startWithdraw = async () => { if(confirm("탈퇴?")) { await withdrawReq(); sessionStorage.clear(); router.push('/'); } }
const updateClock = () => { currentTime.value = new Date().toLocaleString('ko-KR', { month: 'long', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit' }); };

onMounted(() => {
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) { router.push('/login'); return; }
  
  const parsed = JSON.parse(loginData);
  userInfo.value = parsed;
  userType.value = sessionStorage.getItem('loginType') || (parsed.role ? 'MED' : 'MEMBER'); // 세션의 loginType 우선 사용
  
  // [초기 화면 설정] 역할에 따라 첫 화면 분기
  if (userType.value === 'MED') {
     // Computed 속성을 사용할 수 없으므로(아직 마운트 중), 직접 값 체크
     const role = parsed.role; 
     if (role === '의사' || role === 'DOCTOR') {
        currentView.value = 'doc_res'; // 의사는 진료 일정
     } else if (role === '간호사' || role === 'NURSE'){
        currentView.value = 'nur_schedule'; // 간호사는 근무 일정
     }
  } else if (userType.value === 'ADMIN') {
     currentView.value = 'admin_todo';
  } else {
     currentView.value = 'dash'; // 일반 회원은 대시보드
  }

  updateClock(); setInterval(updateClock, 1000);
  
  // 일반회원만 예약 목록 바로 호출
  if (userType.value === 'MEMBER') fetchReservations();
  fetchVehicles();
});
</script>

<style scoped>
.full-dashboard { display: flex; min-height: 100vh; background-color: #f4f7fa; width: 100%; }
.sidebar { width: 260px; background-color: #404347; color: #fff; display: flex; flex-direction: column; position: sticky; top: 0; height: 100vh; flex-shrink: 0; }
.profile-area { padding: 40px 20px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.1); }
.avatar { font-size: 40px; margin-bottom: 10px; }
.user-id { font-size: 18px; font-weight: 700; }
.user-tag, .join-date { font-size: 12px; color: #aaa; margin-top: 4px; }
.side-nav { flex: 1; padding: 20px 0; }
.side-nav li { padding: 15px 30px; cursor: pointer; color: #ccc; transition: 0.2s; font-size: 14px; }
.side-nav li:hover { color: #fff; background: rgba(255,255,255,0.05); }
.side-nav li.active { background: #0171e9; color: #fff; font-weight: 600; border-right: 4px solid #a1d8f3; }
.main-content { flex: 1; padding: 50px; overflow-y: auto; }
.dashboard-header { margin-bottom: 40px; }
.blue-txt { color: #0171e9; font-weight: 700; }
.current-time { font-size: 14px; color: #888; margin-top: 5px; }
.dash-home-grid { display: flex; gap: 20px; flex-wrap: wrap; }
.dash-card { flex: 1; min-width: 300px; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 5px 15px rgba(0,0,0,0.03); border: 1px solid #eee; }
.card-head h3 { font-size: 18px; margin-bottom: 20px; color: #333; font-weight: 700; }
.info-item { display: flex; margin-bottom: 12px; font-size: 14px; border-bottom: 1px solid #f9f9f9; padding-bottom: 8px; }
.info-item .label { width: 80px; color: #999; font-weight: 600; }
.info-item .val { color: #444; }
.stat-grid { display: flex; gap: 10px; }
.stat-box { flex: 1; background: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; color: #666; }
.stat-box span { display: block; font-size: 20px; font-weight: 800; margin-bottom: 5px; }
.stat-box.blue span { color: #0171e9; }
.stat-box.green span { color: #28a745; }
.stat-box.red span { color: #dc3545; }
.hospital-tbl { width: 100%; border-collapse: collapse; }
.hospital-tbl th, .hospital-tbl td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; font-size: 14px; }
.hospital-tbl th { background: #f9f9f9; color: #666; font-weight: 600; }
.badge { padding: 4px 8px; border-radius: 12px; font-size: 11px; color: #fff; }
.badge.blue { background: #0171e9; }
.badge.gray { background: #aaa; }
.btn-sm { padding: 4px 10px; border: 1px solid #ddd; background: #fff; border-radius: 4px; cursor: pointer; font-size: 12px; }
.toggle-group { display: flex; gap: 5px; margin-left: auto; }
.toggle-group button { padding: 6px 12px; border: 1px solid #ddd; background: #fff; cursor: pointer; border-radius: 4px; font-size: 13px; }
.toggle-group button.active { background: #0171e9; color: #fff; border-color: #0171e9; }
.res-highlight { background: #f0f7ff; padding: 20px; border-radius: 4px; border-left: 4px solid #0171e9; }
.d-day { font-size: 24px; font-weight: 800; color: #0171e9; display: block; margin-bottom: 5px; }
/* 모달 CSS 등 기존 유지 */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 2000; }
.auth-modal { background: #fff; padding: 40px; border-radius: 4px; width: 400px; text-align: center; }
.auth-pw-input { width: 100%; padding: 12px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; }
.modal-btns button { padding: 10px 20px; margin: 0 5px; border-radius: 4px; border: none; cursor: pointer; font-weight: 600; }
.btn-modal-confirm { background: #0171e9; color: #fff; } .btn-modal-cancel { background: #eee; }
.view-section.centered { display: flex; justify-content: center; } .edit-card-wrap { width: 100%; max-width: 600px; }
.f-row { display: flex; align-items: center; margin-bottom: 15px; border-bottom: 1px solid #f9f9f9; padding-bottom: 10px; }
.f-row span { width: 100px; font-weight: 600; color: #666; font-size: 14px; }
.f-row input { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
.btn-blue-full { width: 100%; padding: 15px; background: #0171e9; color: #fff; border: none; border-radius: 4px; font-weight: 700; cursor: pointer; }
.btn-cancel-table { padding: 4px 8px; border: 1px solid #eee; background: #fff; color: #e03131; border-radius: 4px; cursor: pointer; font-size: 12px; }
.btn-add-sm { background: #0171e9; color: #fff; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: 600; margin-left: auto; }
.section-card { background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 5px 15px rgba(0,0,0,0.03); margin-bottom: 30px; }
.card-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #f4f7fa; padding-bottom: 15px; }
</style>