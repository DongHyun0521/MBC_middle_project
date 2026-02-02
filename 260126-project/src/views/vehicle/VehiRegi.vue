<template>
  <div class="vehi-regi-container">
    <div class="regi-card">
      <div class="top-txt">
        <h2>차량 정보 등록</h2>
        <p>환자 및 보호자 차량 정보를 정확히 입력해 주세요</p>
      </div>

      <form @submit.prevent="handleVehiRegi">
        <div class="input-group">
          <label for="vehicleNum">차량 번호</label>
          <div class="input-info">
            <input type="text" id="vehicleNum" v-model="vehi.vehicleNum" ref="idInput" placeholder="예) 12가 3456" @input="checkVehiNum"/>
            <p v-if="vehiMsg" class="error-msg">{{ vehiMsg }}</p>
          </div>  
        </div>

        <div class="input-group">
          <label for="vehicleType">차종 선택</label>
          <div class="input-info">
            <select id="vehicleType" v-model="vehi.vehicleType">
              <option value="">차종을 선택해 주세요</option>
              <option value="경차">경차</option>
              <option value="승용차">승용차</option>
              <option value="SUV">SUV</option>
              <option value="기타">기타</option>
            </select>
          </div>
        </div>

        <div class="input-row">
          <label>유종</label>
          <div class="fuel-grid">
            <label class="fuel-item" v-for="fuel in ['전기', '휘발유', '경유', 'LPG']" :key="fuel">
              <input type="radio" :value="fuel" v-model="vehi.fuelType" />
              <span class="fuel-label">{{ fuel }}</span>
            </label>
          </div>
        </div>

        <div class="btn-area">
          <button type="submit" class="submit-btn" :disabled="!isReady">차량 등록하기</button> 
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { vehicleRegisterRequest } from '@/api/vehicle' 

const router = useRouter()
const route = useRoute()

const memId = ref(null)
const userId = ref('')

const vehi = ref({
  vehicleNum: '', 
  vehicleType: '',
  fuelType: '휘발유'
})
const vehiMsg = ref('')

onMounted(() => {
  const loginData = sessionStorage.getItem('login') 
  if (!loginData) {
    alert("로그인이 필요한 서비스입니다")
    router.push({ path: '/login', query: { redirect: route.fullPath } })
  } else {
    const user = JSON.parse(loginData)
    memId.value = user.memId 
    userId.value = user.id
  }
})

// 1. 차량 번호 형식 체크
const checkVehiNum = () => {
  let val = vehi.value.vehicleNum.toUpperCase().replace(/\s/g, '')
  val = val.replace(/[^A-Z0-9가-힣]/g, '')
  vehi.value.vehicleNum = val
  const vehiRegex = /^\d{2}[가-힣]{1}\d{4}$/
  if (!val) {
    vehiMsg.value = "차량 번호를 입력해 주세요"
  } else if (!vehiRegex.test(val)) {
    vehiMsg.value = "형식이 올바르지 않습니다 (예: 12가3456)"
  } else {
    vehiMsg.value = ""
  }
}

// 버튼 활성화 조건
const isReady = computed(() => { 
  const vehiRegex = /^\d{2}[가-힣]{1}\d{4}$/
  // 번호 형식도 맞고 + 차종도 선택해야 버튼이 활성화됨!
  return vehiRegex.test(vehi.value.vehicleNum) && vehi.value.vehicleType !== ''
})

// 2. 차량 등록 실행
const handleVehiRegi = async () => {
  if (!isReady.value) return // 💡 이제 isReady를 정상적으로 참조함!
  
  const saveData = {
    ...vehi.value,
    memId: memId.value
  }

  try {
    const res = await vehicleRegisterRequest(saveData, userId.value)
    if (res.data === "success" || res.data === true) {
      alert("차량 등록이 완료되었습니다 🚗")
      router.push('/mypage')
    } else {
      alert("등록 실패! 정보를 다시 확인해 주세요")
    }
  } catch (err) {
    if (err.response && err.response.status === 500) {
      alert("이미 등록된 차량 번호입니다 🙅‍♀️")
    } else {
      alert("서버 통신 오류가 발생했습니다")
    }
  }
}
</script>