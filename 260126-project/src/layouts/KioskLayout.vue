<template>
    <div class="kiosk-container">
        <header class="kiosk-header">
            <h3>{{ $route.meta.title || '서울에스병원' }}</h3>
        </header>

        <main class="kiosk-main">
            <router-view />
        </main>

        <footer class="kiosk-footer">
            <p>Copyright &copy; 서울에스병원</p>
        </footer>
    </div>
</template>

<style scoped>
.kiosk-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    width: 100%;
}

.kiosk-header {
    background: #404347;
    color: #fff;
    padding: 20px;
    text-align: center;
}

.kiosk-main {
    flex: 1;
    overflow-y: auto;
    background: #fff;
}

.kiosk-footer {
    background: #f8f9fa;
    padding: 15px;
    text-align: center;
    font-size: 13px;
    color: #999;
}
</style>