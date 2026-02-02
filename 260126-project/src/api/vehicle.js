import axios from "axios";

const url = axios.create({
    //baseURL: 'http://localhost:8080/',
    withCredentials: true // 세션(쿠키)을 주고받기 위해 필수!
})

// 🚗 차량 등록
export const vehicleRegisterRequest = (vehicleData) => {
    return url.post('/member/vehiRegi', vehicleData)
}

// 🚗 차량 목록 조회 (세션 대신 id 파라미터 사용)
export const getMyVehiclesRequest = () => {
    return url.get(`/member/vehicles`)
}

// 🛠️ 차량 정보 수정 (PUT)
export const updateVehicleRequest = (vehicleData) => {
    return url.put('/member/vehiUpdate', vehicleData)
}

// 🗑️ 차량 삭제 (DELETE)
export const deleteVehicleRequest = (vNum) => {
    // 💡 주소창에 ?vehicleNum=... 형태로 전달
    return url.delete(`/member/vehiDelete`, { params: { vehicleNum: vNum } })
}