<template>
  <div class="user-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd" class="add-button">
          <el-icon><Plus /></el-icon>新增用户
        </el-button>
        <el-button @click="handleExport" class="export-button">
          <el-icon><Download /></el-icon>导出数据
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <div class="card-header">
        <div class="header-icon">
          <el-icon><Search /></el-icon>
        </div>
        <div class="header-title">搜索筛选</div>
      </div>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
            @keyup.enter="handleSearch"
            :prefix-icon="Search"
            class="custom-input"
          />
        </el-form-item>

        <el-form-item label="用户角色">
          <el-select
            v-model="searchForm.role"
            placeholder="选择角色"
            clearable
            class="custom-select"
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
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            class="custom-select"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="权限筛选">
          <el-select
            v-model="searchForm.permission"
            placeholder="选择权限"
            clearable
            class="custom-select"
            filterable
          >
            <el-option
              v-for="item in permissionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            class="custom-date-picker"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-button">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch" class="reset-button">
            <el-icon><RefreshLeft /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="selectedUsers.length > 0">
      <el-button type="danger" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>批量删除
      </el-button>
      <el-button type="warning" @click="handleBatchDisable">
        <el-icon><Lock /></el-icon>批量禁用
      </el-button>
    </div>

    <!-- 用户表格 -->
    <el-card class="table-card">
      <el-table
        ref="multipleTable"
        :data="tableData"
        style="width: 100%"
        border
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
        :header-cell-style="{backgroundColor: '#f9f6f2', color: '#3d2c29', fontWeight: 'bold'}"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="scope">
            <div class="user-cell">
              <el-avatar :size="32" :src="scope.row.avatar" class="user-avatar">
                {{ scope.row.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="nickname" label="昵称" min-width="120" />

        <el-table-column prop="phone" label="手机号" width="120" />

        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag
              :type="getRoleTagType(scope.row.role)"
              effect="light"
              size="small"
            >
              {{ scope.row.roleName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="permissions" label="权限" min-width="180">
          <template #default="scope">
            <div class="permissions-cell">
              <el-tooltip
                v-if="scope.row.permissions && scope.row.permissions.length > 0"
                placement="top"
                :content="getPermissionsText(scope.row.permissions)"
              >
                <div class="permissions-preview">
                  <el-tag
                    v-for="(perm, index) in scope.row.permissions.slice(0, 2)"
                    :key="index"
                    size="small"
                    type="info"
                    effect="plain"
                    class="permission-tag"
                  >
                    {{ perm.permissionName }}
                  </el-tag>
                  <el-tag
                    v-if="scope.row.permissions.length > 2"
                    size="small"
                    type="info"
                    effect="plain"
                    class="permission-tag"
                  >
                    +{{ scope.row.permissions.length - 2 }}
                  </el-tag>
                </div>
              </el-tooltip>
              <span v-else class="no-permissions">无权限</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(val, scope.row)"
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top" effect="light">
                <el-button
                  type="primary"
                  link
                  @click="handleView(scope.row)"
                >
                  查看
                </el-button>
              </el-tooltip>

              <el-tooltip content="编辑用户" placement="top" effect="light">
                <el-button
                  type="primary"
                  link
                  @click="handleEdit(scope.row)"
                >
                  编辑
                </el-button>
              </el-tooltip>

              <el-tooltip content="重置密码" placement="top" effect="light">
                <el-button
                  type="warning"
                  link
                  @click="handleResetPassword(scope.row)"
                >
                  重置密码
                </el-button>
              </el-tooltip>

              <el-tooltip content="删除用户" placement="top" effect="light">
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="80px"
        class="user-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="userForm.nickname"
            placeholder="请输入昵称"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="userForm.phone"
            placeholder="请输入手机号"
          />
        </el-form-item>

        <el-form-item
          label="密码"
          prop="password"
          v-if="dialogType === 'add'"
        >
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select
            v-model="userForm.role"
            placeholder="请选择角色"
            class="w-full"
          >
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="userForm.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Download, RefreshLeft, Delete, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getUserList } from '@/api/user.js'

const router = useRouter()

// 角色选项
const roleOptions = [
  { value: 1, label: '管理员' },
  { value: 2, label: '操作员' },
  { value: 3, label: '普通用户' }
]

// 状态选项
const statusOptions = [
  { value: 1, label: '启用' },
  { value: 0, label: '禁用' }
]

// 权限选项
const permissionOptions = ref([
  { value: 'user:view', label: '用户查看' },
  { value: 'user:add', label: '用户添加' },
  { value: 'user:edit', label: '用户编辑' },
  { value: 'user:delete', label: '用户删除' },
  { value: 'book:view', label: '图书查看' },
  { value: 'book:add', label: '图书添加' },
  { value: 'book:edit', label: '图书编辑' },
  { value: 'book:delete', label: '图书删除' },
  { value: 'borrow:view', label: '借阅查看' },
  { value: 'borrow:add', label: '借阅申请' },
  { value: 'borrow:approve', label: '借阅审批' },
  { value: 'borrow:return', label: '借阅归还' }
])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: '',
  status: '',
  permission: '',
  dateRange: []
})

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 选中的用户
const selectedUsers = ref([])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const userFormRef = ref(null)

// 用户表单
const userForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: '',
  role: '',
  status: 1
})

// 表单校验规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 全局存储已筛选的数据
const filteredData = ref([])

// 处理搜索
const handleSearch = () => {
  // 如果没有输入任何筛选条件，则从服务器获取数据
  if (!searchForm.keyword && searchForm.role === '' && searchForm.status === '' && searchForm.permission === '' && (!searchForm.dateRange || searchForm.dateRange.length === 0)) {
    pagination.currentPage = 1
    fetchUserList()
    return
  }
  
  // 有筛选条件时，在前端筛选
  loading.value = true
  try {
    // 前端筛选
    filteredData.value = tableData.value.filter(user => {
      // 关键词筛选 (用户名、昵称或手机号)
      const keywordMatch = !searchForm.keyword || 
        user.username.toLowerCase().includes(searchForm.keyword.toLowerCase()) || 
        user.nickname.toLowerCase().includes(searchForm.keyword.toLowerCase()) || 
        (user.phone && user.phone !== '未绑定' && user.phone.includes(searchForm.keyword))
      
      // 角色筛选
      const roleMatch = searchForm.role === '' || user.role === Number(searchForm.role)
      
      // 状态筛选
      const statusMatch = searchForm.status === '' || user.status === Number(searchForm.status)
      
      // 权限筛选
      let permissionMatch = true
      if (searchForm.permission) {
        permissionMatch = user.permissions && user.permissions.some(p => 
          p.permissionKey === searchForm.permission || 
          p.permissionName.includes(searchForm.permission)
        )
      }
      
      // 日期范围筛选
      let dateMatch = true
      if (searchForm.dateRange && searchForm.dateRange.length === 2) {
        const createDate = user.createTime ? new Date(user.createTime) : null
        if (createDate) {
          const startDate = new Date(searchForm.dateRange[0])
          const endDate = new Date(searchForm.dateRange[1])
          // 设置endDate为当天的23:59:59
          endDate.setHours(23, 59, 59, 999)
          
          dateMatch = createDate >= startDate && createDate <= endDate
        } else {
          dateMatch = false
        }
      }
      
      return keywordMatch && roleMatch && statusMatch && permissionMatch && dateMatch
    })
    
    // 更新分页信息
    pagination.total = filteredData.value.length
    pagination.currentPage = 1
    
    // 分页处理
    const start = 0
    const end = Math.min(pagination.pageSize, filteredData.value.length)
    tableData.value = filteredData.value.slice(start, end)
    
    if (filteredData.value.length === 0) {
      ElMessage.info('没有找到符合条件的用户')
    }
  } catch (error) {
    console.error('筛选出错:', error)
    ElMessage.error('筛选过程中出现错误')
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  
  // 如果有筛选条件，使用前端分页
  if (searchForm.keyword || searchForm.role !== '' || searchForm.status !== '' || searchForm.permission !== '' || (searchForm.dateRange && searchForm.dateRange.length === 2)) {
    handleFilterPagination()
  } else {
    // 无筛选条件，使用服务器分页
    fetchUserList()
  }
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  
  // 如果有筛选条件，使用前端分页
  if (searchForm.keyword || searchForm.role !== '' || searchForm.status !== '' || searchForm.permission !== '' || (searchForm.dateRange && searchForm.dateRange.length === 2)) {
    handleFilterPagination()
  } else {
    // 无筛选条件，使用服务器分页
    fetchUserList()
  }
}

// 前端筛选的分页处理
const handleFilterPagination = () => {
  loading.value = true
  try {
    // 更新分页信息
    pagination.total = filteredData.value.length
    
    // 分页处理
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = Math.min(start + pagination.pageSize, filteredData.value.length)
    tableData.value = filteredData.value.slice(start, end)
  } catch (error) {
    console.error('筛选分页出错:', error)
    ElMessage.error('筛选分页过程中出现错误')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.role = ''
  searchForm.status = ''
  searchForm.permission = ''
  searchForm.dateRange = []
  filteredData.value = []
  pagination.currentPage = 1
  fetchUserList()
}

// 处理表格选择变化
const handleSelectionChange = (val) => {
  selectedUsers.value = val
}

// 处理新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  userForm.username = ''
  userForm.nickname = ''
  userForm.phone = ''
  userForm.password = ''
  userForm.role = ''
  userForm.status = 1
  dialogVisible.value = true
}

// 处理编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(userForm, row)
  dialogVisible.value = true
}

// 处理查看详情
const handleView = (row) => {
  // 构建权限信息文本
  const permissionsText = row.permissions && row.permissions.length > 0
    ? row.permissions.map(p => p.permissionName).join('、')
    : '无权限';
  
  ElMessageBox.alert(
    `<div class="user-detail-dialog">
      <div class="avatar-container">
        <img src="${row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'}" class="user-avatar">
      </div>
      <div class="info-container">
        <div class="info-item"><span class="label">用户名：</span>${row.username}</div>
        <div class="info-item"><span class="label">昵称：</span>${row.nickname || '-'}</div>
        <div class="info-item"><span class="label">邮箱：</span>${row.email || '-'}</div>
        <div class="info-item"><span class="label">手机：</span>${row.phone}</div>
        <div class="info-item"><span class="label">角色：</span>${row.roleName}</div>
        <div class="info-item"><span class="label">状态：</span>${row.status === 1 ? '启用' : '禁用'}</div>
        <div class="info-item"><span class="label">创建时间：</span>${row.createTime ? formatDate(row.createTime) : '-'}</div>
        <div class="info-item">
          <span class="label">权限：</span>
          <div class="permissions-list">${permissionsText}</div>
        </div>
      </div>
    </div>`,
    '用户详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭',
      customClass: 'user-detail-box'
    }
  );
}

// 处理删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户"${row.username}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
    fetchUserList()
  }).catch(() => {})
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用批量删除API
    ElMessage.success('批量删除成功')
    fetchUserList()
  }).catch(() => {})
}

// 处理批量禁用
const handleBatchDisable = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要禁用的用户')
    return
  }

  ElMessageBox.confirm(
    `确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`,
    '批量禁用确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用批量禁用API
    ElMessage.success('批量禁用成功')
    fetchUserList()
  }).catch(() => {})
}

// 处理状态变更
const handleStatusChange = (val, row) => {
  // TODO: 调用状态更新API
  ElMessage.success(`用户"${row.username}"${val ? '启用' : '禁用'}成功`)
}

// 处理重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置用户"${row.username}"的密码吗？密码将被重置为: 244466666`,
    '重置密码确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用重置密码API
    ElMessage({
      type: 'success',
      message: `密码重置成功，新密码为: 244466666`,
      duration: 5000
    })
  }).catch(() => {})
}

// 处理导出
const handleExport = () => {
  // TODO: 调用导出API
  ElMessage.success('导出成功')
}

// 处理表单提交
const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()
    
    // TODO: 调用新增/编辑API
    ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
    dialogVisible.value = false
    fetchUserList()
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 获取角色标签类型
const getRoleTagType = (roleId) => {
  // 基于roleId设置不同角色的标签类型
  switch (Number(roleId)) {
    case 1: return 'danger'  // 管理员
    case 2: return 'warning' // 操作员
    case 3: return 'info'    // 普通用户
    default: return 'info'
  }
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const option = roleOptions.find(item => item.value === role)
  return option ? option.label : role
}

// 获取权限文本
const getPermissionsText = (permissions) => {
  if (!permissions || permissions.length === 0) {
    return '无权限'
  }
  return permissions.map(p => p.permissionName).join('、')
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  
  try {
    if (typeof date === 'string') {
      // 如果已经是格式化的日期字符串，直接返回
      if (date.includes('-') && date.length >= 10) {
        // 只返回 YYYY-MM-DD 格式
        return date.substring(0, 10)
      }
      date = new Date(date)
    }
    
    // 确保date是有效的Date对象
    if (isNaN(date.getTime())) {
      return '-'
    }
    
    // 返回标准格式 YYYY-MM-DD
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '-'
  }
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchForm.keyword,
      roleId: searchForm.role, // 使用roleId作为查询参数
      status: searchForm.status,
    }
    
    // 处理日期范围
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    
    // 调用API获取用户列表
    const response = await getUserList(params)
    console.log('获取用户列表接口响应:', response)
    
    // 判断接口返回是否成功
    if (response && response.code === 200) {
      // 直接使用response.data，它本身就是用户数组
      const users = response.data || []
      
      // 处理用户数据，映射字段
      tableData.value = users.map(user => ({
        id: user.id,
        username: user.username || '',
        nickname: user.nickname || user.username || '',
        phone: user.phone || '未绑定',
        email: user.email || '-',
        role: Number(user.roleId) || 0, // 确保roleId是数字
        roleName: user.roleName || '未知',
        status: user.status === undefined ? 1 : Number(user.status), // 确保status是数字
        createTime: user.createTime || null,
        avatar: user.avatarUrl || '',
        permissions: user.permissions || []
      }))
      
      // 设置分页总数 - 暂时使用实际返回的数据长度，后端可能未实现分页
      pagination.total = users.length
      
      console.log('处理后的用户数据:', tableData.value)
    } else {
      // 显示错误消息
      ElMessage.error(response?.msg || '获取用户列表失败')
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败，请稍后重试')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped lang="scss">
.user-list-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
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
        
        &:hover {
          background-color: #6e4c34;
        }
      }

      .export-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;

        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
        }
      }

      .el-icon {
        margin-right: 6px;
      }
    }
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;

    .card-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

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

    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;

      .el-form-item {
        margin-bottom: 10px;
        margin-right: 0;

        .el-form-item__label {
          color: #3d2c29;
          font-weight: 500;
        }
      }

      .custom-input,
      .custom-select,
      .custom-date-picker {
        .el-input__wrapper,
        :deep(.el-range-editor.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 1px rgba(138, 95, 65, 0.2) inset;

          &:hover,
          &.is-focus {
            box-shadow: 0 0 0 1px #8a5f41 inset;
          }
        }

        .el-input__prefix-inner {
          color: #8a5f41;
        }
      }

      .search-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        border-radius: 8px;
        padding: 10px 20px;
        transition: all 0.3s;

        &:hover {
          background-color: #6e4c34;
          transform: translateY(-2px);
        }
      }

      .reset-button {
        border-color: #8a5f41;
        color: #8a5f41;
        border-radius: 8px;
        padding: 10px 20px;
        transition: all 0.3s;

        &:hover {
          background-color: rgba(138, 95, 65, 0.05);
          transform: translateY(-2px);
        }
      }
    }
  }

  .table-toolbar {
    margin-bottom: 16px;
    display: flex;
    gap: 12px;
  }

  .table-card {
    border-radius: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    border: none;
    overflow: hidden;

    .user-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-avatar {
        background-color: #8a5f41;
        color: #fff;
        font-weight: bold;
      }

      .username {
        color: #3d2c29;
        font-weight: 500;
      }
    }

    .last-login {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .ip-address {
        font-size: 12px;
        color: #999;
        cursor: help;
      }
    }

    .table-actions {
      display: flex;
      justify-content: center;
      gap: 12px;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    padding: 10px 0;

    :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
      background-color: #8a5f41;
    }

    :deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
      color: #8a5f41;
    }
  }

  .user-form {
    padding: 20px;

    .el-form-item {
      margin-bottom: 20px;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
  }

  .permissions-cell {
    .permissions-preview {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
    }
    
    .permission-tag {
      margin-right: 4px;
      margin-bottom: 4px;
      background-color: rgba(138, 95, 65, 0.08);
      border-color: rgba(138, 95, 65, 0.2);
      color: #8a5f41;
    }
    
    .no-permissions {
      color: #999;
      font-size: 12px;
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .user-list-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .search-form {
      .el-form-item {
        width: 100%;
      }
    }
  }
}

// 用户详情弹窗样式
:deep(.user-detail-box) {
  .el-message-box__content {
    padding: 0;
  }
  
  .el-message-box__message p {
    margin: 0;
  }
}

:deep(.user-detail-dialog) {
  display: flex;
  flex-direction: column;
  padding: 20px;
  
  .avatar-container {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
    
    .user-avatar {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid #f9f6f2;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }
  
  .info-container {
    .info-item {
      display: flex;
      margin-bottom: 12px;
      
      .label {
        font-weight: 600;
        color: #3d2c29;
        width: 90px;
        flex-shrink: 0;
      }
      
      .permissions-list {
        flex: 1;
        color: #8a5f41;
      }
    }
  }
}
</style> 