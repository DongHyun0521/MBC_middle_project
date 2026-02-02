import axios from "axios";

// 🏥 Axios 인스턴스 설정
const url = axios.create({
    baseURL: 'http://localhost:8080', // Spring 서버 주소 (포트번호 확인 필수 🪄)
    withCredentials: true            // 세션 쿠키 공유를 위해 쥰나 필수 ✨
})

// 1. 로그인 (일반/의료진 공통)
export const loginRequest = (userData) => {
    return url.post('/member/login', userData)
}

// 2. 일반 회원가입
export const registerRequest = (userData) => {
    return url.post('/member/regi', userData)
}

// 3. 의료진 전용 회원가입 (StaffJoinDto 매칭 🩺)
export const staffRegisterRequest = (staffData) => {
    return url.post('/member/staffRegi', staffData)
}

// 4. 아이디 중복 확인 (@RequestParam 방식 🔍)
export const idcheckRequest = (inputId) => {
    return url.get('/member/idcheck', { params: { id: inputId } })
}

// 5. 내 정보 보기 (세션 기반이지만 id 파라미터 백업 🚗)
export const getMyInfoRequest = (userId) => {
    return url.get('/member/mypage', { params: { id: userId } })
}

// 6. 내 정보 수정
export const updateInfoRequest = (userData) => {
    return url.put('/member/mypageUpdate', userData)
}

// 7. 회원 탈퇴 (del=0 -> 1 수술 💉)
export const withdrawRequest = () => {
    return url.delete('/member/withdraw')
}

// 8. 아이디 찾기 (백엔드 /find-id 주소 매칭 ✨)
export const findIdRequest = (userData) => {
    return url.post('/member/find-id', userData)
}

// 9. 로그아웃 (세션 무효화 🔒)
export const logoutRequest = () => {
    return url.post('/member/logout')
}

export default url;