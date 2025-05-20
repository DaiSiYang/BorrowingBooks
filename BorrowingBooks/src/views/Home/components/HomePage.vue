<script setup>
import { ref, onMounted } from 'vue'
import {Calendar, Document, Refresh, Star, Warning} from "@element-plus/icons-vue";

// 获取当前时间
const currentTime = ref(new Date())
const greeting = ref('')

// 根据时间设置不同的问候语
const setGreeting = () => {
  const hours = currentTime.value.getHours()
  if (hours >= 5 && hours < 12) {
    greeting.value = '早上好'
  } else if (hours >= 12 && hours < 14) {
    greeting.value = '中午好'
  } else if (hours >= 14 && hours < 18) {
    greeting.value = '下午好'
  } else {
    greeting.value = '晚上好'
  }
}

// 热门图书数据 - 移除借阅数量
const popularBooks = ref([
  { id: 1, title: '活着', author: '余华', cover: 'book1.jpg' },
  { id: 2, title: '三体', author: '刘慈欣', cover: 'book2.jpg' },
  { id: 3, title: '平凡的世界', author: '路遥', cover: 'book3.jpg' },
  { id: 4, title: '围城', author: '钱钟书', cover: 'book4.jpg' }
])

onMounted(() => {
  setGreeting()
  // 每分钟更新一次时间
  setInterval(() => {
    currentTime.value = new Date()
    setGreeting()
  }, 60000)
})
</script>

<template>
  <div class="home-page">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">{{ greeting }}，欢迎使用青梧书径图书借阅系统</h1>
        <p class="welcome-subtitle">书籍是人类进步的阶梯，知识是改变命运的力量</p>
        <div class="welcome-actions">
          <el-button type="primary" size="large" class="action-button">
            <el-icon><Search /></el-icon>浏览图书
          </el-button>
          <el-button size="large" class="action-button">
            <el-icon><Reading /></el-icon>我的借阅
          </el-button>
        </div>
      </div>
      <div class="welcome-image">
        <img src="@/assets/Image/logo.png" alt="图书馆" />
      </div>
    </div>

    <!-- 借阅规则 -->
    <div class="rules-section">
      <h2 class="section-title">借阅规则</h2>
      <el-card class="rules-card">
        <div class="rules-content">
          <div class="rule-item">
            <div class="rule-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="rule-text">
              <h3>借阅期限</h3>
              <p>普通图书借阅期为30天，珍藏图书借阅期为15天。</p>
            </div>
          </div>
          
          <div class="rule-item">
            <div class="rule-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="rule-text">
              <h3>借阅限制</h3>
              <p>每位读者最多可同时借阅5本普通图书，2本珍藏图书。</p>
            </div>
          </div>
          
          <div class="rule-item">
            <div class="rule-icon">
              <el-icon><Refresh /></el-icon>
            </div>
            <div class="rule-text">
              <h3>续借规则</h3>
              <p>普通图书可续借2次，每次续借期限为15天；珍藏图书可续借1次，续借期限为7天。</p>
            </div>
          </div>
          
          <div class="rule-item">
            <div class="rule-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="rule-text">
              <h3>逾期处理</h3>
              <p>图书逾期未还将产生逾期费用，每本书每天收取0.5元。逾期超过30天将暂停借阅权限。</p>
            </div>
          </div>
          
          <div class="rule-item">
            <div class="rule-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="rule-text">
              <h3>爱护图书</h3>
              <p>请爱护图书，如有损坏或遗失，需按图书价格的1.5倍赔偿。</p>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 热门图书 - 移除借阅数量显示 -->
    <div class="popular-section">
      <h2 class="section-title">热门图书</h2>
      <div class="popular-books">
        <el-card v-for="book in popularBooks" :key="book.id" class="book-card" shadow="hover">
          <div class="book-cover">
            <img :src="`@/assets/Image/${book.cover}`" :alt="book.title" />
            <div class="book-rank">
              <span class="rank-number">{{ book.id }}</span>
            </div>
            <div class="book-actions">
              <el-button type="primary" size="small" round>
                <el-icon><Reading /></el-icon> 借阅
              </el-button>
              <el-button type="info" size="small" round>
                <el-icon><InfoFilled /></el-icon> 详情
              </el-button>
            </div>
          </div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">作者：{{ book.author }}</p>
            <!-- 移除了借阅次数标签 -->
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.home-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto; // 添加这一行，允许整个页面垂直滚动
  
  .welcome-section {
    display: flex;
    align-items: center;
    margin-bottom: 40px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    
    .welcome-content {
      flex: 1;
      padding: 40px;
      
      .welcome-title {
        font-size: 28px;
        color: #183550;
        margin-bottom: 16px;
      }
      
      .welcome-subtitle {
        font-size: 16px;
        color: #68b8d7;
        margin-bottom: 30px;
      }
      
      .welcome-actions {
        display: flex;
        gap: 16px;
        
        .action-button {
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }
    }
    
    .welcome-image {
      flex: 1;
      height: 300px;
      overflow: hidden;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
  
  .section-title {
    font-size: 24px;
    color: #183550;
    margin-bottom: 20px;
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
  
  .rules-section {
    margin-bottom: 40px;
    
    .rules-card {
      border-radius: 8px;
      
      .rules-content {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 24px;
        
        .rule-item {
          display: flex;
          align-items: flex-start;
          
          .rule-icon {
            margin-right: 16px;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background-color: #e6f1f6;
            display: flex;
            align-items: center;
            justify-content: center;
            
            .el-icon {
              font-size: 20px;
              color: #68b8d7;
            }
          }
          
          .rule-text {
            flex: 1;
            
            h3 {
              font-size: 18px;
              color: #183550;
              margin-bottom: 8px;
            }
            
            p {
              font-size: 14px;
              color: #606266;
              line-height: 1.6;
            }
          }
        }
      }
    }
  }
  
  .popular-section {
    margin-bottom: 40px;
    
    .popular-books {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 24px;
      
      .book-card {
        border-radius: 12px;
        overflow: hidden;
        transition: all 0.3s ease;
        border: none;
        
        &:hover {
          transform: translateY(-8px);
          box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
          
          .book-actions {
            opacity: 1;
          }
        }
        
        .book-cover {
          position: relative;
          height: 280px;
          overflow: hidden;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s;
          }
          
          .book-rank {
            position: absolute;
            top: 10px;
            left: 10px;
            width: 30px;
            height: 30px;
            background: linear-gradient(135deg, #183550, #68b8d7);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            
            .rank-number {
              color: white;
              font-weight: bold;
              font-size: 16px;
            }
          }
          
          .book-actions {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(24, 53, 80, 0.8);
            padding: 12px;
            display: flex;
            justify-content: space-around;
            opacity: 0;
            transition: opacity 0.3s;
          }
        }
        
        .book-info {
          padding: 16px;
          background: white;
          
          .book-title {
            font-size: 18px;
            color: #183550;
            margin-bottom: 8px;
            font-weight: bold;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          
          .book-author {
            font-size: 14px;
            color: #606266;
            margin-bottom: 12px;
          }
          
          .book-stats {
            display: flex;
            justify-content: space-between;
            align-items: center;
          }
        }
      }
    }
  }
}

// 确保主容器可以滚动
.home-page {
  height: 100%;
  overflow-y: auto;
  padding-bottom: 40px;
}

// 响应式调整
@media (max-width: 768px) {
  .popular-section {
    .popular-books {
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      
      .book-card {
        .book-cover {
          height: 220px;
        }
      }
    }
  }
}
</style>