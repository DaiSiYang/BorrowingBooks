<template>
  <div class="sidebar-container">
    <div class="logo-container" @click="handleToggleSidebar">
      <img src="@/assets/Image/logo.png" alt="青梧书径" class="logo-img" />
      <span v-if="!isCollapsed" class="logo-text">青梧书径</span>
    </div>
    <el-menu
      default-active="1"
      class="el-menu-vertical"
      :collapse="isCollapsed"
      background-color="#183550"
      text-color="#fff"
      active-text-color="#68b8d7"
    >
      <!-- 菜单项内容 -->
      <el-menu-item index="1">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      
      <el-sub-menu index="2">
        <template #title>
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </template>
        <el-menu-item index="2-1">图书列表</el-menu-item>
        <el-menu-item index="2-2">添加图书</el-menu-item>
        <el-menu-item index="2-3">图书分类</el-menu-item>
      </el-sub-menu>
      
      <el-sub-menu index="3">
        <template #title>
          <el-icon><Tickets /></el-icon>
          <span>借阅管理</span>
        </template>
        <el-menu-item index="3-1">借阅记录</el-menu-item>
        <el-menu-item index="3-2">借阅申请</el-menu-item>
        <el-menu-item index="3-3">逾期管理</el-menu-item>
      </el-sub-menu>
      
      <el-menu-item index="4">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="5">
        <el-icon><DataAnalysis /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
      
      <el-menu-item index="6">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { inject, ref } from 'vue'
import {
  HomeFilled,
  Reading,
  Tickets,
  User,
  DataAnalysis,
  Setting
} from '@element-plus/icons-vue'

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
      font-size: 18px;
      margin: 0;
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