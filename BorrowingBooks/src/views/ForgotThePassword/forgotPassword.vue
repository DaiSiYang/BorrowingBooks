<script setup>
import Backdrop from "@/components/Backdrop.vue";
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {ElNotification} from "element-plus";

const router = useRouter()

const resetForm = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const resetRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 6, message: '验证码长度不正确', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== resetForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const resetFormRef = ref(null)
const isCodeSent = ref(false)
const countdown = ref(0)
const timer = ref(null)

const sendVerificationCode = () => {
  // 验证邮箱
  resetFormRef.value.validateField('email', (valid) => {
    if (!valid) {
      // 发送验证码逻辑
      console.log('发送验证码到邮箱:', resetForm.email)
      isCodeSent.value = true
      countdown.value = 60
      
      // 倒计时
      timer.value = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer.value)
        }
      }, 1000)
    }
  })
}

const handleReset = () => {
  if (!resetFormRef.value) return
  
  resetFormRef.value.validate((valid) => {
    if (valid) {
      // 重置密码逻辑
      console.log('重置密码成功', resetForm)
      ElNotification({
        title: '重置成功',
        message: '请登录',
        type: 'success',
        duration: 3000
      })
      // 重置成功后跳转到登录页
     router.push({name: 'login'})
    } else {
      ElNotification({
        title: '重置失败',
        message: '请检查输入的邮箱、验证码和密码',
        type: 'error',
        duration: 3000
      })
      return false
    }
  })
}

const goToLogin = () => {
  console.log('返回登录页面')
   router.push({name: 'login'})
}
</script>

<template>
  <div class="reset-container">
    <Backdrop/>
    <div class="reset">
      <div class="reset-header">
        <h1 class="system-name">青梧书径</h1>
        <p class="welcome-text">找回密码</p>
      </div>
      <el-form ref="resetFormRef" :model="resetForm" :rules="resetRules" class="resetForm">
        <el-form-item prop="email">
          <el-input 
            v-model="resetForm.email" 
            placeholder="请输入邮箱" 
            size="large"
          >
            <template #prefix>
              <i class="email-icon">📧</i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="verificationCode">
          <div class="verification-code-container">
            <el-input 
              v-model="resetForm.verificationCode" 
              placeholder="请输入验证码" 
              size="large"
              class="verification-input"
            >
              <template #prefix>
                <i class="code-icon">🔑</i>
              </template>
            </el-input>
            <el-button 
              type="primary" 
              class="send-code-button" 
              @click="sendVerificationCode"
              :disabled="countdown > 0"
            >
              {{ countdown > 0 ? `${countdown}秒后重新发送` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input 
            v-model="resetForm.newPassword" 
            placeholder="请输入新密码" 
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
            v-model="resetForm.confirmPassword" 
            placeholder="请确认新密码" 
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
          <el-button type="primary" @click="handleReset" class="reset-button">重置密码</el-button>
        </el-form-item>
        <div class="form-options">
          <span class="login-link" @click="goToLogin">
            <i class="option-icon">⬅️</i> 返回登录
          </span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.reset-container {
  width: 100%;
  height: 100%;
  .reset{
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
    
    .reset-header {
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

    .resetForm{
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
      
      .verification-code-container {
        display: flex;
        gap: 10px;
        
        .verification-input {
          width: 230px;
          flex: 1;
        }
        
        .send-code-button {
          margin-top: 1px;
          width: 100px;
          height: 46px;
          background: linear-gradient(135deg, #68b8d7 0%, #183550 100%);
          border: none;
          border-radius: 10px;
          font-size: 14px;
          transition: all 0.3s ease;
          
          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(24, 53, 80, 0.3);
            opacity: 0.95;
          }
          
          &:disabled {
            background: linear-gradient(135deg, #a8d4e7 0%, #6a8ca3 100%);
            color: #fff;
            cursor: not-allowed;
          }
        }
      }
      
      .email-icon, .password-icon, .code-icon {
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
      
      .reset-button {
        width: 100%;
        height: 50px;
        background: linear-gradient(135deg, #183550 0%, #68b8d7 100%);
        border: none;
        border-radius: 10px;
        font-size: 18px;
        font-weight: 500;
        letter-spacing: 2px;
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