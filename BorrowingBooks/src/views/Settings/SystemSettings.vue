<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Setting, LocationInformation, Timer, Bell, List, User, Lock, 
  Message, Operation, Document, Download, Delete, Plus, Check, 
  RefreshLeft, Calendar, Refresh, Files, Warning, Phone, Link, Location, Menu
} from '@element-plus/icons-vue'

// 页面加载状态
const loading = ref(false)

// 活跃的标签页名称
const activeTab = ref('basic')

// 基本设置表单
const basicForm = reactive({
  systemName: '青梧书径图书借阅系统',
  systemSubtitle: '书籍是人类进步的阶梯，知识是改变命运的力量',
  adminEmail: 'admin@qingshu.com',
  contactPhone: '010-12345678',
  address: '北京市海淀区中关村南大街5号',
  websiteUrl: 'https://qingshu.example.com',
  logoUrl: '/logo.png',
  copyrightText: '© 2023 青梧书径图书借阅系统 版权所有'
})

// 借阅规则表单
const borrowRuleForm = reactive({
  normalBookPeriod: 30,
  specialBookPeriod: 15,
  normalBookMaxRenewals: 2,
  specialBookMaxRenewals: 1,
  normalBookRenewalDays: 15,
  specialBookRenewalDays: 7,
  maxNormalBooksPerUser: 5,
  maxSpecialBooksPerUser: 2,
  overdueFeePerDay: 0.5,
  damageFeeRate: 1.5,
  maxOverdueDays: 30
})

// 通知设置表单
const notificationForm = reactive({
  enableEmailNotification: true,
  enableSmsNotification: false,
  enableWechatNotification: true,
  sendBorrowReceipt: true,
  sendReturnReceipt: true,
  sendDueReminder: true,
  dueReminderDays: [3, 1],
  sendOverdueReminder: true,
  overdueReminderInterval: 7
})

// 权限设置数据
const rolePermissions = ref([
  {
    role: '系统管理员',
    permissions: {
      bookManage: true,
      userManage: true,
      borrowManage: true,
      statisticsView: true,
      systemSettings: true,
      logView: true,
      bookAdd: true,
      bookEdit: true,
      bookDelete: true,
      userAdd: true,
      userEdit: true,
      userDelete: true,
      userBlock: true
    }
  },
  {
    role: '图书管理员',
    permissions: {
      bookManage: true,
      userManage: false,
      borrowManage: true,
      statisticsView: true,
      systemSettings: false,
      logView: false,
      bookAdd: true,
      bookEdit: true,
      bookDelete: false,
      userAdd: false,
      userEdit: false,
      userDelete: false,
      userBlock: false
    }
  },
  {
    role: '前台操作员',
    permissions: {
      bookManage: false,
      userManage: false,
      borrowManage: true,
      statisticsView: false,
      systemSettings: false,
      logView: false,
      bookAdd: false,
      bookEdit: false,
      bookDelete: false,
      userAdd: true,
      userEdit: true,
      userDelete: false,
      userBlock: false
    }
  }
])

// 系统日志数据
const systemLogs = ref([
  { id: 1, type: 'info', user: '系统', action: '系统启动', detail: '系统服务已启动', time: '2023-06-15 08:00:00', ip: '127.0.0.1' },
  { id: 2, type: 'success', user: 'admin', action: '用户登录', detail: '管理员登录系统', time: '2023-06-15 08:05:12', ip: '192.168.1.10' },
  { id: 3, type: 'success', user: 'admin', action: '添加图书', detail: '添加图书《活着》', time: '2023-06-15 09:12:45', ip: '192.168.1.10' },
  { id: 4, type: 'warning', user: 'zhangsan', action: '借阅超期', detail: '图书《三体》借阅超期', time: '2023-06-15 10:25:36', ip: '192.168.1.15' },
  { id: 5, type: 'error', user: 'system', action: '数据备份', detail: '数据备份失败：磁盘空间不足', time: '2023-06-15 23:00:01', ip: '127.0.0.1' },
  { id: 6, type: 'info', user: 'system', action: '系统维护', detail: '系统定时维护开始', time: '2023-06-16 01:00:00', ip: '127.0.0.1' },
  { id: 7, type: 'success', user: 'lisi', action: '用户注册', detail: '新用户注册成功', time: '2023-06-16 09:42:18', ip: '192.168.1.25' },
  { id: 8, type: 'warning', user: 'admin', action: '图书删除', detail: '删除图书《JavaScript高级程序设计》', time: '2023-06-16 11:34:56', ip: '192.168.1.10' }
])

// 表单验证规则
const basicFormRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在2到30个字符之间', trigger: 'blur' }
  ],
  adminEmail: [
    { required: true, message: '请输入管理员邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const borrowRuleFormRules = {
  normalBookPeriod: [
    { required: true, message: '请输入普通图书借阅天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '请输入1-100之间的数字', trigger: 'blur' }
  ],
  specialBookPeriod: [
    { required: true, message: '请输入特殊图书借阅天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 60, message: '请输入1-60之间的数字', trigger: 'blur' }
  ],
  overdueFeePerDay: [
    { required: true, message: '请输入逾期费用', trigger: 'blur' },
    { type: 'number', min: 0, message: '请输入大于等于0的数字', trigger: 'blur' }
  ]
}

// 表单引用
const basicFormRef = ref(null)
const borrowRuleFormRef = ref(null)
const notificationFormRef = ref(null)

// 提交表单
const submitForm = async (formName) => {
  loading.value = true
  
  try {
    let formRef = null
    let successMessage = ''
    
    switch(formName) {
      case 'basic':
        formRef = basicFormRef.value
        successMessage = '基本设置已保存'
        break
      case 'borrowRule':
        formRef = borrowRuleFormRef.value
        successMessage = '借阅规则已更新'
        break
      case 'notification':
        formRef = notificationFormRef.value
        successMessage = '通知设置已更新'
        break
    }
    
    if (formRef) {
      await formRef.validate()
      
      // 模拟异步保存
      setTimeout(() => {
        loading.value = false
        ElMessage({
          type: 'success',
          message: successMessage
        })
      }, 800)
    } else {
      loading.value = false
    }
  } catch (error) {
    loading.value = false
    console.error('表单验证失败:', error)
  }
}

// 保存权限设置
const savePermissions = () => {
  loading.value = true
  
  // 模拟异步保存
  setTimeout(() => {
    loading.value = false
    ElMessage({
      type: 'success',
      message: '权限设置已更新'
    })
  }, 800)
}

// 清空系统日志
const clearSystemLogs = () => {
  ElMessageBox.confirm(
    '您确定要清空所有系统日志吗？此操作不可恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      systemLogs.value = []
      ElMessage({
        type: 'success',
        message: '系统日志已清空',
      })
    })
    .catch(() => {
      // 取消操作
    })
}

// 导出系统日志
const exportSystemLogs = () => {
  ElMessage({
    message: '系统日志导出成功',
    type: 'success'
  })
}

// 系统备份
const backupSystem = () => {
  loading.value = true
  
  // 模拟异步操作
  setTimeout(() => {
    loading.value = false
    ElMessage({
      message: '系统备份已完成',
      type: 'success'
    })
  }, 1500)
}

// 恢复出厂设置
const resetSystem = () => {
  ElMessageBox.confirm(
    '您确定要将系统恢复到出厂设置吗？此操作将清除所有自定义配置。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      loading.value = true
      
      // 模拟异步操作
      setTimeout(() => {
        loading.value = false
        ElMessage({
          type: 'success',
          message: '系统已恢复到出厂设置',
        })
      }, 2000)
    })
    .catch(() => {
      // 取消操作
    })
}

// 保存所有设置
const saveAllSettings = async () => {
  loading.value = true
  
  try {
    // 模拟异步保存
    setTimeout(() => {
      loading.value = false
      ElMessage({
        type: 'success',
        message: '所有设置已保存成功',
        duration: 2000,
      })
    }, 1500)
  } catch (error) {
    loading.value = false
    console.error('保存失败:', error)
  }
}
</script>

<template>
  <div class="settings-page">
    <el-card class="page-header" shadow="hover">
      <div class="header-content">
        <div class="header-title">
          <h2><el-icon><Setting /></el-icon> 系统设置</h2>
          <p>配置系统参数和运行环境</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="saveAllSettings" :loading="loading">
            <el-icon><Check /></el-icon>保存全部设置
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="settings-container" shadow="hover">
      <el-tabs v-model="activeTab" type="border-card" class="settings-tabs" :before-leave="() => !loading">
        <!-- 基本设置 -->
        <el-tab-pane name="basic">
          <template #label>
            <div class="tab-label">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <div class="tab-content">
            <div class="section-header">
              <h3 class="section-title">基本系统信息</h3>
              <p class="section-desc">设置系统的基本信息，这些信息将显示在前台和管理界面</p>
            </div>
            
            <el-divider content-position="left">基础信息</el-divider>
            
            <el-form ref="basicFormRef" :model="basicForm" :rules="basicFormRules" label-position="top" size="default" class="settings-form">
              <el-row :gutter="30">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="系统名称" prop="systemName">
                    <el-input 
                      v-model="basicForm.systemName" 
                      placeholder="请输入系统名称" 
                      prefix-icon="Menu"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                  
                <el-col :xs="24" :sm="12">
                  <el-form-item label="系统副标题" prop="systemSubtitle">
                    <el-input 
                      v-model="basicForm.systemSubtitle" 
                      placeholder="请输入系统副标题"
                      prefix-icon="Document"
                      clearable
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-divider content-position="left">联系方式</el-divider>
              
              <el-row :gutter="30">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="管理员邮箱" prop="adminEmail">
                    <el-input 
                      v-model="basicForm.adminEmail" 
                      placeholder="请输入管理员邮箱" 
                      prefix-icon="Message"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                  
                <el-col :xs="24" :sm="12">
                  <el-form-item label="联系电话" prop="contactPhone">
                    <el-input 
                      v-model="basicForm.contactPhone" 
                      placeholder="请输入联系电话" 
                      prefix-icon="Phone"
                      clearable
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="30">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="图书馆地址" prop="address">
                    <el-input 
                      v-model="basicForm.address" 
                      placeholder="请输入图书馆地址" 
                      prefix-icon="Location"
                      clearable
                    />
                  </el-form-item>
                </el-col>
                  
                <el-col :xs="24" :sm="12">
                  <el-form-item label="网站地址" prop="websiteUrl">
                    <el-input 
                      v-model="basicForm.websiteUrl" 
                      placeholder="请输入网站地址" 
                      prefix-icon="Link"
                      clearable
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-divider content-position="left">品牌设置</el-divider>
              
              <el-row :gutter="30">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="系统Logo">
                    <div class="logo-upload-container">
                      <el-upload
                        class="logo-uploader"
                        action="#"
                        :http-request="() => {}"
                        :show-file-list="false"
                        :before-upload="() => false"
                        drag
                      >
                        <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="logo-preview" />
                        <div v-else class="logo-placeholder">
                          <el-icon class="upload-icon"><Plus /></el-icon>
                          <div class="upload-text">点击或拖拽上传Logo</div>
                        </div>
                      </el-upload>
                    </div>
                  </el-form-item>
                </el-col>
                  
                <el-col :xs="24" :sm="12">
                  <el-form-item label="版权信息" prop="copyrightText">
                    <el-input 
                      v-model="basicForm.copyrightText" 
                      placeholder="请输入版权信息" 
                      clearable
                    />
                    <div class="form-tip">显示在页面底部的版权声明文字</div>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <div class="form-actions">
                <el-button plain @click="basicFormRef.resetFields()">
                  <el-icon><RefreshLeft /></el-icon>重置
                </el-button>
                <el-button type="primary" @click="submitForm('basic')" :loading="loading">
                  <el-icon><Check /></el-icon>保存设置
                </el-button>
              </div>
            </el-form>
          </div>
        </el-tab-pane>
        
        <!-- 借阅规则 -->
        <el-tab-pane name="borrowRule">
          <template #label>
            <div class="tab-label">
              <el-icon><Timer /></el-icon>
              <span>借阅规则</span>
            </div>
          </template>
          
          <div class="tab-content">
            <div class="section-header">
              <h3 class="section-title">借阅规则设置</h3>
              <p class="section-desc">配置图书借阅相关的规则、限制和费用标准</p>
            </div>
            
            <el-form ref="borrowRuleFormRef" :model="borrowRuleForm" :rules="borrowRuleFormRules" label-position="top" size="default" class="settings-form">
              <el-collapse accordion>
                <el-collapse-item name="1">
                  <template #title>
                    <div class="collapse-title">
                      <el-icon><Calendar /></el-icon>
                      <span>借阅期限</span>
                    </div>
                  </template>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="普通图书借阅天数" prop="normalBookPeriod">
                        <el-input-number
                          v-model="borrowRuleForm.normalBookPeriod"
                          :min="1"
                          :max="100"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">读者可借阅普通图书的最长天数</div>
                      </el-form-item>
                    </el-col>
                    
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="特殊图书借阅天数" prop="specialBookPeriod">
                        <el-input-number
                          v-model="borrowRuleForm.specialBookPeriod"
                          :min="1"
                          :max="60"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">读者可借阅特殊图书（如珍本）的最长天数</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-collapse-item>
                
                <el-collapse-item name="2">
                  <template #title>
                    <div class="collapse-title">
                      <el-icon><Refresh /></el-icon>
                      <span>续借规则</span>
                    </div>
                  </template>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="普通图书最大续借次数" prop="normalBookMaxRenewals">
                        <el-input-number
                          v-model="borrowRuleForm.normalBookMaxRenewals"
                          :min="0"
                          :max="10"
                          controls-position="right"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="特殊图书最大续借次数" prop="specialBookMaxRenewals">
                        <el-input-number
                          v-model="borrowRuleForm.specialBookMaxRenewals"
                          :min="0"
                          :max="5"
                          controls-position="right"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="普通图书续借天数" prop="normalBookRenewalDays">
                        <el-input-number
                          v-model="borrowRuleForm.normalBookRenewalDays"
                          :min="1"
                          :max="60"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">每次续借可延长的天数</div>
                      </el-form-item>
                    </el-col>
                    
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="特殊图书续借天数" prop="specialBookRenewalDays">
                        <el-input-number
                          v-model="borrowRuleForm.specialBookRenewalDays"
                          :min="1"
                          :max="30"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">每次续借可延长的天数</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-collapse-item>
                
                <el-collapse-item name="3">
                  <template #title>
                    <div class="collapse-title">
                      <el-icon><Files /></el-icon>
                      <span>借阅限制</span>
                    </div>
                  </template>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="每人最大普通图书借阅数" prop="maxNormalBooksPerUser">
                        <el-input-number
                          v-model="borrowRuleForm.maxNormalBooksPerUser"
                          :min="1"
                          :max="20"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">每个用户可同时借阅的普通图书最大数量</div>
                      </el-form-item>
                    </el-col>
                    
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="每人最大特殊图书借阅数" prop="maxSpecialBooksPerUser">
                        <el-input-number
                          v-model="borrowRuleForm.maxSpecialBooksPerUser"
                          :min="0"
                          :max="10"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">每个用户可同时借阅的特殊图书最大数量</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-collapse-item>
                
                <el-collapse-item name="4">
                  <template #title>
                    <div class="collapse-title">
                      <el-icon><Warning /></el-icon>
                      <span>逾期规则</span>
                    </div>
                  </template>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="每天逾期费用(元)" prop="overdueFeePerDay">
                        <el-input-number
                          v-model="borrowRuleForm.overdueFeePerDay"
                          :min="0"
                          :max="10"
                          :precision="2"
                          :step="0.1"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">逾期归还每天收取的费用</div>
                      </el-form-item>
                    </el-col>
                    
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="图书损坏赔偿倍率" prop="damageFeeRate">
                        <el-input-number
                          v-model="borrowRuleForm.damageFeeRate"
                          :min="1"
                          :max="5"
                          :precision="1"
                          :step="0.1"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">图书损坏时按原价的倍数赔偿</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  
                  <el-row :gutter="30">
                    <el-col :xs="24" :sm="12">
                      <el-form-item label="最大逾期天数限制" prop="maxOverdueDays">
                        <el-input-number
                          v-model="borrowRuleForm.maxOverdueDays"
                          :min="1"
                          :max="90"
                          controls-position="right"
                          style="width: 100%"
                        />
                        <div class="form-tip">超过此天数将暂停用户的借阅权限</div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-collapse-item>
              </el-collapse>
              
              <div class="form-actions">
                <el-button plain @click="borrowRuleFormRef.resetFields()">
                  <el-icon><RefreshLeft /></el-icon>重置
                </el-button>
                <el-button type="primary" @click="submitForm('borrowRule')" :loading="loading">
                  <el-icon><Check /></el-icon>保存规则
                </el-button>
              </div>
            </el-form>
          </div>
        </el-tab-pane>
        
        <!-- 通知设置 -->
        <el-tab-pane name="notification">
          <template #label>
            <div class="tab-label">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </div>
          </template>
          
          <h3 class="section-title">通知设置</h3>
          <el-form ref="notificationFormRef" :model="notificationForm" label-width="180px" size="default">
            <h4 class="form-subtitle">通知方式</h4>
            
            <el-form-item label="启用邮件通知:">
              <el-switch v-model="notificationForm.enableEmailNotification" />
            </el-form-item>
            
            <el-form-item label="启用短信通知:">
              <el-switch v-model="notificationForm.enableSmsNotification" />
            </el-form-item>
            
            <el-form-item label="启用微信通知:">
              <el-switch v-model="notificationForm.enableWechatNotification" />
            </el-form-item>
            
            <h4 class="form-subtitle">通知事件</h4>
            
            <el-form-item label="发送借阅确认:">
              <el-switch v-model="notificationForm.sendBorrowReceipt" />
              <span class="form-help-text">借书成功后通知用户</span>
            </el-form-item>
            
            <el-form-item label="发送归还确认:">
              <el-switch v-model="notificationForm.sendReturnReceipt" />
              <span class="form-help-text">还书成功后通知用户</span>
            </el-form-item>
            
            <el-form-item label="发送到期提醒:">
              <el-switch v-model="notificationForm.sendDueReminder" />
            </el-form-item>
            
            <el-form-item label="到期提前提醒天数:" v-if="notificationForm.sendDueReminder">
              <el-select v-model="notificationForm.dueReminderDays" multiple placeholder="选择提醒天数" style="width: 100%">
                <el-option :value="1" label="提前1天" />
                <el-option :value="3" label="提前3天" />
                <el-option :value="7" label="提前7天" />
              </el-select>
              <span class="form-help-text">借阅到期前多少天发送提醒</span>
            </el-form-item>
            
            <el-form-item label="发送逾期通知:">
              <el-switch v-model="notificationForm.sendOverdueReminder" />
            </el-form-item>
            
            <el-form-item label="逾期通知间隔(天):" v-if="notificationForm.sendOverdueReminder">
              <el-input-number v-model="notificationForm.overdueReminderInterval" :min="1" :max="30" />
              <span class="form-help-text">每隔多少天发送一次逾期通知</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm('notification')" :loading="loading">保存设置</el-button>
              <el-button @click="notificationFormRef.resetFields()">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 权限设置 -->
        <el-tab-pane name="permissions">
          <template #label>
            <div class="tab-label">
              <el-icon><Lock /></el-icon>
              <span>权限设置</span>
            </div>
          </template>
          
          <h3 class="section-title">用户权限设置</h3>
          <div class="role-permissions-container">
            <el-table :data="rolePermissions" border style="width: 100%">
              <el-table-column prop="role" label="角色" width="150" />
              
              <el-table-column label="图书管理" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.bookManage" />
                </template>
              </el-table-column>
              
              <el-table-column label="用户管理" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.userManage" />
                </template>
              </el-table-column>
              
              <el-table-column label="借阅管理" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.borrowManage" />
                </template>
              </el-table-column>
              
              <el-table-column label="统计查看" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.statisticsView" />
                </template>
              </el-table-column>
              
              <el-table-column label="系统设置" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.systemSettings" />
                </template>
              </el-table-column>
              
              <el-table-column label="日志查看" width="100" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.permissions.logView" />
                </template>
              </el-table-column>
            </el-table>
            
            <div class="action-bar">
              <el-button type="primary" @click="savePermissions" :loading="loading">保存权限</el-button>
              <el-button @click="rolePermissions = JSON.parse(JSON.stringify(rolePermissions))">重置</el-button>
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 系统日志 -->
        <el-tab-pane name="logs">
          <template #label>
            <div class="tab-label">
              <el-icon><Document /></el-icon>
              <span>系统日志</span>
            </div>
          </template>
          
          <h3 class="section-title">系统日志</h3>
          <div class="log-actions">
            <el-button type="primary" @click="exportSystemLogs" plain size="small">
              <el-icon><Download /></el-icon>导出日志
            </el-button>
            <el-button type="danger" @click="clearSystemLogs" plain size="small">
              <el-icon><Delete /></el-icon>清空日志
            </el-button>
          </div>
          
          <el-table :data="systemLogs" style="width: 100%" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="type" label="类型" width="80">
              <template #default="scope">
                <el-tag 
                  :type="
                    scope.row.type === 'success' ? 'success' :
                    scope.row.type === 'warning' ? 'warning' :
                    scope.row.type === 'error' ? 'danger' : 'info'
                  "
                  size="small"
                >
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="user" label="用户" width="100" />
            <el-table-column prop="action" label="操作" width="120" />
            <el-table-column prop="detail" label="详情" show-overflow-tooltip />
            <el-table-column prop="ip" label="IP地址" width="120" />
            <el-table-column prop="time" label="时间" width="160" />
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="50"
              :page-size="8"
            />
          </div>
        </el-tab-pane>
        
        <!-- 系统维护 -->
        <el-tab-pane name="maintenance">
          <template #label>
            <div class="tab-label">
              <el-icon><Operation /></el-icon>
              <span>系统维护</span>
            </div>
          </template>
          
          <h3 class="section-title">系统维护</h3>
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>数据备份</span>
              </div>
            </template>
            <p>对系统数据进行备份，包括用户信息、图书信息、借阅记录等全部数据。</p>
            <el-button type="primary" @click="backupSystem" :loading="loading">立即备份</el-button>
          </el-card>
          
          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>计划任务</span>
              </div>
            </template>
            <el-table :data="[
              { name: '数据库备份', schedule: '每日 01:00', status: true },
              { name: '系统自诊断', schedule: '每周一 04:00', status: true },
              { name: '过期数据清理', schedule: '每月1日 02:00', status: false }
            ]" style="width: 100%">
              <el-table-column prop="name" label="任务名称" />
              <el-table-column prop="schedule" label="执行计划" />
              <el-table-column label="状态" align="center">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" />
                </template>
              </el-table-column>
            </el-table>
          </el-card>
          
          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span class="danger-zone">危险区域</span>
              </div>
            </template>
            <p class="warning-text">以下操作将对系统数据进行不可逆更改，请谨慎操作。</p>
            <div class="danger-actions">
              <el-button type="danger" @click="resetSystem" plain>恢复出厂设置</el-button>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <el-backtop :right="20" :bottom="20" />
  </div>
</template>

<style scoped lang="scss">
.settings-page {
  padding: 24px;
  background-color: #f9f6f2;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    background-color: #fff;
    border: none;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-title {
        h2 {
          font-size: 24px;
          color: #3d2c29;
          margin: 0 0 5px 0;
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 8px;
            color: #8a5f41;
          }
        }
        
        p {
          font-size: 14px;
          color: #8a5f41;
          margin: 0;
        }
      }
      
      .header-actions {
        .el-button {
          padding: 12px 24px;
          border-radius: 8px;
        }
      }
    }
  }
  
  .settings-container {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    background-color: #fff;
    border: none;
    
    .settings-tabs {
      min-height: 600px;
      
      :deep(.el-tabs__header) {
        background-color: #f5f0e8;
      }
      
      :deep(.el-tabs__item) {
        height: 56px;
        line-height: 56px;
        
        &.is-active {
          color: #8a5f41;
          font-weight: 600;
        }
        
        &:hover {
          color: #8a5f41;
        }
      }
      
      .tab-label {
        display: flex;
        align-items: center;
        
        .el-icon {
          margin-right: 8px;
          font-size: 18px;
        }
      }
      
      .tab-content {
        padding: 20px;
      }
    }
  }
  
  .section-header {
    margin-bottom: 24px;
    
    .section-title {
      font-size: 20px;
      color: #3d2c29;
      margin: 0 0 8px 0;
    }
    
    .section-desc {
      font-size: 14px;
      color: #8a5f41;
      margin: 0;
    }
  }
  
  .settings-form {
    max-width: 1200px;
    
    .el-form-item {
      margin-bottom: 24px;
      
      :deep(.el-form-item__label) {
        color: #3d2c29;
        font-weight: 500;
        margin-bottom: 8px;
      }
      
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;
        
        &:hover, &.is-focus {
          box-shadow: 0 0 0 1px #8a5f41 inset;
        }
      }
      
      .form-tip {
        font-size: 12px;
        color: #8a5f41;
        margin-top: 4px;
      }
    }
  }
  
  .form-subtitle {
    font-size: 16px;
    color: #8a5f41;
    margin: 24px 0 16px 0;
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 32px;
    padding-top: 20px;
    border-top: 1px dashed rgba(138, 95, 65, 0.2);
    gap: 12px;
    
    .el-button {
      padding: 12px 24px;
      border-radius: 8px;
      
      &:hover {
        transform: translateY(-2px);
        transition: transform 0.3s;
      }
    }
  }
  
  .logo-upload-container {
    .logo-uploader {
      :deep(.el-upload) {
        border-radius: 8px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: all 0.3s;
        
        &:hover {
          border-color: #8a5f41;
        }
      }
      
      :deep(.el-upload-dragger) {
        width: 100%;
        height: auto;
        background-color: #f9f6f2;
        border: 1px dashed rgba(138, 95, 65, 0.3);
        border-radius: 8px;
        
        &:hover {
          border-color: #8a5f41;
          background-color: #f5f0e8;
        }
      }
      
      .logo-preview {
        width: 120px;
        height: 120px;
        display: block;
        object-fit: contain;
      }
      
      .logo-placeholder {
        width: 100%;
        padding: 30px 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        
        .upload-icon {
          font-size: 40px;
          color: #8a5f41;
          margin-bottom: 16px;
        }
        
        .upload-text {
          font-size: 14px;
          color: #8a5f41;
        }
      }
    }
  }
  
  .collapse-title {
    display: flex;
    align-items: center;
    
    .el-icon {
      margin-right: 8px;
      color: #8a5f41;
    }
    
    span {
      font-weight: 500;
    }
  }
  
  :deep(.el-collapse-item__header) {
    padding: 0 16px;
    font-size: 16px;
    color: #3d2c29;
    height: 56px;
    line-height: 56px;
    
    &.is-active {
      color: #8a5f41;
    }
  }
  
  :deep(.el-collapse-item__content) {
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 0 0 8px 8px;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .settings-page {
    padding: 16px;
    
    .page-header {
      .header-content {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
        
        .header-actions {
          width: 100%;
          
          .el-button {
            width: 100%;
          }
        }
      }
    }
    
    .settings-container {
      .settings-tabs {
        :deep(.el-tabs__header) {
          width: 100%;
        }
      }
    }
    
    .form-actions {
      flex-direction: column;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style> 