import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserInfoStore = defineStore('userInfo', () => {
    // 状态定义
    const userInfo = ref({
        avatarUrl: '',
        createTime: '',
        email: '',
        id: 0,
        phone: null,
        realName: '',
        roleId: 0,
        token: '',
        updateTime: null,
        username: ''
    })

    const isLoggedIn = computed(() => !!userInfo.value.token)

    // 设置用户信息
    const setUserInfo = (info) => {
        userInfo.value = { ...userInfo.value, ...info }
    }

    // 清除用户信息（用于登出）
    const clearUserInfo = () => {
        userInfo.value = {
            avatarUrl: '',
            createTime: '',
            email: '',
            id: 0,
            phone: null,
            realName: '',
            roleId: 0,
            token: '',
            updateTime: null,
            username: ''
        }
    }

    // 获取用户信息（返回副本，避免直接修改状态）
    const getUserInfo = () => {
        return { ...userInfo.value }
    }

    return {
        userInfo,
        isLoggedIn,
        setUserInfo,
        clearUserInfo,
        getUserInfo
    }
},{
    persist: true
})