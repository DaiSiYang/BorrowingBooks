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
      
      <div class="page-title" v-if="currentRoute">
        <h2>{{ getPageTitle }}</h2>
      </div>
    </div>
    
    <div class="header-right">
      <div class="search-box">
        <el-input
          v-model="searchText"
          placeholder="搜索图书..."
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      
      <div class="header-actions">
        <el-tooltip content="消息通知" placement="bottom" :show-after="300">
          <el-badge :value="3" class="notification-badge" type="primary">
            <el-button type="text" class="action-button" @click="showNotifications">
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
        </el-tooltip>
        
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="36" :src="userAvatar">
              {{ username ? username.charAt(0).toUpperCase() : 'U' }}
            </el-avatar>
            <div class="user-details">
              <span class="username">{{ username || '未登录' }}</span>
              <span class="user-role">读者</span>
            </div>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="personal">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
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
import { ref, computed, inject, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
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
import { ElMessage } from 'element-plus'

const userInfoStore = useUserInfoStore()
const username = ref(userInfoStore.userInfo.username || '未登录')
const userAvatar = ref(userInfoStore.userInfo.avatarUrl)
const router = useRouter()
const route = useRoute()
const searchText = ref('')
const currentRoute = ref(null)

// 获取页面标题
const getPageTitle = computed(() => {
  const routeMap = {
    'homepage': '首页',
    'bookList': '图书列表',
    'addBook': '添加图书',
    'bookCategory': '图书分类',
    'borrowRecords': '借阅记录',
    'borrowApplications': '借阅申请',
    'overdueManagement': '逾期管理',
    'userManagement': '用户管理',
    'statistics': '统计分析',
    'settings': '系统设置',
    'personalCenter': '个人中心'
  }
  
  return routeMap[route.name] || '图书管理系统'
})

// 从父组件注入折叠状态和切换方法
const isCollapsed = inject('isSidebarCollapsed', ref(false))
const toggleSidebar = inject('toggleSidebar', () => {
  console.warn('toggleSidebar 未被正确注入')
})

// 处理点击logo切换侧边栏状态
const handleToggleSidebar = () => {
  if (typeof toggleSidebar === 'function') {
    toggleSidebar()
  }
}

// 搜索处理
const handleSearch = () => {
  if (!searchText.value.trim()) {
    ElMessage({
      message: '请输入搜索内容',
      type: 'warning'
    })
    return
  }
  
  // 这里实现搜索逻辑
  ElMessage({
    message: `正在搜索: ${searchText.value}`,
    type: 'success'
  })
}

// 显示通知
const showNotifications = () => {
  ElMessage({
    message: '您有3条未读消息',
    type: 'info'
  })
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'personal':
      router.push('/user/personal-center')
      break
    case 'settings':
      router.push('/home/settings')
      break
    case 'logout':
      logout()
      break
  }
}

// 跳转到个人中心
const goToPersonalCenter = () => {
  router.push('/user/personal-center')
}

// 退出登录
const logout = () => {
  // 清除用户信息和token
  userInfoStore.clearUserInfo()
  
  // 提示用户
  ElMessage({
    message: '已成功退出登录',
    type: 'success'
  })
  
  // 跳转到登录页
  router.push({ name: 'login' })
}

// 监听路由变化
watch(() => route.name, (newRouteName) => {
  currentRoute.value = newRouteName
})

onMounted(() => {
  currentRoute.value = route.name
})
</script>

<style scoped lang="scss">
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 10;
  
  .header-left {
    display: flex;
    align-items: center;
    
    .toggle-button {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 8px;
      background-color: transparent;
      margin-right: 20px;
      transition: all 0.3s ease;
      border: none;
      padding: 0;
      
      &:hover {
        background-color: rgba(24, 53, 80, 0.05);
        transform: scale(1.05);
      }
      
      .el-icon {
        font-size: 22px;
        color: #183550;
      }
    }
    
    .page-title {
      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #183550;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 0;
          width: 30px;
          height: 3px;
          background: linear-gradient(90deg, #409eff, rgba(64, 158, 255, 0.5));
          border-radius: 3px;
          transition: width 0.3s ease;
        }
        
        &:hover::after {
          width: 100%;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    
    .search-box {
      width: 280px;
      margin-right: 30px;
      transition: width 0.3s ease;
      
      :deep(.el-input__wrapper) {
        border-radius: 20px;
        box-shadow: 0 0 0 1px rgba(24, 53, 80, 0.1) inset;
        transition: all 0.3s ease;
        
        &:focus-within {
          box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.5) inset, 0 3px 10px rgba(64, 158, 255, 0.1);
        }
      }
      
      :deep(.el-input__inner) {
        font-size: 14px;
        height: 36px;
        color: #183550;
        
        &::placeholder {
          color: #99a9bf;
        }
      }
      
      :deep(.el-input-group__append) {
        border-top-right-radius: 20px;
        border-bottom-right-radius: 20px;
        padding: 0 12px;
        
        .el-button {
          border: none;
          padding: 7px;
          color: #409eff;
        }
      }
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      
      .notification-badge {
        margin-right: 30px;
        
        :deep(.el-badge__content) {
          height: 16px;
          line-height: 16px;
          padding: 0 6px;
          border: none;
          top: 5px;
          right: 5px;
          font-weight: 600;
          font-size: 11px;
          box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        }
        
        .action-button {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 40px;
          height: 40px;
          border-radius: 8px;
          border: none;
          padding: 0;
          background-color: transparent;
          transition: all 0.3s ease;
          
          &:hover {
            background-color: rgba(64, 158, 255, 0.1);
            transform: scale(1.05);
          }
          
          .el-icon {
            font-size: 20px;
            color: #183550;
          }
        }
      }
      
      .user-info {
        display: flex;
        align-items: center;
        padding: 5px 12px;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.3s ease;
        background-color: rgba(24, 53, 80, 0.02);
        
        &:hover {
          background-color: rgba(24, 53, 80, 0.05);
          
          .dropdown-icon {
            transform: rotate(180deg);
          }
        }
        
        .el-avatar {
          border: 2px solid rgba(255, 255, 255, 0.8);
          background-color: #409eff;
          color: white;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
          margin-right: 4px;
        }
        
        .user-details {
          display: flex;
          flex-direction: column;
          margin: 0 6px 0 8px;
          
          .username {
            color: #303133;
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 2px;
          }
          
          .user-role {
            color: #909399;
            font-size: 12px;
          }
        }
        
        .dropdown-icon {
          color: #909399;
          font-size: 14px;
          transition: transform 0.3s ease;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 991px) {
  .header-container {
    padding: 0 15px;
    
    .header-right {
      .search-box {
        width: 220px;
        margin-right: 25px;
      }
      
      .header-actions {
        .notification-badge {
          margin-right: 25px;
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .header-container {
    .header-right {
      .header-actions {
        .user-info {
          padding: 5px 10px;
          
          .user-details {
            margin: 0 4px 0 8px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .header-container {
    .header-left {
      .page-title {
        display: none;
      }
    }
    
    .header-right {
      .search-box {
        width: 180px;
        margin-right: 15px;
      }
      
      .header-actions {
        .notification-badge {
          margin-right: 15px;
        }
        
        .user-info {
          padding: 5px 8px;
          background-color: transparent;
          
          .user-details {
            display: none;
          }
          
          .dropdown-icon {
            margin-left: 4px;
          }
        }
      }
    }
  }
}

@media (max-width: 576px) {
  .header-container {
    .header-right {
      .search-box {
        width: 40px;
        overflow: hidden;
        transition: width 0.3s ease;
        
        &:focus-within {
          width: 180px;
        }
        
        :deep(.el-input-group__append) {
          display: none;
        }
      }
    }
  }
}
</style>