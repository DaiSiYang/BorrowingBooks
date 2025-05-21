<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, LocationInformation, Timer, Bell, List, User, Lock, Message, Operation, Document, Download, Delete, Plus } from '@element-plus/icons-vue'

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
</script>

<template>
  <div class="settings-page">
    <el-card class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h2>系统设置</h2>
          <p>配置系统参数和运行环境</p>
        </div>
      </div>
    </el-card>

    <el-card class="settings-container">
      <el-tabs v-model="activeTab" tab-position="left" class="settings-tabs" :before-leave="() => !loading">
        <!-- 基本设置 -->
        <el-tab-pane name="basic">
          <template #label>
            <div class="tab-label">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <h3 class="section-title">基本设置</h3>
          <el-form ref="basicFormRef" :model="basicForm" :rules="basicFormRules" label-width="120px" size="default">
            <el-form-item label="系统名称:" prop="systemName">
              <el-input v-model="basicForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            
            <el-form-item label="系统副标题:" prop="systemSubtitle">
              <el-input v-model="basicForm.systemSubtitle" placeholder="请输入系统副标题" />
            </el-form-item>
            
            <el-form-item label="管理员邮箱:" prop="adminEmail">
              <el-input v-model="basicForm.adminEmail" placeholder="请输入管理员邮箱" />
            </el-form-item>
            
            <el-form-item label="联系电话:" prop="contactPhone">
              <el-input v-model="basicForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            
            <el-form-item label="图书馆地址:" prop="address">
              <el-input v-model="basicForm.address" placeholder="请输入图书馆地址" />
            </el-form-item>
            
            <el-form-item label="网站地址:" prop="websiteUrl">
              <el-input v-model="basicForm.websiteUrl" placeholder="请输入网站地址" />
            </el-form-item>
            
            <el-form-item label="系统Logo:" prop="logoUrl">
              <el-upload
                class="logo-uploader"
                action="#"
                :http-request="() => {}"
                :show-file-list="false"
                :before-upload="() => false"
              >
                <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="logo-preview" />
                <div v-else class="logo-placeholder">
                  <el-icon><Plus /></el-icon>
                </div>
                <div class="upload-tip">点击上传Logo</div>
              </el-upload>
            </el-form-item>
            
            <el-form-item label="版权信息:" prop="copyrightText">
              <el-input v-model="basicForm.copyrightText" placeholder="请输入版权信息" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm('basic')" :loading="loading">保存设置</el-button>
              <el-button @click="basicFormRef.resetFields()">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 借阅规则 -->
        <el-tab-pane name="borrowRule">
          <template #label>
            <div class="tab-label">
              <el-icon><Timer /></el-icon>
              <span>借阅规则</span>
            </div>
          </template>
          
          <h3 class="section-title">借阅规则设置</h3>
          <el-form ref="borrowRuleFormRef" :model="borrowRuleForm" :rules="borrowRuleFormRules" label-width="180px" size="default">
            <h4 class="form-subtitle">借阅期限</h4>
            
            <el-form-item label="普通图书借阅天数:" prop="normalBookPeriod">
              <el-input-number v-model="borrowRuleForm.normalBookPeriod" :min="1" :max="100" />
            </el-form-item>
            
            <el-form-item label="特殊图书借阅天数:" prop="specialBookPeriod">
              <el-input-number v-model="borrowRuleForm.specialBookPeriod" :min="1" :max="60" />
            </el-form-item>
            
            <h4 class="form-subtitle">续借规则</h4>
            
            <el-form-item label="普通图书最大续借次数:" prop="normalBookMaxRenewals">
              <el-input-number v-model="borrowRuleForm.normalBookMaxRenewals" :min="0" :max="10" />
            </el-form-item>
            
            <el-form-item label="特殊图书最大续借次数:" prop="specialBookMaxRenewals">
              <el-input-number v-model="borrowRuleForm.specialBookMaxRenewals" :min="0" :max="5" />
            </el-form-item>
            
            <el-form-item label="普通图书续借天数:" prop="normalBookRenewalDays">
              <el-input-number v-model="borrowRuleForm.normalBookRenewalDays" :min="1" :max="60" />
            </el-form-item>
            
            <el-form-item label="特殊图书续借天数:" prop="specialBookRenewalDays">
              <el-input-number v-model="borrowRuleForm.specialBookRenewalDays" :min="1" :max="30" />
            </el-form-item>
            
            <h4 class="form-subtitle">借阅限制</h4>
            
            <el-form-item label="每人最大普通图书借阅数:" prop="maxNormalBooksPerUser">
              <el-input-number v-model="borrowRuleForm.maxNormalBooksPerUser" :min="1" :max="20" />
            </el-form-item>
            
            <el-form-item label="每人最大特殊图书借阅数:" prop="maxSpecialBooksPerUser">
              <el-input-number v-model="borrowRuleForm.maxSpecialBooksPerUser" :min="0" :max="10" />
            </el-form-item>
            
            <h4 class="form-subtitle">逾期规则</h4>
            
            <el-form-item label="每天逾期费用(元):" prop="overdueFeePerDay">
              <el-input-number v-model="borrowRuleForm.overdueFeePerDay" :min="0" :max="10" :precision="2" :step="0.1" />
            </el-form-item>
            
            <el-form-item label="图书损坏赔偿倍率:" prop="damageFeeRate">
              <el-input-number v-model="borrowRuleForm.damageFeeRate" :min="1" :max="5" :precision="1" :step="0.1" />
              <span class="form-help-text">图书价格的倍数</span>
            </el-form-item>
            
            <el-form-item label="最大逾期天数限制:" prop="maxOverdueDays">
              <el-input-number v-model="borrowRuleForm.maxOverdueDays" :min="1" :max="90" />
              <span class="form-help-text">超过此天数将暂停借阅权限</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm('borrowRule')" :loading="loading">保存规则</el-button>
              <el-button @click="borrowRuleFormRef.resetFields()">重置</el-button>
            </el-form-item>
          </el-form>
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
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;

  .page-header {
    margin-bottom: 20px;
    
    .header-content {
      .header-title {
        h2 {
          font-size: 22px;
          color: #3d2c29;
          margin: 0 0 5px 0;
        }
        
        p {
          font-size: 14px;
          color: #8a5f41;
          margin: 0;
        }
      }
    }
  }
  
  .settings-container {
    .settings-tabs {
      min-height: 500px;
      
      .tab-label {
        display: flex;
        align-items: center;
        
        .el-icon {
          margin-right: 8px;
          font-size: 16px;
        }
      }
    }
  }
  
  .section-title {
    font-size: 20px;
    color: #3d2c29;
    margin: 0 0 20px 0;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .form-subtitle {
    font-size: 16px;
    color: #8a5f41;
    margin: 20px 0 15px 0;
  }
  
  .form-help-text {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
  }
  
  .logo-uploader {
    .logo-preview {
      width: 100px;
      height: 100px;
      display: block;
      object-fit: contain;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
    }
    
    .logo-placeholder {
      width: 100px;
      height: 100px;
      border: 1px dashed #dcdfe6;
      border-radius: 4px;
      color: #8c939d;
      font-size: 28px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #fbfdff;
    }
    
    .upload-tip {
      font-size: 12px;
      color: #606266;
      margin-top: 8px;
    }
  }
  
  .action-bar {
    margin-top: 20px;
    text-align: right;
  }
  
  .role-permissions-container {
    margin-top: 20px;
  }
  
  .log-actions {
    margin-bottom: 15px;
    text-align: right;
  }
  
  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .danger-zone {
    color: #f56c6c;
    font-weight: bold;
  }
  
  .warning-text {
    color: #e6a23c;
    font-size: 14px;
  }
  
  .danger-actions {
    margin-top: 15px;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .settings-page {
    padding: 10px;
    
    .settings-container {
      .settings-tabs {
        :deep(.el-tabs__header) {
          width: 100%;
        }
      }
    }
  }
}
</style> 