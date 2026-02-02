<template>
  <div id="home-layout">
    <header class="main-header">
      <div class="logo" @click="router.push('/')">S-HOSPITAL</div>
      <nav class="nav-menu">
        <span>진료안내</span>
        <span>의료진소개</span>
        <span class="active" @click="router.push('/vehiregi')">주차안내</span>
        <span>고객센터</span>
      </nav>
      
      <div class="user-menu">
        <template v-if="isLogin">
          <div class="login-user-box">
            <span class="welcome-txt">
              <span class="user-name">{{ loginName }}</span>님 환영합니다
            </span>
            <button @click="router.push('/mypage')" class="mypage-btn">마이페이지</button>
            <button @click="handleLogout" class="logout-btn">로그아웃</button>
          </div>
        </template>
        <template v-else>
          <div class="auth-btn-box">
            <button @click="router.push('/login')" class="login-btn">로그인</button>
            <button @click="router.push('/regi')" class="regi-btn">회원가입</button>
          </div>
        </template>
      </div>
    </header>

    <router-view />

    <footer class="main-footer">
      <p>(01234) 서울특별시 건국구 냠냠로 1004</p>
      <p>Copyright © Gongju Hospital. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isLogin = ref(false)
const loginName = ref('')

const checkLogin = () => {
  const loginData = sessionStorage.getItem('login')
  if (loginData) {
    const user = JSON.parse(loginData)
    isLogin.value = true
    loginName.value = user.name || user.id 
  } else {
    isLogin.value = false
  }
}

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    sessionStorage.removeItem('login')
    isLogin.value = false
    router.push('/')
  }
}

watch(() => route.path, () => checkLogin())
onMounted(() => checkLogin())
</script>

<style scoped>
/* 💡 공주님의 기존 헤더/푸터 스타일 100% 그대로 복붙! */
.main-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 50px; border-bottom: 1px solid #e9ecef; background: #fff; position: sticky; top: 0; z-index: 1000; }
.logo { font-size: 22px; font-weight: 800; color: #007bff; cursor: pointer; }
.nav-menu span { margin: 0 15px; cursor: pointer; font-weight: 600; color: #666; font-size: 15px; }
.active { color: #007bff !important; border-bottom: 2px solid #007bff; padding-bottom: 5px; }
.login-user-box { display: flex; align-items: center; gap: 12px; }
.user-name { color: #007bff; font-weight: bold; }
.logout-btn { padding: 7px 15px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; background: #fff; border: 1px solid #ff4d4f; color: #ff4d4f; }
.login-btn { padding: 7px 15px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; background: #007bff; border: none; color: #fff; }
.main-footer { background: #2c3e50; color: #bdc3c7; padding: 40px; text-align: center; font-size: 13px; margin-top: 50px; }
</style>