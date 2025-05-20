

<template>
  <div class="personal-center">
    <!-- 顶部背景 -->
    <div class="header-bg">
      <div class="overlay"></div>
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button icon="ArrowLeft" circle @click="goBack"></el-button>
      </div>
      <!-- 装饰性书籍元素 -->
      <div class="floating-books">
        <div class="book book-1"></div>
        <div class="book book-2"></div>
        <div class="book book-3"></div>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card" :class="{ 'animate-slide-up': pageLoaded }">
      <div class="user-avatar">
        <el-avatar :size="100" :src="userInfoStore.userInfo.avatarUrl" />
      </div>
      <div class="user-info">
        <h2>{{ userInfoStore.userInfo.username }}</h2>
        <div class="user-stats">
          <div class="stat-item">
            <el-icon><Reading /></el-icon>
            <span>借阅 {{ 0 }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Collection /></el-icon>
            <span>发布 {{ 0 }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Calendar /></el-icon>
            <span>注册于 {{ userInfoStore.userInfo.createTime }}</span>
          </div>
        </div>
      </div>
      <div class="user-actions">
        <el-button type="primary" @click="publishNewBook" class="publish-btn">
          <el-icon><Plus /></el-icon> 发布新图书
        </el-button>
        <el-button class="edit-btn">
          <el-icon><Edit /></el-icon> 编辑资料
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area" :class="{ 'animate-slide-up': pageLoaded }">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="我的发布" name="myBooks">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-myBooks'">
              <div class="book-list">
                <el-empty v-if="myBooks.length === 0" description="暂无发布的图书"></el-empty>
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
                      <p class="book-publish-time">发布于: {{ book.publishTime }}</p>
                      <p class="book-borrow-count">
                        <el-icon><Reading /></el-icon> 已借阅 {{ book.borrowCount }} 次
                      </p>
                      <div class="book-actions">
                        <el-button size="small" type="primary" @click="editBook(book)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteBook(book)">删除</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </el-tab-pane>
        
        <el-tab-pane label="借阅历史" name="borrowHistory">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-borrowHistory'">
              <div class="borrow-history">
                <el-empty v-if="borrowHistory.length === 0" description="暂无借阅记录"></el-empty>
                <el-timeline v-else>
                  <el-timeline-item
                    v-for="record in borrowHistory"
                    :key="record.id"
                    :type="record.status === '已归还' ? 'success' : 'primary'"
                    :timestamp="record.borrowTime"
                  >
                    <div class="history-item">
                      <div class="book-cover">
                        <img :src="record.cover" :alt="record.title">
                      </div>
                      <div class="history-info">
                        <h3>{{ record.title }}</h3>
                        <p>作者: {{ record.author }}</p>
                        <p>借阅时间: {{ record.borrowTime }}</p>
                        <p v-if="record.returnTime">归还时间: {{ record.returnTime }}</p>
                        <el-tag :type="record.status === '已归还' ? 'success' : 'warning'">
                          {{ record.status }}
                        </el-tag>
                      </div>
                    </div>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </transition>
        </el-tab-pane>
        
        <el-tab-pane label="个人资料" name="profile">
          <transition name="fade-fast" mode="out-in">
            <div :key="activeTab + '-profile'">
              <div class="profile-info">
                <div class="profile-card">
                  <div class="profile-header">
                    <el-icon class="profile-icon"><User /></el-icon> {/* Changed */}
                    <h3>基本信息</h3>
                  </div>
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="用户名">{{ userInfoStore.userInfo.username }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱">{{ userInfoStore.userInfo.email }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ userInfoStore.userInfo.phone }}</el-descriptions-item>
                    <el-descriptions-item label="注册时间">{{ userInfoStore.userInfo.createTime }}</el-descriptions-item>
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
    <div class="bottom-decoration">
      <div class="book-shelf"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, Reading, Collection, Calendar, Plus, Edit, User } from '@element-plus/icons-vue';
import {useUserInfoStore} from "@/stores/userInfo.js";

const router = useRouter();
const userInfoStore = useUserInfoStore();

// 返回上一页方法
const goBack = () => {
  router.go(-1);
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

const pageLoaded = ref(false);

onMounted(() => {
  // 这里应该调用API获取用户信息和图书列表
  console.log('个人中心页面已加载');
  // 触发入场动画
  setTimeout(() => { // 使用 setTimeout 确保 DOM 渲染完成
    pageLoaded.value = true;
  }, 100);
});
</script>

<style scoped lang="scss">
.personal-center {
  position: relative;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40px;
  overflow-x: hidden;
}

.header-bg {
  height: 250px;
  background-image: url('https://img.freepik.com/free-photo/books-arrangement-with-copy-space_23-2148898331.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  
  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.5));
  }
  
  .back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    z-index: 20;
    
    .el-button {
      background-color: rgba(255, 255, 255, 0.8);
      border: none;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
      transition: all 0.3s ease;
      
      &:hover {
        background-color: white;
        transform: scale(1.05);
      }
    }
  }
  
  // 装饰性书籍元素
  .floating-books {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    
    .book {
      position: absolute;
      background-size: contain;
      background-repeat: no-repeat;
      opacity: 0.6;
      filter: drop-shadow(0 5px 15px rgba(0, 0, 0, 0.3));
      
      &.book-1 {
        width: 100px;
        height: 150px;
        background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23ffffff"><path d="M21 4H3a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1zm-1 14H4V6h16v12z"/><path d="M8 8h8v2H8zm0 4h8v2H8z"/></svg>');
        top: 30px;
        right: 15%;
        transform: rotate(15deg);
        animation: float 8s ease-in-out infinite;
      }
      
      &.book-2 {
        width: 120px;
        height: 180px;
        background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23ffffff"><path d="M6 22h12a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2zm0-2V4h12v16H6z"/><path d="M9 7h6v2H9zm0 4h6v2H9zm0 4h6v2H9z"/></svg>');
        bottom: 40px;
        left: 10%;
        transform: rotate(-10deg);
        animation: float 10s ease-in-out infinite reverse;
      }
      
      &.book-3 {
        width: 90px;
        height: 130px;
        background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23ffffff"><path d="M19 2H5a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2zm0 18H5V4h14v16z"/><path d="M7 8h10v2H7zm0 4h10v2H7zm0 4h7v2H7z"/></svg>');
        top: 50px;
        left: 25%;
        transform: rotate(5deg);
        animation: float 12s ease-in-out infinite;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(var(--rotation, 0deg));
  }
  50% {
    transform: translateY(-20px) rotate(var(--rotation, 0deg));
  }
}

.user-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin: -80px auto 30px;
  max-width: 1000px;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 10;
  
  &::before {
    content: '';
    position: absolute;
    top: -15px;
    right: 30px;
    width: 40px;
    height: 40px;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23409eff"><path d="M21 4H3a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1zm-1 14H4V6h16v12z"/><path d="M8 8h8v2H8zm0 4h8v2H8z"/></svg>');
    background-repeat: no-repeat;
    transform: rotate(-15deg);
    opacity: 0.2;
  }
  
  .user-avatar {
    margin-right: 30px;
    
    .el-avatar {
      border: 4px solid white;
      box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
    }
  }
  
  .user-info {
    flex: 1;
    
    h2 {
      margin: 0 0 15px;
      font-size: 26px;
      color: #303133;
      font-weight: 600;
    }
    
    .user-stats {
      display: flex;
      gap: 25px;
      
      .stat-item {
        display: flex;
        align-items: center;
        color: #606266;
        background-color: #f5f7fa;
        padding: 8px 15px;
        border-radius: 20px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        
        i {
          margin-right: 8px;
          font-size: 16px;
          color: #409eff;
        }
      }
    }
  }
  
  .user-actions {
    display: flex;
    gap: 12px;
    
    .publish-btn {
      background: linear-gradient(135deg, #409eff, #007bff);
      border: none;
      box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 15px rgba(64, 158, 255, 0.4);
      }
    }
    
    .edit-btn {
      border: 1px solid #dcdfe6;
      background-color: white;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: #c0c4cc;
        background-color: #f5f7fa;
      }
    }
  }
}

.content-area {
  max-width: 1000px;
  margin: 0 auto 40px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
  padding: 25px;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -20px;
    right: -20px;
    width: 60px;
    height: 60px;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23409eff"><path d="M6 22h12a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2zm0-2V4h12v16H6z"/><path d="M9 7h6v2H9zm0 4h6v2H9zm0 4h6v2H9z"/></svg>');
    background-repeat: no-repeat;
    opacity: 0.1;
    z-index: 0;
    transform: rotate(15deg);
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
  gap: 30px;
  padding: 10px;
}

.book-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease; // Added border-color
  background-color: white;
  position: relative;
  border: 1px solid transparent; // Initial transparent border

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.18); // Slightly more pronounced shadow
    border-color: #409eff; // Highlight border on hover
  }
  
  .book-cover {
    height: 220px;
    position: relative;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
    
    &:hover img {
      transform: scale(1.08);
    }
    
    .book-status {
      position: absolute;
      top: 15px;
      right: 15px;
      padding: 5px 10px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: bold;
      z-index: 2;
      
      &.available {
        background-color: #67c23a;
        color: white;
        box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
      }
      
      &.borrowed {
        background-color: #e6a23c;
        color: white;
        box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
      }
    }
    
    .book-ribbon {
      position: absolute;
      top: 15px;
      left: -30px;
      background: #f56c6c;
      color: white;
      padding: 5px 30px;
      transform: rotate(-45deg);
      font-size: 12px;
      font-weight: bold;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      z-index: 2;
    }
  }
  
  .book-info {
    padding: 20px;
    
    .book-title {
      margin: 0 0 8px;
      font-size: 18px;
      font-weight: bold;
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
      
      i {
        margin-right: 5px;
        color: #409eff;
      }
    }
    
    .book-actions {
      margin-top: 15px;
      display: flex;
      justify-content: space-between;
      
      .el-button {
        flex: 1;
        margin: 0 5px;
        
        &:first-child {
          margin-left: 0;
        }
        
        &:last-child {
          margin-right: 0;
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
    height: 5px;
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
  
  .history-item {
    display: flex;
    background-color: #f8f9fa;
    border-radius: 10px;
    padding: 20px;
    margin-bottom: 15px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    }
    
    .book-cover {
      width: 90px;
      height: 130px;
      margin-right: 25px;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 6px;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
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
        margin-top: 10px;
        padding: 0 12px;
        height: 28px;
        line-height: 26px;
        border-radius: 14px;
      }
    }
  }
}

.profile-info {
  padding: 20px;
  
  .profile-card {
    background-color: #f8f9fa;
    border-radius: 10px;
    padding: 25px;
    margin-bottom: 30px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    .profile-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      
      .profile-icon {
        font-size: 24px;
        color: #409eff;
        margin-right: 10px;
        background-color: rgba(64, 158, 255, 0.1);
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
      }
    }
    
    :deep(.el-descriptions) {
      .el-descriptions__label {
        width: 120px;
        color: #606266;
      }
      
      .el-descriptions__content {
        color: #303133;
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
    }
  }
}

// 底部装饰元素
.bottom-decoration {
  position: relative;
  height: 60px;
  margin-top: 40px;
  
  .book-shelf {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 20px;
    background-color: #8c6a56;
    box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
    
    &::before {
      content: '';
      position: absolute;
      top: -15px;
      left: 0;
      right: 0;
      height: 15px;
      background: repeating-linear-gradient(
        90deg,
        #e74c3c,
        #e74c3c 30px,
        #3498db 30px,
        #3498db 60px,
        #2ecc71 60px,
        #2ecc71 90px,
        #f1c40f 90px,
        #f1c40f 120px,
        #9b59b6 120px,
        #9b59b6 150px
      );
    }
  }
}

// 响应式调整
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
      margin-top: 20px;
      width: 100%;
      justify-content: center;
    }
  }
  
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
  
  .history-item {
    flex-direction: column;
    
    .book-cover {
      margin: 0 auto 15px;
    }
  }
}
</style>

// 标签页内容切换动画
.fade-fast-enter-active,
.fade-fast-leave-active {
  transition: opacity 0.3s ease;
}

.fade-fast-enter-from,
.fade-fast-leave-to {
  opacity: 0;
}