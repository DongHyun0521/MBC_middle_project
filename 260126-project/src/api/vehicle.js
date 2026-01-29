import axios from "axios";

const url = axios.create({
    baseURL: 'http://localhost:8080/'   // Spring 서버(배달 목적지)의 대문 주소
})

export const vehicleRegisterRequest = (vehicleData, userId) => {
  // 백틱(`)을 사용해서 변수를 넣어주세요 (키보드 물결표시랑 같이 있는 애)
  return url.post(`/member/vehiRegi?id=${userId}`, vehicleData)
}