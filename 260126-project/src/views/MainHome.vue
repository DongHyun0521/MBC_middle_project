<template>
  <div class="home-container">
    <main>
      <MainBanner />

      <MainQuickMenu 
        @reserve="goToReserve" 
        @doctor="goToDoctor" 
        @dept="goToDept" 
        @check="goToCheck"
        @vehiregi="goToCarRegi" 
        @location="goToLocation" 
      />

      <MainNoticeArea @parking="goToParkingGuide" />
    </main>
  </div>  
</template>

<script setup>
import { useRouter } from 'vue-router'
import MainBanner from '@/components/MainBanner.vue'
import MainQuickMenu from '@/components/MainQuickMenu.vue'
import MainNoticeArea from '@/components/MainNoticeArea.vue'

const router = useRouter()

// 🚀 이동 로직 추가 (공주님 라우터 주소에 맞춤 🧩)
const goToCarRegi = () => router.push('/vehiregi')
const goToParkingGuide = () => router.push('/parking-guide')
const goToReserve = () => router.push('/reservation')
const goToDoctor = () => router.push('/doctorsearch')

// 🪄 진료과 찾기랑 예약 조회 이동 함수 새로 추가! 🚗
const goToDept = () => router.push('/deptsearch')
const goToCheck = () => router.push('/checkreservation')

const goToLocation = () => alert('오시는 길 페이지는 준비 중입니다')
</script>