<template>
  <div>
    <!-- 입차 대기 화면 -->
    <div v-if="viewMode === 'idle'" @click="startEntry">
        <img src="../../assets/s_hospital_logo_title.png" alt="s_hospital 로고 텍스트"/>
    </div>

    <!-- 입차 대기 화면 클릭 시 -->
    <div v-if="viewMode === 'input'">
      <h1>[ 주차장 입차 ]</h1>
      <div v-if="!isLoading">
        <input type="file" id="fileInput" @change="handleFileUpload" accept="image/*"/>
      </div>

      <!-- 번호판 업로드 시 -->
      <div v-if="isLoading">
        <h3>번호판 인식 중...</h3>
      </div>
    </div>

    <!-- 번호판 인식 시 -->
    <div v-if="viewMode === 'result'">
      <h1>[ 입차 완료 ]</h1>
      <table border="1">
        <thead>
          <tr>
            <td>
              <h2>[ 번호판 이미지 ]</h2>
              <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" alt="입차 번호판 이미지"/>
            </td>
            <td>
              <h2>[ 차량 번호 ]</h2>
              <p>{{ resultText || '입차 번호판 인식 오류' }}</p>
            </td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td colspan="2">

              <!-- parking_log에 번호판이 이미 있을 때 -->
              <div v-if="isAlreadyParked">
                <h2>이미 주차된 차량입니다</h2>
              </div>

              <!-- parking_log에 번호판이 없을 때 -->
              <div v-else>
                <h2>[ 입차 시간 ]</h2>
                <div>{{ entryTime || '시간 정보 없음' }}</div>
              </div>

            </td>
          </tr>
        </tbody>
      </table>
      <p>{{ countdown }}초 뒤 초기 화면으로 이동합니다...</p>
    </div>

  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import { uploadEntryImage } from '@/api/entry.js';

  const viewMode = ref('idle');       // 화면 상태 (시작: 대기 화면)
  const isLoading = ref(false);       // 번호판 인식 중인지
  const selectedFile = ref(null);     // 업로드한 번호판 이미지
  const debugImages = ref([]);        // 변환한 번호판 이미지
  const resultText = ref('');         // 이미지에서 추출한 번호판 텍스트
  const isAlreadyParked = ref(false); // 이미 parking_log에 있는 번호판인지 확인
  const entryTime = ref('');          // 입차 시간
  const countdown = ref(3);           // 새로고침 했을 때 타이머 초기화
  let timerId = null;

  // 대기 화면에서 클릭
  const startEntry = () => {
    viewMode.value = 'input';
  };

  // 이미지 업로드 즉시 서버로 전송
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file)
      return;
    selectedFile.value = file;
    await submitImage();
  };

  // 메인
  const submitImage = async () => {
    // 입차 관련 변수들 초기화
    isLoading.value = true;
    resultText.value = '';
    entryTime.value = '';
    debugImages.value = [];
    isAlreadyParked.value = false;

    try {
      // 업로드 한 이미지 서버로 전송
      const response = await uploadEntryImage(selectedFile.value);
      const data = response.data;

      // 이미지와 문자열 받기
      debugImages.value = data.debugImages;
      resultText.value = data.resultText;

      // 입차 되어있는 차량인지 확인
      if (data.entryTime === 'ALREADY_PARKED') {
        isAlreadyParked.value = true;
        entryTime.value = '';
      } else {
        isAlreadyParked.value = false;
        entryTime.value = data.entryTime;
      }
      
      viewMode.value = 'result';  // result 화면으로 전환

      countdown.value = 3;  // 새로고침 안 했을 때 타이머 초기화

      // 1초마다 1씩 감소
      timerId = setInterval(() => {
        countdown.value--;          // 1초씩 감소
        if (countdown.value <= 0) { // 타이머가 0이하가 되면
          clearInterval(timerId);   // 타이머 리셋
          resetToIdle();            // 변수들 초기화
        }
      }, 1000); // 1초마다

    } catch (error) {
      console.error(error);
      alert('인식에 실패했습니다. 다시 시도해주세요.');

      const input = document.getElementById('fileInput');
      if(input)
        input.value = '';
      selectedFile.value = null;
      isLoading.value = false;

    } finally {
      if(viewMode.value !== 'result') {
        isLoading.value = false;
      }
    }
  };

  // 변수 및 타이머 초기화
  const resetToIdle = () => {
    viewMode.value = 'idle';
    isLoading.value = false;
    selectedFile.value = null;
    debugImages.value = [];
    resultText.value = '';
    entryTime.value = '';
    if (timerId) {
      clearInterval(timerId);
      timerId = null;
    }
    const input = document.getElementById('fileInput');
    if(input)
      input.value = '';
  };
</script>