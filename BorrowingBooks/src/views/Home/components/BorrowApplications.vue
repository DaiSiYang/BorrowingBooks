<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search, RefreshLeft, CheckCircle, CircleCloseFilled, CircleClose, View, Check, MoreFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/http.js'

// 申请状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'pending', label: '待处理' },
  { value: 'approved', label: '已批准' },
  { value: 'rejected', label: '已拒绝' }
]

// 搜索条件
const searchForm = reactive({
  keyword: '',
  status: '',
  dateRange: null,
  userId: ''
})

// 加载状态
const loading = ref(false)
const tableLoading = ref(false)

// 所有申请数据
const allApplications = ref([])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 筛选后的申请数据
const filteredApplications = computed(() => {
  let result = allApplications.value

  // 根据关键词筛选
  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(app => 
      app.title.toLowerCase().includes(keyword) || 
      app.applicant.toLowerCase().includes(keyword) || 
      app.applicationId.toLowerCase().includes(keyword)
    )
  }
  
  // 根据状态筛选
  if (searchForm.status) {
    result = result.filter(app => app.status === searchForm.status)
  }
  
  // 根据日期范围筛选
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const start = new Date(searchForm.dateRange[0])
    const end = new Date(searchForm.dateRange[1])
    end.setHours(23, 59, 59, 999) // 设置结束时间为当天最后一秒
    
    result = result.filter(app => {
      const applyDate = new Date(app.applyDate)
      return applyDate >= start && applyDate <= end
    })
  }
  
  // 更新分页信息
  pagination.total = result.length
  
  return result
})

// 表格显示的数据
const tableData = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredApplications.value.slice(start, end)
})

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  searchForm.dateRange = null
  searchForm.userId = ''
  fetchApplications()
}

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchApplications()
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  fetchApplications()
}

// 查看申请详情
const viewApplication = (row) => {
  console.log('查看申请详情:', row)
  ElMessage({
    type: 'info',
    message: `正在查看申请 ${row.applicationId}`
  })
  // 这里应该显示详情对话框
}

// 批准申请
const approveApplication = (row) => {
  ElMessageBox.confirm(
    `确定批准用户"${row.applicant}"借阅《${row.title}》吗？`,
    '批准确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 模拟批准成功，实际应该调用API
    const index = allApplications.value.findIndex(app => app.applicationId === row.applicationId)
    if (index !== -1) {
      allApplications.value[index].status = 'approved'
      allApplications.value[index].processDate = formatDate(new Date())
      allApplications.value[index].processBy = '管理员'
      
      ElMessage({
        type: 'success',
        message: '已批准借阅申请'
      })
    }
  }).catch(() => {})
}

// 拒绝申请
const rejectApplication = (row) => {
  ElMessageBox.prompt(
    `确定拒绝用户"${row.applicant}"借阅《${row.title}》的申请吗？`,
    '拒绝确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝理由（可选）',
      type: 'warning'
    }
  ).then(({ value }) => {
    // 模拟拒绝成功，实际应该调用API
    const index = allApplications.value.findIndex(app => app.applicationId === row.applicationId)
    if (index !== -1) {
      allApplications.value[index].status = 'rejected'
      allApplications.value[index].processDate = formatDate(new Date())
      allApplications.value[index].processBy = '管理员'
      allApplications.value[index].rejectReason = value || '暂无理由'
      
      ElMessage({
        type: 'success',
        message: '已拒绝借阅申请'
      })
    }
  }).catch(() => {})
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.status === 'approved') {
    return 'approved-row'
  } else if (row.status === 'rejected') {
    return 'rejected-row'
  }
  return ''
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string') date = new Date(date)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}

// 获取状态显示文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待处理'
    case 'approved': return '已批准'
    case 'rejected': return '已拒绝'
    default: return status
  }
}

// 获取借阅申请列表
const fetchApplications = async () => {
  tableLoading.value = true
  
  try {
    // 这里应该替换为实际的API调用
    // const response = await service.post('/borrowApplications/list', {
    //   ...searchForm,
    //   page: pagination.currentPage,
    //   pageSize: pagination.pageSize
    // })
    
    // 模拟API响应
    setTimeout(() => {
      // 模拟数据
      const mockData = generateMockData()
      allApplications.value = mockData.records
      pagination.total = mockData.total
      
      tableLoading.value = false
    }, 500)
  } catch (error) {
    console.error('获取借阅申请失败:', error)
    tableLoading.value = false
  }
}

// 生成模拟数据
const generateMockData = () => {
  const records = []
  const statuses = ['pending', 'approved', 'rejected']
  const types = ['new', 'renewal', 'reservation']
  
  for (let i = 1; i <= 23; i++) {
    // 应该根据搜索条件筛选数据
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    
    // 如果有状态筛选且不匹配，则跳过
    if (searchForm.status && searchForm.status !== status) continue
    
    // 如果有关键词筛选
    if (searchForm.keyword && !`用户${i}`.includes(searchForm.keyword) && 
        !`图书标题${i}`.includes(searchForm.keyword)) continue
    
    records.push({
      id: `BRQ-2023-${1000 + i}`,
      userId: `USER${1000 + i}`,
      userName: `用户${i}`,
      bookId: `BOOK${2000 + i}`,
      bookTitle: `图书标题${i}`,
      applicationTime: new Date(2023, 5, i).toLocaleString(),
      expectedReturnTime: new Date(2023, 6, i + 30).toLocaleString(),
      status: status,
      type: types[i % 3],
      reason: `申请理由：这本书对我的学习/研究非常有帮助，希望能借阅/续借${i}天。`,
      remark: status !== 'pending' ? `处理备注：${status === 'approved' ? '符合借阅条件，批准申请' : '库存不足，暂时无法借出'}` : ''
    })
  }
  
  // 分页处理
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  
  return {
    records: records.slice(start, end),
    total: records.length
  }
}

// 处理借阅申请
const handleApplication = async (row, action) => {
  const actionText = action === 'approve' ? '批准' : '拒绝'
  const confirmMessage = action === 'approve' 
    ? `确定批准用户 ${row.userName} 的借阅申请吗？`
    : `确定拒绝用户 ${row.userName} 的借阅申请吗？请确保已告知用户拒绝原因。`
  
  ElMessageBox.confirm(confirmMessage, '操作确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: action === 'approve' ? 'success' : 'warning'
  }).then(async () => {
    loading.value = true
    
    try {
      // 这里应该替换为实际的API调用
      // await service.post(`/borrowApplications/${action}`, { id: row.id })
      
      // 模拟API调用
      setTimeout(() => {
        // 更新本地状态
        const index = allApplications.value.findIndex(item => item.id === row.id)
        if (index !== -1) {
          allApplications.value[index].status = action === 'approve' ? 'approved' : 'rejected'
          allApplications.value[index].remark = action === 'approve' 
            ? '处理备注：符合借阅条件，批准申请' 
            : '处理备注：库存不足，暂时无法借出'
        }
        
        ElMessage({
          type: 'success',
          message: `已${actionText}借阅申请`
        })
        
        loading.value = false
      }, 500)
    } catch (error) {
      console.error(`${actionText}借阅申请失败:`, error)
      ElMessage.error(`${actionText}失败，请重试`)
      loading.value = false
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 查看申请详情
const viewApplicationDetail = (row) => {
  ElMessageBox.alert(
    `
    <div class="application-detail">
      <p><strong>申请编号：</strong>${row.id}</p>
      <p><strong>用户信息：</strong>${row.userName} (${row.userId})</p>
      <p><strong>图书信息：</strong>${row.bookTitle} (${row.bookId})</p>
      <p><strong>申请类型：</strong>${getApplicationTypeLabel(row.type)}</p>
      <p><strong>申请时间：</strong>${row.applicationTime}</p>
      <p><strong>预计归还时间：</strong>${row.expectedReturnTime}</p>
      <p><strong>申请状态：</strong>${getStatusLabel(row.status)}</p>
      <p><strong>申请理由：</strong>${row.reason}</p>
      ${row.remark ? `<p><strong>处理备注：</strong>${row.remark}</p>` : ''}
    </div>
    `,
    '申请详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 获取状态标签
const getStatusLabel = (status) => {
  return statusOptions.find(option => option.value === status)?.label || status
}

// 获取申请类型标签
const getApplicationTypeLabel = (type) => {
  return applicationTypes.find(option => option.value === type)?.label || type
}

// 申请类型选项
const applicationTypes = [
  { value: 'new', label: '新借阅' },
  { value: 'renewal', label: '续借' },
  { value: 'reservation', label: '预约' }
]

onMounted(() => {
  fetchApplications()
})
</script>

<template>
  <div class="borrow-applications-container">
    <div class="page-header">
      <h2 class="page-title">借阅申请</h2>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card pending-card">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredApplications.filter(a => a.status === 'pending').length }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
      
      <div class="stat-card approved-card">
        <div class="stat-icon">
          <el-icon><CheckCircle /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredApplications.filter(a => a.status === 'approved').length }}</div>
          <div class="stat-label">已批准</div>
        </div>
      </div>
      
      <div class="stat-card rejected-card">
        <div class="stat-icon">
          <el-icon><CircleClose /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredApplications.filter(a => a.status === 'rejected').length }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
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
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="书名/申请人/编号" 
            clearable
            @keyup.enter="fetchApplications"
            :prefix-icon="Search"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item label="申请状态">
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
        
        <el-form-item label="申请时间">
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
        
        <el-form-item>
          <el-button type="primary" @click="fetchApplications" class="search-button">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch" class="reset-button">
            <el-icon><RefreshLeft /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 结果信息 -->
    <div class="result-info">
      共找到 <span class="highlight">{{ pagination.total }}</span> 条申请记录
    </div>
    
    <!-- 申请记录表格 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        border 
        v-loading="tableLoading"
        row-key="applicationId"
        :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="封面" width="80" align="center">
          <template #default="scope">
            <el-image 
              :src="scope.row.coverImage" 
              :preview-src-list="[scope.row.coverImage]"
              fit="cover"
              style="width: 60px; height: 80px; border-radius: 6px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="applicationId" label="申请编号" width="120" />
        
        <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <div class="book-title-cell">
              <span class="book-title-text">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="applicant" label="申请人" width="100" />
        
        <el-table-column label="申请日期" width="120">
          <template #default="scope">
            {{ scope.row.applyDate }}
          </template>
        </el-table-column>
        
        <el-table-column label="借阅天数" width="100" align="center">
          <template #default="scope">
            <span class="borrow-days">{{ scope.row.expectedBorrowDays }}天</span>
          </template>
        </el-table-column>
        
        <el-table-column label="申请原因" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.reason }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="getStatusType(scope.row.status)"
              size="small"
              effect="dark"
              class="status-tag"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top" effect="light">
                <el-button 
                  type="primary" 
                  size="small" 
                  circle
                  @click="viewApplicationDetail(scope.row)"
                  class="action-button view-button"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              
              <template v-if="scope.row.status === 'pending'">
                <el-tooltip content="批准" placement="top" effect="light">
                  <el-button 
                    type="success" 
                    size="small" 
                    circle
                    @click="handleApplication(scope.row, 'approve')"
                    class="action-button approve-button"
                  >
                    <el-icon><Check /></el-icon>
                  </el-button>
                </el-tooltip>
                
                <el-tooltip content="拒绝" placement="top" effect="light">
                  <el-button 
                    type="danger" 
                    size="small" 
                    circle
                    @click="handleApplication(scope.row, 'reject')"
                    class="action-button reject-button"
                  >
                    <el-icon><CircleClose /></el-icon>
                  </el-button>
                </el-tooltip>
              </template>
              
              <template v-else>
                <span class="processed-info">
                  {{ scope.row.processDate }} 由 {{ scope.row.processBy }} 处理
                </span>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
    
    <!-- 无数据提示 -->
    <el-empty 
      v-if="filteredApplications.length === 0 && !tableLoading" 
      description="暂无借阅申请" 
      class="empty-data"
    />
  </div>
</template>

<style scoped lang="scss">
.borrow-applications-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .page-title {
      font-size: 24px;
      color: #3d2c29;
      margin: 0;
      position: relative;
      padding-left: 16px;
      font-weight: 600;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: linear-gradient(to bottom, #8a5f41, #e36049);
        border-radius: 2px;
      }
    }
  }
  
  .stats-cards {
    display: flex;
    gap: 20px;
    margin-bottom: 24px;
    flex-wrap: wrap;
    
    .stat-card {
      flex: 1;
      min-width: 200px;
      border-radius: 10px;
      padding: 20px;
      display: flex;
      align-items: center;
      transition: all 0.3s;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 15px rgba(0, 0, 0, 0.08);
      }
      
      &.pending-card {
        background-color: #fdf6ec;
        
        .stat-icon {
          background-color: rgba(230, 162, 60, 0.1);
          color: #e6a23c;
        }
        
        .stat-info {
          .stat-value {
            color: #e6a23c;
          }
          
          .stat-label {
            color: #e6a23c;
          }
        }
      }
      
      &.approved-card {
        background-color: #f0f9eb;
        
        .stat-icon {
          background-color: rgba(103, 194, 58, 0.1);
          color: #67c23a;
        }
        
        .stat-info {
          .stat-value {
            color: #67c23a;
          }
          
          .stat-label {
            color: #67c23a;
          }
        }
      }
      
      &.rejected-card {
        background-color: #fef0f0;
        
        .stat-icon {
          background-color: rgba(245, 108, 108, 0.1);
          color: #f56c6c;
        }
        
        .stat-info {
          .stat-value {
            color: #f56c6c;
          }
          
          .stat-label {
            color: #f56c6c;
          }
        }
      }
      
      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;
        font-size: 24px;
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 24px;
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;
    
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
      
      .custom-input, .custom-select, .custom-date-picker {
        .el-input__wrapper, :deep(.el-range-editor.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;
          
          &:hover, &.is-focus {
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
  
  .result-info {
    margin-bottom: 16px;
    font-size: 14px;
    color: #666;
    
    .highlight {
      color: #e36049;
      font-weight: 600;
      font-size: 16px;
    }
  }
  
  .table-card {
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;
    margin-bottom: 30px;
    
    .el-table {
      border-radius: 8px;
      overflow: hidden;
      
      .book-title-cell {
        .book-title-text {
          font-weight: 500;
          color: #3d2c29;
        }
      }
      
      .borrow-days {
        font-weight: 500;
        color: #8a5f41;
      }
      
      .status-tag {
        border-radius: 12px;
        font-weight: 500;
        padding: 0 10px;
      }
      
      .table-actions {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
        
        .processed-info {
          font-size: 13px;
          color: #8a5f41;
        }
        
        .action-button {
          transition: all 0.3s;
          border: none;
          
          &:hover {
            transform: translateY(-2px);
          }
          
          &.view-button {
            background-color: #4c8dae;
          }
          
          &.approve-button {
            background-color: #67c23a;
          }
          
          &.reject-button {
            background-color: #f56c6c;
          }
        }
      }
    }
    
    .approved-row {
      background-color: rgba(103, 194, 58, 0.05);
    }
    
    .rejected-row {
      background-color: rgba(245, 108, 108, 0.05);
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
  
  .empty-data {
    margin: 40px 0;
    
    .el-button {
      background-color: #8a5f41;
      border-color: #8a5f41;
      margin-top: 16px;
      
      &:hover {
        background-color: #6e4c34;
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .borrow-applications-container {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .stats-cards {
      flex-direction: column;
      
      .stat-card {
        width: 100%;
      }
    }
    
    .search-form {
      .el-form-item {
        width: 100%;
      }
    }
  }
}
</style> 