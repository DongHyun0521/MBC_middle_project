<template>
  <div class="login-page-wrap">
    <div class="portal-login-container">
      <div class="portal-info-panel">
        <div class="brand-area">
          <img src="@/assets/logo.png" alt="S-HOSPITAL" class="brand-logo">
          <h2 class="brand-name">S-HOSPITAL</h2>
        </div>

        <div class="service-intro">
          <p class="intro-main">안전하고 편리한<br>환자 맞춤형 서비스</p>
          <ul class="service-list">
            <li><span>✔</span> 온라인 진료 예약 및 내역 확인</li>
            <li><span>✔</span> 검사 결과 및 증명서 발급</li>
            <li><span>✔</span> 의료진 전용 환자 관리 시스템</li>
          </ul>
        </div>

        <div class="cs-info">
          <p>도움이 필요하신가요?</p>
          <span class="cs-tel" style="font-size: 20px; font-weight: 600;">고객센터 1588-0000</span>
        </div>
      </div>

      <div class="portal-form-panel">
        <div class="login-type-tabs">
          <button 
            type="button" 
            :class="['tab-btn', { active: loginType === 'member' }]" 
            @click="setLoginType('member')"
          >일반회원</button>
          <button 
            type="button" 
            :class="['tab-btn', { active: loginType === 'staff' }]" 
            @click="setLoginType('staff')"
          >의료진</button>
        </div>

        <div class="form-title-group">
          <h3>{{ loginType === 'member' ? '환자 로그인' : '의료진 로그인' }}</h3>
          <div class="title-bar"></div>
        </div>

        <form @submit.prevent="handleLogin" class="login-form transition-box" :key="loginType">
          <div class="input-field">
            <span class="field-label">아이디</span>
            <input type="text" v-model="user.id" :placeholder="loginType === 'member' ? 'ID' : '의료진 사번/ID'" @input="checkID" />
          </div>

          <div class="input-field">
            <span class="field-label">비밀번호</span>
            <div class="pw-box">
              <input :type="showPw ? 'text' : 'password'" v-model="user.password" placeholder="Password" />
              <button type="button" class="eye-toggle" @click="showPw = !showPw">
                <svg v-if="showPw" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
              </button>
            </div>
          </div>

          <div class="helper-row">
            <div class="save-chk">
              <input type="checkbox" id="saveId" v-model="saveId" @change="handleSaveIdChange" />
              <label for="saveId">아이디 저장</label>
            </div>
            <div class="find-group">
              <span @click="isFindIdModalOpen = true">아이디 찾기</span>
              <span class="bar">|</span>
              <span>비밀번호 찾기</span>
            </div>
          </div>

          <button type="submit" class="btn-login-submit">LOGIN</button>

          <div class="regi-footer">
            <p>{{ loginType === 'member' ? '아직 계정이 없으신가요?' : '아직 의료진 등록 전이신가요?' }}</p>
            <router-link :to="loginType === 'member' ? '/regi' : '/regi'">회원가입 바로가기</router-link>
          </div>
        </form>
      </div>
    </div>

    <div v-if="isFindIdModalOpen" class="modal-overlay">
      <div class="modal-card">
        <h3>아이디 찾기</h3>
        <div class="modal-body">
          <div v-if="!foundId">
            <input v-model="findUser.name" type="text" placeholder="성함" class="m-input" />
            <input v-model="findUser.email" type="email" placeholder="이메일" class="m-input" />
            <button class="btn-m-find" @click="handleFindId">아이디 확인</button>
          </div>
          <div v-else class="m-result">
            <div class="id-res-box">{{ foundId }}</div>
            <button class="btn-m-find" @click="copyAndLogin">아이디 적용</button>
          </div>
          <button class="btn-m-close" @click="closeFindIdModal">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { loginRequest, findIdRequest } from '@/api/member.js';
import { useCookies } from 'vue3-cookies';

const { cookies } = useCookies();
const router = useRouter();
const route = useRoute();

const loginType = ref('member'); // 💡 member(일반), staff(의료진)
const showPw = ref(false);
const user = ref({ id: '', password: '', type: 'member' }); // type 추가 🚀
const saveId = ref(false);

const setLoginType = (type) => {
  loginType.value = type;
  user.value.type = type; // 💡 요청 객체에도 타입 빡!
  user.value.id = '';
  user.value.password = '';
};

const handleSaveIdChange = () => {
  if (saveId.value) {
    const confirmSave = confirm("개인정보 보호를 위해 본인 기기에서만 저장해 주세요");
    if (!confirmSave) saveId.value = false;
  }
};

const isFindIdModalOpen = ref(false);
const foundId = ref("");
const findUser = ref({ name: '', email: '' });

const checkID = () => {
  user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, '');
};

const handleFindId = async () => {
  if (!findUser.value.name || !findUser.value.email) {
    alert("정보를 입력해 주세요"); return;
  }
  try {
    const res = await findIdRequest(findUser.value);
    if (res.data) foundId.value = res.data;
    else alert("일치하는 정보가 없습니다");
  } catch (err) { alert("오류 발생"); }
};

const closeFindIdModal = () => { isFindIdModalOpen.value = false; foundId.value = ""; };
const copyAndLogin = () => { user.value.id = foundId.value; closeFindIdModal(); };

onMounted(() => {
  const savedCookie = cookies.get('userId');
  if (savedCookie) { user.value.id = savedCookie; saveId.value = true; }
});

const handleLogin = async () => {
  if (!user.value.id || !user.value.password) { 
    alert('아이디와 비밀번호를 입력해 주십시오'); return; 
  }
  try {
    const response = await loginRequest(user.value); // 💡 백엔드에서 user.type 확인 가능!
    if (!response.data) { alert('정보가 올바르지 않습니다'); return; }
    
    // 의료진 여부를 포함해서 세션 저장
    sessionStorage.setItem('login', JSON.stringify(response.data));
    if (saveId.value) cookies.set("userId", user.value.id, '7d');
    else cookies.remove("userId");
    
    router.push(route.query.redirect || "/");
  } catch (err) { alert('로그인 오류'); }
}
</script>

<style scoped>
.login-page-wrap { display: flex; justify-content: center; align-items: center; min-height: 80vh; background-color: #f9f9f9; padding: 50px 20px; }
.portal-login-container { display: flex; width: 100%; max-width: 1000px; background: #fff; border: 1px solid #eee; border-radius: 4px; box-shadow: 0 15px 40px rgba(0, 0, 0, 0.05); overflow: hidden; }

/* 🏰 좌측 패널 */
.portal-info-panel { flex: 1; background-color: #f9f9f9; padding: 60px; border-right: 1px solid #eee; display: flex; flex-direction: column; justify-content: space-between; }
.brand-area { display: flex; align-items: center; gap: 12px; }
.brand-logo { height: 35px; }
.brand-name { font-size: 20px; font-weight: 700; color: #0171e9; }
.intro-main { font-size: 28px; font-weight: 600; color: #404347; line-height: 1.4; margin: 40px 0 20px; }
.service-list { list-style: none; padding: 0; }
.service-list li { font-size: 14px; color: #777; margin-bottom: 12px; display: flex; gap: 8px; font-weight: 300; }
.service-list span { color: #a1d8f3; font-weight: bold; }

/* 📝 우측 패널 */
.portal-form-panel { flex: 1.2; padding: 60px 80px; background: #fff; }

/* 💡 [수정] 탭 스타일 🧩 */
.login-type-tabs { display: flex; gap: 10px; margin-bottom: 40px; }
.tab-btn {
  flex: 1; padding: 12px; border: 1px solid #eee; background: #f9f9f9;
  color: #999; font-weight: 600; cursor: pointer; border-radius: 4px; transition: 0.3s;
}
.tab-btn.active {
  background: #0171e9; color: #fff; border-color: #0171e9;
  box-shadow: 0 4px 10px rgba(1, 113, 233, 0.2);
}

.form-title-group { margin-bottom: 45px; }
.form-title-group h3 { font-size: 22px; color: #404347; font-weight: 600; margin-bottom: 10px; }
.title-bar { width: 30px; height: 3px; background-color: #0171e9; }

.input-field { margin-bottom: 25px; }
.field-label { display: block; font-size: 12px; font-weight: 700; color: #999; margin-bottom: 8px; }
input[type="text"], input[type="password"] { width: 100%; padding: 12px 0; border: none; border-bottom: 1px solid #ddd; font-size: 16px; color: #4e4e4e; transition: 0.3s; background: transparent; }
input:focus { border-bottom-color: #0171e9; outline: none; }
.pw-box { position: relative; }
.eye-toggle { position: absolute; right: 0; top: 12px; background: none; border: none; cursor: pointer; color: #ccc; }

.helper-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; font-size: 13px; }
.save-chk { display: flex; align-items: center; gap: 8px; color: #777; }
.find-group { color: #aaa; }
.find-group span { cursor: pointer; }
.bar { margin: 0 10px; color: #eee; }

.btn-login-submit { width: 100%; padding: 18px; background-color: #0171e9; color: #fff; border: none; border-radius: 4px; font-size: 16px; font-weight: 600; letter-spacing: 2px; cursor: pointer; transition: 0.3s; }
.btn-login-submit:hover { background-color: #0056b3; box-shadow: 0 5px 15px rgba(1, 113, 233, 0.2); }

.regi-footer { margin-top: 35px; text-align: center; }
.regi-footer p { font-size: 13px; color: #bbb; margin-bottom: 8px; }
.regi-footer a { color: #0171e9; font-weight: 600; font-size: 14px; text-decoration: none; border-bottom: 1px solid #0171e9; }

/* 애니메이션 🪄 */
.transition-box { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

/* 모달 동일 유지 🧩 */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 2000; }
.modal-card { background: #fff; padding: 40px; border-radius: 4px; width: 100%; max-width: 380px; text-align: center; }
.m-input { width: 100%; padding: 12px; border: 1px solid #eee; border-radius: 4px; margin-bottom: 12px; background: #f9f9f9; }
.btn-m-find { width: 100%; padding: 14px; background: #0171e9; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-weight: 600; margin-bottom: 8px; }
.btn-m-close { width: 100%; padding: 12px; background: #fff; color: #999; border: 1px solid #eee; border-radius: 4px; cursor: pointer; }
.id-res-box { font-size: 24px; font-weight: 700; color: #0171e9; padding: 20px; background: #f0f7ff; margin-bottom: 20px; }
</style>