<template>
  <div class="vehi-regi-container">
    <div class="regi-card">
      <div class="top-txt">
        <h2>차량 정보 등록 🚗</h2>
        <p>환자 및 보호자 차량 정보를 정확히 입력해 주세요</p>
      </div>

      <form @submit.prevent="handleVehiRegi">
        <div class="input-group">
          <label for="vehiNum">차량 번호</label>
          <div class="input-info">
            <input 
              type="text" 
              id="vehiNum" 
              v-model="vehi.vehiNum" 
              placeholder="예) 12가 3456" 
              @input="checkVehiNum"
            />
            <p v-if="vehiMsg.vehiNum" class="error-msg">{{ vehiMsg.vehiNum }}</p>
          </div>  
        </div>

        <div class="input-group">
          <label for="vehiType">차종 선택</label>
          <div class="input-info">
            <select id="vehiType" v-model="vehi.vehiType">
              <option value="">차종을 선택해 주세요</option>
              <option value="경차">경차</option>
              <option value="승용차">승용차</option>
              <option value="SUV">SUV</option>
              <option value="기타">기타</option>
            </select>
          </div>
        </div>

        <div class="input-group">
          <label>유종</label>
          <div class="fuel-btn-group">
            <input type="radio" id="elec" value="전기" v-model="vehi.fuelType" />
            <label for="elec">전기</label>

            <input type="radio" id="gas" value="휘발유" v-model="vehi.fuelType" />
            <label for="gas">휘발유</label>
            
            <input type="radio" id="diesel" value="경유" v-model="vehi.fuelType" />
            <label for="diesel">경유</label>

            <input type="radio" id="lpg" value="LPG" v-model="vehi.fuelType" />
            <label for="lpg">LPG</label>
          </div>
        </div>

        <div class="btn-area">
          <button type="submit" class="submit-btn" :disabled="!isFormValid">차량 등록하기</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { vehicleRegisterRequest } from '@/api/vehicle'; 

const router = useRouter();

// 데이터 초기화
const userNo = ref(null);
const userId = ref('');
const vehi = ref({
  vehiNum: '',
  vehiType: '',
  fuelType: '휘발유' // 기본값
});

const vehiMsg = ref({
  vehiNum: ''
});

// 페이지 진입 시 로그인 체크
onMounted(() => {
  const storedData = sessionStorage.getItem('login');
  
  if (storedData) {
    const parsedData = JSON.parse(storedData);
    userId.value = parsedData.id; // 여기서 아이디를 채워줌! (이제 빈 값이 아님)
    userNo.value = parsedData.memId; // (필요하다면) 회원 번호도 저장
  } else {
    alert("로그인이 필요한 서비스입니다");
    router.push('/login');
  }
});

// 차량 번호 실시간 제약 및 자동 변환
const checkVehiNum = () => {
  // 1. 대문자 변환 및 공백 제거
  let val = vehi.value.vehiNum.toUpperCase().replace(/\s/g, '');
  // 2. 허용되지 않는 문자(특수문자 등) 즉시 삭제
  val = val.replace(/[^A-Z0-9가-힣]/g, '');
  vehi.value.vehiNum = val;

  // 3. 정규식 검사 (숫자 2~3자 + 한글 1자 + 숫자 4자)
  const vehiRegex = /^\d{2,3}[가-힣]{1}\d{4}$/;

  if (!val) {
    vehiMsg.value.vehiNum = "차량 번호를 입력해 주세요";
  } else if (!vehiRegex.test(val)) {
    vehiMsg.value.vehiNum = "형식이 올바르지 않습니다 (예: 12가3456)";
  } else {
    vehiMsg.value.vehiNum = "";
  }
};

// 버튼 활성화 조건 (차번호 정규식 통과 + 차종 선택됨)
const isFormValid = computed(() => {
  const vehiRegex = /^\d{2,3}[가-힣]{1}\d{4}$/;
  return vehiRegex.test(vehi.value.vehiNum) && vehi.value.vehiType !== '';
});

// 서버 전송
const handleVehiRegi = async () => {
  if (!isFormValid.value) return;

  // ★ [수정] 백엔드 이름(DTO)에 맞춰서 포장하기
  const saveData = {
    vehicleNum: vehi.value.vehiNum,   // 프론트(vehiNum) -> 백엔드(vehicleNum 추정)
    vehicleType: vehi.value.vehiType, // 프론트(vehiType) -> 백엔드(vehicleType 추정)
    fuelType: vehi.value.fuelType,    // 유종은 같을 듯
    // userNo는 백엔드 Controller가 session에서 알아서 꺼내 쓰므로 안 보내도 됨
  };

  try {
    const res = await vehicleRegisterRequest(saveData, userId.value);
    if (res.data === "success" || res.data === true) {
      alert("차량 등록이 성공적으로 완료되었습니다");
      router.push('/mainhome'); 
    } else {
      alert("등록에 실패했습니다. 입력 정보를 확인해 주세요");
    }
  } catch (err) {
    console.error("차량 등록 오류:", err);
    alert("서버 통신 중 오류가 발생했습니다");
  }
};
</script>

<style scoped>

</style>