<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search, RefreshLeft, CircleCheck, CircleCloseFilled, CircleClose, View, Check, MoreFilled, Tickets } from '@element-plus/icons-vue'
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
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
}

// 查看申请详情
const viewApplication = (row) => {
  ElMessage({
    type: 'info',
    message: `正在查看申请 ${row.applicationId}`
  })
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
const fetchApplications = () => {
  tableLoading.value = true
  
  try {
    // 模拟数据加载
    setTimeout(() => {
      // 生成模拟数据
      allApplications.value = generateMockData().records
      pagination.total = allApplications.value.length
      
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
  const books = [
    '活着', '追风筝的人', '百年孤独', '三体', '球状闪电',
    '目送', '围城', '小王子', '解忧杂货店', '人类简史',
    'JavaScript高级程序设计', 'Vue.js实战', 'CSS权威指南', '算法导论', '数据结构'
  ]
  const users = [
    '张三', '李四', '王五', '赵六', '钱七',
    '孙八', '周九', '吴十', '郑十一', '王十二'
  ]
  
  for (let i = 1; i <= 23; i++) {
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    const type = types[Math.floor(Math.random() * types.length)]
    const book = books[Math.floor(Math.random() * books.length)]
    const user = users[Math.floor(Math.random() * users.length)]
    
    // 生成随机日期，最近30天内
    const today = new Date()
    const randomDays = Math.floor(Math.random() * 30)
    const applyDate = new Date(today)
    applyDate.setDate(today.getDate() - randomDays)
    
    records.push({
      id: i,
      applicationId: `BRQ-2023-${1000 + i}`,
      userId: `USER${1000 + i}`,
      bookId: `BOOK${2000 + i}`,
      type,
      title: book,
      applicant: user,
      applyDate: formatDate(applyDate),
      status,
      processDate: status !== 'pending' ? formatDate(new Date(applyDate.getTime() + 86400000)) : null,
      processBy: status !== 'pending' ? '管理员' : null,
      rejectReason: status === 'rejected' ? '暂时无法借阅' : null,
      notes: `借阅申请${i}的备注信息`
    })
  }
  
  return {
    records,
    total: records.length
  }
}

// 组件挂载时加载数据
onMounted(() => {
  fetchApplications()
})
</script>

<template>
  <div class="borrow-applications-container">
    <el-card class="applications-header" shadow="hover">
      <div class="header-content">
        <div class="header-title">
          <h2><el-icon><Tickets /></el-icon> 借阅申请管理</h2>
          <p>管理和处理用户的借阅申请</p>
        </div>
      </div>
    </el-card>
    
    <el-card class="applications-content" shadow="hover">
      <div class="search-area">
        <el-form :model="searchForm" class="search-form" inline>
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索申请编号/申请人/图书"
              prefix-icon="Search"
              clearable
            />
          </el-form-item>
          
          <el-form-item>
            <el-select v-model="searchForm.status" placeholder="申请状态" clearable>
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY/MM/DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="fetchApplications" :loading="loading">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><RefreshLeft /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="table-area">
        <el-table
          :data="tableData"
          style="width: 100%"
          border
          v-loading="tableLoading"
          :row-class-name="tableRowClassName"
        >
          <el-table-column type="index" width="50" />
          
          <el-table-column prop="applicationId" label="申请编号" width="120" />
          
          <el-table-column prop="title" label="图书名称" min-width="150" show-overflow-tooltip />
          
          <el-table-column prop="applicant" label="申请人" width="100" />
          
          <el-table-column prop="type" label="申请类型" width="100">
            <template #default="scope">
              <el-tag
                :type="scope.row.type === 'renewal' ? 'success' : scope.row.type === 'reservation' ? 'info' : 'primary'"
                size="small"
              >
                {{ scope.row.type === 'renewal' ? '续借' : scope.row.type === 'reservation' ? '预约' : '新借' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyDate" label="申请日期" width="110" />
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag
                :type="getStatusType(scope.row.status)"
                size="small"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="processDate" label="处理日期" width="110" />
          
          <el-table-column prop="processBy" label="处理人" width="100" />
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button
                v-if="scope.row.status === 'pending'"
                type="success"
                size="small"
                @click="approveApplication(scope.row)"
                text
              >
                <el-icon><Check /></el-icon>批准
              </el-button>
              
              <el-button
                v-if="scope.row.status === 'pending'"
                type="danger"
                size="small"
                @click="rejectApplication(scope.row)"
                text
              >
                <el-icon><CircleClose /></el-icon>拒绝
              </el-button>
              
              <el-button
                type="primary"
                size="small"
                @click="viewApplication(scope.row)"
                text
              >
                <el-icon><View /></el-icon>详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-area">
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
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.borrow-applications-container {
  padding: 24px;
  
  .applications-header {
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    
    .header-content {
      .header-title {
        h2 {
          font-size: 22px;
          color: #3d2c29;
          margin: 0 0 5px 0;
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 8px;
            color: #8a5f41;
          }
        }
        
        p {
          font-size: 14px;
          color: #8a5f41;
          margin: 0;
        }
      }
    }
  }
  
  .applications-content {
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    
    .search-area {
      margin-bottom: 20px;
    }
    
    .table-area {
      .pagination-area {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
      }
    }
  }
}

// 表格行样式
:deep(.approved-row) {
  background-color: rgba(103, 194, 58, 0.1);
}

:deep(.rejected-row) {
  background-color: rgba(245, 108, 108, 0.1);
}
</style> 