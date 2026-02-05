<template>
  <div class="search-wrap">
    <div class="search-container">
      <header class="page-header">
        <h2>의료진 찾기</h2>
        <div class="title-bar"></div>
        <p class="page-desc">우리 병원의 전문 의료진을 확인하고 바로 예약해보세요.</p>
      </header>

      <div class="filter-box">
        <select v-model="selectedDeptId" class="search-input select-icon" @change="fetchDoctors">
          <option value="">진료과 전체 보기</option>
          <option v-for="dept in depts" :key="dept.med_dept_id" :value="dept.med_dept_id">
            {{ dept.dept_name }}
          </option>
        </select>
        <input type="text" v-model="searchName" placeholder="의사 성함 입력" class="search-input name-input">
      </div>

      <div class="doctor-grid">
        <div v-for="doc in filteredDoctors" :key="doc.staff_id" class="doc-card">
          <div class="doc-img-area">
            <div class="placeholder-icon">👨‍⚕️</div>
            <span>사진 준비중</span>
          </div>
          <div class="doc-info">
            <div class="doc-header">
              <h4>{{ doc.staff_name }} <span class="doc-role">{{ doc.role }}</span></h4>
            </div>
            <div class="doc-details">
              <p><span class="label">진료과</span> {{ doc.dept_name }}</p>
              <p><span class="label">상태</span> {{ doc.status || '진료가능' }}</p>
            </div>
            <button @click="goToReserve(doc)" class="btn-reserve">진료 예약하기</button>
          </div>
        </div>

        <div v-if="filteredDoctors.length === 0" class="no-result">
          <p>검색 결과가 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getDeptsReq, getDoctorsReq, getAllDoctorsReq } from '@/api/reservation'

const router = useRouter()
const route = useRoute()
const selectedDeptId = ref('')
const searchName = ref('')
const depts = ref([])
const allDoctors = ref([])

// 여기서 간호사를 걸러냅니다!
const filteredDoctors = computed(() => {
  return allDoctors.value.filter(doc => {
    // 1. 이름 검색어 필터
    const nameMatch = (doc.staff_name || '').includes(searchName.value);
    
    // 2. 직업 필터: '의사' 또는 'DOCTOR'만 통과! (간호사 제외)
    const role = (doc.role || '').toUpperCase(); // 대소문자 무시를 위해 변환
    const isDoctor = role === '의사' || role === 'DOCTOR';

    // 이름도 맞고 + 의사여야만 화면에 나옴
    return nameMatch && isDoctor;
  })
})

const goToReserve = (doc) => {
  router.push({
    path: '/reservation',
    query: {
      docId: doc.staff_id, 
      docName: doc.staff_name,
      deptId: doc.med_dept_id || selectedDeptId.value, 
      deptName: doc.dept_name
    }
  })
}

const fetchData = async () => {
  try {
    const resDept = await getDeptsReq()
    depts.value = resDept.data

    if (route.query.deptId) {
      selectedDeptId.value = Number(route.query.deptId)
      await fetchDoctors()
    } else {
      await fetchDoctors()
    }
  } catch (err) { console.error('데이터 로드 실패') }
}

const fetchDoctors = async () => {
  try {
    let res;
    if (selectedDeptId.value) {
      res = await getDoctorsReq(selectedDeptId.value)
    } else {
      res = await getAllDoctorsReq()
    }
    // 일단 다 담아두고, 위의 computed(filteredDoctors)에서 간호사를 숨깁니다.
    allDoctors.value = res.data
  } catch (err) {
    console.error('의료진 로드 실패')
    allDoctors.value = []
  }
}

onMounted(fetchData)
</script>

<style scoped>
.search-wrap { background-color: #f4f7fa; min-height: 100vh; padding: 60px 20px; }
.search-container { max-width: 1000px; margin: 0 auto; }
.page-header { text-align: center; margin-bottom: 50px; }
.page-header h2 { font-size: 28px; color: #404347; margin-bottom: 15px; font-weight: 700; }
.title-bar { width: 40px; height: 3px; background: #0171e9; margin: 0 auto 15px; }
.page-desc { color: #888; font-size: 15px; }
.filter-box { display: flex; gap: 15px; margin-bottom: 40px; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03); border: 1px solid #eee; }
.search-input { flex: 1; padding: 15px; border: 1px solid #ddd; border-radius: 4px; font-size: 15px; color: #4e4e4e; transition: 0.3s; background-color: #f9f9f9; }
.search-input:focus { border-color: #0171e9; outline: none; background-color: #fff; }
.doctor-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(450px, 1fr)); gap: 25px; }
.doc-card { background: #fff; border: 1px solid #eee; padding: 25px; display: flex; gap: 25px; border-radius: 12px; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.02); transition: all 0.3s ease; align-items: center; }
.doc-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(1, 113, 233, 0.08); }
.doc-img-area { width: 110px; height: 130px; background: #f0f2f5; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #aaa; font-size: 13px; flex-shrink: 0; }
.placeholder-icon { font-size: 30px; margin-bottom: 5px; opacity: 0.5; }
.doc-info { flex: 1; display: flex; flex-direction: column; gap: 12px; }
.doc-header h4 { font-size: 20px; color: #333; margin: 0; font-weight: 700; display: flex; align-items: center; gap: 8px; }
.doc-role { font-size: 13px; color: #0171e9; background: #e0f2fe; padding: 3px 8px; border-radius: 4px; font-weight: 600; }
.doc-details p { margin: 0; font-size: 15px; color: #555; line-height: 1.5; }
.doc-details .label { color: #0171e9; font-weight: 600; margin-right: 8px; display: inline-block; width: 60px; }
.btn-reserve { margin-top: 10px; padding: 12px; width: 100%; background: #0171e9; color: #fff; border: none; border-radius: 4px; font-size: 15px; font-weight: 600; cursor: pointer; transition: 0.3s; }
.no-result { grid-column: 1 / -1; text-align: center; padding: 80px 20px; color: #999; }
</style>