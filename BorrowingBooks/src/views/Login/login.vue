<script setup>
import Backdrop from "@/components/Backdrop.vue";
import { reactive, ref } from 'vue'
import {useRouter} from "vue-router";
import {ElNotification} from "element-plus";

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 15, message: '长度在 3 到 15 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符之间', trigger: 'blur' }
  ]
}

const loginFormRef = ref(null)

const handleLogin = () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate((valid) => {
    if (valid) {
      // 登录逻辑
      console.log('登录成功', loginForm)
      ElNotification({
        title: '登录成功',
        message: '欢迎回来！',
        type: 'success',
        duration: 3000
      })
    } else {
      ElNotification({
        title: '登录失败',
        message: '请检查输入的用户名和密码',
        type: 'error',
        duration: 3000
      })
      return false
    }
  })
}

// 注册和找回密码的处理函数
const goToRegister = () => {
  console.log('前往注册页面')
  // 这里添加路由跳转逻辑
  router.push({name: 'register'})
}

const forgotPassword = () => {
  console.log('找回密码')
  // 这里添加找回密码逻辑
  router.push({name: 'forgetPassword'})
}
</script>

<template>
  <div class="login-container">
    <Backdrop/>
    <div class="login">
      <div class="login-header">
        <h1 class="system-name">青梧书径</h1>
        <p class="welcome-text">欢迎使用图书借阅管理系统</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="loginForm">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            size="large"
          >
            <template #prefix>
              <i class="user-icon">👤</i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            type="password" 
            show-password
            size="large"
          >
            <template #prefix>
              <i class="password-icon">🔒</i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-button">登 录</el-button>
        </el-form-item>
        <div class="form-options">
          <span class="forgot-password" @click="forgotPassword">
            <i class="option-icon">📝</i> 忘记密码
          </span>
          <span class="register-link" @click="goToRegister">
            <i class="option-icon">🔑</i> 还没有账号?去注册
          </span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100%;
  .login{
    z-index: 999;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 400px;
    background-color: rgba(255, 255, 255, 0.6);
    border-radius: 15px;
    box-shadow: 0 8px 20px rgba(24, 53, 80, 0.3);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 40px 30px;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    
    .login-header {
      text-align: center;
      margin-bottom: 30px;
      
      .system-name {
        font-size: 38px;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 15px;
        font-weight: bold;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        letter-spacing: 2px;
      }
      
      .welcome-text {
        font-size: 18px;
        color: #183550;
        position: relative;
        display: inline-block;
        padding: 0 10px;
        
        &:before, &:after {
          content: "";
          position: absolute;
          height: 1px;
          width: 30px;
          background: linear-gradient(90deg, transparent, #68b8d7, transparent);
          top: 50%;
        }
        
        &:before {
          left: -35px;
        }
        
        &:after {
          right: -35px;
        }
      }
    }

    .loginForm{
      width: 100%;
      margin-top: 10px;
      
      :deep(.el-form-item) {
        margin-bottom: 20px;
      }
      
      :deep(.el-input) {
        height: 50px;
        
        .el-input__wrapper {
          background-color: rgba(255, 255, 255, 0.7);
          border-radius: 10px;
          box-shadow: 0 0 0 1px rgba(104, 184, 215, 0.2);
          padding: 0 15px;
          transition: all 0.3s ease;
          
          &:hover, &.is-focus {
            box-shadow: 0 0 0 1px rgba(104, 184, 215, 0.5);
          }
          
          &.is-focus {
            background-color: rgba(255, 255, 255, 0.9);
          }
        }
        
        .el-input__inner {
          height: 50px;
          color: #183550;
          font-size: 16px;
          
          &::placeholder {
            color: #8aa0b0;
          }
        }
      }
      
      .user-icon, .password-icon {
        font-size: 18px;
        color: #68b8d7;
        margin-right: 5px; /* 减小图标与文字的距离 */
      }
      
      .form-options {
        display: flex;
        justify-content: space-between;
        margin-top: 15px;
        font-size: 14px;
        
        .register-link, .forgot-password {
          color: #68b8d7;
          cursor: pointer;
          transition: all 0.3s ease;
          display: flex;
          align-items: center;
          
          &:hover {
            color: #183550;
            transform: translateY(-1px);
          }
          
          .option-icon {
            margin-right: 5px;
            font-size: 16px;
          }
        }
      }
      
      .login-button {
        width: 100%;
        height: 50px;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        border: none;
        border-radius: 10px;
        font-size: 18px;
        font-weight: 500;
        letter-spacing: 5px;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 5px 15px rgba(24, 53, 80, 0.3);
          opacity: 0.95;
        }
        
        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}
</style>