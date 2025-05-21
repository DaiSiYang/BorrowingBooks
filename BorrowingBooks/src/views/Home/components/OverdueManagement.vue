<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search, RefreshLeft, View, Message, Phone, Download, Bell, Warning, Money } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 逾期状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'overdue', label: '逾期中' },
  { value: 'returned', label: '已归还' },
  { value: 'compensated', label: '已赔偿' }
]

// 按照逾期天数
const overdueOptions = [
  { value: '', label: '全部' },
  { value: '1-7', label: '1-7天' },
  { value: '8-30', label: '8-30天' },
  { value: '30+', label: '30天以上' }
]

// 搜索条件
const searchForm = reactive({
  keyword: '',
  status: '',
  overdueDays: '',
  dateRange: []
})

// 加载状态
const loading = ref(false)

// 所有逾期数据
const allOverdueRecords = ref([])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 筛选后的逾期数据
const filteredRecords = computed(() => {
  let result = allOverdueRecords.value

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
  
  // 根据逾期天数筛选
  if (searchForm.overdueDays) {
    if (searchForm.overdueDays === '1-7') {
      result = result.filter(record => record.overdueDays >= 1 && record.overdueDays <= 7)
    } else if (searchForm.overdueDays === '8-30') {
      result = result.filter(record => record.overdueDays >= 8 && record.overdueDays <= 30)
    } else if (searchForm.overdueDays === '30+') {
      result = result.filter(record => record.overdueDays > 30)
    }
  }
  
  // 根据日期范围筛选
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const start = new Date(searchForm.dateRange[0])
    const end = new Date(searchForm.dateRange[1])
    end.setHours(23, 59, 59, 999) // 设置结束时间为当天最后一秒
    
    result = result.filter(record => {
      const dueDate = new Date(record.returnDate)
      return dueDate >= start && dueDate <= end
    })
  }
  
  // 更新分页信息
  pagination.total = result.length
  
  return result
})

// 统计数据
const statistics = computed(() => {
  const overdueCount = filteredRecords.value.filter(r => r.status === 'overdue').length
  const returnedCount = filteredRecords.value.filter(r => r.status === 'returned').length
  const compensatedCount = filteredRecords.value.filter(r => r.status === 'compensated').length
  
  // 计算总罚款
  const totalFine = filteredRecords.value.reduce((sum, record) => {
    return sum + record.fine
  }, 0)
  
  // 计算已收罚款
  const collectedFine = filteredRecords.value.reduce((sum, record) => {
    return sum + (record.status !== 'overdue' ? record.fine : 0)
  }, 0)
  
  return {
    overdueCount,
    returnedCount,
    compensatedCount,
    totalFine: totalFine.toFixed(2),
    collectedFine: collectedFine.toFixed(2)
  }
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
  searchForm.overdueDays = ''
  searchForm.dateRange = []
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

// 查看详情
const viewDetail = (row) => {
  console.log('查看逾期详情:', row)
  ElMessage({
    type: 'info',
    message: `正在查看借阅编号 ${row.borrowId} 的逾期详情`
  })
  // 这里应该显示详情对话框
}

// 发送提醒
const sendReminder = (row) => {
  ElMessageBox.confirm(
    `确定发送提醒给用户"${row.borrower}"，提醒归还《${row.title}》吗？`,
    '发送提醒',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 模拟发送成功，实际应该调用API
    ElMessage({
      type: 'success',
      message: `提醒消息已发送给 ${row.borrower}`
    })
  }).catch(() => {})
}

// 归还图书
const returnBook = (row) => {
  ElMessageBox.confirm(
    `确定用户"${row.borrower}"归还《${row.title}》吗？需支付逾期罚款 ${row.fine} 元。`,
    '归还确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟归还成功，实际应该调用API
    const index = allOverdueRecords.value.findIndex(record => record.borrowId === row.borrowId)
    if (index !== -1) {
      allOverdueRecords.value[index].status = 'returned'
      allOverdueRecords.value[index].actualReturnDate = formatDate(new Date())
      
      ElMessage({
        type: 'success',
        message: `图书已归还，已收取罚款 ${row.fine} 元`
      })
    }
  }).catch(() => {})
}

// 处理赔偿
const handleCompensation = (row) => {
  ElMessageBox.prompt(
    `确定为《${row.title}》办理赔偿手续吗？赔偿金额为图书定价的1.5倍，预计 ${(row.bookPrice * 1.5).toFixed(2)} 元`,
    '赔偿确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'number',
      inputValue: (row.bookPrice * 1.5).toFixed(2),
      inputPlaceholder: '请输入实际赔偿金额',
      type: 'warning'
    }
  ).then(({ value }) => {
    // 模拟赔偿成功，实际应该调用API
    const index = allOverdueRecords.value.findIndex(record => record.borrowId === row.borrowId)
    if (index !== -1) {
      allOverdueRecords.value[index].status = 'compensated'
      allOverdueRecords.value[index].actualReturnDate = formatDate(new Date())
      allOverdueRecords.value[index].fine = parseFloat(value || row.bookPrice * 1.5)
      
      ElMessage({
        type: 'success',
        message: `赔偿手续已完成，收取金额 ${value || (row.bookPrice * 1.5).toFixed(2)} 元`
      })
    }
  }).catch(() => {})
}

// 导出逾期记录
const exportRecords = () => {
  ElMessage({
    type: 'success',
    message: '逾期记录导出成功'
  })
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.overdueDays > 30) {
    return 'serious-overdue-row'
  } else if (row.overdueDays > 7) {
    return 'moderate-overdue-row'
  } else if (row.status === 'overdue') {
    return 'light-overdue-row'
  } else if (row.status === 'returned') {
    return 'returned-row'
  } else if (row.status === 'compensated') {
    return 'compensated-row'
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
    case 'overdue': return 'danger'
    case 'returned': return 'success'
    case 'compensated': return 'warning'
    default: return 'info'
  }
}

// 获取状态显示文本
const getStatusText = (status) => {
  switch (status) {
    case 'overdue': return '逾期中'
    case 'returned': return '已归还'
    case 'compensated': return '已赔偿'
    default: return status
  }
}

// 计算逾期天数严重程度
const getOverdueSeverity = (days) => {
  if (days > 30) return 'serious'
  if (days > 7) return 'moderate'
  return 'light'
}

// 模拟获取逾期记录数据
const fetchOverdueRecords = () => {
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 这里应该是调用真实的API
    // 模拟数据
    allOverdueRecords.value = [
      {
        borrowId: 'BR20230001',
        title: '活着',
        author: '余华',
        coverImage: '/src/assets/Image/book1.jpg',
        borrower: '张三',
        borrowDate: '2023-08-01',
        returnDate: '2023-09-01',
        actualReturnDate: null,
        overdueDays: 45,
        fine: 22.5, // 0.5元/天
        bookPrice: 39,
        status: 'overdue',
        contact: '13800000001'
      },
      {
        borrowId: 'BR20230002',
        title: '三体',
        author: '刘慈欣',
        coverImage: '/src/assets/Image/book2.jpg',
        borrower: '李四',
        borrowDate: '2023-09-01',
        returnDate: '2023-10-01',
        actualReturnDate: '2023-10-10',
        overdueDays: 9,
        fine: 4.5,
        bookPrice: 68,
        status: 'returned',
        contact: '13800000002'
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
        overdueDays: 31,
        fine: 15.5,
        bookPrice: 88,
        status: 'overdue',
        contact: '13800000003'
      },
      {
        borrowId: 'BR20230004',
        title: '围城',
        author: '钱钟书',
        coverImage: '/src/assets/Image/book4.jpg',
        borrower: '赵六',
        borrowDate: '2023-09-10',
        returnDate: '2023-10-10',
        actualReturnDate: null,
        overdueDays: 6,
        fine: 3,
        bookPrice: 45,
        status: 'overdue',
        contact: '13800000004'
      },
      {
        borrowId: 'BR20230005',
        title: '人间失格',
        author: '太宰治',
        coverImage: '/src/assets/Image/book1.jpg',
        borrower: '孙七',
        borrowDate: '2023-07-20',
        returnDate: '2023-08-20',
        actualReturnDate: null,
        overdueDays: 57,
        fine: 0, // 已赔偿，无罚款
        bookPrice: 32,
        status: 'compensated',
        contact: '13800000005'
      }
    ]
    
    // 更新总数
    pagination.total = allOverdueRecords.value.length
    loading.value = false
  }, 800) // 模拟加载时间
}

onMounted(() => {
  fetchOverdueRecords()
})
</script>

<template>
  <div class="overdue-management-container">
    <div class="page-header">
      <h2 class="page-title">逾期管理</h2>
      <div class="action-buttons">
        <el-button @click="exportRecords" class="export-button">
          <el-icon><Download /></el-icon>导出记录
        </el-button>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card overdue-card">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.overdueCount }}</div>
          <div class="stat-label">逾期中</div>
        </div>
      </div>
      
      <div class="stat-card returned-card">
        <div class="stat-icon">
          <el-icon><Bell /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.returnedCount + statistics.compensatedCount }}</div>
          <div class="stat-label">已处理</div>
        </div>
      </div>
      
      <div class="stat-card fine-card">
        <div class="stat-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ statistics.totalFine }}</div>
          <div class="stat-label">罚款总额</div>
        </div>
      </div>
      
      <div class="stat-card collected-card">
        <div class="stat-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ statistics.collectedFine }}</div>
          <div class="stat-label">已收金额</div>
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
        
        <el-form-item label="逾期状态">
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
        
        <el-form-item label="逾期天数">
          <el-select 
            v-model="searchForm.overdueDays" 
            placeholder="选择逾期天数" 
            clearable 
            class="custom-select"
          >
            <el-option 
              v-for="item in overdueOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="应还日期">
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
      共找到 <span class="highlight">{{ pagination.total }}</span> 条逾期记录
    </div>
    
    <!-- 逾期记录表格 -->
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
        
        <el-table-column prop="borrower" label="借阅人" width="100" />
        
        <el-table-column label="应还日期" width="120">
          <template #default="scope">
            <span class="return-date">{{ scope.row.returnDate }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="实际归还" width="120">
          <template #default="scope">
            {{ scope.row.actualReturnDate || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="逾期天数" width="100" align="center">
          <template #default="scope">
            <span :class="`overdue-days ${getOverdueSeverity(scope.row.overdueDays)}`">
              {{ scope.row.overdueDays }} 天
            </span>
          </template>
        </el-table-column>
        
        <el-table-column label="罚款金额" width="100" align="center">
          <template #default="scope">
            <span class="fine-amount">¥{{ scope.row.fine.toFixed(2) }}</span>
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
                  @click="viewDetail(scope.row)"
                  class="action-button view-button"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              
              <template v-if="scope.row.status === 'overdue'">
                <el-tooltip content="发送提醒" placement="top" effect="light">
                  <el-button 
                    type="info" 
                    size="small" 
                    circle
                    @click="sendReminder(scope.row)"
                    class="action-button remind-button"
                  >
                    <el-icon><Message /></el-icon>
                  </el-button>
                </el-tooltip>
                
                <el-tooltip content="归还处理" placement="top" effect="light">
                  <el-button 
                    type="success" 
                    size="small" 
                    circle
                    @click="returnBook(scope.row)"
                    class="action-button return-button"
                  >
                    <el-icon><Bell /></el-icon>
                  </el-button>
                </el-tooltip>
                
                <el-tooltip content="赔偿处理" placement="top" effect="light">
                  <el-button 
                    type="warning" 
                    size="small" 
                    circle
                    @click="handleCompensation(scope.row)"
                    class="action-button compensation-button"
                  >
                    <el-icon><Money /></el-icon>
                  </el-button>
                </el-tooltip>
              </template>
              
              <template v-else>
                <span class="processed-info">
                  已于 {{ scope.row.actualReturnDate }} 处理
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
      v-if="filteredRecords.length === 0 && !loading" 
      description="暂无逾期记录" 
      class="empty-data"
    />
  </div>
</template>

<style scoped lang="scss">
.overdue-management-container {
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
      
      &.returned-card {
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
      
      &.fine-card, &.collected-card {
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
          font-size: 22px;
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
      
      .return-date {
        color: #f56c6c;
        font-weight: 500;
      }
      
      .overdue-days {
        font-weight: 500;
        
        &.light {
          color: #e6a23c;
        }
        
        &.moderate {
          color: #f56c6c;
          font-weight: 600;
        }
        
        &.serious {
          color: #f56c6c;
          font-weight: 700;
        }
      }
      
      .fine-amount {
        font-weight: 600;
        color: #e6a23c;
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
        gap: 8px;
        
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
          
          &.remind-button {
            background-color: #909399;
          }
          
          &.return-button {
            background-color: #67c23a;
          }
          
          &.compensation-button {
            background-color: #e6a23c;
          }
        }
      }
    }
    
    .light-overdue-row {
      background-color: rgba(230, 162, 60, 0.05);
    }
    
    .moderate-overdue-row {
      background-color: rgba(245, 108, 108, 0.1);
    }
    
    .serious-overdue-row {
      background-color: rgba(245, 108, 108, 0.2);
    }
    
    .returned-row {
      background-color: rgba(103, 194, 58, 0.05);
    }
    
    .compensated-row {
      background-color: rgba(230, 162, 60, 0.05);
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
  .overdue-management-container {
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