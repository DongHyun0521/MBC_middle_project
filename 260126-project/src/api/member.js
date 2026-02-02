import axios from "axios";

const url = axios.create({
    //baseURL: 'http://localhost:8080/',   // Spring 서버(배달 목적지)의 대문 주소
    withCredentials: true // 세션(쿠키)을 주고받기 위해 필수!
})

// 로그인 심부름 정의
export const loginRequest = (userData) => {
  return url.post('/member/login', userData)
}

// 회원가입 심부름 정의
export const registerRequest = (userData) => {
  return url.post('/member/regi', userData)
}

// 아이디 체크 심부름 정의
export const idcheckRequest = (inputId) => {
  // 추가: 백엔드가 @GetMapping + @RequestParam("id") 이므로 GET 방식 사용
  return url.get('/member/idcheck', {params: { id: inputId }}) // 이 부분이 주소창에 ?id=값 형태로 변환되어 날아감
}

// 내 정보 보기 (GET)
export const getMyInfoRequest = (userId) => {
    return url.get(`/member/mypage?id=${userId}`) 
}

// 내 정보 수정 (PUT)
export const updateInfoRequest = (userData) => {
    return url.put('/member/mypageUpdate', userData)
}

// 회원 탈퇴 (DELETE)
export const withdrawRequest = () => {
    return url.delete('/member/withdraw')
}

// 아이디 찾기 심부름 정의 (이름, 전화번호, 이메일로 찾기)
export const findIdRequest = (userData) => {
    // 💡 백엔드 컨트롤러의 @PostMapping("/member/findId")와 연결됨
    return url.post('/member/findId', userData)
}