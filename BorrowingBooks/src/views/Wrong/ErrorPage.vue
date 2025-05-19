<script setup>
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { ref, onMounted } from 'vue';

const router = useRouter();

const goToHome = () => {
  router.push({ name: 'login' });
}

// 添加动画元素
const books = ref([]);
const totalBooks = 15; // 生成的书本数量

onMounted(() => {
  // 生成随机书本
  for (let i = 0; i < totalBooks; i++) {
    books.value.push({
      id: i,
      left: Math.random() * 90 + 5 + '%',
      top: Math.random() * 90 + 5 + '%',
      rotation: Math.random() * 360 + 'deg',
      delay: Math.random() * 2 + 's',
      duration: (Math.random() * 3 + 2) + 's',
      color: getRandomColor()
    });
  }
});

// 生成随机颜色
const getRandomColor = () => {
  const colors = [
    '#68b8d7', '#183550', '#a8d4e7', '#6a8ca3', '#d0e6f0',
    '#8aa0b0', '#e0eef5', '#f0f5f8', '#c3cfe2'
  ];
  return colors[Math.floor(Math.random() * colors.length)];
}
</script>

<template>
  <div class="error-container">
    <!-- 背景动画元素 -->
    <div class="floating-books">
      <div 
        v-for="book in books" 
        :key="book.id" 
        class="book"
        :style="{
          left: book.left,
          top: book.top,
          transform: `rotate(${book.rotation})`,
          animationDelay: book.delay,
          animationDuration: book.duration,
          color: book.color
        }"
      >
        📚
      </div>
    </div>
    
    <!-- 404页面内容 -->
    <div class="error-content">
      <div class="system-name-container">
        <h1 class="system-name">青梧书径</h1>
      </div>
      
      <div class="error-code-container">
        <div class="error-code">404</div>
        <div class="error-code-shadow">404</div>
      </div>
      
      <div class="message-container">
        <p class="error-message">页面不存在</p>
        <p class="error-description">抱歉，您访问的页面不存在或已被移除</p>
      </div>
      
      <div class="action-buttons">
        <button @click="goToHome" class="home-button">
          <span class="button-icon">🏠</span>
          <span class="button-text">返回首页</span>
        </button>
      </div>
    </div>
    
    <!-- 装饰元素 -->
    <div class="decoration">
      <div class="book-shelf left"></div>
      <div class="book-shelf right"></div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.error-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
  
  // 浮动书本动画
  .floating-books {
    position: absolute;
    width: 100%;
    height: 100%;
    z-index: 1;
    
    .book {
      position: absolute;
      font-size: 24px;
      opacity: 0.4;
      animation: float 5s ease-in-out infinite;
      z-index: 1;
    }
  }
  
  // 书架装饰
  .decoration {
    position: absolute;
    width: 100%;
    height: 100%;
    z-index: 0;
    
    .book-shelf {
      position: absolute;
      height: 10px;
      width: 200px;
      background: linear-gradient(90deg, #183550, #68b8d7);
      border-radius: 5px;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      
      &.left {
        left: 5%;
        top: 30%;
        transform: rotate(-15deg);
      }
      
      &.right {
        right: 5%;
        bottom: 30%;
        transform: rotate(15deg);
      }
      
      &::after {
        content: "";
        position: absolute;
        width: 10px;
        height: 40px;
        background: #183550;
        border-radius: 5px;
        bottom: 0;
      }
      
      &.left::after {
        left: 20px;
        transform: translateY(100%);
      }
      
      &.right::after {
        right: 20px;
        transform: translateY(100%);
      }
    }
  }
  
  // 主要内容
  .error-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 2;
    padding: 40px;
    
    .system-name-container {
      margin-bottom: 20px;
      position: relative;
      
      .system-name {
        font-size: 42px;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        font-weight: bold;
        letter-spacing: 2px;
        position: relative;
        animation: glow 2s ease-in-out infinite alternate;
      }
    }
    
    .error-code-container {
      position: relative;
      margin: 30px 0;
      
      .error-code {
        font-size: 180px;
        font-weight: 900;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        letter-spacing: 5px;
        position: relative;
        z-index: 2;
        animation: pulse 4s ease-in-out infinite;
      }
      
      .error-code-shadow {
        font-size: 180px;
        font-weight: 900;
        color: rgba(24, 53, 80, 0.05);
        letter-spacing: 5px;
        position: absolute;
        top: 5px;
        left: 5px;
        z-index: 1;
      }
    }
    
    .message-container {
      text-align: center;
      margin-bottom: 40px;
      
      .error-message {
        font-size: 32px;
        color: #183550;
        margin: 10px 0;
        font-weight: bold;
        animation: fadeIn 1s ease-in-out;
      }
      
      .error-description {
        font-size: 18px;
        color: #8aa0b0;
        margin-top: 10px;
        animation: fadeIn 1.5s ease-in-out;
      }
    }
    
    .action-buttons {
      display: flex;
      justify-content: center;
      animation: fadeIn 2s ease-in-out;
      
      .home-button {
        padding: 15px 40px;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        border: none;
        border-radius: 50px;
        color: white;
        font-size: 18px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        box-shadow: 0 5px 15px rgba(24, 53, 80, 0.3);
        
        .button-icon {
          margin-right: 10px;
          font-size: 20px;
        }
        
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 20px rgba(24, 53, 80, 0.4);
        }
        
        &:active {
          transform: translateY(-2px);
        }
      }
    }
  }
}

// 动画定义
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes glow {
  from {
    text-shadow: 0 0 5px rgba(104, 184, 215, 0.2),
                 0 0 10px rgba(104, 184, 215, 0.2),
                 0 0 15px rgba(104, 184, 215, 0.2);
  }
  to {
    text-shadow: 0 0 10px rgba(104, 184, 215, 0.4),
                 0 0 20px rgba(104, 184, 215, 0.4),
                 0 0 30px rgba(104, 184, 215, 0.4);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>