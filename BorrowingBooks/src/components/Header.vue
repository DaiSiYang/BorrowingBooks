<template>
  <div class="header-container">
    <div class="header-left">
      <el-button 
        type="text" 
        class="toggle-button"
        @click="handleToggleSidebar"
      >
        <el-icon><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
      </el-button>
    </div>
    
    <div class="header-right">
      <div class="search-box">
        <el-input
          v-model="searchText"
          placeholder="搜索图书..."
          prefix-icon="Search"
          clearable
        />
      </div>
      
      <div class="header-actions">
        <el-tooltip content="消息通知" placement="bottom">
          <el-badge :value="3" class="notification-badge">
            <el-button type="text" class="action-button">
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
        </el-tooltip>
        
        <el-dropdown trigger="click">
          <div class="user-info">
            <el-avatar :size="32" :src= userAvatar />
            <span class="username">{{username || '未登录'}}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToPersonalCenter">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Setting /></el-icon>系统设置
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {useUserInfoStore} from "@/stores/userInfo.js";
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Fold,
  Expand,
  Search,
  Bell,
  User,
  Setting,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'

import { inject } from 'vue'

const userInfoStore = useUserInfoStore()
const username = ref(userInfoStore.userInfo.username || '未登录')
const userAvatar = ref(userInfoStore.userInfo.avatarUrl)
const router = useRouter()
const searchText = ref('')
const currentPage = ref('图书管理')
const isSidebarCollapsed = ref(false)

// 从父组件注入折叠状态和切换方法
const isCollapsed = inject('isSidebarCollapsed')
const toggleSidebar = inject('toggleSidebar')

// 跳转到个人中心
const goToPersonalCenter = () => {
  router.push('/user/personal-center')
}

const handleToggleSidebar = () => {
  console.log('点击了logo，准备切换侧边栏状态')
  toggleSidebar()
  }

const logout = () => {
  // 退出登录逻辑
  router.push({ name: 'login' })
}
</script>

<style scoped lang="scss">
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 16px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  
  .header-left {
    margin-left: 20px;
    display: flex;
    align-items: center;
    
    .toggle-button {
      margin-right: 16px;
      font-size: 20px;
      color: #183550;
    }
    
    .breadcrumb {
      font-size: 14px;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    
    .search-box {
      width: 250px;
      margin-right: 20px;
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      
      .notification-badge {
        margin-right: 20px;
      }
      
      .action-button {
        font-size: 20px;
        color: #183550;
      }
      
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;

        .username {
          margin: 0 8px;
          color: #183550;
          font-size: 14px;
        }
      }
    }
  }
}
</style>