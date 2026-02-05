import { createRouter, createWebHistory } from 'vue-router' // 라우터를 만드는 기계(createRouter)와 주소창 기록을 관리하는 방식(createWebHistory)을 가져옴

// 1. 레이아웃과 화면들 불러오기
import MainLayout from '@/layouts/MainLayout.vue'
import KioskLayout from '@/layouts/KioskLayout.vue'
import MainHome from '@/views/MainHome.vue'

// 2. 찢어놓은 라우터 파일들 불러오기
import member from '@/router/member/member.js'
import vehicle from '@/router/vehicle/vehicle.js'
import reservation from '@/router/reservation/reservation.js' // 예약 라우터 추가!
import customer from '@/router/customer/customer.js'

import entry from '@/router/entry/entry.js'
import exit from '@/router/exit/exit.js'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // [핵심] 모든 홈페이지 주소들을 MainLayout의 '자식(children)'으로 넣기!
        {
            path: '/',
            component: MainLayout, // "난 홈페이지 헤더/푸터 액자를 쓸 거야!"
            children: [
                // 홈페이지 메인 (주소: /)
                {
                    path: '',
                    name: 'mainhome',
                    component: MainHome,
                    //meta: { title: 'S-HOSPITAL' }
                },

                // 회원/차량 관련 주소들도 전부 이 액자 안에서 놀게 함!
                ...member,
                ...vehicle,
                ...reservation,
                ...customer
            ]
        },

        // 추가: 키오스크 그룹 (KioskLayout 사용)
        {
            path: '/kiosk',
            component: KioskLayout,
            children: [
                { path: '', redirect: '/kiosk/entry' },
                {
                    path: 'entry',
                    component: () => import('@/views/parkingLog/Entry.vue'),
                    meta: { title: '입차 시스템' } // KioskLayout 헤더에 뜰 이름
                },
                {
                    path: 'exit',
                    component: () => import('@/views/parkingLog/Exit.vue'),
                    meta: { title: '출차 시스템' }
                },
            ]
        }
    ]
})

// 추가 문지기
router.beforeEach((to, from, next) => {
  // 1. to.meta.requiresAuth : 우리가 붙인 포스트잇 확인
  if (to.meta.requiresAuth) {
    
    // 2. 로그인 했는지 확인
    const isLoggedIn = sessionStorage.getItem('loginId');

    if (!isLoggedIn) {
      // 3. 로그인 안 했으면 쫓아내기
      alert('로그인이 필요한 서비스입니다.');
      next('/login');
    } else {
      // 4. 로그인 했으면 통과
      next();
    }
  } else {
    // 5. equiresAuth이 없거나 false면 그냥 통과시키기
    next();
  }
});

export default router; // 밖으로 내보내기