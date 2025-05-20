<script setup>
import { ref, provide, onMounted } from 'vue'
import Header from "@/components/Header.vue"
import Aside from "@/components/Aside.vue"
import Main from "@/components/Main.vue"

const isSidebarCollapsed = ref(false)

const toggleSidebar = () => {
  console.log('切换侧边栏状态，当前状态:', isSidebarCollapsed.value)
  isSidebarCollapsed.value = !isSidebarCollapsed.value
  console.log('切换后状态:', isSidebarCollapsed.value)
}

// 提供给子组件使用
provide('isSidebarCollapsed', isSidebarCollapsed)
provide('toggleSidebar', toggleSidebar)

onMounted(() => {
  console.log('Home组件已挂载，侧边栏初始状态:', isSidebarCollapsed.value)
})
</script>

<template>
  <el-container class="home-container">
    <el-container class="main-container">
      <el-aside :width="isSidebarCollapsed ? '64px' : '220px'" class="aside-container">
        <Aside />
      </el-aside>
      <el-container class="content-container">
        <el-header height="60px" class="header-container">
          <Header />
        </el-header>
        <el-main class="main-content">
          <router-view />
        </el-main>
        <el-footer height="40px" class="footer-container">
          <div class="footer-content">
            <p>© 2023 青梧书径图书借阅管理系统 | 版权所有</p>
          </div>
        </el-footer>
      </el-container>
    </el-container>
  </el-container>
</template>

<style scoped lang="scss">
.home-container {
  height: 100vh;
  overflow: hidden;
}

.main-container {
  height: 100%;
}

.aside-container {
  height: 100%;
  transition: width 0.3s;
}

.content-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-container {
  padding: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  z-index: 999;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 0;
  background-color: #f5f7fa;
}

.footer-container {
  padding: 0;
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
  
  .footer-content {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    p {
      margin: 0;
      color: #909399;
      font-size: 12px;
    }
  }
}
</style>
