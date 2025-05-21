<template>
  <div class="user-import-export-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">用户导入导出</h2>
    </div>

    <div class="page-content">
      <!-- 导入区域 -->
      <el-card class="import-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="header-title">批量导入用户</div>
          </div>
        </template>
        
        <div class="import-area">
          <div class="upload-area" :class="{ 'is-dragover': isDragover }" @drop.prevent="handleDrop" @dragover.prevent="dragover" @dragleave.prevent="dragleave">
            <template v-if="uploadFile">
              <div class="file-selected">
                <el-icon class="file-icon"><Document /></el-icon>
                <span class="file-name">{{ uploadFile.name }}</span>
                <div class="file-actions">
                  <el-button size="small" type="primary" @click="handleUpload" :loading="uploading" class="upload-button">
                    上传文件
                  </el-button>
                  <el-button size="small" @click="resetUpload" class="reset-button">
                    重新选择
                  </el-button>
                </div>
              </div>
            </template>
            <template v-else>
              <input 
                ref="fileInput"
                type="file" 
                accept=".xlsx,.xls" 
                class="file-input" 
                @change="handleFileChange" 
              />
              <el-icon class="upload-icon"><Upload /></el-icon>
              <div class="upload-text">
                <span>将文件拖到此处，或 <em>点击上传</em></span>
                <p>支持 .xlsx, .xls 格式的 Excel 文件</p>
              </div>
            </template>
          </div>
          
          <div class="import-guide">
            <div class="guide-title">
              <el-icon><InfoFilled /></el-icon>
              <span>导入说明</span>
            </div>
            <div class="guide-content">
              <p>1. 请先下载<el-link type="primary" @click="downloadTemplate" class="template-link">导入模板</el-link>，按照模板格式填写用户信息</p>
              <p>2. Excel 文件大小不超过 5MB</p>
              <p>3. 导入时，系统将检查用户名是否已存在，若存在则不会导入</p>
              <p>4. 密码将通过加密存储，不会明文保存</p>
              <p>5. 角色必须是系统中已有的角色（管理员、操作员、普通用户）</p>
            </div>
          </div>
        </div>
        
        <!-- 导入结果 -->
        <div class="import-result" v-if="importResult.show">
          <div class="result-header" :class="importResult.success ? 'success-header' : 'error-header'">
            <el-icon v-if="importResult.success"><CircleCheckFilled /></el-icon>
            <el-icon v-else><CircleCloseFilled /></el-icon>
            <span>{{ importResult.message }}</span>
          </div>
          
          <div class="result-summary">
            <div class="success-count">
              <span class="label">成功:</span>
              <span class="value">{{ importResult.successCount }}</span>
            </div>
            <div class="failed-count">
              <span class="label">失败:</span>
              <span class="value">{{ importResult.failedCount }}</span>
            </div>
            <div class="total-count">
              <span class="label">总计:</span>
              <span class="value">{{ importResult.totalCount }}</span>
            </div>
          </div>
          
          <div class="result-details" v-if="importResult.errors && importResult.errors.length > 0">
            <div class="details-title">错误详情:</div>
            <el-table :data="importResult.errors" style="width: 100%" border stripe>
              <el-table-column prop="row" label="行号" width="80" />
              <el-table-column prop="field" label="字段" width="120" />
              <el-table-column prop="message" label="错误原因" min-width="200" />
              <el-table-column prop="value" label="错误值" width="150" />
            </el-table>
          </div>
          
          <div class="result-actions">
            <el-button type="primary" @click="resetImport" class="reset-import-button">
              重新导入
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 导出区域 -->
      <el-card class="export-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><Download /></el-icon>
            </div>
            <div class="header-title">批量导出用户</div>
          </div>
        </template>
        
        <div class="export-area">
          <div class="export-form">
            <el-form :model="exportForm" label-width="100px">
              <el-form-item label="导出范围">
                <el-radio-group v-model="exportForm.scope">
                  <el-radio-button label="all">所有用户</el-radio-button>
                  <el-radio-button label="filtered">筛选后的用户</el-radio-button>
                </el-radio-group>
              </el-form-item>
              
              <template v-if="exportForm.scope === 'filtered'">
                <el-form-item label="用户角色">
                  <el-select 
                    v-model="exportForm.role" 
                    placeholder="选择角色" 
                    clearable 
                    class="filter-select"
                  >
                    <el-option 
                      v-for="item in roleOptions" 
                      :key="item.value" 
                      :label="item.label" 
                      :value="item.value" 
                    />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="用户状态">
                  <el-select 
                    v-model="exportForm.status" 
                    placeholder="选择状态" 
                    clearable 
                    class="filter-select"
                  >
                    <el-option 
                      v-for="item in statusOptions" 
                      :key="item.value" 
                      :label="item.label" 
                      :value="item.value" 
                    />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="创建时间">
                  <el-date-picker
                    v-model="exportForm.dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    class="date-range-picker"
                  />
                </el-form-item>
                
                <el-form-item label="关键词">
                  <el-input
                    v-model="exportForm.keyword"
                    placeholder="用户名/昵称/手机号"
                    clearable
                    class="keyword-input"
                  />
                </el-form-item>
              </template>
              
              <el-form-item label="文件格式">
                <el-radio-group v-model="exportForm.format">
                  <el-radio label="xlsx">XLSX</el-radio>
                  <el-radio label="csv">CSV</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="导出字段" class="export-fields-item">
                <el-checkbox-group v-model="exportForm.fields">
                  <el-checkbox label="username">用户名</el-checkbox>
                  <el-checkbox label="nickname">昵称</el-checkbox>
                  <el-checkbox label="phone">手机号</el-checkbox>
                  <el-checkbox label="email">邮箱</el-checkbox>
                  <el-checkbox label="role">角色</el-checkbox>
                  <el-checkbox label="status">状态</el-checkbox>
                  <el-checkbox label="createTime">创建时间</el-checkbox>
                  <el-checkbox label="lastLogin">最后登录</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              
              <el-form-item>
                <div class="export-actions">
                  <el-button type="primary" @click="handleExport" :loading="exporting" class="export-button">
                    <el-icon><Download /></el-icon>导出用户
                  </el-button>
                  <el-button @click="resetExport" class="reset-export-button">
                    重置选项
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
          
          <div class="export-preview">
            <div class="preview-title">导出预览</div>
            <div class="preview-content">
              <div class="preview-info">
                <div class="preview-count">
                  预计导出 <span class="highlight">{{ exportCount }}</span> 条用户数据
                </div>
                <div class="preview-fields">
                  包含字段：
                  <el-tag 
                    v-for="field in exportForm.fields" 
                    :key="field" 
                    class="field-tag"
                  >
                    {{ getFieldLabel(field) }}
                  </el-tag>
                </div>
              </div>
              <div class="preview-table">
                <el-table
                  :data="previewData"
                  style="width: 100%"
                  border
                  size="small"
                  :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
                >
                  <el-table-column 
                    v-for="field in exportForm.fields"
                    :key="field"
                    :prop="field"
                    :label="getFieldLabel(field)"
                    min-width="100"
                  >
                    <template v-if="field === 'status'" #default="scope">
                      <el-tag
                        :type="scope.row.status === 1 ? 'success' : 'danger'"
                        size="small"
                      >
                        {{ scope.row.status === 1 ? '启用' : '禁用' }}
                      </el-tag>
                    </template>
                    
                    <template v-else-if="field === 'role'" #default="scope">
                      <el-tag
                        :type="getRoleTagType(scope.row.role)"
                        size="small"
                        effect="light"
                      >
                        {{ getRoleLabel(scope.row.role) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { 
  Upload, Download, Document, InfoFilled, 
  CircleCheckFilled, CircleCloseFilled 
} from '@element-plus/icons-vue'

// 角色选项
const roleOptions = [
  { value: 'admin', label: '管理员' },
  { value: 'operator', label: '操作员' },
  { value: 'user', label: '普通用户' }
]

// 状态选项
const statusOptions = [
  { value: 1, label: '启用' },
  { value: 0, label: '禁用' }
]

// 文件上传相关
const fileInput = ref(null)
const uploadFile = ref(null)
const isDragover = ref(false)
const uploading = ref(false)

// 导入结果
const importResult = reactive({
  show: false,
  success: false,
  message: '',
  successCount: 0,
  failedCount: 0,
  totalCount: 0,
  errors: []
})

// 导出表单
const exportForm = reactive({
  scope: 'all',
  role: '',
  status: '',
  dateRange: [],
  keyword: '',
  format: 'xlsx',
  fields: ['username', 'nickname', 'phone', 'role', 'status', 'createTime']
})

// 导出状态
const exporting = ref(false)

// 导出预览数据
const previewData = ref([
  {
    username: 'admin',
    nickname: '系统管理员',
    phone: '13800138000',
    email: 'admin@example.com',
    role: 'admin',
    status: 1,
    createTime: '2023-01-01',
    lastLogin: '2023-11-20 15:30:00'
  },
  {
    username: 'operator',
    nickname: '图书管理员',
    phone: '13800138001',
    email: 'operator@example.com',
    role: 'operator',
    status: 1,
    createTime: '2023-02-15',
    lastLogin: '2023-11-19 09:12:30'
  },
  {
    username: 'user1',
    nickname: '张三',
    phone: '13800138002',
    email: 'user1@example.com',
    role: 'user',
    status: 0,
    createTime: '2023-03-20',
    lastLogin: '2023-10-05 16:45:20'
  }
])

// 计算预计导出数量
const exportCount = computed(() => {
  if (exportForm.scope === 'all') {
    return 100 // 假设总共有100条记录
  } else {
    // 根据筛选条件计算
    let count = 100
    if (exportForm.role) count = Math.floor(count * 0.7)
    if (exportForm.status !== '') count = Math.floor(count * 0.5)
    if (exportForm.keyword) count = Math.floor(count * 0.3)
    if (exportForm.dateRange && exportForm.dateRange.length === 2) count = Math.floor(count * 0.6)
    return Math.max(count, 0)
  }
})

// 文件拖拽相关方法
const dragover = () => {
  isDragover.value = true
}

const dragleave = () => {
  isDragover.value = false
}

const handleDrop = (e) => {
  isDragover.value = false
  const files = e.dataTransfer.files
  if (files.length) {
    handleFiles(files[0])
  }
}

// 处理文件选择
const handleFileChange = (e) => {
  const files = e.target.files
  if (files.length) {
    handleFiles(files[0])
  }
}

// 处理文件
const handleFiles = (file) => {
  // 检查文件类型
  const validTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-excel.sheet.macroEnabled.12'
  ]
  
  if (!validTypes.includes(file.type) && 
      !file.name.endsWith('.xls') && 
      !file.name.endsWith('.xlsx')) {
    ElMessage.error('文件格式不正确，请上传Excel文件')
    return
  }
  
  // 检查文件大小
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过5MB')
    return
  }
  
  uploadFile.value = file
}

// 上传文件
const handleUpload = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }
  
  uploading.value = true
  
  // 模拟上传过程
  setTimeout(() => {
    // 模拟导入结果
    importResult.show = true
    importResult.success = Math.random() > 0.3 // 70%概率成功
    importResult.totalCount = Math.floor(Math.random() * 50) + 10
    
    if (importResult.success) {
      importResult.successCount = importResult.totalCount
      importResult.failedCount = 0
      importResult.message = '导入成功'
      importResult.errors = []
    } else {
      importResult.failedCount = Math.floor(Math.random() * 5) + 1
      importResult.successCount = importResult.totalCount - importResult.failedCount
      importResult.message = '部分导入成功，存在错误记录'
      
      // 生成错误记录
      importResult.errors = []
      for (let i = 0; i < importResult.failedCount; i++) {
        const errorTypes = [
          { field: 'username', message: '用户名已存在', value: `user${Math.floor(Math.random() * 100)}` },
          { field: 'phone', message: '手机号格式不正确', value: `1380013${Math.floor(Math.random() * 10000)}` },
          { field: 'role', message: '角色不存在', value: 'guest' },
          { field: 'email', message: '邮箱格式不正确', value: 'invalid-email' }
        ]
        
        const errorType = errorTypes[Math.floor(Math.random() * errorTypes.length)]
        importResult.errors.push({
          row: Math.floor(Math.random() * importResult.totalCount) + 2, // +2 因为有标题行和从1开始计数
          field: errorType.field,
          message: errorType.message,
          value: errorType.value
        })
      }
      
      // 按行号排序
      importResult.errors.sort((a, b) => a.row - b.row)
    }
    
    uploading.value = false
  }, 2000)
}

// 重置上传
const resetUpload = () => {
  uploadFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 重置导入结果
const resetImport = () => {
  importResult.show = false
  resetUpload()
}

// 下载模板
const downloadTemplate = () => {
  ElMessage.success('模板下载成功')
}

// 处理导出
const handleExport = () => {
  if (exportForm.fields.length === 0) {
    ElMessage.warning('请至少选择一个导出字段')
    return
  }
  
  exporting.value = true
  
  // 模拟导出过程
  setTimeout(() => {
    exporting.value = false
    ElMessage.success(`成功导出 ${exportCount.value} 条用户数据`)
  }, 1500)
}

// 重置导出选项
const resetExport = () => {
  exportForm.scope = 'all'
  exportForm.role = ''
  exportForm.status = ''
  exportForm.dateRange = []
  exportForm.keyword = ''
  exportForm.format = 'xlsx'
  exportForm.fields = ['username', 'nickname', 'phone', 'role', 'status', 'createTime']
}

// 获取字段标签
const getFieldLabel = (field) => {
  const fieldMap = {
    username: '用户名',
    nickname: '昵称',
    phone: '手机号',
    email: '邮箱',
    role: '角色',
    status: '状态',
    createTime: '创建时间',
    lastLogin: '最后登录'
  }
  return fieldMap[field] || field
}

// 获取角色标签类型
const getRoleTagType = (role) => {
  switch (role) {
    case 'admin': return 'danger'
    case 'operator': return 'warning'
    case 'user': return 'info'
    default: return 'info'
  }
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const option = roleOptions.find(item => item.value === role)
  return option ? option.label : role
}
</script>

<style scoped lang="scss">
.user-import-export-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;

  .page-header {
    margin-bottom: 24px;
    
    .page-title {
      font-size: 24px;
      color: #3d2c29;
      margin: 0;
      position: relative;
      padding-left: 16px;
      font-weight: 600;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: linear-gradient(to bottom, #8a5f41, #e36049);
        border-radius: 2px;
      }
    }
  }
  
  .page-content {
    display: flex;
    flex-direction: column;
    gap: 30px;
    
    .import-card,
    .export-card {
      border-radius: 10px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
      border: none;
      overflow: hidden;
      
      .card-header {
        display: flex;
        align-items: center;
        padding: 16px;
        background-color: #f9f6f2;
        border-bottom: 1px solid #f0f0f0;
        
        .header-icon {
          width: 36px;
          height: 36px;
          background-color: rgba(138, 95, 65, 0.1);
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          
          .el-icon {
            font-size: 20px;
            color: #8a5f41;
          }
        }
        
        .header-title {
          font-size: 16px;
          color: #3d2c29;
          font-weight: 600;
        }
      }
    }
    
    .import-area {
      padding: 24px;
      display: flex;
      gap: 30px;
      
      @media (max-width: 768px) {
        flex-direction: column;
      }
      
      .upload-area {
        flex: 1;
        border: 2px dashed #d9d9d9;
        border-radius: 8px;
        padding: 30px;
        text-align: center;
        cursor: pointer;
        position: relative;
        transition: all 0.3s;
        background-color: #fafafa;
        
        &:hover, &.is-dragover {
          border-color: #8a5f41;
          background-color: rgba(138, 95, 65, 0.02);
        }
        
        .file-input {
          position: absolute;
          width: 100%;
          height: 100%;
          top: 0;
          left: 0;
          opacity: 0;
          cursor: pointer;
        }
        
        .upload-icon {
          font-size: 48px;
          color: #8a5f41;
          margin-bottom: 16px;
        }
        
        .upload-text {
          color: #606266;
          
          em {
            color: #8a5f41;
            font-style: normal;
            text-decoration: underline;
            cursor: pointer;
          }
          
          p {
            margin-top: 8px;
            font-size: 12px;
            color: #909399;
          }
        }
        
        .file-selected {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 16px;
          
          .file-icon {
            font-size: 48px;
            color: #8a5f41;
          }
          
          .file-name {
            font-size: 16px;
            color: #303133;
            word-break: break-all;
            max-width: 100%;
          }
          
          .file-actions {
            display: flex;
            gap: 12px;
            margin-top: 8px;
            
            .upload-button {
              background-color: #8a5f41;
              border-color: #8a5f41;
              
              &:hover {
                background-color: #6e4c34;
              }
            }
            
            .reset-button {
              border-color: #8a5f41;
              color: #8a5f41;
              
              &:hover {
                background-color: rgba(138, 95, 65, 0.05);
              }
            }
          }
        }
      }
      
      .import-guide {
        width: 300px;
        
        @media (max-width: 768px) {
          width: 100%;
        }
        
        .guide-title {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;
          font-weight: 600;
          color: #303133;
          
          .el-icon {
            color: #8a5f41;
          }
        }
        
        .guide-content {
          background-color: #f9f9f9;
          padding: 16px;
          border-radius: 8px;
          
          p {
            margin: 8px 0;
            color: #606266;
            
            &:first-child {
              margin-top: 0;
            }
            
            &:last-child {
              margin-bottom: 0;
            }
          }
          
          .template-link {
            font-weight: 600;
            
            &:hover {
              color: #8a5f41;
            }
          }
        }
      }
    }
    
    .import-result {
      margin-top: 20px;
      padding: 16px;
      border-radius: 8px;
      border: 1px solid #f0f0f0;
      
      .result-header {
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 16px;
        font-weight: 600;
        padding-bottom: 16px;
        border-bottom: 1px solid #f0f0f0;
        
        &.success-header {
          color: #67c23a;
          
          .el-icon {
            color: #67c23a;
          }
        }
        
        &.error-header {
          color: #f56c6c;
          
          .el-icon {
            color: #f56c6c;
          }
        }
      }
      
      .result-summary {
        display: flex;
        gap: 20px;
        margin: 16px 0;
        
        .success-count,
        .failed-count,
        .total-count {
          display: flex;
          align-items: center;
          
          .label {
            margin-right: 8px;
            color: #606266;
          }
          
          .value {
            font-weight: 600;
          }
        }
        
        .success-count .value {
          color: #67c23a;
        }
        
        .failed-count .value {
          color: #f56c6c;
        }
      }
      
      .result-details {
        margin-top: 16px;
        
        .details-title {
          margin-bottom: 12px;
          font-weight: 600;
          color: #303133;
        }
      }
      
      .result-actions {
        margin-top: 20px;
        text-align: center;
        
        .reset-import-button {
          min-width: 120px;
        }
      }
    }
    
    .export-area {
      padding: 24px;
      display: flex;
      gap: 30px;
      
      @media (max-width: 768px) {
        flex-direction: column;
      }
      
      .export-form {
        flex: 1;
        
        .export-fields-item {
          :deep(.el-form-item__content) {
            flex-wrap: wrap;
          }
        }
        
        .filter-select,
        .date-range-picker,
        .keyword-input {
          width: 100%;
        }
        
        .export-actions {
          display: flex;
          gap: 12px;
          
          .export-button {
            background-color: #8a5f41;
            border-color: #8a5f41;
            
            &:hover {
              background-color: #6e4c34;
            }
            
            .el-icon {
              margin-right: 6px;
            }
          }
          
          .reset-export-button {
            border-color: #8a5f41;
            color: #8a5f41;
            
            &:hover {
              background-color: rgba(138, 95, 65, 0.05);
            }
          }
        }
      }
      
      .export-preview {
        width: 500px;
        border: 1px solid #f0f0f0;
        border-radius: 8px;
        
        @media (max-width: 768px) {
          width: 100%;
        }
        
        .preview-title {
          padding: 12px 16px;
          font-weight: 600;
          color: #303133;
          background-color: #f9f9f9;
          border-bottom: 1px solid #f0f0f0;
        }
        
        .preview-content {
          padding: 16px;
          
          .preview-info {
            margin-bottom: 16px;
            
            .preview-count {
              margin-bottom: 12px;
              
              .highlight {
                color: #e36049;
                font-weight: 600;
              }
            }
            
            .preview-fields {
              display: flex;
              flex-wrap: wrap;
              gap: 8px;
              align-items: center;
              
              .field-tag {
                background-color: rgba(138, 95, 65, 0.1);
                color: #8a5f41;
                border-color: rgba(138, 95, 65, 0.2);
              }
            }
          }
          
          .preview-table {
            border: 1px solid #ebeef5;
            border-radius: 4px;
            overflow: hidden;
          }
        }
      }
    }
  }
}
</style> 