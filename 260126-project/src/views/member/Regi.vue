<template>
  <div class="regi-page-wrap">
    <div class="regi-card">
      <div class="regi-type-tabs">
        <button type="button" :class="['tab-btn', { active: regiType === 'member' }]" @click="setRegiType('member')">일반회원 가입</button>
        <button type="button" :class="['tab-btn', { active: regiType === 'staff' }]" @click="setRegiType('staff')">의료진 가입</button>
      </div>

      <div class="regi-header">
        <h2>{{ regiType === 'member' ? '회원가입' : '의료진 등록' }}</h2>
        <div class="title-bar"></div>
        <p class="regi-desc">{{ regiType === 'member' ? 'S-HOSPITAL의 통합 회원이 되어보세요' : 'S-HOSPITAL의 전문 의료진으로 등록해 주세요' }}</p>
      </div>

      <form @submit.prevent="handleRegi" class="regi-form transition-box" :key="regiType">
        <div class="input-section">
          <label>아이디</label>
          <div class="id-flex-row">
            <input type="text" v-model="user.id" placeholder="6~16자 영문 소문자/숫자" @input="checkID" />
            <button type="button" @click="idcheck" class="check-btn">중복확인</button>
          </div>
          <ul class="validation-guide">
            <li :class="{ 'valid': isIdLengthValid }">6~16자 영문 소문자/숫자 조합</li>
            <li :class="{ 'valid': isIdChecked }">아이디 중복 확인 완료</li>
          </ul>
        </div>

        <div v-if="regiType === 'staff'" class="staff-special-fields">
          <div class="grid-row">
            <div class="input-section">
              <label>의사 면허 번호</label>
              <input type="text" v-model="user.license_number" placeholder="숫자 입력" @input="checkLicense" />
            </div>
            <div class="input-section">
              <label>진료과 선택</label>
              <select v-model="user.dept_id">
                <option value="">진료과 선택</option>
                <option v-for="d in depts" :key="d.dept_id" :value="d.dept_id">{{ d.dept_name }}</option>
              </select>
            </div>
          </div>
        </div>

        <div class="input-section">
          <label>비밀번호</label>
          <div class="pw-field-wrap">
            <input :type="showPw ? 'text' : 'password'" v-model="user.password" @input="checkPw" placeholder="6~16자 (대문자, 특수문자 포함)" class="pw-input" />
            <button type="button" class="eye-toggle" @click="showPw = !showPw">
              <svg v-if="showPw" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
            </button>
          </div>
          <ul class="validation-guide">
            <li :class="{ 'valid': isPwLengthValid }">6~16자 이내</li>
            <li :class="{ 'valid': hasUppercase }">영문 대문자 포함</li>
            <li :class="{ 'valid': hasSpecial }">특수문자 포함</li>
          </ul>
        </div>

        <div class="input-section">
          <label>비밀번호 확인</label>
          <input type="password" v-model="user.pwCk" placeholder="비밀번호 재입력" @input="checkPwCk" />
          <p v-if="pwCkMsg" :class="['status-txt', isPwCkValid ? 'success' : 'error']">{{ pwCkMsg }}</p>
        </div>

        <div class="grid-row">
          <div class="input-section">
            <label>이름</label>
            <input type="text" v-model="user.name" placeholder="실명 입력" @input="checkName" />
          </div>
          <div class="input-section">
            <label>성별</label>
            <div class="gender-selector">
              <input type="radio" id="m" value="1" v-model="user.gender" class="hide-radio" />
              <label for="m">남자</label>
              <input type="radio" id="f" value="2" v-model="user.gender" class="hide-radio" />
              <label for="f">여자</label>
            </div>
          </div>
        </div>

        <div class="input-section">
          <label>생년월일</label>
          <input type="text" v-model="user.birthday" @input="checkBirth" placeholder="YYYYMMDD (예: 20000101)" maxlength="8" />
        </div>

        <div class="input-section">
          <label>휴대폰 번호</label>
          <div class="tel-flex-row">
            <select v-model="tel1" @change="syncPhone">
              <option value="010">010</option>
              <option value="011">011</option>
            </select>
            <span class="tel-dash">-</span>
            <input type="text" v-model="tel2" maxlength="4" @input="syncPhone" />
            <span class="tel-dash">-</span>
            <input type="text" v-model="tel3" maxlength="4" @input="syncPhone" />
          </div>
        </div>

        <div class="input-section">
          <label>주소</label>
          <div class="addr-flex-row">
            <input type="text" v-model="user.address" placeholder="주소 검색 버튼을 눌러주세요" readonly @click="openPostcode" />
            <button type="button" @click="openPostcode" class="check-btn">주소 검색</button>
          </div>
          <input type="text" id="userAddrDetail" v-model="user.addressDetail" placeholder="상세 주소를 입력하세요" class="mt-8" />
        </div>

        <button type="submit" class="regi-submit-btn">{{ regiType === 'member' ? '가입하기' : '의료진 등록 신청' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// 💡 의료진용 심부름(staffRegisterRequest) 추가 🪄
import { idcheckRequest, registerRequest, staffRegisterRequest } from '@/api/member';
import axios from 'axios';

const router = useRouter();
const regiType = ref('member'); 
const depts = ref([]); 

const user = ref({
  id: '', password: '', pwCk: '', name: '', birthday: '', gender: '1',
  phoneNumber: '', email: '', address: '', addressDetail: '',
  type: 'member', license_number: '', dept_id: '' 
});

const tel1 = ref('010'); const tel2 = ref(''); const tel3 = ref('');
const isIdChecked = ref(false);
const showPw = ref(false);
const pwCkMsg = ref("");

const setRegiType = (type) => {
  regiType.value = type; user.value.type = type; isIdChecked.value = false;
};

const isIdLengthValid = computed(() => user.value.id.length >= 6 && user.value.id.length <= 16);
const hasUppercase = computed(() => /[A-Z]/.test(user.value.password));
const hasSpecial = computed(() => /[!@#$%^&*()_+]/.test(user.value.password));
const isPwLengthValid = computed(() => user.value.password.length >= 6 && user.value.password.length <= 16);
const isPwCkValid = computed(() => user.value.password === user.value.pwCk && user.value.pwCk !== '');

const checkID = () => { user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, ''); isIdChecked.value = false; }
const checkLicense = () => { user.value.license_number = user.value.license_number.replace(/[^0-9]/g, ''); }
const syncPhone = () => { 
  tel2.value = tel2.value.replace(/[^0-9]/g, '');
  tel3.value = tel3.value.replace(/[^0-9]/g, '');
  user.value.phoneNumber = `${tel1.value}${tel2.value}${tel3.value}`; 
}

const idcheck = async () => {
  if (!isIdLengthValid.value) { alert('아이디는 6~16자여야 합니다'); return; }
  try {
    const res = await idcheckRequest(user.value.id);
    if (res.data === true) { alert("사용 가능합니다"); isIdChecked.value = true; }
    else { alert("이미 사용 중입니다"); isIdChecked.value = false; }
  } catch (err) { alert("시스템 오류") }
}

const checkPw = () => {};
const checkPwCk = () => {
  pwCkMsg.value = user.value.password !== user.value.pwCk ? "비밀번호가 일치하지 않습니다" : "비밀번호가 일치합니다"
}

const checkBirth = () => { user.value.birthday = user.value.birthday.replace(/[^0-9]/g, ''); };
const checkName = () => { user.value.name = user.value.name.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣]/g, ''); }

const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      user.value.address = data.roadAddress || data.jibunAddress;
      document.getElementById("userAddrDetail")?.focus();
    }
  }).open();
};

// 🚀 [수정] 회원가입 핸들러 (데이터 타입 변환 💉)
const handleRegi = async () => {
  if (!isIdChecked.value) { alert("아이디 중복 확인 필수!"); return; }
  if (regiType.value === 'staff' && (!user.value.license_number || !user.value.dept_id)) {
    alert("면허 번호와 진료과를 선택해 주세요 🩺"); return;
  }

  // 1. DB 타입에 맞게 데이터 변환 (String -> Number) 🪄
  const sendData = {
    ...user.value,
    birthday: Number(user.value.birthday), // 숫자로 빡!
    gender: Number(user.value.gender),     // 숫자로 빡!
    dept_id: user.value.dept_id ? Number(user.value.dept_id) : null
  };

  try {
    let res;
    if (regiType.value === 'member') {
      res = await registerRequest(sendData);
    } else {
      res = await staffRegisterRequest(sendData); // 의료진 전용 호출 ✨
    }

    if (res.data === "success" || res.data === true) {
      alert("환영합니다! 가입이 완료되었습니다 🎉");
      router.push('/login');
    } else {
      alert("가입 실패! 정보를 다시 확인해 주세요 🧩");
    }
  } catch (err) {
    console.error(err);
    alert("가입 실패 ㅠㅠ 백엔드 에러를 확인하세요 👋");
  }
}

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/member/depts')
    depts.value = res.data
  } catch(e) { 
    depts.value = [{dept_id: 1, dept_name: '내과'}, {dept_id: 2, dept_name: '외과'}];
  }
})
</script>

<style scoped>
/* 🔒 [중복 눈 아이콘 제거 핵심 💉] */
input::-ms-reveal,
input::-ms-clear {
  display: none;
}

.regi-page-wrap { display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f9f9f9; padding: 60px 20px; }
.regi-card { width: 100%; max-width: 650px; background: #fff; padding: 60px 50px; border-radius: 4px; border: 1px solid #eee; box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05); }

.regi-type-tabs { display: flex; gap: 10px; margin-bottom: 40px; }
.tab-btn { flex: 1; padding: 14px; border: 1px solid #eee; background: #f9f9f9; color: #999; font-weight: 600; cursor: pointer; border-radius: 4px; transition: 0.3s; }
.tab-btn.active { background: #0171e9; color: #fff; border-color: #0171e9; }

.regi-header { text-align: center; margin-bottom: 45px; }
.regi-header h2 { font-size: 28px; color: #404347; margin-bottom: 12px; font-weight: 700; }
.title-bar { width: 40px; height: 3px; background: #0171e9; margin: 0 auto 20px; }

.regi-form { display: flex; flex-direction: column; gap: 30px; }
.input-section label { display: block; font-size: 14px; font-weight: 600; color: #4e4e4e; margin-bottom: 10px; }

input, select { width: 100%; padding: 14px; border: 1px solid #ddd; border-radius: 4px; font-size: 15px; background: #f9f9f9; transition: 0.3s; }
input:focus { border-color: #0171e9; background: #fff; outline: none; }

.id-flex-row, .addr-flex-row { display: flex; gap: 10px; }
.check-btn { width: 130px; background: #404347; color: #fff; border: none; border-radius: 4px; font-weight: 600; cursor: pointer; }

.pw-field-wrap { position: relative; width: 100%; }
.eye-toggle { 
  position: absolute; right: 15px; top: 50%; transform: translateY(-50%); 
  background: none; border: none; cursor: pointer; color: #ccc; 
  display: flex; align-items: center; padding: 0;
}

.tel-flex-row { display: flex; align-items: center; gap: 8px; }
.tel-flex-row select { width: 100px; flex-shrink: 0; }
.tel-flex-row input { flex: 1; text-align: center; }
.tel-dash { color: #ccc; font-weight: bold; }

.staff-special-fields { background: #f0f7ff; padding: 25px; border-radius: 4px; border: 1px dashed #a1d8f3; margin-bottom: 10px; }
.grid-row { display: flex; gap: 20px; }
.grid-row > div { flex: 1; }

.gender-selector { display: flex; border: 1px solid #ddd; border-radius: 4px; overflow: hidden; height: 48px; }
.hide-radio { display: none !important; }
.gender-selector label { flex: 1; display: flex; align-items: center; justify-content: center; background: #f9f9f9; color: #888; cursor: pointer; transition: 0.2s; margin: 0; }
.hide-radio:checked + label { background: #0171e9; color: #fff; font-weight: 600; }

.validation-guide { list-style: none; padding: 8px 0 0; display: flex; gap: 15px; }
.validation-guide li { font-size: 11px; color: #bbb; display: flex; align-items: center; gap: 4px; }
.validation-guide li.valid { color: #0171e9; font-weight: 600; }

.status-txt { font-size: 12px; margin-top: 8px; }
.success { color: #28a745; }
.error { color: #dc3545; }

.regi-submit-btn { width: 100%; padding: 18px; background: #0171e9; color: #fff; border: none; border-radius: 4px; font-size: 17px; font-weight: 600; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.regi-submit-btn:hover { background: #0056b3; transform: translateY(-3px); box-shadow: 0 10px 20px rgba(1, 113, 233, 0.2); }

.mt-8 { margin-top: 8px; }
.transition-box { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
</style>