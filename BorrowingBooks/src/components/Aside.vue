<template>
  <div class="sidebar-container" :class="{ 'is-collapsed': isCollapsed }">
    <div class="logo-container" @click="handleToggleSidebar">
      <div class="logo-icon">
        <div class="book-icon">
          <span class="book-page"></span>
        </div>
      </div>
      <span v-if="!isCollapsed" class="logo-text">青梧书径</span>
    </div>
    <el-menu
      :default-active="activeIndex"
      class="el-menu-vertical"
      :collapse="isCollapsed"
      background-color="#f9f6f2"
      text-color="#3d2c29"
      active-text-color="#8a5f41"
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
      
      <el-sub-menu index="4">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/home/user/list" @click="navigateTo('/home/user/list')">用户列表</el-menu-item>
        <el-menu-item index="/home/user/import-export" @click="navigateTo('/home/user/import-export')">用户导入导出</el-menu-item>
      </el-sub-menu>
      
      <el-menu-item index="/home/statistics" @click="navigateTo('/home/statistics')">
        <el-icon><DataAnalysis /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
      
      <el-menu-item index="/home/settings" @click="navigateTo('/home/settings')">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
    
    <div class="sidebar-footer">
      <div class="collapse-button" @click="handleToggleSidebar">
        <el-icon><ArrowLeft v-if="!isCollapsed" /><ArrowRight v-else /></el-icon>
      </div>
    </div>
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
  Setting,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

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
  } 
  else {
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
  background-color: #f9f6f2;
  border-right: 1px solid rgba(138, 95, 65, 0.1);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  
  &.is-collapsed {
    .logo-container {
      padding: 20px 0;
      justify-content: center;
      
      .logo-icon {
        margin-right: 0;
      }
    }
  }
  
  .logo-container {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 20px;
    background: linear-gradient(135deg, #f2ebe3 0%, #f9f6f2 100%);
    border-bottom: 1px solid rgba(138, 95, 65, 0.1);
    overflow: hidden;
    white-space: nowrap;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      background: linear-gradient(135deg, #ede5db 0%, #f5f0e8 100%);
      
      .logo-icon {
        .book-icon {
          transform: rotate(-5deg);
        }
      }
    }
    
    .logo-icon {
      width: 36px;
      height: 36px;
      margin-right: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;
      
      .book-icon {
        width: 24px;
        height: 30px;
        background-color: #8a5f41;
        border-radius: 2px 4px 4px 2px;
        position: relative;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          width: 4px;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.1);
          border-radius: 2px 0 0 2px;
        }
        
        .book-page {
          position: absolute;
          width: 18px;
          height: 25px;
          background-color: #f9f6f2;
          right: -2px;
          top: 2px;
          border-radius: 0 2px 2px 0;
          transform-origin: left;
          transform: perspective(300px) rotateY(-25deg);
        }
      }
    }
    
    .logo-text {
      color: #3d2c29;
      font-size: 22px;
      font-weight: 600;
      font-family: "STKaiti", "楷体", serif;
      letter-spacing: 1px;
      transition: opacity 0.3s, transform 0.3s;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -4px;
        left: 0;
        width: 70%;
        height: 2px;
        background: linear-gradient(to right, #e36049, transparent);
        transform: scaleX(0);
        transform-origin: left;
        transition: transform 0.3s;
      }
      
      &:hover::after {
        transform: scaleX(1);
      }
    }
  }
  
  .el-menu-vertical:not(.el-menu--collapse) {
    width: 220px;
    border-right: none;
  }
  
  .el-menu-vertical {
    flex: 1;
    border-right: none;
    
    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
      font-size: 14px;
      position: relative;
      margin: 4px 0;
      
      &:hover {
        background-color: rgba(138, 95, 65, 0.05);
        color: #8a5f41;
      }
      
      &.is-active {
        background-color: rgba(138, 95, 65, 0.08);
        color: #8a5f41;
        font-weight: 500;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          width: 4px;
          height: 100%;
          background-color: #8a5f41;
          border-radius: 0 2px 2px 0;
        }
      }
      
      .el-icon {
        margin-right: 12px;
        font-size: 20px;
        color: #8a5f41;
      }
    }
    
    :deep(.el-sub-menu__title) {
      height: 50px;
      line-height: 50px;
      font-size: 14px;
      
      &:hover {
        background-color: rgba(138, 95, 65, 0.05);
      }
      
      .el-icon {
        margin-right: 12px;
        font-size: 20px;
        color: #8a5f41;
      }
    }
    
    :deep(.el-sub-menu .el-menu-item) {
      min-width: auto;
      background-color: #f5f0e8;
      height: 46px;
      line-height: 46px;
      
      &.is-active {
        &::before {
          height: 70%;
          top: 15%;
        }
      }
    }
  }
  
  .sidebar-footer {
    padding: 10px;
    display: flex;
    justify-content: flex-end;
    border-top: 1px solid rgba(138, 95, 65, 0.1);
    
    .collapse-button {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      background-color: #f2ebe3;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background-color: rgba(138, 95, 65, 0.15);
        transform: scale(1.1);
        
        .el-icon {
          color: #e36049;
        }
      }
      
      .el-icon {
        font-size: 16px;
        color: #8a5f41;
        transition: color 0.3s;
      }
    }
  }
}
</style>