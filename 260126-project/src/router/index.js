import { createRouter, createWebHistory } from 'vue-router' // 라우터를 만드는 기계(createRouter)와 주소창 기록을 관리하는 방식(createWebHistory)을 가져옴

import MainHome from '@/views/MainHome.vue'
import member from '@/router/member/member.js' // 회원 관련 파일 불러오기
import vehicle from '@/router/vehicle/vehicle.js'
import parkingLog from '@/router/parkingLog/parkingLog.js'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        ...member,       // ... 전개 연산자: 배열 속의 내용만 꺼내서 전체에 합쳐줌
        ...vehicle,
        ...parkingLog,

        {
            path: '/',           // 도메인만 쳤을 때 (http://localhost:3000/)
            name: 'mainhome',        // 별명
            component: MainHome,     // 보여줄 파일 (MainHome.vue)
            meta: { title: 'S-HOSPITAL' } // 헤더에 뜰 제목
        }
    ]
})
export default router; // 밖으로 내보내기