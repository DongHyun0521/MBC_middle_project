<template>
  <div class="check-res-wrap">
    <div class="check-res-container">
      <header class="page-header">
        <h2>나의 진료 예약 조회</h2>
        <div class="title-bar"></div>
        <p class="page-desc">예약 내역을 확인하고 관리하실 수 있습니다.</p>
      </header>

      <div class="res-list-card">
        <table class="hospital-table">
          <thead>
            <tr>
              <th class="w-id">예약번호</th>
              <th class="w-info">진료과 / 의료진</th>
              <th class="w-date">예약일시</th>
              <th class="w-type">방문구분</th>
              <th class="w-status">상태</th>
              <th class="w-action">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="res in myReservations" :key="res.reservation_id">
              <td class="w-id"><div class="cell-content res-id">#{{ res.reservation_id }}</div></td>
              <td class="w-info">
                <div class="cell-content res-doc-info">
                  <strong class="dept-name">{{ res.dept_name }}</strong>
                  <span class="staff-name">{{ res.doctor_name }} 의사</span>
                </div>
              </td>
              <td class="w-date">
                <div class="cell-content res-date-time">
                  <span class="d-txt">{{ formatDate(res.reservation_date) }}</span>
                  <span class="t-txt">{{ res.reservation_time }}</span>
                </div>
              </td>
              <td class="w-type">
                <div class="cell-content"><span class="visit-badge">{{ res.visit_type || '진료' }}</span></div>
              </td>
              <td class="w-status">
                <div class="cell-content">
                  <span :class="['status-badge', statusClass(res.reservation_status)]">
                    {{ res.reservation_status }}
                  </span>
                </div>
              </td>
              <td class="w-action">
                <div class="cell-content txt-center">
                  <button 
                    v-if="res.reservation_status === '예약'" 
                    @click="handleCancel(res.reservation_id)"
                    class="btn-cancel-res"
                  >
                    예약취소
                  </button>
                  <span v-else class="done-txt">-</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="myReservations.length === 0" class="empty-state">
          <div class="empty-icon"></div>
          <p>예약된 내역이 존재하지 않습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyResReq, cancelResReq } from '@/api/reservation' 

const myReservations = ref([])

/**
 * [데이터 로드]
 */
const fetchMyReservations = async () => {
  try {
    const res = await getMyResReq()
    myReservations.value = res.data // DB Map 리스트 (스네이크 케이스)
  } catch (err) {
    console.error('데이터 로드 실패')
  }
}

/**
 * [예약 취소]
 */
const handleCancel = async (resId) => {
  if (!confirm('정말로 예약을 취소하시겠습니까?')) return

  try {
    const res = await cancelResReq(resId)
    // 성공 시 'success' 문자열 또는 true 리턴
    if (res.data === 'success' || res.data === true) {
      alert('예약이 정상적으로 취소되었습니다')
      fetchMyReservations() 
    }
  } catch (err) {
    alert('취소 처리 중 오류가 발생했습니다')
  }
}

/**
 * [유틸] 날짜 변환
 */
const formatDate = (dateInt) => {
  if (!dateInt) return '-'
  const s = String(dateInt)
  return `${s.substring(0, 4)}-${s.substring(4, 6)}-${s.substring(6, 8)}`
}

const statusClass = (s) => s === '예약' ? 'is-pending' : s === '취소' ? 'is-canceled' : 'is-success'

onMounted(fetchMyReservations)
</script>

<style scoped>
.check-res-wrap { background-color: #f4f7fa; min-height: 100vh; padding: 80px 20px; }
.check-res-container { max-width: 1100px; margin: 0 auto; }
.page-header { text-align: center; margin-bottom: 60px; }
.page-header h2 { font-size: 30px; color: #404347; margin-bottom: 15px; font-weight: 800; }
.title-bar { width: 45px; height: 4px; background: #0171e9; margin: 0 auto 20px; }
.page-desc { color: #888; font-size: 16px; font-weight: 300; }
.res-list-card { background: #fff; border-radius: 8px; box-shadow: 0 12px 40px rgba(0,0,0,0.04); border: 1px solid #eee; overflow: hidden; }
.hospital-table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.w-id { width: 130px; } .w-info { width: auto; } .w-date { width: 180px; } .w-type { width: 100px; } .w-status { width: 100px; } .w-action { width: 130px; }
.hospital-table th { background-color: #f8fafc; padding: 20px 15px; font-size: 14px; font-weight: 700; color: #64748b; border-bottom: 2px solid #edf2f7; text-align: center; }
.hospital-table td { padding: 0; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.cell-content { padding: 25px 15px; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100px; }
.res-id { color: #94a3b8; font-family: 'Consolas', monospace; font-size: 14px; }
.res-doc-info { align-items: flex-start; }
.dept-name { color: #0171e9; font-size: 17px; font-weight: 700; margin-bottom: 5px; }
.staff-name { color: #64748b; font-size: 14px; }
.res-date-time { gap: 5px; }
.d-txt { font-weight: 700; color: #1e293b; font-size: 16px; }
.t-txt { font-size: 14px; color: #94a3b8; }
.visit-badge { background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.status-badge { display: inline-block; padding: 6px 12px; border-radius: 4px; font-size: 12px; font-weight: 700; }
.status-badge.is-pending { background-color: #e0f2fe; color: #0171e9; }
.status-badge.is-success { background-color: #f1f5f9; color: #64748b; }
.status-badge.is-canceled { background-color: #fee2e2; color: #ef4444; }
.btn-cancel-res { background-color: #fff; border: 1px solid #fecaca; color: #ef4444; padding: 8px 14px; border-radius: 4px; font-size: 13px; font-weight: 600; cursor: pointer; transition: 0.2s; }
.btn-cancel-res:hover { background-color: #ef4444; color: #fff; }
.txt-center { text-align: center; }
.done-txt { color: #cbd5e1; }
.empty-state { padding: 120px 20px; text-align: center; color: #94a3b8; }
.empty-icon { font-size: 60px; margin-bottom: 25px; opacity: 0.2; }
</style>