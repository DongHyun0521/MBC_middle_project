import axios from "axios";

const url = axios.create({
    baseURL: 'http://localhost:8080/'   // Spring 서버(배달 목적지)의 대문 주소
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
  return url.get('/member/idcheck', { params: { id: inputId } })
}