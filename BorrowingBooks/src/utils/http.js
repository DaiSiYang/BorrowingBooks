// src/utils/axios.js
import axios from 'axios';
import { ElLoading, ElMessage } from 'element-plus';
import 'element-plus/es/components/message/style/css';
import 'element-plus/es/components/loading/style/css';
import {useTokenStore} from '@/stores/tokenStore.js';



let loadingInstance = null;

// 创建 Axios 实例
const service = axios.create({
    baseURL: "http://localhost:8080/api",
    timeout: 10000,
});

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        // 启用加载中动画
        loadingInstance = ElLoading.service({
            lock: true,
            text: '加载中...',
            background: 'rgba(0, 0, 0, 0.3)',
        });

        // 添加 token
        const tokenStore = useTokenStore();
        const token = tokenStore.token;
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        if (loadingInstance) loadingInstance.close();
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response) => {
        if (loadingInstance) loadingInstance.close();

        const { code, message } = response.data;
        if (code !== 200) {
            ElMessage.error(message || '请求失败');
            return Promise.reject(new Error(message || '请求失败'));
        }

        return response.data;
    },
    (error) => {
        if (loadingInstance) loadingInstance.close();

        if (error.response) {
            const { status } = error.response;
            switch (status) {
                case 401:
                    ElMessage.error('未授权，请重新登录');
                    // 清除 token 并跳转到登录页
                    const tokenStore = useTokenStore();
                    tokenStore.clearToken();
                    // 使用 router 实例进行导航
                    import('@/router').then(({ default: router }) => {
                        router.replace({ name: 'login' });
                    });
                    break;
                case 403:
                    ElMessage.error('登录已过期，请重新登录');
                    // 清除 token 并跳转到登录页
                    const tokenStore403 = useTokenStore();
                    tokenStore403.clearToken();
                    // 使用 router 实例进行导航
                    import('@/router').then(({ default: router }) => {
                        router.replace({ name: 'login' });
                    });
                    break;
                case 500:
                    ElMessage.error('服务器异常，请稍后重试');
                    break;
                default:
                    ElMessage.error(`请求错误：${status}`);
            }
        } else {
            ElMessage.error('网络连接失败');
        }

        return Promise.reject(error);
    }
);

export default service;