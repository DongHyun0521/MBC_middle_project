<template>
    <div>
      <h1>[ 주차장 출차 ]</h1>
      <div v-if="!resultText && !isLoading">
          <input type="file" id="fileInput" @change="handleFileUpload" accept="image/*"/>
      </div>
  <!-- --------------------------------------------------------- -->
      <div v-if="isLoading">
          <h2>번호판 인식 및 요금 계산 중...</h2>
      </div>
  <!-- --------------------------------------------------------- -->
      <div v-if="resultText && !isLoading">
        <table>
          <thead>
            <tr>
              <td>
                <h2>[ 번호판 이미지 ]</h2>
                <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" alt="번호판 이미지"/>
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
        </thead>
        <tbody>
        <tr>
            <td colspan="2">
                <h2>[ 결제 금액 ]</h2>
                <p>{{ formattedFee }}원</p>
                <p>(최초 30분 무료 / 이후 30분당 {{ isMember ? '1,000' : '2,000' }}원)</p>
            </td>
        </tr>
        </tbody>
        </table>
        <button v-if="parkingFee > 0" @click="handlePayment" class="pay-btn">
          (카드)결제하기
        </button>
      </div>
    </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { uploadExitImage, requestPayment } from '@/api/exit.js';

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

  const formattedFee = computed(() => {
    if (parkingFee.value === -1) {
      return '확인 불가';
    }
    return parkingFee.value.toLocaleString();
  });

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    selectedFile.value = file;
    await submitImage();
  };

  const submitImage = async () => {
    isLoading.value = true;
    
    // 변수 초기화
    resultText.value = '';
    entryTime.value = '';
    exitTime.value = '';
    parkingFee.value = 0;
    debugImages.value = [];

    try {
      const response = await uploadExitImage(selectedFile.value);
      const data = response.data;
      
      // 데이터 바인딩
      resultText.value = data.resultText;
      entryTime.value = data.entryTime;
      exitTime.value = data.exitTime;
      parkingFee.value = data.parkingFee;
      isMember.value = data.isMember;
      debugImages.value = data.debugImages;
      
      currentLogId.value = data.parkingLogId;
      currentMemId.value = data.memId;

      // 30분 이내는 바로 얼럿 창
      if (data.parkingFee === 0) {
        setTimeout(() => {
          alert(`[무료 주차]\n차량번호: ${data.resultText}\n회차 차량(30분 이내)입니다.\n안녕히 가십시오.`);
          resetUpload(); // 확인 누르면 바로 초기화
        }, 1000); // 1초 딜레이
      }
      
    } catch (error) {
      console.error(error);
      alert('차량 인식 중 오류 발생');
      resetUpload();
    } finally {
      isLoading.value = false;
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
      // ★ API 함수 호출 (결제 요청)
      const res = await requestPayment(paymentData);
      
      if (res.data === 'success') {
        alert(`[결제 완료]\n차량번호: ${resultText.value}\n금액: ${formattedFee.value}원\n\n안녕히 가십시오.`);
        resetUpload();
      } else {
        alert("결제 실패");
      }
    } catch (err) {
      console.error(err);
      alert("서버 통신 오류");
    }
  };

  // 변수 초기화
  const resetUpload = () => {
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
  };
</script>