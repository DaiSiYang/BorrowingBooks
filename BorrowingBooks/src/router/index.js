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
      redirect: 'login',
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
    // 添加404错误页面路由
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
      children: [
        {
          path: 'homepage',
          name: 'homepage',
          component: () => import('@/views/Home/components/HomePage.vue')
        },
        {
          path: '',
          redirect: { name: 'homepage' }
        },
        // 添加图书分类路由
        {
          path: 'book-category',
          name: 'bookCategory',
          component: () => import('@/views/Home/components/BookCategory.vue')
        },
        // 可以根据需要添加其他路由
        {
          path: 'book-list',
          name: 'bookList',
          component: () => import('@/views/Home/components/BookList.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'add-book',
          name: 'addBook',
          component: () => import('@/views/Home/components/AddBook.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'borrow-records',
          name: 'borrowRecords',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'borrow-applications',
          name: 'borrowApplications',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'overdue-management',
          name: 'overdueManagement',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'user-management',
          name: 'userManagement',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/Home/components/HomePage.vue') // 暂时使用HomePage作为占位
        }
      ]
    }
  ],
})

export default router