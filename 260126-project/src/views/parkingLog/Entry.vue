<template>
  <div>
      <h1>[ 주차장 입차 ]</h1>
      <div v-if="!selectedFile">
        <input type="file" @change="handleFileUpload" accept="image/*"/>
      </div>
      <!-- --------------------------------------------------------- -->
      <div v-if="isLoading">
        <h3>전처리 및 번호판 추출 중입니다...</h3>
      </div>
      <!-- ------------------------------------------------------------------ -->
      <div v-if="resultText && !isLoading">
        <table border="1" style="text-align: center; border-collapse: collapse;">
          <thead>
            <tr>
              <td>
                <h2>[ 번호판 이미지 ]</h2>
                <img v-if="debugImages[0]" :src="`data:image/png;base64,${debugImages[0]}`" alt="번호판 이미지"/>
              </td>

              <td>
                <h2>[ 차량 번호 ]</h2>
                <p>{{ resultText || '번호판 인식 오류' }}</p>
              </td>
            </tr>
          </thead>

          <tbody>
            <tr>
              <td colspan="2">
                <h2>[ 입차 시간 ]</h2>
                <div>{{ entryTime || '시간 정보 없음' }}</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
</template>

<script setup>
  import { ref } from 'vue';
  import { uploadEntryImage } from '@/api/entry.js';

  const selectedFile = ref(null);
  const resultText = ref('');
  const entryTime = ref(''); // 입차 시간 저장 변수
  const debugImages = ref([]);
  const isLoading = ref(false);

  // 파일 선택 시 자동 전송
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file)
      return;

    selectedFile.value = file;
    await submitImage();
  };

  // 서버 전송 로직
  const submitImage = async () => {
    isLoading.value = true;
    resultText.value = '';
    entryTime.value = '';
    debugImages.value = [];

    try {
      const response = await uploadEntryImage(selectedFile.value);

      const data = response.data;
      resultText.value = data.resultText;
      entryTime.value = data.entryTime;
      debugImages.value = data.debugImages; 
      
    } catch (error) {
      console.error(error);
      alert('인식에 실패했습니다. 다시 시도해주세요.');
      resetUpload();
    } finally {
      isLoading.value = false;
    }
  };

  // 변수들 초기화
  const resetUpload = () => {
    selectedFile.value = null;
    resultText.value = '';
    entryTime.value = '';
    debugImages.value = [];
    const input = document.getElementById('fileInput');
    if(input)
      input.value = '';
  };
</script>