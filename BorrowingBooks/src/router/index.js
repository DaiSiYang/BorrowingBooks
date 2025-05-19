import { createRouter, createWebHistory } from 'vue-router'

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
      redirect:'login',
    },
    {
      path:"/register",
      name:"register",
      component: () => import('../views/Register/register.vue')
    },
    {
      path:"/forgetPassword",
      name:"forgetPassword",
      component: () => import('@/views/ForgotThePassword/forgotPassword.vue')
    },
    // 添加404错误页面路由
    {
      path: '/404',
      name: 'notFound',
      component: () => import('@/views/Wrong/ErrorPage.vue')
    },
    // 捕获所有未匹配的路由，重定向到404页面
    {
      path: '/:pathMatch(.*)*',
      redirect: { name: 'notFound' }
    }
  ],
})

export default router
