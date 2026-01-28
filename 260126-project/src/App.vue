<script setup>
import router from './router';
</script>

<template>
  <div>
    <header>
      <h3>{{ $route.meta.title || 'S-HOSPITAL'}}</h3>   <!-- $route.meta.title: 현재 접속한 정보를 실시간으로 읽어오는 변수 -->
    </header>

    <router-view />
    
    <footer>
      <p>Copyright &copy; S-HOSPITAL</p>
    </footer>
  </div>
</template>

<style scoped>

</style>
