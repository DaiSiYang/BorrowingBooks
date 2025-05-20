<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
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
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加分类
        </el-button>
      </div>
    </div>
    
    <el-card class="table-card">
      <el-table 
        :data="categoryData" 
        style="width: 100%" 
        border 
        v-loading="loading"
        row-key="id"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
        <el-table-column label="创建日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              type="warning" 
              size="small" 
              circle
              @click="openEditDialog(scope.row)"
              title="编辑"
            >
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              circle
              @click="deleteCategory(scope.row)"
              title="删除"
              :disabled="scope.row.bookCount > 0"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
    >
      <el-form 
        ref="formRef"
        :model="categoryForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input 
            v-model="categoryForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入分类描述"
          />
        </el-form-item>
        
        <el-form-item v-if="isEdit" label="图书数量">
          <el-input v-model="categoryForm.bookCount" disabled />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.category-container {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .page-title {
      font-size: 24px;
      color: #183550;
      margin: 0;
    }
    
    .action-buttons {
      display: flex;
      gap: 10px;
    }
  }
  
  .table-card {
    margin-bottom: 20px;
  }
}
</style>