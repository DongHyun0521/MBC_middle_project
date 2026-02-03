import axios from "axios";

// 🏥 Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 주소 🪄
    withCredentials: true            // 세션 공유 필수 ✨
})

// 1. 진료부서(dept) 목록 가져오기
// 💡 백엔드에 해당 매핑이 있는지 확인 필요! (일반적으로 /member/depts 등)
export const getDepts = () => {
    return url.get('/med/dept/list') 
}

// 2. 특정 부서의 의료진 목록 가져오기
// 💡 백엔드 주소: /member/staff/dept/{deptId} 🩺
export const getDoctorsByDept = (deptId) => {
    return url.get(`/med/staff/dept/${deptId}`)
}

// 3. 진료 예약 등록하기 (reservation 테이블 INSERT)
// 💡 백엔드 주소: /member/reservation 🚀
export const postReservation = (reservationData) => {
    return url.post('/med/reservation', reservationData)
}

// 4. 내 예약 내역 조회하기 (CheckReservation.vue용)
// 💡 백엔드 주소: /member/my-reservations 📅
// 백엔드에서 HttpSession으로 loginId를 식별하므로 파라미터 없이 호출 가능!
export const getMyReservations = () => {
    return url.get('/med/my-reservations')
}

// 5. [추가] 의사별 예약 스케줄 조회 (의료진 전용)
// 💡 백엔드 주소: /member/reservation/doctor/{doctorId} 💉
export const getDoctorSchedule = (doctorId) => {
    return url.get(`/med/doctor/schedule/${doctorId}`)
}

// [추가] 전체 의사 목록 조회 (DoctorSearch.vue에서 사용)
// [수정] 백엔드 MedController: @GetMapping("/doctors")
export const getAllDoctors = () => {
    return url.get('/med/doctors')
}

export default url;