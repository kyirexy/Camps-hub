<script setup>
import { useAuthStore } from '@/stores/auth'
import NavBar from '@/components/NavBar.vue'

const authStore = useAuthStore()
</script>

<template>
  <div class="app-container">
    <nav-bar />
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<style>
#app {
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'microsoft yahei', arial, STHeiTi, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  margin: 0;
  padding: 0;
}

a {
  text-decoration: none;
  color: inherit;
}

.el-button {
  border-radius: 4px;
}

.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-content {
  padding-top: 64px; /* 为固定导航栏留出空间 */
  min-height: calc(100vh - 64px);
}
</style>
