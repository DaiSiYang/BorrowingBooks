import { createRouter, createWebHistory } from 'vue-router'
import {useTokenStore} from "@/stores/tokenStore.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login/login.vue')
    },
    {
      path: '/',
      redirect: 'home',
    },
    {
      path: "/register",
      name: "register",
      component: () => import('../views/Register/register.vue')
    },
    {
      path: "/forgetPassword",
      name: "forgetPassword",
      component: () => import('@/views/ForgotThePassword/forgotPassword.vue')
    },
    {
      path: '/404',
      name: 'notFound',
      component: () => import('@/views/Wrong/ErrorPage.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: { name: 'notFound' }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/Home/index.vue'),
      redirect: '/home/homepage',  // 确保直接重定向到homepage
      children: [
        {
          path: 'homepage',
          name: 'homepage',
          component: () => import('@/views/Home/components/HomePage.vue'),
          meta: {
            title: '首页',
            requireAuth: true
          }
        },
        {
          path: 'book-category',
          name: 'bookCategory',
          component: () => import('@/views/Home/components/BookCategory.vue')
        },
        {
          path: 'book-list',
          name: 'bookList',
          component: () => import('@/views/Home/components/BookList.vue')
        },
        {
          path: 'book-detail/:id',
          name: 'bookDetail',
          component: () => import('@/views/Book/BookDetail.vue'),
          meta: {
            title: '图书详情',
            requireAuth: true
          }
        },
        {
          path: 'add-book',
          name: 'addBook',
          component: () => import('@/views/Home/components/AddBook.vue')
        },
        {
          path: 'borrow-records',
          name: 'borrowRecords',
          component: () => import('@/views/Home/components/BorrowRecords.vue')
        },
        {
          path: 'borrow-applications',
          name: 'borrowApplications',
          component: () => import('@/views/Home/components/BorrowApplications.vue')
        },
        {
          path: 'overdue-management',
          name: 'overdueManagement',
          component: () => import('@/views/Home/components/OverdueManagement.vue')
        },
        {
          path: 'user',
          name: 'userRoot',
          redirect: '/home/user/list',
          children: [
            {
              path: 'list',
              name: 'userList',
              component: () => import('@/views/Home/components/User/UserList.vue'),
              meta: { title: '用户列表' }
            },
            {
              path: 'detail/:id',
              name: 'userDetail',
              component: () => import('@/views/Home/components/User/UserDetail.vue'),
              meta: { title: '用户详情' }
            },
            {
              path: 'permissions/:id',
              name: 'userPermissions',
              component: () => import('@/views/Home/components/User/UserPermissions.vue'),
              meta: { title: '权限配置' }
            },
            {
              path: 'logs/:id',
              name: 'userLogs',
              component: () => import('@/views/Home/components/User/UserLogs.vue'),
              meta: { title: '操作日志' }
            },
            {
              path: 'import-export',
              name: 'userImportExport',
              component: () => import('@/views/Home/components/User/UserImportExport.vue'),
              meta: { title: '用户导入导出' }
            }
          ]
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/views/Statistics/StatisticsAnalysis.vue'),
          meta: {
            title: '统计分析',
            requireAuth: true
          }
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/Settings/SystemSettings.vue'),
          meta: {
            title: '系统设置',
            requireAuth: true
          }
        },
        // 添加个人中心路由
        {
          path: '/user/personal-center',
          name: 'personalCenter',
          component: () => import('../views/User/PersonalCenter.vue'),
          meta: {
            requiresAuth: true,
            title: '个人中心'
          }
        }
      ]
    }
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const tokenStore = useTokenStore()
  const token = tokenStore.token  // 直接使用 token 属性，而不是调用 getToken 方法

  // 白名单：不需要登录的路由
  const whiteList = ['/login', '/register', '/forgetPassword']

  if (whiteList.includes(to.path)) {
    return next()
  }

  if (!token) {
    return next({ name: 'login' })
  }

  next()
})

export default router