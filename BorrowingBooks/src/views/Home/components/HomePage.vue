<script setup>
import { ref, onMounted } from 'vue'
import {Calendar, Document, Refresh, Star, Warning, Search, Reading, InfoFilled} from "@element-plus/icons-vue";

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

// 借阅规则数据
const borrowRules = ref([
  { 
    icon: Calendar, 
    title: '借阅期限', 
    desc: '普通图书借阅期为30天，珍藏图书借阅期为15天。' 
  },
  { 
    icon: Document, 
    title: '借阅限制', 
    desc: '每位读者最多可同时借阅5本普通图书，2本珍藏图书。' 
  },
  { 
    icon: Refresh, 
    title: '续借规则', 
    desc: '普通图书可续借2次，每次续借期限为15天；珍藏图书可续借1次，续借期限为7天。' 
  },
  { 
    icon: Warning, 
    title: '逾期处理', 
    desc: '图书逾期未还将产生逾期费用，每本书每天收取0.5元。逾期超过30天将暂停借阅权限。' 
  },
  { 
    icon: Star, 
    title: '爱护图书', 
    desc: '请爱护图书，如有损坏或遗失，需按图书价格的1.5倍赔偿。' 
  }
])

// 热门图书数据
const popularBooks = ref([
  { 
    id: 1, 
    title: '活着', 
    author: '余华', 
    cover: 'book1.jpg',
    rating: 9.4,
    desc: '讲述了一个人历经世间沧桑和种种磨难的人生故事，揭示了人如何面对荒谬的命运。'
  },
  { 
    id: 2, 
    title: '三体', 
    author: '刘慈欣', 
    cover: 'book2.jpg',
    rating: 9.3,
    desc: '描绘了人类文明与三体文明的信息交流、生死搏杀及两个文明若干次的生死存亡的历程。'
  },
  { 
    id: 3, 
    title: '平凡的世界', 
    author: '路遥', 
    cover: 'book3.jpg',
    rating: 9.0,
    desc: '展现了中国社会从70年代到80年代期间，普通人在大时代背景下的命运变迁。'
  },
  { 
    id: 4, 
    title: '围城', 
    author: '钱钟书', 
    cover: 'book4.jpg',
    rating: 8.9,
    desc: '刻画了一幅20世纪30年代中期知识分子的群像，围城是结婚的人想逃出去，未婚的人想挤进去。'
  }
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
        <el-badge value="新" class="greeting-badge">
          <h1 class="welcome-title">{{ greeting }}，欢迎使用青梧书径图书借阅系统</h1>
        </el-badge>
        <p class="welcome-subtitle">书籍是人类进步的阶梯，知识是改变命运的力量</p>
        
        <!-- 统计数据展示 -->
        <div class="welcome-stats">
          <div class="stat-item">
            <div class="stat-number">5,000+</div>
            <div class="stat-label">馆藏图书</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">300+</div>
            <div class="stat-label">今日借阅</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">12,000+</div>
            <div class="stat-label">会员用户</div>
          </div>
        </div>
        
        <div class="welcome-actions">
          <el-button type="primary" size="large" class="action-button" :icon="Search">
            浏览图书
          </el-button>
          <el-button size="large" class="action-button" :icon="Reading">
            我的借阅
          </el-button>
        </div>
      </div>
      
      <!-- 优化的装饰区域 -->
      <div class="welcome-decoration">
        <div class="brand-visual">
          <div class="books-container">
            <div class="book book-brown">
              <div class="book-spine"></div>
              <div class="book-title">青梧</div>
            </div>
            <div class="book book-red">
              <div class="book-spine"></div>
              <div class="book-title">书径</div>
            </div>
            <div class="book book-blue">
              <div class="book-spine"></div>
            </div>
          </div>
          
          <div class="brand-name">
            <div class="cn-name">青梧书径</div>
            <div class="en-name">QingWuShuJing</div>
          </div>
          
          <div class="plum-blossom">
            <div class="blossom-center"></div>
            <div class="blossom-petal p1"></div>
            <div class="blossom-petal p2"></div>
            <div class="blossom-petal p3"></div>
            <div class="blossom-petal p4"></div>
            <div class="blossom-petal p5"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 借阅规则 -->
    <div class="rules-section">
      <h2 class="section-title">借阅规则</h2>
      <div class="rules-content">
        <el-card 
          v-for="(rule, index) in borrowRules" 
          :key="index" 
          class="rule-card"
          shadow="hover"
        >
          <div class="rule-inner">
            <div class="rule-icon" :class="`rule-icon-${index+1}`">
              <el-icon>
                <component :is="rule.icon"></component>
              </el-icon>
            </div>
            <h3 class="rule-title">{{ rule.title }}</h3>
            <p class="rule-desc">{{ rule.desc }}</p>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 热门图书 -->
    <div class="popular-section">
      <h2 class="section-title">热门图书</h2>
      
      <el-carousel :interval="4000" type="card" height="380px" class="book-carousel">
        <el-carousel-item v-for="book in popularBooks" :key="book.id" class="carousel-item">
          <div class="featured-book-card">
            <div class="book-cover">
              <img :src="`@/assets/Image/${book.cover}`" :alt="book.title" />
              <div class="book-rank">
                <span class="rank-number">{{ book.id }}</span>
              </div>
              <div class="book-rating">
                <el-rate
                  v-model="book.rating"
                  disabled
                  :colors="['#ffd21e', '#ffd21e', '#ffd21e']"
                  score-template="{value}"
                  text-color="#e36049"
                  show-score
                ></el-rate>
              </div>
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">作者：{{ book.author }}</p>
              <p class="book-desc">{{ book.desc }}</p>
              <div class="book-actions">
                <el-button type="primary" round>
                  <el-icon><Reading /></el-icon> 借阅
                </el-button>
                <el-button type="info" round>
                  <el-icon><InfoFilled /></el-icon> 详情
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      
      <div class="popular-books">
        <div v-for="book in popularBooks" :key="book.id" class="book-card">
          <div class="book-inner">
            <div class="book-cover">
              <img :src="`@/assets/Image/${book.cover}`" :alt="book.title" />
              <div class="book-hover-info">
                <p class="book-desc-preview">{{ book.desc }}</p>
                <div class="book-actions">
                  <el-button type="primary" round size="small">
                    <el-icon><Reading /></el-icon> 借阅
                  </el-button>
                </div>
              </div>
              <div class="book-rank">
                <span class="rank-number">{{ book.id }}</span>
              </div>
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">作者：{{ book.author }}</p>
              <div class="book-rating">
                <el-rate
                  v-model="book.rating"
                  disabled
                  :colors="['#ffd21e', '#ffd21e', '#ffd21e']"
                  show-score
                  text-color="#e36049"
                  score-template="{value}"
                ></el-rate>
                <span class="rating-value">{{ book.rating }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.home-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;
  
  .welcome-section {
    display: flex;
    align-items: stretch;
    margin-bottom: 40px;
    background: linear-gradient(135deg, #f9f6f2 0%, #f2ebe3 100%);
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    position: relative;
    transition: all 0.3s ease;
    border-left: 4px solid #8a5f41;
    
    &:hover {
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
      transform: translateY(-5px);
    }
    
    .welcome-content {
      flex: 3;
      padding: 40px 50px;
      
      .greeting-badge :deep(.el-badge__content) {
        background-color: #e36049;
        border: none;
      }
      
      .welcome-title {
        font-size: 32px;
        color: #3d2c29;
        margin-bottom: 16px;
        font-weight: 600;
        line-height: 1.3;
        position: relative;
        z-index: 2;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 0;
          width: 120px;
          height: 2px;
          background: linear-gradient(to right, #8a5f41, transparent);
        }
      }
      
      .welcome-subtitle {
        font-size: 16px;
        color: #8a5f41;
        margin-bottom: 30px;
        font-style: italic;
        letter-spacing: 1px;
      }
      
      .welcome-stats {
        display: flex;
        gap: 40px;
        margin-bottom: 30px;
        
        .stat-item {
          text-align: center;
          
          .stat-number {
            font-size: 24px;
            color: #e36049;
            font-weight: bold;
            margin-bottom: 5px;
          }
          
          .stat-label {
            font-size: 14px;
            color: #8a5f41;
          }
        }
      }
      
      .welcome-actions {
        display: flex;
        gap: 16px;
        
        .action-button {
          border-radius: 8px;
          padding: 12px 24px;
          transition: all 0.3s;
          
          &:first-child {
            background-color: #8a5f41;
            border-color: #8a5f41;
            
            &:hover {
              background-color: #6e4c34;
              border-color: #6e4c34;
              transform: translateY(-2px);
            }
          }
          
          &:last-child {
            color: #8a5f41;
            border-color: #8a5f41;
            
            &:hover {
              color: #6e4c34;
              border-color: #6e4c34;
              background-color: rgba(138, 95, 65, 0.05);
              transform: translateY(-2px);
            }
          }
        }
      }
    }
    
    .welcome-decoration {
      flex: 2;
      position: relative;
      background-color: rgba(255, 255, 255, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 30px;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: radial-gradient(circle at center, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.8) 70%);
        z-index: 1;
      }
      
      .brand-visual {
        position: relative;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        z-index: 2;
        
        .books-container {
          display: flex;
          align-items: flex-end;
          margin-bottom: 25px;
          transform: perspective(800px) rotateX(10deg);
          
          .book {
            position: relative;
            height: 120px;
            width: 80px;
            margin: 0 -10px;
            border-radius: 3px 10px 10px 3px;
            box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.15);
            transition: all 0.3s ease;
            overflow: hidden;
            
            .book-spine {
              position: absolute;
              left: 0;
              top: 0;
              width: 15px;
              height: 100%;
              background-color: rgba(0, 0, 0, 0.1);
              border-radius: 2px 0 0 2px;
            }
            
            .book-title {
              position: absolute;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              color: rgba(255, 255, 255, 0.9);
              font-size: 24px;
              font-weight: bold;
              text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
              font-family: "STKaiti", "楷体", serif;
            }
            
            &.book-brown {
              background-color: #8a5f41;
              z-index: 3;
              transform: rotate(-5deg);
            }
            
            &.book-red {
              background-color: #e36049;
              z-index: 2;
              transform: rotate(3deg);
            }
            
            &.book-blue {
              background-color: #4c8dae;
              z-index: 1;
              transform: rotate(-2deg);
            }
            
            &:hover {
              transform: translateY(-10px) rotate(0);
            }
          }
        }
        
        .brand-name {
          text-align: center;
          margin-bottom: 20px;
          
          .cn-name {
            font-family: "STKaiti", "楷体", serif;
            font-size: 36px;
            font-weight: bold;
            color: #3d2c29;
            line-height: 1.2;
            margin-bottom: 5px;
            letter-spacing: 2px;
          }
          
          .en-name {
            font-size: 14px;
            color: #8a5f41;
            letter-spacing: 1px;
            opacity: 0.8;
          }
        }
        
        .plum-blossom {
          position: absolute;
          bottom: 20px;
          right: 20px;
          width: 50px;
          height: 50px;
          
          .blossom-center {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 12px;
            height: 12px;
            background-color: #ffd700;
            border-radius: 50%;
            z-index: 2;
          }
          
          .blossom-petal {
            position: absolute;
            width: 20px;
            height: 20px;
            background-color: #e36049;
            border-radius: 40% 40% 40% 0;
            
            &.p1 {
              top: 0;
              left: 50%;
              transform: translateX(-50%) rotate(-45deg);
            }
            
            &.p2 {
              top: 50%;
              right: 0;
              transform: translateY(-50%) rotate(45deg);
            }
            
            &.p3 {
              bottom: 0;
              left: 50%;
              transform: translateX(-50%) rotate(135deg);
            }
            
            &.p4 {
              top: 50%;
              left: 0;
              transform: translateY(-50%) rotate(-135deg);
            }
            
            &.p5 {
              top: 25%;
              left: 25%;
              transform: rotate(-90deg);
              width: 15px;
              height: 15px;
            }
          }
        }
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
    margin-bottom: 50px;
    
    .rules-content {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); // 增加卡片宽度
      gap: 30px; // 增加卡片间距
      
      .rule-card {
        border: none;
        border-radius: 12px;
        height: 100%;
        transition: all 0.3s ease;
        overflow: hidden;
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 15px 25px rgba(0, 0, 0, 0.1);
          
          .rule-icon {
            transform: scale(1.1) rotate(5deg);
          }
        }
        
        .rule-inner {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 40px 25px; // 增加内边距
          text-align: center;
          height: 100%;
          position: relative;
          overflow: hidden;
          
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 5px;
            background: linear-gradient(to right, transparent, #e36049, transparent);
            transform: translateX(-100%);
            transition: transform 0.5s ease;
          }
          
          &:hover::before {
            transform: translateX(0);
          }
        }
        
        .rule-icon {
          width: 70px; // 增大图标尺寸
          height: 70px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 25px; // 增加下边距
          transition: all 0.3s ease;
          background-color: #f8f4ef;
          
          .el-icon {
            font-size: 32px; // 增大图标尺寸
            color: #8a5f41;
          }
        }
        
        .rule-icon-1 { 
          box-shadow: 0 5px 15px rgba(227, 96, 73, 0.2);
          .el-icon { color: #e36049; }
        }
        
        .rule-icon-2 { 
          box-shadow: 0 5px 15px rgba(76, 141, 174, 0.2);
          .el-icon { color: #4c8dae; }
        }
        
        .rule-icon-3 { 
          box-shadow: 0 5px 15px rgba(138, 95, 65, 0.2);
          .el-icon { color: #8a5f41; }
        }
        
        .rule-icon-4 { 
          box-shadow: 0 5px 15px rgba(232, 155, 12, 0.2);
          .el-icon { color: #e89b0c; }
        }
        
        .rule-icon-5 { 
          box-shadow: 0 5px 15px rgba(99, 125, 33, 0.2);
          .el-icon { color: #637d21; }
        }
        
        .rule-title {
          font-size: 20px; // 增大标题字号
          color: #3d2c29;
          margin-bottom: 15px; // 增加下边距
          font-weight: 600;
        }
        
        .rule-desc {
          font-size: 15px; // 增大描述字号
          color: #666;
          line-height: 1.8; // 增加行高
        }
      }
    }
  }
  
  .popular-section {
    margin-bottom: 50px;
    
    .book-carousel {
      margin-bottom: 30px;
      
      .carousel-item {
        cursor: pointer;
      }
      
      .featured-book-card {
        height: 100%;
        display: flex;
        background-color: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
        
        .book-cover {
          width: 40%;
          position: relative;
          overflow: hidden;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s;
          }
          
          .book-rank {
            position: absolute;
            top: 15px;
            left: 15px;
            width: 35px;
            height: 35px;
            background: #e36049;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 8px rgba(227, 96, 73, 0.3);
            
            .rank-number {
              color: white;
              font-weight: bold;
              font-size: 16px;
            }
          }
          
          .book-rating {
            position: absolute;
            bottom: 15px;
            left: 15px;
            background-color: rgba(255, 255, 255, 0.9);
            padding: 5px 10px;
            border-radius: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }
        }
        
        .book-info {
          width: 60%;
          padding: 30px;
          display: flex;
          flex-direction: column;
          
          .book-title {
            font-size: 24px;
            color: #3d2c29;
            margin-bottom: 10px;
            font-weight: 600;
          }
          
          .book-author {
            font-size: 16px;
            color: #8a5f41;
            margin-bottom: 20px;
          }
          
          .book-desc {
            font-size: 14px;
            color: #666;
            line-height: 1.8;
            margin-bottom: 30px;
            flex-grow: 1;
          }
          
          .book-actions {
            display: flex;
            gap: 15px;
            
            .el-button {
              padding: 10px 24px;
            }
          }
        }
      }
    }
    
    .popular-books {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 25px;
      
      .book-card {
        cursor: pointer;
        perspective: 1000px;
        height: 380px;
        
        .book-inner {
          height: 100%;
          width: 100%;
          transform-style: preserve-3d;
          transition: transform 0.6s;
          border-radius: 12px;
          box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
          overflow: hidden;
          background-color: white;
          
          &:hover {
            box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
            transform: translateY(-10px);
            
            .book-cover {
              img {
                transform: scale(1.05);
              }
              
              .book-hover-info {
                opacity: 1;
                transform: translateY(0);
              }
            }
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
            transition: transform 0.5s ease;
          }
          
          .book-rank {
            position: absolute;
            top: 10px;
            left: 10px;
            width: 28px;
            height: 28px;
            background: #e36049;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 2;
            
            .rank-number {
              color: white;
              font-weight: bold;
              font-size: 14px;
            }
          }
          
          .book-hover-info {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(61, 44, 41, 0.85);
            padding: 15px;
            color: white;
            opacity: 0;
            transform: translateY(20px);
            transition: all 0.3s ease;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            height: 70%;
            
            .book-desc-preview {
              font-size: 13px;
              line-height: 1.5;
              margin-bottom: 15px;
              overflow: hidden;
              display: -webkit-box;
              -webkit-line-clamp: 5;
              -webkit-box-orient: vertical;
            }
            
            .book-actions {
              text-align: center;
              
              .el-button {
                background-color: #8a5f41;
                border-color: #8a5f41;
                
                &:hover {
                  background-color: #6e4c34;
                  border-color: #6e4c34;
                }
              }
            }
          }
        }
        
        .book-info {
          padding: 15px;
          
          .book-title {
            font-size: 16px;
            color: #3d2c29;
            margin-bottom: 5px;
            font-weight: 600;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          
          .book-author {
            font-size: 14px;
            color: #8a5f41;
            margin-bottom: 10px;
          }
          
          .book-rating {
            display: flex;
            align-items: center;
            
            .el-rate {
              height: 20px;
              line-height: 20px;
            }
            
            .rating-value {
              color: #e36049;
              font-weight: bold;
              margin-left: 5px;
            }
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
  .welcome-section {
    flex-direction: column;
    
    .welcome-content, 
    .welcome-decoration {
      width: 100%;
    }
    
    .welcome-decoration {
      height: 250px;
      padding: 20px;
      
      .brand-visual {
        .books-container {
          transform: perspective(800px) rotateX(10deg) scale(0.8);
        }
        
        .brand-name {
          .cn-name {
            font-size: 28px;
          }
          
          .en-name {
            font-size: 12px;
          }
        }
      }
    }
  }
  
  .popular-section {
    .book-carousel {
      display: none; // 在移动设备上隐藏轮播，只显示网格
    }
    
    .popular-books {
      grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
      
      .book-card {
        height: 320px;
        
        .book-cover {
          height: 220px;
        }
      }
    }
  }
}
</style>