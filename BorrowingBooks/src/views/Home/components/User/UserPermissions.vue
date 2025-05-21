<template>
  <div class="user-permissions-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="title-section">
        <el-button @click="goBack" class="back-button">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h2 class="page-title">权限配置 - {{ userInfo.username }}</h2>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="handleSave" :loading="saving" class="save-button">
          <el-icon><Check /></el-icon>保存配置
        </el-button>
      </div>
    </div>

    <el-alert
      type="info"
      :closable="false"
      class="alert-info"
    >
      <template #title>
        <div class="alert-title">
          <el-icon><InfoFilled /></el-icon>
          <span>当前用户：{{ userInfo.username }}，角色：{{ getRoleLabel(userInfo.role) }}</span>
        </div>
      </template>
      <div class="alert-description">
        您可以通过选择已有角色或自定义权限来设置用户权限。选择角色后，将应用该角色的默认权限设置。
      </div>
    </el-alert>

    <div v-loading="loading" class="permissions-content">
      <!-- 角色选择 -->
      <el-card class="role-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="header-title">角色配置</div>
          </div>
        </template>
        
        <div class="roles-container">
          <div class="role-subtitle">选择角色：</div>
          <el-radio-group v-model="selectedRole" @change="handleRoleChange" class="role-radio-group">
            <el-radio-button v-for="role in roleList" :key="role.value" :label="role.value" :disabled="role.disabled">
              {{ role.label }}
            </el-radio-button>
          </el-radio-group>
          
          <div class="role-description" v-if="selectedRoleInfo">
            <div class="role-info-title">角色说明：</div>
            <div class="role-info-content">{{ selectedRoleInfo.description }}</div>
          </div>
        </div>
      </el-card>

      <!-- 权限树 -->
      <el-card class="permissions-card">
        <template #header>
          <div class="card-header">
            <div class="header-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="header-title">权限配置</div>
            <div class="header-actions">
              <el-switch
                v-model="customPermissions"
                active-text="自定义权限"
                inactive-text="角色默认权限"
                @change="handleCustomChange"
              />
            </div>
          </div>
        </template>
        
        <div class="permissions-container">
          <div class="search-container">
            <el-input
              v-model="filterText"
              placeholder="输入关键字过滤权限"
              clearable
              prefix-icon="Search"
            />
          </div>
          
          <div class="permissions-tree-container">
            <el-tree
              ref="permissionsTree"
              :data="permissionsData"
              show-checkbox
              node-key="id"
              :props="{ label: 'name', children: 'children' }"
              :default-checked-keys="defaultCheckedKeys"
              :filter-node-method="filterNode"
              :disabled="!customPermissions"
              :check-strictly="checkStrictly"
              :default-expand-all="true"
              class="permissions-tree"
            />
          </div>
          
          <div class="permissions-actions">
            <el-button type="primary" @click="checkAll" plain :disabled="!customPermissions">全选</el-button>
            <el-button type="info" @click="resetChecked" plain :disabled="!customPermissions">重置</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Check, InfoFilled, UserFilled, Setting, 
  Search 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userId = route.params.id

// 角色列表
const roleList = [
  { 
    value: 'admin', 
    label: '管理员', 
    description: '系统管理员，拥有所有操作权限，可以管理系统所有功能和配置。',
    disabled: false
  },
  { 
    value: 'operator', 
    label: '操作员', 
    description: '图书馆操作员，负责日常图书借阅、归还、管理等操作，无系统配置权限。',
    disabled: false
  },
  { 
    value: 'user', 
    label: '普通用户', 
    description: '普通用户，只有基本的图书浏览、借阅申请等功能权限。',
    disabled: false
  }
]

// 当前选中的角色
const selectedRole = ref('')

// 获取选中角色信息
const selectedRoleInfo = computed(() => {
  return roleList.find(role => role.value === selectedRole.value)
})

// 是否自定义权限
const customPermissions = ref(false)
// 是否严格匹配树节点选择
const checkStrictly = ref(false)

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  nickname: '',
  role: ''
})

// 过滤关键字
const filterText = ref('')
// 权限树引用
const permissionsTree = ref(null)

// 监听过滤文本变化
watch(filterText, (val) => {
  permissionsTree.value?.filter(val)
})

// 过滤节点方法
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

// 加载和保存状态
const loading = ref(false)
const saving = ref(false)

// 权限数据
const permissionsData = ref([])
// 默认选中的权限ID
const defaultCheckedKeys = ref([])

// 获取角色显示文本
const getRoleLabel = (role) => {
  const option = roleList.find(item => item.value === role)
  return option ? option.label : role
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 处理角色变更
const handleRoleChange = (value) => {
  if (!customPermissions.value) {
    // 如果不是自定义权限，切换角色时自动更新权限选择
    fetchRolePermissions(value)
  } else {
    ElMessageBox.confirm(
      '切换角色将覆盖当前已选的自定义权限，是否继续？',
      '权限切换确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      fetchRolePermissions(value)
    }).catch(() => {
      // 取消切换，恢复之前的选择
      selectedRole.value = userInfo.role
    })
  }
}

// 处理自定义权限开关变更
const handleCustomChange = (value) => {
  if (value) {
    // 打开自定义权限
    checkStrictly.value = true
    ElMessage.info('已启用自定义权限模式，您可以自由勾选权限项')
  } else {
    // 关闭自定义权限，恢复为角色默认权限
    ElMessageBox.confirm(
      '关闭自定义权限将恢复为当前角色的默认权限设置，是否继续？',
      '权限模式切换确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      checkStrictly.value = false
      fetchRolePermissions(selectedRole.value)
    }).catch(() => {
      // 取消切换，恢复自定义权限开关
      customPermissions.value = true
    })
  }
}

// 全选
const checkAll = () => {
  const allKeys = getAllPermissionKeys(permissionsData.value)
  permissionsTree.value.setCheckedKeys(allKeys)
}

// 获取所有权限键
const getAllPermissionKeys = (data) => {
  const keys = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      keys.push(node.id)
      if (node.children && node.children.length) {
        traverse(node.children)
      }
    })
  }
  traverse(data)
  return keys
}

// 重置选择
const resetChecked = () => {
  permissionsTree.value.setCheckedKeys([])
}

// 保存配置
const handleSave = async () => {
  saving.value = true
  
  try {
    const selectedPermissions = permissionsTree.value.getCheckedKeys()
    const halfCheckedKeys = permissionsTree.value.getHalfCheckedKeys()
    
    // 合并半选中和全选中的节点
    const allSelectedKeys = [...selectedPermissions, ...halfCheckedKeys]
    
    // TODO: 调用保存权限API
    console.log('保存权限:', {
      userId,
      role: selectedRole.value,
      isCustom: customPermissions.value,
      permissions: allSelectedKeys
    })
    
    setTimeout(() => {
      ElMessage.success('权限配置已保存')
      saving.value = false
    }, 1000)
  } catch (error) {
    console.error('保存权限配置失败:', error)
    ElMessage.error('保存失败，请重试')
    saving.value = false
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    // TODO: 调用用户详情API
    // 模拟API调用
    setTimeout(() => {
      Object.assign(userInfo, {
        id: userId,
        username: 'admin',
        nickname: '系统管理员',
        role: 'admin'
      })
      
      // 设置当前选中的角色
      selectedRole.value = userInfo.role
      
      loading.value = false
    }, 600)
  } catch (error) {
    console.error('获取用户信息失败:', error)
    loading.value = false
  }
}

// 获取角色对应的权限
const fetchRolePermissions = async (roleValue) => {
  loading.value = true
  try {
    // TODO: 调用角色权限API
    // 模拟API调用
    setTimeout(() => {
      // 模拟不同角色的权限
      let checkedKeys = []
      
      if (roleValue === 'admin') {
        // 管理员拥有所有权限
        checkedKeys = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
      } else if (roleValue === 'operator') {
        // 操作员拥有图书管理和借阅管理权限
        checkedKeys = [1, 2, 3, 4, 5, 6, 7, 8, 9]
      } else if (roleValue === 'user') {
        // 普通用户只有查看图书和个人借阅记录权限
        checkedKeys = [1, 2, 8]
      }
      
      defaultCheckedKeys.value = checkedKeys
      setTimeout(() => {
        permissionsTree.value?.setCheckedKeys(checkedKeys)
      }, 100)
      
      loading.value = false
    }, 800)
  } catch (error) {
    console.error('获取角色权限失败:', error)
    loading.value = false
  }
}

// 获取所有权限数据
const fetchPermissionsData = async () => {
  try {
    // TODO: 调用权限列表API
    // 模拟API调用
    setTimeout(() => {
      // 模拟权限数据结构
      permissionsData.value = [
        {
          id: 1,
          name: '图书管理',
          children: [
            { id: 2, name: '图书查看' },
            { id: 3, name: '图书添加' },
            { id: 4, name: '图书编辑' },
            { id: 5, name: '图书删除' },
            { id: 6, name: '图书分类管理' },
            { id: 7, name: '图书导入导出' }
          ]
        },
        {
          id: 8,
          name: '借阅管理',
          children: [
            { id: 9, name: '借阅记录查看' },
            { id: 10, name: '借阅申请处理' },
            { id: 11, name: '逾期处理' }
          ]
        },
        {
          id: 12,
          name: '用户管理',
          children: [
            { id: 13, name: '用户查看' },
            { id: 14, name: '用户添加/编辑' },
            { id: 15, name: '用户删除' },
            { id: 16, name: '权限配置' }
          ]
        },
        {
          id: 17,
          name: '系统设置',
          children: [
            { id: 18, name: '基本设置' },
            { id: 19, name: '参数配置' },
            { id: 20, name: '日志查看' }
          ]
        }
      ]
      
      // 获取当前角色的权限
      fetchRolePermissions(userInfo.role)
    }, 500)
  } catch (error) {
    console.error('获取权限数据失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchPermissionsData()
})
</script>

<style scoped lang="scss">
.user-permissions-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #fff;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .title-section {
      display: flex;
      align-items: center;
      gap: 16px;

      .back-button {
        border: 1px solid #8a5f41;
        color: #8a5f41;
        border-radius: 50%;
        padding: 8px;
        height: 36px;
        width: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .page-title {
        font-size: 24px;
        color: #3d2c29;
        margin: 0;
        position: relative;
        font-weight: 600;
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;

      .save-button {
        background-color: #8a5f41;
        border-color: #8a5f41;
        
        &:hover {
          background-color: #6e4c34;
        }
      }

      .el-icon {
        margin-right: 6px;
      }
    }
  }

  .alert-info {
    margin-bottom: 24px;
    border-radius: 8px;
    
    .alert-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
    }
    
    .alert-description {
      margin-top: 8px;
      color: #606266;
    }
  }

  .permissions-content {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .role-card,
    .permissions-card {
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
          flex-grow: 1;
        }

        .header-actions {
          display: flex;
          align-items: center;
        }
      }
    }

    .roles-container {
      padding: 20px;

      .role-subtitle {
        font-size: 15px;
        color: #606266;
        margin-bottom: 12px;
      }

      .role-radio-group {
        margin-bottom: 20px;
      }

      .role-description {
        background-color: #f9f9f9;
        padding: 16px;
        border-radius: 8px;
        margin-top: 20px;

        .role-info-title {
          font-weight: 600;
          color: #303133;
          margin-bottom: 8px;
        }

        .role-info-content {
          color: #606266;
          line-height: 1.6;
        }
      }
    }

    .permissions-container {
      padding: 20px;

      .search-container {
        margin-bottom: 16px;
      }

      .permissions-tree-container {
        max-height: 400px;
        overflow-y: auto;
        padding: 12px;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        background-color: #f9f9f9;
        margin-bottom: 16px;

        .permissions-tree {
          :deep(.el-tree-node__content) {
            height: 36px;
          }

          :deep(.el-tree-node.is-current > .el-tree-node__content) {
            background-color: rgba(138, 95, 65, 0.1);
          }

          :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
            background-color: #8a5f41;
            border-color: #8a5f41;
          }
        }
      }

      .permissions-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .user-permissions-container {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .action-buttons {
        width: 100%;
      }
    }
  }
}
</style> 