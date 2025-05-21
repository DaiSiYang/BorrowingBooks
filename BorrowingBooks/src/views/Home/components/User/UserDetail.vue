<template>
  <div class="user-detail-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="title-section">
        <el-button @click="goBack" class="back-button">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h2 class="page-title">用户详情</h2>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="handleEdit" class="edit-button">
          <el-icon><Edit /></el-icon>编辑用户
        </el-button>
        <el-button @click="handlePermission" class="permission-button">
          <el-icon><SetUp /></el-icon>权限设置
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="detail-content">
      <!-- 用户基本信息 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="header-title">基本信息</div>
          </div>
        </template>
        
        <div class="user-profile">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
            </el-avatar>
            <div class="user-role">
              <el-tag
                :type="getRoleTagType(userInfo.role)"
                effect="light"
                size="small"
              >
                {{ getRoleLabel(userInfo.role) }}
              </el-tag>
            </div>
          </div>
          
          <div class="info-section">
            <div class="info-grid">
              <div class="info-item">
                <div class="info-label">用户名</div>
                <div class="info-value">{{ userInfo.username }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">昵称</div>
                <div class="info-value">{{ userInfo.nickname }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">手机号</div>
                <div class="info-value">{{ userInfo.phone }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">状态</div>
                <div class="info-value">
                  <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">
                    {{ userInfo.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </div>
              </div>
              <div class="info-item">
                <div class="info-label">创建时间</div>
                <div class="info-value">{{ formatDate(userInfo.createTime) }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">最后登录</div>
                <div class="info-value">{{ formatDate(userInfo.lastLogin) }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">登录IP</div>
                <div class="info-value">{{ userInfo.lastLoginIp || '-' }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">邮箱</div>
                <div class="info-value">{{ userInfo.email || '-' }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 登录历史 -->
      <el-card class="login-history-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="header-title">登录历史</div>
          </div>
        </template>
        
        <el-timeline>
          <el-timeline-item
            v-for="(login, index) in loginHistory"
            :key="index"
            :timestamp="formatDateTime(login.loginTime)"
            :type="login.status === 'success' ? 'success' : 'danger'"
          >
            <div class="timeline-content">
              <div class="login-status">
                {{ login.status === 'success' ? '登录成功' : '登录失败' }}
              </div>
              <div class="login-detail">
                <el-tag size="small" type="info">{{ login.ip }}</el-tag>
                <span class="device-info">{{ login.device }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 操作日志 -->
      <el-card class="operation-log-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="header-title">操作日志</div>
            <div class="card-options">
              <el-button size="small" @click="viewMoreLogs" class="more-button">
                查看更多
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </template>
        
        <el-table
          :data="operationLogs"
          style="width: 100%"
          :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
        >
          <el-table-column prop="time" label="操作时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.time) }}
            </template>
          </el-table-column>
          <el-table-column prop="module" label="操作模块" width="150" />
          <el-table-column prop="action" label="操作内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="ip" label="IP地址" width="140" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 'success' ? 'success' : 'danger'"
                size="small"
              >
                {{ scope.row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, ArrowRight, User, Edit, SetUp, 
  Connection, Document 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userId = route.params.id

// 角色选项
const roleOptions = [
  { value: 'admin', label: '管理员' },
  { value: 'operator', label: '操作员' },
  { value: 'user', label: '普通用户' }
]

// 加载状态
const loading = ref(false)

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  role: '',
  status: 1,
  createTime: '',
  lastLogin: '',
  lastLoginIp: ''
})

// 登录历史
const loginHistory = ref([])

// 操作日志
const operationLogs = ref([])

// 获取角色标签类型
const getRoleTagType = (role) => {
  switch (role) {
    case 'admin': return 'danger'
    case 'operator': return 'warning'
    case 'user': return 'info'
    default: return 'info'
  }
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const option = roleOptions.find(item => item.value === role)
  return option ? option.label : role
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 编辑用户
const handleEdit = () => {
  // 打开编辑对话框或跳转到编辑页面
  ElMessage.info('打开编辑对话框')
}

// 权限设置
const handlePermission = () => {
  router.push(`/home/user/permissions/${userId}`)
}

// 查看更多日志
const viewMoreLogs = () => {
  router.push(`/home/user/logs/${userId}`)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  if (typeof date === 'string') date = new Date(date)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '-'
  if (typeof date === 'string') date = new Date(date)
  return `${formatDate(date)} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    // TODO: 调用用户详情API
    // 模拟API调用
    setTimeout(() => {
      Object.assign(userInfo, {
        id: userId,
        username: 'admin',
        nickname: '系统管理员',
        phone: '13800138000',
        email: 'admin@example.com',
        avatar: '',
        role: 'admin',
        status: 1,
        createTime: new Date('2023-01-01'),
        lastLogin: new Date(),
        lastLoginIp: '192.168.1.1'
      })
      loading.value = false
    }, 800)
  } catch (error) {
    console.error('获取用户信息失败:', error)
    loading.value = false
  }
}

// 获取登录历史
const fetchLoginHistory = async () => {
  try {
    // TODO: 调用登录历史API
    // 模拟数据
    loginHistory.value = [
      {
        id: 1,
        loginTime: new Date(),
        ip: '192.168.1.1',
        device: 'Chrome 98.0.4758.102 / Windows 10',
        status: 'success'
      },
      {
        id: 2,
        loginTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        ip: '192.168.1.2',
        device: 'Firefox 97.0 / macOS 12.2.1',
        status: 'success'
      },
      {
        id: 3,
        loginTime: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
        ip: '192.168.1.100',
        device: 'Safari / iOS 15.3.1',
        status: 'failed'
      }
    ]
  } catch (error) {
    console.error('获取登录历史失败:', error)
  }
}

// 获取操作日志
const fetchOperationLogs = async () => {
  try {
    // TODO: 调用操作日志API
    // 模拟数据
    operationLogs.value = [
      {
        id: 1,
        time: new Date(),
        module: '用户管理',
        action: '修改用户信息',
        ip: '192.168.1.1',
        status: 'success'
      },
      {
        id: 2,
        time: new Date(Date.now() - 2 * 60 * 60 * 1000),
        module: '图书管理',
        action: '添加新书《三体》',
        ip: '192.168.1.1',
        status: 'success'
      },
      {
        id: 3,
        time: new Date(Date.now() - 5 * 60 * 60 * 1000),
        module: '借阅管理',
        action: '处理借阅申请',
        ip: '192.168.1.1',
        status: 'success'
      },
      {
        id: 4,
        time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
        module: '系统设置',
        action: '修改系统参数',
        ip: '192.168.1.1',
        status: 'failed'
      }
    ]
  } catch (error) {
    console.error('获取操作日志失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchLoginHistory()
  fetchOperationLogs()
})
</script>

<style scoped lang="scss">
.user-detail-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .title-section {
      display: flex;
      align-items: center;
      gap: 16px;

      .back-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;
        border-radius: 50%;
        padding: 8px;
        height: 36px;
        width: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .page-title {
        font-size: 24px;
        color: #3d2c29;
        margin: 0;
        position: relative;
        font-weight: 600;
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;

      .edit-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        
        &:hover {
          background-color: #6e4c34;
        }
      }

      .permission-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;

        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
        }
      }

      .el-icon {
        margin-right: 6px;
      }
    }
  }

  .detail-content {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .info-card,
    .login-history-card,
    .operation-log-card {
      border-radius: 10px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
      border: none;
      overflow: hidden;

      .card-header {
        display: flex;
        align-items: center;
        padding: 16px;
        background-color: #f9f6f2;
        border-bottom: 1px solid #f0f0f0;

        .header-icon {
          width: 36px;
          height: 36px;
          background-color: rgba(138, 95, 65, 0.1);
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          .el-icon {
            font-size: 20px;
            color: #8a5f41;
          }
        }

        .header-title {
          font-size: 16px;
          color: #3d2c29;
          font-weight: 600;
          flex-grow: 1;
        }

        .card-options {
          .more-button {
            border: 1px solid #8a5f41;
            color: #8a5f41;
            background: none;

            &:hover {
              background-color: rgba(138, 95, 65, 0.05);
            }

            .el-icon {
              margin-left: 4px;
            }
          }
        }
      }
    }

    .user-profile {
      display: flex;
      padding: 24px;
      gap: 32px;

      @media (max-width: 768px) {
        flex-direction: column;
        align-items: center;
      }

      .avatar-section {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;

        .user-avatar {
          background-color: #8a5f41;
          color: #fff;
          font-weight: bold;
          font-size: 36px;
        }

        .user-role {
          margin-top: 8px;
        }
      }

      .info-section {
        flex-grow: 1;

        .info-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
          gap: 20px;

          .info-item {
            .info-label {
              font-size: 14px;
              color: #909399;
              margin-bottom: 8px;
            }

            .info-value {
              font-size: 16px;
              color: #303133;
              font-weight: 500;
            }
          }
        }
      }
    }

    .timeline-content {
      .login-status {
        font-weight: 500;
        margin-bottom: 4px;
      }

      .login-detail {
        display: flex;
        align-items: center;
        gap: 8px;

        .device-info {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .user-detail-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .action-buttons {
        width: 100%;
        justify-content: space-between;
      }
    }
  }
}
</style> 