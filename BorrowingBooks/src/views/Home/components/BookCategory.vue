<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete, Reading, Document, InfoFilled } from '@element-plus/icons-vue'
import {ElMessageBox, ElMessage, ElNotification} from 'element-plus'
import {BookCategoryAPI} from "@/api/Book.js";

// 分类数据
const categoryData = ref([])
const loading = ref(true)

// 分类表单
const categoryForm = ref({
  name: '',
  description: '',
  bookCount: 0
})

// 对话框控制
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const isEdit = ref(false)
const currentId = ref(null)

// 表单规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符之间', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 表单引用
const formRef = ref(null)

// 获取分类数据
const fetchCategories =async () => {
  loading.value = true
  const result = await BookCategoryAPI()
  if (result && result.code === 200) {
    console.log(result)
    categoryData.value = result.data.map(item => ({
      ...item,
      createdAt: item.createTime.split('T')[0] // 格式化日期
    }))
    loading.value = false
  } else {
    loading.value = false
    ElNotification({
      title: '错误',
      message: '获取分类数据失败',
      type: 'error'
    })
  }
}

// 打开添加分类对话框
const openAddDialog = () => {
  dialogTitle.value = '添加分类'
  isEdit.value = false
  categoryForm.value = {
    name: '',
    description: '',
    bookCount: 0
  }
  dialogVisible.value = true
}

// 打开编辑分类对话框
const openEditDialog = (row) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  currentId.value = row.id
  categoryForm.value = {
    name: row.name,
    description: row.description,
    bookCount: row.bookCount
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      if (isEdit.value) {
        // 编辑现有分类
        console.log('编辑分类:', categoryForm.value)
        // 实际应用中应该调用API更新分类
        
        // 模拟更新成功
        const index = categoryData.value.findIndex(item => item.id === currentId.value)
        if (index !== -1) {
          categoryData.value[index] = {
            ...categoryData.value[index],
            name: categoryForm.value.name,
            description: categoryForm.value.description
          }
        }
        
        ElMessage({
          type: 'success',
          message: '分类更新成功'
        })
      } else {
        // 添加新分类
        console.log('添加分类:', categoryForm.value)
        // 实际应用中应该调用API添加分类
        
        // 模拟添加成功
        const newId = Math.max(...categoryData.value.map(item => item.id)) + 1
        categoryData.value.push({
          id: newId,
          name: categoryForm.value.name,
          description: categoryForm.value.description,
          bookCount: 0,
          createdAt: new Date().toISOString().split('T')[0]
        })
        
        ElMessage({
          type: 'success',
          message: '分类添加成功'
        })
      }
      
      dialogVisible.value = false
    } else {
      console.log('表单验证失败:', fields)
    }
  })
}

// 删除分类
const deleteCategory = (row) => {
  if (row.bookCount > 0) {
    ElMessage({
      type: 'warning',
      message: `该分类下还有${row.bookCount}本图书，无法删除`
    })
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除分类"${row.name}"吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 实际应用中应该调用API删除分类
    console.log('删除分类:', row)
    
    // 模拟删除成功
    const index = categoryData.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      categoryData.value.splice(index, 1)
    }
    
    ElMessage({
      type: 'success',
      message: '删除成功'
    })
  }).catch(() => {
    // 取消删除
  })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dateString
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="category-container">
    <div class="page-header">
      <h2 class="page-title">图书分类</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="openAddDialog" class="add-button">
          <el-icon><Plus /></el-icon>添加分类
        </el-button>
      </div>
    </div>
    
    <div class="category-overview">
      <div class="overview-card">
        <el-icon class="overview-icon"><Document /></el-icon>
        <div class="overview-info">
          <div class="overview-value">{{ categoryData.length }}</div>
          <div class="overview-label">分类总数</div>
        </div>
      </div>
      
      <div class="overview-card">
        <el-icon class="overview-icon"><Reading /></el-icon>
        <div class="overview-info">
          <div class="overview-value">{{ categoryData.reduce((total, category) => total + (category.bookCount || 0), 0) }}</div>
          <div class="overview-label">图书总数</div>
        </div>
      </div>
      
      <div class="overview-tips">
        <el-icon><InfoFilled /></el-icon>
        <span>分类是图书检索的重要依据，合理设置分类有助于读者快速找到所需图书</span>
      </div>
    </div>
    
    <el-card class="table-card">
      <el-table 
        :data="categoryData" 
        style="width: 100%" 
        border 
        v-loading="loading"
        row-key="id"
        :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150">
          <template #default="scope">
            <div class="category-name">{{ scope.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip>
          <template #default="scope">
            <div class="category-desc">{{ scope.row.description || '暂无描述' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创建日期" width="120">
          <template #default="scope">
            <div class="category-date">{{ formatDate(scope.row.createdAt) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="图书数量" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.bookCount > 0 ? 'success' : 'info'"
              size="small"
              class="book-count-tag"
            >
              {{ scope.row.bookCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="编辑" placement="top" effect="light">
                <el-button 
                  type="primary" 
                  size="small" 
                  circle
                  @click="openEditDialog(scope.row)"
                  class="action-button edit-button"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" effect="light">
                <el-button 
                  type="danger" 
                  size="small" 
                  circle
                  @click="deleteCategory(scope.row)"
                  class="action-button delete-button"
                  :disabled="scope.row.bookCount > 0"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="categoryData.length === 0 && !loading" class="empty-data">
        <el-empty description="暂无分类数据">
          <el-button type="primary" @click="openAddDialog" class="add-empty-button">
            添加首个分类
          </el-button>
        </el-empty>
      </div>
    </el-card>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
      destroy-on-close
      class="category-dialog"
    >
      <div class="dialog-icon">
        <el-icon v-if="isEdit"><Edit /></el-icon>
        <el-icon v-else><Plus /></el-icon>
      </div>
      
      <el-form 
        ref="formRef"
        :model="categoryForm"
        :rules="rules"
        label-width="100px"
        class="category-form"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input 
            v-model="categoryForm.name" 
            placeholder="请输入分类名称"
            class="custom-input"
          />
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input 
            v-model="categoryForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入分类描述"
            class="custom-textarea"
          />
        </el-form-item>
        
        <el-form-item v-if="isEdit" label="图书数量">
          <el-input v-model="categoryForm.bookCount" class="custom-input" disabled />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-button">取消</el-button>
          <el-button type="primary" @click="submitForm" class="confirm-button">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.category-container {
  padding: 24px;
  background-color: #fff;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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
    
    .action-buttons {
      display: flex;
      gap: 12px;
      
      .add-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        padding: 10px 20px;
        border-radius: 8px;
        transition: all 0.3s;
        
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
  
  .category-overview {
    display: flex;
    margin-bottom: 24px;
    gap: 20px;
    flex-wrap: wrap;
    
    .overview-card {
      background-color: #f9f6f2;
      border-radius: 10px;
      padding: 20px;
      display: flex;
      align-items: center;
      min-width: 200px;
      flex: 1;
      min-height: 100px;
      transition: all 0.3s;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 15px rgba(0, 0, 0, 0.08);
      }
      
      .overview-icon {
        width: 50px;
        height: 50px;
        background-color: rgba(138, 95, 65, 0.1);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;
        color: #8a5f41;
        font-size: 24px;
      }
      
      .overview-info {
        .overview-value {
          font-size: 28px;
          font-weight: bold;
          color: #3d2c29;
          margin-bottom: 5px;
        }
        
        .overview-label {
          font-size: 14px;
          color: #8a5f41;
        }
      }
    }
    
    .overview-tips {
      flex-basis: 100%;
      display: flex;
      align-items: center;
      padding: 12px 16px;
      background-color: #f5f0e8;
      border-radius: 8px;
      border-left: 4px solid #e36049;
      
      .el-icon {
        font-size: 18px;
        color: #e36049;
        margin-right: 10px;
      }
      
      span {
        font-size: 14px;
        color: #8a5f41;
        line-height: 1.4;
      }
    }
  }
  
  .table-card {
    margin-bottom: 30px;
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;
    
    .category-name {
      font-weight: 500;
      color: #3d2c29;
      font-size: 15px;
    }
    
    .category-desc {
      color: #666;
      font-size: 14px;
    }
    
    .category-date {
      color: #8a5f41;
      font-size: 14px;
    }
    
    .book-count-tag {
      background-color: rgba(138, 95, 65, 0.1);
      color: #8a5f41;
      border: none;
      border-radius: 12px;
      
      &.el-tag--success {
        background-color: rgba(103, 194, 58, 0.1);
        color: #67C23A;
      }
    }
    
    .table-actions {
      display: flex;
      justify-content: center;
      gap: 10px;
      
      .action-button {
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
        }
        
        &.edit-button {
          background-color: #8a5f41;
          border-color: #8a5f41;
          
          &:hover {
            background-color: #6e4c34;
          }
        }
        
        &.delete-button {
          background-color: #e36049;
          border-color: #e36049;
          
          &:hover {
            background-color: #c64c38;
          }
          
          &:disabled {
            background-color: #f0f0f0;
            border-color: #dcdfe6;
            color: #c0c4cc;
          }
        }
      }
    }
    
    .empty-data {
      padding: 30px 0;
      
      .add-empty-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        margin-top: 16px;
        
        &:hover {
          background-color: #6e4c34;
        }
      }
    }
  }
  
  .category-dialog {
    :deep(.el-dialog__header) {
      text-align: center;
      margin-bottom: 0;
      position: relative;
      padding-bottom: 0;
    }
    
    :deep(.el-dialog__body) {
      padding-top: 30px;
    }
    
    :deep(.el-dialog__title) {
      font-size: 18px;
      font-weight: 600;
      color: #3d2c29;
    }
    
    .dialog-icon {
      width: 60px;
      height: 60px;
      background-color: #f9f6f2;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      
      .el-icon {
        font-size: 24px;
        color: #8a5f41;
      }
    }
    
    .category-form {
      .el-form-item__label {
        color: #3d2c29;
        font-weight: 500;
      }
      
      .custom-input, .custom-textarea {
        .el-input__wrapper, .el-textarea__inner {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;
          
          &:hover, &.is-focus {
            box-shadow: 0 0 0 1px #8a5f41 inset;
          }
        }
      }
    }
    
    .dialog-footer {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin-top: 10px;
      
      .cancel-button {
        border-color: #8a5f41;
        color: #8a5f41;
        border-radius: 8px;
        padding: 8px 20px;
        min-width: 100px;
        
        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
        }
      }
      
      .confirm-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        border-radius: 8px;
        padding: 8px 20px;
        min-width: 100px;
        
        &:hover {
          background-color: #6e4c34;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .category-container {
    padding: 16px;
    
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }
    
    .category-overview {
      flex-direction: column;
      gap: 16px;
    }
    
    .dialog-footer {
      flex-direction: column-reverse;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style>