<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search, RefreshLeft, View, StarFilled, DocumentCopy, Download, Calendar, Timer, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 借阅状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'borrowing', label: '借阅中' },
  { value: 'returned', label: '已归还' },
  { value: 'overdue', label: '已逾期' }
]

// 时间范围选项
const timeRangeOptions = [
  { value: '', label: '全部时间' },
  { value: 'last7days', label: '最近7天' },
  { value: 'last30days', label: '最近30天' },
  { value: 'last90days', label: '最近90天' },
  { value: 'custom', label: '自定义时间' }
]

// 搜索条件
const searchForm = reactive({
  keyword: '',
  status: '',
  timeRange: '',
  dateRange: [],
  showDatePicker: false
})

// 加载状态
const loading = ref(false)

// 所有借阅记录数据
const allRecords = ref([])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 监听时间范围变化
const handleTimeRangeChange = (value) => {
  if (value === 'custom') {
    searchForm.showDatePicker = true
  } else {
    searchForm.showDatePicker = false
    searchForm.dateRange = []
  }
}

// 筛选后的记录数据
const filteredRecords = computed(() => {
  let result = allRecords.value

  // 根据关键词筛选
  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(record => 
      record.title.toLowerCase().includes(keyword) || 
      record.borrower.toLowerCase().includes(keyword) || 
      record.borrowId.toLowerCase().includes(keyword)
    )
  }
  
  // 根据状态筛选
  if (searchForm.status) {
    result = result.filter(record => record.status === searchForm.status)
  }
  
  // 根据时间范围筛选
  if (searchForm.timeRange) {
    const now = new Date()
    let startDate = new Date()
    
    if (searchForm.timeRange === 'last7days') {
      startDate.setDate(now.getDate() - 7)
    } else if (searchForm.timeRange === 'last30days') {
      startDate.setDate(now.getDate() - 30)
    } else if (searchForm.timeRange === 'last90days') {
      startDate.setDate(now.getDate() - 90)
    } else if (searchForm.timeRange === 'custom' && searchForm.dateRange.length === 2) {
      // 自定义时间范围
      const start = new Date(searchForm.dateRange[0])
      const end = new Date(searchForm.dateRange[1])
      end.setHours(23, 59, 59, 999) // 设置结束时间为当天最后一秒
      
      result = result.filter(record => {
        const borrowDate = new Date(record.borrowDate)
        return borrowDate >= start && borrowDate <= end
      })
      
      // 更新分页信息并直接返回结果
      pagination.total = result.length
      return result
    }
    
    // 非自定义时间的筛选
    if (searchForm.timeRange !== 'custom') {
      result = result.filter(record => {
        const borrowDate = new Date(record.borrowDate)
        return borrowDate >= startDate && borrowDate <= now
      })
    }
  }
  
  // 更新分页信息
  pagination.total = result.length
  
  return result
})

// 表格显示的数据
const tableData = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredRecords.value.slice(start, end)
})

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  searchForm.timeRange = ''
  searchForm.dateRange = []
  searchForm.showDatePicker = false
  pagination.currentPage = 1
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

// 查看借阅详情
const viewRecord = (row) => {
  console.log('查看借阅详情:', row)
  ElMessage({
    type: 'info',
    message: `正在查看借阅记录 ${row.borrowId}`
  })
  // 这里应该显示详情对话框
}

// 续借图书
const renewBook = (row) => {
  // 检查是否可以续借
  if (row.renewCount >= row.maxRenewCount) {
    ElMessage({
      type: 'warning',
      message: `此书已达到最大续借次数 (${row.maxRenewCount}次)`
    })
    return
  }
  
  ElMessageBox.confirm(
    `确定要续借《${row.title}》吗？续借后归还日期将延长15天。`,
    '续借确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 模拟续借成功，实际应该调用API
    const index = allRecords.value.findIndex(record => record.borrowId === row.borrowId)
    if (index !== -1) {
      const record = allRecords.value[index]
      
      // 更新归还日期（增加15天）
      const returnDate = new Date(record.returnDate)
      returnDate.setDate(returnDate.getDate() + 15)
      record.returnDate = formatDate(returnDate)
      
      // 更新续借次数
      record.renewCount++
      
      ElMessage({
        type: 'success',
        message: `续借成功，新的归还日期为 ${record.returnDate}`
      })
    }
  }).catch(() => {})
}

// 归还图书
const returnBook = (row) => {
  ElMessageBox.confirm(
    `确定要归还《${row.title}》吗？`,
    '归还确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 模拟归还成功，实际应该调用API
    const index = allRecords.value.findIndex(record => record.borrowId === row.borrowId)
    if (index !== -1) {
      // 更新状态为已归还
      allRecords.value[index].status = 'returned'
      // 设置实际归还日期为今天
      allRecords.value[index].actualReturnDate = formatDate(new Date())
      
      ElMessage({
        type: 'success',
        message: '图书归还成功'
      })
    }
  }).catch(() => {})
}

// 导出借阅记录
const exportRecords = () => {
  ElMessage({
    type: 'success',
    message: '借阅记录导出成功'
  })
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.status === 'overdue') {
    return 'overdue-row'
  } else if (row.status === 'returned') {
    return 'returned-row'
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
    case 'borrowing': return 'primary'
    case 'returned': return 'success'
    case 'overdue': return 'danger'
    default: return 'info'
  }
}

// 获取状态显示文本
const getStatusText = (status) => {
  switch (status) {
    case 'borrowing': return '借阅中'
    case 'returned': return '已归还'
    case 'overdue': return '已逾期'
    default: return status
  }
}

// 计算剩余天数
const getRemainingDays = (returnDate) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const due = new Date(returnDate)
  const diffTime = due.getTime() - today.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays
}

// 模拟获取借阅记录数据
const fetchBorrowRecords = () => {
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 这里应该是调用真实的API
    // 模拟数据
    allRecords.value = [
      {
        borrowId: 'BR20230001',
        title: '活着',
        author: '余华',
        coverImage: '/src/assets/Image/book1.jpg',
        borrower: '张三',
        borrowDate: '2023-09-01',
        returnDate: '2023-10-01',
        actualReturnDate: null,
        status: 'returned',
        renewCount: 0,
        maxRenewCount: 2
      },
      {
        borrowId: 'BR20230002',
        title: '三体',
        author: '刘慈欣',
        coverImage: '/src/assets/Image/book2.jpg',
        borrower: '李四',
        borrowDate: '2023-09-15',
        returnDate: '2023-11-20',
        actualReturnDate: null,
        status: 'borrowing',
        renewCount: 1,
        maxRenewCount: 2
      },
      {
        borrowId: 'BR20230003',
        title: '平凡的世界',
        author: '路遥',
        coverImage: '/src/assets/Image/book3.jpg',
        borrower: '王五',
        borrowDate: '2023-08-15',
        returnDate: '2023-09-15',
        actualReturnDate: null,
        status: 'overdue',
        renewCount: 0,
        maxRenewCount: 2
      },
      {
        borrowId: 'BR20230004',
        title: '围城',
        author: '钱钟书',
        coverImage: '/src/assets/Image/book4.jpg',
        borrower: '赵六',
        borrowDate: '2023-10-10',
        returnDate: '2023-11-10',
        actualReturnDate: null,
        status: 'borrowing',
        renewCount: 0,
        maxRenewCount: 2
      },
      {
        borrowId: 'BR20230005',
        title: '人间失格',
        author: '太宰治',
        coverImage: '/src/assets/Image/book1.jpg',
        borrower: '张三',
        borrowDate: '2023-09-20',
        returnDate: '2023-10-20',
        actualReturnDate: '2023-10-18',
        status: 'returned',
        renewCount: 0,
        maxRenewCount: 2
      },
      // 添加更多模拟数据
      {
        borrowId: 'BR20230006',
        title: '月亮与六便士',
        author: '毛姆',
        coverImage: '/src/assets/Image/book2.jpg',
        borrower: '李四',
        borrowDate: '2023-10-05',
        returnDate: '2023-11-05',
        actualReturnDate: null,
        status: 'borrowing',
        renewCount: 0,
        maxRenewCount: 2
      },
      {
        borrowId: 'BR20230007',
        title: '百年孤独',
        author: '加西亚·马尔克斯',
        coverImage: '/src/assets/Image/book3.jpg',
        borrower: '王五',
        borrowDate: '2023-10-01',
        returnDate: '2023-11-01',
        actualReturnDate: null,
        status: 'borrowing',
        renewCount: 0,
        maxRenewCount: 2
      }
    ]
    
    // 更新总数
    pagination.total = allRecords.value.length
    loading.value = false
  }, 800) // 模拟加载时间
}

onMounted(() => {
  fetchBorrowRecords()
})
</script>

<template>
  <div class="borrow-records-container">
    <div class="page-header">
      <h2 class="page-title">借阅记录</h2>
      <div class="action-buttons">
        <el-button @click="exportRecords" class="export-button">
          <el-icon><Download /></el-icon>导出记录
        </el-button>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><DocumentCopy /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredRecords.filter(r => r.status === 'borrowing').length }}</div>
          <div class="stat-label">借阅中</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><StarFilled /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredRecords.filter(r => r.status === 'returned').length }}</div>
          <div class="stat-label">已归还</div>
        </div>
      </div>
      
      <div class="stat-card overdue-card">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ filteredRecords.filter(r => r.status === 'overdue').length }}</div>
          <div class="stat-label">已逾期</div>
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
            placeholder="书名/借阅人/编号" 
            clearable
            @keyup.enter="handleSearch"
            :prefix-icon="Search"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item label="借阅状态">
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
        
        <el-form-item label="借阅时间">
          <el-select 
            v-model="searchForm.timeRange" 
            placeholder="选择时间范围" 
            clearable 
            class="custom-select"
            @change="handleTimeRangeChange"
          >
            <el-option 
              v-for="item in timeRangeOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item v-if="searchForm.showDatePicker" label="自定义时间">
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
          <el-button type="primary" @click="handleSearch" class="search-button">
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
      共找到 <span class="highlight">{{ pagination.total }}</span> 条借阅记录
    </div>
    
    <!-- 借阅记录表格 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        border 
        v-loading="loading"
        row-key="borrowId"
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
        
        <el-table-column prop="borrowId" label="借阅编号" width="120" />
        
        <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <div class="book-title-cell">
              <span class="book-title-text">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="borrower" label="借阅人" width="120" />
        
        <el-table-column label="借阅日期" width="120">
          <template #default="scope">
            {{ scope.row.borrowDate }}
          </template>
        </el-table-column>
        
        <el-table-column label="应还日期" width="120">
          <template #default="scope">
            <span 
              :class="{ 
                'date-highlight': scope.row.status === 'borrowing' && getRemainingDays(scope.row.returnDate) <= 3,
                'date-overdue': scope.row.status === 'overdue'
              }"
            >
              {{ scope.row.returnDate }}
              <el-tooltip 
                v-if="scope.row.status === 'borrowing' && getRemainingDays(scope.row.returnDate) <= 3" 
                content="即将到期" 
                placement="top" 
                effect="light"
              >
                <el-icon class="warning-icon"><Timer /></el-icon>
              </el-tooltip>
            </span>
          </template>
        </el-table-column>
        
        <el-table-column label="实际归还" width="120">
          <template #default="scope">
            {{ scope.row.actualReturnDate || '-' }}
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
        
        <el-table-column label="续借次数" width="110" align="center">
          <template #default="scope">
            <span class="renew-count">
              {{ scope.row.renewCount }} / {{ scope.row.maxRenewCount }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top" effect="light">
                <el-button 
                  type="primary" 
                  size="small" 
                  circle
                  @click="viewRecord(scope.row)"
                  class="action-button view-button"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="续借" placement="top" effect="light" v-if="scope.row.status === 'borrowing'">
                <el-button 
                  type="warning" 
                  size="small" 
                  circle
                  @click="renewBook(scope.row)"
                  class="action-button renew-button"
                  :disabled="scope.row.renewCount >= scope.row.maxRenewCount"
                >
                  <el-icon><Calendar /></el-icon>
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="归还" placement="top" effect="light" v-if="scope.row.status === 'borrowing' || scope.row.status === 'overdue'">
                <el-button 
                  type="success" 
                  size="small" 
                  circle
                  @click="returnBook(scope.row)"
                  class="action-button return-button"
                >
                  <el-icon><DocumentCopy /></el-icon>
                </el-button>
              </el-tooltip>
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
      v-if="filteredRecords.length === 0 && !loading" 
      description="暂无借阅记录" 
      class="empty-data"
    />
  </div>
</template>

<style scoped lang="scss">
.borrow-records-container {
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
    
    .action-buttons {
      display: flex;
      gap: 12px;
      
      .export-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;
        border-radius: 8px;
        padding: 10px 20px;
        transition: all 0.3s;
        
        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
          transform: translateY(-2px);
        }
        
        .el-icon {
          margin-right: 6px;
        }
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
      background-color: #f9f6f2;
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
      
      &.overdue-card {
        background-color: #fff0f0;
        
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
        background-color: rgba(138, 95, 65, 0.1);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;
        color: #8a5f41;
        font-size: 24px;
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #3d2c29;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #8a5f41;
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
      
      .date-highlight {
        color: #e6a23c;
        font-weight: 500;
        
        .warning-icon {
          margin-left: 4px;
          color: #e6a23c;
          animation: pulse 1.5s infinite;
        }
      }
      
      .date-overdue {
        color: #f56c6c;
        font-weight: 500;
      }
      
      .renew-count {
        font-size: 14px;
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
        gap: 10px;
        
        .action-button {
          transition: all 0.3s;
          border: none;
          
          &:hover {
            transform: translateY(-2px);
          }
          
          &.view-button {
            background-color: #4c8dae;
          }
          
          &.renew-button {
            background-color: #e6a23c;
            
            &:disabled {
              background-color: #f0f0f0;
              cursor: not-allowed;
            }
          }
          
          &.return-button {
            background-color: #67c23a;
          }
        }
      }
    }
    
    .overdue-row {
      background-color: rgba(245, 108, 108, 0.05);
    }
    
    .returned-row {
      background-color: rgba(103, 194, 58, 0.05);
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
  .borrow-records-container {
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

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}
</style> 