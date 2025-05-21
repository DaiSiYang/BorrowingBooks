<template>
  <div class="book-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button @click="goBack" :icon="Back" round>返回</el-button>
    </div>
    
    <!-- 图书详情内容 -->
    <div v-if="!loading" class="book-detail-content">
      <!-- 头部区域 -->
      <div class="book-detail-header">
        <div class="book-cover">
          <el-image 
            :src="bookDetail.coverImage || '/src/assets/Image/图书封面.png'" 
            fit="cover"
            @error="handleImageError"
          />
          <div class="book-status-tag">
            <el-tag 
              :type="bookDetail.statusType"
              effect="dark"
              size="large"
            >
              {{ bookDetail.statusText }}
            </el-tag>
          </div>
        </div>
        
        <div class="book-info">
          <h1 class="book-title">{{ bookDetail.title }}</h1>
          
          <div class="book-meta">
            <div class="meta-item">
              <span class="label">作者：</span>
              <span class="value">{{ bookDetail.author }}</span>
            </div>
            <div class="meta-item">
              <span class="label">出版社：</span>
              <span class="value">{{ bookDetail.publisher || '未知' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">ISBN：</span>
              <span class="value">{{ bookDetail.isbn }}</span>
            </div>
            <div class="meta-item">
              <span class="label">出版日期：</span>
              <span class="value">{{ bookDetail.formattedPublishDate }}</span>
            </div>
            <div class="meta-item">
              <span class="label">分类：</span>
              <el-tag size="small" effect="plain">{{ bookDetail.categoryName }}</el-tag>
            </div>
            <div class="meta-item">
              <span class="label">库存：</span>
              <span class="value stock-value">{{ bookDetail.stock || 0 }}</span>
            </div>
          </div>
          
          <div class="book-actions">
            <el-button 
              type="primary" 
              :icon="Reading" 
              size="large" 
              :disabled="bookDetail.status !== 1 || bookDetail.stock <= 0 || bookDetail.isBorrowed"
              @click="borrowBook"
            >
              {{ bookDetail.isBorrowed ? '已借阅' : '借阅此书' }}
            </el-button>
            <el-button 
              type="warning" 
              :icon="Star" 
              size="large"
              @click="collectBook"
            >
              加入收藏
            </el-button>
            <el-button 
              type="info" 
              :icon="Share" 
              size="large"
            >
              分享
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 图书详情区域 -->
      <div class="book-sections">
        <!-- 图书简介 -->
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>图书简介</span>
            </div>
          </template>
          <div class="card-content description-content">
            <p>{{ bookDetail.description || bookDetail.desc || '暂无简介' }}</p>
          </div>
        </el-card>
        
        <!-- 图书信息 -->
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>图书信息</span>
            </div>
          </template>
          <div class="card-content">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="页数">{{ bookDetail.pages || '未知' }}</el-descriptions-item>
              <el-descriptions-item label="装帧">{{ bookDetail.binding || '未知' }}</el-descriptions-item>
              <el-descriptions-item label="开本">{{ bookDetail.format || '未知' }}</el-descriptions-item>
              <el-descriptions-item label="语种">{{ bookDetail.language || '中文' }}</el-descriptions-item>
              <el-descriptions-item label="印次">{{ bookDetail.impression || '1' }}</el-descriptions-item>
              <el-descriptions-item label="版次">{{ bookDetail.edition || '1' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
        
        <!-- 借阅信息 -->
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>借阅信息</span>
            </div>
          </template>
          <div class="card-content">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="馆藏状态">
                <el-tag :type="bookDetail.statusType">{{ bookDetail.statusText }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="馆藏位置">
                {{ bookDetail.location || 'A-103' }}
              </el-descriptions-item>
              <el-descriptions-item label="借阅次数">
                {{ bookDetail.borrowCount || 0 }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 加载中 -->
    <div v-else class="loading-container">
      <el-skeleton style="width: 100%" animated :rows="10" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { BookDetailAPI, BorrowBookAPI } from '@/api/Book.js'
import { ElMessage, ElLoading } from 'element-plus'
import { Reading, Star, Back, Share } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const bookDetail = ref({})
const loading = ref(true)

// 添加状态标记，是否已借阅
const isBorrowed = ref(false)

// 获取状态信息
const getStatusInfo = (status) => {
  const statusMap = {
    1: { text: '可借阅', type: 'success' },
    0: { text: '已借出', type: 'info' },
    2: { text: '已损坏', type: 'warning' },
    3: { text: '已下架', type: 'danger' }
  }
  return statusMap[status] || { text: '未知', type: 'info' }
}

// 获取分类名称 (简单模拟，实际可从BookCategoryAPI获取)
const getCategoryName = (categoryId) => {
  const categoryMap = {
    1: '文学',
    2: '小说',
    3: '散文',
    4: '科技',
    5: '计算机',
    6: '物理学'
  }
  return categoryMap[categoryId] || '未知分类'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 处理图片加载错误
const handleImageError = (e) => {
  e.target.src = '/src/assets/Image/图书封面.png'
}

// 获取图书详情
const fetchBookDetail = async () => {
  const loadingInstance = ElLoading.service({
    text: '正在加载图书详情...',
    background: 'rgba(255, 255, 255, 0.7)'
  })
  
  try {
    loading.value = true
    const bookId = route.params.id
    
    if (!bookId) {
      ElMessage.error('图书ID不能为空')
      router.push('/home')
      return
    }
    
    const { data } = await BookDetailAPI(bookId)
    
    if (data) {
      // 处理分类名称和状态
      const statusInfo = getStatusInfo(data.status)
      
      bookDetail.value = {
        ...data,
        categoryName: getCategoryName(data.categoryId),
        statusText: statusInfo.text,
        statusType: statusInfo.type,
        formattedPublishDate: formatDate(data.publishDate),
        isBorrowed: data.isBorrowed || false // 添加已借阅标记
      }
      
      // 更新借阅状态
      isBorrowed.value = bookDetail.value.isBorrowed
    } else {
      ElMessage.warning('未找到图书信息')
      router.push('/home')
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
    ElMessage.error('获取图书详情失败，请稍后再试')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 借阅图书
const borrowBook = async () => {
  try {
    const loadingInstance = ElLoading.service({
      text: '正在处理借阅请求...',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    
    const response = await BorrowBookAPI(bookDetail.value.id)
    
    loadingInstance.close()
    
    // 调整判断逻辑，只要请求成功就认为借阅成功
    ElMessage.success(`《${bookDetail.value.title}》借阅成功！`)
      
    // 更新图书状态和库存
    bookDetail.value.stock -= 1
    if (bookDetail.value.stock <= 0) {
      const statusInfo = getStatusInfo(0) // 更新为已借出状态
      bookDetail.value.status = 0
      bookDetail.value.statusText = statusInfo.text
      bookDetail.value.statusType = statusInfo.type
    }
    
    // 标记为已借阅
    bookDetail.value.isBorrowed = true
    isBorrowed.value = true
  } catch (error) {
    console.error('借阅图书失败:', error)
    ElMessage.error('借阅失败，请稍后再试')
  }
}

// 收藏图书
const collectBook = () => {
  ElMessage.success(`《${bookDetail.value.title}》已加入收藏`)
  // 这里可以添加收藏API调用
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped lang="scss">
.book-detail-container {
  padding: 24px;
  background-color: #fff;
  min-height: 100%;
  
  .back-button {
    margin-bottom: 24px;
    
    .el-button {
      border-color: #8a5f41;
      color: #8a5f41;
      background: rgba(138, 95, 65, 0.05);
      
      &:hover {
        background: rgba(138, 95, 65, 0.1);
        color: #6e4c34;
      }
    }
  }
  
  .book-detail-content {
    .book-detail-header {
      display: flex;
      gap: 40px;
      margin-bottom: 40px;
      
      .book-cover {
        width: 300px;
        height: 420px;
        flex-shrink: 0;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 20px 30px rgba(0, 0, 0, 0.15);
        position: relative;
        
        .el-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .book-status-tag {
          position: absolute;
          top: 20px;
          right: 0;
          transform: translateX(10px);
          
          .el-tag {
            padding: 0 15px;
            height: 32px;
            line-height: 32px;
            font-weight: bold;
            border-radius: 4px 0 0 4px;
            box-shadow: -4px 4px 10px rgba(0, 0, 0, 0.1);
          }
        }
      }
      
      .book-info {
        flex-grow: 1;
        
        .book-title {
          font-size: 32px;
          color: #3d2c29;
          margin: 0 0 30px 0;
          line-height: 1.3;
          position: relative;
          padding-bottom: 15px;
          
          &::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 80px;
            height: 3px;
            background: linear-gradient(to right, #8a5f41, transparent);
          }
        }
        
        .book-meta {
          margin-bottom: 30px;
          
          .meta-item {
            margin-bottom: 15px;
            font-size: 16px;
            
            .label {
              color: #8a5f41;
              font-weight: 600;
              margin-right: 10px;
              display: inline-block;
              width: 80px;
            }
            
            .value {
              color: #3d2c29;
            }
            
            .stock-value {
              font-weight: bold;
              color: #e36049;
            }
            
            .el-tag {
              background-color: rgba(138, 95, 65, 0.1);
              color: #8a5f41;
              border: none;
            }
          }
        }
        
        .book-actions {
          display: flex;
          gap: 15px;
          
          .el-button {
            padding: 12px 24px;
            border-radius: 8px;
            
            &.el-button--primary {
              background-color: #8a5f41;
              border-color: #8a5f41;
              
              &:hover:not([disabled]) {
                background-color: #6e4c34;
                border-color: #6e4c34;
              }
              
              &[disabled] {
                background-color: #f9f6f2;
                border-color: #e4e7ed;
                color: #c0c4cc;
              }
            }
            
            &.el-button--warning {
              background-color: #e89b0c;
              border-color: #e89b0c;
              
              &:hover {
                background-color: #d18c0c;
                border-color: #d18c0c;
              }
            }
            
            &.el-button--info {
              background-color: #4c8dae;
              border-color: #4c8dae;
              
              &:hover {
                background-color: #3a7a9b;
                border-color: #3a7a9b;
              }
            }
          }
        }
      }
    }
    
    .book-sections {
      display: grid;
      grid-template-columns: 1fr;
      gap: 30px;
      
      .section-card {
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
        border: none;
        
        .card-header {
          background-color: #f9f6f2;
          color: #3d2c29;
          font-size: 18px;
          font-weight: 600;
          padding: 15px 20px;
          position: relative;
          border-bottom: 1px solid rgba(138, 95, 65, 0.1);
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 4px;
            height: 20px;
            background-color: #8a5f41;
          }
          
          span {
            margin-left: 10px;
          }
        }
        
        .card-content {
          padding: 20px;
          
          &.description-content {
            font-size: 16px;
            color: #333;
            line-height: 1.8;
            text-align: justify;
          }
          
          .el-descriptions {
            .el-descriptions__body {
              .el-descriptions__label {
                color: #8a5f41;
                font-weight: 600;
              }
              
              .el-descriptions__content {
                color: #3d2c29;
              }
            }
          }
        }
      }
    }
  }
  
  .loading-container {
    padding: 30px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  }
}

// 响应式调整
@media (max-width: 992px) {
  .book-detail-container {
    .book-detail-content {
      .book-detail-header {
        flex-direction: column;
        
        .book-cover {
          width: 250px;
          height: 350px;
          margin: 0 auto 30px;
        }
        
        .book-info {
          .book-title {
            font-size: 28px;
            text-align: center;
            
            &::after {
              left: 50%;
              transform: translateX(-50%);
            }
          }
          
          .book-actions {
            justify-content: center;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .book-detail-container {
    padding: 16px;
    
    .book-detail-content {
      .book-detail-header {
        .book-info {
          .book-title {
            font-size: 24px;
          }
          
          .book-meta {
            .meta-item {
              .label {
                width: 70px;
                font-size: 14px;
              }
              
              .value {
                font-size: 14px;
              }
            }
          }
          
          .book-actions {
            flex-wrap: wrap;
            
            .el-button {
              flex: 1;
              min-width: 120px;
              padding: 10px 15px;
              font-size: 14px;
            }
          }
        }
      }
    }
  }
}
</style> 