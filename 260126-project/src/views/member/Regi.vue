<template>
  <div class="regi-container">
    <h2>회원가입</h2>

    <form @submit.prevent="handleRegi">
      <div class="input-group">
        <label for="userId">아이디</label>
        <div class="id-box">
          <input type="text" id="userId" v-model="user.id" ref="idInput" placeholder="아이디" @input="checkID"/>
          <button type="button" @click="idcheck" class="idcheck-btn">중복확인</button>
        </div>
        <ul class="guide-list">
          <li :class="{ 'valid': isIdLengthValid }">{{ isIdLengthValid ? '✅' : '※' }} 6~16자 영문 소문자/숫자</li>
          <li :class="{ 'valid': isIdChecked }">{{ isIdChecked ? '✅' : '※' }} 중복 확인 완료</li>
        </ul>
        <p v-if="idcheckMsg" :class="{'success-msg': isIdChecked, 'error-msg': !isIdChecked}">{{ idcheckMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userPw">비밀번호</label>
        <div class="pw-wrapper">
          <input :type="showPw ? 'text' : 'password'" id="userPw" v-model="user.password" @input="checkPw" placeholder="6~16자(대문자, 특수문자 포함)"/>
          <button type="button" class="eye-btn" @click="showPw = !showPw">
            <svg v-if="showPw" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
          </button>
        </div>
        
        <ul class="guide-list">
          <li :class="{ 'valid': isPwLengthValid }">{{ isPwLengthValid ? '✅' : '※' }} 6~16자 이내</li>
          <li :class="{ 'valid': hasUppercase }">{{ hasUppercase ? '✅' : '※' }} 영문 대문자 포함</li>
          <li :class="{ 'valid': hasSpecial }">{{ hasSpecial ? '✅' : '※' }} 특수문자 포함</li>
        </ul>
        <p v-if="pwMsg" class="error-msg">{{ pwMsg }}</p>
      </div>

      <div class="input-group">
        <label for="PwCheck">비밀번호 재확인</label>
        <input type="password" id="PwCheck" v-model="user.pwCk" placeholder="비밀번호 재확인" @focus="handleFocusPwCk" @input="checkPwCk"/>
        <p v-if="pwCkMsg" :class="{'success-msg': isPwCkValid, 'error-msg': !isPwCkValid && user.pwCk !== ''}">{{ pwCkMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userName">이름</label>
        <input type="text" id="userName" v-model="user.name" placeholder="이름(실명)" @input="checkName"/>
        <ul class="guide-list">
          <li :class="{ 'valid': isNameValid }">{{ isNameValid ? '✅' : '※' }} 한글 2자 이상</li>
        </ul>
        <p v-if="nameMsg" class="error-msg">{{ nameMsg }}</p>
      </div>

      <div class="input-group birth-gender">
        <div class="birth-box">
          <label for="userBirth">생년월일</label>
          <input type="text" id="userBirth" v-model="user.birthday" @input="checkBirth" placeholder="예: 20000132" maxlength="8"/>
          <ul class="guide-list">
            <li :class="{ 'valid': isBirthValid }">{{ isBirthValid ? '✅' : '※' }} 숫자 8자리 (날짜 형식)</li>
          </ul>
          <p v-if="birthMsg" class="error-msg">{{ birthMsg }}</p>
        </div>
        <br/>

        <div class="gender-box">
          <label>성별</label>
          <div class="gender-btn-group">
            <input type="radio" id="male" value="1" v-model="user.gender" class="hidden-radio"/>
            <label for="male" class="gender-label">남자</label>
            
            <input type="radio" id="female" value="2" v-model="user.gender" class="hidden-radio"/>
            <label for="female" class="gender-label">여자</label>
          </div>
        </div>
      </div>

      <div class="input-group">
        <label for="userPhone">휴대폰</label>
        <div class="tel-row">
          <select v-model="tel1" class="tel-select" @change="syncPhone">
            <option value="010">010</option>
            <option value="011">011</option>
            <option value="070">070</option>
            <option value="02">02</option>
            <option value="031">031</option>
          </select>
          <span class="dash">-</span>
          <input type="text" v-model="tel2" maxlength="4" class="tel-in" placeholder="0000" @input="syncPhone" />
          <span class="dash">-</span>
          <input type="text" v-model="tel3" maxlength="4" class="tel-in" placeholder="0000" @input="syncPhone" />
        </div>
        <ul class="guide-list">
          <li :class="{ 'valid': isPhoneValid }">{{ isPhoneValid ? '✅' : '❌' }} 전화번호 형식 일치</li>
        </ul>
        <p v-if="phoneMsg" class="error-msg">{{ phoneMsg }}</p>
      </div>
      
      <div class="input-group">
        <label for="userEmail">이메일</label>
        <input type="email" id="userEmail" v-model="user.email" @input="checkEmail" placeholder="example@naver.com"/>
        <ul class="guide-list">
          <li :class="{ 'valid': isEmailValid }">{{ isEmailValid ? '✅' : '❌' }} 올바른 이메일 형식</li>
        </ul>
        <p v-if="emailMsg" class="error-msg">{{ emailMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userAddr">주소</label>
        <div class="addr-box">
          <input type="text" id="userAddr" v-model="user.address" placeholder="주소를 입력해 주세요." readonly @click="openPostcode"/>
          <button type="button" @click="openPostcode" class="addr-btn">주소 검색</button>
        </div>
        <input type="text" id="userAddrDetail" v-model="user.addressDetail" placeholder="예) S아파트 101동 202호"/>
      </div>

      <br/>
      <button type="submit" class="submit-btn">회원가입</button>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { idcheckRequest, registerRequest } from '@/api/member';

const router = useRouter();
const idInput = ref(null);
const showPw = ref(false); // 💡 눈 표시 상태

const user = ref({
  id: '', password: '', pwCk: '', name: '', birthday: '', gender: '1',
  phoneNumber: '', email: '', address: '', addressDetail: ''
});

const tel1 = ref('010'); const tel2 = ref(''); const tel3 = ref('');
const isIdChecked = ref(false);
const idcheckMsg = ref(""); const pwMsg = ref(""); const emailMsg = ref("");
const phoneMsg = ref(""); const birthMsg = ref(""); const pwCkMsg = ref(""); const nameMsg = ref("");

const isIdLengthValid = computed(() => user.value.id.length >= 6 && user.value.id.length <= 16);
const hasUppercase = computed(() => /[A-Z]/.test(user.value.password));
const hasSpecial = computed(() => /[!@#$%^&*()_+]/.test(user.value.password));
const isPwLengthValid = computed(() => user.value.password.length >= 6 && user.value.password.length <= 16);
const isPwCkValid = computed(() => user.value.password === user.value.pwCk && user.value.pwCk !== '');
const isNameValid = computed(() => /^[가-힣]{2,}$/.test(user.value.name));
const isBirthValid = computed(() => user.value.birthday.length === 8 && birthMsg.value === "");
const isPhoneValid = computed(() => /^(010|011|070|02|031|032)\d{7,8}$/.test(user.value.phoneNumber));
const isEmailValid = computed(() => /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(user.value.email));

const syncPhone = () => {
  tel2.value = tel2.value.replace(/[^0-9]/g, '');
  tel3.value = tel3.value.replace(/[^0-9]/g, '');
  user.value.phoneNumber = `${tel1.value}${tel2.value}${tel3.value}`;
  checkPhone();
};

const checkID = async () => {
  // 💡 한글/특수문자 입력 즉시 컷
  user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, '');
  isIdChecked.value = false;
  if (!user.value.id) idcheckMsg.value = "아이디를 입력해 주세요"
  else if (user.value.id.length < 6 || user.value.id.length > 16) idcheckMsg.value = "6~16자리 사이로 입력해 주세요"
  else idcheckMsg.value = "";
}

const idcheck = async () => {
  user.value.id = user.value.id.trim();
  const idFullRegex = /^[a-z0-9]{6,16}$/
  if(!idFullRegex.test(user.value.id)){ alert('아이디 형식을 다시 확인해 주세요'); idInput.value?.focus(); return; }
  try {
    const res = await idcheckRequest(user.value.id);
    if(res.data === true) { idcheckMsg.value = "사용 가능한 아이디 입니다"; isIdChecked.value = true; }
    else { idcheckMsg.value = "이미 사용중인 아이디 입니다"; isIdChecked.value = false; user.value.id = ""; idInput.value?.focus(); }
  } catch(err){ idcheckMsg.value = "오류 발생" }
}

const checkPw = () => {
  const pwRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{6,16}$/;
  if (!user.value.password) pwMsg.value = "비밀번호를 입력해 주세요";
  else if (user.value.password.length < 6 || user.value.password.length > 16) pwMsg.value = "6~16자리 사이로 입력해 주세요";
  else if (!pwRegex.test(user.value.password)) pwMsg.value = "대문자 및 특수문자를 포함해 주세요";
  else pwMsg.value = "";
};

const handleFocusPwCk = () => { if (!user.value.pwCk) pwCkMsg.value = "비밀번호를 한 번 더 입력해 주세요" }
const checkPwCk = () => {
  if (!user.value.pwCk) pwCkMsg.value = "비밀번호를 한 번 더 입력해 주세요"
  else if (user.value.password !== user.value.pwCk) pwCkMsg.value = "비밀번호가 일치하지 않습니다"
  else pwCkMsg.value = "비밀번호가 일치합니다"
}

const checkName = () => {
  user.value.name = user.value.name.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  if(!user.value.name) nameMsg.value = "이름을 입력해 주세요";
  else if(!/^[가-힣]{2,}$/.test(user.value.name)) nameMsg.value = "이름은 2자 이상으로 입력해 주세요";
  else nameMsg.value ="";
}

const checkBirth = () => {
  user.value.birthday = user.value.birthday.replace(/[^0-9]/g, '');
  const bth = user.value.birthday;
  if(bth.length === 8){
    const y = Number(bth.substring(0,4)); const m = Number(bth.substring(4,6)); const d = Number(bth.substring(6,8));
    const date = new Date(y, m-1, d); const today = new Date(); today.setHours(0,0,0,0);
    if(date.getFullYear() !== y || date.getMonth() + 1 !== m || date.getDate() !== d) birthMsg.value = "존재하지 않는 날짜입니다";
    else if(date > today) birthMsg.value = "오늘 이후의 날짜는 입력할 수 없습니다";
    else if(y < 1900) birthMsg.value = "올바른 출생 연도를 입력해 주세요";
    else birthMsg.value = "";
  }
};

const checkPhone = () => {
  if(!user.value.phoneNumber) phoneMsg.value = "번호를 입력해 주세요";
  else if(!/^(010|011|070|02|031|032)\d{7,8}$/.test(user.value.phoneNumber)) phoneMsg.value = "형식이 맞지 않습니다";
  else phoneMsg.value = "";
};

const checkEmail = () => {
  if (!user.value.email) emailMsg.value = "이메일을 입력해 주세요";
  else if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(user.value.email)) emailMsg.value = "형식이 맞지 않습니다";
  else emailMsg.value = "";
};

const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      user.value.address = data.roadAddress || data.jibunAddress;
      document.getElementById("userAddrDetail")?.focus();
    }
  }).open();
};

const handleRegi = async () => {
  const checkList = { id: "아이디", password: "비밀번호", pwCk: "비밀번호 확인", name: "이름", birthday: "생년월일", phoneNumber: "휴대폰", email: "이메일", address: "주소" };
  for (const key in checkList) { if (!user.value[key] || user.value[key].toString().trim() === "") { alert(`[${checkList[key]}] 필수 입력!`); return; } }
  if (!isIdChecked.value) { alert("아이디 중복 확인 필수!"); return; }
  try {
    const res = await registerRequest(user.value)
    if (res.data === "success" || res.data === true) { alert("가입 성공! 🎉"); router.push('/login'); }
  } catch (err) { alert("오류 발생"); }
}
</script>

<style scoped>
/* 🎨 아이콘 정렬을 위한 CSS 추가 */
.pw-wrapper { position: relative; display: flex; align-items: center; }
.eye-btn { position: absolute; right: 10px; background: none; border: none; cursor: pointer; color: #999; display: flex; align-items: center; }
.eye-btn:hover { color: #007bff; }

.regi-container { max-width: 500px; margin: 40px auto; padding: 30px; background: #fff; border-radius: 20px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
.input-group { margin-bottom: 25px; text-align: left; }
.id-box, .addr-box { display: flex; gap: 10px; }
input, select { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; margin-top: 5px; font-size: 14px; }
.guide-list { list-style: none; padding: 8px 5px; margin: 0; font-size: 11px; color: #dc3545; }
.guide-list li.valid { color: #28a745; font-weight: bold; }
.gender-btn-group { display: flex; gap: 10px; margin-top: 10px; }
.hidden-radio { display: none !important; }
.gender-label { flex: 1; padding: 12px; border: 1px solid #ddd; border-radius: 8px; text-align: center; cursor: pointer; transition: all 0.2s; background: #f9f9f9; }
.hidden-radio:checked + .gender-label { background: #007bff; color: #fff; border-color: #007bff; font-weight: bold; }
.tel-row { display: flex; align-items: center; gap: 8px; }
.tel-select { width: 100px; background: #fff; }
.tel-in { text-align: center; }
.success-msg { color: #28a745; font-size: 12px; margin-top: 5px; }
.error-msg { color: #dc3545; font-size: 12px; margin-top: 5px; }
.submit-btn { width: 100%; padding: 15px; background: #007bff; color: #fff; border: none; border-radius: 10px; cursor: pointer; font-size: 16px; font-weight: bold; }
</style>