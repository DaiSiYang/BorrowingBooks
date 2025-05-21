<template>
  <div class="personal-center">
    <!-- 顶部背景 -->
    <div class="header-bg">
      <div class="overlay"></div>
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button icon="ArrowLeft" circle @click="goBack"></el-button>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card" :class="{ 'animate-slide-up': pageLoaded }">
      <div class="user-avatar">
        <el-avatar :size="100" :src="userInfoStore.userInfo.avatarUrl" />
      </div>
      <div class="user-info">
        <h2>{{ userInfoStore.userInfo.username || '未登录' }}</h2>
        <div class="user-stats">
          <div class="stat-item">
            <el-icon><Reading /></el-icon>
            <span>借阅 {{ borrowHistory.length }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Collection /></el-icon>
            <span>发布 {{ myBooks.length }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Calendar /></el-icon>
            <span>注册于 {{ formatDate(userInfoStore.userInfo.createTime) }}</span>
          </div>
        </div>
      </div>
      <div class="user-actions">
        <el-button type="primary" @click="publishNewBook" class="publish-btn">
          <el-icon><Plus /></el-icon> 发布新图书
        </el-button>
        <el-button class="edit-btn" @click="activeTab = 'profile'">
          <el-icon><Edit /></el-icon> 编辑资料
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area" :class="{ 'animate-slide-up': pageLoaded }">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="我的发布" name="myBooks">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-myBooks'" class="tab-content">
              <div v-if="myBooks.length === 0" class="empty-state">
                <el-empty description="暂无发布的图书">
                  <template #description>
                    <p>您还没有发布任何图书</p>
                  </template>
                  <el-button type="primary" @click="publishNewBook">现在发布</el-button>
                </el-empty>
              </div>
              <div v-else class="book-grid">
                <div v-for="book in myBooks" :key="book.id" class="book-card">
                  <div class="book-cover">
                    <img :src="book.cover" :alt="book.title">
                    <div class="book-status" :class="book.status === '可借阅' ? 'available' : 'borrowed'">
                      {{ book.status }}
                    </div>
                    <div class="book-ribbon" v-if="book.borrowCount > 10">热门</div>
                  </div>
                  <div class="book-info">
                    <h3 class="book-title">{{ book.title }}</h3>
                    <p class="book-author">{{ book.author }}</p>
                    <p class="book-publish-time">发布于: {{ formatDate(book.publishTime) }}</p>
                    <p class="book-borrow-count">
                      <el-icon><Reading /></el-icon> 已借阅 {{ book.borrowCount }} 次
                    </p>
                    <div class="book-actions">
                      <el-button size="small" type="primary" @click="editBook(book)" plain>编辑</el-button>
                      <el-button size="small" type="danger" @click="deleteBook(book)" plain>删除</el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </el-tab-pane>
        
        <el-tab-pane label="借阅历史" name="borrowHistory">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-borrowHistory'" class="tab-content">
              <div v-if="borrowHistory.length === 0" class="empty-state">
                <el-empty description="暂无借阅记录">
                  <template #description>
                    <p>您还没有借阅过任何图书</p>
                  </template>
                  <el-button type="primary" @click="router.push('/home/book-list')">去借阅</el-button>
                </el-empty>
              </div>
              <el-timeline v-else>
                <el-timeline-item
                  v-for="record in borrowHistory"
                  :key="record.id"
                  :type="record.status === '已归还' ? 'success' : 'primary'"
                  :timestamp="formatDate(record.borrowTime)"
                  :hollow="record.status === '已归还'"
                >
                  <div class="history-item">
                    <div class="book-cover">
                      <img :src="record.cover" :alt="record.title">
                    </div>
                    <div class="history-info">
                      <h3>{{ record.title }}</h3>
                      <p>作者: {{ record.author }}</p>
                      <p>借阅时间: {{ formatDate(record.borrowTime) }}</p>
                      <p v-if="record.returnTime">归还时间: {{ formatDate(record.returnTime) }}</p>
                      <el-tag :type="record.status === '已归还' ? 'success' : 'warning'" effect="light">
                        {{ record.status }}
                      </el-tag>
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </transition>
        </el-tab-pane>
        
        <el-tab-pane label="个人资料" name="profile">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-profile'" class="tab-content">
              <div class="profile-info">
                <div class="profile-card">
                  <div class="profile-header">
                    <el-icon class="profile-icon"><User /></el-icon>
                    <h3>基本信息</h3>
                  </div>
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="用户名">{{ userInfoStore.userInfo.username || '未设置' }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱">{{ userInfoStore.userInfo.email || '未设置' }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ userInfoStore.userInfo.phone || '未设置' }}</el-descriptions-item>
                    <el-descriptions-item label="注册时间">{{ formatDate(userInfoStore.userInfo.createTime) }}</el-descriptions-item>
                  </el-descriptions>
                </div>
                
                <div class="profile-actions">
                  <el-button type="primary">修改密码</el-button>
                  <el-button>更新个人信息</el-button>
                </div>
              </div>
            </div>
          </transition>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 底部装饰元素 -->
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, Reading, Collection, Calendar, Plus, Edit, User } from '@element-plus/icons-vue';
import {useUserInfoStore} from "@/stores/userInfo.js";

const router = useRouter();
const userInfoStore = useUserInfoStore();
const pageLoaded = ref(false);

// 返回上一页方法
const goBack = () => {
  router.go(-1);
};

// 格式化日期函数
const formatDate = (dateString) => {
  if (!dateString) return '未知日期';
  
  try {
    const date = new Date(dateString);
    
    if (isNaN(date.getTime())) return dateString;
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
  } catch (e) {
    return dateString;
  }
};

// 我发布的图书列表
const myBooks = ref([
  {
    id: 1,
    title: '百年孤独',
    author: '加西亚·马尔克斯',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s27237850.jpg',
    publishTime: '2023-05-20',
    status: '可借阅',
    borrowCount: 8
  },
  {
    id: 2,
    title: '活着',
    author: '余华',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s29053580.jpg',
    publishTime: '2023-06-15',
    status: '已借出',
    borrowCount: 5
  },
  {
    id: 3,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    publishTime: '2023-07-10',
    status: '可借阅',
    borrowCount: 12
  },
  {
    id: 4,
    title: '围城',
    author: '钱钟书',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1070222.jpg',
    publishTime: '2023-08-05',
    status: '可借阅',
    borrowCount: 3
  },
  {
    id: 5,
    title: '平凡的世界',
    author: '路遥',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s1144911.jpg',
    publishTime: '2023-09-01',
    status: '已借出',
    borrowCount: 7
  }
]);

// 借阅历史
const borrowHistory = ref([
  {
    id: 1,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s1791544.jpg',
    borrowTime: '2023-10-15',
    returnTime: '2023-11-15',
    status: '已归还'
  },
  {
    id: 2,
    title: '1984',
    author: '乔治·奥威尔',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s4371408.jpg',
    borrowTime: '2023-11-20',
    returnTime: '',
    status: '借阅中'
  }
]);

// 当前激活的标签页
const activeTab = ref('myBooks');

// 编辑图书
const editBook = (book) => {
  router.push(`/edit-book/${book.id}`);
};

// 删除图书
const deleteBook = (book) => {
  ElMessageBox.confirm(
    `确定要删除《${book.title}》吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 这里应该调用API删除图书
      myBooks.value = myBooks.value.filter(item => item.id !== book.id);
      ElMessage({
        type: 'success',
        message: '删除成功',
      });
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除',
      });
    });
};

// 发布新图书
const publishNewBook = () => {
  router.push('/publish-book');
};

onMounted(() => {
  // 这里应该调用API获取用户信息和图书列表
  console.log('个人中心页面已加载');
  // 触发入场动画
  setTimeout(() => {
    pageLoaded.value = true;
  }, 100);
});
</script>

<style scoped lang="scss">
.personal-center {
  position: relative;
  min-height: 100vh;
  background-color: #f8f9fc;
  padding-bottom: 40px;
  overflow-x: hidden;
}

.header-bg {
  height: 240px;
  background-image: linear-gradient(135deg, #183550 0%, #2c5282 100%);
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><path fill="%23ffffff" fill-opacity="0.05" d="M10 10L90 10 90 90 10 90z"/></svg>');
    background-size: 30px;
    opacity: 0.3;
  }
  
  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.3));
  }
  
  .back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    z-index: 20;
    
    .el-button {
      background-color: rgba(255, 255, 255, 0.9);
      border: none;
      box-shadow: 0 3px 12px rgba(0, 0, 0, 0.15);
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      
      &:hover {
        background-color: white;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
      }
    }
  }
}

.user-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.08);
  padding: 30px;
  margin: -80px auto 30px;
  max-width: 1000px;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 10;
  transform: translateY(30px);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
  
  &.animate-slide-up {
    transform: translateY(0);
    opacity: 1;
  }
  
  .user-avatar {
    margin-right: 40px;
    
    .el-avatar {
      border: 4px solid white;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
      }
    }
  }
  
  .user-info {
    flex: 1;
    
    h2 {
      margin: 0 0 18px;
      font-size: 28px;
      color: #303133;
      font-weight: 600;
    }
    
    .user-stats {
      display: flex;
      gap: 20px;
      
      .stat-item {
        display: flex;
        align-items: center;
        color: #606266;
        background-color: #f8f9fc;
        padding: 10px 18px;
        border-radius: 20px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
        border: 1px solid rgba(0, 0, 0, 0.02);
        transition: all 0.3s ease;
        
        &:hover {
          background-color: #fff;
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
          transform: translateY(-2px);
        }
        
        .el-icon {
          margin-right: 10px;
          font-size: 18px;
          color: #409eff;
        }
        
        span {
          font-weight: 500;
        }
      }
    }
  }
  
  .user-actions {
    display: flex;
    gap: 14px;
    
    .publish-btn {
      background: linear-gradient(135deg, #409eff, #007bff);
      border: none;
      padding: 12px 20px;
      font-weight: 500;
      box-shadow: 0 4px 15px rgba(64, 158, 255, 0.25);
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
      }
    }
    
    .edit-btn {
      border: 1px solid #e4e7ed;
      background-color: white;
      padding: 12px 20px;
      font-weight: 500;
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      
      &:hover {
        border-color: #c0c4cc;
        background-color: #f5f7fa;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      }
    }
  }
}

.content-area {
  max-width: 1000px;
  margin: 0 auto 40px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 25px rgba(0, 0, 0, 0.06);
  padding: 30px;
  position: relative;
  transform: translateY(30px);
  opacity: 0;
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
  transition-delay: 0.1s;
  
  &.animate-slide-up {
    transform: translateY(0);
    opacity: 1;
  }

  .tab-content {
    min-height: 400px;
  }
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    
    p {
      margin: 16px 0;
      color: #909399;
    }
  }
}

.custom-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 30px;
    border-bottom: 2px solid #f0f2f5;
  }
  
  :deep(.el-tabs__item) {
    font-size: 16px;
    padding: 0 25px;
    height: 50px;
    line-height: 50px;
    transition: all 0.3s ease;
    
    &.is-active {
      color: #409eff;
      font-weight: 600;
    }
    
    &:hover {
      color: #409eff;
    }
  }
  
  :deep(.el-tabs__active-bar) {
    height: 3px;
    border-radius: 3px;
    background: linear-gradient(90deg, #409eff, #007bff);
  }
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  padding: 10px 0;
}

.book-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background-color: white;
  position: relative;
  border: 1px solid rgba(0, 0, 0, 0.03);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 25px rgba(0, 0, 0, 0.1);
    border-color: rgba(64, 158, 255, 0.2);
  }
  
  .book-cover {
    height: 200px;
    position: relative;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
    }
    
    &:hover img {
      transform: scale(1.08);
    }
    
    .book-status {
      position: absolute;
      top: 15px;
      right: 15px;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      z-index: 2;
      letter-spacing: 0.5px;
      
      &.available {
        background-color: rgba(103, 194, 58, 0.95);
        color: white;
        box-shadow: 0 3px 10px rgba(103, 194, 58, 0.3);
      }
      
      &.borrowed {
        background-color: rgba(230, 162, 60, 0.95);
        color: white;
        box-shadow: 0 3px 10px rgba(230, 162, 60, 0.3);
      }
    }
    
    .book-ribbon {
      position: absolute;
      top: 15px;
      left: -30px;
      background: linear-gradient(45deg, #f56c6c, #e83e8c);
      color: white;
      padding: 6px 30px;
      transform: rotate(-45deg);
      font-size: 12px;
      font-weight: 600;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
      z-index: 2;
      letter-spacing: 1px;
    }
  }
  
  .book-info {
    padding: 20px;
    
    .book-title {
      margin: 0 0 8px;
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .book-author {
      color: #606266;
      margin: 0 0 12px;
      font-size: 14px;
      font-style: italic;
    }
    
    .book-publish-time, .book-borrow-count {
      color: #909399;
      font-size: 13px;
      margin: 8px 0;
      display: flex;
      align-items: center;
      
      .el-icon {
        margin-right: 6px;
        color: #409eff;
      }
    }
    
    .book-actions {
      margin-top: 15px;
      display: flex;
      justify-content: space-between;
      gap: 10px;
      
      .el-button {
        flex: 1;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
        }
      }
    }
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #409eff, #007bff);
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  &:hover::after {
    opacity: 1;
  }
}

.borrow-history {
  padding: 15px;
}

.history-item {
  display: flex;
  background-color: #f9fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(0, 0, 0, 0.02);
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    background-color: white;
  }
  
  .book-cover {
    width: 90px;
    height: 130px;
    margin-right: 25px;
    flex-shrink: 0;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 8px;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
  }
  
  .history-info {
    flex: 1;
    
    h3 {
      margin: 0 0 12px;
      font-size: 18px;
      color: #303133;
      font-weight: 600;
    }
    
    p {
      margin: 8px 0;
      color: #606266;
      font-size: 14px;
    }
    
    .el-tag {
      margin-top: 12px;
      padding: 0 15px;
      height: 28px;
      line-height: 26px;
      border-radius: 14px;
      font-weight: 500;
    }
  }
}

:deep(.el-timeline) {
  padding: 10px 20px;
  
  .el-timeline-item__tail {
    border-left: 2px solid #e4e7ed;
  }
  
  .el-timeline-item__node {
    background-color: #409eff;
    
    &.is-hollow {
      background-color: #fff;
      border-color: #67c23a;
    }
  }
  
  .el-timeline-item__wrapper {
    padding-left: 20px;
  }
  
  .el-timeline-item__timestamp {
    color: #909399;
    font-size: 13px;
    margin-top: 8px;
  }
}

.profile-info {
  padding: 20px;
  
  .profile-card {
    background-color: #f9fafc;
    border-radius: 12px;
    padding: 25px;
    margin-bottom: 30px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
    border: 1px solid rgba(0, 0, 0, 0.02);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.06);
      background-color: white;
    }
    
    .profile-header {
      display: flex;
      align-items: center;
      margin-bottom: 25px;
      
      .profile-icon {
        font-size: 24px;
        color: #409eff;
        margin-right: 12px;
        background-color: rgba(64, 158, 255, 0.1);
        width: 45px;
        height: 45px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      h3 {
        margin: 0;
        font-size: 20px;
        color: #303133;
        font-weight: 600;
      }
    }
    
    :deep(.el-descriptions) {
      .el-descriptions__label {
        width: 120px;
        color: #606266;
        font-weight: 500;
      }
      
      .el-descriptions__content {
        color: #303133;
        font-weight: 400;
      }
      
      .el-descriptions__body {
        background-color: white;
      }
      
      .el-descriptions-item__container {
        margin-bottom: 0;
      }
    }
  }
  
  .profile-actions {
    margin-top: 30px;
    display: flex;
    gap: 15px;
    justify-content: center;
    
    .el-button {
      min-width: 120px;
      padding: 12px 25px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      &[type="primary"] {
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
      }
    }
  }
}

// 响应式调整
@media (max-width: 991px) {
  .user-card {
    margin-left: 15px;
    margin-right: 15px;
    padding: 25px;
  }
  
  .content-area {
    margin-left: 15px;
    margin-right: 15px;
    padding: 25px 20px;
  }
  
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
    
    .user-avatar {
      margin-right: 0;
      margin-bottom: 20px;
    }
    
    .user-stats {
      justify-content: center;
      flex-wrap: wrap;
    }
    
    .user-actions {
      margin-top: 25px;
      width: 100%;
      justify-content: center;
    }
  }
  
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
  }
  
  .history-item {
    flex-direction: column;
    
    .book-cover {
      margin: 0 auto 20px;
    }
    
    .history-info {
      text-align: center;
    }
  }
  
  .profile-info {
    padding: 10px;
  }
}

@media (max-width: 480px) {
  .header-bg {
    height: 180px;
  }
  
  .user-card {
    margin-top: -60px;
    padding: 20px 15px;
  }
  
  .user-info {
    h2 {
      font-size: 22px;
      margin-bottom: 15px;
    }
    
    .user-stats {
      gap: 10px;
      
      .stat-item {
        padding: 8px 12px;
        font-size: 12px;
        
        .el-icon {
          font-size: 14px;
          margin-right: 6px;
        }
      }
    }
  }
  
  .user-actions {
    flex-direction: column;
    gap: 10px;
    
    .el-button {
      width: 100%;
    }
  }
  
  .content-area {
    padding: 15px;
  }
  
  .custom-tabs {
    :deep(.el-tabs__item) {
      padding: 0 15px;
      font-size: 14px;
    }
  }
  
  .profile-actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>

// 标签页内容切换动画
<style>
.fade-fast-enter-active,
.fade-fast-leave-active {
  transition: opacity 0.3s ease;
}

.fade-fast-enter-from,
.fade-fast-leave-to {
  opacity: 0;
}
</style>