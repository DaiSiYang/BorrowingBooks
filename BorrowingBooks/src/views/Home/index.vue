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
  <div class="home-container">
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
  </div>
</template>

<style scoped lang="scss">
.home-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  
  .main-container {
    height: 100%;
    
    .aside-container {
      transition: width 0.3s;
      overflow: hidden;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      z-index: 10;
    }
    
    .content-container {
      .header-container {
        padding: 0;
      }
      
      .main-content {
        padding: 0;
        overflow: hidden;
      }
      
      .footer-container {
        background-color: #fff;
        border-top: 1px solid #e6e6e6;
        padding: 0;
        
        .footer-content {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 100%;
          
          p {
            margin: 0;
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }
}
</style>