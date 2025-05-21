<template>
  <div class="add-book-container">
    <div class="page-header">
      <div class="back-button" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>
      <h2 class="page-title">添加图书</h2>
    </div>

    <el-card class="form-card">
      <div class="form-header">
        <div class="form-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="form-title">图书信息</div>
      </div>
      
      <el-form 
        ref="bookFormRef" 
        :model="bookForm" 
        :rules="rules" 
        label-position="top" 
        class="book-form"
        :hide-required-asterisk="false"
      >
        <el-row :gutter="30">
          <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
            <el-row :gutter="24">
              <el-col :xs="24" :sm="12">
                <el-form-item label="书名" prop="title">
                  <el-input 
                    v-model="bookForm.title" 
                    placeholder="请输入书名"
                    :prefix-icon="Document"
                    class="custom-input"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="作者" prop="author">
                  <el-input 
                    v-model="bookForm.author" 
                    placeholder="请输入作者"
                    :prefix-icon="User"
                    class="custom-input"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24" :sm="12">
                <el-form-item label="出版社" prop="publisher">
                  <el-input 
                    v-model="bookForm.publisher" 
                    placeholder="请输入出版社"
                    :prefix-icon="OfficeBuilding"
                    class="custom-input"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="ISBN" prop="isbn">
                  <el-input 
                    v-model="bookForm.isbn" 
                    placeholder="请输入ISBN"
                    :prefix-icon="Ticket"
                    class="custom-input"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24" :sm="12">
                <el-form-item label="分类" prop="category">
                  <el-select 
                    v-model="bookForm.category" 
                    placeholder="请选择分类" 
                    style="width: 100%"
                    class="custom-select"
                  >
                    <el-option 
                      v-for="item in categoryOptions" 
                      :key="item.value" 
                      :label="item.label" 
                      :value="item.value" 
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="出版日期" prop="publishDate">
                  <el-date-picker
                    v-model="bookForm.publishDate"
                    type="date"
                    placeholder="选择出版日期"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                    class="custom-date-picker"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :xs="24">
                <el-form-item label="馆藏数量" prop="totalCopies">
                  <el-input-number 
                    v-model="bookForm.totalCopies" 
                    :min="1" 
                    :max="999"
                    class="custom-input-number"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="图书简介" prop="description">
              <el-input 
                v-model="bookForm.description" 
                type="textarea" 
                :rows="5" 
                placeholder="请输入图书简介"
                class="custom-textarea"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
            <el-form-item label="封面图片" prop="cover" class="cover-upload">
              <div class="upload-container">
                <el-upload
                  class="cover-uploader"
                  action="#"
                  :http-request="handleUpload"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                >
                  <div class="upload-area">
                    <img v-if="imageUrl" :src="imageUrl" class="cover-image" />
                    <div v-else class="upload-placeholder">
                      <div class="placeholder-icon">
                        <el-icon class="upload-icon"><Plus /></el-icon>
                      </div>
                      <div class="upload-text">点击上传</div>
                      <div class="upload-hint">建议尺寸: 300x400像素<br>JPG/PNG格式, 不超过2MB</div>
                    </div>
                  </div>
                </el-upload>
                <div class="upload-tips">
                  <el-icon><InfoFilled /></el-icon>
                  <span>上传清晰的封面图片有助于读者快速识别图书</span>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-divider">
          <span>确认信息</span>
        </div>

        <div class="form-actions">
          <el-button @click="resetForm" class="reset-button">
            <el-icon><RefreshRight /></el-icon>重置
          </el-button>
          <el-button type="primary" @click="submitForm" class="submit-button">
            <el-icon><Check /></el-icon>保存图书
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, 
  Document, 
  User, 
  OfficeBuilding, 
  Ticket, 
  Plus, 
  QuestionFilled,
  InfoFilled,
  RefreshRight,
  Check
} from '@element-plus/icons-vue'

const router = useRouter()
const bookFormRef = ref(null)
const imageUrl = ref('')

// 分类选项
const categoryOptions = [
  { value: '文学小说', label: '文学小说' },
  { value: '科幻小说', label: '科幻小说' },
  { value: '历史传记', label: '历史传记' },
  { value: '经济管理', label: '经济管理' },
  { value: '科学技术', label: '科学技术' }
]

// 表单数据
const bookForm = reactive({
  title: '',
  author: '',
  publisher: '',
  isbn: '',
  category: '',
  publishDate: '',
  totalCopies: 1,
  description: '',
  cover: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在1到100个字符之间', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在1到50个字符之间', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' }
  ],
  isbn: [
    { required: true, message: '请输入ISBN', trigger: 'blur' },
    { pattern: /^(?:\d[- ]?){9}[\dX]$|^(?:\d[- ]?){13}$/, message: 'ISBN格式不正确', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  publishDate: [
    { required: true, message: '请选择出版日期', trigger: 'change' }
  ],
  totalCopies: [
    { required: true, message: '请输入馆藏数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '馆藏数量必须大于0', trigger: 'blur' }
  ]
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 上传前验证
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('封面图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理上传
const handleUpload = (options) => {
  const file = options.file
  // 这里应该调用API上传文件，这里使用本地预览
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    imageUrl.value = reader.result
    bookForm.cover = file.name // 实际应用中应该是上传后的文件路径
  }
}

// 提交表单
const submitForm = () => {
  bookFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API保存图书
      console.log('提交的表单数据:', bookForm)
      ElMessage({
        type: 'success',
        message: '图书添加成功!'
      })
      // 重置表单或跳转到列表页
      resetForm()
      router.push('/home/books')
    } else {
      ElMessage({
        type: 'error',
        message: '请完善表单信息!'
      })
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  bookFormRef.value.resetFields()
  imageUrl.value = ''
}
</script>

<style scoped lang="scss">
.add-book-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;
  
  .page-header {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    
    .back-button {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #8a5f41;
      margin-right: 16px;
      transition: all 0.3s;
      
      &:hover {
        color: #e36049;
        transform: translateX(-3px);
      }
      
      .el-icon {
        margin-right: 4px;
        font-size: 16px;
      }
    }
    
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
  
  .form-card {
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;
    margin-bottom: 30px;
    
    .form-header {
      display: flex;
      align-items: center;
      padding-bottom: 20px;
      margin-bottom: 24px;
      border-bottom: 1px dashed rgba(138, 95, 65, 0.2);
      
      .form-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        background-color: rgba(138, 95, 65, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        
        .el-icon {
          font-size: 20px;
          color: #8a5f41;
        }
      }
      
      .form-title {
        font-size: 18px;
        color: #3d2c29;
        font-weight: 600;
      }
    }
    
    .book-form {
      .el-form-item__label {
        font-weight: 500;
        color: #3d2c29;
        padding-bottom: 8px;
        font-size: 15px;
      }
      
      .custom-input, .custom-select, .custom-date-picker, .custom-input-number, .custom-textarea {
        .el-input__wrapper {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;
          transition: all 0.3s;
          
          &:hover, &.is-focus {
            box-shadow: 0 0 0 1px #8a5f41 inset;
          }
        }
        
        .el-input__prefix-inner {
          color: #8a5f41;
        }
      }
      
      .cover-upload {
        height: 100%;
        
        .upload-container {
          display: flex;
          flex-direction: column;
          height: 100%;
          
          .cover-uploader {
            width: 100%;
            flex: 1;
            
            .upload-area {
              width: 100%;
              height: 400px;
              border: 2px dashed rgba(138, 95, 65, 0.3);
              border-radius: 10px;
              cursor: pointer;
              position: relative;
              overflow: hidden;
              display: flex;
              justify-content: center;
              align-items: center;
              transition: all 0.3s;
              background-color: #f9f6f2;
              
              &:hover {
                border-color: #8a5f41;
                background-color: #f5f0e8;
              }
              
              .cover-image {
                width: 100%;
                height: 100%;
                object-fit: cover;
                border-radius: 8px;
              }
              
              .upload-placeholder {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                height: 100%;
                width: 100%;
                padding: 20px;
                
                .placeholder-icon {
                  width: 80px;
                  height: 80px;
                  border-radius: 50%;
                  background-color: rgba(138, 95, 65, 0.1);
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin-bottom: 16px;
                  
                  .upload-icon {
                    font-size: 40px;
                    color: #8a5f41;
                  }
                }
                
                .upload-text {
                  font-size: 16px;
                  color: #3d2c29;
                  margin-bottom: 10px;
                  font-weight: 500;
                }
                
                .upload-hint {
                  font-size: 13px;
                  color: #8a5f41;
                  text-align: center;
                  line-height: 1.5;
                }
              }
            }
          }
          
          .upload-tips {
            display: flex;
            align-items: center;
            padding: 10px;
            margin-top: 12px;
            background-color: #f9f6f2;
            border-radius: 6px;
            
            .el-icon {
              font-size: 16px;
              color: #e36049;
              margin-right: 8px;
            }
            
            span {
              font-size: 13px;
              color: #8a5f41;
              line-height: 1.4;
            }
          }
        }
      }
      
      .form-divider {
        display: flex;
        align-items: center;
        margin: 32px 0 24px;
        
        &::before, &::after {
          content: '';
          flex: 1;
          height: 1px;
          background: rgba(138, 95, 65, 0.2);
        }
        
        span {
          padding: 0 16px;
          font-size: 14px;
          color: #8a5f41;
        }
      }
      
      .form-actions {
        display: flex;
        justify-content: center;
        margin-top: 32px;
        gap: 20px;
        
        .reset-button {
          min-width: 120px;
          border-color: #8a5f41;
          color: #8a5f41;
          border-radius: 8px;
          transition: all 0.3s;
          padding: 12px 24px;
          
          &:hover {
            background-color: rgba(138, 95, 65, 0.05);
            transform: translateY(-2px);
          }
          
          .el-icon {
            margin-right: 6px;
          }
        }
        
        .submit-button {
          min-width: 120px;
          background-color: #8a5f41;
          border-color: #8a5f41;
          border-radius: 8px;
          transition: all 0.3s;
          padding: 12px 24px;
          
          &:hover {
            background-color: #6e4c34;
            transform: translateY(-2px);
          }
          
          .el-icon {
            margin-right: 6px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .add-book-container {
    padding: 16px;
    
    .form-card {
      .form-actions {
        flex-direction: column;
        
        .el-button {
          width: 100%;
          margin-left: 0 !important;
          margin-bottom: 12px;
        }
      }
    }
  }
}
</style>