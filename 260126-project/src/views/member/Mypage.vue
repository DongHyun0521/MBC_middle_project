<template>
  <div class="full-dashboard">
    <aside class="sidebar">
      <div class="profile-area">
        <div class="avatar">👤</div>
        <p class="user-id">{{ userInfo.name }} 님</p>
        <p class="user-name-tag">{{ formatBirthday(userInfo.birthday) }} | {{ userInfo.gender == 1 ? '남' : '여' }}</p>
      </div>
      <nav class="side-nav">
        <ul>
          <li :class="{ active: currentView === 'dash' }" @click="changeView('dash')">🏠 대시보드 홈</li>
          <li @click="alertReady" class="ready">📅 예약 조회</li>
          <li :class="{ active: currentView === 'vehi' }" @click="changeView('vehi')">🚗 차량 관리</li>
          <li @click="alertReady" class="ready">📄 진료 내역</li>
          <li :class="{ active: currentView === 'edit' }" @click="changeView('edit')">⚙️ 내 정보 수정</li>
        </ul>
      </nav>
      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">로그아웃</button>
      </div>
    </aside>

    <main class="main-content">
      <header class="header-wide">
        <div class="title-group">
          <h2>안녕하세요, <span class="blue-point">{{ userInfo.name }}</span>님. 즐거운 하루 되세요!</h2>
          <p class="date">{{ currentTime }}</p>
        </div>
      </header>

      <div v-if="currentView === 'dash'" class="content-grid">
        <section class="grid-card">
          <h3>👤 내 프로필 요약</h3>
          <div class="summary-list">
            <p><b>성함:</b> {{ userInfo.name }}</p>
            <p><b>연락처:</b> {{ userInfo.phoneNumber }}</p>
            <p><b>생년월일:</b> {{ formatBirthday(userInfo.birthday) }}</p>
            <p><b>주소:</b> {{ userInfo.address }} {{ userInfo.addressDetail }}</p>
          </div>
        </section>
        <section class="grid-card">
          <h3>🚗 내 차량 정보</h3>
          <p class="count-txt">등록된 차량: <b>{{ myVehicles.length }}</b>대</p>
          <button @click="changeView('vehi')" class="detail-link">상세보기 ➔</button>
        </section>
      </div>

      <div v-if="currentView === 'edit'" class="edit-section">
        <section class="grid-card">
          <div class="card-head"><h3>⚙️ 상세 정보 관리</h3></div>
          <div class="card-body">
            <div class="row left-align"><span class="label">아이디</span><span class="val gray">{{ userInfo.id }}</span></div>
            <div class="row"><span class="label">이름</span><input v-model="userInfo.name" class="edit-in" /></div>
            <div class="row left-align"><span class="label">생년월일</span><span class="val">{{ formatBirthday(userInfo.birthday) }}</span></div>
            <div class="row left-align"><span class="label">성별</span><span class="val">{{ userInfo.gender == 1 ? '남 ♂️' : '여 ♀️' }}</span></div>
            <div class="row"><span class="label">이메일</span><input v-model="userInfo.email" class="edit-in" /></div>
            <div class="row"><span class="label">전화번호</span><input v-model="userInfo.phoneNumber" class="edit-in" /></div>
            <div class="row"><span class="label">주소</span><input v-model="userInfo.address" class="edit-in" /></div>
            <div class="row"><span class="label">상세주소</span><input v-model="userInfo.addressDetail" class="edit-in" /></div>
            <button @click="saveUserInfo" class="submit-btn hover-effect">수정 내용 저장</button>
          </div>
        </section>

        <section class="grid-card pw-box">
          <h3>🔒 보안 설정 (비밀번호 변경)</h3>
          <button v-if="pwStep === 1" @click="pwStep = 2" class="submit-btn gray hover-effect">비밀번호 변경하기</button>
          
          <div v-if="pwStep === 2" class="row-stack">
            <div class="pw-wrapper">
              <input v-model="pwData.currentPw" :type="showPwCur ? 'text' : 'password'" class="full-in" placeholder="현재 비밀번호" />
              <button type="button" class="eye-btn" @click="showPwCur = !showPwCur">
                <svg v-if="showPwCur" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
              </button>
            </div>
            <div class="btn-group">
              <button @click="verifyCurrentPw" class="submit-btn hover-effect">확인</button>
              <button @click="pwStep = 1" class="submit-btn gray hover-effect">취소</button>
            </div>
          </div>

          <div v-if="pwStep === 3" class="row-stack">
            <div class="pw-wrapper">
              <input v-model="pwData.newPw" :type="showPwNew ? 'text' : 'password'" @input="validateNewPw" class="full-in" placeholder="새 비밀번호 (대문자+특수문자 필수)" />
              <button type="button" class="eye-btn" @click="showPwNew = !showPwNew">
                <svg v-if="showPwNew" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
              </button>
            </div>
            <p v-if="newPwErrorMsg" class="error-txt">{{ newPwErrorMsg }}</p>
            <input v-model="pwData.confirmPw" :type="showPwNew ? 'text' : 'password'" class="full-in" placeholder="새 비밀번호 확인" />
            <button @click="handlePasswordUpdate" class="submit-btn hover-effect">비밀번호 변경 완료</button>
          </div>
        </section>

        <div class="withdraw-area">
          <p class="withdraw-info">더 이상 서비스를 이용하지 않으시나요?</p>
          <button @click="isWithdrawModalOpen = true" class="withdraw-link-small">회원 탈퇴하기</button>
        </div>
      </div>

      <div v-if="currentView === 'vehi'" class="vehicle-section">
        <section class="grid-card full-row">
          <div class="card-head">
            <h3>🚗 차량 등록 현황</h3>
            <button @click="router.push('/vehiregi')" class="action-btn success hover-effect">+ 새 차량 등록</button>
          </div>
          <table class="wide-tbl">
            <thead><tr><th>차량번호</th><th>차종/유종</th><th class="txt-right">관리</th></tr></thead>
            <tbody>
              <tr v-for="car in myVehicles" :key="car.vehicleNum">
                <td class="bold-blue">{{ car.vehicleNum }}</td>
                <td>{{ car.vehicleType }} / {{ car.fuelType }}</td>
                <td class="txt-right">
                  <button @click="openEditModal(car)" class="btn-text edit">수정</button>
                  <button @click="deleteVehicle(car.vehicleNum)" class="btn-text del">삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>
      </div>
    </main>

    <div v-if="isEditAuthModalOpen" class="modal-overlay">
      <div class="modal-card auth-card">
        <h3>🔒 본인 확인</h3>
        <p class="modal-desc">개인정보 보호를 위해 비밀번호를 입력해 주세요.</p>
        <input v-model="editAuthPw" type="password" class="full-in" placeholder="비밀번호 입력" @keyup.enter="verifyEditAccess" />
        <div class="btn-group">
          <button @click="verifyEditAccess" class="submit-btn hover-effect">확인</button>
          <button @click="closeEditAuth" class="submit-btn gray hover-effect">취소</button>
        </div>
      </div>
    </div>

    <div v-if="isWithdrawModalOpen" class="modal-overlay">
      <div class="modal-card auth-card">
        <h3>⚠️ 회원 탈퇴</h3>
        <p class="modal-desc">탈퇴를 위해 아이디와 비밀번호를 모두 입력해 주세요.</p>
        <input v-model="withdrawId" type="text" class="full-in" placeholder="아이디 입력" />
        <input v-model="withdrawPw" type="password" class="full-in" placeholder="비밀번호 입력" style="margin-top:10px" />
        <div class="btn-group">
          <button @click="confirmWithdraw" class="submit-btn del-btn hover-effect">탈퇴 승인</button>
          <button @click="isWithdrawModalOpen = false; withdrawPw = ''; withdrawId = '';" class="submit-btn gray hover-effect">취소</button>
        </div>
      </div>
    </div>

    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-card">
        <h3>🚗 차량 정보 수정</h3>
        <div class="modal-body">
          <div class="modal-row"><label>차량번호</label><input v-model="editData.vehicleNum" readonly class="edit-in gray" /></div>
          <div class="modal-row">
            <label>차종</label>
            <select v-model="editData.vehicleType" class="edit-in">
              <option value="경차">경차</option>
              <option value="승용차">승용차</option>
              <option value="SUV">SUV</option>
              <option value="기타">기타</option>
            </select>
          </div>
          <div class="modal-row">
            <label>유종</label>
            <select v-model="editData.fuelType" class="edit-in">
              <option value="휘발유">휘발유</option>
              <option value="경유">경유</option>
              <option value="전기">전기</option>
              <option value="LPG">LPG</option>
            </select>
          </div>
        </div>
        <div class="btn-group">
          <button @click="saveVehicleEdit" class="submit-btn hover-effect">저장</button>
          <button @click="isModalOpen = false" class="submit-btn gray hover-effect">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { updateInfoRequest, withdrawRequest, loginRequest } from '@/api/member'
import { getMyVehiclesRequest, deleteVehicleRequest, updateVehicleRequest } from '@/api/vehicle'

const router = useRouter()
const currentView = ref('dash')
const lastView = ref('dash')
const currentTime = ref("") 
let timer = null

const userInfo = ref({ id: '', name: '', email: '', phoneNumber: '', address: '', addressDetail: '', birthday: '', gender: '' })
const myVehicles = ref([])

// 💡 보안 및 모달 상태
const isEditAuthModalOpen = ref(false)
const editAuthPw = ref("")
const isWithdrawModalOpen = ref(false)
const withdrawId = ref("")
const withdrawPw = ref("")

// 💡 차량 수정 상태 (여기 에러 해결!)
const isModalOpen = ref(false)
const editData = ref({ vehicleNum: '', vehicleType: '', fuelType: '' })

// 💡 비밀번호 상태
const pwStep = ref(1)
const pwData = ref({ currentPw: '', newPw: '', confirmPw: '' })
const showPwCur = ref(false)
const showPwNew = ref(false)
const newPwErrorMsg = ref("")

const isPwMatch = computed(() => pwData.value.newPw === pwData.value.confirmPw)

const formatBirthday = (bth) => {
  if (!bth) return bth
  const bthStr = String(bth)
  if (bthStr.length !== 8) return bthStr
  return `${bthStr.substring(0, 4)}년 ${bthStr.substring(4, 6)}월 ${bthStr.substring(6, 8)}일`
}

const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }) + 
                     ' ' + now.toLocaleTimeString('ko-KR')
}

const changeView = (view) => {
  if (view === 'edit') {
    lastView.value = currentView.value;
    isEditAuthModalOpen.value = true;
  } else {
    currentView.value = view;
    sessionStorage.setItem('active_menu', view);
  }
}

const verifyEditAccess = async () => {
  try {
    const res = await loginRequest({ id: userInfo.value.id, password: editAuthPw.value })
    if (res.data) {
      isEditAuthModalOpen.value = false;
      editAuthPw.value = "";
      currentView.value = 'edit';
      sessionStorage.setItem('active_menu', 'edit');
    } else { alert("비밀번호 불일치!"); editAuthPw.value = ""; }
  } catch (err) { alert("인증 오류") }
}

const closeEditAuth = () => {
  isEditAuthModalOpen.value = false;
  editAuthPw.value = "";
  currentView.value = lastView.value;
}

const confirmWithdraw = async () => {
  if (withdrawId.value !== userInfo.value.id) { alert("아이디를 확인해 주세요."); return; }
  try {
    const res = await loginRequest({ id: withdrawId.value, password: withdrawPw.value })
    if (res.data) {
      if (confirm('정말로 탈퇴하시겠습니까?')) {
        const result = await withdrawRequest()
        if (result.data === "success") { alert('탈퇴 완료!'); handleLogout(); }
      }
    } else alert("비밀번호 불일치!")
  } catch (err) { alert("오류") }
}

const validateNewPw = () => {
  const pwRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{6,16}$/
  if (!pwData.value.newPw) newPwErrorMsg.value = "비밀번호 입력 필수"
  else if (!pwRegex.test(pwData.value.newPw)) newPwErrorMsg.value = "대문자+특수문자 조합(6~16자) 🔒"
  else newPwErrorMsg.value = ""
}

onMounted(() => {
  updateClock(); timer = setInterval(updateClock, 1000)
  const savedView = sessionStorage.getItem('active_menu')
  if (savedView) currentView.value = savedView
  const loginData = sessionStorage.getItem('login')
  if (!loginData) { router.push('/login'); return }
  userInfo.value = JSON.parse(loginData)
  fetchVehicleList()
})

onUnmounted(() => { if (timer) clearInterval(timer) })

const fetchVehicleList = async () => {
  try {
    const res = await getMyVehiclesRequest()
    myVehicles.value = res.data || []
  } catch (err) { console.error("로드 실패") }
}

const saveUserInfo = async () => {
  try {
    const res = await updateInfoRequest(userInfo.value)
    if (res.data === "success") { alert('저장 완료!'); sessionStorage.setItem('login', JSON.stringify(userInfo.value)) }
  } catch (err) { alert('실패') }
}

const verifyCurrentPw = async () => {
  try {
    const res = await loginRequest({ id: userInfo.value.id, password: pwData.value.currentPw })
    if (res.data) pwStep.value = 3
    else alert("비밀번호 틀림")
  } catch (err) { alert("오류") }
}

const handlePasswordUpdate = async () => {
  if (!isPwMatch.value) return
  try {
    const res = await updateInfoRequest({ ...userInfo.value, password: pwData.value.newPw })
    if (res.data === "success") { alert("변경 완료!"); handleLogout() }
  } catch (err) { alert("실패") }
}

// 💡 뻥 뚫어주는 수정 로직!
const openEditModal = (car) => { 
  editData.value = { ...car }; 
  isModalOpen.value = true; 
}

const saveVehicleEdit = async () => {
  try {
    const res = await updateVehicleRequest(editData.value)
    if (res.data === "success") { alert('수정 완료!'); isModalOpen.value = false; fetchVehicleList(); }
  } catch (err) { alert("실패") }
}

const deleteVehicle = async (vNum) => {
  if (confirm('삭제?')) { try { await deleteVehicleRequest(vNum); fetchVehicleList(); } catch (err) { alert("실패") } }
}

const handleLogout = () => { sessionStorage.clear(); router.push('/login') }
const alertReady = () => alert('준비 중!')
</script>

<style scoped>
/* 🎨 스타일 가이드 (기존과 동일) */
.full-dashboard { display: flex; width: 100%; min-height: 100vh; background: #f4f7fa; }
.sidebar { width: 260px; background: #1a222d; color: #fff; padding: 40px 0; display: flex; flex-direction: column; }
.user-name-tag { font-size: 14px; color: #007bff; font-weight: bold; margin-top: 5px; }
.side-nav li { padding: 15px 25px; cursor: pointer; color: #94a3b8; list-style: none; transition: all 0.2s ease; }
.side-nav li:hover { background: #2d3748; color: #fff; padding-left: 30px; }
.side-nav li.active { background: #007bff; color: #fff; font-weight: bold; border-left: 5px solid #fff; }
.main-content { flex-grow: 1; padding: 40px 60px; }
.grid-card { background: #fff; border-radius: 20px; padding: 25px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.hover-effect { transition: all 0.2s ease; }
.hover-effect:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); opacity: 0.9; }
.row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f1f5f9; align-items: center; }
.row.left-align { justify-content: flex-start; gap: 20px; }
.label { color: #888; font-weight: 600; width: 120px; text-align: left; }
.val { text-align: left; flex-grow: 1; }
.edit-in, .full-in { padding: 12px; border: 1px solid #e2e8f0; border-radius: 8px; width: 100%; max-width: 400px; }
.submit-btn { width: 100%; padding: 15px; background: #007bff; color: #fff; border: none; border-radius: 10px; cursor: pointer; font-weight: bold; margin-top: 15px; }
.withdraw-area { margin-top: 40px; padding-bottom: 40px; text-align: center; }
.withdraw-link-small { background: none; border: none; color: #bbb; text-decoration: underline; font-size: 12px; cursor: pointer; }
.pw-wrapper { position: relative; width: 100%; max-width: 400px; display: flex; align-items: center; }
.eye-btn { position: absolute; right: 12px; background: none; border: none; color: #94a3b8; cursor: pointer; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-card { background: #fff; padding: 30px; border-radius: 20px; width: 400px; text-align: center; }
.auth-card { border-top: 5px solid #007bff; }
.error-txt { color: #ff4d4f; font-size: 12px; margin-top: 5px; text-align: left; }
</style>