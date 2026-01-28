<template>
  <div class="regi-container">
    <h2>회원가입</h2>

    <form @submit.prevent="handleRegi">

      <div class="input-group">
        <label for="userId">아이디</label>
        <div class="id-box">
          <input type="text" id="userId" v-model="user.id" placeholder="아이디" @input="checkID"/>
          <button type="button" @click="idcheck" class="idcheck-btn">중복확인</button>
        </div>
        <p v-if="idcheckMsg" :class="{'success-msg': isIdChecked, 'error-msg': !isIdChecked}">{{ idcheckMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userPw">비밀번호</label>
        <input type="password" id="userPw" v-model="user.pw" @input="checkPw" placeholder="6~16자(특수문자 포함)"/>
        <p v-if="pwMsg" class="error-msg">{{ pwMsg }}</p>
      </div>

      <div class="input-group">
        <label for="PwCheck">비밀번호 재확인</label>
        <input type="password" id="PwCheck" v-model="user.pwCk" placeholder="비밀번호 재확인" @focus="handleFocusPwCk" @input="checkPwCk"/>
        <p v-if="pwCkMsg" :class="{'success-msg': user.pw === user.pwCk && user.pwCk !== '', 'error-msg': user.pw !== user.pwCk}">{{ pwCkMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userName">이름</label>
        <input type="text" id="userName" v-model="user.name" placeholder="이름(실명)" @input="checkName"/>
        <p v-if="nameMsg" class="error-msg">{{ nameMsg }}</p>
      </div>

      <div class="input-group birth-gender">
        <div class="birth-box">
          <label for="userBirth">생년월일</label>
          <input type="text" id="userBirth" v-model="user.birth" @input="checkBirth" placeholder="예: 20000132" maxlength="8"/>
          <p v-if="birthMsg" class="error-msg">{{ birthMsg }}</p>
        </div>

        <div class="gender-box">
          <label>성별</label>
          <div class="gender-btn-group">
            <input type="radio" id="male" value="1" v-model="user.gender"/>
            <label for="male">남자</label>
            <input type="radio" id="female" value="2" v-model="user.gender"/>
            <label for="female">여자</label>
          </div>
        </div>
      </div>

      <div class="input-group">
        <label for="userPhone">휴대폰</label>
        <input type="text" id="userPhone" v-model="user.phone" @input="checkPhone" placeholder="예) 01012345678" maxlength="11"/>
        <p v-if="phoneMsg" class="error-msg">{{ phoneMsg }}</p>
      </div>
      
      <div class="input-group">
        <label for="userEmail">이메일</label>
        <input type="email" id="userEmail" v-model="user.email" @input="checkEmail" placeholder="example@naver.com"/>
        <p v-if="emailMsg" class="error-msg">{{ emailMsg }}</p>
      </div>

      <div class="input-group">
        <label for="userAddr">주소</label>
        <div class="addr-box">
          <input type="text" id="userAddr" v-model="user.addr" placeholder="주소를 입력해 주세요." readonly @click="openPostcode"/>
          <button type="button" @click="openPostcode" class="addr-btn">주소 검색</button>
        </div>
        <input type="text" id="userAddrDetail" v-model="user.addrDetail" placeholder="예) S아파트 101동 202호"/>
      </div>

      <br/>
      <button type="submit" class="submit-btn">회원가입</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { idcheckRequest, registerRequest } from '@/api/member';

const router = useRouter();

const user = ref({
  id: '',
  pw: '',
  pwCk: '',
  name: '',
  birth: '',
  gender: '1',
  phone: '',
  email: '',
  addr: '',
  addrDetail: ''
});

// 화면에 보여줄 메세지
const isIdChecked = ref(false);
const idcheckMsg = ref("");
const pwMsg = ref("");
const emailMsg = ref("");
const phoneMsg = ref("");
const birthMsg = ref("");
const pwCkMsg = ref("");
const nameMsg = ref("");

// 아이디 길이, 입력 형식 제약
const checkID = async () => {
  // 대문자를 입력하면 즉시 소문자로 변환
  user.value.id = user.value.id.toLowerCase();
  
  // 소문자와 숫자 이외의 문자(한글, 특수문자, 공백 등) 즉시 삭제
  user.value.id = user.value.id.replace(/[^a-z0-9]/g, '');

  const id = user.value.id;
  isIdChecked.value = false; // 입력 중엔 중복확인 리셋

  if (!id) {
    idcheckMsg.value = "아이디를 입력해 주세요."
  } 
  else if (id.length < 6 || id.length > 16) {
    idcheckMsg.value = "6~16자리 사이로 입력해 주세요."
  } 
  else {
    idcheckMsg.value = "";
  }
}  
// 아이디 중복확인
const idcheck = async () => { 
  const id = user.value.id
  const idFullRegex = /^[a-z0-9]{6,16}$/

  if(!idFullRegex.test(id)){
    alert('아이디 형식을 다시 확인해 주세요.');
    return;
  }

  try{
    const res = await idcheckRequest(id);
    if(res.data === true || res.data === "OK"){
      idcheckMsg.value = "사용 가능한 아이디 입니다.";
      isIdChecked.value = true;
    } else{
      idcheckMsg.value = "이미 사용중인 아이디 입니다.";
      isIdChecked.value = false;
    }
  } catch(err){
    console.log("아이디 중복확인 실패", err);
  }
}

// 아이디 중복확인 안 했을 경우 대비
const idCheckReset = () => {
  isIdChecked.value = false;
  idcheckMsg.value = '';
}

// 비밀번호 실시간 검사 (6 ~ 16자리, 특수문자 포함)
const checkPw = () => {
  const pw = user.value.pw;
  const pwRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{6,16}$/; // 대문자, 소문자, 특수문자 필수 포함 + 6~16자

  // 빈 값 검사하기
  if (!pw) {
    pwMsg.value = "비밀번호를 입력해 주세요.";
  } 
  // 비밀번호 길이 검사! (6~16자)
  else if (pw.length < 6 || pw.length > 16) {
    pwMsg.value = "6~16자리 사이로 입력해 주세요.";
  } 
  // 특수문자 포함 여부 검사하기
  else if (!pwRegex.test(pw)) {
    pwMsg.value = "영문 대소문자 및 특수문자를 각각 최소 1자 이상 포함해 주세요.";
  } 
  // 4단계: 모든 조건 통과
  else {
    pwMsg.value = "";
  }
};

// 비밀번호 확인 관련 메세지
// 비밀번호 확인 칸을 클릭(포커스)했을 때
const handleFocusPwCk = () => {
  if (!user.value.pwCk) {
    pwCkMsg.value = "비밀번호를 한 번 더 입력해 주세요."
  }
}

// 비밀번호 확인 입력 중 실시간 검사
const checkPwCk = () => {
  const pw = user.value.pw
  const pwCk = user.value.pwCk

  if (!pwCk) {
    pwCkMsg.value = "비밀번호를 한 번 더 입력해 주세요."
  } 
  else if (pw !== pwCk) {
    pwCkMsg.value = "비밀번호가 일치하지 않습니다."
  } 
  else {
    // 비밀번호와 확인값이 완벽히 일치할 때
    pwCkMsg.value = "비밀번호가 일치합니다."
  }
}

// 이름 형식 검사
const checkName = () => {
  user.value.name = user.value.name.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣]/g, ''); // 한글 아니면 입력 막기
  const name = user.value.name;
  const nameRegex = /^[가-힣]{2,}$/;

  if(!name){
    nameMsg.value = "이름을 입력해 주세요.";
  } else if(!nameRegex.test(name)){
    nameMsg.value = "이름은 2자 이상으로 입력해 주세요.";
  } else {
    nameMsg.value ="";
  }
}

// 생년월일 검사
const checkBirth = () => {
  user.value.birth = user.value.birth.replace(/[^0-9]/g, ''); // 숫자 입력 아니면 막음
  const bth = user.value.birth;
  if(bth.length === 8){
    const y = Number(bth.substring(0,4));
    const m = Number(bth.substring(4,6));
    const d = Number(bth.substring(6,8));

    const date = new Date(y, m-1, d);

    if(date.getFullYear() !== y || date.getMonth() + 1 !== m || date.getDate() !== d){
      birthMsg.value = "존재하지 않는 날짜입니다. 다시 확인해 주세요";
    } else if(y < 1900 || y > new Date().getFullYear()){
      birthMsg.value = "올바른 출생 연도를 입력해 주세요."
    } else {
      birthMsg.value = ""
    }
  }
};

// 휴대폰 번호 검사
const checkPhone = () => {
  user.value.phone = user.value.phone.replace(/[^0-9]/g, '');
  const phone = user.value.phone;
  const phoneRegex = /^(010|011)\d{8}$/; // 010, 011으로 시작하고 뒤에 숫자 8개

  if(!phone){
    phoneMsg.value = "휴대폰 번호를 입력해 주세요.";
  } else if(!phone.startsWith('010') && !phone.startsWith('011')){
    phoneMsg.value = "휴대폰 번호는 반드시 010 혹은 011으로 시작해야 합니다."
  } else if(phone.length !== 11){
    phoneMsg.value = "휴대폰 번호 11자리를 모두 입력해 주세요.";
  } else if(!phoneRegex.test(phone)){
    phoneMsg.value = "올바른 번호 형식이 아닙니다. 다시 입력해 주세요."
  } else {
    phoneMsg.value = "";
  }
};

// 이메일 형식 검사
const checkEmail = () => {
  const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
  const email = user.value.email;

  if (!email) {
    emailMsg.value = "이메일을 입력해 주세요.";
  } 
  else if (!regex.test(email)) {
    emailMsg.value = "올바른 이메일 형식이 아닙니다.";
  } 
  else {
    emailMsg.value = "";
  }
};

// 카카오 주소 API
const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      user.value.addr = data.roadAddress || data.jibunAddress;
      // 상세주소 칸으로 자동 이동 (id="userAddrDetail" 추가했을 경우)
      document.getElementById("userAddrDetail")?.focus();
    }
  }).open();
};

// 회원 가입 버튼(서버로 데이터 전송 api -> spring)
const handleRegi = async () => {
  // 아이디 중복 확인 여부
  if (!isIdChecked.value) {
    alert("아이디 중복 확인을 진행해 주세요.")
    return;
  }

  // user 객체 안에 빈 값이 하나라도 있는지 검사
  const checkList = {
    id: "아이디",
    pw: "비밀번호",
    pwCk: "비밀번호 확인",
    name: "이름",
    birth: "생년월일",
    phone: "휴대폰 번호",
    email: "이메일",
    addr: "주소",
    addrDetail: "상세 주소"
  };

  for (const key in checkList) {
    if (!user.value[key] || user.value[key].trim() === "") {
      alert(`${checkList[key]} 항목은 필수 입력 사항입니다.`)
      return;
    }
  }

  // 입력 형식 에러 메시지
  if (pwMsg.value || nameMsg.value || emailMsg.value || phoneMsg.value || birthMsg.value) {
    alert("입력 형식이 올바르지 않은 항목이 있습니다.")
    return;
  }

  // 비밀번호 일치 체크
  if (user.value.pw !== user.value.pwCk) {
    alert("비밀번호가 일치하지 않습니다.")
    return;
  }

  // 서버로 보내기
  try {
    const memberData = {
      id: user.value.id,
      password: user.value.pw,          // 프론트(pw) -> 백엔드(password)
      name: user.value.name,
      birthday: user.value.birth,       // 프론트(birth) -> 백엔드(birthday)
      gender: user.value.gender,
      phoneNumber: user.value.phone,    // 프론트(phone) -> 백엔드(phoneNumber)
      email: user.value.email,
      address: user.value.addr,         // 프론트(addr) -> 백엔드(address)
      addressDetail: user.value.addrDetail // 프론트(addrDetail) -> 백엔드(addressDetail)
    };
    
    const res = await registerRequest(memberData);

    if (res.data === "success" || res.data === true) {
      alert("환영합니다! 회원가입이 정상적으로 완료되었습니다.")
      router.push('/login')
    } else {
      alert("가입 처리에 실패했습니다. 입력 정보를 다시 확인해 주세요");
    }
  } catch (err) {
    console.error("Registration Error Detail:", err)
    alert("서버 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요");
  }
}
</script>

<style scoped>

</style>