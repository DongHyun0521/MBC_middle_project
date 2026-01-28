<template>
  <div>
    <h2>로그인</h2>
    
    <form @submit.prevent="handleLogin"> <!--@submit.prevent: Form 태그의 기본 제출 동작(새로고침)을 중단하고, 지정된 함수만 실행-->
      <div>
        <label for="userId">아이디</label>
        <input type="text" id="userId" v-model="user.id" placeholder="아이디"/>
      </div>

      <div>
        <label for="userPw">비밀번호</label>
        <input type="password" id="userPw" v-model="user.password" placeholder="비밀번호"/>
      </div>

      <div>
        <input type="checkbox" id="saveId" v-model="saveId"/>
        <label for="saveId">아이디 저장</label>
      </div>

      <button type="submit">로그인</button>
    </form>

    <div>
      <router-link to="/find-id">아이디 찾기</router-link>
      <span>|</span>

      <router-link to="/find-pw">비밀번호 찾기</router-link>
      <span>|</span>

      <router-link to="/regi">회원가입</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { loginRequest } from '@/api/member.js';
import { useCookies } from 'vue3-cookies';

const { cookies } = useCookies(); // cookies 꺼내기
const router = useRouter(); // 라우터 장착

const user = ref({  // == data() { return {} } | DTO랑 변수명 꼭 맞추기 ★
  id: '',  
  password: ''
})

// 아이디 저장 체크 박스 변수
const saveId = ref(false)

// 화면 켜지면 쿠키 확인(mounted 역할)
const savedCookie = cookies.get('userId');
if(savedCookie){
  user.value.id = savedCookie; // 있으면 아이디 칸 채워주기
  saveId.value = true; // 체크박스 체크!
}

const handleLogin = async()=>{  // == methods: { async handleLogin() {} } | async
  if(user.value.id === '' || user.value.password === ''){
    alert('아이디와 비밀번호를 입력해 주십시오.');
    return;
  }

  try{
    const response = await loginRequest(user.value) // loginRequest한테 배달 시키기(api -> spring)

    if (!response.data || response.data === "") {
        alert('아이디 또는 비밀번호가 일치하지 않습니다.')
        return;
    }

    console.log("success!", response)
   // alert('환영합니다!')
    sessionStorage.setItem('login', JSON.stringify(response.data))

    // 로그인 성공했을 때, 쿠키 추가
    if(saveId.value){
      // 체크했으면 저장해주기
      cookies.set("userId", user.value.id, '7d');
    }else{
      cookies.remove("userId");
    }

    router.push("/")
  }
  catch (err){
    console.log("error", err)
    alert('아이디 또는 비밀번호가 일치하지 않습니다')
  }  
}
</script>

<style scoped>

</style>