<template>
  <div class="user-logs-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="title-section">
        <el-button @click="goBack" class="back-button">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h2 class="page-title">{{ userInfo.username }} 的操作日志</h2>
      </div>
      <div class="action-buttons">
        <el-button @click="handleExport" :loading="exporting" class="export-button">
          <el-icon><Download /></el-icon>导出日志
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <div class="card-header">
        <div class="header-icon">
          <el-icon><Search /></el-icon>
        </div>
        <div class="header-title">搜索筛选</div>
      </div>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="操作模块">
          <el-select
            v-model="searchForm.module"
            placeholder="选择模块"
            clearable
            class="custom-select"
          >
            <el-option
              v-for="item in moduleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="操作类型">
          <el-select
            v-model="searchForm.actionType"
            placeholder="选择类型"
            clearable
            class="custom-select"
          >
            <el-option
              v-for="item in actionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="操作状态">
          <el-select
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            class="custom-select"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            class="custom-date-picker"
          />
        </el-form-item>

        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索操作内容"
            clearable
            @keyup.enter="handleSearch"
            :prefix-icon="Search"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-button">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch" class="reset-button">
            <el-icon><RefreshLeft /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志表格 -->
    <el-card class="logs-card">
      <div class="logs-header">
        <div class="logs-title">操作日志列表</div>
        <div class="logs-summary">
          共 <span class="highlight">{{ pagination.total }}</span> 条日志记录
        </div>
      </div>

      <el-table
        :data="logsList"
        style="width: 100%"
        border
        v-loading="loading"
        :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
      >
        <el-table-column type="index" width="50" align="center" />

        <el-table-column prop="time" label="操作时间" width="170" sortable>
          <template #default="scope">
            {{ formatDateTime(scope.row.time) }}
          </template>
        </el-table-column>

        <el-table-column prop="module" label="操作模块" width="120">
          <template #default="scope">
            <el-tag
              :type="getModuleType(scope.row.module)"
              size="small"
              effect="light"
            >
              {{ scope.row.module }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="actionType" label="操作类型" width="120">
          <template #default="scope">
            <el-tag
              :type="getActionType(scope.row.actionType)"
              size="small"
              effect="plain"
            >
              {{ getActionLabel(scope.row.actionType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="action" label="操作内容" min-width="250" show-overflow-tooltip />

        <el-table-column prop="ip" label="IP地址" width="130" />

        <el-table-column prop="device" label="设备信息" width="120" show-overflow-tooltip />

        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'success' ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-tooltip content="查看详情" placement="top" effect="light">
              <el-button
                type="primary"
                link
                @click="handleViewDetail(scope.row)"
              >
                查看详情
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <!-- 日志详情弹窗 -->
    <el-dialog
      title="日志详情"
      v-model="detailDialogVisible"
      width="700px"
    >
      <div class="log-detail-content">
        <div class="detail-item">
          <div class="detail-label">操作时间</div>
          <div class="detail-value">{{ formatDateTime(currentLog.time) }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">操作用户</div>
          <div class="detail-value">{{ userInfo.username }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">操作模块</div>
          <div class="detail-value">
            <el-tag
              :type="getModuleType(currentLog.module)"
              size="small"
              effect="light"
            >
              {{ currentLog.module }}
            </el-tag>
          </div>
        </div>
        <div class="detail-item">
          <div class="detail-label">操作类型</div>
          <div class="detail-value">
            <el-tag
              :type="getActionType(currentLog.actionType)"
              size="small"
              effect="plain"
            >
              {{ getActionLabel(currentLog.actionType) }}
            </el-tag>
          </div>
        </div>
        <div class="detail-item">
          <div class="detail-label">操作内容</div>
          <div class="detail-value">{{ currentLog.action }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">操作状态</div>
          <div class="detail-value">
            <el-tag
              :type="currentLog.status === 'success' ? 'success' : 'danger'"
              size="small"
            >
              {{ currentLog.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </div>
        </div>
        <div class="detail-item">
          <div class="detail-label">IP地址</div>
          <div class="detail-value">{{ currentLog.ip }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">设备信息</div>
          <div class="detail-value">{{ currentLog.device }}</div>
        </div>

        <template v-if="currentLog.status === 'failed'">
          <div class="detail-item">
            <div class="detail-label">失败原因</div>
            <div class="detail-value error-message">{{ currentLog.errorMessage || '未知错误' }}</div>
          </div>
        </template>

        <div class="detail-item full-width" v-if="currentLog.params">
          <div class="detail-label">请求参数</div>
          <div class="detail-value code-block">
            <pre>{{ formatJson(currentLog.params) }}</pre>
          </div>
        </div>

        <div class="detail-item full-width" v-if="currentLog.response">
          <div class="detail-label">响应结果</div>
          <div class="detail-value code-block">
            <pre>{{ formatJson(currentLog.response) }}</pre>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, Download, Search, RefreshLeft
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userId = route.params.id

// 模块选项
const moduleOptions = [
  { value: '图书管理', label: '图书管理' },
  { value: '借阅管理', label: '借阅管理' },
  { value: '用户管理', label: '用户管理' },
  { value: '系统设置', label: '系统设置' }
]

// 操作类型选项
const actionOptions = [
  { value: 'create', label: '新增' },
  { value: 'update', label: '修改' },
  { value: 'delete', label: '删除' },
  { value: 'query', label: '查询' },
  { value: 'login', label: '登录' },
  { value: 'logout', label: '登出' },
  { value: 'import', label: '导入' },
  { value: 'export', label: '导出' },
  { value: 'approve', label: '审批' },
  { value: 'reject', label: '拒绝' }
]

// 状态选项
const statusOptions = [
  { value: 'success', label: '成功' },
  { value: 'failed', label: '失败' }
]

// 搜索表单
const searchForm = reactive({
  module: '',
  actionType: '',
  status: '',
  dateRange: [],
  keyword: ''
})

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  nickname: ''
})

// 加载和导出状态
const loading = ref(false)
const exporting = ref(false)

// 日志列表
const logsList = ref([])
const allLogs = ref([])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 日志详情弹窗
const detailDialogVisible = ref(false)
const currentLog = reactive({
  id: '',
  time: '',
  module: '',
  actionType: '',
  action: '',
  status: '',
  ip: '',
  device: '',
  errorMessage: '',
  params: null,
  response: null
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  filterLogs()
}

// 重置搜索
const resetSearch = () => {
  searchForm.module = ''
  searchForm.actionType = ''
  searchForm.status = ''
  searchForm.dateRange = []
  searchForm.keyword = ''
  handleSearch()
}

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  filterLogs()
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  filterLogs()
}

// 查看日志详情
const handleViewDetail = (log) => {
  Object.assign(currentLog, log)
  detailDialogVisible.value = true
}

// 处理导出
const handleExport = () => {
  exporting.value = true
  
  // TODO: 调用导出API
  setTimeout(() => {
    ElMessage.success('日志导出成功')
    exporting.value = false
  }, 1000)
}

// 筛选日志
const filterLogs = () => {
  let result = [...allLogs.value]
  
  // 按模块筛选
  if (searchForm.module) {
    result = result.filter(log => log.module === searchForm.module)
  }
  
  // 按操作类型筛选
  if (searchForm.actionType) {
    result = result.filter(log => log.actionType === searchForm.actionType)
  }
  
  // 按状态筛选
  if (searchForm.status) {
    result = result.filter(log => log.status === searchForm.status)
  }
  
  // 按日期范围筛选
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const start = new Date(searchForm.dateRange[0])
    const end = new Date(searchForm.dateRange[1])
    end.setHours(23, 59, 59, 999) // 设置结束时间为当天最后一秒
    
    result = result.filter(log => {
      const logTime = new Date(log.time)
      return logTime >= start && logTime <= end
    })
  }
  
  // 按关键字筛选
  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(log => 
      log.action.toLowerCase().includes(keyword) || 
      (log.params && JSON.stringify(log.params).toLowerCase().includes(keyword))
    )
  }
  
  // 更新分页信息
  pagination.total = result.length
  
  // 获取当前页的数据
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  logsList.value = result.slice(start, end)
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '-'
  if (typeof date === 'string') date = new Date(date)
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取操作类型标签样式
const getActionType = (actionType) => {
  switch (actionType) {
    case 'create': return 'success'
    case 'update': return 'warning'
    case 'delete': return 'danger'
    case 'query': return 'info'
    case 'login': return 'primary'
    case 'logout': return ''
    case 'import': return 'warning'
    case 'export': return 'warning'
    case 'approve': return 'success'
    case 'reject': return 'danger'
    default: return 'info'
  }
}

// 获取操作类型显示文本
const getActionLabel = (actionType) => {
  const option = actionOptions.find(item => item.value === actionType)
  return option ? option.label : actionType
}

// 获取模块标签类型
const getModuleType = (module) => {
  switch (module) {
    case '图书管理': return 'primary'
    case '借阅管理': return 'success'
    case '用户管理': return 'warning'
    case '系统设置': return 'info'
    default: return ''
  }
}

// 格式化JSON显示
const formatJson = (json) => {
  if (!json) return ''
  try {
    if (typeof json === 'string') {
      json = JSON.parse(json)
    }
    return JSON.stringify(json, null, 2)
  } catch (e) {
    return json
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // TODO: 调用用户详情API
    // 模拟API调用
    setTimeout(() => {
      Object.assign(userInfo, {
        id: userId,
        username: 'admin',
        nickname: '系统管理员'
      })
    }, 500)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取日志数据
const fetchLogsList = async () => {
  loading.value = true
  try {
    // TODO: 调用日志列表API
    // 模拟API调用
    setTimeout(() => {
      // 生成30条模拟日志数据
      const logs = []
      const modules = ['图书管理', '借阅管理', '用户管理', '系统设置']
      const actionTypes = ['create', 'update', 'delete', 'query', 'login', 'logout', 'approve', 'reject']
      const statuses = ['success', 'failed']
      const devices = ['Chrome 98.0 / Windows 10', 'Firefox 97.0 / macOS', 'Safari / iOS', 'Edge / Windows 10']
      
      for (let i = 0; i < 30; i++) {
        const module = modules[Math.floor(Math.random() * modules.length)]
        const actionType = actionTypes[Math.floor(Math.random() * actionTypes.length)]
        const status = statuses[Math.floor(Math.random() * statuses.length)]
        const device = devices[Math.floor(Math.random() * devices.length)]
        
        let action = ''
        if (module === '图书管理') {
          if (actionType === 'create') action = '新增图书《三体》'
          else if (actionType === 'update') action = '更新图书《活着》信息'
          else if (actionType === 'delete') action = '删除图书《围城》'
          else if (actionType === 'query') action = '查询图书列表'
        } else if (module === '借阅管理') {
          if (actionType === 'approve') action = '审批借阅申请BR20230001'
          else if (actionType === 'reject') action = '拒绝借阅申请BR20230002'
          else if (actionType === 'query') action = '查询借阅记录'
        } else if (module === '用户管理') {
          if (actionType === 'create') action = '新增用户张三'
          else if (actionType === 'update') action = '修改用户李四信息'
          else if (actionType === 'delete') action = '删除用户王五'
        } else if (module === '系统设置') {
          if (actionType === 'update') action = '修改系统参数'
          else if (actionType === 'query') action = '查看系统日志'
        }
        
        const daysAgo = Math.floor(Math.random() * 30)
        const hoursAgo = Math.floor(Math.random() * 24)
        const minutesAgo = Math.floor(Math.random() * 60)
        const time = new Date()
        time.setDate(time.getDate() - daysAgo)
        time.setHours(time.getHours() - hoursAgo)
        time.setMinutes(time.getMinutes() - minutesAgo)
        
        const params = actionType !== 'query' ? {
          id: Math.floor(Math.random() * 100) + 1,
          name: '示例参数名称',
          value: '示例参数值'
        } : null
        
        const errorMessage = status === 'failed' ? '操作失败，权限不足' : null
        
        logs.push({
          id: `LOG${String(i + 1).padStart(5, '0')}`,
          time,
          module,
          actionType,
          action,
          status,
          ip: `192.168.1.${Math.floor(Math.random() * 254) + 1}`,
          device,
          errorMessage,
          params,
          response: status === 'success' ? { code: 200, message: '操作成功' } : { code: 403, message: '权限不足' }
        })
      }
      
      // 按时间排序
      logs.sort((a, b) => new Date(b.time) - new Date(a.time))
      
      allLogs.value = logs
      filterLogs()
      loading.value = false
    }, 1000)
  } catch (error) {
    console.error('获取日志列表失败:', error)
    loading.value = false
  }
}

// 监听搜索条件变化
watch(
  [() => searchForm.module, () => searchForm.actionType, () => searchForm.status],
  () => {
    if (allLogs.value.length > 0) {
      filterLogs()
    }
  }
)

onMounted(() => {
  fetchUserInfo()
  fetchLogsList()
})
</script>

<style scoped lang="scss">
.user-logs-container {
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

      .export-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;
        border-radius: 8px;
        
        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
        }
      }

      .el-icon {
        margin-right: 6px;
      }
    }
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;

    .card-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

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
      }
    }

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;

      .el-form-item {
        margin-bottom: 10px;
        margin-right: 0;

        .el-form-item__label {
          color: #3d2c29;
          font-weight: 500;
        }
      }

      .custom-input,
      .custom-select,
      .custom-date-picker {
        .el-input__wrapper,
        :deep(.el-range-editor.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;

          &:hover,
          &.is-focus {
            box-shadow: 0 0 0 1px #8a5f41 inset;
          }
        }

        .el-input__prefix-inner {
          color: #8a5f41;
        }
      }

      .search-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        border-radius: 8px;
        padding: 10px 20px;
        transition: all 0.3s;

        &:hover {
          background-color: #6e4c34;
          transform: translateY(-2px);
        }
      }

      .reset-button {
        border-color: #8a5f41;
        color: #8a5f41;
        border-radius: 8px;
        padding: 10px 20px;
        transition: all 0.3s;

        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
          transform: translateY(-2px);
        }
      }
    }
  }

  .logs-card {
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;

    .logs-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f0f0f0;
      background-color: #f9f6f2;

      .logs-title {
        font-size: 16px;
        color: #3d2c29;
        font-weight: 600;
      }

      .logs-summary {
        font-size: 14px;
        color: #606266;

        .highlight {
          color: #e36049;
          font-weight: 600;
        }
      }
    }

    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 20px;
      padding: 10px 0;

      :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
        background-color: #8a5f41;
      }

      :deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
        color: #8a5f41;
      }
    }
  }

  .log-detail-content {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;

    .detail-item {
      width: calc(50% - 10px);

      &.full-width {
        width: 100%;
      }

      .detail-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .detail-value {
        font-size: 14px;
        color: #303133;
        word-break: break-word;

        &.error-message {
          color: #f56c6c;
        }

        &.code-block {
          background-color: #f8f8f8;
          padding: 10px;
          border-radius: 4px;
          font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
          overflow-x: auto;

          pre {
            margin: 0;
            font-size: 12px;
            line-height: 1.5;
          }
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .user-logs-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .action-buttons {
        width: 100%;
      }
    }

    .search-form {
      .el-form-item {
        width: 100%;
      }
    }

    .log-detail-content {
      .detail-item {
        width: 100%;
      }
    }
  }
}
</style> 