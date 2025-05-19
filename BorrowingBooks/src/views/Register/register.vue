<script setup>
import Backdrop from "@/components/Backdrop.vue";
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {ElNotification} from "element-plus";

const router = useRouter()

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 15, message: '长度在 3 到 15 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const registerFormRef = ref(null)

const handleRegister = () => {
  if (!registerFormRef.value) return
  
  registerFormRef.value.validate((valid) => {
    if (valid) {
      // 注册逻辑
      console.log('注册成功', registerForm)
      ElNotification({
        title: '注册成功',
        message: '请登录',
        type: 'success',
        duration: 3000
      })
      // 注册成功后跳转到登录页
       router.push({name: 'login'})
    } else {
      ElNotification({
        title: '注册失败',
        message: '请检查输入的用户名、密码和邮箱',
        type: 'error',
        duration: 3000
      })
      return false
    }
  })
}

const goToLogin = () => {
  console.log('返回登录页面')
  router.push(({name: 'login'}))
}
</script>

<template>
  <div class="register-container">
    <Backdrop/>
    <div class="register">
      <div class="register-header">
        <h1 class="system-name">青梧书径</h1>
        <p class="welcome-text">欢迎注册图书借阅管理系统</p>
      </div>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="registerForm">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            size="large"
          >
            <template #prefix>
              <i class="user-icon">👤</i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input 
            v-model="registerForm.email" 
            placeholder="请输入邮箱" 
            size="large"
          >
            <template #prefix>
              <i class="email-icon">📧</i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
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
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            placeholder="请确认密码" 
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
          <el-button type="primary" @click="handleRegister" class="register-button">注 册</el-button>
        </el-form-item>
        <div class="form-options">
          <span class="login-link" @click="goToLogin">
            <i class="option-icon">⬅️</i> 已有账号?返回登录
          </span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.register-container {
  width: 100%;
  height: 100%;
  .register{
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
    
    .register-header {
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

    .registerForm{
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
      
      .user-icon, .password-icon, .email-icon {
        font-size: 18px;
        color: #68b8d7;
        margin-right: 5px;
      }
      
      .form-options {
        display: flex;
        justify-content: center;
        margin-top: 15px;
        font-size: 14px;
        
        .login-link {
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
      
      .register-button {
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