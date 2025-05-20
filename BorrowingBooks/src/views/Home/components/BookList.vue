<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search, Edit, Delete, View, Download, List, Grid } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {BookListAPI} from "@/api/Book.js";

// 处理图片加载错误
const handleImageError = (e) => {
  console.error('图片加载失败:', e);
  // 设置默认图片
  e.target.src = '/src/assets/default-book.jpg'; // 请确保这个路径是正确的
}

// 添加视图模式变量
const viewMode = ref('table')

// 所有图书数据 - 从后端获取的完整数据
const allBooks = ref([])

// 表格显示的数据 - 经过筛选的数据
const tableData = computed(() => {
  // 根据当前页码和每页显示数量计算显示的数据
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredBooks.value.slice(start, end)
})

// 筛选后的图书数据
const filteredBooks = computed(() => {
  let result = allBooks.value

  // 根据关键词筛选
  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(book => 
      book.title.toLowerCase().includes(keyword) || 
      book.author.toLowerCase().includes(keyword) || 
      book.isbn.toLowerCase().includes(keyword)
    )
  }
  
  // 根据分类筛选 - 修改为使用categoryId字段
  if (searchForm.category) {
    result = result.filter(book => String(book.categoryId) === searchForm.category)
  }
  
  // 根据状态筛选 - 修改为使用status字段
  if (searchForm.status) {
    if (searchForm.status === '可借阅') {
      result = result.filter(book => book.status === true)
    } else if (searchForm.status === '已借出') {
      result = result.filter(book => book.status === false)
    }
  }
  
  // 更新分页信息
  pagination.total = result.length
  
  return result
})

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
  pageSize: 4,  // 从10改为4
  total: 0
})

// 分类选项 - 根据第二张图修改
const categoryOptions = [
  { value: '', label: '全部分类' },
  { value: '1', label: '文学' },
  { value: '2', label: '小说' },
  { value: '3', label: '散文' },
  { value: '4', label: '科技' },
  { value: '5', label: '计算机' },
  { value: '6', label: '物理学' }
];

// 状态选项
const statusOptions = [
  { value: '', label: '全部状态' },
  { value: '可借阅', label: '可借阅' },
  { value: '已借出', label: '已借出' }
]

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (!row.inStock) {
    return 'borrowed-row'
  }
  return ''
}

// 处理搜索 - 前端筛选
const handleSearch = () => {
  // 重置到第一页
  pagination.currentPage = 1
  // 不需要额外操作，computed属性会自动更新
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.status = ''
  pagination.currentPage = 1
  // 不需要额外操作，computed属性会自动更新
}

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  // 不需要额外操作，computed属性会自动更新
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  // 不需要额外操作，computed属性会自动更新
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
    // 删除成功后，从本地数据中移除
    const index = allBooks.value.findIndex(book => book.id === row.id)
    if (index !== -1) {
      allBooks.value.splice(index, 1)
      ElMessage({
        type: 'success',
        message: '删除成功'
      })
    }
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

// 获取分类名称
const getCategoryName = (categoryId) => {
  // 确保categoryId是字符串类型，因为我们的选项值是字符串
  const categoryIdStr = String(categoryId);
  const category = categoryOptions.find(item => item.value === categoryIdStr);
  return category ? category.label : '未知分类';
}

// 获取所有图书数据
const fetchAllBooks = async () => {
  loading.value = true
  try {
    const result = await BookListAPI(pagination);
    console.log('API返回数据:', result);
    
    // 假设后端返回的数据格式为 { data: [...] }
    if (result && result.data) {
      // 处理后端返回的图书数据，添加必要的字段
      allBooks.value = result.data.map(book => {
        // 处理图片URL，确保没有多余的引号
        let coverUrl = book.coverImage;
        if (coverUrl) {
          // 移除可能存在的多余引号
          coverUrl = coverUrl.replace(/^["'\s]+|["'\s]+$/g, '');
          
          // 由于豆瓣图片有防盗链，直接使用本地默认图片
          coverUrl = '/src/assets/default-book.jpg'; // 请确保这个路径是正确的
        }
        
        return {
          ...book,
          // 使用处理后的图片URL
          coverImage: '/src/assets/Image/图书封面.png',
          // 使用status字段作为inStock
          inStock: book.status === true,
          // 根据status字段设置可借状态
          availableCopies: book.status === true ? 1 : 0,
          totalCopies: 1,
          // 获取分类名称
          categoryName: getCategoryName(book.categoryId)
        };
      });
      
      console.log('处理后的图书数据:', allBooks.value);
      
      // 更新分页信息
      pagination.total = result.total || allBooks.value.length;
    } else {
      // 如果没有数据，设置为空数组
      allBooks.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取图书列表失败:', error);
    ElMessage.error('获取图书列表失败，请稍后重试');
    allBooks.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  // 组件加载时获取所有图书数据
  fetchAllBooks()
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
        <!-- 表格视图中的封面图片 -->
        <el-table-column label="封面" width="80" align="center">
          <template #default="scope">
            <el-image 
              :src="scope.row.coverImage" 
              :preview-src-list="[scope.row.coverImage]"
              fit="cover"
              style="width: 50px; height: 70px; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"
              @error="handleImageError"
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
        <el-table-column label="分类" width="120">
          <template #default="scope">
            <el-tag size="small" effect="plain" class="category-tag">{{ scope.row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isbn" label="ISBN" min-width="140" show-overflow-tooltip />
        <el-table-column label="出版日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.publishDate) }}
          </template>
        </el-table-column>
        <el-table-column label="馆藏" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.inStock ? 'success' : 'danger'"
              size="small"
              effect="dark"
            >
              {{ scope.row.inStock ? '可借阅' : '已借出' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 添加馆藏位置列 -->
        <el-table-column prop="location" label="馆藏位置" width="100" align="center">
          <template #default="scope">
            <el-tag size="small" effect="plain" class="location-tag">
              {{ scope.row.location }}
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
        :page-sizes="[4, 8, 12, 16]"
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
                :src="book.coverImage" 
                fit="cover"
              />
              <div class="book-card-status">
                <el-tag 
                  :type="book.inStock ? 'success' : 'danger'"
                  size="small"
                  effect="dark"
                >
                  {{ book.inStock ? '可借阅' : '已借出' }}
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
                <el-tag size="small" effect="plain" class="category-tag">{{ book.categoryName }}</el-tag>
                <el-tag 
                  :type="book.inStock ? 'success' : 'danger'"
                  size="small"
                  effect="dark"
                >
                  {{ book.inStock ? '可借阅' : '已借出' }}
                </el-tag>
              </div>
              <!-- 添加馆藏位置显示 -->
              <div class="book-card-location">
                <span class="location-label">馆藏位置:</span>
                <el-tag size="small" effect="plain" class="location-tag">{{ book.location }}</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 卡片视图的分页 -->
      <div class="pagination-container">
        <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[4, 8, 12, 16]"
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
      v-if="filteredBooks.length === 0 && !loading" 
      description="暂无图书数据" 
      class="empty-data"
    >
      <el-button type="primary" @click="$router.push('/home/add-book')">添加图书</el-button>
    </el-empty>
    
    <!-- 添加占位元素确保滚动 -->
    <div class="scroll-spacer"></div>
  </div>
</template>

<style scoped lang="scss">
.book-list-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  
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
    height: auto;
    overflow: visible;
    
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
          transition: all 0.5s;
          
          &:hover {
            transform: scale(1.05);
          }
        }
        
        .book-card-status {
          position: absolute;
          top: 10px;
          right: 10px;
          z-index: 2;
        }
        
        .book-card-actions {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: rgba(0, 0, 0, 0.6);
          padding: 10px;
          display: flex;
          justify-content: center;
          opacity: 0;
          transition: all 0.3s;
          z-index: 2;
        }
      }
      
      .book-card-info {
        padding: 15px;
        
        .book-card-title {
          font-size: 16px;
          font-weight: 500;
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
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        
        .book-card-meta {
          display: flex;
          justify-content: space-between;
          margin-bottom: 10px;
        }
        
        .book-card-location {
          display: flex;
          align-items: center;
          font-size: 13px;
          
          .location-label {
            color: #909399;
            margin-right: 5px;
          }
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    margin-bottom: 20px;
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

// 添加处理图片错误的函数
:deep(.el-image__error) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #909399;
  background-color: #f5f7fa;
}

// 确保主容器可以滚动

// 确保表格容器不限制高度
.table-card {
  height: auto !important;
  overflow: visible !important;
  
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
        transition: all 0.5s;
        
        &:hover {
          transform: scale(1.05);
        }
      }
      
      .book-card-status {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 2;
      }
      
      .book-card-actions {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: rgba(0, 0, 0, 0.6);
        padding: 10px;
        display: flex;
        justify-content: center;
        opacity: 0;
        transition: all 0.3s;
        z-index: 2;
      }
    }
    
    .book-card-info {
      padding: 15px;
      
      .book-card-title {
        font-size: 16px;
        font-weight: 500;
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
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .book-card-meta {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
      }
      
      .book-card-location {
        display: flex;
        align-items: center;
        font-size: 13px;
        
        .location-label {
          color: #909399;
          margin-right: 5px;
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    margin-bottom: 20px;
  }
  
  .empty-data {
    margin: 40px 0;
  }
}

// 确保分页容器正常显示
.pagination-container {
  margin-top: 20px;
  margin-bottom: 20px;
  

}

// 添加一个占位元素，确保有足够的内容触发滚动
.scroll-spacer {
  height: 50px;
  width: 100%;
}
</style>