<template>
  <div class="sidebar-container">
    <div class="logo-container" @click="handleToggleSidebar">
      <span v-if="!isCollapsed" class="logo-text">青梧书径</span>
    </div>
    <el-menu
      :default-active="activeIndex"
      class="el-menu-vertical"
      :collapse="isCollapsed"
      background-color="#183550"
      text-color="#fff"
      active-text-color="#68b8d7"
    >
      <!-- 菜单项内容 -->
      <el-menu-item index="/home/homepage" @click="navigateTo('/home/homepage')">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      
      <el-sub-menu index="2">
        <template #title>
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </template>
        <el-menu-item index="/home/book-list" @click="navigateTo('/home/book-list')">图书列表</el-menu-item>
        <el-menu-item index="/home/add-book" @click="navigateTo('/home/add-book')">添加图书</el-menu-item>
        <el-menu-item index="/home/book-category" @click="navigateTo('/home/book-category')">图书分类</el-menu-item>
      </el-sub-menu>
      
      <el-sub-menu index="3">
        <template #title>
          <el-icon><Tickets /></el-icon>
          <span>借阅管理</span>
        </template>
        <el-menu-item index="/home/borrow-records" @click="navigateTo('/home/borrow-records')">借阅记录</el-menu-item>
        <el-menu-item index="/home/borrow-applications" @click="navigateTo('/home/borrow-applications')">借阅申请</el-menu-item>
        <el-menu-item index="/home/overdue-management" @click="navigateTo('/home/overdue-management')">逾期管理</el-menu-item>
      </el-sub-menu>
      
      <el-menu-item index="/home/user-management" @click="navigateTo('/home/user-management')">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="/home/statistics" @click="navigateTo('/home/statistics')">
        <el-icon><DataAnalysis /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
      
      <el-menu-item index="/home/settings" @click="navigateTo('/home/settings')">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { inject, ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {useUserInfoStore} from "@/stores/userInfo.js";
import {
  HomeFilled,
  Reading,
  Tickets,
  User,
  DataAnalysis,
  Setting
} from '@element-plus/icons-vue'

const userInfoStore = useUserInfoStore()
const router = useRouter()
const route = useRoute()
const activeIndex = ref(route.path) // 初始化为当前路径

// 从父组件注入折叠状态和切换方法
// 如果注入失败，使用本地状态
const isCollapsed = inject('isSidebarCollapsed', ref(false))

const toggleSidebar = inject('toggleSidebar', () => {
  isCollapsed.value = !isCollapsed.value
  console.log('侧边栏状态切换:', isCollapsed.value)
})

// 处理点击logo切换侧边栏状态
const handleToggleSidebar = () => {
  console.log('点击了logo，准备切换侧边栏状态')
  toggleSidebar()
}

// 导航到指定路由 - 强制使用函数方式
const navigateTo = (path) => {
  console.log('导航到路径:', path)
  
  // 使用动态导入确保组件已加载
  if (path === '/home/add-book') {
    // 先导入组件，确保它已加载
    import('@/views/Home/components/AddBook.vue')
      .then(() => {
        router.push(path).catch(err => {
          console.error('路由导航失败:', err)
          // 如果仍然失败，尝试重新加载页面
          window.location.href = path
        })
      })
      .catch(err => {
        console.error('组件导入失败:', err)
        // 导航到错误页面
        router.push('/error')
      })
  } else {
    // 其他路由正常导航
    router.push(path).catch(err => {
      console.error('路由导航失败:', err)
    })
  }
  
  // 更新活动菜单项
  activeIndex.value = path
}

// 监听路由变化，更新activeIndex
watch(() => route.path, (newPath) => {
  activeIndex.value = newPath
  console.log('路由变化，当前活动菜单:', activeIndex.value)
})

// 组件挂载时设置当前活动菜单项
onMounted(() => {
  activeIndex.value = route.path
  console.log('组件挂载，当前活动菜单:', activeIndex.value)
})
</script>

<style scoped lang="scss">
.sidebar-container {
  height: 100%;
  background-color: #183550;
  
  .logo-container {
    display: flex;
    align-items: center;
    padding: 16px;
    background-color: #132c40;
    overflow: hidden;
    white-space: nowrap;
    cursor: pointer; /* 添加鼠标指针样式 */
    transition: all 0.3s;
    
    &:hover {
      background-color: #1a3a55; /* 鼠标悬停效果 */
    }
    
    .logo-img {
      width: 32px;
      height: 32px;
      margin-right: 10px;
    }
    
    .logo-text {
      color: #fff;
      font-size: 25px;
      margin: 0;
      margin-left: 40px;
      background: linear-gradient(135deg, #fff 0%, #68b8d7 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      transition: opacity 0.3s;
    }
  }
  
  .el-menu-vertical:not(.el-menu--collapse) {
    width: 220px;
  }
  
  .el-menu-vertical {
    border-right: none;
    
    :deep(.el-menu-item) {
      &:hover, &.is-active {
        background-color: #132c40;
      }
      
      .el-icon {
        margin-right: 10px;
        font-size: 18px;
      }
    }
    
    :deep(.el-sub-menu__title) {
      &:hover {
        background-color: #132c40;
      }
      
      .el-icon {
        margin-right: 10px;
        font-size: 18px;
      }
    }
  }
}
</style>