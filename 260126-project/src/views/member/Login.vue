<template>
  <div class="login-container">
    <div class="login-card">
      <h2>로그인</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="userId">아이디</label>
          <input type="text" id="userId" v-model="user.id" placeholder="아이디" @input="checkID"/>
        </div>

        <div class="input-group">
          <label for="userPw">비밀번호</label>
          <div class="pw-wrapper">
            <input :type="showPw ? 'text' : 'password'" id="userPw" v-model="user.password" placeholder="비밀번호"/>

            <button type="button" class="eye-btn" @click="showPw = !showPw">
              <svg v-if="showPw" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                <line x1="1" y1="1" x2="23" y2="23"></line>
              </svg>
            </button>

          </div>
        </div>

        <div class="login-action-row">
          <div class="save-id-box">
            <input type="checkbox" id="saveId" v-model="saveId" @change="handleSaveIdChange"/>
            <label for="saveId">아이디 저장</label>
          </div>
          <button type="submit" class="login-submit-btn">로그인</button>
        </div>
      </form>

      <div class="auth-links">
        <span class="link-txt" @click="isFindIdModalOpen = true">아이디 찾기</span>
        <span class="divider">|</span>
        <router-link to="/find-pw">비밀번호 찾기</router-link>
        <span class="divider">|</span>
        <router-link to="/regi">회원가입</router-link>
      </div>
    </div>

    <div v-if="isFindIdModalOpen" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-header">
          <h3>아이디 찾기</h3>
          <button class="close-btn" @click="closeFindIdModal">✕</button>
        </div>
        
        <div class="modal-body">
          <div v-if="!foundId">
            <div class="modal-input-group">
              <label>이름</label>
              <input v-model="findUser.name" type="text" placeholder="성함" />
            </div>
            <div class="modal-input-group">
              <label>전화번호</label>
              <div class="tel-row">
                <select v-model="findTel.t1">
                  <option value="010">010</option><option value="011">011</option><option value="070">070</option>
                </select>
                <input v-model="findTel.t2" type="text" maxlength="4" placeholder="0000" />
                <input v-model="findTel.t3" type="text" maxlength="4" placeholder="0000" />
              </div>
            </div>
            <div class="modal-input-group">
              <label>이메일</label>
              <input v-model="findUser.email" type="email" placeholder="example@naver.com" />
            </div>
            <button class="find-btn" @click="handleFindId">아이디 찾기</button>
          </div>

          <div v-else class="result-area">
            <p>회원님의 아이디를 찾았습니다!</p>
            <div class="id-display">{{ foundId }}</div>
            <button class="find-btn" @click="copyAndLogin">아이디 적용 후 로그인</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { loginRequest, findIdRequest } from '@/api/member.js'; 
import { useCookies } from 'vue3-cookies';

const { cookies } = useCookies();
const router = useRouter();
const route = useRoute();

const showPw = ref(false);
const user = ref({ id: '', password: '' });
const saveId = ref(false);

// 아이디 저장 버튼 클릭시
const handleSaveIdChange = () => {
  if (saveId.value) { // 체크가 된 상태라면
    const confirmSave = confirm("여럿이 사용하는 환경에서는 아이디를 저장하지 않는 것이 좋습니다. 아이디를 저장하시겠습니까?");
    if (!confirmSave) {
      saveId.value = false; // 취소 누르면 다시 체크 해제!
    }
  }
};

const isFindIdModalOpen = ref(false);
const foundId = ref("");
const findUser = ref({ name: '', email: '', phoneNumber: '' });
const findTel = ref({ t1: '010', t2: '', t3: '' });

const checkID = () => {
  user.value.id = user.value.id.toLowerCase().replace(/[^a-z0-9]/g, '');
};

const handleFindId = async () => {
  findUser.value.phoneNumber = `${findTel.value.t1}${findTel.value.t2}${findTel.value.t3}`;
  if (!findUser.value.name || !findUser.value.email || findUser.value.phoneNumber.length < 10) {
    alert("정보를 모두 입력해 주세요"); return;
  }
  try {
    const res = await findIdRequest(findUser.value);
    if (res.data) foundId.value = res.data;
    else alert("일치하는 정보가 없습니다");
  } catch (err) { alert("오류 발생"); }
};

const closeFindIdModal = () => {
  isFindIdModalOpen.value = false; foundId.value = "";
  findUser.value = { name: '', email: '', phoneNumber: '' };
};

const copyAndLogin = () => {
  user.value.id = foundId.value; closeFindIdModal();
};

const savedCookie = cookies.get('userId');
if(savedCookie){ user.value.id = savedCookie; saveId.value = true; }

const handleLogin = async()=>{
  if(!user.value.id || !user.value.password){ alert('아이디와 비밀번호를 입력해 주십시오'); return; }
  try{
    const response = await loginRequest(user.value)
    if (!response.data) { alert('로그인 정보를 확인해 주세요'); return; }
    sessionStorage.setItem('login', JSON.stringify(response.data))
    if(saveId.value) cookies.set("userId", user.value.id, '7d');
    else cookies.remove("userId");
    router.push(route.query.redirect || "/");
  } catch (err){ alert('로그인 오류'); }  
}
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
.login-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; }
.login-card { width: 100%; max-width: 400px; padding: 40px; background: #fff; border-radius: 20px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
.input-group { margin-bottom: 20px; text-align: left; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 14px; }
input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
.login-action-row { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; gap: 15px; }
.save-id-box { display: flex; align-items: center; gap: 5px; font-size: 14px; white-space: nowrap; }
.save-id-box input { width: 18px; height: 18px; margin: 0; cursor: pointer; }
.login-submit-btn { flex: 1; padding: 12px; background: #007bff; color: #fff; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; }
.pw-wrapper { position: relative; display: flex; align-items: center; }
.eye-btn { position: absolute; right: 10px; background: none; border: none; cursor: pointer; color: #999; display: flex; }
.auth-links { margin-top: 25px; font-size: 13px; color: #888; }
.link-txt { cursor: pointer; }
.link-txt:hover { color: #007bff; text-decoration: underline; }
.divider { margin: 0 10px; color: #eee; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 2000; }
.modal-card { background: #fff; width: 90%; max-width: 380px; padding: 30px; border-radius: 20px; }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.close-btn { background: none; border: none; font-size: 20px; cursor: pointer; }
.modal-input-group { margin-bottom: 15px; text-align: left; }
.modal-input-group label { font-size: 12px; font-weight: bold; color: #666; }
.tel-row { display: flex; gap: 5px; margin-top: 5px; }
.tel-row select { width: 70px; padding: 8px; border: 1px solid #ddd; border-radius: 8px; }
.find-btn { width: 100%; padding: 12px; background: #007bff; color: #fff; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; margin-top: 10px; }
.id-display { font-size: 22px; font-weight: 800; color: #007bff; margin: 20px 0; padding: 15px; background: #f0f7ff; border-radius: 10px; text-align: center; }
</style>