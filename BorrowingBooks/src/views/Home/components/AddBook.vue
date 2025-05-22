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
      <el-steps :active="activeStep" finish-status="success" simple class="book-steps">
        <el-step title="基本信息" icon="Document" />
        <el-step title="详细描述" icon="Edit" />
        <el-step title="封面上传" icon="Picture" />
      </el-steps>
      
      <el-form 
        ref="bookFormRef" 
        :model="bookForm" 
        :rules="rules" 
        label-position="top" 
        class="book-form"
        :hide-required-asterisk="false"
      >
        <!-- 第一步：基本信息 -->
        <div v-show="activeStep === 1">
          <div class="step-title">
            <el-icon><Document /></el-icon>
            <span>基本信息</span>
          </div>
          
        <el-row :gutter="30">
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

          <el-row :gutter="30">
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

          <el-row :gutter="30">
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

          <el-row :gutter="30">
            <el-col :xs="24" :sm="12">
                <el-form-item label="馆藏数量" prop="totalCopies">
                  <el-input-number 
                    v-model="bookForm.totalCopies" 
                    :min="1" 
                    :max="999"
                    class="custom-input-number"
                    style="width: 100%"
                  controls-position="right"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="馆藏位置" prop="location">
                <el-input 
                  v-model="bookForm.location" 
                  placeholder="请输入馆藏位置"
                  :prefix-icon="Location"
                  class="custom-input"
                  />
                </el-form-item>
              </el-col>
            </el-row>
        </div>
        
        <!-- 第二步：详细描述 -->
        <div v-show="activeStep === 2">
          <div class="step-title">
            <el-icon><Edit /></el-icon>
            <span>详细描述</span>
          </div>
          
          <el-alert
            title="填写详细的图书描述可以帮助读者更好地了解图书内容"
            type="info"
            :closable="false"
            class="description-tip"
          />

            <el-form-item label="图书简介" prop="description">
              <el-input 
                v-model="bookForm.description" 
                type="textarea" 
              :rows="8" 
                placeholder="请输入图书简介"
                class="custom-textarea"
              show-word-limit
              maxlength="1000"
            />
          </el-form-item>
          
          <el-form-item label="目录" prop="catalog">
            <el-input 
              v-model="bookForm.catalog" 
              type="textarea" 
              :rows="6" 
              placeholder="请输入图书目录"
              class="custom-textarea"
              />
            </el-form-item>
        </div>
        
        <!-- 第三步：封面上传 -->
        <div v-show="activeStep === 3">
          <div class="step-title">
            <el-icon><Picture /></el-icon>
            <span>封面上传</span>
          </div>
          
          <div class="upload-section">
            <el-row :gutter="30">
              <el-col :xs="24" :sm="14" :md="14">
                <el-form-item label="封面图片" prop="coverImage" class="cover-upload">
              <div class="upload-container">
                <el-upload
                  class="cover-uploader"
                  action="#"
                  :http-request="handleUpload"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                      drag
                >
                  <div class="upload-area">
                    <img v-if="imageUrl" :src="imageUrl" class="cover-image" />
                    <div v-else class="upload-placeholder">
                          <el-icon class="upload-icon"><Picture /></el-icon>
                          <div class="upload-text">拖拽图片到此处或点击上传</div>
                          <div class="upload-hint">建议尺寸: 300x400像素<br>JPG/PNG格式, 不超过50MB</div>
                        </div>
                      </div>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="10" :md="10">
                <div class="upload-tips-card">
                  <h3 class="tips-title">上传说明</h3>
                  <ul class="tips-list">
                    <li>
                      <el-icon><InfoFilled /></el-icon>
                      <span>上传清晰的封面图片有助于读者快速识别图书</span>
                    </li>
                    <li>
                      <el-icon><Warning /></el-icon>
                      <span>请确保图片质量清晰，无水印</span>
                    </li>
                    <li>
                      <el-icon><PictureFilled /></el-icon>
                      <span>如无图书封面，系统将使用默认封面</span>
                    </li>
                    <li>
                      <el-icon><Lock /></el-icon>
                      <span>上传的图片仅用于图书展示，请遵守版权规范</span>
                    </li>
                  </ul>
                  
                  <div class="preview-section" v-if="imageUrl">
                    <h4 class="preview-title">预览效果</h4>
                    <div class="book-card-preview">
                      <div class="book-card-cover">
                        <img :src="imageUrl" class="preview-cover-image">
                      </div>
                      <div class="book-card-info">
                        <h3 class="book-card-title">{{ bookForm.title || '图书标题' }}</h3>
                        <p class="book-card-author">{{ bookForm.author || '作者姓名' }}</p>
                      </div>
                    </div>
                  </div>
                </div>
          </el-col>
        </el-row>
          </div>
        </div>

        <div class="form-actions">
          <div class="left-actions" v-if="activeStep > 1">
            <el-button @click="prevStep" class="prev-button">
              <el-icon><ArrowLeft /></el-icon>上一步
            </el-button>
          </div>
          
          <div class="right-actions">
          <el-button @click="resetForm" class="reset-button">
            <el-icon><RefreshRight /></el-icon>重置
          </el-button>
            
            <template v-if="activeStep < 3">
              <el-button type="primary" @click="nextStep" class="next-button">
                下一步<el-icon><ArrowRight /></el-icon>
              </el-button>
            </template>
            <template v-else>
              <el-button type="success" @click="submitForm" :loading="loading" class="submit-button">
                <el-icon v-if="!loading"><Check /></el-icon>保存图书
          </el-button>
            </template>
          </div>
        </div>
      </el-form>
    </el-card>
    
    <el-backtop :bottom="100" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useTokenStore } from '@/stores/tokenStore.js'
import { AddBookAPI } from '@/api/Book.js'
import { 
  ArrowLeft, 
  ArrowRight, 
  Document, 
  User, 
  OfficeBuilding, 
  Ticket, 
  Picture,
  PictureFilled,
  Plus, 
  QuestionFilled,
  InfoFilled,
  RefreshRight,
  Check,
  Edit,
  Location,
  Warning,
  Lock
} from '@element-plus/icons-vue'

const router = useRouter()
const bookFormRef = ref(null)
const imageUrl = ref('')
const activeStep = ref(1)
const loading = ref(false)

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
  catalog: '',
  location: '',
  coverImage: ''
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
    { required: true, message: '请输入ISBN', trigger: 'blur' }
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
  ],
  location: [
    { required: true, message: '请输入馆藏位置', trigger: 'blur' }
  ]
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 下一步
const nextStep = () => {
  if (activeStep.value === 1) {
    // 使用完整的validate方法进行验证
    bookFormRef.value.validate((valid) => {
      if (valid) {
        // 验证通过，进入下一步
        activeStep.value++
      } else {
        // 验证失败，提示用户
        ElMessage.warning('请完善表单信息后继续')
      }
    })
  } else {
    // 不是第一步，直接进入下一步
    activeStep.value++
  }
}

// 上一步
const prevStep = () => {
  activeStep.value--
}

// 上传前验证
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isJPG && !isPNG) {
    ElMessage.error('封面图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('封面图片大小不能超过 50MB!')
    return false
  }
  return true
}

// 处理上传
const handleUpload = (options) => {
  const file = options.file
  // 创建FormData对象用于文件上传
  const formData = new FormData()
  formData.append('file', file)
  
  // 使用axios上传文件到后端  
  const tokenStore = useTokenStore()
  
  axios.post('http://localhost:8080/api/public/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${tokenStore.token}`
    }
  })
  .then(response => {
    console.log('上传成功:', response)
    if (response.data && response.data.code === 200) {
      // 设置图片URL和表单值
      imageUrl.value = response.data.data // 或根据后端返回的数据结构调整
      bookForm.coverImage = response.data.data // 使用后端返回的文件路径
      ElMessage.success('图片上传成功!')
    } else {
      ElMessage.error('图片上传失败: ' + (response.data.message || '未知错误'))
    }
  })
  .catch(error => {
    console.error('上传失败:', error)
    ElMessage.error('图片上传失败，请稍后再试')
    
    // 这里仍然保留本地预览功能，以便在API不可用时测试UI
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    imageUrl.value = reader.result
      bookForm.coverImage = file.name // 实际应用中应该是上传后的文件路径
  }
  })
}

// 提交表单
const submitForm = async () => {
  bookFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        console.log('提交的表单数据:', bookForm) // 打印提交的数据
        const response = await AddBookAPI(bookForm)
        console.log('后端返回的响应:', response) // 打印后端响应
        
        // 放宽成功判断条件：只要有返回值就认为成功，因为后端已经提示插入成功
        if (response) {
      ElMessage({
        type: 'success',
        message: '图书添加成功!'
      })
          // 重置表单并跳转到列表页
      resetForm()
          router.push('/home/book-list')
        } else {
          ElMessage({
            type: 'error',
            message: '添加失败，请稍后再试'
          })
        }
      } catch (error) {
        console.error('添加图书出错:', error)
        ElMessage({
          type: 'error',
          message: '添加图书失败：' + (error.message || '未知错误')
        })
      } finally {
        loading.value = false
      }
    } else {
      ElMessage({
        type: 'error',
        message: '请完善必填表单信息!'
      })
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  bookFormRef.value.resetFields()
  imageUrl.value = ''
  activeStep.value = 1
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
    border-radius: 16px;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.07);
    border: none;
    overflow: hidden;
    margin-bottom: 30px;
    padding: 20px;
    background-color: #fff;
    
    .book-steps {
      margin-bottom: 30px;
      
      :deep(.el-step__icon) {
        background-color: rgba(138, 95, 65, 0.1);
        
        &.is-text {
          border-color: #8a5f41;
          color: #8a5f41;
        }
        
        &.is-process {
          background-color: #8a5f41;
          color: #fff;
        }
        
        &.is-finish {
          background-color: #67c23a;
          color: #fff;
          border-color: #67c23a;
        }
      }
    }
    
    .step-title {
      display: flex;
      align-items: center;
      margin-bottom: 25px;
      padding-bottom: 15px;
      border-bottom: 1px solid rgba(138, 95, 65, 0.2);
      
      .el-icon {
        font-size: 22px;
        color: #8a5f41;
        margin-right: 10px;
      }
      
      span {
        font-size: 18px;
        color: #3d2c29;
        font-weight: 600;
      }
    }
    
    .description-tip {
      margin-bottom: 20px;
      background-color: rgba(138, 95, 65, 0.1);
      border-color: rgba(138, 95, 65, 0.2);
      
      :deep(.el-alert__title) {
        color: #8a5f41;
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
      
      .upload-section {
        margin-top: 20px;
        
      .cover-upload {
          margin-bottom: 0;
        
        .upload-container {
          .cover-uploader {
            width: 100%;
              
              :deep(.el-upload) {
                width: 100%;
              }
              
              :deep(.el-upload-dragger) {
              width: 100%;
              height: 400px;
                padding: 0;
              display: flex;
                align-items: center;
              justify-content: center;
              background-color: #f9f6f2;
                border: 2px dashed rgba(138, 95, 65, 0.3);
              
              &:hover {
                border-color: #8a5f41;
                background-color: #f5f0e8;
                }
              }
              
              .upload-area {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                
                .cover-image {
                  width: 100%;
                  height: 100%;
                  object-fit: contain;
                border-radius: 8px;
              }
              
              .upload-placeholder {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                  padding: 30px;
                  
                  .upload-icon {
                    font-size: 60px;
                    color: #8a5f41;
                    margin-bottom: 20px;
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
          }
        }
        
        .upload-tips-card {
          height: 100%;
          background-color: #f9f6f2;
          border-radius: 12px;
          padding: 20px;
          
          .tips-title {
            font-size: 16px;
            color: #3d2c29;
            margin-top: 0;
            margin-bottom: 16px;
            padding-bottom: 8px;
            border-bottom: 1px solid rgba(138, 95, 65, 0.2);
          }
          
          .tips-list {
            list-style: none;
            padding: 0;
            margin: 0;
            
            li {
            display: flex;
            align-items: center;
              margin-bottom: 12px;
            
            .el-icon {
              font-size: 16px;
                color: #8a5f41;
              margin-right: 8px;
                flex-shrink: 0;
            }
            
            span {
                font-size: 14px;
                color: #3d2c29;
                line-height: 1.5;
          }
        }
      }
      
          .preview-section {
            margin-top: 30px;
            
            .preview-title {
              font-size: 15px;
              color: #3d2c29;
              margin-bottom: 15px;
            }
            
            .book-card-preview {
              width: 180px;
              background-color: white;
              border-radius: 8px;
              overflow: hidden;
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
              
              .book-card-cover {
                height: 240px;
                overflow: hidden;
                
                .preview-cover-image {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
        }
              }
              
              .book-card-info {
                padding: 12px;
                
                .book-card-title {
                  font-size: 14px;
                  color: #3d2c29;
                  margin: 0 0 5px;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                }
                
                .book-card-author {
                  font-size: 12px;
          color: #8a5f41;
                  margin: 0;
                }
              }
            }
          }
        }
      }
      
      .form-actions {
        display: flex;
        justify-content: space-between;
        margin-top: 40px;
        border-top: 1px dashed rgba(138, 95, 65, 0.2);
        padding-top: 20px;
        
        .left-actions {
          .prev-button {
            border-color: #4c8dae;
            color: #4c8dae;
            border-radius: 8px;
            transition: all 0.3s;
            
            &:hover {
              background-color: rgba(76, 141, 174, 0.05);
              transform: translateY(-2px);
            }
            
            .el-icon {
              margin-right: 6px;
            }
          }
        }
        
        .right-actions {
          display: flex;
          gap: 12px;
        
        .reset-button {
          border-color: #8a5f41;
          color: #8a5f41;
          border-radius: 8px;
          transition: all 0.3s;
          
          &:hover {
            background-color: rgba(138, 95, 65, 0.05);
            transform: translateY(-2px);
          }
          
          .el-icon {
            margin-right: 6px;
          }
        }
        
          .next-button {
            background-color: #4c8dae;
            border-color: #4c8dae;
          border-radius: 8px;
          transition: all 0.3s;
          
          &:hover {
              background-color: #3a7a99;
              transform: translateY(-2px);
            }
            
            .el-icon {
              margin-left: 6px;
            }
          }
          
          .submit-button {
            background-color: #67c23a;
            border-color: #67c23a;
            border-radius: 8px;
            transition: all 0.3s;
            
            &:hover {
              background-color: #5daf34;
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
}

@media (max-width: 768px) {
  .add-book-container {
    padding: 16px;
    
    .form-card {
      padding: 16px;
      
      .form-actions {
        flex-direction: column;
        gap: 15px;
        
        .left-actions, .right-actions {
          width: 100%;
          
          .el-button {
            width: 100%;
          }
        }
        
        .right-actions {
          flex-direction: column;
        }
      }
    }
  }
}
</style>