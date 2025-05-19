<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Search, Edit, Delete, View, Download, List, Grid } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 添加视图模式变量
const viewMode = ref('table')

// 图书数据
const tableData = ref([
  {
    id: 1,
    title: '活着',
    author: '余华',
    publisher: '作家出版社',
    isbn: '9787506365437',
    category: '文学小说',
    publishDate: '2012-08-01',
    totalCopies: 10,
    availableCopies: 5,
    location: 'A-12-05',
    cover: 'book1.jpg',
    status: '可借阅'
  },
  {
    id: 2,
    title: '三体',
    author: '刘慈欣',
    publisher: '重庆出版社',
    isbn: '9787536692930',
    category: '科幻小说',
    publishDate: '2008-01-01',
    totalCopies: 8,
    availableCopies: 3,
    location: 'B-05-12',
    cover: 'book2.jpg',
    status: '可借阅'
  },
  {
    id: 3,
    title: '平凡的世界',
    author: '路遥',
    publisher: '北京十月文艺出版社',
    isbn: '9787530216781',
    category: '文学小说',
    publishDate: '2017-06-01',
    totalCopies: 12,
    availableCopies: 7,
    location: 'A-08-15',
    cover: 'book3.jpg',
    status: '可借阅'
  },
  {
    id: 4,
    title: '围城',
    author: '钱钟书',
    publisher: '人民文学出版社',
    isbn: '9787020090006',
    category: '文学小说',
    publishDate: '2006-05-01',
    totalCopies: 6,
    availableCopies: 2,
    location: 'A-10-08',
    cover: 'book4.jpg',
    status: '已借出'
  }
])

// 加载状态
const loading = ref(false)

// 搜索条件
const searchForm = reactive({
  keyword: '',
  category: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 4
})

// 分类选项
const categoryOptions = [
  { value: '', label: '全部分类' },
  { value: '文学小说', label: '文学小说' },
  { value: '科幻小说', label: '科幻小说' },
  { value: '历史传记', label: '历史传记' },
  { value: '经济管理', label: '经济管理' },
  { value: '科学技术', label: '科学技术' }
]

// 状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: '可借阅', label: '可借阅' },
  { value: '已借出', label: '已借出' },
  { value: '维护中', label: '维护中' }
]

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.status === '已借出') {
    return 'borrowed-row'
  } else if (row.status === '维护中') {
    return 'maintenance-row'
  }
  return ''
}

// 处理搜索
const handleSearch = () => {
  console.log('搜索条件:', searchForm)
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 根据搜索条件过滤
    let filteredData = [
      {
        id: 1,
        title: '活着',
        author: '余华',
        publisher: '作家出版社',
        isbn: '9787506365437',
        category: '文学小说',
        publishDate: '2012-08-01',
        totalCopies: 10,
        availableCopies: 5,
        location: 'A-12-05',
        cover: 'book1.jpg',
        status: '可借阅'
      },
      {
        id: 2,
        title: '三体',
        author: '刘慈欣',
        publisher: '重庆出版社',
        isbn: '9787536692930',
        category: '科幻小说',
        publishDate: '2008-01-01',
        totalCopies: 8,
        availableCopies: 3,
        location: 'B-05-12',
        cover: 'book2.jpg',
        status: '可借阅'
      },
      {
        id: 3,
        title: '平凡的世界',
        author: '路遥',
        publisher: '北京十月文艺出版社',
        isbn: '9787530216781',
        category: '文学小说',
        publishDate: '2017-06-01',
        totalCopies: 12,
        availableCopies: 7,
        location: 'A-08-15',
        cover: 'book3.jpg',
        status: '可借阅'
      },
      {
        id: 4,
        title: '围城',
        author: '钱钟书',
        publisher: '人民文学出版社',
        isbn: '9787020090006',
        category: '文学小说',
        publishDate: '2006-05-01',
        totalCopies: 6,
        availableCopies: 2,
        location: 'A-10-08',
        cover: 'book4.jpg',
        status: '已借出'
      }
    ]
    
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase()
      filteredData = filteredData.filter(book => 
        book.title.toLowerCase().includes(keyword) || 
        book.author.toLowerCase().includes(keyword) || 
        book.isbn.toLowerCase().includes(keyword)
      )
    }
    
    if (searchForm.category) {
      filteredData = filteredData.filter(book => book.category === searchForm.category)
    }
    
    if (searchForm.status) {
      filteredData = filteredData.filter(book => book.status === searchForm.status)
    }
    
    // 更新分页信息
    pagination.total = filteredData.length
    
    // 模拟分页
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = filteredData.slice(start, end)
    
    loading.value = false
  }, 500)
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.status = ''
  handleSearch()
}

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  handleSearch()
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  handleSearch()
}

// 查看图书详情
const viewBookDetail = (row) => {
  console.log('查看图书详情:', row)
  ElMessage({
    type: 'info',
    message: `正在查看《${row.title}》的详情`
  })
  // 跳转到详情页或打开详情对话框
}

// 编辑图书
const editBook = (row) => {
  console.log('编辑图书:', row)
  ElMessage({
    type: 'warning',
    message: `正在编辑《${row.title}》`
  })
  // 跳转到编辑页或打开编辑对话框
}

// 删除图书
const deleteBook = (row) => {
  ElMessageBox.confirm(
    `确定要删除《${row.title}》吗？此操作不可逆。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用API删除图书
    ElMessage({
      type: 'success',
      message: '删除成功'
    })
  }).catch(() => {
    // 取消删除
  })
}

// 导出图书数据
const exportBooks = () => {
  console.log('导出图书数据')
  // 这里应该调用API导出数据
  ElMessage({
    type: 'success',
    message: '导出成功'
  })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

onMounted(() => {
  // 这里应该调用API获取图书数据
  console.log('图书列表页面已加载')
  handleSearch()
})
</script>

<template>
  <div class="book-list-container">
    <div class="page-header">
      <h2 class="page-title">图书列表</h2>
      <div class="action-buttons">
        <el-button @click="exportBooks" class="export-button">
          <el-icon><Download /></el-icon>导出列表
        </el-button>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="书名/作者/ISBN" 
            clearable
            @keyup.enter="handleSearch"
            prefix-icon="Search"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="选择分类" clearable>
            <el-option 
              v-for="item in categoryOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option 
              v-for="item in statusOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-button">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch" class="reset-button">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 切换视图按钮 -->
    <div class="view-toggle">
      <el-radio-group v-model="viewMode" size="large">
        <el-radio-button label="table">
          <el-icon><List /></el-icon>表格视图
        </el-radio-button>
        <el-radio-button label="card">
          <el-icon><Grid /></el-icon>卡片视图
        </el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 表格视图 -->
    <el-card v-if="viewMode === 'table'" class="table-card">
      <el-table 
        :data="tableData" 
        style="width: 100%" 
        border 
        v-loading="loading"
        row-key="id"
        :header-cell-style="{backgroundColor: '#f5f7fa', color: '#183550', fontWeight: 'bold'}"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="封面" width="80" align="center">
          <template #default="scope">
            <el-image 
              :src="`@/assets/Image/${scope.row.cover}`" 
              :preview-src-list="[`@/assets/Image/${scope.row.cover}`]"
              fit="cover"
              style="width: 50px; height: 70px; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <div class="book-title-cell">
              <span class="book-title-text">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="scope">
            <el-tag size="small" effect="plain" class="category-tag">{{ scope.row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isbn" label="ISBN" min-width="140" show-overflow-tooltip />
        <el-table-column label="出版日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.publishDate) }}
          </template>
        </el-table-column>
        <el-table-column label="馆藏/可借" width="100" align="center">
          <template #default="scope">
            <span :class="{'text-warning': scope.row.availableCopies === 0, 'text-success': scope.row.availableCopies > 0}">
              {{ scope.row.availableCopies }}/{{ scope.row.totalCopies }}
            </span>
          </template>
        </el-table-column>
        <!-- 移除了馆藏位置列 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.status === '可借阅' ? 'success' : 
                    scope.row.status === '已借出' ? 'danger' : 'info'"
              size="small"
              effect="dark"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top" effect="light">
                <el-button 
                  type="primary" 
                  size="small" 
                  circle
                  @click="viewBookDetail(scope.row)"
                  class="action-button view-button"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="编辑" placement="top" effect="light">
                <el-button 
                  type="warning" 
                  size="small" 
                  circle
                  @click="editBook(scope.row)"
                  class="action-button edit-button"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" effect="light">
                <el-button 
                  type="danger" 
                  size="small" 
                  circle
                  @click="deleteBook(scope.row)"
                  class="action-button delete-button"
                >
                  <el-icon><Delete /></el-icon>
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
    
    <!-- 卡片视图 -->
    <div v-else class="card-view">
      <el-row :gutter="24">
        <el-col 
          v-for="book in tableData" 
          :key="book.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6" 
          :xl="4"
          class="book-card-col"
        >
          <el-card class="book-card" shadow="hover">
            <div class="book-card-cover">
              <el-image 
                :src="`@/assets/Image/${book.cover}`" 
                fit="cover"
              />
              <div class="book-card-status">
                <el-tag 
                  :type="book.status === '可借阅' ? 'success' : 
                        book.status === '已借出' ? 'danger' : 'info'"
                  size="small"
                  effect="dark"
                >
                  {{ book.status }}
                </el-tag>
              </div>
              <div class="book-card-actions">
                <el-button-group>
                  <el-button type="primary" size="small" @click="viewBookDetail(book)">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-button type="warning" size="small" @click="editBook(book)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="danger" size="small" @click="deleteBook(book)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-button-group>
              </div>
            </div>
            <div class="book-card-info">
              <h3 class="book-card-title" :title="book.title">{{ book.title }}</h3>
              <p class="book-card-author">{{ book.author }}</p>
              <div class="book-card-meta">
                <el-tag size="small" effect="plain" class="category-tag">{{ book.category }}</el-tag>
                <span class="book-card-copies">
                  <i class="el-icon-reading"></i>
                  {{ book.availableCopies }}/{{ book.totalCopies }}
                </span>
              </div>
              <!-- 移除了卡片中的馆藏位置信息 -->
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 卡片视图的分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[12, 24, 48, 96]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <el-empty 
      v-if="tableData.length === 0 && !loading" 
      description="暂无图书数据" 
      class="empty-data"
    >
      <el-button type="primary" @click="$router.push('/home/add-book')">添加图书</el-button>
    </el-empty>
  </div>
</template>

<style scoped lang="scss">
.book-list-container {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .page-title {
      font-size: 24px;
      color: #183550;
      margin: 0;
      position: relative;
      padding-left: 16px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: linear-gradient(to bottom, #183550, #68b8d7);
        border-radius: 2px;
      }
    }
    
    .action-buttons {
      display: flex;
      gap: 12px;
      
      .add-button {
        background: linear-gradient(135deg, #183550, #68b8d7);
        border: none;
        
        &:hover {
          background: linear-gradient(135deg, #1a3a55, #7ac5e4);
        }
      }
      
      .export-button {
        border: 1px solid #dcdfe6;
        
        &:hover {
          border-color: #c6e2ff;
          color: #409EFF;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      
      .el-form-item {
        margin-bottom: 10px;
        margin-right: 0;
      }
      
      .search-button {
        background: #183550;
        border: none;
        
        &:hover {
          background: #1a3a55;
        }
      }
      
      .reset-button {
        border: 1px solid #dcdfe6;
        
        &:hover {
          border-color: #c6e2ff;
          color: #409EFF;
        }
      }
    }
  }
  
  .view-toggle {
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
    
    .el-radio-group {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      border-radius: 4px;
    }
  }
  
  .table-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
    
    .el-table {
      border-radius: 8px;
      overflow: hidden;
      
      .book-title-cell {
        .book-title-text {
          font-weight: 500;
          color: #183550;
        }
      }
      
      .category-tag {
        border-radius: 12px;
      }
      
      .location-tag {
        border-radius: 4px;
      }
      
      .text-warning {
        color: #E6A23C;
        font-weight: bold;
      }
      
      .text-success {
        color: #67C23A;
      }
      
      .table-actions {
        display: flex;
        justify-content: space-around;
        
        .action-button {
          transition: all 0.3s;
          
          &:hover {
            transform: translateY(-2px);
          }
        }
      }
    }
    
    .borrowed-row {
      background-color: rgba(245, 108, 108, 0.05);
    }
    
    .maintenance-row {
      background-color: rgba(144, 147, 153, 0.05);
    }
  }
  
  .card-view {
    margin-bottom: 20px;
    
    .book-card-col {
      margin-bottom: 24px;
    }
    
    .book-card {
      height: 100%;
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s;
      border: none;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
        
        .book-card-actions {
          opacity: 1;
        }
      }
      
      .book-card-cover {
        position: relative;
        height: 220px;
        overflow: hidden;
        
        .el-image {
          width: 100%;
          height: 100%;
          transition: transform 0.5s;
          
          &:hover {
            transform: scale(1.05);
          }
        }
        
        .book-card-status {
          position: absolute;
          top: 10px;
          right: 10px;
          z-index: 1;
        }
        
        .book-card-actions {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: rgba(24, 53, 80, 0.8);
          padding: 10px;
          display: flex;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s;
        }
      }
      
      .book-card-info {
        padding: 16px;
        
        .book-card-title {
          font-size: 16px;
          font-weight: 600;
          color: #183550;
          margin: 0 0 8px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .book-card-author {
          font-size: 14px;
          color: #606266;
          margin: 0 0 12px;
        }
        
        .book-card-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .category-tag {
            border-radius: 12px;
          }
          
          .book-card-copies {
            font-size: 12px;
            color: #909399;
          }
        }
        
        .book-card-location {
          font-size: 12px;
          color: #909399;
          
          .location-label {
            margin-right: 4px;
          }
          
          .location-value {
            color: #606266;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  
  .empty-data {
    margin: 40px 0;
  }
}

@media (max-width: 768px) {
  .book-list-container {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      
      .action-buttons {
        margin-top: 16px;
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