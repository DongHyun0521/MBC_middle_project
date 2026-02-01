<template>
  <div>
    <!-- 출차 대기 화면 -->
    <div v-if="viewMode === 'idle'" @click="startExit" style="width: 100%; height: auto;">
        <img src="../../assets/s_hospital_logo_title.png" alt="s_hospital 로고 텍스트" />
    </div>

    <!-- 출차 대기 화면 클릭 시 -->
    <div v-if="viewMode === 'input'">
      <h1>[ 주차장 출차 ]</h1>
      <div v-if="!isLoading">
        <input type="file" id="fileInput" @change="handleFileUpload" accept="image/*" />
      </div>

      <!-- 번호판 업로드 시 -->
      <div v-if="isLoading">
        <h3>번호판 인식 중...</h3>
      </div>
    </div>

    <!-- 번호판 인식 시 -->
    <div v-if="viewMode === 'result'">
      <h1>[ 출차 확인 ]</h1>
      <table border="1">
        <thead>
          <tr>
            <td>
              <h2>[ 번호판 이미지 ]</h2>
              <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" alt="번호판 이미지" width="300" />
            </td>
            <td>
              <h2>[ 차량 번호 ]</h2>
              <p>{{ resultText }}</p>
              <div>
                <span v-if="isMember">[ 회원 차량 ]</span>
                <span v-else>[ 일반 차량 ]</span>
              </div>
            </td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <p>[ 입차 시간 ]</p>
              <p>{{ entryTime || '-' }}</p>
            </td>
            <td>
              <p>[ 출차 시간 ]</p>
              <p>{{ exitTime || '-' }}</p>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <h2>[ 결제 금액 ]</h2>
              
              <!--  -->
              <div v-if="parkingFee === -1">
                <p>확인 불가 (입차 기록 없음)</p>
              </div>

              <!-- 주차 시간 30분 -->
              <div v-else-if="parkingFee === 0">
                <p>0원 (무료)</p>
                <p>30분 이내 회차 차량입니다.</p>
              </div>

              <!-- 주차 시간 30분 이상 -->
              <div v-else>
                <p>주차요금 : {{ formattedFee }}원</p>
                <p>(최초 30분 무료 / 이후 30분당 {{ isMember ? '1,000' : '2,000' }}원)</p>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div>
        
        <div v-if="parkingFee === 0">
            <h2>안녕히 가십시오.</h2>
            <p>{{ countdown }}초 뒤 초기 화면으로 이동합니다...</p>
        </div>

        <div v-if="parkingFee > 0">
            <button @click="handlePayment">
                결제하기
            </button>
        </div>

        <div v-if="parkingFee === -1">
            <h2>차량 조회 실패</h2>
            <p>{{ countdown }}초 뒤 초기 화면으로 이동합니다...</p>
        </div>
      </div>
    </div>

    <div v-if="viewMode === 'success'">
        <h1>결제 완료!</h1>
        <div>
            <p>차량번호 : {{ resultText }}</p>
            <p>주차요금 : {{ formattedFee }}원</p>
        </div>
        <h2>안녕히 가십시오.</h2>
        <p>{{ countdown }}초 뒤 초기 화면으로 이동합니다...</p>
    </div>

  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { uploadExitImage, requestPayment } from '@/api/exit.js';
  // 배경 이미지 import 제거함

  const viewMode = ref('idle');

  const selectedFile = ref(null);
  const resultText = ref('');
  const entryTime = ref('');
  const exitTime = ref('');
  const parkingFee = ref(0);
  const isMember = ref(false);
  const debugImages = ref([]);
  const isLoading = ref(false);
  
  const currentLogId = ref(null);
  const currentMemId = ref(null);

  const countdown = ref(3);
  let timerId = null;

  const formattedFee = computed(() => {
    if (parkingFee.value === -1) return '확인 불가';
    return parkingFee.value.toLocaleString();
  });

  const startExit = () => {
    viewMode.value = 'input';
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    await submitImage();
  };

  const submitImage = async () => {
    isLoading.value = true;
    
    resultText.value = '';
    entryTime.value = '';
    exitTime.value = '';
    parkingFee.value = 0;
    debugImages.value = [];

    try {
      const response = await uploadExitImage(selectedFile.value);
      const data = response.data;
      
      resultText.value = data.resultText;
      entryTime.value = data.entryTime;
      exitTime.value = data.exitTime;
      parkingFee.value = data.parkingFee;
      isMember.value = data.isMember;
      debugImages.value = data.debugImages;
      
      currentLogId.value = data.parkingLogId;
      currentMemId.value = data.memId;

      viewMode.value = 'result';

      // 무료(0) 이거나 입차기록 없음(-1)일 경우 모두 자동 카운트다운 시작
      if (data.parkingFee <= 0) {
        startCountdown();
      }
      
    } catch (error) {
      console.error(error);
      alert('차량 인식 중 오류 발생');
      resetToIdle();
    } finally {
      if (viewMode.value !== 'result') {
         isLoading.value = false;
      } else {
         isLoading.value = false;
      }
    }
  };

  const handlePayment = async () => {
    if (!currentLogId.value) {
      alert("결제할 정보가 없습니다.");
      return;
    }

    const paymentData = {
      parkingLogId: currentLogId.value,
      memId: currentMemId.value,
      amount: parkingFee.value
    };

    try {
      const res = await requestPayment(paymentData);
      
      if (res.data === 'success') {
        viewMode.value = 'success';
        startCountdown();
      } else {
        alert("결제 실패");
      }
    } catch (err) {
      console.error(err);
      alert("서버 통신 오류");
    }
  };

  const startCountdown = () => {
    countdown.value = 3; 
    
    if (timerId) clearInterval(timerId);

    timerId = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
            clearInterval(timerId);
            resetToIdle();
        }
    }, 1000);
  };

  const resetToIdle = () => {
    if (timerId) {
        clearInterval(timerId);
        timerId = null;
    }

    selectedFile.value = null;
    resultText.value = '';
    entryTime.value = '';
    exitTime.value = '';
    parkingFee.value = 0;
    debugImages.value = [];
    currentLogId.value = null;
    currentMemId.value = null;
    
    const input = document.getElementById('fileInput');
    if(input) input.value = '';

    viewMode.value = 'idle';
  };
</script>